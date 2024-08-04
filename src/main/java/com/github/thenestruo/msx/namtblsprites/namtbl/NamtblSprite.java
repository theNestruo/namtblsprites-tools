package com.github.thenestruo.msx.namtblsprites.namtbl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.github.thenestruo.msx.namtblsprites.model.Char;
import com.github.thenestruo.msx.namtblsprites.model.Coord;
import com.github.thenestruo.msx.namtblsprites.model.Size;
import com.github.thenestruo.msx.namtblsprites.util.CharUtils;

/**
 * A NAMTBL sprite that can be read as asm code
 */
public class NamtblSprite {

	private static final char[] OPTIMIZATION_REGISTERS = new char[]{ 'a', 'd', 'e' };

	private static final Coord ONE_TO_RIGHT = new Coord(1, 0);
	private static final Coord ONE_TO_LEFT = new Coord(-1, 0);

	private final String spriteId;
	private final NamtblSpriteAlignment alignment;
	private final Size actualSize;
	private final List<Char> relativeChars;

	private final short[] optimizableValues;
	private Coord previousOffset;

	/**
	 * Constructor
	 * @param spriteId the literal that identifies this particular sprite
	 * @param pChars the chars that compose the NAMTBL sprite
	 * @param frameSize the width and height of the sprites
	 * @param pAlignment the NAMTBL sprite alignment and drawing direciton
	 */
	public NamtblSprite(final String spriteId,
			final List<Char> pChars, final Size frameSize,
			final NamtblSpriteAlignment pAlignment) {
		super();

		this.spriteId = Validate.notBlank(spriteId);
		this.alignment = Objects.requireNonNull(pAlignment);

		Objects.requireNonNull(pChars);
		Validate.isTrue(!pChars.isEmpty());

		// Width / Height
		this.actualSize = Size.of(pChars);

		{
			final List<Char> alignedChars = align(pChars, this.alignment);

			final Coord startingPosition = new Coord(
					this.alignment == NamtblSpriteAlignment.LEFT ? 0
							: this.alignment == NamtblSpriteAlignment.RIGHT ? frameSize.getWidth() -1
							: Math.floorDiv(this.actualSize.getWidth() - 1, 2),
					frameSize.getHeight() -1);
			
			this.relativeChars = CharUtils.asRelativeChars(alignedChars, startingPosition);
		}

		// Optimizable values
		this.optimizableValues = ArrayUtils.toPrimitive(
				findMostCommonValues(pChars, 3).toArray(new Short[0]));
	}

	/**
	 * @return the asm lines to render this particular sprite
	 */
	public List<String> asAsm() {

		final List<String> lines = new ArrayList<>();

		if (this.actualSize.getWidth() == 1) {

			// Source code for special cases: width == 1
			lines.addAll(this.asmHeader());
			if (this.actualSize.getHeight() == 1) {
				lines.addAll(indent(this.asmInstructions1x1()));
			} else {
				lines.add(indent("ex de, hl"));
				lines.addAll(indent(this.asmPrepareOptimizableValues()));
				lines.addAll(indent(this.asmInstructions1xN()));
			}
			lines.add(indent("ret"));

		} else {

			// Source code
			lines.addAll(this.asmHeader());
			lines.add(indent("ex de, hl"));
			lines.addAll(indent(this.asmPrepareOptimizableValues()));
			lines.addAll(indent(this.asmInstructions()));
			lines.add(indent("ret"));
		}

		return lines;
	}

	/**
	 * @return the asm lines to render this 1x1 sprite
	 */
	private List<String> asmInstructions1x1() {

		return Arrays.asList(
				String.format("ld a, %s", asmByte(this.relativeChars.iterator().next())),
				"ld (de), a");
	}

	/**
	 * @return the asm lines to render this 1xN sprite
	 */
	private List<String> asmInstructions1xN() {

		System.err.println(this.relativeChars);

		final List<String> lines = new ArrayList<>();

		lines.add("ld bc, -NAMTBL_BUFFER_WIDTH");
		for (final Char c : this.relativeChars) {
			lines.addAll(Collections.nCopies(-c.getY(), "add hl, bc ; (0, -1)"));
			lines.add(String.format("ld [hl], %s", asmByte(c)));
		}
		return lines;
	}

	private List<String> asmHeader() {

		switch (this.alignment) {
			case DEFAULT:
			{
				final List<String> lines = new ArrayList<>();
				lines.add(String.format(".%s:", this.spriteId));
				if (this.actualSize.getWidth() % 2 == 0) {
					lines.add(indent("dec de ; (even width centering)"));
				}
				return lines;
			}

			case ALIGNED:
			{
				final List<String> lines = new ArrayList<>();
				lines.add(String.format(".%s_L:", this.spriteId));
				if (this.actualSize.getWidth() % 2 == 0) {
					lines.add(indent("dec de ; (-1, 0)"));
				}
				lines.add(String.format(".%s_R:", this.spriteId));
				return lines;
			}

			case LEFT:
			case RIGHT:
			default:
				return Collections.singletonList(String.format(".%s:", this.spriteId));
		}
	}

	private List<String> asmPrepareOptimizableValues() {

		List<String> lines = new ArrayList<>();
		for (int i = 0, n = this.optimizableValues.length; i < n; i++) {
			final short optimizationValue = this.optimizableValues[i];
			final long optimizationOcurrences = this.relativeChars
					.stream()
					.map(Char::getValue)
					.filter(v -> v.shortValue() == optimizationValue)
					.count();
			lines.add(String.format("ld %s, %s ; (optimization, %d ocurrences)",
					OPTIMIZATION_REGISTERS[i],
					asmByte(optimizationValue),
					optimizationOcurrences));
		}
		return lines;
	}

	/**
	 * @return the asm lines of code to render this particular sprite
	 */
	private List<String> asmInstructions() {

		final List<String> lines = new ArrayList<>();
		for (final Char c : this.relativeChars) {
			lines.addAll(this.asmOffsetInstructions(c));
			lines.add(String.format("ld [hl], %s", asmByte(c)));
		}
		return lines;
	}

	private List<String> asmOffsetInstructions(final Char c) {
		
		final int x = c.getX();
		final int y = c.getY();

		// Optimized horizontal only
		if ((y == 0) && (Math.abs(x) <= 3)) {
			return (x == 0)
					? Collections.emptyList()
					: Collections.nCopies(
							Math.abs(x),
							x > 0 ? "inc hl ; (+1, 0)" : "dec hl ; (-1, 0)");
		}

		// Optimized cases
		final Coord offset = c.coords();

		if (this.previousOffset != null) {
			if (offset.equals(this.previousOffset)) {
				return Collections.singletonList(
						String.format("add hl, bc ; %s", offset));
			}
			if (offset.equals(this.previousOffset.add(ONE_TO_RIGHT))) {
				return Arrays.asList(
						String.format("add hl, bc ; %s, as %s + %s", offset, this.previousOffset, ONE_TO_RIGHT),
						"inc hl");
			}
			if (offset.equals(this.previousOffset.add(ONE_TO_LEFT))) {
				return Arrays.asList(
						String.format("add hl, bc ; %s, as %s + %s", offset, this.previousOffset, ONE_TO_LEFT),
						"dec hl");
			}
		}

		// Default case
		this.previousOffset = offset;
		
		return Arrays.asList(
				String.format("ld bc, %d %+d*NAMTBL_BUFFER_WIDTH ; %s", x, y, offset),
				"add hl, bc");
	}

	/*
	 * (utility routines)
	 */
	
	private static List<Char> align(final List<Char> chars, final NamtblSpriteAlignment alignment) {
		
		final List<Char> alignedChars = new ArrayList<>(chars);
		alignedChars.sort(alignment.getComparator());
		
		return alignedChars;
	}

	private static List<Short> findMostCommonValues(final List<Char> chars, final int limit) {
		
		return chars
				.stream()
				.collect(Collectors.groupingBy(Char::getValue, Collectors.counting()))
				.entrySet()
				.stream()
				.filter(entry -> entry.getValue() >= 3)
				.sorted(Map.Entry.<Short, Long> comparingByValue().reversed())
				.limit(limit)
				.map(Entry::getKey)
				.collect(Collectors.toList());
	}

	private static List<String> indent(final List<String> list) {
		return list.stream().map(s -> indent(s)).collect(Collectors.toList());
	}

	private static String indent(final String s) {
		return StringUtils.prependIfMissing(s, "\t");
	}

	private String asmByte(final Char c) {

		final short value = c.getValue();
		final int optimizationIndex = ArrayUtils.indexOf(this.optimizableValues, value);
		return optimizationIndex >= 0
				? Character.toString(OPTIMIZATION_REGISTERS[optimizationIndex])
				: asmByte(c.getValue());
	}

	private static String asmByte(final short s) {
		return "$" + StringUtils.leftPad(StringUtils.right(Integer.toHexString(s), 2), 2, '0');
	}


}

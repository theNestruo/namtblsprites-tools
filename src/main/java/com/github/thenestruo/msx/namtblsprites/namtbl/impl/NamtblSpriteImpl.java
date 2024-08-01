package com.github.thenestruo.msx.namtblsprites.namtbl.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.github.thenestruo.msx.namtblsprites.model.Char;
import com.github.thenestruo.msx.namtblsprites.namtbl.NamtblSprite;
import com.github.thenestruo.msx.namtblsprites.namtbl.NamtblSpriteAlignment;

/**
 * NamtblSprite default implementation
 */
public class NamtblSpriteImpl implements NamtblSprite {

	private static final char[] OPTIMIZATION_REGISTER = new char[]{ 'a', 'd', 'e' };

	private final String spriteId;
	private final NamtblSpriteAlignment alignment;
	private final int width;
	private final int height;
	private final List<Char> sequence;

	private final short[] optimizationValues;
	private String previousOffset;

	/**
	 * Constructor
	 * @param spriteId the literal that identifies this particular sprite
	 * @param pChars the chars that compose the NAMTBL sprite
	 * @param pWidth the width of the sprites
	 * @param pHeight the height of the sprites
	 * @param pAlignment the NAMTBL sprite alignment and drawing direciton
	 */
	public NamtblSpriteImpl(final String spriteId,
			final List<Char> pChars, final int pWidth, final int pHeight,
			final NamtblSpriteAlignment pAlignment) {
		super();

		this.spriteId = Validate.notBlank(spriteId);
		this.alignment = pAlignment;

		Objects.requireNonNull(pChars);
		Validate.isTrue(!pChars.isEmpty());

		// Width / Height
		{
			final Iterator<Char> it = pChars.iterator();
			final Char firstChar = it.next();
			int minX = firstChar.getX();
			int minY = firstChar.getY();
			int maxX = firstChar.getX();
			int maxY = firstChar.getY();
			while (it.hasNext()) {
				final Char c = it.next();
				minX = Math.min(minX, c.getX());
				minY = Math.min(minY, c.getY());
				maxX = Math.max(maxX, c.getX());
				maxY = Math.max(maxY, c.getY());
			}
			this.width = maxX - minX + 1;
			this.height = maxY - minY + 1;
		}

		{
			final List<Char> chars = new ArrayList<>(pChars);
			chars.sort(this.alignment.getComparator());

			final List<Char> lSequence = new ArrayList<>();
			int x = this.alignment == NamtblSpriteAlignment.LEFT ? 0
					: this.alignment == NamtblSpriteAlignment.RIGHT ? pWidth -1
					: Math.floorDiv(this.width - 1, 2);
			int y = pHeight -1;
			for (final Char c : chars) {
				final int diffX = c.getX() - x;
				final int diffY = c.getY() - y;
				lSequence.add(new Char(diffX, diffY, c.getValue()));
				x = c.getX();
				y = c.getY();
			}
			this.sequence = Collections.unmodifiableList(lSequence);
		}

		{
			this.optimizationValues = ArrayUtils.toPrimitive(
					pChars
					.stream()
					.collect(Collectors.groupingBy(Char::getValue, Collectors.counting()))
					.entrySet()
					.stream()
					.filter(entry -> entry.getValue() >= 3)
					.sorted(Comparator.<Map.Entry<Short, Long>, Long> comparing(Entry::getValue).reversed())
					.limit(3)
					.map(Entry::getKey)
					.collect(Collectors.toList())
					.toArray(new Short[0]));
		}
	}

	/**
	 * @return the asm lines to render this particular sprite
	 */
	@Override
	public List<String> asAsm() {

		final List<String> lines = new ArrayList<>();

		if (this.width == 1) {

			// Source code for special cases: width == 1
			lines.addAll(this.asmHeader());
			if (this.height == 1) {
				lines.addAll(indent(this.asmInstructions1x1()));
			} else {
				lines.add(indent("ex de, hl"));
				lines.addAll(indent(this.asmPrepareOptimizations()));
				lines.addAll(indent(this.asmInstructions1xN()));
			}
			lines.add(indent("ret"));

		} else {

			// Source code
			lines.addAll(this.asmHeader());
			lines.add(indent("ex de, hl"));
			lines.addAll(indent(this.asmPrepareOptimizations()));
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
				String.format("ld a, %s", asmByte(this.sequence.iterator().next())),
				"ld (de), a");
	}

	/**
	 * @return the asm lines to render this 1xN sprite
	 */
	private List<String> asmInstructions1xN() {

		System.err.println(this.sequence);

		final List<String> lines = new ArrayList<>();

		lines.add("ld bc, -NAMTBL_BUFFER_WIDTH");
		for (final Char c : this.sequence) {
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
				if (this.width % 2 == 0) {
					lines.add(indent("dec de ; (even width centering)"));
				}
				return lines;
			}

			case ALIGNED:
			{
				final List<String> lines = new ArrayList<>();
				lines.add(String.format(".%s_L:", this.spriteId));
				if (this.width % 2 == 0) {
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

	private List<String> asmPrepareOptimizations() {

		List<String> lines = new ArrayList<>();
		for (int i = 0, n = this.optimizationValues.length; i < n; i++) {
			final short optimizationValue = this.optimizationValues[i];
			final long optimizationOcurrences = this.sequence
					.stream()
					.map(Char::getValue)
					.filter(v -> v.shortValue() == optimizationValue)
					.count();
			lines.add(String.format("ld %s, %s ; (optimization, %d ocurrences)",
					OPTIMIZATION_REGISTER[i],
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
		for (final Char c : this.sequence) {
			lines.addAll(this.asmOffsetInstructions(c));
			lines.add(String.format("ld [hl], %s", asmByte(c)));
		}
		return lines;
	}

	private List<String> asmOffsetInstructions(final Char c) {

		final int x = c.getX();
		final int y = c.getY();

		if ((y == 0) && (Math.abs(x) <= 3)) {
			return (x == 0)
					? Collections.emptyList()
					: Collections.nCopies(
							Math.abs(x),
							x > 0 ? "inc hl ; (+1, 0)" : "dec hl ; (-1, 0)");
		}

		final String offset =
				String.format("%1$d %2$+d*NAMTBL_BUFFER_WIDTH ", x, y);

		if (StringUtils.equals(offset, this.previousOffset)) {
			return Collections.singletonList(
				String.format("add hl, bc ; (%+d, %+d)", x, y));

		} else {
			this.previousOffset = offset;
			return Arrays.asList(
					String.format("ld bc, %s ; (%+d, %+d)", offset, x, y),
					"add hl, bc");
		}
	}

	/*
	 * (utility routines)
	 */

	private static List<String> indent(final List<String> list) {
		return list.stream().map(s -> indent(s)).collect(Collectors.toList());
	}

	private static String indent(final String s) {
		return StringUtils.prependIfMissing(s, "\t");
	}

	private String asmByte(final Char c) {

		final short value = c.getValue();
		final int optimizationIndex = ArrayUtils.indexOf(this.optimizationValues, value);
		return optimizationIndex >= 0
				? Character.toString(OPTIMIZATION_REGISTER[optimizationIndex])
				: asmByte(c.getValue());
	}

	private static String asmByte(final short s) {
		return "$" + StringUtils.leftPad(StringUtils.right(Integer.toHexString(s), 2), 2, '0');
	}

}

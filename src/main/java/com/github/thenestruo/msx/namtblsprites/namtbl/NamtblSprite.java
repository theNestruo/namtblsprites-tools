package com.github.thenestruo.msx.namtblsprites.namtbl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
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

	private static final Coord ONE_TO_RIGHT = new Coord(1, 0);
	private static final Coord ONE_TO_LEFT = new Coord(-1, 0);

	private final String spriteId;
	private final NamtblSpriteAlignment alignment;
	private final Size actualSize;
	private final boolean compensateEvenWidthCentering;
	private final List<Char> relativeChars;

	private char[] optimizationRegisters;
	private short[] optimizableValues;
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

			Coord startingPosition;
			switch (this.alignment) {
			case LEFT:
				startingPosition = new Coord(0, frameSize.getHeight() -1);
				this.compensateEvenWidthCentering = false;
				break;
			
			case RIGHT:
				startingPosition = new Coord(frameSize.getWidth() -1, frameSize.getHeight() -1);
				this.compensateEvenWidthCentering = false;
				break;
				
			case DEFAULT:
			case ALIGNED:
			default:
				startingPosition = new Coord(
						Math.floorDiv(this.actualSize.getWidth() - 1, 2),
						frameSize.getHeight() -1);
				this.compensateEvenWidthCentering = canCompensateEvenWidthCentering(alignedChars, startingPosition); 
				if (this.compensateEvenWidthCentering) {
					startingPosition = startingPosition.add(ONE_TO_RIGHT);
				}
				break;
			}

			this.relativeChars = CharUtils.asRelativeChars(alignedChars, startingPosition);
		}
	}
	
	/**
	 * @return the asm lines to render this particular sprite
	 */
	public List<String> asAsm() {

		final List<String> lines = new ArrayList<>();

		if (this.actualSize.getWidth() == 1) {

			// Source code for special cases: width == 1
			lines.addAll(this.asmHeader(false));
			if (this.actualSize.getHeight() == 1) {
				lines.addAll(indent(this.asmInstructions1x1()));
			} else {
				lines.add(indent("ex\tde, hl"));
				lines.addAll(indent(this.asmPrepareOptimizableValues()));
				lines.addAll(indent(this.asmInstructions1xN()));
			}
			lines.add(indent("ret"));

		} else {

			// Source code
			lines.addAll(this.asmHeader(true));
			lines.add(indent("ex\tde, hl"));
			lines.addAll(indent(this.asmPrepareOptimizableValues()));
			lines.addAll(indent(this.asmInstructions()));
			lines.add(indent("ret"));
		}

		return lines;
	}

	private List<String> asmHeader(boolean allowImplicitCentering) {

		switch (this.alignment) {
			case DEFAULT:
			{
				final List<String> lines = new ArrayList<>();
				lines.add(
					String.format(".%s: ; %s", this.spriteId, this.actualSize));
				if (this.actualSize.getWidth() % 2 == 0) {
					lines.add(indent(this.compensateEvenWidthCentering
							? "; (implicit even width centering => (+1, 0))"
							: "dec\tde\t; (even width centering)"));
				}
				return lines;
			}

			case ALIGNED:
			{
				if (this.compensateEvenWidthCentering) {
					return Arrays.asList(
							String.format(".%s_R: ; %s", this.spriteId, this.actualSize),
							indent("inc\tde\t; (+1, 0)"),
							String.format(".%s_L: ; %s", this.spriteId, this.actualSize));
					
				} else {
					final List<String> lines = new ArrayList<>();
					lines.add(String.format(".%s_L: ; %s", this.spriteId, this.actualSize));
					if (this.actualSize.getWidth() % 2 == 0) {
						lines.add(indent("dec\tde\t; (-1, 0)"));
					}
					lines.add(String.format(".%s_R: ; %s", this.spriteId, this.actualSize));
					return lines;
				}
			}

			case LEFT:
			case RIGHT:
			default:
				return Collections.singletonList(
					String.format(".%s: ; %s", this.spriteId, this.actualSize));
		}
	}

	/**
	 * @return the asm lines to render this 1x1 sprite
	 */
	private List<String> asmInstructions1x1() {

		return Arrays.asList(
				String.format("ld\ta, %s", asmByteOptimizable(this.relativeChars.iterator().next())),
				"ld\t(de), a");
	}

	/**
	 * @return the asm lines to render this 1xN sprite
	 */
	private List<String> asmInstructions1xN() {

		System.err.println(this.relativeChars);

		final List<String> lines = new ArrayList<>();

		lines.add("ld\tbc, -NAMTBL_BUFFER_WIDTH");
		for (final Char c : this.relativeChars) {
			lines.addAll(Collections.nCopies(-c.getY(), "add\thl, bc\t; (0, -1)"));
			lines.add(String.format("ld\t(hl), %s", asmByteOptimizable(c)));
		}
		return lines;
	}


	/**
	 * @return the asm lines of code to render this particular sprite
	 */
	private List<String> asmInstructions() {
	
		final List<String> lines = new ArrayList<>();
		for (final ListIterator<Char> lit = this.relativeChars.listIterator(); lit.hasNext(); ) {
			final Char c = lit.next();
			lines.addAll(this.asmOffsetInstructions(c));
			lines.add(String.format("ld\t(hl), %s", this.asmByteOptimizable(c)));
			lines.addAll(this.asmUpdateOptimizableValues(lit.nextIndex()));
		}
		return lines;
	}

	private List<String> asmOffsetInstructions(final Char c) {
	
		final Coord offset = c.coords();
		final int x = offset.getX();
		final int y = offset.getY();
	
		// Horizontal only
		if (y == 0) {
	
			// (should never happen)
			if (x == 0) {
				return Collections.emptyList();
			}
	
			// Optimized cases
			if (Math.abs(x) <= 3) {
				return Collections.nCopies(
						Math.abs(x),
						x > 0
							? "inc\thl\t; (+1, 0)"
							: "dec\thl\t; (-1, 0)");
			}
		}
	
		// General cases (horizontal and vertical)
	
		try {
	
			// Attempt optimization
			if (this.previousOffset != null) {
	
				// Best case scenario (same offset)
				if (offset.equals(this.previousOffset)) {
					return Collections.singletonList(
						String.format("add\thl, bc\t; %s", offset));
				}
	
				// "inc"/"dec" optimized cases
				if (offset.equals(this.previousOffset.add(ONE_TO_RIGHT))) {
					return Arrays.asList(
							String.format("inc\tc\t; %s -> %s", this.previousOffset, offset),
							String.format("add\thl, bc\t ; %s", offset));
				}
				if (offset.equals(this.previousOffset.add(ONE_TO_LEFT))) {
					return Arrays.asList(
							String.format("dec\tc\t; %s -> %s", this.previousOffset, offset),
							String.format("add\thl, bc\t; %s", offset));
				}
	
				// "ld c,n" optimized cases
				if (Integer.signum(x) == Integer.signum(this.previousOffset.getX())) {
					return Arrays.asList(
							String.format(
									"ld\tc, $00ff AND (%+d %+d*NAMTBL_BUFFER_WIDTH)\t; %s -> %s",
									x, y, this.previousOffset, offset),
							String.format("add\thl, bc\t; %s", offset));
				}
			}
	
			// Non-optimizable case (or first offset)
			return Arrays.asList(
					String.format("ld\tbc, %d %+d*NAMTBL_BUFFER_WIDTH\t; %s", x, y, offset),
					String.format("add\thl, bc\t; %s", offset));
	
		} finally {
			this.previousOffset = offset;
		}
	}

	private String asmByteOptimizable(final Char c) {
	
		final short value = c.getValue();
		final int optimizationIndex = ArrayUtils.indexOf(this.optimizableValues, value);
		return optimizationIndex >= 0
				? Character.toString(this.optimizationRegisters[optimizationIndex])
				: asmByte(c.getValue());
	}

	private List<String> asmPrepareOptimizableValues() {
	
		// Optimizable values
		final short[] lOptimizableValues = ArrayUtils.toPrimitive(
				this.findMostCommonValues(3, 3).toArray(new Short[0]));
		
		switch (lOptimizableValues.length) {
		case 0:
			this.optimizationRegisters = ArrayUtils.EMPTY_CHAR_ARRAY;
			this.optimizableValues = ArrayUtils.EMPTY_SHORT_ARRAY;
			return Collections.emptyList();
			
		case 1:
			this.optimizationRegisters = new char[] {'a'};
			this.optimizableValues = lOptimizableValues;
			return Collections.singletonList(String.format(
					"ld\t%s, %s\t; (optimization for %d ocurrences)",
					this.optimizationRegisters[0],
					asmByte(this.optimizableValues[0]),
					this.ocurrencesOf(this.optimizableValues[0])));
			
		case 2:
			this.optimizationRegisters = new char[] {'d', 'e'};
			this.optimizableValues = lOptimizableValues;
			return Collections.singletonList(String.format(
					"ld\t%s%s, %s << 8 + %s\t; (optimization for %d and %d ocurrences)",
					this.optimizationRegisters[0],
					this.optimizationRegisters[1],
					asmByte(this.optimizableValues[0]),
					asmByte(this.optimizableValues[1]),
					this.ocurrencesOf(this.optimizableValues[0]),
					this.ocurrencesOf(this.optimizableValues[1])));
			
		case 3:
		default:
			this.optimizationRegisters = new char[] {'a', 'd', 'e'};
			this.optimizableValues = ArrayUtils.subarray(lOptimizableValues, 0, 3);
			return Arrays.asList(
					String.format("ld\t%s, %s\t; (optimization for %d ocurrences)",
							this.optimizationRegisters[0],
							asmByte(this.optimizableValues[0]),
							this.ocurrencesOf(this.optimizableValues[0])),
					String.format("ld\t%s%s, %s << 8 + %s\t; (optimization for %d and %d ocurrences)",
							this.optimizationRegisters[1],
							this.optimizationRegisters[2],
							asmByte(this.optimizableValues[1]),
							asmByte(this.optimizableValues[2]),
							this.ocurrencesOf(this.optimizableValues[1]),
							this.ocurrencesOf(this.optimizableValues[2])));
		}
	}

	private List<String> asmUpdateOptimizableValues(final long skip) {
		
		final List<String> lines = new ArrayList<>();
		for (int i = 0, n = this.optimizableValues.length; i < n; i++) {
			
			final short value = this.optimizableValues[i];
			final long ocurrences = this.ocurrencesOf(skip, value);
			
			final short valueMinus1 = (short) (value - 1);
			final long ocurrencesOfMinus1 = ArrayUtils.contains(this.optimizableValues, valueMinus1)
					? -1L
					: this.ocurrencesOf(skip, valueMinus1);

			final short valuePlus1 = (short) (value + 1);
			final long ocurrencesOfPlus1 = ArrayUtils.contains(this.optimizableValues, valuePlus1)
					? -1L
					: this.ocurrencesOf(skip, valuePlus1);
			
			if ((ocurrencesOfMinus1 - ocurrences >= 2) && (ocurrencesOfMinus1 >= ocurrencesOfPlus1)) {
				
				// Update optimization to value - 1
				lines.add(String.format("dec\t%s\t; (optimization for %d > %d ocurrences)",
						this.optimizationRegisters[i],
						ocurrencesOfMinus1,
						ocurrences));
				this.optimizableValues[i] = valueMinus1;
				continue;
			}
			
			if (ocurrencesOfPlus1 - ocurrences >= 2) {
				
				// Update optimization to value + 1
				lines.add(String.format("inc\t%s\t; (optimization for %d > %d ocurrences)",
						this.optimizationRegisters[i],
						ocurrencesOfPlus1,
						ocurrences));
				this.optimizableValues[i] = valuePlus1;
				continue;
			}
		}
		return lines;
	}

	private List<Short> findMostCommonValues(final int atLeast, final int limit) {
		
		return this.findMostCommonValues(0L, atLeast, limit);
	}

	private List<Short> findMostCommonValues(final long skip, final int atLeast, final int limit) {

		return this.relativeChars
				.stream()
				.skip((long) skip)
				.collect(Collectors.groupingBy(Char::getValue, Collectors.counting()))
				.entrySet()
				.stream()
				.filter(entry -> entry.getValue() >= atLeast)
				.sorted(Map.Entry.<Short, Long> comparingByValue().reversed())
				.limit(limit)
				.map(Entry::getKey)
				.collect(Collectors.toList());
	}
	
	private long ocurrencesOf(final short value) {
		
		return this.ocurrencesOf(0L, value);
	}
	
	private long ocurrencesOf(final long skip, final short value) {
		
		return this.relativeChars
				.stream()
				.skip(skip)
				.map(Char::getValue)
				.filter(v -> v.shortValue() == value)
				.count();
	}

	/*
	 * (utility routines)
	 */

	private static List<Char> align(final List<Char> chars, final NamtblSpriteAlignment alignment) {

		final List<Char> alignedChars = new ArrayList<>(chars);
		alignedChars.sort(alignment.getComparator());

		return alignedChars;
	}

	private static boolean canCompensateEvenWidthCentering(final List<Char> alignedChars, final Coord startingPosition) {
		
		if (Size.of(alignedChars).getWidth() % 2 != 0) {
			return false;
		}
		
		final Coord compensatedStartingPosition = startingPosition.add(ONE_TO_RIGHT);
		final Coord firstAlignedCoord = alignedChars.get(0).coords();
		return firstAlignedCoord.equals(compensatedStartingPosition);
	}

	private static List<String> indent(final List<String> list) {
		return list.stream().map(s -> indent(s)).collect(Collectors.toList());
	}

	private static String indent(final String s) {
		return StringUtils.prependIfMissing(s, "\t");
	}

	private static String asmByte(final short s) {
		return "$" + StringUtils.leftPad(StringUtils.right(Integer.toHexString(s), 2), 2, '0');
	}
}

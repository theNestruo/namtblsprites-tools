package com.github.thenestruo.msx.namtblsprites.namtbl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.github.thenestruo.msx.namtblsprites.model.Char;
import com.github.thenestruo.msx.namtblsprites.model.Coord;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * A NAMTBL sprite that can be read as asm code
 */
public class NamtblSprite {

	/** NAMTBL sprite alignment and drawing direciton */
	public static enum Alignment {

		/** Aligned left, draw to right */
		LEFT(Coord.bottomUpComparator.thenComparing(Coord.leftToRightComparator)),

		/** Centered, draw to right */
		CENTER(Coord.bottomUpComparator.thenComparing(Coord.leftToRightComparator)),

		/** Aligned right, draw to left */
		RIGHT(Coord.bottomUpComparator.thenComparing(Coord.rightToLeftComparator));

		private final Comparator<Coord> comparator;

		private Alignment(final Comparator<Coord> pComparator) {
			this.comparator = pComparator;
		}
	};

	private final String spriteId;
	private final Alignment alignment;
	private final int width;
	private final int height;
	private final List<Char> sequence;

	/**
	 * Constructor
	 * @param spriteId the literal that identifies this particular sprite
	 * @param pChars the chars that compose the NAMTBL sprite
	 * @param pWidth the width of the sprites
	 * @param pHeight the height of the sprites
	 * @param pAlignment the NAMTBL sprite alignment and drawing direciton
	 */
	public NamtblSprite(final String spriteId,
			final List<Char> pChars, final int pWidth, final int pHeight, final Alignment pAlignment) {
		super();

		this.spriteId = Validate.notBlank(spriteId);
		this.alignment = pAlignment;

		Validate.notNull(pChars);
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
			chars.sort(alignment.comparator);

			final List<Char> lSequence = new ArrayList<>();
			int x = alignment == Alignment.LEFT ? 0
					: alignment == Alignment.CENTER ? Math.floorDiv(width - 1, 2)
					: pWidth -1;
			int y = pHeight -1;
			for (Char c : chars) {
				final int diffX = c.getX() - x;
				final int diffY = c.getY() - y;
				lSequence.add(new Char(diffX, diffY, c.getValue()));
				x = c.getX() + (alignment == Alignment.RIGHT ? -1 : 1);
				y = c.getY();
			}
			this.sequence = Collections.unmodifiableList(lSequence);
		}
	}

	/**
	 * @return the asm lines to render this particular sprite
	 */
	public List<String> asAsm() {

		final List<String> lines = new ArrayList<>();

		if (this.width == 1) {

			// Special cases: width == 1
			lines.addAll(this.asmHeader());
			if (this.height == 1) {
				lines.addAll(indent(this.asmInstructions1x1()));
			} else {
				lines.addAll(indent(this.asmInstructions1xN()));
			}
			lines.add(indent("ret"));

		} else {

			// Source code
			lines.addAll(this.asmHeader());
			lines.add(indent(String.format("ld hl, .%s_DATA", this.spriteId)));
			lines.addAll(indent(this.asmInstructions()));
			lines.add(indent("ret"));
			lines.addAll(alignment != Alignment.RIGHT
					? this.asmDataLeft()
					: this.asmDataRight());
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

		final List<String> lines = new ArrayList<>(Arrays.asList(
				"ex de, hl",
				"ld bc, -NAMTBL_BUFFER_WIDTH"));
		for (Char c : this.sequence) {
			lines.addAll(Collections.nCopies(-c.getY(), "add hl, bc"));
			lines.add(String.format("ld [hl], %s", asmByte(c)));
		}
		return lines;
	}

	private List<String> asmHeader() {

		if (alignment != Alignment.CENTER) {
			return Collections.singletonList(String.format(".%s:", this.spriteId));
		}

		final List<String> lines = new ArrayList<>();
		lines.add(String.format(".%s_L:", this.spriteId));
		if (this.width % 2 != 0) {
			lines.add(indent("dec de ; (-1, 0)"));
		}
		lines.add(String.format(".%s_R:", this.spriteId));
		return lines;
	}

	/**
	 * @return the asm lines of code to render this particular sprite
	 */
	private List<String> asmInstructions() {

		final List<String> lines = new ArrayList<>();
		for (Char c : this.sequence) {
			lines.addAll(this.asmOffsetInstructions(c));
			lines.add(alignment == Alignment.RIGHT ? "ldd" : "ldi");
		}
		return lines;
	}

	private List<String> asmOffsetInstructions(final Char c) {

		final int x = c.getX();
		final int y = c.getY();

		if (y == 0) {
			return (x == 0)
					? Collections.emptyList()
					: Collections.nCopies(
							Math.abs(x),
							x > 0 ? "inc de ; (+1, 0)" : "dec de ; (-1, 0)");
		}

		final String offset =
				String.format("%1$d %2$+d*NAMTBL_BUFFER_WIDTH ", -x, -y);

		return Arrays.asList(
				"ld a, e",
				String.format("sub %s ; (%+d, %+d)", offset, x, y),
				"ld e, a",
				"jp nc, $+4",
				"dec d");
	}

	private List<String> asmDataLeft() {

		final List<String> lines = new ArrayList<>();
		lines.add(String.format(".%s_DATA:", this.spriteId));
		for (List<Char> partition : ListUtils.partition(this.sequence, 8)) {
			lines.add(indent("db " + StringUtils.join(asmBytes(partition), ", ")));
		}
		return lines;
	}

	private List<String> asmDataRight() {

		final List<Char> reversedSequence = new ArrayList<>(this.sequence);
		Collections.reverse(reversedSequence);

		final List<String> lines = new ArrayList<>();
		for (List<Char> partition : ListUtils.partition(reversedSequence, 8)) {
			lines.add(indent("db " + StringUtils.join(asmBytes(partition), ", ")));
		}
		lines.add(String.format(".%s_DATA: equ $ - 1", this.spriteId));
		return lines;
	}

	/*
	 * (utility routines)
	 */

	private static List<String> indent(final List<String> list) {
		return list.stream().map(s -> indent(s)).collect(Collectors.toList());
	}

	private static String indent(String s) {
		return StringUtils.prependIfMissing(s, "\t");
	}

	private static List<String> asmBytes(final List<Char> list) {
		return list.stream().map(c -> asmByte(c)).collect(Collectors.toList());
	}

	private static String asmByte(final Char c) {
		return asmByte(c.getValue());
	}

	private static String asmByte(final short s) {
		return "$" + StringUtils.leftPad(StringUtils.right(Integer.toHexString(s), 2), 2, '0');
	}

}

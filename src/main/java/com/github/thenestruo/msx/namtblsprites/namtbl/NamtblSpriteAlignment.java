package com.github.thenestruo.msx.namtblsprites.namtbl;

import java.util.Comparator;

import com.github.thenestruo.msx.namtblsprites.model.Coord;

/**
 * NAMTBL sprite alignment and drawing direciton
 */
public enum NamtblSpriteAlignment {

	/** Default (centered), draw to right */
	DEFAULT(Coord.bottomUpComparator.thenComparing(Coord.leftToRightComparator)),

	/** Aligned left, draw to right */
	LEFT(Coord.bottomUpComparator.thenComparing(Coord.leftToRightComparator)),

	/** Aligned to center (alt. entry points for even widths), draw to right */
	ALIGNED(Coord.bottomUpComparator.thenComparing(Coord.leftToRightComparator)),

	/** Aligned right, draw to left */
	RIGHT(Coord.bottomUpComparator.thenComparing(Coord.rightToLeftComparator));

	private final Comparator<Coord> comparator;

	private NamtblSpriteAlignment(final Comparator<Coord> pComparator) {
		this.comparator = pComparator;
	}

	public Comparator<Coord> getComparator() {
		return this.comparator;
	}
}

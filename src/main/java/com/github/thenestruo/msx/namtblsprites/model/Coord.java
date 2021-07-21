package com.github.thenestruo.msx.namtblsprites.model;

import java.util.Comparator;

/**
 * A bidimensional coordinate
 */
public class Coord {

	public static final Comparator<Coord> leftToRightComparator = Comparator.<Coord> comparingInt(p -> p.getX());
	public static final Comparator<Coord> rightToLeftComparator = leftToRightComparator.reversed();
	public static final Comparator<Coord> topDownComparator = Comparator.<Coord> comparingInt(p -> p.getY());
	public static final Comparator<Coord> bottomUpComparator = topDownComparator.reversed();

	protected final int x;
	protected final int y;

	public Coord(final int x, final int y) {
		super();

		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}

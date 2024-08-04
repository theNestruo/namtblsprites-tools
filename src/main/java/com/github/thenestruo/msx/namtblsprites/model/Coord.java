package com.github.thenestruo.msx.namtblsprites.model;

import java.util.Comparator;
import java.util.Objects;

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

	public Coord(final Coord c) {
		this(Objects.requireNonNull(c).x,
				Objects.requireNonNull(c).y);
	}
	
	public Coord(final int x, final int y) {
		super();

		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {

		return String.format("(%d,%d)", this.x, this.y);
	}
	
	public Coord negate() {
		
		return new Coord(-this.x, -this.y);
	}
	
	public Coord add(final Coord addend) {
		
		return addend == null ? this : new Coord(this.x + addend.x, this.y + addend.y);
	}
	
	public Coord subtract(final Coord subtrahend) {
		
		return subtrahend == null ? this : this.add(subtrahend.negate());
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}

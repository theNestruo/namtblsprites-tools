package com.github.thenestruo.msx.namtblsprites.model;

import java.util.Comparator;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
	public int hashCode() {
		
		return new HashCodeBuilder()
				.append(this.x)
				.append(this.y)
				.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {

		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != this.getClass()) {
			return false;
		}
		final Coord that = (Coord) obj;

		return new EqualsBuilder()
				.append(this.x, that.x)
				.append(this.y, that.y)
				.isEquals();
	}

	@Override
	public String toString() {

		return String.format("(%+d, %+d)", this.x, this.y);
	}
	
	public Coord coords() {
		
		return new Coord(this.x, this.y);
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

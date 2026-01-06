package com.github.thenestruo.msx.namtblsprites.model;

import java.util.Objects;

/**
 * A char that compose a NAMTBL sprite
 */
public class Char extends Coord {

	protected final int value;

	public Char(final Coord coord, final int value) {
		super(coord);
		this.value = value;
	}

	public Char(final int x, final int y, final int value) {
		super(x, y);
		this.value = value;
	}

	@Override
	public int hashCode() {

		return Objects.hash(super.hashCode(), this.value);
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

		final Char that = (Char) obj;
		return super.equals(obj)
				&& (this.value == that.value);
	}

	@Override
	public String toString() {

		return String.format("%s,%d", super.toString(), this.value);
	}

	public Char relativeTo(final Coord position) {

		return new Char(this.subtract(position), this.value);
	}

	public int getValue() {
		return this.value;
	}
}

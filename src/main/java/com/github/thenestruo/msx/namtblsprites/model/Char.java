package com.github.thenestruo.msx.namtblsprites.model;

/**
 * A char that compose a NAMTBL sprite
 */
public class Char extends Coord {

	protected final short value;

	public Char(final Coord coord, final short value) {
		super(coord);
		this.value = value;
	}

	public Char(final int x, final int y, final short value) {
		super(x, y);
		this.value = value;
	}

	@Override
	public String toString() {

		return String.format("(%d,%d),%d", this.x, this.y, this.value);
	}
	
	public Char relativeTo(final Coord position) {
		
		return new Char(this.subtract(position), this.value); 
	}

	public short getValue() {
		return value;
	}
}

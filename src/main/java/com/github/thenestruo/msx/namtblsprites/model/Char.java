package com.github.thenestruo.msx.namtblsprites.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
	public int hashCode() {
		
		return new HashCodeBuilder()
				.appendSuper(super.hashCode())
				.append(this.value)
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
		final Char that = (Char) obj;

		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(this.x, that.x)
				.append(this.y, that.y)
				.isEquals();
	}

	@Override
	public String toString() {

		return String.format("%s,%d", super.toString(), this.value);
	}
	
	public Char relativeTo(final Coord position) {
		
		return new Char(this.subtract(position), this.value); 
	}

	public short getValue() {
		return value;
	}
}

package com.github.thenestruo.msx.namtblsprites.model;

import java.util.Collection;

public class Size {

	public static final Size EMPTY = new Size(0, 0);

	public static final Size of(final Collection<? extends Coord> coords) {

		if ((coords == null) || coords.isEmpty()) {
			return EMPTY;
		}

		final int minX = coords.stream().map(Coord::getX).reduce(Integer::min).orElseThrow();
		final int minY = coords.stream().map(Coord::getY).reduce(Integer::min).orElseThrow();
		final int maxX = coords.stream().map(Coord::getX).reduce(Integer::max).orElseThrow();
		final int maxY = coords.stream().map(Coord::getY).reduce(Integer::max).orElseThrow();

		return new Size(
				(maxX - minX) + 1,
				(maxY - minY) + 1);
	}

	protected final int width;
	protected final int height;

	public Size(final int width, final int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public String toString() {

		return String.format("%dx%d", this.width, this.height);
	}

	public int size() {
		return this.width * this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
}

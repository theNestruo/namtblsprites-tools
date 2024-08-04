package com.github.thenestruo.msx.namtblsprites.model;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;

public class Size {
	
	public static final Size EMPTY = new Size(0, 0);
	
	public static final Size of(Collection<? extends Coord> coords) {
		
		if (CollectionUtils.isEmpty(coords)) {
			return EMPTY;
		}
		
		final int minX = coords.stream().map(Coord::getX).reduce(Integer::min).get();
		final int minY = coords.stream().map(Coord::getY).reduce(Integer::min).get();
		final int maxX = coords.stream().map(Coord::getX).reduce(Integer::max).get();
		final int maxY = coords.stream().map(Coord::getY).reduce(Integer::max).get();
		
		return new Size(
				maxX - minX + 1,
				maxY - minY + 1);
	}

	protected final int width;
	protected final int height;
	
	public Size(int width, int height) {
		super();
		
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
		return width;
	}

	public int getHeight() {
		return height;
	}
}

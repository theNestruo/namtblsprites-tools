package com.github.thenestruo.msx.namtblsprites.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * A bidimensional chunk of raw data
 */
public class RawData {

	private final List<Short> data;
	private final int width;
	private final int height;

	/**
	 * Constructor
	 * @param data the raw data
	 * @param width the width of the chunk
	 * @param height the height of the chunk
	 */
	public RawData(final List<Short> data, final int width, final int height) {
		super();

		this.data = Validate.notNull(data);

		Validate.isTrue(data.size() == width * height);
		this.width = width;
		this.height = height;
	}

	/**
	 * Slices the chunk into smaller chunks
	 * @param sliceWidth the width of the slices
	 * @param sliceHeight the height of the slices
	 * @return the slices in top to down, then left to right, order
	 */
	public List<RawData> slice(final int sliceWidth, final int sliceHeight) {

		final List<RawData> slices = new ArrayList<>();
		for (int y = 0; y < height; y += sliceHeight) {
			for (int x = 0; x < width; x += sliceWidth) {
				final List<Short> sliceData = new ArrayList<>();
				for (int i = 0, fromIndex = y * this.width + x; i < sliceHeight; i++, fromIndex += this.width) {
					sliceData.addAll(this.data.subList(fromIndex, fromIndex + sliceWidth));
				}
				slices.add(new RawData(sliceData, sliceWidth, sliceHeight));
			}
		}
		return slices;
	}

	public List<Short> getData() {
		return data;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}

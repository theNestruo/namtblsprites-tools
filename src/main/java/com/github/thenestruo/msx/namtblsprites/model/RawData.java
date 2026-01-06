package com.github.thenestruo.msx.namtblsprites.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.github.thenestruo.commons.Bools;

/**
 * A bidimensional chunk of raw data
 */
public class RawData {

	private final List<Integer> data;
	private final Size size;

	/**
	 * Constructor
	 *
	 * @param data the raw data
	 * @param size the width and height of the chunk
	 */
	public RawData(final List<Integer> data, final Size size) {
		this.data = Collections.unmodifiableList(Objects.requireNonNull(data));

		Bools.requireTrue(data.size() == size.size());
		this.size = size;
	}

	/**
	 * Slices the chunk into smaller chunks
	 *
	 * @param sliceSize the width and height of the slices
	 * @return the slices in top to down, then left to right, order
	 */
	public List<RawData> slice(final Size sliceSize) {

		final List<RawData> slices = new ArrayList<>();
		for (int y = 0; y < this.size.getHeight(); y += sliceSize.getHeight()) {
			for (int x = 0; x < this.size.getWidth(); x += sliceSize.getWidth()) {
				final List<Integer> sliceData = new ArrayList<>();
				for (int i = 0, fromIndex = (y * this.size.getWidth()) + x; i < sliceSize
						.getHeight(); i++, fromIndex += this.size.getWidth()) {
					sliceData.addAll(this.data.subList(fromIndex, fromIndex + sliceSize.getWidth()));
				}
				slices.add(new RawData(sliceData, sliceSize));
			}
		}
		return slices;
	}

	/**
	 * @param blankValue the value that represents the absence of character
	 * @param addend     the value that will be added to the raw values
	 * @return the chars that compose the NAMTBL sprite
	 */
	public List<Char> asChars(final int blankValue, final int addend) {

		final List<Char> chars = new ArrayList<>();
		int y = 0, x = 0;
		for (final Integer s : this.data) {
			if (s.intValue() != blankValue) {
				chars.add(new Char(x, y, (s.intValue() + addend) - 1));
			}
			if ((++x) == this.size.getWidth()) {
				x = 0;
				y++;
			}
		}

		return chars;
	}

	public List<Integer> getData() {
		return Collections.unmodifiableList(this.data);
	}

	public Size getSize() {
		return this.size;
	}
}

package com.github.thenestruo.msx.namtblsprites.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * The raw data for a NAMTBL sprite
 */
public class RawSprite {

	/**
	 * Builds the raw data for a NAMTBL sprite from a bidimensional chunk of data
	 * @param data the chunk of raw data
	 * @param blankValue the value that represents the absence of character
	 * @param addend the value that will be added to the raw values
	 */
	public static RawSprite of(final RawData data, final short blankValue, final short addend) {

		final List<Char> chars = new ArrayList<>();
		int y = 0, x = 0;
		for (Short s : data.getData()) {
			if (s.shortValue() != blankValue) {
				chars.add(new Char(x, y, (short) (s.shortValue() + addend - 1)));
			}
			if ((++x) == data.getWidth()) {
				x = 0;
				y++;
			}
		}

		return new RawSprite(chars);
	}

	private final List<Char> chars;

	/**
	 * Constructor
	 * @param chars the chars that compose the NAMTBL sprite
	 */
	private RawSprite(final List<Char> chars) {
		super();

		this.chars = Collections.unmodifiableList(Validate.notNull(chars));
	}

	public List<Char> getChars() {
		return Collections.unmodifiableList(chars);
	}
}

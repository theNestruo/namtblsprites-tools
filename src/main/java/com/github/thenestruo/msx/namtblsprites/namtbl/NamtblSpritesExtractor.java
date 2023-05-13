package com.github.thenestruo.msx.namtblsprites.namtbl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import com.github.thenestruo.msx.namtblsprites.model.Char;
import com.github.thenestruo.msx.namtblsprites.model.RawData;
import com.github.thenestruo.msx.namtblsprites.model.RawSprite;

/**
 * An extractor of {@link NamtblSpriteDefaultImpl NAMTBL sprites}
 * from a bidimensional chunk of raw data
 */
public class NamtblSpritesExtractor<S extends NamtblSprite> {

	private final NamtblSpriteFactory<S> factory;

	private final RawData data;
	private final short blankValue;
	private final short addend;
	private final String spriteName;

	/**
	 * Constructor
	 * @param factory the NamtblSprite factory
	 * @param data the bidimensional chunk of raw data
	 * @param blankValue the value that represents the absence of character
	 * @param spriteName the name of the sprite
	 */
	public NamtblSpritesExtractor(
			final NamtblSpriteFactory<S> factory, final RawData data,
			final short blankValue, final short addend, final String spriteName) {
		super();

		this.factory = Validate.notNull(factory);
		this.data = Validate.notNull(data);
		this.blankValue = blankValue;
		this.addend = addend;
		this.spriteName = Validate.notNull(spriteName);
	}

	/**
	 * Builds the {@link NamtblSpriteDefaultImpl NAMTBL sprites}
	 * @param width the width of the sprites
	 * @param height the width of the sprites
	 * @return the NAMTBL sprites in top to down, then left to right, order
	 */
	public List<S> extractFrom(final int width, final int height) {

		final List<S> sprites = new ArrayList<>();
		int i = 0;

		// Slices the chunk into raw sprites
		for (final RawData slice : this.data.slice(width, height)) {
			final String spriteId = String.format("%s_%d", this.spriteName, i++);

			// Extracts the chars of the sprite
			final List<Char> chars = new ArrayList<>(RawSprite.of(slice, this.blankValue, this.addend).getChars());

			// Saves the sprite
			if (!chars.isEmpty()) {
				sprites.add(this.factory.instance(spriteId, chars, width, height));
			}
		}
		return sprites;
	}
}

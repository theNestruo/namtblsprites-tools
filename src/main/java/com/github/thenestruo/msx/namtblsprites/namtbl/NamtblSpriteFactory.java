package com.github.thenestruo.msx.namtblsprites.namtbl;

import java.util.ArrayList;
import java.util.List;

import com.github.thenestruo.msx.namtblsprites.model.Char;
import com.github.thenestruo.msx.namtblsprites.model.RawData;
import com.github.thenestruo.msx.namtblsprites.model.RawSprite;

import org.apache.commons.lang3.Validate;

/**
 * A factory of {@link NamtblSprite NAMTBL sprites} from a bidimensional chunk of raw data
 */
public class NamtblSpriteFactory {

	private final RawData data;
	private final short blankValue;
	private final short addend;
	private final String spriteName;

	/**
	 * Constructor
	 * @param data the bidimensional chunk of raw data
	 * @param blankValue the value that represents the absence of character
	 * @param spriteName the name of the sprite
	 */
	public NamtblSpriteFactory(
		final RawData data, final short blankValue, final short addend, final String spriteName) {
		super();

		this.data = Validate.notNull(data);
		this.blankValue = blankValue;
		this.addend = addend;
		this.spriteName = Validate.notNull(spriteName);
	}

	/**
	 * Builds the {@link NamtblSprite NAMTBL sprites}
	 * @param width the width of the sprites
	 * @param height the width of the sprites
	 * @return the NAMTBL sprites in top to down, then left to right, order
	 */
	public List<NamtblSprite> create(final int width, final int height, final boolean centered) {

		final List<NamtblSprite> sprites = new ArrayList<>();
		int i = 0;

		// Slices the chunk into raw sprites
		for (RawData slice : this.data.slice(width, height)) {
			final String spriteId = String.format("%s_%d", this.spriteName, i++);

			// Extracts the chars of the sprite
			final List<Char> chars = new ArrayList<>(RawSprite.of(slice, this.blankValue, this.addend).getChars());

			// Saves the sprite
			if (!chars.isEmpty()) {
				sprites.add(new NamtblSprite(spriteId, chars, width, height, centered));
			}
		}
		return sprites;
	}
}

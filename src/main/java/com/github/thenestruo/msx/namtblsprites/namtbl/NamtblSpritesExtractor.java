package com.github.thenestruo.msx.namtblsprites.namtbl;

import java.util.ArrayList;
import java.util.List;

import com.github.thenestruo.msx.namtblsprites.model.Char;
import com.github.thenestruo.msx.namtblsprites.model.RawData;
import com.github.thenestruo.msx.namtblsprites.model.Size;

/**
 * An extractor of {@link NamtblSprite NAMTBL sprites}
 * from a bidimensional chunk of raw data
 */
public class NamtblSpritesExtractor {

	/**
	 * Builds the {@link NamtblSprite NAMTBL sprites}
	 *
	 * @param data       the bidimensional chunk of raw data
	 * @param blankValue the value that represents the absence of character
	 * @param spriteName the name of the sprite
	 * @param spriteSize the width and height of the sprites
	 * @return the NAMTBL sprites in top to down, then left to right, order
	 */
	public static List<NamtblSprite> extract(
			final RawData data, final int blankValue, final int addend,
			final String spriteName, final Size spriteSize,
			final NamtblSpriteAlignment alignment,
			final String returnInstruction) {

		final List<NamtblSprite> sprites = new ArrayList<>();
		int i = 0;

		// Slices the chunk into raw sprites
		for (final RawData slice : data.slice(spriteSize)) {
			final String spriteId = String.format("%s_%d", spriteName, i++);

			// Extracts the chars of the sprite
			final List<Char> spriteChars = slice.asChars(blankValue, addend);

			// Saves the sprite
			if (!spriteChars.isEmpty()) {
				sprites.add(new NamtblSprite(spriteId, spriteChars, spriteSize, alignment, returnInstruction));
			}
		}
		return sprites;
	}

	private NamtblSpritesExtractor() {
	}
}

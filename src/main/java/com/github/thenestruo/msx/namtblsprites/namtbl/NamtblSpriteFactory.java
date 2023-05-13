package com.github.thenestruo.msx.namtblsprites.namtbl;

import java.util.List;

import com.github.thenestruo.msx.namtblsprites.model.Char;

public interface NamtblSpriteFactory<S extends NamtblSprite> {

	/**
	 * Constructor
	 * @param spriteId the literal that identifies this particular sprite
	 * @param pChars the chars that compose the NAMTBL sprite
	 * @param pWidth the width of the sprites
	 * @param pHeight the height of the sprites
	 */
	S instance(final String spriteId,
			final List<Char> pChars, final int pWidth, final int pHeight);
}

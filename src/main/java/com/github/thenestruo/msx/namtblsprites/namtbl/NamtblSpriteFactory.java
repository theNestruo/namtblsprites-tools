package com.github.thenestruo.msx.namtblsprites.namtbl;

import java.util.List;

import com.github.thenestruo.msx.namtblsprites.model.Char;

public interface NamtblSpriteFactory<S extends NamtblSprite> {

	/**
	 * Instance constructor
	 * @param spriteId the literal that identifies this particular sprite
	 * @param chars the chars that compose the NAMTBL sprite
	 * @param width the width of the sprites
	 * @param height the height of the sprites
	 */
	S instance(final String spriteId,
			final List<Char> chars, final int width, final int height);
}

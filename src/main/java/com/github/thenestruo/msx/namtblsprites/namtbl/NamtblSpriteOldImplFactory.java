package com.github.thenestruo.msx.namtblsprites.namtbl;

import java.util.List;

import com.github.thenestruo.msx.namtblsprites.model.Char;

/**
 * @deprecated Use the NamtblSpriteImpl-based implementation instead
 */
@Deprecated
public class NamtblSpriteOldImplFactory implements NamtblSpriteFactory<NamtblSpriteOldImpl> {

	private final NamtblSpriteAlignment alignment;

	public NamtblSpriteOldImplFactory(final NamtblSpriteAlignment alignment) {
		super();

		this.alignment = alignment;
	}

	@Override
	public NamtblSpriteOldImpl instance(final String spriteId, final List<Char> chars, final int width, final int height) {
		return new NamtblSpriteOldImpl(spriteId, chars, width, height, this.alignment);
	}
}

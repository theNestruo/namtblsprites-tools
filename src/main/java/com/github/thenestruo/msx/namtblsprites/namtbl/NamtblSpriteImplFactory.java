package com.github.thenestruo.msx.namtblsprites.namtbl;

import java.util.List;

import com.github.thenestruo.msx.namtblsprites.model.Char;

public class NamtblSpriteImplFactory implements NamtblSpriteFactory<NamtblSpriteImpl> {

	private final NamtblSpriteAlignment alignment;

	public NamtblSpriteImplFactory(final NamtblSpriteAlignment alignment) {
		super();

		this.alignment = alignment;
	}

	@Override
	public NamtblSpriteImpl instance(final String spriteId, final List<Char> chars, final int width, final int height) {
		return new NamtblSpriteImpl(spriteId, chars, width, height, this.alignment);
	}
}

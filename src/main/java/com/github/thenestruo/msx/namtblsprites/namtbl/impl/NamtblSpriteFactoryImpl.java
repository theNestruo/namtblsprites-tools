package com.github.thenestruo.msx.namtblsprites.namtbl.impl;

import java.util.List;

import com.github.thenestruo.msx.namtblsprites.model.Char;
import com.github.thenestruo.msx.namtblsprites.namtbl.NamtblSpriteAlignment;
import com.github.thenestruo.msx.namtblsprites.namtbl.NamtblSpriteFactory;

public class NamtblSpriteFactoryImpl implements NamtblSpriteFactory<NamtblSpriteImpl> {

	private final NamtblSpriteAlignment alignment;

	public NamtblSpriteFactoryImpl(final NamtblSpriteAlignment alignment) {
		super();

		this.alignment = alignment;
	}

	@Override
	public NamtblSpriteImpl instance(final String spriteId, final List<Char> chars, final int width, final int height) {
		return new NamtblSpriteImpl(spriteId, chars, width, height, this.alignment);
	}
}

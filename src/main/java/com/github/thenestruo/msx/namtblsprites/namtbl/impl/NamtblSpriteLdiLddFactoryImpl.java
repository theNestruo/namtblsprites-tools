package com.github.thenestruo.msx.namtblsprites.namtbl.impl;

import java.util.List;

import com.github.thenestruo.msx.namtblsprites.model.Char;
import com.github.thenestruo.msx.namtblsprites.namtbl.NamtblSpriteAlignment;
import com.github.thenestruo.msx.namtblsprites.namtbl.NamtblSpriteFactory;

/**
 * @deprecated Use the NamtblSpriteImpl-based implementation instead
 */
@Deprecated
public class NamtblSpriteLdiLddFactoryImpl implements NamtblSpriteFactory<NamtblSpriteLdiLddImpl> {

	private final NamtblSpriteAlignment alignment;

	public NamtblSpriteLdiLddFactoryImpl(final NamtblSpriteAlignment alignment) {
		super();

		this.alignment = alignment;
	}

	@Override
	public NamtblSpriteLdiLddImpl instance(final String spriteId, final List<Char> chars, final int width, final int height) {
		return new NamtblSpriteLdiLddImpl(spriteId, chars, width, height, this.alignment);
	}
}

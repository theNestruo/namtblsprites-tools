package com.github.thenestruo.msx.namtblsprites.namtbl;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.thenestruo.msx.namtblsprites.model.RawData;
import com.github.thenestruo.msx.namtblsprites.namtbl.impl.NamtblSpriteImpl;
import com.github.thenestruo.msx.namtblsprites.namtbl.impl.NamtblSpriteFactoryImpl;
import com.github.thenestruo.msx.namtblsprites.namtbl.impl.NamtblSpriteLdiLddImpl;
import com.github.thenestruo.msx.namtblsprites.namtbl.impl.NamtblSpriteLdiLddFactoryImpl;
import com.github.thenestruo.msx.namtblsprites.tmx.TmxReader;
import com.github.thenestruo.util.ClassPathResource;

public class NamtblSpritesExtractorTest {

	@Test
	public void testExample() throws IOException {

		this.asserts(this.extractor(this.oldFactory(NamtblSpriteAlignment.DEFAULT)).extractFrom(3, 3));
	}

	@Test
	public void testExampleLeft() throws IOException {

		this.asserts(this.extractor(this.oldFactory(NamtblSpriteAlignment.LEFT)).extractFrom(3, 3));
	}

	@Test
	public void testExampleAligned() throws IOException {

		this.asserts(this.extractor(this.oldFactory(NamtblSpriteAlignment.ALIGNED)).extractFrom(3, 3));
	}

	@Test
	public void testExampleRight() throws IOException {

		this.asserts(this.extractor(this.oldFactory(NamtblSpriteAlignment.RIGHT)).extractFrom(3, 3));
	}

	@Test
	public void testExampleNoLdi() throws IOException {

		this.asserts(this.extractor(this.factory(NamtblSpriteAlignment.DEFAULT)).extractFrom(3, 3));
	}

	@Test
	public void testExampleLeftNoLdi() throws IOException {

		this.asserts(this.extractor(this.factory(NamtblSpriteAlignment.LEFT)).extractFrom(3, 3));
	}

	@Test
	public void testExampleAlignedNoLdi() throws IOException {

		this.asserts(this.extractor(this.factory(NamtblSpriteAlignment.ALIGNED)).extractFrom(3, 3));
	}

	@Test
	public void testExampleRightNoLdi() throws IOException {

		this.asserts(this.extractor(this.factory(NamtblSpriteAlignment.RIGHT)).extractFrom(3, 3));
	}

	@SuppressWarnings("deprecation")
	private NamtblSpriteFactory<NamtblSpriteLdiLddImpl> oldFactory(
			final NamtblSpriteAlignment alignment) {

		return new NamtblSpriteLdiLddFactoryImpl(alignment);
	}

	private NamtblSpriteFactory<NamtblSpriteImpl> factory(
			final NamtblSpriteAlignment alignment) {

		return new NamtblSpriteFactoryImpl(alignment);
	}

	private <S extends NamtblSprite> NamtblSpritesExtractor<S> extractor(
			final NamtblSpriteFactory<S> factory) throws IOException {

		final RawData rawData = new TmxReader(ClassPathResource.from("example.tmx")).read();
		return new NamtblSpritesExtractor<>(
				factory, rawData, (short) 64, (short) 0, "EXAMPLE");
	}

	private <S extends NamtblSprite> void asserts(final List<S> sprites) {

		Assertions.assertNotNull(sprites);
		Assertions.assertEquals(5, sprites.size());
		for (final NamtblSprite sprite : sprites) {
			final List<String> asm = sprite.asAsm();
			Assertions.assertNotNull(asm);
			Assertions.assertFalse(asm.isEmpty());
			// System.out.println(asm);
		}
	}
}

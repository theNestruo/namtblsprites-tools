package com.github.thenestruo.msx.namtblsprites.namtbl;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.thenestruo.msx.namtblsprites.model.RawData;
import com.github.thenestruo.msx.namtblsprites.tmx.TmxReader;
import com.github.thenestruo.util.ClassPathResource;

public class NamtblSpritesExtractorTest {

	@Test
	public void testExample() throws IOException {

		this.asserts(this.extractor(this.defaultFactory(NamtblSpriteAlignment.DEFAULT)).extractFrom(3, 3));
	}

	@Test
	public void testExampleLeft() throws IOException {

		this.asserts(this.extractor(this.defaultFactory(NamtblSpriteAlignment.LEFT)).extractFrom(3, 3));
	}

	@Test
	public void testExampleAligned() throws IOException {

		this.asserts(this.extractor(this.defaultFactory(NamtblSpriteAlignment.ALIGNED)).extractFrom(3, 3));
	}

	@Test
	public void testExampleRight() throws IOException {

		this.asserts(this.extractor(this.defaultFactory(NamtblSpriteAlignment.RIGHT)).extractFrom(3, 3));
	}

	@Test
	public void testExampleNoLdi() throws IOException {

		this.asserts(this.extractor(this.noLdiFactory(NamtblSpriteAlignment.DEFAULT)).extractFrom(3, 3));
	}

	@Test
	public void testExampleLeftNoLdi() throws IOException {

		this.asserts(this.extractor(this.noLdiFactory(NamtblSpriteAlignment.LEFT)).extractFrom(3, 3));
	}

	@Test
	public void testExampleAlignedNoLdi() throws IOException {

		this.asserts(this.extractor(this.noLdiFactory(NamtblSpriteAlignment.ALIGNED)).extractFrom(3, 3));
	}

	@Test
	public void testExampleRightNoLdi() throws IOException {

		this.asserts(this.extractor(this.noLdiFactory(NamtblSpriteAlignment.RIGHT)).extractFrom(3, 3));
	}

	private <S extends NamtblSprite> NamtblSpritesExtractor<S> extractor(
			final NamtblSpriteFactory<S> factory) throws IOException {

		final RawData rawData = new TmxReader(new ClassPathResource("example.tmx")).read();
		return new NamtblSpritesExtractor<>(
				factory, rawData, (short) 64, (short) 0, "EXAMPLE");
	}

	private NamtblSpriteFactory<NamtblSpriteDefaultImpl> defaultFactory(
			final NamtblSpriteAlignment alignment) {
		return new NamtblSpriteDefaultImpl.Factory(alignment);
	}

	private NamtblSpriteFactory<NamtblSpriteNoLdiImpl> noLdiFactory(
			final NamtblSpriteAlignment alignment) {
		return new NamtblSpriteNoLdiImpl.Factory(alignment);
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

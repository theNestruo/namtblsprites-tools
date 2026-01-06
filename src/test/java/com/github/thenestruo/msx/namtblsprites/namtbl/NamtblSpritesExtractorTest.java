package com.github.thenestruo.msx.namtblsprites.namtbl;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.thenestruo.commons.io.ClassPathResource;
import com.github.thenestruo.msx.namtblsprites.model.RawData;
import com.github.thenestruo.msx.namtblsprites.model.Size;
import com.github.thenestruo.msx.namtblsprites.tmx.TmxReader;

public class NamtblSpritesExtractorTest {

	@Test
	public void testExample() throws IOException {

		this.asserts(this.extractFromExample(NamtblSpriteAlignment.DEFAULT));
	}

	@Test
	public void testExampleLeft() throws IOException {

		this.asserts(this.extractFromExample(NamtblSpriteAlignment.LEFT));
	}

	@Test
	public void testExampleAligned() throws IOException {

		this.asserts(this.extractFromExample(NamtblSpriteAlignment.ALIGNED));
	}

	@Test
	public void testExampleRight() throws IOException {

		this.asserts(this.extractFromExample(NamtblSpriteAlignment.RIGHT));
	}

	private List<NamtblSprite> extractFromExample(
			final NamtblSpriteAlignment alignment) throws IOException {

		final RawData rawData = new TmxReader(ClassPathResource.from("example.tmx")).read();
		return NamtblSpritesExtractor.extract(
				rawData, (short) 64, (short) 0, "EXAMPLE", new Size(3, 3), alignment, null);
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

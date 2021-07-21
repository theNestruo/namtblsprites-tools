package com.github.thenestruo.msx.namtblsprites.namtbl;

import java.io.IOException;
import java.util.List;

import com.github.thenestruo.msx.namtblsprites.model.RawData;
import com.github.thenestruo.msx.namtblsprites.tmx.TmxReader;
import com.github.thenestruo.util.ClassPathResource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class NamtblSpriteFactoryTest {

	@Test
	public void testExample() throws IOException {

		this.asserts(this.factory().create(3, 3, true));
	}

	@Test
	public void testExampleCenterOff() throws IOException {

		this.asserts(this.factory().create(3, 3, false));
	}
	private NamtblSpriteFactory factory() throws IOException {

		final RawData rawData = new TmxReader(new ClassPathResource("example.tmx")).read();
		return new NamtblSpriteFactory(rawData, (short) 64, (short) 0, "EXAMPLE");
	}

	private void asserts(final List<NamtblSprite> sprites) {

		Assert.assertNotNull(sprites);
		Assert.assertEquals(5, sprites.size());
		for (NamtblSprite sprite : sprites) {
			List<String> asm = sprite.asAsm();
			Assert.assertNotNull(asm);
			Assert.assertFalse(asm.isEmpty());
			// System.out.println(asm);
		}
	}
}

package com.github.thenestruo.msx.namtblsprites.tmx;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.thenestruo.msx.namtblsprites.model.RawData;
import com.github.thenestruo.util.ClassPathResource;

class TmxReaderTest {

	@Test
	void doTest() throws IOException {

		final RawData rawData = new TmxReader(new ClassPathResource("example.tmx")).read();
		Assertions.assertNotNull(rawData);
		Assertions.assertNotNull(rawData.getData());
		Assertions.assertEquals(3, rawData.getWidth());
		Assertions.assertEquals(15, rawData.getHeight());
	}
}

package com.github.thenestruo.msx.namtblsprites.tmx;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.thenestruo.commons.io.ClassPathResource;
import com.github.thenestruo.msx.namtblsprites.model.RawData;

class TmxReaderTest {

	@Test
	void doTest() throws IOException {

		final RawData rawData = new TmxReader(ClassPathResource.from("example.tmx")).read();
		Assertions.assertNotNull(rawData);
		Assertions.assertNotNull(rawData.getData());
		Assertions.assertNotNull(rawData.getSize());
		Assertions.assertEquals(3, rawData.getSize().getWidth());
		Assertions.assertEquals(15, rawData.getSize().getHeight());
	}
}

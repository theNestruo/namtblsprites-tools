package com.github.thenestruo.msx.namtblsprites.tmx;

import java.io.IOException;

import com.github.thenestruo.msx.namtblsprites.model.RawData;
import com.github.thenestruo.util.ClassPathResource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TmxReaderTest {

	@Test
	public void doTest() throws IOException {

		RawData rawData = new TmxReader(new ClassPathResource("example.tmx")).read();
		Assert.assertNotNull(rawData);
		Assert.assertNotNull(rawData.getData());
		Assert.assertEquals(3, rawData.getWidth());
		Assert.assertEquals(15, rawData.getHeight());
	}
}

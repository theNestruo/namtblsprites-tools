package com.github.thenestruo.msx.namtblsprites.tmx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.thenestruo.msx.namtblsprites.model.RawData;
import com.github.thenestruo.util.ReadableResource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

/**
 * Reads a Tiled {@code .tmx} file
 */
public class TmxReader {

	private final ReadableResource source;

	public TmxReader(final ReadableResource source) {
		super();

		this.source = Validate.notNull(source, "The source must not be null");
	}

	public RawData read() throws IOException {

		try (final InputStream inputStream = IOUtils.buffer(this.source.getInputStream())) {

			final Tmx tmx = new XmlMapper().readValue(inputStream, Tmx.class);

			// final int tileCount = tmx.getTileset().getTileCount();

			final Tmx.Layer layer = tmx.getLayer();
			final int width = layer.getWidth();
			final int height = layer.getHeight();

			final List<Short> data = Arrays.stream(layer.getData().getCsv().split("\\s*,\\s*"))
					.map(s -> Short.parseShort(s.strip()))
					.collect(Collectors.toList());

			return new RawData(data, width, height);
		}
	}
}

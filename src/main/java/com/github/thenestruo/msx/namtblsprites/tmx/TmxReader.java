package com.github.thenestruo.msx.namtblsprites.tmx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.github.thenestruo.commons.io.ReadableResource;
import com.github.thenestruo.msx.namtblsprites.model.RawData;
import com.github.thenestruo.msx.namtblsprites.model.Size;

import tools.jackson.dataformat.xml.XmlMapper;

/**
 * Reads a Tiled {@code .tmx} file
 */
public class TmxReader {

	private final ReadableResource source;

	public TmxReader(final ReadableResource source) {
		this.source = Objects.requireNonNull(source, "The source must not be null");
	}

	public RawData read() throws IOException {

		try (final InputStream inputStream = this.source.getBufferedInputStream()) {

			final Tmx tmx = new XmlMapper().readValue(inputStream, Tmx.class);

			// final int tileCount = tmx.getTileset().getTileCount();

			final Tmx.TmxLayer layer = tmx.layer();

			final List<Integer> layerData = Arrays.stream(layer.data().csv().split("\\s*,\\s*"))
					.map(s -> Integer.parseInt(s.strip()))
					.toList();

			final Size layerSize = new Size(layer.width(), layer.height());

			return new RawData(layerData, layerSize);
		}
	}
}

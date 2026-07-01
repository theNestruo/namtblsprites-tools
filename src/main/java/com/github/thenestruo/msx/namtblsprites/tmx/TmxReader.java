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

	public static TmxReader from(final ReadableResource source) {
		Objects.requireNonNull(source, "The source must not be null");
		return new TmxReader(source);
	}

	private final ReadableResource source;

	private TmxReader(final ReadableResource source) {
		this.source = source;
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

			return RawData.of(layerData, layerSize);
		}
	}
}

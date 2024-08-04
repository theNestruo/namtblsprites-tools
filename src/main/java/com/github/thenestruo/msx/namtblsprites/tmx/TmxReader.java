package com.github.thenestruo.msx.namtblsprites.tmx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.thenestruo.msx.namtblsprites.model.RawData;
import com.github.thenestruo.msx.namtblsprites.model.Size;
import com.github.thenestruo.util.ReadableResource;

/**
 * Reads a Tiled {@code .tmx} file
 */
public class TmxReader {

	private final ReadableResource source;

	public TmxReader(final ReadableResource source) {
		super();

		this.source = Objects.requireNonNull(source, "The source must not be null");
	}

	public RawData read() throws IOException {

		try (final InputStream inputStream = IOUtils.buffer(this.source.getInputStream())) {

			final Tmx tmx = new XmlMapper().readValue(inputStream, Tmx.class);

			// final int tileCount = tmx.getTileset().getTileCount();

			final Tmx.TmxLayer layer = tmx.getLayer();

			final List<Short> layerData = Arrays.stream(layer.getData().getCsv().split("\\s*,\\s*"))
					.map(s -> Short.parseShort(s.strip()))
					.collect(Collectors.toList());
			
			final Size layerSize = new Size(layer.getWidth(), layer.getHeight());

			return new RawData(layerData, layerSize);
		}
	}
}

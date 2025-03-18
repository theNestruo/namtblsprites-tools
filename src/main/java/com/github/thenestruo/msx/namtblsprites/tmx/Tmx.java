package com.github.thenestruo.msx.namtblsprites.tmx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "map")
class Tmx {

	@JsonProperty("tileset")
	private TmxTileset tileset;

	@JsonProperty("layer")
	private TmxLayer layer;

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TmxTileset {

		@JsonProperty("tilecount")
		private int tileCount;

		public int getTileCount() {
			return this.tileCount;
		}

		public void setTileCount(final int tileCount) {
			this.tileCount = tileCount;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TmxLayer {

		private int width;

		private int height;

		@JsonProperty("data")
		private TmxData data;

		/**
		 * Default constructor
		 */
		public TmxLayer() {
			super();
		}

		/**
		 * Copy constructor
		 * @param that TmxData
		 */
		private TmxLayer(final TmxLayer that) {
			this();

			this.width = that.width;
			this.height = that.height;
			this.data = new TmxData(that.data);
		}

		@JsonIgnoreProperties(ignoreUnknown = true)
		public static class TmxData {

			@JacksonXmlText
			private String csv;

			/**
			 * Default constructor
			 */
			public TmxData() {
				super();
			}

			/**
			 * Copy constructor
			 * @param that TmxData
			 */
			private TmxData(final TmxData that) {
				this();
				this.csv = that.csv;
			}

			public String getCsv() {
				return this.csv;
			}

			public void setCsv(final String csv) {
				this.csv = csv;
			}
		}

		public int getWidth() {
			return this.width;
		}

		public void setWidth(final int width) {
			this.width = width;
		}

		public int getHeight() {
			return this.height;
		}

		public void setHeight(final int height) {
			this.height = height;
		}

		public TmxData getData() {
			return this.data != null ? new TmxData(this.data) : null;
		}

		public void setData(final TmxData data) {
			this.data = data != null ? new TmxData(data) : null;
		}
	}

	public TmxTileset getTileset() {
		return this.tileset;
	}

	public void setTileset(final TmxTileset tileset) {
		this.tileset = tileset;
	}

	public TmxLayer getLayer() {
		return this.layer != null ? new TmxLayer(this.layer) : null;
	}

	public void setLayer(final TmxLayer layer) {
		this.layer = layer != null ? new TmxLayer(layer) : null;
	}
}

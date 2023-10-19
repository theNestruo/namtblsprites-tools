package com.github.thenestruo.msx.namtblsprites.tmx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import jakarta.xml.bind.annotation.XmlAttribute;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "map")
class Tmx {

	@XmlAttribute(name = "tileset")
	private TmxTileset tileset;

	@XmlAttribute(name = "layer")
	private TmxLayer layer;

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TmxTileset {

		@XmlAttribute(name = "tilecount")
		private int tileCount;

		public int getTileCount() {
			return tileCount;
		}

		public void setTileCount(int tileCount) {
			this.tileCount = tileCount;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TmxLayer {

		private int width;

		private int height;

		@XmlAttribute(name = "data")
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
		private TmxLayer(TmxLayer that) {
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
			private TmxData(TmxData that) {
				this();
				this.csv = that.csv;
			}

			public String getCsv() {
				return csv;
			}

			public void setCsv(String csv) {
				this.csv = csv;
			}
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public TmxData getData() {
			return data != null ? new TmxData(data) : null;
		}

		public void setData(TmxData data) {
			this.data = data != null ? new TmxData(data) : null;
		}
	}

	public TmxTileset getTileset() {
		return tileset;
	}

	public void setTileset(TmxTileset tileset) {
		this.tileset = tileset;
	}

	public TmxLayer getLayer() {
		return layer != null ? new TmxLayer(layer) : null;
	}

	public void setLayer(TmxLayer layer) {
		this.layer = layer != null ? new TmxLayer(layer) : null;
	}
}

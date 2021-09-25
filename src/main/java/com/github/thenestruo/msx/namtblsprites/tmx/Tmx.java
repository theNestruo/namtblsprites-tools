package com.github.thenestruo.msx.namtblsprites.tmx;

import javax.xml.bind.annotation.XmlAttribute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "map")
class Tmx {

	@XmlAttribute(name = "tileset")
	private Tileset tileset;

	@XmlAttribute(name = "layer")
	private Layer layer;

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Tileset {

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
	public static class Layer {

		private int width;

		private int height;

		@XmlAttribute(name = "data")
		private Data data;

		/**
		 * Default constructor
		 */
		public Layer() {
			super();
		}

		/**
		 * Copy constructor
		 * @param that Data
		 */
		private Layer(Layer that) {
			this();

			this.width = that.width;
			this.height = that.height;
			this.data = new Data(that.data);
		}

		@JsonIgnoreProperties(ignoreUnknown = true)
		public static class Data {

			@JacksonXmlText
			private String csv;

			/**
			 * Default constructor
			 */
			public Data() {
				super();
			}

			/**
			 * Copy constructor
			 * @param that Data
			 */
			private Data(Data that) {
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

		public Data getData() {
			return data != null ? new Data(data) : null;
		}

		public void setData(Data data) {
			this.data = data != null ? new Data(data) : null;
		}
	}

	public Tileset getTileset() {
		return tileset;
	}

	public void setTileset(Tileset tileset) {
		this.tileset = tileset;
	}

	public Layer getLayer() {
		return layer != null ? new Layer(layer) : null;
	}

	public void setLayer(Layer layer) {
		this.layer = layer != null ? new Layer(layer) : null;
	}
}

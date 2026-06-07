package com.github.thenestruo.msx.namtblsprites.tmx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import tools.jackson.dataformat.xml.annotation.JacksonXmlText;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName("map")
record Tmx(
		@JsonProperty("tileset") TmxTileset tileset,
		@JsonProperty("layer") TmxLayer layer) {

	@JsonIgnoreProperties(ignoreUnknown = true)
	record TmxTileset(
			@JsonProperty("tilecount") int tileCount) {
		//
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record TmxLayer(
			@JsonProperty("width") int width,
			@JsonProperty("height") int height,
			@JsonProperty("data") TmxData data) {
		//
	}

	// (class instead of record because: https://github.com/FasterXML/jackson-dataformat-xml/issues/735)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TmxData {

		@JacksonXmlText
		private String csv;

		public void setCsv(String csv) {
			this.csv = csv;
		}

		public String csv() {
			return this.csv;
		}
	}
}

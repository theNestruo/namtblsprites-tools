package com.github.thenestruo.msx.namtblsprites;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.tinylog.Logger;
import org.tinylog.configuration.Configuration;

import com.github.thenestruo.commons.io.FileSystemResource;
import com.github.thenestruo.commons.io.Paths;
import com.github.thenestruo.msx.namtblsprites.model.RawData;
import com.github.thenestruo.msx.namtblsprites.model.Size;
import com.github.thenestruo.msx.namtblsprites.namtbl.NamtblSprite;
import com.github.thenestruo.msx.namtblsprites.namtbl.NamtblSpriteAlignment;
import com.github.thenestruo.msx.namtblsprites.namtbl.NamtblSpritesExtractor;
import com.github.thenestruo.msx.namtblsprites.tmx.TmxReader;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "tmx2namtblsprites", sortOptions = false)
public class Tmx2NamtblSpritesApp implements Callable<Integer> {

	public static void main(final String... args) {
		new CommandLine(new Tmx2NamtblSpritesApp()).execute(args);
	}

	@Option(names = { "-h", "--help" }, usageHelp = true, description = "shows usage")
	private boolean help;

	@Option(names = { "-v", "--verbose" }, description = "verbose execution")
	private boolean verbose;

	@Parameters(index = "0", arity = "1", paramLabel = "input", description = "Tiled TMX input file")
	private Path inputPath;

	@Parameters(index = "1",
			arity = "0..1",
			paramLabel = "output",
			description = "ASM output file (optional, defaults to <input>.asm)")
	private Path outputPath;

	@Option(names = { "-width" }, required = true, description = "Sprites width (in characters) (required)")
	private int spriteWidth;

	@Option(names = { "-height" }, required = true, description = "Sprites height (in characters) (required)")
	private int spriteHeight;

	@Option(names = { "-add" }, description = "The addend for the values")
	private int addend;

	@Option(names = { "-blank" }, description = "The blank value to be ignored")
	private int blankValue;

	@Option(names = { "-name" }, description = "An identifying name for the rendering routines")
	private String spriteName;

	@Option(names = { "-return" }, description = "Return instruction")
	private String returnInstruction;

	@ArgGroup
	private Alignment alignment;

	static class Alignment {

		@Option(names = { "--left" }, description = "Align left, draw to right")
		private boolean isLeft;

		@Option(names = { "--right" }, description = "Align right, draw to left")
		private boolean isRight;

		@Option(names = { "--align" }, description = "Align center, draw to right (alt. entry points for even widths)")
		private boolean isAlign;
	}

	@Override
	public Integer call() throws IOException {

		// (before using tinylog)
		this.handleVerbose();

		// Reads the Tiled TMX file
		final RawData rawData = this.readTmx();
		if (rawData == null) {
			return 10;
		}
		Logger.debug("Tiled TMX file read: {}", rawData.getSize());

		// Builds the NAMTBL sprites
		final List<? extends NamtblSprite> namtblSprites = this.toNamtblSprites(rawData);
		Logger.debug("{} NAMTBL sprites read", namtblSprites.size());

		// Writes the ASM file
		Logger.debug("{} NAMTBL sprites will be written to ASM file {}", namtblSprites.size(), this.outputPath());
		this.writeAsmFile(namtblSprites);
		Logger.debug("ASM file {} written", this.outputPath());

		return 0;
	}

	private void handleVerbose() {

		if (this.verbose) {
			Configuration.set("writer.level", "debug");
		}
	}

	private RawData readTmx() throws IOException {

		if (!Files.exists(this.inputPath)) {
			Logger.warn("Tiled TMX input file {} does not exist", this.inputPath);
			return null;
		}

		Logger.debug("TmxData will be read from Tiled TMX input file {}", this.inputPath);
		return new TmxReader(new FileSystemResource(this.inputPath)).read();
	}

	private List<? extends NamtblSprite> toNamtblSprites(final RawData rawData) {

		final Size spriteSize = new Size(this.spriteWidth, this.spriteHeight);

		final NamtblSpriteAlignment alignment = this.alignment.isLeft ? NamtblSpriteAlignment.LEFT
				: this.alignment.isRight ? NamtblSpriteAlignment.RIGHT
				: this.alignment.isAlign ? NamtblSpriteAlignment.ALIGNED
				: NamtblSpriteAlignment.DEFAULT;

		return NamtblSpritesExtractor.extract(rawData,
				this.blankValue, this.addend, this.spriteName, spriteSize, alignment, this.returnInstruction);
	}

	private Path outputPath() {

		if (this.outputPath != null) {
			return this.outputPath;
		}

		return Paths.append(this.inputPath, ".asm");
	}

	private void writeAsmFile(final List<? extends NamtblSprite> sprites) throws IOException {

		if (Files.exists(this.outputPath())) {
			Logger.warn("ASM output file {} already exists", this.outputPath());
		}

		final List<String> lines = new ArrayList<>();
		for (final NamtblSprite sprite : sprites) {
			lines.addAll(sprite.asAsm());
		}

		Files.write(this.outputPath(), lines, Charset.defaultCharset());
	}
}

package com.github.thenestruo.msx.namtblsprites;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thenestruo.msx.namtblsprites.model.RawData;
import com.github.thenestruo.msx.namtblsprites.namtbl.NamtblSprite;
import com.github.thenestruo.msx.namtblsprites.namtbl.NamtblSpriteAlignment;
import com.github.thenestruo.msx.namtblsprites.namtbl.NamtblSpriteFactory;
import com.github.thenestruo.msx.namtblsprites.namtbl.NamtblSpritesExtractor;
import com.github.thenestruo.msx.namtblsprites.namtbl.impl.NamtblSpriteFactoryImpl;
import com.github.thenestruo.msx.namtblsprites.tmx.TmxReader;
import com.github.thenestruo.util.FileSystemResource;

import ch.qos.logback.classic.Level;

public class Tmx2NamtblSpritesApp {

	private static final String HELP = "help";
	private static final String VERBOSE = "verbose";
	private static final String WIDTH = "width";
	private static final String HEIGHT = "height";
	private static final String ADD = "add";
	private static final String BLANK = "blank";
	private static final String NAME = "name";
	private static final String LEFT = "left";
	private static final String RIGHT = "right";
	private static final String ALIGN = "align";

	private static final Logger logger = LoggerFactory.getLogger(Tmx2NamtblSpritesApp.class);

	public static void main(final String[] args) throws ParseException, IOException {

		// Parses the command line
		final Options options = options();
		final CommandLine command;
		try {
			command = new DefaultParser().parse(options, args);
		} catch (final MissingOptionException e) {
			showUsage(options);
			return;
		}

		// Main options
		if (showUsage(command, options)) {
			return;
		}
		setVerbose(command);

		// Reads the Tiled TMX file
		final Pair<String, RawData> pair = readTmx(command);
		final String inputFilePath = pair.getLeft();
		final RawData rawData = pair.getRight();
		if (rawData == null) {
			return;
		}
		logger.debug("Tiled TMX file read: {}x{}", rawData.getWidth(), rawData.getHeight());

		// Builds the NAMTBL sprites
		final List<? extends NamtblSprite> namtblSprites = toNamtblSprites(inputFilePath, rawData, command);
		logger.debug("{} NAMTBL sprites read", namtblSprites.size());

		// Writes the ASM file
		final String asmFilePath = nextPath(command, inputFilePath + ".asm");
		logger.debug("{} NAMTBL sprites will be written to ASM file {}", namtblSprites.size(), asmFilePath);
		writeAsmFile(asmFilePath, namtblSprites, command);
		logger.debug("ASM file {} written", asmFilePath);
	}

	private static Options options() {

		final Options options = new Options();
		options.addOption(HELP, "Shows usage");
		options.addOption(VERBOSE, "Verbose execution");
		options.addOption(WIDTH, true, "Sprites width (in characters) (required)");
		options.addOption(HEIGHT, true, "Sprites height (in characters) (required)");
		options.addOption(ADD, true, "The addend for the values");
		options.addOption(BLANK, true, "The blank value to be ignored");
		options.addOption(NAME, true, "An identifying name for the rendering routines");
		options.addOption(LEFT, "Align left, draw to right");
		options.addOption(RIGHT, "Align right, draw to left");
		options.addOption(ALIGN, "Align center, draw to right (alt. entry points for even widths)");
		return options;
	}

	private static boolean showUsage(final CommandLine command, final Options options) {

		return command.hasOption(HELP)
				? showUsage(options)
				: false;
	}

	private static boolean showUsage(final Options options) {

		// (prints in proper order)
		final HelpFormatter helpFormatter = new HelpFormatter();
		final PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.ISO_8859_1));
		helpFormatter.printUsage(pw, 114, "java -jar tmx2NamtblSprites.jar");
		for (final Option option : options.getOptions()) {
			helpFormatter.printOptions(pw, 114, new Options().addOption(option), 2, 4);
		}
		helpFormatter.printWrapped(pw, 114, "  <input>    Tiled TMX input file");
		helpFormatter.printWrapped(pw, 114, "  <output>    ASM output file (optional, defaults to <input>.asm)");
		pw.flush();

		return true;
	}

	private static boolean setVerbose(final CommandLine command) {

		if (!command.hasOption(VERBOSE)) {
			return false;
		}

		final Logger rootLogger = LoggerFactory.getILoggerFactory().getLogger(Logger.ROOT_LOGGER_NAME);
		ch.qos.logback.classic.Logger.class.cast(rootLogger).setLevel(Level.DEBUG);

		return true;
	}

	private static Pair<String, RawData> readTmx(final CommandLine command) throws IOException {

		final String path = nextPath(command, null);
		if (path == null) {
			return Pair.of(null, null);
		}
		final File file = new File(path);
		if (!file.exists()) {
			logger.warn("Tiled TMX input file {} does not exist", file.getAbsolutePath());
			return Pair.of(path, null);
		}

		logger.debug("TmxData will be read from Tiled TMX input file {}", file.getAbsolutePath());
		final RawData rawData = new TmxReader(new FileSystemResource(file)).read();
		return Pair.of(path, rawData);
	}

	private static List<? extends NamtblSprite> toNamtblSprites(
			final String path, final RawData rawData, final CommandLine command) throws ParseException {

		final short blankValue = (short) Integer.parseUnsignedInt(command.getOptionValue(BLANK, "0"));
		final short addend = (short) Integer.parseUnsignedInt(command.getOptionValue(ADD, "0"));
		final String spriteName = command.getOptionValue(NAME, StringUtils.upperCase(FilenameUtils.getBaseName(path)));

		final NamtblSpriteAlignment alignment =
				command.hasOption(LEFT) ? NamtblSpriteAlignment.LEFT
				: command.hasOption(RIGHT) ? NamtblSpriteAlignment.RIGHT
				: command.hasOption(ALIGN) ? NamtblSpriteAlignment.ALIGNED
				: NamtblSpriteAlignment.DEFAULT;

		final NamtblSpriteFactory<?> factory =
				new NamtblSpriteFactoryImpl(alignment);

		final NamtblSpritesExtractor<?> extractor =
				new NamtblSpritesExtractor<>(factory, rawData, blankValue, addend, spriteName);

		final int spriteWidth = Integer.parseUnsignedInt(
				command.getOptionValue(WIDTH, Integer.toString(rawData.getWidth())));
		final int spriteHeight = Integer.parseUnsignedInt(
				command.getOptionValue(HEIGHT, Integer.toString(rawData.getHeight())));

		return extractor.extractFrom(spriteWidth, spriteHeight);
	}

	private static void writeAsmFile(
			final String path, final List<? extends NamtblSprite> sprites, final CommandLine command) throws IOException {

		final File file = new File(path);
		if (file.exists()) {
			logger.warn("ASM output file {} already exists", file.getAbsolutePath());
		}

		try (final Writer writer = IOUtils.buffer(new FileWriter(file, Charset.defaultCharset()))) {
			for (final NamtblSprite sprite : sprites) {
				IOUtils.writeLines(sprite.asAsm(), System.lineSeparator(), writer);
			}
		}
	}

	private static String nextPath(final CommandLine command, final String defaultValue) {

		final List<String> argList = command.getArgList();
		return argList.isEmpty() ? defaultValue : argList.remove(0);
	}
}

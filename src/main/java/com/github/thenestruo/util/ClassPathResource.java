package com.github.thenestruo.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * A readable classpath resource
 */
public class ClassPathResource implements ReadableResource {

	/**
	 * Factory method
	 * @param path the path of the classpath resource
	 * @throws IOException if the classpath resource does not exists
	 */
	public static ClassPathResource from(String path) {

		ClassPathResource instance = new ClassPathResource(path);

		// Checks existence
		try (InputStream is = instance.getInputStream()) {
			return instance;

		} catch (IOException e) {
			return ExceptionUtils.rethrow(e);
		}

	}

	private final String path;

	/**
	 * Constructor
	 * @param path the path of the classpath resource
	 * @throws IOException if the classpath resource does not exists
	 */
	private ClassPathResource(String path) {
		super();

		this.path = Validate.notBlank(path, "The path must not be null nor blank");
	}

	@Override
	public InputStream getInputStream() {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return classLoader.getResourceAsStream(path);
	}
}

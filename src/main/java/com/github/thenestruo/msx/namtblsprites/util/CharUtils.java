package com.github.thenestruo.msx.namtblsprites.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;

import com.github.thenestruo.msx.namtblsprites.model.Char;
import com.github.thenestruo.msx.namtblsprites.model.Coord;

public class CharUtils {
	
	public static List<Char> asRelativeChars(
			final List<Char> absoluteChars, final Coord startingPosition) {
		
		if (CollectionUtils.isEmpty(absoluteChars)) {
			return Collections.emptyList();
		}
		
		Coord position = Objects.requireNonNull(startingPosition);
		
		final List<Char> relativeChars = new ArrayList<>();
		for (final Char c : absoluteChars) {
			relativeChars.add(c.relativeTo(position));
			position = c;
		}
		return relativeChars;
	}
	
	private CharUtils() {
		super();
	}
}

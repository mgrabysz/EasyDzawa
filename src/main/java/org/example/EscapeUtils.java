package org.example;

import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class EscapeUtils {

	public static Map<Character, Character> SEQUENCE_MAP = Map.ofEntries(
			Map.entry('n', '\n'),
			Map.entry('t', '\t'),
			Map.entry('r', '\r'),
			Map.entry('\"', '\"'),
			Map.entry('\\', '\\')
	);
}

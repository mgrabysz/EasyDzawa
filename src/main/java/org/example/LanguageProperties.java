package org.example;

import lombok.experimental.UtilityClass;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@UtilityClass
public class LanguageProperties {

	private static final Properties properties;
	static {
		properties = new Properties();
		try {
			FileInputStream propsInput = new FileInputStream(Configuration.getLanguageConfigPath());
			properties.load(new InputStreamReader(propsInput, StandardCharsets.UTF_8));	// this aberration reads UTF-8 symbols
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String get(String key) {
		return properties.getProperty(key);
	}

}

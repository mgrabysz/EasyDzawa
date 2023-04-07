package org.example;

import lombok.Getter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

	private static final String APPLICATION_CONFIG_PATH = "src/main/resources/properties.config";
	private static final Properties applicationProperties;
	static {
		applicationProperties = new Properties();
		try {
			FileInputStream propsInput = new FileInputStream(APPLICATION_CONFIG_PATH);
			applicationProperties.load(propsInput);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Getter
	private static final String languageConfigPath = getPropertyValue("language.config.path");
	@Getter
	private static final Integer identifierMaxLength = Integer.valueOf(getPropertyValue("identifier.maxlength"));
	@Getter
	private static final Integer commentMaxLength = Integer.valueOf(getPropertyValue("comment.maxlength"));
	@Getter
	private static final Integer textMaxLength = Integer.valueOf(getPropertyValue("text.maxlength"));

	private static String getPropertyValue(String key) {
		return applicationProperties.getProperty(key);
	}

}

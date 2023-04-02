package org.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

	private static final String APPLICATION_CONFIG_PATH = "src/main/resources/properties.config";
	private static Properties applicationProperties;

	static {
		applicationProperties = new Properties();
		try {
			FileInputStream propsInput = new FileInputStream(APPLICATION_CONFIG_PATH);
			applicationProperties.load(propsInput);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getPropertyValue(String key) {
		return applicationProperties.getProperty(key);
	}

}

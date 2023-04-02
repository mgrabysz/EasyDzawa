package org.example.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

@Getter
@AllArgsConstructor
public enum TokenType {

	END_OF_FILE,

	COMMENT("//"),

	RETURN,
	FOR,
	IF,
	ELSE,
	CLASS,
	NEW,
	THIS,

	OPEN_BRACKET("{"),
	CLOSE_BRACKET("}"),
	OPEN_PARENTHESIS("("),
	CLOSE_PARENTHESIS(")"),
	SEMICOLON(";"),
	COMA(","),
	DOUBLE_QUOTE("\""),
	ASSIGN("="),
	ADD_AND_ASSIGN("+="),
	SUBTRACT_AND_ASSIGN("-="),

	EQUAL("=="),
	NOT_EQUAL("!="),
	GREATER(">"),
	LESS("<"),
	GREATER_OR_EQUAL("<="),
	LESS_OR_EQUAL(">="),

	INTEGER,
	FLOAT,
	BOOL,
	TEXT,

	IDENTIFIER;

	private String keyword;

	private static Properties properties;

	private static String LANGUAGE_CONFIG_PATH = "src/main/resources/polish.config";

	private TokenType() {
		if (this.keyword == null) {
			readFromLanguageConfig();
		}
	}

	private void readFromLanguageConfig() {
		loadLanguageConfig();
		this.keyword = (String) properties.get(this.toString());
	}

	private static void loadLanguageConfig() {
		if (properties == null) {
			properties = new Properties();
			try {
				FileInputStream propsInput = new FileInputStream(LANGUAGE_CONFIG_PATH);
				properties.load(propsInput);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}

package org.example.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Getter
@AllArgsConstructor
public enum TokenType {

	END_OF_FILE,

	COMMENT("//"),

	RETURN,
	FOR,
	IN,
	IF,
	ELSE,
	CLASS,
	THIS,

	OPEN_BRACKET("{"),
	CLOSE_BRACKET("}"),
	OPEN_PARENTHESIS("("),
	CLOSE_PARENTHESIS(")"),
	SEMICOLON(";"),
	COMA(","),
	DOT("."),
	ASSIGN("="),
	ADD_AND_ASSIGN("+="),
	SUBTRACT_AND_ASSIGN("-="),

	EQUAL("=="),
	NOT_EQUAL("!="),
	GREATER(">"),
	LESS("<"),
	GREATER_OR_EQUAL(">="),
	LESS_OR_EQUAL("<="),

	AND,
	OR,
	NOT,

	ADD("+"),
	SUBTRACT("-"),
	MULTIPLY("*"),
	DIVIDE("/"),

	INTEGER,
	FLOAT,
	BOOL,
	FALSE,
	TRUE,
	TEXT,

	IDENTIFIER;

	private String keyword;

	private static Properties properties;

	TokenType() {
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
				FileInputStream propsInput = new FileInputStream(Configuration.getLanguageConfigPath());
				properties.load(new InputStreamReader(propsInput, StandardCharsets.UTF_8));	// this aberration reads UTF-8 symbols
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}

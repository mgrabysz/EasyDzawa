package org.example.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.properties.LanguageProperties;

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

	TokenType() {
		if (this.keyword == null) {
			readFromLanguageConfig();
		}
	}

	private void readFromLanguageConfig() {
		this.keyword = LanguageProperties.get(this.toString());
	}

}

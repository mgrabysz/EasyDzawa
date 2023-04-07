package org.example.lexer;

import org.apache.commons.lang3.StringUtils;
import org.example.Configuration;
import org.example.Position;
import org.example.token.*;

import java.io.BufferedReader;
import java.io.IOException;

public class LexerImpl implements Lexer {

	private static final String UNDERSCORE = "_";
	private static final String DOT = ".";
	private static final String DOUBLE_QUOTE = "\"";

	private Token token;
	private String currentChar;
	private Position currentPosition;
	private Position tokenPosition;
	private final BufferedReader bufferedReader;

	public LexerImpl(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
		this.currentPosition = new Position(0, -1);
		this.currentChar = nextChar();
	}

	@Override
	public Token next() {
		while (isWhitespace(currentChar)) {
			currentChar = nextChar();
		}
		this.tokenPosition = currentPosition;

		if (tryBuildEOF()
			|| tryBuildNumber()
			|| tryBuildIdentifierOrKeyword()
			|| tryBuildSymbolOrComment()
			|| tryBuildText()) {
			return token;
		}
		return new TokenUndefined(tokenPosition);
	}

	private boolean tryBuildNumber() {
		if (!StringUtils.isNumeric(currentChar)) {
			return false;
		}
		int value = Integer.parseInt(currentChar);
		if (value != 0) {
			value = getDecimalPart(value);
		} else {
			nextChar();
		}

		if (StringUtils.equals(currentChar, DOT)) {
			double fraction_part = getFractionPart();
			if (StringUtils.isAlpha(currentChar)) {			// don't allow alpha symbols follow floats
				handleError();
			}
			this.token = new TokenFloat(tokenPosition, value+fraction_part);
			return true;
		}

		if (StringUtils.isAlpha(currentChar)) {				// don't allow alpha symbols follow integers
			handleError();
		}
		this.token = new TokenInteger(tokenPosition, value);
		return true;
	}

	private int getDecimalPart(int value) {
		int decimal;
		while (StringUtils.isNumeric(nextChar())) {
			decimal = Integer.parseInt(currentChar);
			if ((Integer.MAX_VALUE - decimal) / 10 > value) {
				value = value * 10 + decimal;
			} else {
				handleError();
			}
		}
		return value;
	}

	private double getFractionPart() {
		int num_of_digits = 0;
		int fraction = 0;
		while (StringUtils.isNumeric(nextChar())) {
			int decimal = Integer.parseInt(currentChar);
			if ((Integer.MAX_VALUE - decimal) / 10 > fraction) {
				fraction = fraction * 10 + decimal;
				num_of_digits++;
			} else {
				handleError();
			}
		}
		return fraction / Math.pow(10, num_of_digits);
	}

	private boolean tryBuildIdentifierOrKeyword() {
		if (!StringUtils.isAlpha(currentChar)) {
			return false;
		}
		var builder = new StringBuilder(currentChar);
		while (isIdentifierChar(nextChar())) {
			builder.append(currentChar);
			if (builder.length() > Configuration.getIdentifierMaxLength()) {
				handleError();
			}
		}
		String key = builder.toString();
		if (TokenGroups.BOOL_LITERALS.containsKey(key)) {
			TokenType tokenType = TokenGroups.BOOL_LITERALS.get(key);
			Boolean value = tokenType == TokenType.TRUE ? Boolean.TRUE : Boolean.FALSE;
			this.token = new TokenBool(tokenPosition, value);
			return true;
		}
		if (TokenGroups.KEYWORDS.containsKey(key)) {
			TokenType tokenType = TokenGroups.KEYWORDS.get(key);
			this.token = new TokenKeyword(tokenType, tokenPosition);
			return true;
		}
		this.token = new TokenIdentifier(tokenPosition, key);
		return true;
	}

	private boolean tryBuildSymbolOrComment() {
		if (!TokenGroups.SYMBOLS.containsKey(currentChar) && !currentChar.equals("!")) {
			return false;
		}
		switch (currentChar) {
			case "=" -> {
				if (StringUtils.equals(nextChar(), "=")) {
					this.token = new TokenSymbol(TokenType.EQUAL, tokenPosition);
					nextChar();
				} else {
					this.token = new TokenSymbol(TokenType.ASSIGN, tokenPosition);
				}
			}
			case "+" -> {
				if (StringUtils.equals(nextChar(), "=")) {
					this.token = new TokenSymbol(TokenType.ADD_AND_ASSIGN, tokenPosition);
					nextChar();
				} else {
					this.token = new TokenSymbol(TokenType.ADD, tokenPosition);
				}
			}
			case "-" -> {
				if (StringUtils.equals(nextChar(), "=")) {
					this.token = new TokenSymbol(TokenType.SUBTRACT_AND_ASSIGN, tokenPosition);
					nextChar();
				} else {
					this.token = new TokenSymbol(TokenType.SUBTRACT, tokenPosition);
				}
			}
			case "<" -> {
				if (StringUtils.equals(nextChar(), "=")) {
					this.token = new TokenSymbol(TokenType.LESS_OR_EQUAL, tokenPosition);
					nextChar();
				} else {
					this.token = new TokenSymbol(TokenType.LESS, tokenPosition);
				}
			}
			case ">" -> {
				if (StringUtils.equals(nextChar(), "=")) {
					this.token = new TokenSymbol(TokenType.GREATER_OR_EQUAL, tokenPosition);
					nextChar();
				} else {
					this.token = new TokenSymbol(TokenType.GREATER, tokenPosition);
				}
			}
			case "!" -> {
				if(StringUtils.equals(nextChar(), "=")) {
					this.token = new TokenSymbol(TokenType.NOT_EQUAL, tokenPosition);
					nextChar();
				} else {
					handleError();
					return false;
				}
			}
			case "/" -> {
				if (StringUtils.equals(nextChar(), "/")) {
					parseComment();
				} else {
					this.token = new TokenSymbol(TokenType.DIVIDE, tokenPosition);
				}
			}
			default -> {
				final var tokenType = TokenGroups.SYMBOLS.get(currentChar);
				this.token = new TokenSymbol(tokenType, tokenPosition);
				nextChar();
			}
		}
		return true;
	}

	private void parseComment() {
		final var builder = new StringBuilder();
		while (!StringUtils.isEmpty(nextChar())) {
			if (tryBuildLineSeparator()) {
				this.token = new TokenComment(tokenPosition, builder.toString());
				return;
			}
			if (builder.length() > Configuration.getCommentMaxLength()) {
				handleError();
				return;
			}
			builder.append(currentChar);
		}
	}

	private boolean tryBuildText() {
		if (!StringUtils.equals(currentChar, DOUBLE_QUOTE)) {
			return false;
		}
		final var builder = new StringBuilder();
		while (!StringUtils.equals(nextChar(), DOUBLE_QUOTE)) {
			if (builder.length() > Configuration.getTextMaxLength()) {
				handleError();
				return false;
			}
			if (StringUtils.isEmpty(currentChar)) {
				handleError();
				return false;
			}
			builder.append(currentChar);
		}
		this.token = new TokenText(tokenPosition, builder.toString());
		nextChar();
		return true;
	}

	private boolean tryBuildEOF() {
		if (!StringUtils.isEmpty(currentChar)) {
			return false;
		}
		this.token = new TokenEOF(tokenPosition);
		return true;
	}

	private String nextChar() {
		int character;
		try {
			character = bufferedReader.read();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.currentPosition = currentPosition.nextChar();
		if (character == -1) {
			this.currentChar = StringUtils.EMPTY;
		} else {
			this.currentChar = Character.toString(character);
		}
		return this.currentChar;
	}

	private boolean isWhitespace(String string) {
		return string.isBlank() && !string.isEmpty();
	}

	private boolean isIdentifierChar(String string) {
		return StringUtils.isAlphanumeric(string) || StringUtils.equals(string, UNDERSCORE);
	}

	private boolean tryBuildLineSeparator() {
		if (StringUtils.equals(currentChar, "\n")) {
			nextChar();
			return true;
		}
		if (StringUtils.equals(currentChar, "\r")) {
			nextChar();
			if (StringUtils.equals(currentChar, "\n")) {
				nextChar();
			}
			return true;
		}
		return false;
	}

	private void handleError()  {
		// TODO
	}
}

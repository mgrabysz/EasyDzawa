package org.example.lexer;

import org.apache.commons.lang3.StringUtils;
import org.example.Position;
import org.example.token.*;

import java.io.BufferedReader;
import java.io.IOException;

import static org.example.Configuration.getPropertyValue;

public class EasyLexerImpl implements Lexer {

	private static final String UNDERSCORE = "_";
	private static final String DOT = ".";

	private Token token;
	private String currentChar;
	private Position currentPosition;
	private Position tokenPosition;
	private final BufferedReader bufferedReader;

	public EasyLexerImpl(BufferedReader bufferedReader) {
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
			|| tryBuildIdentifierOrKeyword()) {
			return token;
		}
		return new EmptyToken();
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
			if (builder.length() > Integer.parseInt(getPropertyValue("identifier.maxlength"))) {
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

	private void handleError()  {
		// TODO
	}
}

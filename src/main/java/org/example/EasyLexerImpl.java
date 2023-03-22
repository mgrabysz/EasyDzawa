package org.example;

import org.apache.commons.lang3.StringUtils;
import org.example.token.EmptyToken;
import org.example.token.Token;
import org.example.token.TokenEOF;
import org.example.token.TokenInteger;

import java.io.BufferedReader;
import java.io.IOException;

public class EasyLexerImpl implements EasyLexer {

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
			|| tryBuildNumber()) {
			return token;
		}
		return new EmptyToken();
	}

	private boolean tryBuildNumber() {
		if (!StringUtils.isNumeric(currentChar)) {
			return false;
		}
		int value = Integer.valueOf(currentChar);
		if (value != 0) {
			while (StringUtils.isNumeric(nextChar())) {
				int decimal = Integer.valueOf(currentChar);
				if ((Integer.MAX_VALUE - decimal) / 10 > value) {
					value = value * 10 + decimal;
				} else {
					handleError();
				}
			}
		}
		this.token = new TokenInteger(tokenPosition, value);
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
		int character = 0;
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

	private void handleError()  {
		// TODO
	}
}

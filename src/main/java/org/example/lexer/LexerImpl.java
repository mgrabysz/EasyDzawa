package org.example.lexer;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.example.Configuration;
import org.example.EscapeUtils;
import org.example.Position;
import org.example.error.ErrorHandler;
import org.example.error.ErrorLexerDetails;
import org.example.error.enums.ErrorType;
import org.example.token.*;

import java.io.BufferedReader;
import java.io.IOException;

public class LexerImpl implements Lexer {

	private static final String UNDERSCORE = "_";
	private static final String DOT = ".";
	private static final String DOUBLE_QUOTE = "\"";
	private static final String BACKSLASH = "\\";
	private static final int LINE_FEED_ASCII = 10;
	private static final int CARRIAGE_RETURN_ASCII = 13;

	private Token token;
	private String currentChar;
	private Position currentPosition;
	private Position tokenPosition;
	private final BufferedReader bufferedReader;
	private final ErrorHandler errorHandler;

	public LexerImpl(BufferedReader bufferedReader, ErrorHandler errorHandler) {
		this.bufferedReader = bufferedReader;
		this.errorHandler = errorHandler;
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
		buildUndefinedTokenAndHandleError(currentChar);
		return token;
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
			double fractionPart = getFractionPart(value);
			if (StringUtils.isAlpha(currentChar)) {			// don't allow alpha symbols follow floats
				if (fractionPart == 0) {
					buildUndefinedTokenAndHandleError(value + DOT + currentChar);
				} else {
					buildUndefinedTokenAndHandleError(value + fractionPart + currentChar);
				}
				return true;	// number is not built, although function returns true as we don't want to evaluate other token types
			}
			this.token = new TokenFloat(tokenPosition, value+fractionPart);
			return true;
		}

		if (StringUtils.isAlpha(currentChar)) {				// don't allow alpha symbols follow integers
			buildUndefinedTokenAndHandleError(value + currentChar);
			return true;	// number is not built, although function returns true as we don't want to evaluate other token types
		}
		this.token = new TokenInteger(tokenPosition, value);
		return true;
	}

	private int getDecimalPart(int value) {
		int decimal;
		while (StringUtils.isNumeric(nextChar())) {
			decimal = Integer.parseInt(currentChar);
			if ((Integer.MAX_VALUE - decimal) / 10 >= value) {
				value = value * 10 + decimal;
			} else {
				handleError(ErrorType.NUMERIC_LIMIT_EXCEEDED, tokenPosition, value + "...");
			}
		}
		return value;
	}

	private double getFractionPart(int decimalPart) {
		int num_of_digits = 0;
		int fraction = 0;
		while (StringUtils.isNumeric(nextChar())) {
			int decimal = Integer.parseInt(currentChar);
			if ((Integer.MAX_VALUE - decimal) / 10 >= fraction) {
				fraction = fraction * 10 + decimal;
				num_of_digits++;
			} else {
				handleError(ErrorType.NUMERIC_LIMIT_EXCEEDED, tokenPosition, decimalPart + DOT + fraction + "...");
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
			if (builder.length() >= Configuration.getIdentifierMaxLength()) {
				handleError(ErrorType.IDENTIFIER_LENGTH_EXCEEDED, tokenPosition, builder.toString());
				return true;
			}
			builder.append(currentChar);
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
					buildUndefinedTokenAndHandleError("!" + currentChar);
					return true;
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
			if (StringUtils.equals(currentChar, StringUtils.LF)) {
				this.token = new TokenComment(tokenPosition, builder.toString());
				return;
			}
			if (builder.length() >= Configuration.getCommentMaxLength()) {
				handleError(ErrorType.COMMENT_LENGTH_EXCEEDED, tokenPosition, builder.toString());
				return;
			}
			builder.append(currentChar);
		}
		this.token = new TokenComment(tokenPosition, builder.toString());
	}

	private boolean tryBuildText() {
		if (!StringUtils.equals(currentChar, DOUBLE_QUOTE)) {
			return false;
		}
		final var builder = new StringBuilder();
		while (!StringUtils.equals(nextChar(), DOUBLE_QUOTE)) {
			if (builder.length() >= Configuration.getTextMaxLength()) {
				handleError(ErrorType.TEXT_LENGTH_EXCEEDED, tokenPosition, builder.toString());
			}
			if (StringUtils.isEmpty(currentChar)) {
				handleError(ErrorType.END_OF_FILE_REACHED, tokenPosition, builder.toString());
			}
			if (StringUtils.equals(currentChar, BACKSLASH)) {
				builder.append(parseEscapeCharacter());
				continue;
			}
			builder.append(currentChar);
		}
		this.token = new TokenText(tokenPosition, builder.toString());
		nextChar();
		return true;
	}

	private String parseEscapeCharacter() {
		nextChar();
		return EscapeUtils.SEQUENCE_MAP.getOrDefault(currentChar, currentChar);
	}

	private boolean tryBuildEOF() {
		if (!StringUtils.isEmpty(currentChar)) {
			return false;
		}
		this.token = new TokenEOF(tokenPosition);
		return true;
	}

	private void buildUndefinedTokenAndHandleError(String initialString) {
		final var builder = new StringBuilder(initialString);
		while (!nextChar().isBlank() && builder.length() < Configuration.getIdentifierMaxLength()) {
			builder.append(currentChar);
		}
		handleError(ErrorType.UNDEFINED_TOKEN, tokenPosition, builder.toString());
	}

	private String nextChar() {
		int character;
		boolean isLineSeparator;
		try {
			character = bufferedReader.read();
			isLineSeparator = tryConsumeLineSeparator(character);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (isLineSeparator) {
			this.currentPosition = currentPosition.nextLine();
			this.currentChar = StringUtils.LF;		// any combination of LF and CR is mapped to LF
			return this.currentChar;
		}
		if (character == -1) {
			this.currentChar = StringUtils.EMPTY;
		} else {
			this.currentChar = Character.toString(character);
		}
		this.currentPosition = currentPosition.nextChar();
		return this.currentChar;
	}

	private boolean tryConsumeLineSeparator(int character) throws IOException {
		if (character == LINE_FEED_ASCII) {
			return true;
		}
		if (character == CARRIAGE_RETURN_ASCII) {	// if character is carriage return, the next can be line feed
			bufferedReader.mark(1);
			character = bufferedReader.read();	// reading the next character
			if (character != LINE_FEED_ASCII) {	// if it is not line feed, reset pointer to the previous character
				bufferedReader.reset();
			}
			return true;
		}
		return false;
	}

	private boolean isWhitespace(String string) {
		return string.isBlank() && !string.isEmpty();
	}

	private boolean isIdentifierChar(String string) {
		return StringUtils.isAlphanumeric(string) || StringUtils.equals(string, UNDERSCORE);
	}

	@SneakyThrows
	private void handleError(ErrorType errorType, Position position, String expression) {
		ErrorLexerDetails errorDetails = new ErrorLexerDetails(errorType, position, expression);
		this.token = new TokenUndefined(tokenPosition);
		errorHandler.handleError(errorDetails);
	}

}

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
import java.util.Optional;

public class LexerImpl implements Lexer {

	private static final char UNDERSCORE = '_';
	private static final char DOT = '.';
	private static final char DOUBLE_QUOTE = '\"';
	private static final char BACKSLASH = '\\';
	private static final char LINE_SEPARATOR = '\n';
	private static final char ETX = 3;
	private static final int LINE_FEED_ASCII = 10;
	private static final int CARRIAGE_RETURN_ASCII = 13;

	private final BufferedReader bufferedReader;
	private final ErrorHandler errorHandler;
	private final Position currentPosition;

	private char currentChar;
	private Position tokenPosition;

	public LexerImpl(BufferedReader bufferedReader, ErrorHandler errorHandler) {
		this.bufferedReader = bufferedReader;
		this.errorHandler = errorHandler;
		this.currentPosition = new Position(1, 1);
		this.currentChar = readChar();
	}

	@Override
	public Token next() {
		while (Character.isWhitespace(currentChar)) {
			currentChar = nextChar();
		}
		this.tokenPosition = currentPosition.copy();

		Optional<Token> token = tryBuildEOF();
		if (token.isPresent()) {
			return token.get();
		}
		token = tryBuildNumber();
		if (token.isPresent()) {
			return token.get();
		}
		token = tryBuildIdentifierOrKeyword();
		if (token.isPresent()) {
			return token.get();
		}
		token = tryBuildSymbolOrComment();
		if (token.isPresent()) {
			return token.get();
		}
		token = tryBuildText();
		if (token.isPresent()) {
			return token.get();
		}
		String undefinedSequence = parseUndefinedSequence(Character.toString(currentChar));
		handleError(ErrorType.UNDEFINED_TOKEN, tokenPosition, undefinedSequence);
		return new TokenUndefined(tokenPosition);
	}

	private Optional<Token> tryBuildNumber() {
		if (!Character.isDigit(currentChar)) {
			return Optional.empty();
		}
		int value = Character.getNumericValue(currentChar);
		Integer decimalPart = parseDecimalPart(value);
		if (decimalPart == null) {
			return Optional.of(new TokenUndefined(tokenPosition));
		}

		if (currentChar == DOT) {
			// don't allow alpha symbols to follow numbers
			if (Character.isLetter(nextChar())) {
				String undefinedSequence = parseUndefinedSequence(StringUtils.join(decimalPart, DOT, currentChar));
				handleError(ErrorType.UNDEFINED_TOKEN, tokenPosition, undefinedSequence);
				return Optional.of(new TokenUndefined(tokenPosition));
			}
			return parseFloat(decimalPart);
		}

		// don't allow alpha symbols follow numbers
		if (Character.isLetter(currentChar)) {
			String undefinedSequence = parseUndefinedSequence(StringUtils.join(decimalPart, currentChar));
			handleError(ErrorType.UNDEFINED_TOKEN, tokenPosition, undefinedSequence);
			return Optional.of(new TokenUndefined(tokenPosition));
		}
		return Optional.of(new TokenInteger(tokenPosition, decimalPart));
	}

	private Optional<Token> parseFloat(Integer decimalPart) {
		if (!Character.isDigit(currentChar)) {
			return Optional.of(new TokenFloat(tokenPosition, Double.valueOf(decimalPart)));
		}
		int fraction = Character.getNumericValue(currentChar);
		Double fractionPart = parseFractionPart(decimalPart, fraction);
		if (fractionPart == null) {
			return Optional.of(new TokenUndefined(tokenPosition));
		}
		// don't allow alpha symbols follow floats
		if (Character.isLetter(currentChar)) {
			String undefinedSequence = parseUndefinedSequence(StringUtils.join(decimalPart + fractionPart, currentChar));
			handleError(ErrorType.UNDEFINED_TOKEN, tokenPosition, undefinedSequence);
			return Optional.of(new TokenUndefined(tokenPosition));
		}
		return Optional.of(new TokenFloat(tokenPosition, decimalPart + fractionPart));
	}

	private Integer parseDecimalPart(int value) {
		int digit;
		while (Character.isDigit(nextChar())) {
			digit = Character.getNumericValue(currentChar);
			if ((Integer.MAX_VALUE - digit) / 10 >= value) {
				value = value * 10 + digit;
			} else {
				handleError(ErrorType.NUMERIC_LIMIT_EXCEEDED, tokenPosition, value + "...");
				return null;
			}
		}
		return value;
	}

	private Double parseFractionPart(int decimalPart, int fraction) {
		int num_of_digits = 1;
		int digit;
		while (Character.isDigit(nextChar())) {
			digit = Character.getNumericValue(currentChar);
			if ((Integer.MAX_VALUE - digit) / 10 >= fraction) {
				fraction = fraction * 10 + digit;
				num_of_digits++;
			} else {
				handleError(ErrorType.NUMERIC_LIMIT_EXCEEDED, tokenPosition, StringUtils.join(decimalPart, DOT, fraction, "..."));
				return null;
			}
		}
		return fraction / Math.pow(10, num_of_digits);
	}

	private Optional<Token> tryBuildIdentifierOrKeyword() {
		if (!Character.isLetter(currentChar)) {
			return Optional.empty();
		}
		var builder = new StringBuilder(Character.toString(currentChar));
		while (isIdentifierChar(nextChar())) {
			if (builder.length() == Configuration.getIdentifierMaxLength()) {
				handleError(ErrorType.IDENTIFIER_LENGTH_EXCEEDED, tokenPosition, builder.toString());
				return Optional.of(new TokenIdentifier(tokenPosition, builder.toString()));
			}
			builder.append(currentChar);
		}

		String key = builder.toString();
		TokenType tokenType = TokenGroups.BOOL_LITERALS.get(key);
		if (tokenType != null) {
			Boolean value = tokenType == TokenType.TRUE ? Boolean.TRUE : Boolean.FALSE;
			return Optional.of(new TokenBool(tokenPosition, value));
		}

		tokenType = TokenGroups.KEYWORDS.get(key);
		if (tokenType != null) {
			return Optional.of(new TokenKeyword(tokenType, tokenPosition));
		}
		return Optional.of(new TokenIdentifier(tokenPosition, key));
	}

	private Optional<Token> tryBuildSymbolOrComment() {
		if (!TokenGroups.SYMBOLS.containsKey(Character.toString(currentChar)) && currentChar != '!') {
			return Optional.empty();
		}
		switch (currentChar) {
			case '=' -> {
				return parseDoubleSymbol(TokenType.EQUAL, TokenType.ASSIGN);
			}
			case '+' -> {
				return parseDoubleSymbol(TokenType.ADD_AND_ASSIGN, TokenType.ADD);
			}
			case '-' -> {
				return parseDoubleSymbol(TokenType.SUBTRACT_AND_ASSIGN, TokenType.SUBTRACT);
			}
			case '<' -> {
				return parseDoubleSymbol(TokenType.LESS_OR_EQUAL, TokenType.LESS);
			}
			case '>' -> {
				return parseDoubleSymbol(TokenType.GREATER_OR_EQUAL, TokenType.GREATER);
			}
			case '!' -> {
				if (nextChar() == '=') {
					nextChar();
					return Optional.of(new TokenSymbol(TokenType.NOT_EQUAL, tokenPosition));
				} else {
					String undefinedSequence = parseUndefinedSequence("!" + currentChar);
					handleError(ErrorType.UNDEFINED_TOKEN, tokenPosition, undefinedSequence);
					return Optional.of(new TokenUndefined(tokenPosition));
				}
			}
			case '/' -> {
				if (nextChar() == '/') {
					return parseComment();
				} else {
					return Optional.of(new TokenSymbol(TokenType.DIVIDE, tokenPosition));
				}
			}
			default -> {
				final var tokenType = TokenGroups.SYMBOLS.get(Character.toString(currentChar));
				nextChar();
				return Optional.of(new TokenSymbol(tokenType, tokenPosition));
			}
		}
	}

	private Optional<Token> parseDoubleSymbol(TokenType doubleSymbolType, TokenType singleSymbolType) {
		if (nextChar() == '=') {
			nextChar();
			return Optional.of(new TokenSymbol(doubleSymbolType, tokenPosition));
		} else {
			return Optional.of(new TokenSymbol(singleSymbolType, tokenPosition));
		}
	}

	private Optional<Token> parseComment() {
		final var builder = new StringBuilder();
		while (nextChar() != ETX) {
			if (currentChar == LINE_SEPARATOR) {
				break;
			}
			if (builder.length() == Configuration.getCommentMaxLength()) {
				handleError(ErrorType.COMMENT_LENGTH_EXCEEDED, tokenPosition, builder.toString());
				break;
			}
			builder.append(currentChar);
		}
		return Optional.of(new TokenComment(tokenPosition, builder.toString()));
	}

	private Optional<Token> tryBuildText() {
		if (currentChar != DOUBLE_QUOTE) {
			return Optional.empty();
		}
		final var builder = new StringBuilder();
		while (nextChar() != DOUBLE_QUOTE) {
			if (builder.length() == Configuration.getTextMaxLength()) {
				handleError(ErrorType.TEXT_LENGTH_EXCEEDED, tokenPosition, builder.toString());
				return Optional.of(new TokenText(tokenPosition, builder.toString()));
			}
			if (currentChar == ETX) {
				handleError(ErrorType.END_OF_FILE_REACHED, tokenPosition, builder.toString());
				return Optional.of(new TokenText(tokenPosition, builder.toString()));
			}
			if (currentChar == BACKSLASH) {
				builder.append(parseEscapeCharacter());
				continue;
			}
			builder.append(currentChar);
		}
		nextChar();
		return Optional.of(new TokenText(tokenPosition, builder.toString()));
	}

	private char parseEscapeCharacter() {
		nextChar();
		return EscapeUtils.SEQUENCE_MAP.getOrDefault(currentChar, currentChar);
	}

	private Optional<Token> tryBuildEOF() {
		if (currentChar != ETX) {
			return Optional.empty();
		}
		return Optional.of(new TokenEOF(tokenPosition));
	}

	private String parseUndefinedSequence(String initialString) {
		final var builder = new StringBuilder(initialString);
		while (isUndefinedTokenChar(nextChar())
				&& builder.length() < Configuration.getIdentifierMaxLength()) {
			builder.append(currentChar);
		}
		return builder.toString();
	}

	private char nextChar() {
		movePosition();
		return readChar();
	}

	private char readChar() {
		int character;
		boolean isLineSeparator;
		try {
			character = bufferedReader.read();
			isLineSeparator = tryConsumeLineSeparator(character);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (isLineSeparator) {
			this.currentChar = LINE_SEPARATOR;
			return this.currentChar;
		}
		if (character == -1) {
			this.currentChar = ETX;
		} else {
			this.currentChar = (char) character;
		}
		return this.currentChar;
	}

	private boolean tryConsumeLineSeparator(int character) throws IOException {
		if (character == LINE_FEED_ASCII) {
			bufferedReader.mark(1);
			character = bufferedReader.read();    // reading the next character
			if (character != CARRIAGE_RETURN_ASCII) {    // if it is not carriage return, reset pointer to the previous character
				bufferedReader.reset();
			}
			return true;
		}
		if (character == CARRIAGE_RETURN_ASCII) {    // if character is carriage return, the next can be line feed
			bufferedReader.mark(1);
			character = bufferedReader.read();    // reading the next character
			if (character != LINE_FEED_ASCII) {    // if it is not line feed, reset pointer to the previous character
				bufferedReader.reset();
			}
			return true;
		}
		return false;
	}

	private void movePosition() {
		if (currentChar == LINE_SEPARATOR) {
			this.currentPosition.nextLine();
		} else {
			this.currentPosition.nextChar();
		}
	}

	private boolean isIdentifierChar(char c) {
		return Character.isLetter(c) || Character.isDigit(c) || c == UNDERSCORE;
	}

	private boolean isUndefinedTokenChar(char c) {
		return !(Character.isWhitespace(c) || currentChar == ETX || TokenGroups.SYMBOLS.containsKey(Character.toString(c)));
	}

	@SneakyThrows
	private void handleError(ErrorType errorType, Position position, String expression) {
		ErrorLexerDetails errorDetails = new ErrorLexerDetails(errorType, position, expression);
		errorHandler.handleError(errorDetails);
	}

}

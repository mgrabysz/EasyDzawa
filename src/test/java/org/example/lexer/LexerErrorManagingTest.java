package org.example.lexer;

import org.example.error.exception.LexicalException;
import org.example.error.manager.ErrorManager;
import org.example.token.Token;
import org.example.token.TokenType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LexerErrorManagingTest {

	private static Stream<Arguments> testUndefinedToken() {
		return Stream.of(
				Arguments.of("@#$%^&", "Undefined expression: @#$%^& at line 0 position 0"),
				Arguments.of("123abc", "Undefined expression: 123abc at line 0 position 0"),
				Arguments.of("123.abc", "Undefined expression: 123.abc at line 0 position 0"),
				Arguments.of("123.456abc", "Undefined expression: 123.456abc at line 0 position 0"),
				Arguments.of("@$abc", "Undefined expression: @$abc at line 0 position 0"),
				Arguments.of("!+", "Undefined expression: !+ at line 0 position 0"),
				Arguments.of("correct @#$ correct", "Undefined expression: @#$ at line 0 position 8")
		);
	}

	@ParameterizedTest
	@MethodSource
	public void testUndefinedToken(String inputString, String expectedMessage) {
		Exception exception = assertThrows(LexicalException.class, () -> readFromString(inputString));
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}

	private static Stream<Arguments> testNumericLimitExceeded() {
		// Maximal integer value == 2147483647
		return Stream.of(
				Arguments.of("2147483648", "Numeric expression: 214748364... at line 0 position 0 exceeds limit"),
				Arguments.of("0.2147483648", "Numeric expression: 0.214748364... at line 0 position 0 exceeds limit")
		);
	}

	@ParameterizedTest
	@MethodSource
	public void testNumericLimitExceeded(String inputString, String expectedMessage) {
		Exception exception = assertThrows(LexicalException.class, () -> readFromString(inputString));
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testIdentifierLengthExceeded() {
		String inputString = "a".repeat(257);
		String trimmedExpression = "a".repeat(24) + "...";
		String expectedMessage = "Identifier: %s starting at line %d position %d exceeds maximal length"
				.formatted(trimmedExpression, 0, 0);

		Exception exception = assertThrows(LexicalException.class, () -> readFromString(inputString));
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testTextLengthExceeded() {
		String inputString = "\"" + "a".repeat(1025) + "\"";
		String trimmedExpression = "a".repeat(24) + "...";
		String expectedMessage = "Text: %s starting at line %d position %d exceeds maximal length"
				.formatted(trimmedExpression, 0, 0);

		Exception exception = assertThrows(LexicalException.class, () -> readFromString(inputString));
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testTextReachedEndOfFile() {
		String inputString = "\"abc";
		String expectedMessage = "End of file reached while parsing text: %s starting at line %d position %d"
				.formatted("abc", 0, 0);

		Exception exception = assertThrows(LexicalException.class, () -> readFromString(inputString));
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void testCommentLengthExceeded() {
		String inputString = "//" + "a".repeat(1025);
		String trimmedExpression = "a".repeat(24) + "...";
		String expectedMessage = "Comment: %s starting at line %d position %d exceeds maximal length"
				.formatted(trimmedExpression, 0, 0);

		Exception exception = assertThrows(LexicalException.class, () -> readFromString(inputString));
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}

	private static List<Token> readFromString(String input) throws IOException {
		List<Token> tokens = new ArrayList<>();
		try (var reader = new BufferedReader(new StringReader(input))) {
			var lexer = new LexerImpl(reader, ErrorManager::handleError);
			Token token = lexer.next();
			while (token.getType() != TokenType.END_OF_FILE) {
				tokens.add(token);
				token = lexer.next();
			}
			tokens.add(token);
		}
		return tokens;
	}

}

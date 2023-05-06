package org.example.lexer;

import org.example.commons.Position;
import org.example.error.manager.ErrorManager;
import org.example.token.Token;
import org.example.token.TokenEOF;
import org.example.token.TokenText;
import org.example.token.TokenType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.example.token.TokenType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LexerFileInputTest {


	private static Stream<Arguments> testEscapeCharacters() {
		final String path = "src/test/resources/lexer/escapeCharacter.txt";
		List<Token> expectedTokens = new ArrayList<>(Arrays.asList(
				new TokenText(new Position(), "This is tab: \t, this is newline: \n"),
				new TokenText(new Position(), "This is \"quote\" inside a quote"),
				new TokenText(new Position(), "This is backslash: \\"),
				new TokenText(new Position(), "This single backslash is ignored"),
				new TokenEOF(new Position())
		));
		final List<Token> actualTokens = readFromFile(path);
		return IntStream.range(0, actualTokens.size())
				.mapToObj(i -> Arguments.of(expectedTokens.get(i), actualTokens.get(i)));
	}

	@ParameterizedTest
	@MethodSource
	void testEscapeCharacters(Token expectedToken, Token actualToken) {
		assertEquals((String) expectedToken.getValue(), actualToken.getValue());
		assertEquals(expectedToken.getType(), actualToken.getType());
	}

	private static Stream<Arguments> testOperations() {
		final String path = "src/test/resources/lexer/operations.txt";
		List<TokenType> expectedTokenTypes = new ArrayList<>(Arrays.asList(
				IDENTIFIER, ASSIGN, INTEGER, ADD, INTEGER, SUBTRACT, INTEGER, MULTIPLY,
				OPEN_PARENTHESIS, INTEGER, ADD, INTEGER, CLOSE_PARENTHESIS, SEMICOLON,
				IDENTIFIER, ADD_AND_ASSIGN, INTEGER, SEMICOLON,
				IDENTIFIER, ASSIGN, FLOAT, DIVIDE, INTEGER, SEMICOLON,
				IDENTIFIER, ASSIGN, IDENTIFIER, MULTIPLY, IDENTIFIER, SEMICOLON,
				IDENTIFIER, ASSIGN, IDENTIFIER, GREATER_OR_EQUAL, IDENTIFIER, SEMICOLON,
				IDENTIFIER, ASSIGN, IDENTIFIER, OR, OPEN_PARENTHESIS,
				IDENTIFIER, EQUAL, INTEGER, CLOSE_PARENTHESIS, SEMICOLON,
				IDENTIFIER, ASSIGN, OPEN_PARENTHESIS, OPEN_PARENTHESIS,
				IDENTIFIER, GREATER, INTEGER, CLOSE_PARENTHESIS,
				AND, IDENTIFIER, CLOSE_PARENTHESIS, OR, IDENTIFIER, LESS, INTEGER, SEMICOLON,
				END_OF_FILE
		));
		final List<Token> actualTokens = readFromFile(path);
		return IntStream.range(0, actualTokens.size())
				.mapToObj(i -> Arguments.of(expectedTokenTypes.get(i), actualTokens.get(i).getType()));
	}

	@ParameterizedTest
	@MethodSource
	void testOperations(TokenType expectedTokenType, TokenType actualTokenType) {
		assertEquals(expectedTokenType, actualTokenType);
	}

	private static Stream<Arguments> testMethodCalls() {
		final String path = "src/test/resources/lexer/methodCalls.txt";
		List<TokenType> expectedTokenTypes = new ArrayList<>(Arrays.asList(
				IDENTIFIER, ASSIGN, IDENTIFIER, OPEN_PARENTHESIS, CLOSE_PARENTHESIS, SEMICOLON,
				IDENTIFIER, DOT, IDENTIFIER, OPEN_PARENTHESIS, INTEGER, CLOSE_PARENTHESIS, SEMICOLON,
				IDENTIFIER, DOT, IDENTIFIER, OPEN_PARENTHESIS, INTEGER, CLOSE_PARENTHESIS, SEMICOLON,
				IDENTIFIER, DOT, IDENTIFIER, OPEN_PARENTHESIS, INTEGER,
				COMA, INTEGER, CLOSE_PARENTHESIS, SEMICOLON,
				IDENTIFIER, ASSIGN, IDENTIFIER, DOT, IDENTIFIER,
				OPEN_PARENTHESIS, INTEGER, CLOSE_PARENTHESIS, SEMICOLON,
				IDENTIFIER, DOT, IDENTIFIER, OPEN_PARENTHESIS, INTEGER, CLOSE_PARENTHESIS, SEMICOLON,
				IDENTIFIER, DOT, IDENTIFIER, OPEN_PARENTHESIS, INTEGER, CLOSE_PARENTHESIS, SEMICOLON,
				IDENTIFIER, DOT, IDENTIFIER, OPEN_PARENTHESIS, FLOAT, CLOSE_PARENTHESIS, SEMICOLON,
				END_OF_FILE
		));
		final List<Token> actualTokens = readFromFile(path);
		return IntStream.range(0, actualTokens.size())
				.mapToObj(i -> Arguments.of(expectedTokenTypes.get(i), actualTokens.get(i).getType()));
	}

	@ParameterizedTest
	@MethodSource
	void testMethodCalls(TokenType expectedTokenType, TokenType actualTokenType) {
		assertEquals(expectedTokenType, actualTokenType);
	}

	private static Stream<Arguments> testLoopAndIf() {
		final String path = "src/test/resources/lexer/loopAndIf.txt";
		List<TokenType> expectedTokenTypes = new ArrayList<>(Arrays.asList(
				IDENTIFIER, ASSIGN, IDENTIFIER, OPEN_PARENTHESIS, CLOSE_PARENTHESIS, SEMICOLON,
				FOR, IDENTIFIER, IN, IDENTIFIER, OPEN_PARENTHESIS, INTEGER, COMA, INTEGER,
				COMA, INTEGER, CLOSE_PARENTHESIS, OPEN_BRACKET,
				IDENTIFIER, DOT, IDENTIFIER, OPEN_PARENTHESIS, IDENTIFIER, MULTIPLY, INTEGER,
				CLOSE_PARENTHESIS, SEMICOLON, CLOSE_BRACKET,
				FOR, IDENTIFIER, IN, IDENTIFIER, OPEN_BRACKET, COMMENT,
				IF, OPEN_PARENTHESIS, IDENTIFIER, GREATER, INTEGER, CLOSE_PARENTHESIS, OPEN_BRACKET,
				IDENTIFIER, OPEN_PARENTHESIS, IDENTIFIER, COMA, TEXT, CLOSE_PARENTHESIS, SEMICOLON,
				CLOSE_BRACKET, ELSE, OPEN_BRACKET,
				IDENTIFIER, OPEN_PARENTHESIS, IDENTIFIER, COMA, TEXT, CLOSE_PARENTHESIS, SEMICOLON,
				CLOSE_BRACKET, CLOSE_BRACKET,
				END_OF_FILE
		));
		final List<Token> actualTokens = readFromFile(path);
		return IntStream.range(0, actualTokens.size())
				.mapToObj(i -> Arguments.of(expectedTokenTypes.get(i), actualTokens.get(i).getType()));
	}

	@ParameterizedTest
	@MethodSource
	void testLoopAndIf(TokenType expectedTokenType, TokenType actualTokenType) {
		assertEquals(expectedTokenType, actualTokenType);
	}

	private static Stream<Arguments> testPositions() {
		final String path = "src/test/resources/lexer/positions.txt";
		List<Position> positions = new ArrayList<>(Arrays.asList(
				new Position(1, 1),
				new Position(2, 2),
				new Position(3, 3),
				new Position(4, 4),
				new Position(5, 5),
				new Position(6, 6),
				new Position(6, 10)
		));
		final List<Token> actualTokens = readFromFile(path);
		return IntStream.range(0, actualTokens.size())
				.mapToObj(i -> Arguments.of(positions.get(i), actualTokens.get(i).getPosition()));
	}

	@ParameterizedTest
	@MethodSource
	void testPositions(Position expectedPosition, Position actualPosition) {
		assertEquals(expectedPosition.getLineNumber(), actualPosition.getLineNumber());
		assertEquals(expectedPosition.getCharacterNumber(), actualPosition.getCharacterNumber());
	}

	private static List<Token> readFromFile(String path) {
		List<Token> tokens = new ArrayList<>();
		try (FileReader fileReader = new FileReader(path)) {
			var file = new BufferedReader(fileReader);
			var lexer = new LexerImpl(file, ErrorManager::handleError);
			Token token = lexer.next();
			while (token.getType() != TokenType.END_OF_FILE) {
				tokens.add(token);
				token = lexer.next();
			}
			tokens.add(token);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return tokens;
	}

}

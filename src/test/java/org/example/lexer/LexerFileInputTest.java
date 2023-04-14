package org.example.lexer;

import org.example.EasyInterpreter;
import org.example.Position;
import org.example.token.Token;
import org.example.token.TokenEOF;
import org.example.token.TokenText;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
		assertEquals((String)expectedToken.getValue(), actualToken.getValue());
		assertEquals(expectedToken.getType(), actualToken.getType());
	}

	private static List<Token> readFromFile(String path) {
		List<Token> tokens;
		final var interpreter = new EasyInterpreter();
		try {
			tokens = interpreter.readTokens(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return tokens;
	}

}

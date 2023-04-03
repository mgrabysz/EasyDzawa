package org.example.lexer;

import org.example.Position;
import org.example.token.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LexerTest {

    /*
    * Static method returns Stream of pairs - each pair contains expected and actually received Token
    * Method with annotation @ParameterizedTest reads the stream of pairs and performs assertion
    */
    private static Stream<Arguments> testReadingIntegers() throws IOException {
        String input = "22 348 \t 9 9999  \n 102456";
        List<Token> expectedTokens = new ArrayList<>(Arrays.asList(
            new TokenInteger(new Position(0, 0), 22),
            new TokenInteger(new Position(0, 3), 348),
            new TokenInteger(new Position(0, 7), 9),
            new TokenInteger(new Position(0, 9), 9999),
            new TokenInteger(new Position(0, 16), 102456),
            new TokenEOF(new Position(0, 22))
        ));
        List<Token> actualTokens = readFromString(input);
        return IntStream.range(0, actualTokens.size())
                .mapToObj(i -> Arguments.of(expectedTokens.get(i), actualTokens.get(i)));
    }

    @ParameterizedTest
    @MethodSource
    void testReadingIntegers(Token expectedToken, Token actualToken) {
        assertEquals((Integer) expectedToken.getValue(), actualToken.getValue());
        assertEquals(expectedToken.getType(), actualToken.getType());
    }

    private static Stream<Arguments> testReadingIdentifiers() throws IOException {
        String input = "dla element w moja_lista12 prawda";
        List<Token> expectedTokens = new ArrayList<>(Arrays.asList(
                new TokenKeyword(TokenType.FOR, new Position()),
                new TokenIdentifier(new Position(), "element"),
                new TokenKeyword(TokenType.IN, new Position()),
                new TokenIdentifier(new Position(), "moja_lista12"),
                new TokenBool(new Position(), Boolean.TRUE),
                new TokenEOF(new Position())
        ));
        List<Token> actualTokens = readFromString(input);
        return IntStream.range(0, actualTokens.size())
                .mapToObj(i -> Arguments.of(expectedTokens.get(i), actualTokens.get(i)));
    }

    @ParameterizedTest
    @MethodSource
    void testReadingIdentifiers(Token expectedToken, Token actualToken) {
        assertEquals((Object) expectedToken.getValue(), actualToken.getValue());
        assertEquals(expectedToken.getType(), actualToken.getType());
    }

    private static List<Token> readFromString(String input) throws IOException {
        List<Token> tokens = new ArrayList<>();
        try (var reader = new BufferedReader(new StringReader(input))) {
            var lexer = new EasyLexerImpl(reader);
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

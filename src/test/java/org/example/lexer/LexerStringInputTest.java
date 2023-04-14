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

public class LexerStringInputTest {

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
        String input = "zwróć dla w jeżeli \n inaczej klasa \t tenże oraz lub nie prawda fałsz moja_lista12 orazlub";
        List<Token> expectedTokens = new ArrayList<>(Arrays.asList(
                new TokenKeyword(TokenType.RETURN, new Position()),
                new TokenKeyword(TokenType.FOR, new Position()),
                new TokenKeyword(TokenType.IN, new Position()),
                new TokenKeyword(TokenType.IF, new Position()),
                new TokenKeyword(TokenType.ELSE, new Position()),
                new TokenKeyword(TokenType.CLASS, new Position()),
                new TokenKeyword(TokenType.THIS, new Position()),
                new TokenKeyword(TokenType.AND, new Position()),
                new TokenKeyword(TokenType.OR, new Position()),
                new TokenKeyword(TokenType.NOT, new Position()),
                new TokenBool(new Position(), Boolean.TRUE),
                new TokenBool(new Position(), Boolean.FALSE),
                new TokenIdentifier(new Position(), "moja_lista12"),
                new TokenIdentifier(new Position(), "orazlub"),
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

    private static Stream<Arguments> testReadingFloats() throws IOException {
        String input = "22.22 348.098473 \t 9. 9999.12342423  \n 0.102456";
        List<Token> expectedTokens = new ArrayList<>(Arrays.asList(
                new TokenFloat(new Position(), 22.22),
                new TokenFloat(new Position(), 348.098473),
                new TokenFloat(new Position(), 9.0),
                new TokenFloat(new Position(), 9999.12342423),
                new TokenFloat(new Position(), 0.102456),
                new TokenEOF(new Position())
        ));
        List<Token> actualTokens = readFromString(input);
        return IntStream.range(0, actualTokens.size())
                .mapToObj(i -> Arguments.of(expectedTokens.get(i), actualTokens.get(i)));
    }

    @ParameterizedTest
    @MethodSource
    void testReadingFloats(Token expectedToken, Token actualToken) {
        assertEquals((Double) expectedToken.getValue(), actualToken.getValue());
        assertEquals(expectedToken.getType(), actualToken.getType());
    }

    private static Stream<Arguments> testReadingSymbols() throws IOException {
        String input = "{}();,=+=-===!=><\n>=<=+-*/ //comment\n;";
        List<Token> expectedTokens = new ArrayList<>(Arrays.asList(
            new TokenSymbol(TokenType.OPEN_BRACKET, new Position()),
            new TokenSymbol(TokenType.CLOSE_BRACKET, new Position()),
            new TokenSymbol(TokenType.OPEN_PARENTHESIS, new Position()),
            new TokenSymbol(TokenType.CLOSE_PARENTHESIS, new Position()),
            new TokenSymbol(TokenType.SEMICOLON, new Position()),
            new TokenSymbol(TokenType.COMA, new Position()),
            new TokenSymbol(TokenType.ASSIGN, new Position()),
            new TokenSymbol(TokenType.ADD_AND_ASSIGN, new Position()),
            new TokenSymbol(TokenType.SUBTRACT_AND_ASSIGN, new Position()),
            new TokenSymbol(TokenType.EQUAL, new Position()),
            new TokenSymbol(TokenType.NOT_EQUAL, new Position()),
            new TokenSymbol(TokenType.GREATER, new Position()),
            new TokenSymbol(TokenType.LESS, new Position()),
            new TokenSymbol(TokenType.GREATER_OR_EQUAL, new Position()),
            new TokenSymbol(TokenType.LESS_OR_EQUAL, new Position()),
            new TokenSymbol(TokenType.ADD, new Position()),
            new TokenSymbol(TokenType.SUBTRACT, new Position()),
            new TokenSymbol(TokenType.MULTIPLY, new Position()),
            new TokenSymbol(TokenType.DIVIDE, new Position()),
            new TokenComment(new Position(), "comment"),
            new TokenSymbol(TokenType.SEMICOLON, new Position()),
            new TokenEOF(new Position())
        ));
        List<Token> actualTokens = readFromString(input);
        return IntStream.range(0, actualTokens.size())
                .mapToObj(i -> Arguments.of(expectedTokens.get(i), actualTokens.get(i)));
    }

    @ParameterizedTest
    @MethodSource
    void testReadingSymbols(Token expectedToken, Token actualToken) {
        assertEquals((Object) expectedToken.getValue(), actualToken.getValue());
        assertEquals(expectedToken.getType(), actualToken.getType());
    }

    private static Stream<Arguments> testReadingTexts() throws IOException {
        String input = "  \t \"Hello, it's me - I was wandering...\n\" \"this is plus:+;\"";
        List<Token> expectedTokens = new ArrayList<>(Arrays.asList(
                new TokenText(new Position(), "Hello, it's me - I was wandering...\n"),
                new TokenText(new Position(), "this is plus:+;"),
                new TokenEOF(new Position(0, 22))
        ));
        List<Token> actualTokens = readFromString(input);
        return IntStream.range(0, actualTokens.size())
                .mapToObj(i -> Arguments.of(expectedTokens.get(i), actualTokens.get(i)));
    }

    @ParameterizedTest
    @MethodSource
    void testReadingTexts(Token expectedToken, Token actualToken) {
        assertEquals((String) expectedToken.getValue(), actualToken.getValue());
        assertEquals(expectedToken.getType(), actualToken.getType());
    }


    private static List<Token> readFromString(String input) throws IOException {
        List<Token> tokens = new ArrayList<>();
        try (var reader = new BufferedReader(new StringReader(input))) {
            var lexer = new LexerImpl(reader);
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

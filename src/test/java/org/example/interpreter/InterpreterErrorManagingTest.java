package org.example.interpreter;

import org.example.error.exception.SemanticException;
import org.example.error.manager.ErrorManager;
import org.example.lexer.LexerImpl;
import org.example.parser.ParserImpl;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InterpreterErrorManagingTest {

    private final static String ATTRIBUTE_NOT_DEFINED = """
            klasa A {
              A() { }
            }
            główna() {
              a = A();
              var = a.atrybut;
            }
            """;

    private static Stream<Arguments> testUnsupportedOperation() {
        return Stream.of(
                Arguments.of("1 + 2 * prawda", "Semantic error of type: OPERATION_NOT_SUPPORTED: << 2 * prawda >> at line 1"),
                Arguments.of("2 + prawda", "Semantic error of type: OPERATION_NOT_SUPPORTED: << 2 + prawda >> at line 1"),
                Arguments.of("prawda >= fałsz", "Semantic error of type: OPERATION_NOT_SUPPORTED: << prawda >= fałsz >> at line 1"),
                Arguments.of("1 * 2 oraz 3", "Semantic error of type: OPERATION_NOT_SUPPORTED: << 1 * 2 oraz 3 >> at line 1")
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testUnsupportedOperation(String expression, String expectedMessage) {
        String inputString = placeInMain(expression);
        Exception exception = assertThrows(SemanticException.class, () -> readFromString(inputString));
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    private static Stream<Arguments> testErrors() {
        return Stream.of(
                Arguments.of(ATTRIBUTE_NOT_DEFINED, "Semantic error of type: ATTRIBUTE_NOT_DEFINED: << A.atrybut >> at line 6")
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testErrors(String inputString, String expectedMessage) {
        Exception exception = assertThrows(SemanticException.class, () -> readFromString(inputString));
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }


    private static String placeInMain(String expression) {
        return "główna() { var = %s; } ".formatted(expression);
    }

    private static void readFromString(String input) {
        try (BufferedReader reader = new BufferedReader(new StringReader(input))) {
            var lexer = new LexerImpl(reader, ErrorManager::handleError);
            var parser = new ParserImpl(lexer, ErrorManager::handleError);
            var program = parser.parse();
            Interpreter interpreter = new Interpreter(ErrorManager::handleError);
            interpreter.execute(program);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

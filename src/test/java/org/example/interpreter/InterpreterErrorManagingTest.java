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

    private final static String METHOD_NOT_DEFINED = """
            klasa A {
                A() {}
            }
            główna() {
                a = A();
                a.metoda();
            }
            """;

    private final static String ACCESS_NOT_ALLOWED = """
            klasa A {
                A() {
                    tenże.atrybut = 10;
                }
            }
            główna() {
                a = A();
                var = a.atrybut.atrybut;
            }
            """;

    private final static String CONSTRUCTOR_MISSING = """
            klasa A { }
            główna() { }
            """;

    private final static String ASSIGNMENT_INCORRECT = """
            funkcja() {
                zwróć 4;
            }
            główna() {
                funkcja() = 5;
            }
            """;

    private final static String ABORTED = """
            główna() {
                zakończ();
            }
            """;

    private final static String CONDITION_NOT_BOOLEAN = """
            główna() {
                jeżeli(2+2) {
                    var = prawda;
                }
            }
            """;

    private final static String MAIN_FUNCTION_MISSING = """
            hello() {
                napisz("Witaj, świecie");
            }
            """;

    private final static String CONSTRUCTOR_CONTAINS_RETURN = """
            klasa A {
                A() {
                    zwróć 40;
                }
            }
            główna() { }
            """;

    private final static String SELF_ACCESS_OUTSIDE_OF_CLASS = """
            główna() {
                tenże.a = 2;
            }
            """;

    private final static String VARIABLE_NOT_DEFINED_IN_SCOPE = """
            główna() {
                a = b;
            }
            """;

    private final static String INCORRECT_NUMBER_OF_ARGUMENTS = """
           dodawanie(a, b) {
                zwróć a + b;
           }
           główna() {
                dodawanie(1, 2, 3);
           }
           """;

    private static Stream<Arguments> testErrors() {
        return Stream.of(
                Arguments.of(ATTRIBUTE_NOT_DEFINED, "Semantic error of type: ATTRIBUTE_NOT_DEFINED: << A.atrybut >> at line 6"),
                Arguments.of(METHOD_NOT_DEFINED, "Semantic error of type: METHOD_NOT_DEFINED: << A.metoda() >> at line 6"),
                Arguments.of(ACCESS_NOT_ALLOWED, "Semantic error of type: ACCESS_NOT_ALLOWED: << a.atrybut.atrybut >> at line 8"),
                Arguments.of(CONSTRUCTOR_MISSING, "Semantic error of type: CONSTRUCTOR_MISSING: << A >> at line 1"),
                Arguments.of(ASSIGNMENT_INCORRECT, "Semantic error of type: ASSIGNMENT_INCORRECT: << funkcja() = 5 >> at line 5"),
                Arguments.of(ABORTED, "Terminated the execution of the program at line 2"),
                Arguments.of(CONDITION_NOT_BOOLEAN, "Semantic error of type: CONDITION_NOT_BOOLEAN: << jeżeli (2 + 2) >> at line 2"),
                Arguments.of(CONDITION_NOT_BOOLEAN, "Semantic error of type: CONDITION_NOT_BOOLEAN: << jeżeli (2 + 2) >> at line 2"),
                Arguments.of(MAIN_FUNCTION_MISSING, "Main function is missing"),
                Arguments.of(CONSTRUCTOR_CONTAINS_RETURN, "Semantic error of type: CONSTRUCTOR_CONTAINS_RETURN: << A >> at line 1"),
                Arguments.of(SELF_ACCESS_OUTSIDE_OF_CLASS, "Semantic error of type: SELF_ACCESS_OUTSIDE_OF_CLASS: << tenże.a >> at line 2"),
                Arguments.of(VARIABLE_NOT_DEFINED_IN_SCOPE, "Semantic error of type: VARIABLE_NOT_DEFINED_IN_SCOPE: << b >> at line 2"),
                Arguments.of(INCORRECT_NUMBER_OF_ARGUMENTS, "Semantic error of type: INCORRECT_NUMBER_OF_ARGUMENTS: << dodawanie(1,2,3) >> at line 5")
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testErrors(String inputString, String expectedMessage) {
        Exception exception = assertThrows(SemanticException.class, () -> readFromString(inputString));
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    private static Stream<Arguments> testUnsupportedOperations() {
        return Stream.of(
                Arguments.of("1 + 2 * prawda", "Semantic error of type: OPERATION_NOT_SUPPORTED: << 2 * prawda >> at line 1"),
                Arguments.of("2 + prawda", "Semantic error of type: OPERATION_NOT_SUPPORTED: << 2 + prawda >> at line 1"),
                Arguments.of("prawda >= fałsz", "Semantic error of type: OPERATION_NOT_SUPPORTED: << prawda >= fałsz >> at line 1"),
                Arguments.of("2 * 4 / 0", "Semantic error of type: ZERO_DIVISION: << 2 * 4 / 0 >> at line 1"),
                Arguments.of("1 * 2 oraz 3", "Semantic error of type: OPERATION_NOT_SUPPORTED: << 1 * 2 oraz 3 >> at line 1")
                );
    }

    @ParameterizedTest
    @MethodSource
    public void testUnsupportedOperations(String expression, String expectedMessage) {
        String inputString = placeInMain(expression);
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

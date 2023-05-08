package org.example.parser;

import org.apache.commons.lang3.StringUtils;
import org.example.error.exception.SyntacticException;
import org.example.error.manager.ErrorManager;
import org.example.lexer.LexerImpl;
import org.example.programstructure.containers.Program;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserErrorManagingTest {

	private static Stream<Arguments> testFullErrorMessages() {
		return Stream.of(
				Arguments.of("main() { a = b }", "While parsing statement << a = b >> at line 1 position 14 given problem was found: SEMICOLON_EXPECTED"),
				Arguments.of("main() { a = }", "While parsing statement << a = >> at line 1 position 12 given problem was found: ASSIGNMENT_EXPRESSION_EXPECTED"),
				Arguments.of("fun() { object.attribute. = 2", "While parsing statement << object.attribute. >> at line 1 position 25 given problem was found: IDENTIFIER_EXPECTED"),
				Arguments.of("main() { jeżeli (a==2) }", "While parsing statement << jeżeli ( a == 2 ) >> at line 1 position 22 given problem was found: CONDITIONAL_STATEMENT_BODY_EXPECTED"),
				Arguments.of("fun() { bool = b > a oraz }", "While parsing statement << bool = b > a oraz >> at line 1 position 22 given problem was found: EXPRESSION_EXPECTED"),
				Arguments.of("fun() { zwróć 2; } fun() { zwróć 3; }", "While parsing class or function definition << fun >> at line 1 position 20 given problem was found: FUNCTION_NAME_NOT_UNIQUE"),
				Arguments.of("klasa { }", "While parsing class or function definition << klasa >> at line 1 position 1 given problem was found: CLASS_NAME_MISSING"),
				Arguments.of("power 2,3", "While parsing statement << power >> at line 1 position 1 given problem was found: OPENING_PARENTHESIS_MISSING"),
				Arguments.of("mult (a,b { return a*b; } ", "While parsing statement << mult ( a , b >> at line 1 position 9 given problem was found: CLOSING_PARENTHESIS_MISSING"),
				Arguments.of("2 + 2 > 3", "Syntactic error of type: EXPRESSION_OUTSIDE_DEFINITION")
		);
	}

	@ParameterizedTest
	@MethodSource
	public void testFullErrorMessages(String inputString, String expectedMessage) {
		Exception exception = assertThrows(SyntacticException.class, () -> readFromString(inputString));
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}

	private static Stream<Arguments> testErrors() {
		return Stream.of(
				Arguments.of("fun() { jeżeli() {}}", "CONDITION_EXPECTED"),
				Arguments.of("fun() { dla w lista() {}", "ITERATOR_EXPECTED"),
				Arguments.of("fun() { dla i lista() {}", "IN_KEYWORD_EXPECTED"),
				Arguments.of("fun() { dla i w {}", "LOOP_RANGE_EXPECTED"),
				Arguments.of("fun() { zwróć ; }", "RETURN_EXPRESSION_EXPECTED"),
				Arguments.of("fun(a, ) {}", "PARAMETER_EXPECTED"),
				Arguments.of("klasa Tkom {} klasa Tkom {}", "CLASS_NAME_NOT_UNIQUE"),
				Arguments.of("fun(a, b, a) {}", "PARAMETER_NAME_NOT_UNIQUE"),
				Arguments.of("klasa Tkom klasa Pipr", "CLASS_BODY_MISSING"),
				Arguments.of("fun() fun2() {}", "FUNCTION_BODY_MISSING"),
				Arguments.of("fun() { napisz(\"Witaj świecie\");", "CLOSING_BRACKET_MISSING")
		);
	}

	@ParameterizedTest
	@MethodSource
	public void testErrors(String inputString, String expectedMessage) {
		Exception exception = assertThrows(SyntacticException.class, () -> readFromString(inputString));
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage.substring(actualMessage.lastIndexOf(StringUtils.SPACE) + 1));
	}

	private static Program readFromString(String input) {
		try (var reader = new BufferedReader(new StringReader(input))) {
			var lexer = new LexerImpl(reader, ErrorManager::handleError);
			var parser = new ParserImpl(lexer, ErrorManager::handleError);
			return parser.parse();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

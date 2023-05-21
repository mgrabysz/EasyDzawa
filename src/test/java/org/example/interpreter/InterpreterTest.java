package org.example.interpreter;

import org.example.error.manager.ErrorManager;
import org.example.lexer.LexerImpl;
import org.example.parser.ParserImpl;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {


	@Test
	void testInterpretFactorial() {
		String path = "src/test/resources/interpreter/factorial.txt";
		String expectedOutput = "24\n120\n720\n";
		String actualOutput = readFromFile(path);
		assertEquals(expectedOutput, actualOutput);
	}

	@Test
	void testInterpretDefinitions() {
		String path = "src/test/resources/interpreter/definitions.txt";
		String expectedOutput = """
                Zdefiniowano właśnie ułamek właściwy
                licznik: 1 mianownik: 2
                rozszerzony licznik: 2 rozszerzony mianownik: 4
                Zdefiniowano właśnie ułamek niewłaściwy
                licznik: 3 mianownik: 2
                rozszerzony licznik: 6 rozszerzony mianownik: 4
                """;
		String actualOutput = readFromFile(path);
		assertEquals(expectedOutput, actualOutput);

	}

	private static String readFromFile(String path) {
		try (FileReader fileReader = new FileReader(path)) {
			var file = new BufferedReader(fileReader);
			var lexer = new LexerImpl(file, ErrorManager::handleError);
			var parser = new ParserImpl(lexer, ErrorManager::handleError);
			var program = parser.parse();
			boolean testingMode = true;
			Interpreter interpreter = new Interpreter(ErrorManager::handleError, testingMode);
			interpreter.execute(program);
			return interpreter.getOutput();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

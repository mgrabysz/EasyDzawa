package org.example;

import org.example.error.manager.ErrorManager;
import org.example.interpreter.Interpreter;
import org.example.lexer.LexerImpl;
import org.example.parser.ParserImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {

		String path = "src/main/resources/factorial.txt";
		try (FileReader fileReader = new FileReader(path)) {
			var file = new BufferedReader(fileReader);
			var lexer = new LexerImpl(file, ErrorManager::handleError);
			var parser = new ParserImpl(lexer, ErrorManager::handleError);
			var program = parser.parse();
			Interpreter interpreter = new Interpreter();
			interpreter.execute(program);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}



	}

}
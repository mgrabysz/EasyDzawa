package org.example;

import org.example.error.manager.ErrorManager;
import org.example.lexer.LexerImpl;
import org.example.parser.Parser;
import org.example.parser.ParserImpl;
import org.example.programstructure.containers.Program;
import org.example.visitor.PrinterVisitor;
import org.example.visitor.Visitor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {

		try (FileReader fileReader = new FileReader("src/main/resources/definitions.txt")) {
			var file = new BufferedReader(fileReader);
			var lexer = new LexerImpl(file, ErrorManager::handleError);
			final Parser parser = new ParserImpl(lexer, ErrorManager::handleError);
			final Program program = parser.parse();
			final Visitor visitor = new PrinterVisitor();
			program.accept(visitor);
		}
	}

}
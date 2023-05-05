package org.example;

import org.example.error.manager.ErrorManager;
import org.example.lexer.Lexer;
import org.example.lexer.LexerImpl;
import org.example.lexer.LexerMock;
import org.example.parser.Parser;
import org.example.parser.ParserImpl;
import org.example.programstructure.containers.Program;
import org.example.token.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
	public static void main(String[] args) throws IOException {
//		List<Token> tokens = new ArrayList<>(Arrays.asList(
//				new TokenIdentifier(new Position(), "multiply"),
//				new TokenSymbol(TokenType.OPEN_PARENTHESIS, new Position()),
//				new TokenIdentifier(new Position(), "x"),
//				new TokenSymbol(TokenType.COMA, new Position()),
//				new TokenIdentifier(new Position(), "y"),
//				new TokenSymbol(TokenType.CLOSE_PARENTHESIS, new Position()),
//				new TokenSymbol(TokenType.OPEN_BRACKET, new Position()),
//				new TokenKeyword(TokenType.RETURN, new Position()),
//				new TokenIdentifier(new Position(), "x"),
//				new TokenSymbol(TokenType.MULTIPLY, new Position()),
//				new TokenIdentifier(new Position(), "y"),
//				new TokenSymbol(TokenType.SEMICOLON, new Position()),
//				new TokenSymbol(TokenType.CLOSE_BRACKET, new Position()),
//				new TokenEOF(new Position())
//		));
//		final Lexer lexer = new LexerMock(tokens);
		List<Token> tokens = new ArrayList<>();
		try (FileReader fileReader = new FileReader("src/main/resources/input.txt")) {
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
		System.out.println(tokens);


		try (FileReader fileReader = new FileReader("src/main/resources/input.txt")) {
			var file = new BufferedReader(fileReader);
			var lexer = new LexerImpl(file, ErrorManager::handleError);
			final Parser parser = new ParserImpl(lexer, ErrorManager::handleError);
			final Program program = parser.parse();
			final Visitor visitor = new PrinterVisitor();
			program.accept(visitor);
		}
	}

}
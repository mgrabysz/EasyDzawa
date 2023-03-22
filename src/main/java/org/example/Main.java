package org.example;

import org.example.token.Token;
import org.example.token.TokenType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) throws IOException {

		try (var file = new BufferedReader(new FileReader("src/main/resources/input.txt"))) {
			var lexer = new EasyLexerImpl(file);
			List<Token> tokens = new ArrayList<>();
			Token token = lexer.next();
			while (token.getType() != TokenType.END_OF_FILE) {
				tokens.add(token);
				token = lexer.next();
			}
			tokens.add(token);
			for (Token t : tokens) {
				System.out.println(t);
			}
		}
	}
}
package org.example;

import org.example.token.Token;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) throws IOException {

		System.out.println("Hello world!");

		try (var file = new BufferedReader(new FileReader("src/main/resources/input.txt"))) {
			var lexer = new EasyLexerImpl(file);
			List<Token> tokens = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				Token token = lexer.next();
				tokens.add(token);
				System.out.println(tokens);
			}
			System.out.println("");
		}
	}
}
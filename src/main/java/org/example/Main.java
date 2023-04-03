package org.example;

import org.example.token.Token;
import org.example.token.TokenGroups;

import java.io.IOException;
import java.util.List;

public class Main {
	public static void main(String[] args) throws IOException {
		final var interpreter = new EasyInterpreter();
		List<Token> tokens = interpreter.readTokens("src/main/resources/input.txt");
		for (Token token : tokens) {
			System.out.println(token);
		}

		System.out.println(TokenGroups.KEYWORDS);
	}
}
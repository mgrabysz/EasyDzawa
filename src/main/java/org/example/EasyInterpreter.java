package org.example;

import org.example.lexer.LexerImpl;
import org.example.token.Token;
import org.example.token.TokenType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EasyInterpreter {

	public List<Token> readTokens(String path) throws IOException {

		List<Token> tokens = new ArrayList<>();
		try (var file = new BufferedReader(new FileReader(path))) {
			var lexer = new LexerImpl(file);
			Token token = lexer.next();
			while (token.getType() != TokenType.END_OF_FILE) {
				tokens.add(token);
				token = lexer.next();
			}
			tokens.add(token);
		}
		return tokens;
	}

}

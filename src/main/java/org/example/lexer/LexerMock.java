package org.example.lexer;

import lombok.RequiredArgsConstructor;
import org.example.token.Token;

import java.util.List;

@RequiredArgsConstructor
public class LexerMock implements Lexer {

	private final List<Token> tokens;

	@Override
	public Token next() {
		if (tokens.size() > 0) {
			return tokens.remove(0);
		}
		return null;
	}
}

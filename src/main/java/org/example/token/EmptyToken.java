package org.example.token;

import org.example.Position;

public class EmptyToken implements Token {
	@Override
	public TokenType getType() {
		return null;
	}

	@Override
	public Position getPosition() {
		return null;
	}

	@Override
	public <V> V getValue() {
		return null;
	}
}

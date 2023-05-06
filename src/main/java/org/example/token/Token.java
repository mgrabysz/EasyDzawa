package org.example.token;

import org.example.commons.Position;

public interface Token {

	TokenType getType();

	Position getPosition();

	<V> V getValue();
}

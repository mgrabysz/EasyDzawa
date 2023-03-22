package org.example.token;

import lombok.ToString;
import org.example.Position;

public interface Token {

	TokenType getType();

	Position getPosition();

	<V> V getValue();
}

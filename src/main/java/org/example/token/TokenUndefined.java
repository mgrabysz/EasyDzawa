package org.example.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.example.commons.Position;

@Getter
@AllArgsConstructor
@ToString
public class TokenUndefined implements Token {

	private final Position position;
	@Override
	public TokenType getType() {
		return null;
	}

	@Override
	public <V> V getValue() {
		return null;
	}
}

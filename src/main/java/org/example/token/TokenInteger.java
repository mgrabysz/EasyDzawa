package org.example.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.Position;

@Getter
@AllArgsConstructor
public class TokenInteger implements Token {

	private final TokenType type = TokenType.INTEGER;
	private final Position position;
	private final Integer value;

}

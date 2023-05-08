package org.example.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.example.commons.Position;

@Getter
@AllArgsConstructor
@ToString
public class TokenInteger implements Token {

	private final TokenType type = TokenType.INTEGER;
	private final Position position;
	private final Integer value;

}

package org.example.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.example.commons.Position;

@Getter
@AllArgsConstructor
@ToString
public class TokenFloat implements Token {

	private final TokenType type = TokenType.FLOAT;
	private final Position position;
	private final Double value;

}

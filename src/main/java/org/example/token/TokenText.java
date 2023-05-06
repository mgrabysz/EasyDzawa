package org.example.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.example.commons.Position;

@Getter
@AllArgsConstructor
@ToString
public class TokenText implements Token {

	private final TokenType type = TokenType.TEXT;
	private final Position position;
	private final String value;

}

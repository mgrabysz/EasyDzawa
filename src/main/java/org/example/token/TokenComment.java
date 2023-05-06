package org.example.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.example.commons.Position;

@Getter
@AllArgsConstructor
@ToString
public class TokenComment implements Token {

	private final TokenType type = TokenType.COMMENT;
	private final Position position;
	private final String value;

}

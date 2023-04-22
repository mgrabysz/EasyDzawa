package org.example.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.example.Position;

@Getter
@AllArgsConstructor
@ToString
public class TokenBool implements Token {

	private final TokenType type = TokenType.BOOL;
	private final Position position;
	private final Boolean value;

}

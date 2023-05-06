package org.example.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.example.commons.Position;

@Getter
@AllArgsConstructor
@ToString
public class TokenKeyword implements Token {

	private final TokenType type;
	private final Position position;

	@Override
	public String getValue() {
		return type.getKeyword();
	}
}

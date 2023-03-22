package org.example.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.example.Position;

@Getter
@ToString
@RequiredArgsConstructor
public class TokenEOF implements Token {

	private final TokenType type = TokenType.END_OF_FILE;
	private final Integer value = null;
	private final Position position;

}

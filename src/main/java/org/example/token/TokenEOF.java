package org.example.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.Position;

@Getter
@RequiredArgsConstructor
public class TokenEOF implements Token {

	private final TokenType type = TokenType.END_OF_FILE;
	private final Integer value = null;
	private final Position position;

}

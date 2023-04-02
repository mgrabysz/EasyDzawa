package org.example.token;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TokenTypeTest {

	@Test
	void tokenKeywordTest() {
		Assertions.assertEquals("klasa", TokenType.CLASS.getKeyword());
		Assertions.assertEquals("nowy", TokenType.NEW_MASCULINE.getKeyword());
		Assertions.assertEquals("nowa", TokenType.NEW_FEMININE.getKeyword());
		Assertions.assertEquals("nowe", TokenType.NEW_NEUTER.getKeyword());
		Assertions.assertNull(TokenType.END_OF_FILE.getKeyword());
		Assertions.assertNull(TokenType.NEW.getKeyword());
	}
}

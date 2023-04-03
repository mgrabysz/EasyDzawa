package org.example.token;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TokenGroupTest {

	@Test
	void keywordsTest() {
		Assertions.assertEquals(TokenType.RETURN, TokenGroups.KEYWORDS.get("zwróć"));
		Assertions.assertEquals(TokenType.FOR, TokenGroups.KEYWORDS.get("dla"));
		Assertions.assertEquals(TokenType.IF, TokenGroups.KEYWORDS.get("jeżeli"));
		Assertions.assertEquals(TokenType.ELSE, TokenGroups.KEYWORDS.get("inaczej"));
		Assertions.assertEquals(TokenType.CLASS, TokenGroups.KEYWORDS.get("klasa"));
		Assertions.assertEquals(TokenType.THIS, TokenGroups.KEYWORDS.get("tenże"));
	}

	@Test
	void boolLiteralsTest() {
		Assertions.assertEquals(TokenType.FALSE, TokenGroups.BOOL_LITERALS.get("fałsz"));
		Assertions.assertEquals(TokenType.TRUE, TokenGroups.BOOL_LITERALS.get("prawda"));
	}
}

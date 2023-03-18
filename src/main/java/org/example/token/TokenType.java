package org.example.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum TokenType {

	COMMENT("//"),
	INTEGER,
	END_OF_FILE;
	private String keyword;
}

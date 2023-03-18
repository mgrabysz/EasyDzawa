package org.example;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Position {

	final int line;
	final int characterNumber;

	public Position() {
		this.line = 0;
		this.characterNumber = -1;
	}

	public Position nextLine() {
		return new Position(this.line + 1, -1);
	}

	public Position nextChar() {
		return new Position(this.line, this.characterNumber + 1);
	}
}

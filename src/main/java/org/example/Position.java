package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Position {

	int lineNumber;
	int characterNumber;

	public Position() {
		this.lineNumber = 1;
		this.characterNumber = 1;
	}

	public void nextLine() {
		this.lineNumber += 1;
		this.characterNumber = 1;
	}

	public void nextChar() {
		this.characterNumber += 1;
	}

	public Position copy() {
		return new Position(this.lineNumber, this.characterNumber);
	}
}

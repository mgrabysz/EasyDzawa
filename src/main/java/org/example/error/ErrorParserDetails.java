package org.example.error;

import org.example.Position;
import org.example.error.enums.ErrorLevel;
import org.example.error.enums.ErrorType;

public record ErrorParserDetails(ErrorType type, Position position, String expression) implements ErrorDetails {

	@Override
	public ErrorLevel level() {
		return ErrorLevel.SYNTACTICAL;
	}

}

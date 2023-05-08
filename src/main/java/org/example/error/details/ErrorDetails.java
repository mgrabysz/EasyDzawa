package org.example.error.details;

import org.example.commons.Position;
import org.example.error.enums.ErrorLevel;
import org.example.error.enums.ErrorType;

public interface ErrorDetails {

	ErrorLevel level();
	ErrorType type();
	Position position();
	String expression();

}

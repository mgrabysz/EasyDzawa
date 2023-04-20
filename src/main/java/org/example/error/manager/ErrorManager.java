package org.example.error.manager;

import org.example.Configuration;
import org.example.error.ErrorDetails;
import org.example.error.enums.ErrorLevel;
import org.example.error.exception.LexicalException;

public class ErrorManager {

	private static final String UNDEFINED_TOKEN_MESSAGE = "Undefined expression: %s at line %d position %d";
	private static final String NUMERIC_LIMIT_EXCEEDED_MESSAGE = "Numeric expression: %s at line %d position %d exceeds limit";
	private static final String IDENTIFIER_LENGTH_EXCEEDED = "Identifier: %s starting at line %d position %d exceeds maximal length";
	private static final String TEXT_LENGTH_EXCEEDED_MESSAGE = "Text: %s starting at line %d position %d exceeds maximal length";
	private static final String COMMENT_LENGTH_EXCEEDED_MESSAGE = "Comment: %s starting at line %d position %d exceeds maximal length";
	private static final String END_OF_FILE_REACHED_MESSAGE = "End of file reached while parsing text: %s starting at line %d position %d";
	private static final String GENERIC_LEXICAL_ERROR_MESSAGE = "Lexical error at line %d position %d";

	public static void handleError(ErrorDetails errorDetails) throws Exception {

		if (errorDetails.level() == ErrorLevel.LEXICAL) {
			handleLexicalError(errorDetails);
		}
	}

	private static void handleLexicalError(ErrorDetails errorDetails) throws LexicalException {
		String errorMessage;
		switch (errorDetails.type()) {
			case UNDEFINED_TOKEN -> {
				errorMessage = UNDEFINED_TOKEN_MESSAGE.formatted(trimExpression(errorDetails.expression()),
						errorDetails.position().getLineNumber(), errorDetails.position().getCharacterNumber());
			}
			case NUMERIC_LIMIT_EXCEEDED -> {
				errorMessage = NUMERIC_LIMIT_EXCEEDED_MESSAGE.formatted(trimExpression(errorDetails.expression()),
						errorDetails.position().getLineNumber(), errorDetails.position().getCharacterNumber());
			}
			case IDENTIFIER_LENGTH_EXCEEDED -> {
				errorMessage = IDENTIFIER_LENGTH_EXCEEDED.formatted(trimExpression(errorDetails.expression()),
						errorDetails.position().getLineNumber(), errorDetails.position().getCharacterNumber());
			}
			case TEXT_LENGTH_EXCEEDED -> {
				errorMessage = TEXT_LENGTH_EXCEEDED_MESSAGE.formatted(trimExpression(errorDetails.expression()),
						errorDetails.position().getLineNumber(), errorDetails.position().getCharacterNumber());
			}
			case COMMENT_LENGTH_EXCEEDED -> {
				errorMessage = COMMENT_LENGTH_EXCEEDED_MESSAGE.formatted(trimExpression(errorDetails.expression()),
						errorDetails.position().getLineNumber(), errorDetails.position().getCharacterNumber());
			}
			case END_OF_FILE_REACHED -> {
				errorMessage = END_OF_FILE_REACHED_MESSAGE.formatted(trimExpression(errorDetails.expression()),
						errorDetails.position().getLineNumber(), errorDetails.position().getCharacterNumber());
			}
			default -> {
				errorMessage = GENERIC_LEXICAL_ERROR_MESSAGE.formatted(
						errorDetails.position().getLineNumber(), errorDetails.position().getCharacterNumber());
			}
		}
		throw new LexicalException(errorMessage);
	}

	private static String trimExpression(String expression) {
		if (expression.length() > Configuration.getErrorMessageExpressionMaxLength()) {
			return expression.substring(0, Configuration.getErrorMessageExpressionMaxLength()) + "...";
		}
		return expression;
	}

}

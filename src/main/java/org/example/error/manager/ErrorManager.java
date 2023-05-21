package org.example.error.manager;

import org.example.Configuration;
import org.example.error.details.ErrorDetails;
import org.example.error.enums.ErrorType;
import org.example.error.exception.LexicalException;
import org.example.error.exception.SemanticException;
import org.example.error.exception.SyntacticException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.example.error.enums.ErrorType.*;

public class ErrorManager {

	private static final String UNDEFINED_TOKEN_MESSAGE = "Undefined expression: %s at line %d position %d";
	private static final String NUMERIC_LIMIT_EXCEEDED_MESSAGE = "Numeric expression: %s at line %d position %d exceeds limit";
	private static final String IDENTIFIER_LENGTH_EXCEEDED_MESSAGE = "Identifier: %s starting at line %d position %d exceeds maximal length";
	private static final String TEXT_LENGTH_EXCEEDED_MESSAGE = "Text: %s starting at line %d position %d exceeds maximal length";
	private static final String COMMENT_LENGTH_EXCEEDED_MESSAGE = "Comment: %s starting at line %d position %d exceeds maximal length";
	private static final String END_OF_FILE_REACHED_MESSAGE = "End of file reached while parsing text: %s starting at line %d position %d";
	private static final String GENERIC_LEXICAL_ERROR_MESSAGE = "Lexical error at line %d position %d";
	private static final String INCORRECT_STATEMENT_MESSAGE = "While parsing statement << %s >> at line %d position %d given problem was found: %s";
	private static final String INCORRECT_DEFINITION_MESSAGE = "While parsing class or function definition << %s >> at line %d position %d given problem was found: %s";
	private static final String MISSING_PARENTHESIS_MESSAGE = "While parsing statement << %s >> at line %d position %d given problem was found: %s";
	private static final String GENERIC_SYNTACTIC_ERROR_MESSAGE = "Syntactic error of type: %s";
    private static final String GENERIC_SEMANTIC_ERROR_MESSAGE = "Semantic error of type: %s: << %s >> at line %d";
    private static final String MAIN_MISSING_ERROR_MESSAGE = "Main function is missing";

	private static final List<ErrorType> incorrectStatementErrors = new ArrayList<>(Arrays.asList(
			SEMICOLON_EXPECTED,
			ASSIGNMENT_EXPRESSION_EXPECTED,
			IDENTIFIER_EXPECTED,
			CONDITION_EXPECTED,
			CONDITIONAL_STATEMENT_BODY_EXPECTED,
			ITERATOR_EXPECTED,
			IN_KEYWORD_EXPECTED,
			LOOP_RANGE_EXPECTED,
			RETURN_EXPRESSION_EXPECTED,
			EXPRESSION_EXPECTED,
			PARAMETER_EXPECTED
	));

	private static final List<ErrorType> incorrectDefinitionErrors = new ArrayList<>(Arrays.asList(
			FUNCTION_NAME_NOT_UNIQUE,
			CLASS_NAME_NOT_UNIQUE,
			PARAMETER_NAME_NOT_UNIQUE,
			CLASS_NAME_MISSING,
			CLASS_BODY_MISSING,
			FUNCTION_BODY_MISSING
	));

	private static final List<ErrorType> missingParenthesisErrors = new ArrayList<>(Arrays.asList(
			OPENING_PARENTHESIS_MISSING,
			CLOSING_PARENTHESIS_MISSING,
			CLOSING_BRACKET_MISSING
	));

	private static final Map<ErrorType, String> lexicalErrorsMessages = Map.ofEntries(
			Map.entry(UNDEFINED_TOKEN, UNDEFINED_TOKEN_MESSAGE),
			Map.entry(NUMERIC_LIMIT_EXCEEDED, NUMERIC_LIMIT_EXCEEDED_MESSAGE),
			Map.entry(IDENTIFIER_LENGTH_EXCEEDED, IDENTIFIER_LENGTH_EXCEEDED_MESSAGE),
			Map.entry(TEXT_LENGTH_EXCEEDED, TEXT_LENGTH_EXCEEDED_MESSAGE),
			Map.entry(COMMENT_LENGTH_EXCEEDED, COMMENT_LENGTH_EXCEEDED_MESSAGE),
			Map.entry(END_OF_FILE_REACHED, END_OF_FILE_REACHED_MESSAGE)
	);

	public static void handleError(ErrorDetails errorDetails) throws Exception {
        switch (errorDetails.level()) {
            case LEXICAL -> handleLexicalError(errorDetails);
            case SYNTACTICAL -> handleSyntacticError(errorDetails);
            case SEMANTIC -> handleSemanticError(errorDetails);
        }
	}

	private static void handleLexicalError(ErrorDetails errorDetails) throws LexicalException {
		String errorMessage = lexicalErrorsMessages.get(errorDetails.type());
		if (errorMessage == null) {
			throw new LexicalException(GENERIC_LEXICAL_ERROR_MESSAGE.formatted(
					errorDetails.position().getLineNumber(), errorDetails.position().getCharacterNumber()));
		}
		throw new LexicalException(errorMessage.formatted(
				trimExpression(errorDetails.expression()), errorDetails.position().getLineNumber(),
				errorDetails.position().getCharacterNumber()));
	}

	private static void handleSyntacticError(ErrorDetails errorDetails) throws SyntacticException {
		String errorMessage;
		ErrorType errorType = errorDetails.type();
		if (incorrectStatementErrors.contains(errorType)) {
			errorMessage = INCORRECT_STATEMENT_MESSAGE;
		} else if (incorrectDefinitionErrors.contains(errorType)) {
			errorMessage = INCORRECT_DEFINITION_MESSAGE;
		} else if (missingParenthesisErrors.contains(errorType)) {
			errorMessage = MISSING_PARENTHESIS_MESSAGE;
		} else {
			throw new SyntacticException(GENERIC_SYNTACTIC_ERROR_MESSAGE.formatted(errorType));
		}
		throw new SyntacticException(errorMessage.formatted(
                trimExpression(errorDetails.expression()),
                errorDetails.position().getLineNumber(),
                errorDetails.position().getCharacterNumber(),
                errorDetails.type()));
	}

    private static void handleSemanticError(ErrorDetails errorDetails) throws SemanticException {
        String errorMessage = switch (errorDetails.type()) {
            case MAIN_FUNCTION_MISSING -> MAIN_MISSING_ERROR_MESSAGE;
            default -> GENERIC_SEMANTIC_ERROR_MESSAGE.formatted(
                    errorDetails.type(),
                    trimExpression(errorDetails.expression()),
                    errorDetails.position().getLineNumber()
            );
        };
        throw new SemanticException(errorMessage);
    }

	private static String trimExpression(String expression) {
		if (expression.length() > Configuration.getErrorMessageExpressionMaxLength()) {
			return expression.substring(0, Configuration.getErrorMessageExpressionMaxLength()) + "...";
		}
		return expression;
	}

}

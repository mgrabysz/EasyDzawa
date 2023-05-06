package org.example.error.enums;

public enum ErrorType {

	// lexical
	UNDEFINED_TOKEN,
	NUMERIC_LIMIT_EXCEEDED,
	TEXT_LENGTH_EXCEEDED,
	IDENTIFIER_LENGTH_EXCEEDED,
	COMMENT_LENGTH_EXCEEDED,
	END_OF_FILE_REACHED,

	// syntactic
	// general
	END_OF_FILE_NOT_PRESENT,

	// missing parenthesis
	OPENING_PARENTHESIS_MISSING,
	CLOSING_PARENTHESIS_MISSING,
	CLOSING_BRACKET_MISSING,

	// ill-defined class/function
	FUNCTION_NAME_NOT_UNIQUE,
	CLASS_NAME_NOT_UNIQUE,
	PARAMETER_NAME_NOT_UNIQUE,
	METHOD_NAME_NOT_UNIQUE,
	CLASS_NAME_MISSING,
	CLASS_BODY_MISSING,
	FUNCTION_BODY_MISSING,

	// statement errors
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


	// semantic
}

package org.example.parser;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.example.commons.ErrorContext;
import org.example.commons.Position;
import org.example.error.ErrorHandler;
import org.example.error.details.ErrorParserDetails;
import org.example.error.enums.ErrorType;
import org.example.error.exception.SyntacticException;
import org.example.lexer.Lexer;
import org.example.programstructure.containers.*;
import org.example.programstructure.expression.*;
import org.example.programstructure.expression.enums.AdditiveType;
import org.example.programstructure.expression.enums.MultiplicativeType;
import org.example.programstructure.expression.enums.RelativeType;
import org.example.programstructure.statement.*;
import org.example.token.Token;
import org.example.commons.TokenGroups;
import org.example.token.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserImpl implements Parser {
	private final Lexer lexer;
	private final ErrorHandler errorHandler;
	private final ErrorContext errorContext;
	private Token currentToken;
	private Token previousToken;

	public ParserImpl(Lexer lexer, ErrorHandler errorHandler) {
		this.lexer = lexer;
		this.errorHandler = errorHandler;
		this.previousToken = null;
		this.currentToken = null;
		this.errorContext = new ErrorContext(new Position(), new StringBuilder());
		nextToken();
	}

	/**
	 * program    = {definition};
	 * definition = function-definition | class-definition
	 *
	 * @return Program
	 */
	@Override
	public Program parse() {
		Map<String, FunctionDefinition> functions = new HashMap<>();
		Map<String, ClassDefinition> classes = new HashMap<>();
		while (parseFunctionDefinition(functions) || parseClassDefinition(classes)) {
			errorContext.reset();
		}
		if (currentToken.getType() != TokenType.END_OF_FILE) {
			handleCriticalError(ErrorType.EXPRESSION_OUTSIDE_DEFINITION, currentToken.getPosition(),
					currentToken.getValue().toString());
		}
		return new Program(functions, classes);
	}

	/**
	 * function-definition = identifier, "(", [parameters-list], ")", block;
	 */
	private boolean parseFunctionDefinition(Map<String, FunctionDefinition> functions) {
		if (!consumeIf(TokenType.IDENTIFIER)) {
			return false;
		}
		final String functionName = previousToken.getValue();
		if (functions.containsKey(functionName)) {
			handleCriticalError(ErrorType.FUNCTION_NAME_NOT_UNIQUE, errorContext.getPosition(), functionName);
		}
		if (!consumeIf(TokenType.OPEN_PARENTHESIS)) {
			handleCriticalError(ErrorType.OPENING_PARENTHESIS_MISSING, errorContext.getPosition(), errorContext.getContext());
		}
		final List<Parameter> parameters = parseParameters();
		if (!consumeIf(TokenType.CLOSE_PARENTHESIS)) {
			handleCriticalError(ErrorType.CLOSING_PARENTHESIS_MISSING, errorContext.getPosition(), errorContext.getContext());
		}
		final Block block = parseBlock(errorContext.getContext());
		if (block == null) {
			handleCriticalError(ErrorType.FUNCTION_BODY_MISSING, errorContext.getPosition(), errorContext.getContext());
		}
		functions.put(functionName, new FunctionDefinition(functionName, parameters, block));
		return true;
	}

	/**
	 * parameters-list = identifier, {",", identifier};
	 */
	private List<Parameter> parseParameters() {
		List<Parameter> parameters = new ArrayList<>();
		List<String> names = new ArrayList<>();
		Parameter parameter = parseParameter();
		if (parameter == null) {
			return parameters;
		}
		parameters.add(parameter);
		names.add(parameter.name());
		while (consumeIf(TokenType.COMA)) {
			parameter = parseParameter();
			if (parameter == null) {
				handleCriticalError(ErrorType.PARAMETER_EXPECTED, errorContext.getPosition(), errorContext.getContext());
			} else if (names.contains(parameter.name())) {
				handleCriticalError(ErrorType.PARAMETER_NAME_NOT_UNIQUE, errorContext.getPosition(), errorContext.getContext());
			} else {
				parameters.add(parameter);
				names.add(parameter.name());
			}
		}
		return parameters;
	}

	private Parameter parseParameter() {
		if (!consumeIf(TokenType.IDENTIFIER)) {
			return null;
		}
		String name = previousToken.getValue();
		return new Parameter(name);
	}

	/**
	 * class-definition = class-keyword, identifier, class-body;
	 */
	private boolean parseClassDefinition(Map<String, ClassDefinition> classes) {
		if (!consumeIf(TokenType.CLASS)) {
			return false;
		}
		if (!consumeIf(TokenType.IDENTIFIER)) {
			handleCriticalError(ErrorType.CLASS_NAME_MISSING, errorContext.getPosition(), errorContext.getContext());
		}
		final String className = previousToken.getValue();
		if (classes.containsKey(className)) {
			handleCriticalError(ErrorType.CLASS_NAME_NOT_UNIQUE, errorContext.getPosition(), className);
		}
		final Map<String, FunctionDefinition> methods = parseClassBody(className);
		if (methods == null) {
			handleCriticalError(ErrorType.CLASS_BODY_MISSING, errorContext.getPosition(), errorContext.getContext());
		} else {
			classes.put(className, new ClassDefinition(className, methods));
		}
		return true;
	}

	/**
	 * class-body = "{", {function-definition}, "}";
	 */
	private Map<String, FunctionDefinition> parseClassBody(String className) {
		if (!consumeIf(TokenType.OPEN_BRACKET)) {
			return null;
		}
		final Map<String, FunctionDefinition> methods = new HashMap<>();
		while (true) {
			if (!parseFunctionDefinition(methods)) break;
		}
		if (!consumeIf(TokenType.CLOSE_BRACKET)) {
			handleCriticalError(ErrorType.CLOSING_BRACKET_MISSING, errorContext.getPosition(),
					String.join(StringUtils.SPACE, TokenType.CLASS.getKeyword(), className, "{"));
		}
		return methods;
	}

	/**
	 * block = "{", {statement}, "}";
	 */
	private Block parseBlock(String preceding) {
		if (!consumeIf(TokenType.OPEN_BRACKET)) {
			return null;
		}
		preceding = preceding.concat("{");
		final List<Statement> statements = new ArrayList<>();
		Statement statement = parseStatement();
		while (statement != null) {
			statements.add(statement);
			statement = parseStatement();
		}
		if (!consumeIf(TokenType.CLOSE_BRACKET)) {
			handleCriticalError(ErrorType.CLOSING_BRACKET_MISSING, errorContext.getPosition(), preceding);
		}
		return new Block(statements);
	}

	/**
	 * statement = object-access, [assignment], ";"
	 * 			 | if-statement
	 * 			 | for-statement
	 * 			 | return-statement
	 */
	private Statement parseStatement() {
		errorContext.reset();
		Statement statement = parseObjectAccessOrAssignment();
		if (statement != null) {
			return statement;
		}
		statement = parseIfStatement();
		if (statement != null) {
			return statement;
		}
		statement = parseForStatement();
		if (statement != null) {
			return statement;
		}
		statement = parseReturnStatement();
		return statement;
	}

	/**
	 * statement  = object-access, [assignment], ";"
	 * assignment = ("=" | "+=" | "-="), expression;
	 */
	private Statement parseObjectAccessOrAssignment() {
		Expression objectAccess = parseObjectAccess();
		if (objectAccess == null) {
			return null;
		}
		Statement statement;
		if (consumeIf(TokenType.ASSIGN)) {
			statement = parseAssignmentStatement(objectAccess);
		} else if (consumeIf(TokenType.ADD_AND_ASSIGN)) {
			statement = parseModifyAndAssignStatement(objectAccess, AdditiveType.ADD);
		} else if (consumeIf(TokenType.SUBTRACT_AND_ASSIGN)) {
			statement = parseModifyAndAssignStatement(objectAccess, AdditiveType.SUBTRACT);
		} else {
			statement = (Statement) objectAccess;
		}
		if (!consumeIf(TokenType.SEMICOLON)) {
			handleNonCriticalError(ErrorType.SEMICOLON_EXPECTED, errorContext.getPosition(), errorContext.getContext());
		}
		return statement;
	}

	private AssignmentStatement parseAssignmentStatement(Expression objectAccess) {
		Expression expression = parseExpression();
		if (expression == null) {
			handleCriticalError(ErrorType.ASSIGNMENT_EXPRESSION_EXPECTED, errorContext.getPosition(), errorContext.getContext());
		}
		return new AssignmentStatement(objectAccess, expression);
	}

	private ModifyAndAssignStatement parseModifyAndAssignStatement(Expression objectAccess, AdditiveType additiveType) {
		Expression expression = parseExpression();
		if (expression == null) {
			handleCriticalError(ErrorType.ASSIGNMENT_EXPRESSION_EXPECTED, errorContext.getPosition(), errorContext.getContext());
		}
		return new ModifyAndAssignStatement(additiveType, objectAccess, expression);
	}

	/**
	 * object-access = (title | this-keyword), {".", title};
	 * title         = identifier, ["(", arguments-list, ")"];
	 * Note that term "object access" is not exhausting, as it may refer to
	 * object field, object method call, function call or a single variable
	 */
	private Expression parseObjectAccess() {
		Expression left = parseSelfAccess();
		if (left == null) {
			left = parseIdentifierOrFunCall();
		}
		if (left == null) {
			return null;
		}
		while (consumeIf(TokenType.DOT)) {
			Expression right = parseIdentifierOrFunCall();
			if (right == null) {
				handleCriticalError(ErrorType.IDENTIFIER_EXPECTED, errorContext.getPosition(), errorContext.getContext());
			}
			left = new ObjectAccess(left, right);
		}
		return left;
	}

	private Expression parseSelfAccess() {
		final Position position = currentToken.getPosition();
		if (!consumeIf(TokenType.THIS)) {
			return null;
		}
		return new SelfAccess(position);
	}

	/**
	 * title = identifier, ["(", arguments-list, ")"];
	 */
	private Expression parseIdentifierOrFunCall() {
		if (!consumeIf(TokenType.IDENTIFIER)) {
			return null;
		}
		final String name = previousToken.getValue();
		final Position position = previousToken.getPosition();
		final Expression expression = parseRestOfFunCall(name, position);
		if (expression == null) {
			return new IdentifierExpression(name, position);
		}
		return expression;
	}

	private Expression parseRestOfFunCall(String name, Position position) {
		if (!consumeIf(TokenType.OPEN_PARENTHESIS)) {
			return null;
		}
		List<Expression> arguments = parseArguments();
		if (!consumeIf(TokenType.CLOSE_PARENTHESIS)) {
			handleCriticalError(ErrorType.CLOSING_PARENTHESIS_MISSING, errorContext.getPosition(), errorContext.getContext());
		}
		return new FunctionCallExpression(name, arguments, position);
	}

	/**
	 * arguments-list = expression, {",", expression};
	 */
	private List<Expression> parseArguments() {
		List<Expression> arguments = new ArrayList<>();
		Expression argument = parseExpression();
		if (argument == null) {
			return arguments;
		}
		arguments.add(argument);
		while (consumeIf(TokenType.COMA)) {
			argument = parseExpression();
			if (argument == null) {
				handleCriticalError(ErrorType.EXPRESSION_EXPECTED, errorContext.getPosition(), errorContext.getContext());
			}
			arguments.add(argument);
		}
		return arguments;
	}

	/**
	 * if-statement = if-keyword, "(", expression, ")", block, [else-keyword, block];
	 */
	private IfStatement parseIfStatement() {
		if (!consumeIf(TokenType.IF)) {
			return null;
		}
		if (!consumeIf(TokenType.OPEN_PARENTHESIS)) {
			handleNonCriticalError(ErrorType.OPENING_PARENTHESIS_MISSING, errorContext.getPosition(), errorContext.getContext());
		}
		Expression condition = parseOrExpression();
		if (condition == null) {
			handleCriticalError(ErrorType.CONDITION_EXPECTED, errorContext.getPosition(), errorContext.getContext());
		}
		if (!consumeIf(TokenType.CLOSE_PARENTHESIS)) {
			handleNonCriticalError(ErrorType.CLOSING_PARENTHESIS_MISSING, errorContext.getPosition(), errorContext.getContext());
		}
		String conditionContext = errorContext.getContext();
		Block blockIfTrue = parseBlock(conditionContext);
		if (blockIfTrue == null) {
			handleCriticalError(ErrorType.CONDITIONAL_STATEMENT_BODY_EXPECTED, errorContext.getPosition(), conditionContext);
		}
		if (!consumeIf(TokenType.ELSE)) {
			return new IfStatement(condition, blockIfTrue, null);
		}
		Block elseBlock = parseBlock(conditionContext);
		if (elseBlock == null) {
			handleCriticalError(ErrorType.CONDITIONAL_STATEMENT_BODY_EXPECTED, errorContext.getPosition(), conditionContext);
		}
		return new IfStatement(condition, blockIfTrue, elseBlock);
	}

	/**
	 * for-statement = for-keyword, identifier, in-keyword, object-access, block;
	 */
	private ForStatement parseForStatement() {
		if (!consumeIf(TokenType.FOR)) {
			return null;
		}
		if (!consumeIf(TokenType.IDENTIFIER)) {
			handleCriticalError(ErrorType.ITERATOR_EXPECTED, errorContext.getPosition(), errorContext.getContext());
		}
		final String iteratorName = previousToken.getValue();
		if (!consumeIf(TokenType.IN)) {
			handleNonCriticalError(ErrorType.IN_KEYWORD_EXPECTED, errorContext.getPosition(), errorContext.getContext());
		}
		Expression range = parseObjectAccess();
		if (range == null) {
			handleCriticalError(ErrorType.LOOP_RANGE_EXPECTED, errorContext.getPosition(), errorContext.getContext());
		}
		Block block = parseBlock(errorContext.getContext());
		if (block == null) {
			handleCriticalError(ErrorType.CONDITIONAL_STATEMENT_BODY_EXPECTED, errorContext.getPosition(), errorContext.getContext());
		}
		return new ForStatement(iteratorName, range, block);
	}

	/**
	 * return-statement = return-keyword, [expression], ";";
	 */
	private ReturnStatement parseReturnStatement() {
		if (!consumeIf(TokenType.RETURN)) {
			return null;
		}
		Expression expression = parseExpression();
		if (expression == null) {
			handleCriticalError(ErrorType.RETURN_EXPRESSION_EXPECTED, errorContext.getPosition(), errorContext.getContext());
		}
		if (!consumeIf(TokenType.SEMICOLON)) {
			handleNonCriticalError(ErrorType.SEMICOLON_EXPECTED, errorContext.getPosition(), errorContext.getContext());
		}
		return new ReturnStatement(expression);
	}

	/**
	 * expression = or-expression;
	 */
	private Expression parseExpression() {
		return parseOrExpression();
	}

	/**
	 * or-expression = and-expression, {or-keyword, and-expression};
	 */
	private Expression parseOrExpression() {
		Expression left = parseAndExpression();
		if (left == null) {
			return null;
		}
		while (consumeIf(TokenType.OR)) {
			Expression right = parseAndExpression();
			if (right == null) {
				handleCriticalError(ErrorType.EXPRESSION_EXPECTED, errorContext.getPosition(), errorContext.getContext());
			}
			left = new OrExpression(left, right);
		}
		return left;
	}

	/**
	 * and-expression = relative-expression, {and-keyword, relative-expression};
	 */
	private Expression parseAndExpression() {
		Expression left = parseRelativeExpression();
		if (left == null) {
			return null;
		}
		while (consumeIf(TokenType.AND)) {
			Expression right = parseRelativeExpression();
			if (right == null) {
				handleCriticalError(ErrorType.EXPRESSION_EXPECTED, errorContext.getPosition(), errorContext.getContext());
			}
			left = new AndExpression(left, right);
		}
		return left;
	}

	/**
	 * relative-expression = arithmetic-expression, [relative-operator, arithmetic-expression];
	 */
	private Expression parseRelativeExpression() {
		Expression left = parseArithmeticExpression();
		if (left == null) {
			return null;
		}
		RelativeType relativeType;
		if ((relativeType = TokenGroups.RELATIVE_OPERATORS.get(currentToken.getType())) != null) {
			consumeCurrent();
			Expression right = parseArithmeticExpression();
			if (right == null) {
				handleCriticalError(ErrorType.EXPRESSION_EXPECTED, errorContext.getPosition(), errorContext.getContext());
			}
			left = new RelativeExpression(relativeType, left, right);
		}
		return left;
	}

	/**
	 * arithmetic-expression = multiplicative-expression, {("+" | "-"), multiplicative-expression};
	 */

	private Expression parseArithmeticExpression() {
		Expression left = parseMultiplicativeExpression();
		if (left == null) {
			return null;
		}
		AdditiveType additiveType;
		while ((additiveType = TokenGroups.ADDITIVE_OPERATORS.get(currentToken.getType())) != null) {
			consumeCurrent();
			Expression right = parseMultiplicativeExpression();
			if (right == null) {
				handleCriticalError(ErrorType.EXPRESSION_EXPECTED, errorContext.getPosition(), errorContext.getContext());
			}
			left = new ArithmeticExpression(additiveType, left, right);
		}
		return left;
	}

	/**
	 * multiplicative-expression = factor, {("*" | "/"), factor};
	 */
	private Expression parseMultiplicativeExpression() {
		Expression left = parseNegatedFactor();
		if (left == null) {
			return null;
		}
		MultiplicativeType multiplicativeType;
		while ((multiplicativeType = TokenGroups.MULTIPLICATIVE_OPERATORS.get(currentToken.getType())) != null) {
			consumeCurrent();
			Expression right = parseNegatedFactor();
			if (right == null) {
				handleCriticalError(ErrorType.EXPRESSION_EXPECTED, errorContext.getPosition(), errorContext.getContext());
			}
			left = new MultiplicativeExpression(multiplicativeType, left, right);
		}
		return left;
	}


	private Expression parseNegatedFactor() {
		boolean negated = false;
		Position position = currentToken.getPosition();
		if (consumeIf(TokenType.NOT) || consumeIf(TokenType.SUBTRACT)) {
			negated = true;
		}
		Expression expression = parseFactor();
		if (negated && expression == null) {
			handleCriticalError(ErrorType.EXPRESSION_EXPECTED, errorContext.getPosition(), errorContext.getContext());
		}
		if (negated) {
			return new NegatedExpression(expression, position);
		}
		return expression;
	}

	/**
	 * factor = [negation], (literal | object-access | "(", expression, ")");
	 */
	private Expression parseFactor() {
		Expression expression = parseLiteral();
		if (expression != null) {
			return expression;
		}
		expression = parseObjectAccess();
		if (expression != null) {
			return expression;
		}
		if (!consumeIf(TokenType.OPEN_PARENTHESIS)) {
			return null;
		}
		expression = parseExpression();
		if (!consumeIf(TokenType.CLOSE_PARENTHESIS)) {
			handleCriticalError(ErrorType.EXPRESSION_EXPECTED, errorContext.getPosition(), errorContext.getContext());
		}
		return expression;
	}

	/**
	 * literal = integer | float | bool | text;
	 */
	private Expression parseLiteral() {
		Expression expression = null;
		if (consumeIf(TokenType.INTEGER)) {
			expression = new LiteralInteger(previousToken.getValue(), previousToken.getPosition());
		}
		if (consumeIf(TokenType.FLOAT)) {
			expression = new LiteralFloat(previousToken.getValue(), previousToken.getPosition());
		}
		if (consumeIf(TokenType.BOOL)) {
			expression = new LiteralBool(previousToken.getValue(), previousToken.getPosition());
		}
		if (consumeIf(TokenType.TEXT)) {
			expression = new LiteralText(previousToken.getValue(), previousToken.getPosition());
		}
		return expression;
	}

	private void nextToken() {
		this.previousToken = this.currentToken;
		this.currentToken = lexer.next();
		while (this.currentToken.getType() == TokenType.COMMENT) {
			this.currentToken = lexer.next();
		}
	}

	private boolean consumeIf(TokenType tokenType) {
		if (this.currentToken.getType() == tokenType) {
			errorContext.update(this.currentToken);
			nextToken();
			return true;
		}
		return false;
	}

	private void consumeCurrent() {
		errorContext.update(this.currentToken);
		nextToken();
	}

	@SneakyThrows
	private void handleCriticalError(ErrorType errorType, Position position, String expression) {
		ErrorParserDetails errorDetails = new ErrorParserDetails(errorType, position, expression.stripTrailing());
		errorHandler.handleError(errorDetails);
		throw new SyntacticException("Syntax error");
	}

	@SneakyThrows
	private void handleNonCriticalError(ErrorType errorType, Position position, String expression) {
		ErrorParserDetails errorDetails = new ErrorParserDetails(errorType, position, expression.stripTrailing());
		errorHandler.handleError(errorDetails);
	}

}

package org.example.parser;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.example.Position;
import org.example.error.ErrorHandler;
import org.example.error.ErrorParserDetails;
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
import org.example.token.TokenGroups;
import org.example.token.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ParserImpl implements Parser {

	private final Lexer lexer;
	private final ErrorHandler errorHandler;
	private Token currentToken;
	private String statementDraft;

	public ParserImpl(Lexer lexer, ErrorHandler errorHandler) {
		this.lexer = lexer;
		this.errorHandler = errorHandler;
		resetStatementDraft();
		nextToken();
	}

	@Override
	public Program parse() {
		HashMap<String, FunctionDefinition> functions = new HashMap<>();
		HashMap<String, ClassDefinition> classes = new HashMap<>();

		while (true) {
			if (!parseFunctionDefinition(functions) && !parseClassDefinition(classes)) break;
		}
		if (currentToken.getType() != TokenType.END_OF_FILE) {
			handleLightError(ErrorType.END_OF_FILE_NOT_PRESENT, null, null);
		}
		return new Program(functions, classes);
	}

	/**
	 * function-definition = identifier, "(", [parameters-list], ")", block;
	 */
	private boolean parseFunctionDefinition(HashMap<String, FunctionDefinition> functions) {
		if (currentToken.getType() != TokenType.IDENTIFIER) {
			return false;
		}
		final String name = currentToken.getValue();
		final Position position = currentToken.getPosition();
		nextToken();
		if (!consumeIf(TokenType.OPEN_PARENTHESIS)) {
			handleError(ErrorType.OPENING_PARENTHESIS_MISSING, position, name);
		}
		final List<Parameter> parameters = parseParameters();
		if (!consumeIf(TokenType.CLOSE_PARENTHESIS)) {
			handleError(ErrorType.CLOSING_PARENTHESIS_MISSING, position,
					buildFunctionHeaderMessage(name, parameters, false));
		}
		final String functionHeader = buildFunctionHeaderMessage(name, parameters, true);
		final Block block = parseBlock(functionHeader);
		if (block == null) {
			handleError(ErrorType.FUNCTION_BODY_MISSING, position, functionHeader);
		}
		if (functions.containsKey(name)) {
			handleError(ErrorType.FUNCTION_NAME_NOT_UNIQUE, position, name);
		}
		functions.put(name, new FunctionDefinition(name, parameters, block));
		return true;
	}

	/**
	 * class-definition = class-keyword, identifier, class-body;
	 */
	private boolean parseClassDefinition(HashMap<String, ClassDefinition> classes) {
		final Position position = currentToken.getPosition();
		if (!consumeIf(TokenType.CLASS)) {
			return false;
		}
		if (currentToken.getType() != TokenType.IDENTIFIER) {
			handleError(ErrorType.CLASS_NAME_MISSING, position, TokenType.CLASS.getKeyword());
		}
		final String name = currentToken.getValue();
		nextToken();
		final ClassBody classBody = parseClassBody(name, position);
		if (classBody == null) {
			handleError(ErrorType.CLASS_BODY_MISSING, position, name);
		}
		if (classes.containsKey(name)) {
			handleError(ErrorType.CLASS_NAME_NOT_UNIQUE, position, name);
		}
		classes.put(name, new ClassDefinition(name, classBody.methods()));
		return true;
	}

	/**
	 * class-body = "{", {function-definition}, "}";
	 */
	private ClassBody parseClassBody(String className, Position position) {
		if (!consumeIf(TokenType.OPEN_BRACKET)) {
			return null;
		}
		final HashMap<String, FunctionDefinition> methods = new HashMap<>();
		while (true) {
			if (!parseFunctionDefinition(methods)) break;
		}
		if (!consumeIf(TokenType.CLOSE_BRACKET)) {
			handleError(ErrorType.CLOSING_BRACKET_MISSING, position,
					StringUtils.join("klasa ", className, " {"));
		}
		return new ClassBody(methods);
	}

	/**
	 * block = "{", {statement}, "}";
	 */
	private Block parseBlock(String preceding) {
		Position position = currentToken.getPosition();
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
			handleError(ErrorType.CLOSING_BRACKET_MISSING, position, preceding);
		}
		return new Block(statements);
	}

	/**
	 * statement = object-access, [assignment], ";"
	 *           | if-statement
	 *           | for-statement
	 *           | return-statement
	 */
	private Statement parseStatement() {
		resetStatementDraft();
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
		Position position = objectAccess.position();
		Statement statement = (Statement) objectAccess;
		if (consumeIf(TokenType.ASSIGN)) {
			statement = parseAssignmentStatement(objectAccess);
		} else if (consumeIf(TokenType.ADD_AND_ASSIGN)) {
			statement = parseAddAndAssignStatement(objectAccess);
		} else if (consumeIf(TokenType.SUBTRACT_AND_ASSIGN)) {
			statement = parseSubtractAndAssignStatement(objectAccess);
		}
		if (!consumeIf(TokenType.SEMICOLON)) {
			handleLightError(ErrorType.SEMICOLON_MISSING, position, statementDraft);
		}
		return statement;
	}

	private AssignmentStatement parseAssignmentStatement(Expression objectAccess) {
		Expression expression = parseExpression();
		if (expression == null) {
			handleError(ErrorType.ASSIGNMENT_MISSING_EXPRESSION, objectAccess.position(), statementDraft);
		}
		return new AssignmentStatement(objectAccess, expression);
	}

	private AddAndAssignStatement parseAddAndAssignStatement(Expression objectAccess) {
		Expression expression = parseExpression();
		if (expression == null) {
			handleError(ErrorType.ASSIGNMENT_MISSING_EXPRESSION, objectAccess.position(), statementDraft);
		}
		return new AddAndAssignStatement(objectAccess, expression);
	}

	private SubtractAndAssignStatement parseSubtractAndAssignStatement(Expression objectAccess) {
		Expression expression = parseExpression();
		if (expression == null) {
			handleError(ErrorType.ASSIGNMENT_MISSING_EXPRESSION, objectAccess.position(), statementDraft);
		}
		return new SubtractAndAssignStatement(objectAccess, expression);
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
				handleError(ErrorType.IDENTIFIER_EXPECTED, left.position(), statementDraft);
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
		if (currentToken.getType() != TokenType.IDENTIFIER) {
			return null;
		}
		final String name = currentToken.getValue();
		final Position position = currentToken.getPosition();
		nextToken();
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
			handleError();
		}
		return new FunctionCallExpression(name, arguments, position);
	}

	/**
	 * if-statement = if-keyword, "(", expression, ")", block, [else-keyword, block];
	 */
	private IfStatement parseIfStatement() {
		final Position position = currentToken.getPosition();
		if (!consumeIf(TokenType.IF)) {
			return null;
		}
		if (!consumeIf(TokenType.OPEN_PARENTHESIS)) {
			handleLightError(ErrorType.OPENING_PARENTHESIS_MISSING, position, statementDraft);
		}
		Expression condition = parseOrExpression();
		if (condition == null) {
			handleError(ErrorType.CONDITION_MISSING, position, statementDraft);
		}
		if (!consumeIf(TokenType.CLOSE_PARENTHESIS)) {
			handleLightError(ErrorType.CLOSING_PARENTHESIS_MISSING, position, statementDraft);
		}
		Block blockIfTrue = parseBlock(statementDraft);
		if (blockIfTrue == null) {
			handleError(ErrorType.CONDITIONAL_STATEMENT_BODY_MISSING, position, statementDraft);
		}
		if (!consumeIf(TokenType.ELSE)) {
			return new IfStatement(condition, blockIfTrue, null);
		}
		Block elseBlock = parseBlock(statementDraft);
		if (elseBlock == null) {
			handleError(ErrorType.CONDITIONAL_STATEMENT_BODY_MISSING, position, statementDraft);
		}
		return new IfStatement(condition, blockIfTrue, elseBlock);
	}

	/**
	 * for-statement = for-keyword, identifier, in-keyword, object-access, block;
	 */
	private ForStatement parseForStatement() {
		final Position position = currentToken.getPosition();
		if (!consumeIf(TokenType.FOR)) {
			return null;
		}
		if (currentToken.getType() != TokenType.IDENTIFIER) {
			handleError(ErrorType.ITERATOR_MISSING, position, statementDraft);
		}
		final String iteratorName = currentToken.getValue();
		nextToken();
		if (!consumeIf(TokenType.IN)) {
			handleLightError(ErrorType.IN_KEYWORD_MISSING, position, statementDraft);
		}
		Expression range = parseObjectAccess();
		if (range == null) {
			handleError(ErrorType.LOOP_RANGE_MISSING, position, statementDraft);
		}
		Block block = parseBlock(statementDraft);
		if (block == null) {
			handleError(ErrorType.CONDITIONAL_STATEMENT_BODY_MISSING, position, statementDraft);
		}
		return new ForStatement(iteratorName, range, block);
	}

	/**
	 * return-statement = return-keyword, [expression], ";";
	 */
	private ReturnStatement parseReturnStatement() {
		final Position position = currentToken.getPosition();
		if (!consumeIf(TokenType.RETURN)) {
			return null;
		}
		Expression expression = parseExpression();
		if (expression == null) {
			handleError(ErrorType.RETURN_MISSING_EXPRESSION, position, statementDraft);
		}
		if (!consumeIf(TokenType.SEMICOLON)) {
			handleLightError(ErrorType.SEMICOLON_MISSING, position, statementDraft);
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
				handleError();
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
				handleError();
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
		TokenType tokenType = currentToken.getType();
		RelativeType relativeType = TokenGroups.RELATIVE_OPERATORS.get(tokenType);
		if (relativeType != null) {
			nextToken();
			Expression right = parseArithmeticExpression();
			if (right == null) {
				handleError();
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
		TokenType tokenType = currentToken.getType();
		AdditiveType additiveType = TokenGroups.ADDITIVE_OPERATORS.get(tokenType);
		while (additiveType != null) {
			nextToken();
			Expression right = parseMultiplicativeExpression();
			if (right == null) {
				handleError();
			}
			left = new ArithmeticExpression(additiveType, left, right);
			tokenType = currentToken.getType();
			additiveType = TokenGroups.ADDITIVE_OPERATORS.get(tokenType);
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
		TokenType tokenType = currentToken.getType();
		MultiplicativeType multiplicativeType = TokenGroups.MULTIPLICATIVE_OPERATORS.get(tokenType);
		while (multiplicativeType != null) {
			nextToken();
			Expression right = parseNegatedFactor();
			if (right == null) {
				handleError();
			}
			left = new MultiplicativeExpression(multiplicativeType, left, right);
			tokenType = currentToken.getType();
			multiplicativeType = TokenGroups.MULTIPLICATIVE_OPERATORS.get(tokenType);
		}
		return left;
	}


	private Expression parseNegatedFactor() {
		boolean negated = false;
		Position position = null;
		if (currentToken.getType() == TokenType.SUBTRACT || currentToken.getType() == TokenType.NOT) {
			position = currentToken.getPosition();
			negated = true;
			nextToken();
		}
		Expression expression = parseFactor();
		if (negated && expression == null) {
			handleError();
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
			handleError();
		}
		return expression;
	}

	/**
	 * literal = integer | float | bool | text;
	 */
	private Expression parseLiteral() {
		Expression expression = null;
		if (currentToken.getType() == TokenType.INTEGER) {
			expression = new LiteralInteger(currentToken.getValue(), currentToken.getPosition());
			nextToken();
		}
		if (currentToken.getType() == TokenType.FLOAT) {
			expression = new LiteralFloat(currentToken.getValue(), currentToken.getPosition());
			nextToken();
		}
		if (currentToken.getType() == TokenType.BOOL) {
			expression = new LiteralBool(currentToken.getValue(), currentToken.getPosition());
			nextToken();
		}
		if (currentToken.getType() == TokenType.TEXT) {
			expression = new LiteralText(currentToken.getValue(), currentToken.getPosition());
			nextToken();
		}
		return expression;
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
				handleError();
			}
			if (names.contains(parameter.name())) {
				handleError();
			}
			parameters.add(parameter);
			names.add(parameter.name());
		}
		return parameters;
	}

	private Parameter parseParameter() {
		if (currentToken.getType() != TokenType.IDENTIFIER) {
			return null;
		}
		String name = currentToken.getValue();
		nextToken();
		return new Parameter(name);
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
				handleError();
			}
			arguments.add(argument);
		}
		return arguments;
	}

	private void nextToken() {
		this.currentToken = lexer.next();
		while (this.currentToken.getType() == TokenType.COMMENT) {
			this.currentToken = lexer.next();
		}
	}

	private boolean consumeIf(TokenType tokenType) {
		if (this.currentToken.getType() == tokenType) {
			updateStatementDraft();
			nextToken();
			return true;
		}
		return false;
	}

	private void updateStatementDraft() {
		if (currentToken.getType() == TokenType.DOT && statementDraft.length() > 0) {
			statementDraft = statementDraft.substring(0, statementDraft.length()-1)
					.concat(".");
		} else {
			statementDraft = statementDraft.concat(currentToken.getValue() + " ");
		}
	}
	private void resetStatementDraft() {
		this.statementDraft = StringUtils.EMPTY;
	}

	private String buildFunctionHeaderMessage(String functionName, List<Parameter> parameters, boolean closed) {
		String parametersString = parameters.stream()
				.map(Parameter::name)
				.collect(Collectors.joining(","));
		String functionHeader = StringUtils.join(functionName, "(", parametersString);
		if (closed) {
			functionHeader = functionHeader.concat(")");
		}
		return functionHeader;
	}

	@SneakyThrows
	private void handleError(ErrorType errorType, Position position, String expression) {
		ErrorParserDetails errorDetails = new ErrorParserDetails(errorType, position, expression);
		errorHandler.handleError(errorDetails);
		throw new SyntacticException("Syntax error");
	}

	@SneakyThrows
	private void handleError() {
		throw new SyntacticException("Syntax error");
	}

	@SneakyThrows
	private void handleLightError(ErrorType errorType, Position position, String expression) {
		ErrorParserDetails errorDetails = new ErrorParserDetails(errorType, position, expression);
		errorHandler.handleError(errorDetails);
	}
}

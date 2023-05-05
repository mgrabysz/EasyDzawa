package org.example.parser;

import lombok.SneakyThrows;
import org.example.Position;
import org.example.error.ErrorHandler;
import org.example.error.ErrorParserDetails;
import org.example.error.enums.ErrorType;
import org.example.error.exception.SyntacticalException;
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

public class ParserImpl implements Parser {

	private final Lexer lexer;
	private final ErrorHandler errorHandler;
	private Token currentToken;

	public ParserImpl(Lexer lexer, ErrorHandler errorHandler) {
		this.lexer = lexer;
		this.errorHandler = errorHandler;
		nextToken();
	}

	@Override
	public Program parse() {
		nextToken();
		HashMap<String, FunctionDefinition> functions = new HashMap<>();
		HashMap<String, ClassDefinition> classes = new HashMap<>();

		while(true) {
			if (!parseFunctionDefinition(functions) && !parseClassDefinition(classes)) break;
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
		nextToken();
		if (!consumeIf(TokenType.OPEN_PARENTHESIS)) {
			handleError();
		}
		final List<Parameter> parameters = parseParameters();
		if (!consumeIf(TokenType.CLOSE_PARENTHESIS)) {
			handleError();
		}
		final Block block = parseBlock();
		if (block == null) {
			handleError();
		}
		if (functions.containsKey(name)) {
			handleError();
		}
		functions.put(name, new FunctionDefinition(name, parameters, block));
		return true;
	}

	/**
	 * class-definition = class-keyword, identifier, class-body;
	 */
	private boolean parseClassDefinition(HashMap<String, ClassDefinition> classes) {
		if (!consumeIf(TokenType.CLASS)) {
			return false;
		}
		if (currentToken.getType() != TokenType.IDENTIFIER) {
			handleError();
		}
		final String name = currentToken.getValue();
		nextToken();
		final ClassBody classBody = parseClassBody();
		if (classBody == null) {
			handleError();
		}
		if (classes.containsKey(name)) {
			handleError();
		}
		classes.put(name, new ClassDefinition(name, classBody.methods()));
		return true;
	}

	/**
	 * class-body = "{", {function-definition}, "}";
	 */
	private ClassBody parseClassBody() {
		if (!consumeIf(TokenType.OPEN_BRACKET)) {
			return null;
		}
		final HashMap<String, FunctionDefinition> methods = new HashMap<>();
		while (true) {
			if (!parseFunctionDefinition(methods)) break;
		}
		if (!consumeIf(TokenType.CLOSE_BRACKET)) {
			handleError();
		}
		return new ClassBody(methods);
	}

	/**
	 * block = "{", {statement}, "}";
	 */
	private Block parseBlock() {
		if (!consumeIf(TokenType.OPEN_BRACKET)) {
			return null;
		}
		final List<Statement> statements = new ArrayList<>();
		Statement statement = parseStatement();
		while (statement != null) {
			statements.add(statement);
			statement = parseStatement();
		}
		if (!consumeIf(TokenType.CLOSE_BRACKET)) {
			handleError();
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
	 * object-access = title, {".", title};
	 * title         = identifier, ["(", arguments-list, ")"];
	 * assignment    = ("=" | "+=" | "-="), expression;
	 * Note that term "object access" is not exhausting, as it may refer to
	 * object field, object method call, function call or a single variable
	 */
	private Statement parseObjectAccessOrAssignment() {
		Expression objectAccess = parseObjectAccess();
		if (objectAccess == null) {
			return null;
		}
		Statement statement = (Statement) objectAccess;
		if (consumeIf(TokenType.ASSIGN)) {
			statement = parseAssignmentStatement(objectAccess);
		} else if (consumeIf(TokenType.ADD_AND_ASSIGN)) {
			statement = parseAddAndAssignStatement(objectAccess);
		} else if (consumeIf(TokenType.SUBTRACT_AND_ASSIGN)) {
			statement = parseSubtractAndAssignStatement(objectAccess);
		}
		if (!consumeIf(TokenType.SEMICOLON)) {
			handleError();
		}
		return statement;
	}

	private AssignmentStatement parseAssignmentStatement(Expression objectAccess) {
		Expression expression = parseExpression();
		if (expression == null) {
			handleError();
		}
		return new AssignmentStatement(objectAccess, expression);
	}

	private AddAndAssignStatement parseAddAndAssignStatement(Expression objectAccess) {
		Expression expression = parseExpression();
		if (expression == null) {
			handleError();
		}
		return new AddAndAssignStatement(objectAccess, expression);
	}

	private SubtractAndAssignStatement parseSubtractAndAssignStatement(Expression objectAccess) {
		Expression expression = parseExpression();
		if (expression == null) {
			handleError();
		}
		return new SubtractAndAssignStatement(objectAccess, expression);
	}

	private Expression parseObjectAccess() {
		Expression left = parseIdentifierOrFunCall();
		if (left == null) {
			return null;
		}
		while (consumeIf(TokenType.DOT)) {
			Expression right = parseIdentifierOrFunCall();
			if (right == null) {
				handleError();
			}
			left = new ObjectAccess(left, right);
		}
		return left;
	}

	/**
	 * if-statement = if-keyword, "(", expression, ")", block, [else-keyword, block];
	 */
	private IfStatement parseIfStatement() {
		if (!consumeIf(TokenType.IF)) {
			return null;
		}
		if (!consumeIf(TokenType.OPEN_PARENTHESIS)) {
			handleLightError();
		}
		Expression condition = parseOrExpression();
		if (condition == null) {
			handleError();
		}
		if (!consumeIf(TokenType.CLOSE_PARENTHESIS)) {
			handleLightError();
		}
		Block blockIfTrue = parseBlock();
		if (blockIfTrue == null) {
			handleError();
		}
		if (!consumeIf(TokenType.ELSE)) {
			return new IfStatement(condition, blockIfTrue, null);
		}
		Block elseBlock = parseBlock();
		if (elseBlock == null) {
			handleError();
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
		if (currentToken.getType() != TokenType.IDENTIFIER) {
			handleError();
		}
		final String iteratorName = currentToken.getValue();
		nextToken();
		if (!consumeIf(TokenType.IN)) {
			handleError();
		}
		Expression range = parseObjectAccess();
		if (range == null) {
			handleError();
		}
		Block block = parseBlock();
		if (block == null) {
			handleError();
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
			handleError();
		}
		if (!consumeIf(TokenType.SEMICOLON)) {
			handleLightError();
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
		}
		if (currentToken.getType() == TokenType.FLOAT) {
			expression = new LiteralFloat(currentToken.getValue(), currentToken.getPosition());
		}
		if (currentToken.getType() == TokenType.BOOL) {
			expression = new LiteralBool(currentToken.getValue(), currentToken.getPosition());
		}
		if (currentToken.getType() == TokenType.TEXT) {
			expression = new LiteralText(currentToken.getValue(), currentToken.getPosition());
		}
		nextToken();
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

	private Token nextToken() {
		this.currentToken = lexer.next();
		while (this.currentToken.getType() == TokenType.COMMENT) {
			this.currentToken = lexer.next();
		}
		return this.currentToken;
	}

	private boolean consumeIf(TokenType tokenType) {
		if (this.currentToken.getType() == tokenType) {
			nextToken();
			return true;
		}
		return false;
	}

	@SneakyThrows
	private void handleError(ErrorType errorType, Position position, String expression) {
		ErrorParserDetails errorDetails = new ErrorParserDetails(errorType, position, expression);
		errorHandler.handleError(errorDetails);
		throw new SyntacticalException("Syntax error");
	}

	@SneakyThrows
	private void handleError() {
		throw new SyntacticalException("Syntax error");
	}

	private void handleLightError() {}
}

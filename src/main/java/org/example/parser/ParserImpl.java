package org.example.parser;

import org.example.lexer.Lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParserImpl implements Parser {

	private Lexer lexer;

	@Override
	public Program parse() {
		HashMap<String, FunctionDefinition> functions = new HashMap<>();
		HashMap<String, ClassDefinition> classes = new HashMap<>();

		while(parseFunDef(functions) || parseClassDef(classes)) {

		}

		return new Program(functions, classes);
	}

	private boolean parseFunDef(HashMap<String, FunctionDefinition> functions) {

		// parsing tokens
		String name = "";
		functions.put(name, new FunctionDefinition());
		return false;
	}

	private boolean parseClassDef(HashMap<String, ClassDefinition> classes) {
		return false;
	}

	private List<Parameter> parseParams() {
		List<Parameter> parameters = new ArrayList<>();
		/*
		param = parseParameter();
		if (param != null) {
			if (param == null) {
					handleError
				} else if (param name exists) {error}
				else {parameters.add(param);}

			while (TokenType.COMA) {
				param = parseParameter();
				if (param == null) {
					handleError
				} else if (param name exists) {error}
				else {parameters.add(param);}
			}
		}
		 */
		return parameters;
	}

	private Block parseBlock() {
		/*
		if (!consumeIf(Left bracket) {return null }
		statements = new List<Statement>;
		while( statement = parseStatement()) { statements.add(statement)}
		if (!ConsumeIf(Right bracket)) {error}
		 */
		return new Block();
	}

	private Statement parseStatement() {
		return parseIfStatement() /*|| parseWhileStatement() || parseAssignOrFunctionCallStatement() */;
	}

	private IfStatement parseIfStatement() {
		/*
		if (!ConsumeIf(IF)) {return null;}
		if (!ConsumeIf(Left parenthesis)) { zgłaszamy brak nawiasu otwieracjącego }
		condition = parseCondition();
		if (condition == null) { gruby błąd }
		 */
		return new IfStatement(/* condition, blockTrue, elseBlock*/);
	}

	private Expression parseCondition() {
		/*
		left = parseAndCondition();
		if (left == null) { return null }
		while (consumeIf(OR)) {
			right = parseAndCondition();
			if (right == null) { błąd składniowy }
			left = new AndCondition(left, right);
		}
		return left;
		 */
		return null;
	}

	private Expression parseNegated() {
		/*
		negated = false;
		if (consumeIf(MINUS)) { negated = true }
		expression = parseTerm();
		if (negated && expression == null) { błąd składniowy }
		if (negated) {return new NegateExpression(expression);
		else {return expression}
		 */
		return null;
	}

	private Expression parseIdentifierOrFunCall() {
		/*
		if (Token.Type != Identifier) { return null }
		name = Token.value
		lexer.nextToken();

		expression = parseRestOfFunCall(name);
		if (expression == null) { return new IdentifierExpression(name); }
		return expression;
		 */
		return null;
	}

	private Expression parseRestOfFunCall(String name) {
		return null;
		/*
		if (!consumeIf(left parenthesis)) { return null; }
		arguments = parseArguments()
		return new FunCallExpression(name, arguments);
		 */
	}
}

package org.example;

import org.apache.commons.lang3.StringUtils;
import org.example.programstructure.containers.*;
import org.example.programstructure.expression.*;
import org.example.programstructure.statement.*;

public class PrinterVisitor implements Visitor {

	private int spaces = 0;

	@Override
	public void accept(Program program) {
		print(program);
		spaces += 2;
		for (FunctionDefinition functionDefinition : program.functionDefinitions().values()) {
			functionDefinition.accept(this);
		}
		for (ClassDefinition classDefinition : program.classDefinitions().values()) {
			classDefinition.accept(this);
		}
		spaces -= 2;
	}

	@Override
	public void accept(FunctionDefinition functionDefinition) {
		print(functionDefinition);
		spaces += 2;
		for (Parameter parameter : functionDefinition.parameters()) {
			parameter.accept(this);
		}
		functionDefinition.block().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(ClassDefinition classDefinition) {
		print(classDefinition);
		spaces += 2;
		for (FunctionDefinition functionDefinition : classDefinition.methods().values()) {
			functionDefinition.accept(this);
		}
		spaces -= 2;
	}

	@Override
	public void accept(Block block) {
		print(block);
		spaces += 2;
		for (Statement statement : block.statements()) {
			statement.accept(this);
		}
		spaces -= 2;
	}

	@Override
	public void accept(ClassBody classBody) {
		print(classBody);
	}

	@Override
	public void accept(Parameter parameter) {
		print(parameter);
	}

	@Override
	public void accept(OrExpression expression) {
		print(expression);
		spaces += 2;
		expression.left().accept(this);
		expression.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(AndExpression expression) {
		print(expression);
		spaces += 2;
		expression.left().accept(this);
		expression.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(RelativeExpression expression) {
		print(expression);
		spaces += 2;
		expression.left().accept(this);
		expression.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(ArithmeticExpression expression) {
		print(expression);
		spaces += 2;
		expression.left().accept(this);
		expression.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(MultiplicativeExpression expression) {
		print(expression);
		spaces += 2;
		expression.left().accept(this);
		expression.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(FunctionCallExpression expression) {
		print(expression);
		spaces += 2;
		for (Expression argument : expression.arguments()) {
			argument.accept(this);
		}
		spaces -= 2;
	}

	@Override
	public void accept(IdentifierExpression expression) {
		print(expression);
	}

	@Override
	public void accept(NegatedExpression expression) {
		print(expression);
	}

	@Override
	public void accept(LiteralBool expression) {
		print(expression);
	}

	@Override
	public void accept(LiteralFloat expression) {
		print(expression);
	}

	@Override
	public void accept(LiteralInteger expression) {
		print(expression);
	}

	@Override
	public void accept(LiteralText expression) {
		print(expression);
	}

	@Override
	public void accept(SelfAccess expression) {
		print(expression);
	}

	@Override
	public void accept(AddAndAssignStatement statement) {
		print(statement);
		spaces += 2;
		statement.objectAccess().accept(this);
		statement.expression().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(AssignmentStatement statement) {
		print(statement);
		spaces += 2;
		statement.objectAccess().accept(this);
		statement.expression().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(ForStatement statement) {
		print(statement);
		spaces += 2;
		statement.range().accept(this);
		statement.block().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(IfStatement statement) {
		print(statement);
		spaces += 2;
		statement.condition().accept(this);
		statement.blockIfTrue().accept(this);
		if (statement.elseBlock() != null) {
			statement.elseBlock().accept(this);
		}
		spaces -= 2;
	}

	@Override
	public void accept(ObjectAccess statement) {
		print(statement);
		spaces += 2;
		statement.left().accept(this);
		statement.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(ReturnStatement statement) {
		print(statement);
		spaces += 2;
		statement.expression().accept(this);
	}

	@Override
	public void accept(SubtractAndAssignStatement statement) {
		print(statement);
		spaces += 2;
		statement.objectAccess().accept(this);
		statement.expression().accept(this);
		spaces -= 2;
	}

	private String space() {
		return "-".repeat(spaces);
	}

	private void print(Object object) {
		System.out.println(StringUtils.left(space() + object.toString(), 300));
	}

}

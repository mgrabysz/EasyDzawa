package org.example;

import org.example.programstructure.containers.*;
import org.example.programstructure.expression.*;
import org.example.programstructure.statement.*;

public class PrinterVisitor implements Visitor {

	private int spaces = 0;

	@Override
	public void accept(Program program) {
		System.out.println(space() + program);
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
		System.out.println(space() + functionDefinition);
		spaces += 2;
		for (Parameter parameter : functionDefinition.parameters()) {
			parameter.accept(this);
		}
		functionDefinition.block().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(ClassDefinition classDefinition) {
		System.out.println(space() + classDefinition);
		spaces += 2;
		for (FunctionDefinition functionDefinition : classDefinition.methods().values()) {
			functionDefinition.accept(this);
		}
		spaces -= 2;
	}

	@Override
	public void accept(Block block) {
		System.out.println(space() + block);
		spaces += 2;
		for (Statement statement : block.statements()) {
			statement.accept(this);
		}
		spaces -= 2;
	}

	@Override
	public void accept(ClassBody classBody) {
		System.out.println(space() + classBody);
	}

	@Override
	public void accept(Parameter parameter) {
		System.out.println(space() + parameter);
	}

	@Override
	public void accept(OrExpression expression) {
		System.out.println(space() + expression);
		spaces += 2;
		expression.left().accept(this);
		expression.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(AndExpression expression) {
		System.out.println(space() + expression);
		spaces += 2;
		expression.left().accept(this);
		expression.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(RelativeExpression expression) {
		System.out.println(space() + expression);
		spaces += 2;
		expression.left().accept(this);
		expression.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(ArithmeticExpression expression) {
		System.out.println(space() + expression);
		spaces += 2;
		expression.left().accept(this);
		expression.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(MultiplicativeExpression expression) {
		System.out.println(space() + expression);
		spaces += 2;
		expression.left().accept(this);
		expression.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(FunctionCallExpression expression) {
		System.out.println(space() + expression);
		spaces += 2;
		for (Expression argument : expression.arguments()) {
			argument.accept(this);
		}
		spaces -= 2;
	}

	@Override
	public void accept(IdentifierExpression expression) {
		System.out.println(space() + expression);
	}

	@Override
	public void accept(NegatedExpression expression) {
		System.out.println(space() + expression);
	}

	@Override
	public void accept(LiteralBool expression) {
		System.out.println(space() + expression);
	}

	@Override
	public void accept(LiteralFloat expression) {
		System.out.println(space() + expression);
	}

	@Override
	public void accept(LiteralInteger expression) {
		System.out.println(space() + expression);
	}

	@Override
	public void accept(LiteralText expression) {
		System.out.println(space() + expression);
	}

	@Override
	public void accept(AddAndAssignStatement statement) {
		System.out.println(space() + statement);
		spaces += 2;
		statement.objectAccess().accept(this);
		statement.expression().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(AssignmentStatement statement) {
		System.out.println(space() + statement);
		spaces += 2;
		statement.objectAccess().accept(this);
		statement.expression().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(ForStatement statement) {
		System.out.println(space() + statement);
		spaces += 2;
		statement.range().accept(this);
		statement.block().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(IfStatement statement) {
		System.out.println(space() + statement);
		spaces += 2;
		statement.condition().accept(this);
		statement.blockIfTrue().accept(this);
		statement.elseBlock().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(ObjectAccess statement) {
		System.out.println(space() + statement);
		spaces += 2;
		statement.left().accept(this);
		statement.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void accept(ReturnStatement statement) {
		System.out.println(space() + statement);
		spaces += 2;
		statement.expression().accept(this);
	}

	@Override
	public void accept(SubtractAndAssignStatement statement) {
		System.out.println(space() + statement);
		spaces += 2;
		statement.objectAccess().accept(this);
		statement.expression().accept(this);
		spaces -= 2;
	}

	private String space() {
		return "-".repeat(spaces);
	}
}

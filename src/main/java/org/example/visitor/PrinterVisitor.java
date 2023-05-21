package org.example.visitor;

import org.apache.commons.lang3.StringUtils;
import org.example.programstructure.containers.*;
import org.example.programstructure.expression.*;
import org.example.programstructure.statement.*;

public class PrinterVisitor implements Visitor {

	private int spaces = 0;

	@Override
	public void visit(Program program) {
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
	public void visit(FunctionDefinition functionDefinition) {
		print(functionDefinition);
		spaces += 2;
		for (Parameter parameter : functionDefinition.parameters()) {
			parameter.accept(this);
		}
		functionDefinition.block().accept(this);
		spaces -= 2;
	}

	@Override
	public void visit(ClassDefinition classDefinition) {
		print(classDefinition);
		spaces += 2;
		for (FunctionDefinition functionDefinition : classDefinition.methods().values()) {
			functionDefinition.accept(this);
		}
		spaces -= 2;
	}

	@Override
	public void visit(Block block) {
		print(block);
		spaces += 2;
		for (Statement statement : block.statements()) {
			statement.accept(this);
		}
		spaces -= 2;
	}

	@Override
	public void visit(Parameter parameter) {
		print(parameter);
	}

	@Override
	public void visit(OrExpression expression) {
		print(expression);
		spaces += 2;
		expression.left().accept(this);
		expression.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void visit(AndExpression expression) {
		print(expression);
		spaces += 2;
		expression.left().accept(this);
		expression.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void visit(RelationalExpression expression) {
		print(expression);
		spaces += 2;
		expression.left().accept(this);
		expression.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void visit(ArithmeticExpression expression) {
		print(expression);
		spaces += 2;
		expression.left().accept(this);
		expression.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void visit(MultiplicativeExpression expression) {
		print(expression);
		spaces += 2;
		expression.left().accept(this);
		expression.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void visit(FunctionCallExpression expression) {
		print(expression);
		spaces += 2;
		for (Expression argument : expression.arguments()) {
			argument.accept(this);
		}
		spaces -= 2;
	}

	@Override
	public void visit(IdentifierExpression expression) {
		print(expression);
	}

	@Override
	public void visit(NegatedExpression expression) {
		print(expression);
	}

	@Override
	public void visit(LiteralBool expression) {
		print(expression);
	}

	@Override
	public void visit(LiteralFloat expression) {
		print(expression);
	}

	@Override
	public void visit(LiteralInteger expression) {
		print(expression);
	}

	@Override
	public void visit(LiteralText expression) {
		print(expression);
	}

	@Override
	public void visit(SelfAccess expression) {
		print(expression);
	}

	@Override
	public void visit(ModifyAndAssignStatement statement) {
		print(statement);
		spaces += 2;
		statement.objectAccess().accept(this);
		statement.expression().accept(this);
		spaces -= 2;
	}

	@Override
	public void visit(AssignmentStatement statement) {
		print(statement);
		spaces += 2;
		statement.left().accept(this);
		statement.expression().accept(this);
		spaces -= 2;
	}

	@Override
	public void visit(ForStatement statement) {
		print(statement);
		spaces += 2;
		statement.range().accept(this);
		statement.block().accept(this);
		spaces -= 2;
	}

	@Override
	public void visit(IfStatement statement) {
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
	public void visit(ObjectAccess statement) {
		print(statement);
		spaces += 2;
		statement.left().accept(this);
		statement.right().accept(this);
		spaces -= 2;
	}

	@Override
	public void visit(ReturnStatement statement) {
		print(statement);
		spaces += 2;
		statement.expression().accept(this);
	}


	private String space() {
		return "-".repeat(spaces);
	}

	private void print(Object object) {
		System.out.println(StringUtils.left(space() + object.toString(), 300));
	}

}

package org.example;

import org.example.programstructure.containers.*;
import org.example.programstructure.expression.*;
import org.example.programstructure.statement.*;

public interface Visitor {

	// containers
	void accept(Program program);
	void accept(FunctionDefinition functionDefinition);
	void accept(ClassDefinition classDefinition);
	void accept(Block block);
	void accept(ClassBody classBody);
	void accept(Parameter parameter);

	// expressions
	void accept(OrExpression expression);
	void accept(AndExpression expression);
	void accept(RelativeExpression expression);
	void accept(ArithmeticExpression expression);
	void accept(MultiplicativeExpression expression);
	void accept(FunctionCallExpression expression);
	void accept(IdentifierExpression expression);
	void accept(NegatedExpression expression);
	void accept(LiteralBool expression);
	void accept(LiteralFloat expression);
	void accept(LiteralInteger expression);
	void accept(LiteralText expression);

	// statements
	void accept(AddAndAssignStatement statement);
	void accept(AssignmentStatement statement);
	void accept(ForStatement statement);
	void accept(IfStatement statement);
	void accept(ObjectAccess statement);
	void accept(ReturnStatement statement);
	void accept(SubtractAndAssignStatement statement);
}

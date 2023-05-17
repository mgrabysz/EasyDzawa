package org.example.visitor;

import org.example.programstructure.containers.*;
import org.example.programstructure.expression.*;
import org.example.programstructure.statement.*;

public interface Visitor {

	// containers
	void visit(Program program);
	void visit(FunctionDefinition functionDefinition);
	void visit(ClassDefinition classDefinition);
	void visit(Block block);
	void visit(Parameter parameter);

	// expressions
	void visit(OrExpression expression);
	void visit(AndExpression expression);
	void visit(RelativeExpression expression);
	void visit(ArithmeticExpression expression);
	void visit(MultiplicativeExpression expression);
	void visit(FunctionCallExpression expression);
	void visit(IdentifierExpression expression);
	void visit(NegatedExpression expression);
	void visit(LiteralBool expression);
	void visit(LiteralFloat expression);
	void visit(LiteralInteger expression);
	void visit(LiteralText expression);
	void visit(SelfAccess expression);

	// statements
	void visit(ModifyAndAssignStatement statement);
	void visit(AssignmentStatement statement);
	void visit(ForStatement statement);
	void visit(IfStatement statement);
	void visit(ObjectAccess statement);
	void visit(ReturnStatement statement);
}

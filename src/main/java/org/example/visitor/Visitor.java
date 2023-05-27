package org.example.visitor;

import org.example.interpreter.AbortFunction;
import org.example.interpreter.PrintFunction;
import org.example.interpreter.environment.ProgramHolder;
import org.example.programstructure.containers.*;
import org.example.programstructure.expression.*;
import org.example.programstructure.statement.*;

public interface Visitor {

	// containers
	void visit(Program program);
	void visit(UserFunctionDefinition functionDefinition);
	void visit(ClassDefinition classDefinition);
	void visit(Block block);
	void visit(Parameter parameter);

	// expressions
	void visit(OrExpression expression);
	void visit(AndExpression expression);
	void visit(RelationalExpression expression);
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

    // external
    void visit(PrintFunction printFunction);
    void visit(AbortFunction abortFunction);
    void visit(ProgramHolder programHolder);
}

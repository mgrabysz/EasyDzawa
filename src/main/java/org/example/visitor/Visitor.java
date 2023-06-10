package org.example.visitor;

import org.example.interpreter.builtins.*;
import org.example.programstructure.containers.*;
import org.example.programstructure.expression.*;
import org.example.programstructure.statement.*;

public interface Visitor {

	// containers
	void visit(Program program);
	void visit(UserFunctionDefinition functionDefinition);
	void visit(UserClassDefinition userClassDefinition);
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

    // built-ins
    void visit(PrintFunction printFunction);
    void visit(AbortFunction abortFunction);
    void visit(RangeFunction rangeFunction);
    void visit(ListDefinition listDefinition);
    void visit(ListConstructor listConstructor);
    void visit(AppendMethod method);
    void visit(GetMethod method);
    void visit(RemoveMethod method);
    void visit(LengthMethod method);

}

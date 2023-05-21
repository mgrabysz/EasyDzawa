package org.example.programstructure.statement;

import org.example.visitor.Visitor;
import org.example.programstructure.expression.Expression;

public record AssignmentStatement(Expression left, Expression right) implements Statement {

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}

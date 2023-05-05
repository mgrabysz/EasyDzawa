package org.example.programstructure.statement;

import org.example.Visitor;
import org.example.programstructure.expression.Expression;

public record AssignmentStatement(Expression objectAccess, Expression expression) implements Statement {

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

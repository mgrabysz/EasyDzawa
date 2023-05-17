package org.example.programstructure.statement;

import org.example.visitor.Visitor;
import org.example.programstructure.expression.Expression;

public record ReturnStatement(Expression expression) implements Statement {

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}

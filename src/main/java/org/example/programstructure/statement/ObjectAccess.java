package org.example.programstructure.statement;

import org.example.commons.Position;
import org.example.visitor.Visitor;
import org.example.programstructure.expression.Expression;

public record ObjectAccess(Expression left, Expression right) implements Expression, Statement {

	@Override
	public Position position() {
		return left.position();
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}

package org.example.programstructure.expression;

import org.example.commons.Position;
import org.example.visitor.Visitor;

public record OrExpression(Expression left, Expression right) implements Expression {

	@Override
	public Position position() {
		return left.position();
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

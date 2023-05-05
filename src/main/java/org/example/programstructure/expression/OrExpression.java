package org.example.programstructure.expression;

import org.example.Position;

public record OrExpression(Expression left, Expression right) implements Expression {

	@Override
	public Position position() {
		return left.position();
	}

}

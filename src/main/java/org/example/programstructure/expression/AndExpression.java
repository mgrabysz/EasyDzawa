package org.example.programstructure.expression;

import org.example.Position;

public record AndExpression(Expression left, Expression right) implements Expression {

	@Override
	public Position position() {
		return left.position();
	}

}

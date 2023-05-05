package org.example.programstructure.statement;

import org.example.Position;
import org.example.programstructure.expression.Expression;

public record ObjectAccess(Expression left, Expression right) implements Expression, Statement {

	@Override
	public Position position() {
		return left.position();
	}

}

package org.example.programstructure.expression;

import org.example.commons.Position;
import org.example.visitor.Visitor;
import org.example.programstructure.expression.enums.RelationalType;

public record RelationalExpression(RelationalType relationalType, Expression left, Expression right) implements Expression {

	@Override
	public Position position() {
		return left.position();
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}

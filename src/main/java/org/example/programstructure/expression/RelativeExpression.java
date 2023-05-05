package org.example.programstructure.expression;

import org.example.Position;
import org.example.Visitor;
import org.example.programstructure.expression.enums.RelativeType;

public record RelativeExpression(RelativeType relativeType, Expression left, Expression right) implements Expression {

	@Override
	public Position position() {
		return left.position();
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

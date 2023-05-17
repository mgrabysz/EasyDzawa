package org.example.programstructure.expression;

import org.example.commons.Position;
import org.example.visitor.Visitor;

public record LiteralInteger(Integer value, Position position) implements Expression {

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}

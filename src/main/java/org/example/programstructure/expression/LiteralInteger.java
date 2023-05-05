package org.example.programstructure.expression;

import org.example.Position;
import org.example.Visitor;

public record LiteralInteger(Integer value, Position position) implements Expression {

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

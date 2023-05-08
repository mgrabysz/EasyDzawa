package org.example.programstructure.expression;

import org.example.commons.Position;
import org.example.visitor.Visitor;

public record SelfAccess(Position position) implements Expression {

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

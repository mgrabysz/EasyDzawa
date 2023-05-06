package org.example.programstructure.expression;

import org.example.Position;
import org.example.Visitor;
import org.example.programstructure.statement.Statement;

public record IdentifierExpression(String name, Position position) implements Expression, Statement {

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

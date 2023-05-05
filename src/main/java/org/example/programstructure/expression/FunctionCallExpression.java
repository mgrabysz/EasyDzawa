package org.example.programstructure.expression;

import org.example.Position;
import org.example.Visitor;

import java.util.List;

public record FunctionCallExpression(String name, List<Expression> arguments, Position position) implements Expression {

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

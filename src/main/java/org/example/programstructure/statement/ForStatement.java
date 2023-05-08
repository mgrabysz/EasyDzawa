package org.example.programstructure.statement;

import org.example.visitor.Visitor;
import org.example.programstructure.containers.Block;
import org.example.programstructure.expression.Expression;

public record ForStatement(String iteratorName, Expression range, Block block) implements Statement {

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

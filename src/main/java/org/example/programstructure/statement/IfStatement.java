package org.example.programstructure.statement;

import org.example.visitor.Visitor;
import org.example.programstructure.containers.Block;
import org.example.programstructure.expression.Expression;

public record IfStatement(Expression condition, Block blockIfTrue, Block elseBlock) implements Statement {

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

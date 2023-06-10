package org.example.programstructure.statement;

import org.example.commons.Position;
import org.example.visitor.Visitor;
import org.example.programstructure.containers.Block;
import org.example.programstructure.expression.Expression;

public record IfStatement(Expression condition, Block blockIfTrue, Block elseBlock, Position position) implements Statement {

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}

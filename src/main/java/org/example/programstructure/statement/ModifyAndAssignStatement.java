package org.example.programstructure.statement;

import org.example.programstructure.expression.enums.AdditiveType;
import org.example.visitor.Visitor;
import org.example.programstructure.expression.Expression;

public record ModifyAndAssignStatement(AdditiveType additiveType, Expression left, Expression right) implements Statement {

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}

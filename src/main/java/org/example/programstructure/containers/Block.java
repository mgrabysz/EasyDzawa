package org.example.programstructure.containers;

import org.example.visitor.Visitable;
import org.example.visitor.Visitor;
import org.example.programstructure.statement.Statement;

import java.util.List;

public record Block(List<Statement> statements) implements Visitable {

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

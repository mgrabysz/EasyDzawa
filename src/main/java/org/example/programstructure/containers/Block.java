package org.example.programstructure.containers;

import org.example.Visitable;
import org.example.Visitor;
import org.example.programstructure.statement.Statement;

import java.util.List;

public record Block(List<Statement> statements) implements Visitable {

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

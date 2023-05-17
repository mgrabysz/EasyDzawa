package org.example.programstructure.containers;

import org.example.visitor.Visitable;
import org.example.visitor.Visitor;

public record Parameter(String name) implements Visitable {

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}

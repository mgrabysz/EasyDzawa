package org.example.programstructure.containers;

import org.example.Visitable;
import org.example.Visitor;

public record Parameter(String name) implements Visitable {

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

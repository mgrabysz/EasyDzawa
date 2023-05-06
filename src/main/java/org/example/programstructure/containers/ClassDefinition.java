package org.example.programstructure.containers;

import org.example.Visitable;
import org.example.Visitor;

import java.util.Map;

public record ClassDefinition(String name, Map<String, FunctionDefinition> methods) implements Visitable {

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

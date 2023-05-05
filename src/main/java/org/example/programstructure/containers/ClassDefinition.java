package org.example.programstructure.containers;

import org.example.Visitable;
import org.example.Visitor;

import java.util.HashMap;

public record ClassDefinition(String name, HashMap<String, FunctionDefinition> methods) implements Visitable {

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

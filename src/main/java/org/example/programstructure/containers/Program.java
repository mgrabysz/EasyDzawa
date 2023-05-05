package org.example.programstructure.containers;

import org.example.Visitable;
import org.example.Visitor;

import java.util.HashMap;

public record Program(HashMap<String, FunctionDefinition> functionDefinitions,
					  HashMap<String, ClassDefinition> classDefinitions) implements Visitable {

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

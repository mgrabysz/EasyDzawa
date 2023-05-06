package org.example.programstructure.containers;

import org.example.Visitable;
import org.example.Visitor;

import java.util.Map;

public record Program(Map<String, FunctionDefinition> functionDefinitions,
					  Map<String, ClassDefinition> classDefinitions) implements Visitable {

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

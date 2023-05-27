package org.example.programstructure.containers;

import org.example.visitor.Visitable;
import org.example.visitor.Visitor;

import java.util.Map;

public record Program(Map<String, UserFunctionDefinition> functionDefinitions,
					  Map<String, ClassDefinition> classDefinitions) implements Visitable {

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}

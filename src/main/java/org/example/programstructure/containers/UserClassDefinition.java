package org.example.programstructure.containers;

import org.example.commons.Position;
import org.example.visitor.Visitor;

import java.util.Map;

public record UserClassDefinition(String name, Map<String, FunctionDefinition> methods, Position position) implements ClassDefinition {

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}

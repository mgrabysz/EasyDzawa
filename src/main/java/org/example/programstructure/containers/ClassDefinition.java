package org.example.programstructure.containers;

import org.example.commons.Position;
import org.example.visitor.Visitable;
import org.example.visitor.Visitor;

import java.util.Map;

public record ClassDefinition(String name, Map<String, UserFunctionDefinition> methods, Position position) implements Visitable {

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}

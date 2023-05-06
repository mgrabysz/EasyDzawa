package org.example.programstructure.containers;

import org.example.visitor.Visitable;
import org.example.visitor.Visitor;

import java.util.Map;

public record ClassBody(Map<String, FunctionDefinition> methods) implements Visitable {

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

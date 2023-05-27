package org.example.programstructure.containers;

import org.example.visitor.Visitor;

import java.util.List;

public record UserFunctionDefinition(String name, List<Parameter> parameters, Block block) implements FunctionDefinition {

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}

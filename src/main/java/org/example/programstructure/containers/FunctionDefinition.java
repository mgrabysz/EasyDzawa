package org.example.programstructure.containers;

import org.example.visitor.Visitable;
import org.example.visitor.Visitor;

import java.util.List;

public record FunctionDefinition(String name, List<Parameter> parameters, Block block) implements Visitable {

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

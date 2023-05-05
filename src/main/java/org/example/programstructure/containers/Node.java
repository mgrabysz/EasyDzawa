package org.example.programstructure.containers;

import org.example.Visitor;

public class Node implements Visitable {

	@Override
	public void accept(Visitor visitor) {
		visitor.accept(this);
	}

}

package org.example.programstructure.containers;

import org.example.Visitor;

public interface Visitable {

	void accept(Visitor visitor);

}

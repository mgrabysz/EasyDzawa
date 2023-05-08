package org.example.programstructure.expression;

import org.example.commons.Position;
import org.example.visitor.Visitable;

public interface Expression extends Visitable {

	Position position();

}

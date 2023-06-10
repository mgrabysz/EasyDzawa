package org.example.programstructure.containers;

import org.example.visitor.Visitable;

import java.util.Map;

public interface ClassDefinition extends Visitable {

    Map<String, FunctionDefinition> methods();
    String name();

}

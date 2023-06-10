package org.example.programstructure.containers;

import org.example.visitor.Visitable;

import java.util.List;

public interface FunctionDefinition extends Visitable {

    String name();
    List<Parameter> parameters();
}

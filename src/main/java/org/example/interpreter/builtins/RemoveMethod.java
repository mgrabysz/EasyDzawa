package org.example.interpreter.builtins;

import org.example.programstructure.containers.FunctionDefinition;
import org.example.programstructure.containers.Parameter;
import org.example.properties.LanguageProperties;
import org.example.visitor.Visitor;

import java.util.List;

public class RemoveMethod implements FunctionDefinition {

    public static final String REMOVE = LanguageProperties.get("REMOVE");
    public static final String INDEX = "index";

    @Override
    public String name() {
        return REMOVE;
    }

    @Override
    public List<Parameter> parameters() {
        return List.of(new Parameter(INDEX));
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}

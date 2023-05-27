package org.example.interpreter.builtins;

import org.example.programstructure.containers.FunctionDefinition;
import org.example.programstructure.containers.Parameter;
import org.example.properties.LanguageProperties;
import org.example.visitor.Visitor;

import java.util.List;

public class AppendMethod implements FunctionDefinition {

    public static final String APPEND = LanguageProperties.get("APPEND");
    public static final String ITEM = "item";


    @Override
    public String name() {
        return APPEND;
    }

    @Override
    public List<Parameter> parameters() {
        return List.of(new Parameter(ITEM));
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}

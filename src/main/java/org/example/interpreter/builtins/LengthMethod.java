package org.example.interpreter.builtins;

import org.example.programstructure.containers.FunctionDefinition;
import org.example.programstructure.containers.Parameter;
import org.example.properties.LanguageProperties;
import org.example.visitor.Visitor;

import java.util.List;

public class LengthMethod implements FunctionDefinition {

    public static final String LENGTH = LanguageProperties.get("LENGTH");

    @Override
    public String name() {
        return LENGTH;
    }

    @Override
    public List<Parameter> parameters() {
        return List.of();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}

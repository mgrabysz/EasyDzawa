package org.example.interpreter.builtins;

import org.example.programstructure.containers.FunctionDefinition;
import org.example.programstructure.containers.Parameter;
import org.example.properties.LanguageProperties;
import org.example.visitor.Visitor;

import java.util.List;

public class AbortFunction implements FunctionDefinition {

    public static final String ABORT = LanguageProperties.get("ABORT");

    @Override
    public String name() {
        return ABORT;
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

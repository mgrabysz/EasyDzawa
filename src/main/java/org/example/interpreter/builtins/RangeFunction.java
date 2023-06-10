package org.example.interpreter.builtins;

import org.example.programstructure.containers.FunctionDefinition;
import org.example.programstructure.containers.Parameter;
import org.example.properties.LanguageProperties;
import org.example.visitor.Visitor;

import java.util.List;

public class RangeFunction implements FunctionDefinition {

    public static final String RANGE = LanguageProperties.get("RANGE");
    public static final String START = "start";
    public static final String STOP = "stop";

    @Override
    public String name() {
        return RANGE;
    }

    @Override
    public List<Parameter> parameters() {
        return List.of(new Parameter(START), new Parameter(STOP));
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}

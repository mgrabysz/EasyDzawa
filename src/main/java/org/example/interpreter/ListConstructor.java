package org.example.interpreter;

import org.example.programstructure.containers.FunctionDefinition;
import org.example.programstructure.containers.Parameter;
import org.example.properties.LanguageProperties;
import org.example.visitor.Visitor;

import java.util.List;

public class ListConstructor implements FunctionDefinition {

    public static final String LIST = LanguageProperties.get("LIST");

    @Override
    public String name() {
        return LIST;
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

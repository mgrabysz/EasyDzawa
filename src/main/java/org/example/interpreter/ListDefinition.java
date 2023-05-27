package org.example.interpreter;

import org.example.programstructure.containers.ClassDefinition;
import org.example.programstructure.containers.FunctionDefinition;
import org.example.properties.LanguageProperties;
import org.example.visitor.Visitor;

import java.util.HashMap;
import java.util.Map;

public class ListDefinition implements ClassDefinition {

    public static final String LIST = LanguageProperties.get("LIST");
    private final Map<String, FunctionDefinition> methods = new HashMap<>();

    public ListDefinition() {
        methods.put(AppendMethod.APPEND, new AppendMethod());
        methods.put(GetMethod.GET, new GetMethod());
    }

    @Override
    public Map<String, FunctionDefinition> methods() {
        return methods;
    }

    @Override
    public String name() {
        return LIST;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}

package org.example.interpreter.builtins;

import lombok.NoArgsConstructor;
import org.example.programstructure.containers.ClassDefinition;
import org.example.programstructure.containers.FunctionDefinition;
import org.example.properties.LanguageProperties;
import org.example.visitor.Visitor;

import java.util.Map;

@NoArgsConstructor
public class ListDefinition implements ClassDefinition {

    public static final String LIST = LanguageProperties.get("LIST");
    static final Map<String, FunctionDefinition> methods = Map.ofEntries(
            Map.entry(AppendMethod.APPEND, new AppendMethod()),
            Map.entry(GetMethod.GET, new GetMethod()),
            Map.entry(LengthMethod.LENGTH, new LengthMethod()),
            Map.entry(RemoveMethod.REMOVE, new RemoveMethod())
    );

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

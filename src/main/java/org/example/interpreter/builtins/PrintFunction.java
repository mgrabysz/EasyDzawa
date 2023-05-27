package org.example.interpreter.builtins;

import org.example.properties.LanguageProperties;
import org.example.visitor.Visitor;

public class PrintFunction extends VariadicFunction {

    public static final String PRINT = LanguageProperties.get("PRINT");

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String name() {
        return PRINT;
    }

}

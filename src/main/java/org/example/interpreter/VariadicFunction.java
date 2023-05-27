package org.example.interpreter;

import org.example.programstructure.containers.FunctionDefinition;
import org.example.programstructure.containers.Parameter;
import org.example.visitor.Visitable;
import org.example.visitor.Visitor;

import java.util.List;

/**
 * Function which accepts a variable number of arguments
 */
public abstract class VariadicFunction implements Visitable, FunctionDefinition {

    public static String ARGS = "args";

    @Override
    public List<Parameter> parameters() {
        return null;
    }

    @Override
    public abstract void accept(Visitor visitor);

}

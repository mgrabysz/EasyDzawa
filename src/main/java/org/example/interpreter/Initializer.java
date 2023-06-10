package org.example.interpreter;

import lombok.Getter;
import org.example.interpreter.builtins.AbortFunction;
import org.example.interpreter.builtins.ListDefinition;
import org.example.interpreter.builtins.PrintFunction;
import org.example.interpreter.builtins.RangeFunction;
import org.example.programstructure.containers.Program;

@Getter
public class Initializer {

    public static void addBuiltIns(Program program) {
        program.functionDefinitions().put(PrintFunction.PRINT, new PrintFunction());
        program.functionDefinitions().put(AbortFunction.ABORT, new AbortFunction());
        program.functionDefinitions().put(RangeFunction.RANGE, new RangeFunction());
        program.classDefinitions().put(ListDefinition.LIST, new ListDefinition());
    }

}

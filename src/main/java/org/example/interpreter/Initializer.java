package org.example.interpreter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.programstructure.containers.Program;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Initializer {

    public static void addBuiltIns(Program program) {
        program.functionDefinitions().put(PrintFunction.PRINT, new PrintFunction());
        program.functionDefinitions().put(AbortFunction.ABORT, new AbortFunction());
        program.classDefinitions().put(ListDefinition.LIST, new ListDefinition());
    }

}

package org.example.interpreter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.programstructure.containers.ClassDefinition;
import org.example.programstructure.containers.FunctionDefinition;
import org.example.programstructure.containers.Program;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProgramHolder {

    private Map<String, FunctionDefinition> functionDefinitions;
    private Map<String, ClassDefinition> classesDefinitions;

    public static ProgramHolder init(Program parsedProgram) {
        // map all user class definitions to generic ClassDefinition
        Map<String, ClassDefinition> classes = new HashMap<>();
        parsedProgram.classDefinitions()
                .values()
                .stream()
                .map(userClassDefinition -> (ClassDefinition) userClassDefinition)
                .forEach(classDefinition -> classes.put(classDefinition.name(), classDefinition));

        // map all user function definitions to generic FunctionDefinition
        Map<String, FunctionDefinition> functions = new HashMap<>();
        parsedProgram.functionDefinitions()
                .values()
                .stream()
                .map(userFunctionDefinition -> (FunctionDefinition) userFunctionDefinition)
                .forEach(functionDefinition -> functions.put(functionDefinition.name(), functionDefinition));

        FunctionDefinition printFunction = new PrintFunction();
        functions.put(printFunction.name(), printFunction);
        FunctionDefinition abortFunction = new AbortFunction();
        functions.put(abortFunction.name(), abortFunction);
        classes.put(ListDefinition.LIST, new ListDefinition());

        return new ProgramHolder(functions, classes);
    }

}

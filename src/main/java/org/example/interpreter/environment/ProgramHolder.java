package org.example.interpreter.environment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.interpreter.AbortFunction;
import org.example.interpreter.PrintFunction;
import org.example.programstructure.containers.ClassDefinition;
import org.example.programstructure.containers.FunctionDefinition;
import org.example.programstructure.containers.Program;
import org.example.visitor.Visitable;
import org.example.visitor.Visitor;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProgramHolder implements Visitable {

    private Map<String, FunctionDefinition> functionDefinitions;
    private Map<String, ClassDefinition> classesDefinitions;

    public static ProgramHolder init(Program parsedProgram) {
        Map<String, ClassDefinition> classes = parsedProgram.classDefinitions();
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
        return new ProgramHolder(functions, classes);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}

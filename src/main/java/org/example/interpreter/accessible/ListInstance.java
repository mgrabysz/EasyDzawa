package org.example.interpreter.accessible;

import lombok.Getter;
import org.example.programstructure.containers.FunctionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class ListInstance extends ObjectInstance {

    private final List<Object> list = new ArrayList<>();

    public ListInstance(String className, Map<String, FunctionDefinition> methods) {
        super(className, methods);
    }

}

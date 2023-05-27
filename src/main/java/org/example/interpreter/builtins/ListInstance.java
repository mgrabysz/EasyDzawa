package org.example.interpreter.builtins;

import lombok.Getter;
import lombok.Setter;
import org.example.interpreter.accessible.ObjectInstance;
import org.example.programstructure.containers.FunctionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ListInstance extends ObjectInstance {

    private List<Object> list = new ArrayList<>();

    public ListInstance(String className, Map<String, FunctionDefinition> methods) {
        super(className, methods);
    }

}

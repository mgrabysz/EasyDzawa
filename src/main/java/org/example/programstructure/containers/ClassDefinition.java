package org.example.programstructure.containers;

import java.util.HashMap;

public record ClassDefinition(String name, HashMap<String, FunctionDefinition> methods) {
}

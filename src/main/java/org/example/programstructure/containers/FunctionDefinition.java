package org.example.programstructure.containers;

import java.util.List;

public record FunctionDefinition(String name, List<Parameter> parameters, Block block) {
}

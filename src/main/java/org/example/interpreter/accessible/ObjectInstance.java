package org.example.interpreter.accessible;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.programstructure.containers.FunctionDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a specific instance of an object
 */
@Getter
@RequiredArgsConstructor
public class ObjectInstance {

    private final String className;
    private final Map<String, FunctionDefinition> methods;
    private final Map<String, ValueReference> attributes = new HashMap<>();

	public ValueReference findAttribute(String name) {
		return attributes.get(name);
	}

	public void storeAttribute(String name, ValueReference valueReference) {
		attributes.put(name, valueReference);
	}

	public FunctionDefinition getMethodDefinition(String name) {
		return methods.get(name);
	}

}

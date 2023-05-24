package org.example.interpreter.accessible;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.programstructure.containers.FunctionDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a specific instance of an object defined by the user
 */
@Getter
@RequiredArgsConstructor
public class UserObject {

	private final Map<String, Object> attributes = new HashMap<>();

    private final String className;
    private final Map<String, FunctionDefinition> methods;

	public Object findAttribute(String name) {
		return attributes.get(name);
	}

	public void storeAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	public FunctionDefinition getMethodDefinition(String name) {
		return methods.get(name);
	}

}

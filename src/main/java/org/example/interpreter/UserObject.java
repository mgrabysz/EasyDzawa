package org.example.interpreter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.programstructure.containers.ClassDefinition;
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
	private final ClassDefinition classDefinition;

	public boolean hasAttribute(String name) {
		return attributes.containsKey(name);
	}

	public boolean hasMethod(String name) {
		return classDefinition.methods().containsKey(name);
	}

	public Object findAttribute(String name) {
		return attributes.get(name);
	}

	public void storeAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	public FunctionDefinition getMethodDefinition(String name) {
		return classDefinition.methods().get(name);
	}

    public String getClassName() {
        return classDefinition.name();
    }
}

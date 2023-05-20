package org.example.interpreter.environment;

import lombok.Getter;
import org.example.interpreter.UserObject;
import org.example.programstructure.containers.ClassDefinition;

@Getter
public class ConstructorCallContext extends FunctionCallContext{

	private final UserObject userObject;

	ConstructorCallContext(ClassDefinition classDefinition) {
		userObject = new UserObject(classDefinition);
	}

	public void storeAttribute(String name, Object value) {
		userObject.storeAttribute(name, value);
	}

	public Object findAttribute(String name) {
		return userObject.findAttribute(name);
	}

}

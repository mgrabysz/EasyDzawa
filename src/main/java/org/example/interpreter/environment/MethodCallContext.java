package org.example.interpreter.environment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.interpreter.UserObject;

@Getter
@RequiredArgsConstructor
public class MethodCallContext extends FunctionCallContext {

	private final UserObject userObject;

	public boolean hasAttribute(String name) {
		return userObject.hasAttribute(name);
	}

	public boolean hasMethod(String name) {
		return userObject.hasMethod(name);
	}

	public void storeAttribute(String name, Object value) {
		userObject.storeAttribute(name, value);
	}

	public Object findAttribute(String name) {
		return userObject.findAttribute(name);
	}

}

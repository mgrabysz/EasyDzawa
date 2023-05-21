package org.example.interpreter.environment;

import lombok.Getter;
import org.example.interpreter.UserObject;

@Getter
public class MethodCallContext extends CallContext {

	private final UserObject userObject;

    public MethodCallContext(UserObject userObject) {
        super();
        this.userObject = userObject;
    }

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

    @Override
    public ContextType getContextType() {
        return ContextType.METHOD;
    }

}

package org.example.interpreter.environment;

import lombok.Getter;
import org.example.interpreter.accessible.UserObject;
import org.example.programstructure.containers.ClassDefinition;

@Getter
public class ConstructorCallContext extends CallContext {

	private final UserObject userObject;

	ConstructorCallContext(ClassDefinition classDefinition) {
        super();
		userObject = new UserObject(classDefinition);
	}

	public void storeAttribute(String name, Object value) {
		userObject.storeAttribute(name, value);
	}

	public Object findAttribute(String name) {
		return userObject.findAttribute(name);
	}

    @Override
    public ContextType getContextType() {
        return ContextType.CONSTRUCTOR;
    }

}

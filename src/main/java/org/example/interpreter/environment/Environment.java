package org.example.interpreter.environment;

import lombok.NoArgsConstructor;
import org.example.interpreter.UserObject;
import org.example.programstructure.containers.ClassDefinition;

import java.util.Stack;

@NoArgsConstructor
public class Environment {

	private final Stack<CallContext> functionCallContexts = new Stack<>();

	public void enterFunctionCall() {
		functionCallContexts.push(new FunctionCallContext());
	}

	public void enterMethodCall(UserObject userObject) {
		functionCallContexts.push(new MethodCallContext(userObject));
	}

	public void enterConstructorCall(ClassDefinition classDefinition) {
		functionCallContexts.push(new ConstructorCallContext(classDefinition));
	}

	public void exitCurrentCall() {
		functionCallContexts.pop();
	}

	public void enterScope() {
		CallContext currentContext = functionCallContexts.peek();
		currentContext.createScope();
	}

	public void exitScope() {
		CallContext currentContext = functionCallContexts.peek();
		currentContext.exitScope();
	}

	public void store(String key, Object value) {
		CallContext currentContext = functionCallContexts.peek();
		currentContext.store(key, value);
	}

	public Object find(String key) {
		CallContext currentContext = functionCallContexts.peek();
        return  currentContext.find(key);
	}

	public void storeAttribute(String key, Object value) {
		CallContext callContext = functionCallContexts.peek();
		switch (callContext) {
			case ConstructorCallContext constructor -> constructor.storeAttribute(key, value);
			case MethodCallContext method -> method.storeAttribute(key, value);
            default -> throw new IllegalStateException("Unexpected value: " + callContext);
        }
	}

	public Object findAttribute(String key) {
		CallContext callContext = functionCallContexts.peek();
        return switch (callContext) {
            case ConstructorCallContext constructor -> constructor.findAttribute(key);
            case MethodCallContext method -> method.findAttribute(key);
            default -> null;
        };
	}

	public UserObject getSelfObject() {
		CallContext callContext = functionCallContexts.peek();
        return switch (callContext) {
            case ConstructorCallContext constructor -> constructor.getUserObject();
            case MethodCallContext method -> method.getUserObject();
            default -> null;
        };
	}

    public ContextType getContextType() {
        return functionCallContexts.peek().getContextType();
    }

}

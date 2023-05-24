package org.example.interpreter.environment;

import lombok.NoArgsConstructor;

import java.util.Stack;

@NoArgsConstructor
public class Environment {

	private final Stack<FunctionCallContext> functionCallContexts = new Stack<>();

	public void enterFunctionCall() {
		functionCallContexts.push(new FunctionCallContext(ContextType.FUNCTION));
	}

	public void enterMethodCall() {
		functionCallContexts.push(new FunctionCallContext(ContextType.METHOD));
	}

	public void enterConstructorCall() {
		functionCallContexts.push(new FunctionCallContext(ContextType.CONSTRUCTOR));
	}

	public void exitCurrentCall() {
		functionCallContexts.pop();
	}

	public void enterScope() {
		FunctionCallContext currentContext = functionCallContexts.peek();
		currentContext.createScope();
	}

	public void exitScope() {
		FunctionCallContext currentContext = functionCallContexts.peek();
		currentContext.exitScope();
	}

	public void store(String key, Object value) {
		FunctionCallContext currentContext = functionCallContexts.peek();
		currentContext.store(key, value);
	}

	public Object find(String key) {
		FunctionCallContext currentContext = functionCallContexts.peek();
        return  currentContext.find(key);
	}


    public ContextType getContextType() {
        return functionCallContexts.peek().getContextType();
    }

    public boolean isAssignment() {
        return functionCallContexts.peek().isAssignment();
    }

    public void setIsAssignment(boolean isAssignment) {
        functionCallContexts.peek().setAssignment(isAssignment);
    }

}

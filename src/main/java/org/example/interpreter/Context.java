package org.example.interpreter;

import lombok.NoArgsConstructor;

import java.util.Stack;

@NoArgsConstructor
public class Context {

	private final Stack<FunctionCallContext> functionCallContexts = new Stack<>();

	public void enterFunctionCall() {
		functionCallContexts.push(new FunctionCallContext());
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

	public void find(String key) {
		FunctionCallContext currentContext = functionCallContexts.peek();
		currentContext.find(key);
	}

	public void exitFunctionCall() {
		functionCallContexts.pop();
	}

}

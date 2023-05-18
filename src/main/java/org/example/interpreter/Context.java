package org.example.interpreter;

import lombok.NoArgsConstructor;

import java.util.Stack;

@NoArgsConstructor
public class Context {

	private final Stack<FunctionCallContext> functionCallContexts = new Stack<>();
	private final Stack<UserObject> userObjectContexts = new Stack<>();

	public void enterFunctionCall() {
		functionCallContexts.push(new FunctionCallContext());
	}

	public void exitFunctionCall() {
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
		return currentContext.find(key);
	}

	public void enterObjectScope(UserObject userObject) {
		userObjectContexts.push(userObject);
	}

	public void exitObjectScope() {
		userObjectContexts.pop();
	}

	public boolean isInsideObjectScope() {
		return !userObjectContexts.empty();
	}

	public Object findAttribute(String key) {
		UserObject userObject = userObjectContexts.peek();
		return userObject.findAttribute(key);
	}

	public void storeAttribute(String key, Object value) {
		UserObject userObject = userObjectContexts.peek();
		userObject.storeAttribute(key, value);
	}

}

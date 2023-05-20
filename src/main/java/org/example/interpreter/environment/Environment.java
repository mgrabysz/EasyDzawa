package org.example.interpreter.environment;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.example.error.enums.ErrorType;
import org.example.error.exception.SemanticException;
import org.example.interpreter.UserObject;
import org.example.programstructure.containers.ClassDefinition;

import java.util.Stack;

@NoArgsConstructor
public class Environment {

	private final Stack<FunctionCallContext> functionCallContexts = new Stack<>();

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
		Object object;
		if ((object = currentContext.find(key)) != null) {
			return object;
		} else {
			handleError(ErrorType.VARIABLE_NOT_DEFINED_IN_SCOPE);
			return null;
		}
	}

	public void storeAttribute(String key, Object value) {
		FunctionCallContext callContext = functionCallContexts.peek();
		switch (callContext) {
			case ConstructorCallContext constructor -> constructor.storeAttribute(key, value);
			case MethodCallContext method -> {
				if (method.hasAttribute(key)) {
					method.storeAttribute(key, value);
				} else {
					handleError(ErrorType.ATTRIBUTE_NOT_DEFINED); // new attributes can be defined only in constructor
				}
			}
			default -> handleError(ErrorType.SELF_ACCESS_OUTSIDE_OF_CLASS);
		}
	}

	public Object findAttribute(String key) {
		FunctionCallContext callContext = functionCallContexts.peek();
		Object attribute = null;
		switch (callContext) {
			case ConstructorCallContext constructor -> attribute = constructor.findAttribute(key);
			case MethodCallContext method -> attribute = method.findAttribute(key);
			default -> handleError(ErrorType.SELF_ACCESS_OUTSIDE_OF_CLASS);
		}
		if (attribute == null) {
			handleError(ErrorType.ATTRIBUTE_NOT_DEFINED);
		}
		return attribute;
	}

	public UserObject getSelfObject() {
		FunctionCallContext callContext = functionCallContexts.peek();
		UserObject selfObject = null;
		switch (callContext) {
			case ConstructorCallContext constructor -> selfObject = constructor.getUserObject();
			case MethodCallContext method -> selfObject = method.getUserObject();
			default -> handleError(ErrorType.SELF_ACCESS_OUTSIDE_OF_CLASS);
		}
		return selfObject;
	}

	@SneakyThrows
	private void handleError(ErrorType errorType) {
		throw new SemanticException(errorType.toString());
	}

}

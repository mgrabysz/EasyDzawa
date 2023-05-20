package org.example.interpreter.environment;

import java.util.*;

public class FunctionCallContext {

	private final List<Map<String, Object>> scopes = new ArrayList<>();

	FunctionCallContext() {
		createScope();
	}

	public void createScope() {
		// the scope at position 0 is the newest one
		scopes.add(0, new HashMap<>());
	}

	public void exitScope() {
		scopes.remove(0);
	}

	public void store(String key, Object value) {
		Map<String, Object> scope = scopes.get(0);
		scope.put(key, value);
	}

	public Object find(String key) {
		return scopes.stream()
				.map(scope -> scope.get(key))
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);
	}

}

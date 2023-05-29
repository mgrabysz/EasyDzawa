package org.example.interpreter.environment;

import lombok.Getter;
import lombok.Setter;
import org.example.interpreter.accessible.ValueReference;

import java.util.*;

public class FunctionCallContext {

    FunctionCallContext(ContextType contextType) {
        this.contextType = contextType;
        createScope();
    }

    private final List<Map<String, ValueReference>> scopes = new ArrayList<>();

    @Getter
    private final ContextType contextType;

    @Getter
    @Setter
    private boolean isAssignment = false;

    public void createScope() {
        // the scope at position 0 is the newest one
        scopes.add(0, new HashMap<>());
    }

    public void exitScope() {
        scopes.remove(0);
    }

    public void store(String key, ValueReference value) {
        Map<String, ValueReference> scope = scopes.get(0);
        scope.put(key, value);
    }

    public ValueReference find(String key) {
        return scopes.stream()
                .map(scope -> scope.get(key))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

}

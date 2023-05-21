package org.example.interpreter.environment;

public class FunctionCallContext extends CallContext{

	FunctionCallContext() {
		super();
	}

    @Override
    public ContextType getContextType() {
        return ContextType.FUNCTION;
    }

}

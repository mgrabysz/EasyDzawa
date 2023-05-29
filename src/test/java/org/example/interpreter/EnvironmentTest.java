package org.example.interpreter;

import org.example.interpreter.accessible.ValueReference;
import org.example.interpreter.environment.Environment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EnvironmentTest {

	@Test
	void testEnterFunctionCall() {
		Environment environment = new Environment();
		environment.enterFunctionCall();
		environment.store("var1", new ValueReference(10));
		assertEquals(10, environment.find("var1").getValue());

		environment.enterFunctionCall();
		environment.store("var1", new ValueReference(20));
		environment.store("var2", new ValueReference(30));
		assertEquals(20, environment.find("var1").getValue());
		assertEquals(30, environment.find("var2").getValue());
		environment.exitCurrentCall();

		assertEquals(10, environment.find("var1").getValue());
        assertNull(environment.find("var2"));
	}

}

package org.example.interpreter;

import org.example.commons.Position;
import org.example.interpreter.environment.Environment;
import org.example.programstructure.containers.ClassDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EnvironmentTest {

	@Test
	void testEnterFunctionCall() {
		Environment environment = new Environment();
		environment.enterFunctionCall();
		environment.store("var1", 10);
		assertEquals(10, environment.find("var1"));

		environment.enterFunctionCall();
		environment.store("var1", 20);
		environment.store("var2", 30);
		assertEquals(20, environment.find("var1"));
		assertEquals(30, environment.find("var2"));
		environment.exitCurrentCall();

		assertEquals(10, environment.find("var1"));
        assertNull(environment.find("var2"));
	}

	@Test
	void testEnterMethodCall() {
		ClassDefinition classDefinition = new ClassDefinition("class", null, new Position());
		UserObject userObject = new UserObject(classDefinition);
		userObject.storeAttribute("name", "Franek");
		Environment environment = new Environment();
		environment.enterMethodCall(userObject);
		environment.store("var", 10);
		assertEquals("Franek", environment.findAttribute("name"));
		assertEquals(10, environment.find("var"));
        assertNull(environment.findAttribute("var"));

		environment.enterFunctionCall();
        assertNull(environment.findAttribute("var"));
		environment.exitCurrentCall();

		assertEquals("Franek", environment.findAttribute("name"));
		assertEquals(10, environment.find("var"));
        assertNull(environment.findAttribute("var"));
	}
}

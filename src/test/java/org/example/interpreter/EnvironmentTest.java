package org.example.interpreter;

import org.example.error.exception.LexicalException;
import org.example.error.exception.SemanticException;
import org.example.interpreter.environment.Environment;
import org.example.programstructure.containers.ClassDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
		assertThrows(SemanticException.class, () -> environment.find("var2"));
	}

	@Test
	void testEnterMethodCall() {
		ClassDefinition classDefinition = new ClassDefinition("class", null	);
		UserObject userObject = new UserObject(classDefinition);
		userObject.storeAttribute("name", "Franek");
		Environment environment = new Environment();
		environment.enterMethodCall(userObject);
		environment.store("var", 10);
		assertEquals("Franek", environment.findAttribute("name"));
		assertEquals(10, environment.find("var"));
		assertThrows(SemanticException.class, () -> environment.findAttribute("var"));

		environment.enterFunctionCall();
		assertThrows(SemanticException.class, () -> environment.findAttribute("var"));
		environment.exitCurrentCall();

		assertEquals("Franek", environment.findAttribute("name"));
		assertEquals(10, environment.find("var"));
		assertThrows(SemanticException.class, () -> environment.findAttribute("var"));

	}
}

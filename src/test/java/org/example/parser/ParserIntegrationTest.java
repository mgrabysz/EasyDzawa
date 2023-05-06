package org.example.parser;

import org.example.error.manager.ErrorManager;
import org.example.lexer.LexerImpl;
import org.example.programstructure.containers.Block;
import org.example.programstructure.containers.ClassDefinition;
import org.example.programstructure.containers.FunctionDefinition;
import org.example.programstructure.containers.Program;
import org.example.programstructure.expression.*;
import org.example.programstructure.expression.enums.RelativeType;
import org.example.programstructure.statement.AssignmentStatement;
import org.example.programstructure.statement.IfStatement;
import org.example.programstructure.statement.ObjectAccess;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserIntegrationTest {

	@Test
	void testParsingDefinitions() {
		final String path = "src/test/resources/parser/definitions.txt";
		final Program program = readFromFile(path);
		assertEquals(1, program.classDefinitions().size());
		assertEquals(1, program.functionDefinitions().size());
		// class definition
		final ClassDefinition classDefinition = program.classDefinitions().get("Ułamek");
		assertEquals("Ułamek", classDefinition.name());
		assertEquals(2, classDefinition.methods().size());
		final FunctionDefinition method = classDefinition.methods().get("rozszerz");
		// constructor
		final FunctionDefinition constructor = classDefinition.methods().get("Ułamek");
		final Block constructorBlock = constructor.block();
		assertEquals(2, constructor.parameters().size());
		assertEquals(4, constructorBlock.statements().size());
		// first assignment
		final AssignmentStatement assignmentStatement1 = (AssignmentStatement) constructorBlock.statements().get(0);
		final ObjectAccess objectAccess1 = (ObjectAccess) assignmentStatement1.objectAccess();
		final IdentifierExpression identifierExpression10 = (IdentifierExpression) objectAccess1.right();
		final IdentifierExpression identifierExpression11 = (IdentifierExpression) assignmentStatement1.expression();
		assertTrue(objectAccess1.left() instanceof SelfAccess);
		assertEquals("licznik", identifierExpression10.name());
		assertEquals("l", identifierExpression11.name());
		// second assignment
		final AssignmentStatement assignmentStatement2 = (AssignmentStatement) constructorBlock.statements().get(1);
		final ObjectAccess objectAccess2 = (ObjectAccess) assignmentStatement2.objectAccess();
		final IdentifierExpression identifierExpression20 = (IdentifierExpression) objectAccess2.right();
		final IdentifierExpression identifierExpression21 = (IdentifierExpression) assignmentStatement2.expression();
		assertTrue(objectAccess2.left() instanceof SelfAccess);
		assertEquals("mianownik", identifierExpression20.name());
		assertEquals("m", identifierExpression21.name());
		// first conditional
		final IfStatement ifStatement3 = (IfStatement) constructorBlock.statements().get(2);
		final RelativeExpression relativeExpression3 = (RelativeExpression) ifStatement3.condition();
		final IdentifierExpression identifierExpression30 = (IdentifierExpression) relativeExpression3.left();
		final LiteralInteger literalInteger = (LiteralInteger) relativeExpression3.right();
		final Block blockIfTrue3 = ifStatement3.blockIfTrue();
		final FunctionCallExpression functionCallExpression3 = (FunctionCallExpression) blockIfTrue3.statements().get(0);
		assertEquals(RelativeType.EQUAL, relativeExpression3.relativeType());
		assertEquals("m", identifierExpression30.name());
		assertEquals(0, literalInteger.value());
		assertEquals(1, blockIfTrue3.statements().size());
		assertEquals("kończWaść", functionCallExpression3.name());
		assertEquals(0, functionCallExpression3.arguments().size());
		// second conditional
		final IfStatement ifStatement4 = (IfStatement) constructorBlock.statements().get(3);
		final RelativeExpression relativeExpression4 = (RelativeExpression) ifStatement4.condition();
		final IdentifierExpression identifierExpression40 = (IdentifierExpression) relativeExpression4.left();
		final IdentifierExpression identifierExpression41 = (IdentifierExpression) relativeExpression4.right();
		final Block blockIfTrue4 = ifStatement4.blockIfTrue();
		final Block elseBlock = ifStatement4.elseBlock();
		final AssignmentStatement assignmentStatement40 = (AssignmentStatement) blockIfTrue4.statements().get(0);
		final ObjectAccess objectAccess40 = (ObjectAccess) assignmentStatement40.objectAccess();
		final IdentifierExpression identifierExpression42 = (IdentifierExpression) objectAccess40.right();
		final LiteralBool literalBool = (LiteralBool) assignmentStatement40.expression();
		assertEquals(RelativeType.LESS, relativeExpression4.relativeType());
		assertEquals("l", identifierExpression40.name());
		assertEquals("m", identifierExpression41.name());
		assertTrue(objectAccess40.left() instanceof SelfAccess);
		assertEquals("jestWłaściwy", identifierExpression42.name());
		assertEquals(true, literalBool.value());
		assertEquals(1, blockIfTrue4.statements().size());
		assertEquals(1, elseBlock.statements().size());
		assertTrue(elseBlock.statements().get(0) instanceof AssignmentStatement);
	}

	private static Program readFromFile(String path) {
		try (FileReader fileReader = new FileReader(path)) {
			var file = new BufferedReader(fileReader);
			var lexer = new LexerImpl(file, ErrorManager::handleError);
			var parser = new ParserImpl(lexer, ErrorManager::handleError);
			return parser.parse();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}

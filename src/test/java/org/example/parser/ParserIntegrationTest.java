package org.example.parser;

import org.example.error.manager.ErrorManager;
import org.example.lexer.LexerImpl;
import org.example.programstructure.containers.Block;
import org.example.programstructure.containers.ClassDefinition;
import org.example.programstructure.containers.UserFunctionDefinition;
import org.example.programstructure.containers.Program;
import org.example.programstructure.expression.*;
import org.example.programstructure.expression.enums.AdditiveType;
import org.example.programstructure.expression.enums.RelationalType;
import org.example.programstructure.statement.*;
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
		final UserFunctionDefinition method = classDefinition.methods().get("rozszerz");
		// constructor
		final UserFunctionDefinition constructor = classDefinition.methods().get("Ułamek");
		final Block constructorBlock = constructor.block();
		assertEquals(2, constructor.parameters().size());
		assertEquals(4, constructorBlock.statements().size());
		// first assignment
		final AssignmentStatement assignmentStatement1 = (AssignmentStatement) constructorBlock.statements().get(0);
		final ObjectAccess objectAccess1 = (ObjectAccess) assignmentStatement1.left();
		final IdentifierExpression identifierExpression10 = (IdentifierExpression) objectAccess1.right();
		final IdentifierExpression identifierExpression11 = (IdentifierExpression) assignmentStatement1.right();
		assertTrue(objectAccess1.left() instanceof SelfAccess);
		assertEquals("licznik", identifierExpression10.name());
		assertEquals("l", identifierExpression11.name());
		// second assignment
		final AssignmentStatement assignmentStatement2 = (AssignmentStatement) constructorBlock.statements().get(1);
		final ObjectAccess objectAccess2 = (ObjectAccess) assignmentStatement2.left();
		final IdentifierExpression identifierExpression20 = (IdentifierExpression) objectAccess2.right();
		final IdentifierExpression identifierExpression21 = (IdentifierExpression) assignmentStatement2.right();
		assertTrue(objectAccess2.left() instanceof SelfAccess);
		assertEquals("mianownik", identifierExpression20.name());
		assertEquals("m", identifierExpression21.name());
		// first conditional
		final IfStatement ifStatement3 = (IfStatement) constructorBlock.statements().get(2);
		final RelationalExpression relationalExpression3 = (RelationalExpression) ifStatement3.condition();
		final IdentifierExpression identifierExpression30 = (IdentifierExpression) relationalExpression3.left();
		final LiteralInteger literalInteger = (LiteralInteger) relationalExpression3.right();
		final Block blockIfTrue3 = ifStatement3.blockIfTrue();
		final FunctionCallExpression functionCallExpression3 = (FunctionCallExpression) blockIfTrue3.statements().get(0);
		assertEquals(RelationalType.EQUAL, relationalExpression3.relationalType());
		assertEquals("m", identifierExpression30.name());
		assertEquals(0, literalInteger.value());
		assertEquals(1, blockIfTrue3.statements().size());
		assertEquals("kończWaść", functionCallExpression3.name());
		assertEquals(0, functionCallExpression3.arguments().size());
		// second conditional
		final IfStatement ifStatement4 = (IfStatement) constructorBlock.statements().get(3);
		final RelationalExpression relationalExpression4 = (RelationalExpression) ifStatement4.condition();
		final IdentifierExpression identifierExpression40 = (IdentifierExpression) relationalExpression4.left();
		final IdentifierExpression identifierExpression41 = (IdentifierExpression) relationalExpression4.right();
		final Block blockIfTrue4 = ifStatement4.blockIfTrue();
		final Block elseBlock = ifStatement4.elseBlock();
		final AssignmentStatement assignmentStatement40 = (AssignmentStatement) blockIfTrue4.statements().get(0);
		final ObjectAccess objectAccess40 = (ObjectAccess) assignmentStatement40.left();
		final IdentifierExpression identifierExpression42 = (IdentifierExpression) objectAccess40.right();
		final LiteralBool literalBool = (LiteralBool) assignmentStatement40.right();
		assertEquals(RelationalType.LESS, relationalExpression4.relationalType());
		assertEquals("l", identifierExpression40.name());
		assertEquals("m", identifierExpression41.name());
		assertTrue(objectAccess40.left() instanceof SelfAccess);
		assertEquals("jestWłaściwy", identifierExpression42.name());
		assertEquals(true, literalBool.value());
		assertEquals(1, blockIfTrue4.statements().size());
		assertEquals(1, elseBlock.statements().size());
		assertTrue(elseBlock.statements().get(0) instanceof AssignmentStatement);

		// main function
		final UserFunctionDefinition main = program.functionDefinitions().get("main");
		assertEquals("main", main.name());
		// modify and assign
		final ModifyAndAssignStatement modifyAndAssignStatement = (ModifyAndAssignStatement) main.block().statements().get(1);
		final IdentifierExpression identifierExpression5 = (IdentifierExpression) modifyAndAssignStatement.left();
		final AdditiveType additiveType = modifyAndAssignStatement.additiveType();
		final LiteralInteger literalInteger5 = (LiteralInteger) modifyAndAssignStatement.right();
		assertEquals("l", identifierExpression5.name());
		assertEquals(AdditiveType.ADD, additiveType);
		assertEquals(2, literalInteger5.value());
		// for statement
		final ForStatement forStatement = (ForStatement) main.block().statements().get(2);
		final FunctionCallExpression functionCallExpression = (FunctionCallExpression) forStatement.range();
		assertEquals("i", forStatement.iteratorName());
		assertEquals("zakres", functionCallExpression.name());
		assertEquals(1, forStatement.block().statements().size());
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

package org.example.parser;

import org.example.commons.Position;
import org.example.error.manager.ErrorManager;
import org.example.lexer.Lexer;
import org.example.lexer.LexerMock;
import org.example.programstructure.containers.*;
import org.example.programstructure.expression.IdentifierExpression;
import org.example.programstructure.expression.MultiplicativeExpression;
import org.example.programstructure.expression.SelfAccess;
import org.example.programstructure.expression.enums.MultiplicativeType;
import org.example.programstructure.statement.AssignmentStatement;
import org.example.programstructure.statement.ObjectAccess;
import org.example.programstructure.statement.ReturnStatement;
import org.example.token.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTokenStreamInputTest {

	@Test
	void testFunctionDefinition() {
		List<Token> tokens = new ArrayList<>(Arrays.asList(
				new TokenIdentifier(new Position(), "multiply"),
				new TokenSymbol(TokenType.OPEN_PARENTHESIS, new Position()),
				new TokenIdentifier(new Position(), "x"),
				new TokenSymbol(TokenType.COMA, new Position()),
				new TokenIdentifier(new Position(), "y"),
				new TokenSymbol(TokenType.CLOSE_PARENTHESIS, new Position()),
				new TokenSymbol(TokenType.OPEN_BRACKET, new Position()),
				new TokenKeyword(TokenType.RETURN, new Position()),
				new TokenIdentifier(new Position(), "x"),
				new TokenSymbol(TokenType.MULTIPLY, new Position()),
				new TokenIdentifier(new Position(), "y"),
				new TokenSymbol(TokenType.SEMICOLON, new Position()),
				new TokenSymbol(TokenType.CLOSE_BRACKET, new Position()),
				new TokenEOF(new Position())
		));
		final Lexer lexer = new LexerMock(tokens);
		final Parser parser = new ParserImpl(lexer, ErrorManager::handleError);
		final Program program = parser.parse();
		assertEquals(1, program.functionDefinitions().size());
		assertEquals(0, program.classDefinitions().size());
		final UserFunctionDefinition functionDefinition = program.functionDefinitions().get("multiply");
		assertEquals("multiply", functionDefinition.name());
		assertEquals(2, functionDefinition.parameters().size());
		assertNotNull(functionDefinition.block());
		final Parameter x = functionDefinition.parameters().get(0);
		final Parameter y = functionDefinition.parameters().get(1);
		final Block block = functionDefinition.block();
		assertEquals("x", x.name());
		assertEquals("y", y.name());
		assertEquals(1, block.statements().size());
		final ReturnStatement returnStatement = (ReturnStatement) block.statements().get(0);
	 	assertNotNull(returnStatement.expression());
		final MultiplicativeExpression expression = (MultiplicativeExpression) returnStatement.expression();
		assertEquals(MultiplicativeType.MULTIPLY, expression.multiplicativeType());
		assertNotNull(expression.left());
		assertNotNull(expression.right());
		assertNotNull(expression.position());
		final IdentifierExpression left = (IdentifierExpression) expression.left();
		final IdentifierExpression right = (IdentifierExpression) expression.right();
		assertEquals("x", left.name());
		assertEquals("y", right.name());
	}

	@Test
	void testClassDefinition() {
		List<Token> tokens = new ArrayList<>(Arrays.asList(
				new TokenKeyword(TokenType.CLASS, new Position()),
				new TokenIdentifier(new Position(), "User"),
				new TokenSymbol(TokenType.OPEN_BRACKET, new Position()),
				new TokenIdentifier(new Position(), "User"),
				new TokenSymbol(TokenType.OPEN_PARENTHESIS, new Position()),
				new TokenIdentifier(new Position(), "name"),
				new TokenSymbol(TokenType.CLOSE_PARENTHESIS, new Position()),
				new TokenSymbol(TokenType.OPEN_BRACKET, new Position()),
				new TokenKeyword(TokenType.THIS, new Position()),
				new TokenSymbol(TokenType.DOT, new Position()),
				new TokenIdentifier(new Position(), "username"),
				new TokenSymbol(TokenType.ASSIGN, new Position()),
				new TokenIdentifier(new Position(), "name"),
				new TokenSymbol(TokenType.SEMICOLON, new Position()),
				new TokenSymbol(TokenType.CLOSE_BRACKET, new Position()),
				new TokenSymbol(TokenType.CLOSE_BRACKET, new Position()),
				new TokenEOF(new Position())
		));
		final Lexer lexer = new LexerMock(tokens);
		final Parser parser = new ParserImpl(lexer, ErrorManager::handleError);
		final Program program = parser.parse();
		assertEquals(0, program.functionDefinitions().size());
		assertEquals(1, program.classDefinitions().size());
		final UserClassDefinition userClassDefinition = program.classDefinitions().get("User");
		assertEquals(1, userClassDefinition.methods().size());
		final UserFunctionDefinition functionDefinition = userClassDefinition.methods().get("User");
		assertEquals(1, functionDefinition.parameters().size());
		assertNotNull(functionDefinition.block());
		final Parameter parameter = functionDefinition.parameters().get(0);
		final Block block = functionDefinition.block();
		assertEquals("name", parameter.name());
		assertEquals(1, block.statements().size());
		final AssignmentStatement assignmentStatement = (AssignmentStatement) block.statements().get(0);
		final ObjectAccess objectAccess = (ObjectAccess) assignmentStatement.left();
		final IdentifierExpression attribute = (IdentifierExpression) objectAccess.right();
		final IdentifierExpression variable = (IdentifierExpression) assignmentStatement.right();
		assertTrue(objectAccess.left() instanceof SelfAccess);
		assertEquals("username", attribute.name());
		assertEquals("name", variable.name());
	}
}

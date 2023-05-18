package org.example.interpreter;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.example.LanguageProperties;
import org.example.error.exception.SemanticException;
import org.example.interpreter.computers.LogicalComputer;
import org.example.interpreter.computers.MathematicalComputer;
import org.example.interpreter.computers.NegationComputer;
import org.example.interpreter.computers.RelationalComputer;
import org.example.interpreter.enums.LogicalOperation;
import org.example.programstructure.containers.*;
import org.example.programstructure.expression.*;
import org.example.programstructure.statement.*;
import org.example.visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class Interpreter implements Visitor {

	private static final String MAIN = LanguageProperties.get("MAIN");
	private final Context context = new Context();

	private Program program;
	private Object lastValue;
	private boolean returning;

	public void execute(Program program) {
		this.program = program;
		program.accept(this);
	}

	@SneakyThrows
	@Override
	public void visit(Program program) {
		FunctionDefinition main = program.functionDefinitions().get(MAIN);
		if (main == null) {
			handleError();
		} else {
			context.enterFunctionCall();
			main.accept(this);
		}
	}

	@Override
	public void visit(FunctionDefinition functionDefinition) {
		functionDefinition.block().accept(this);
	}

	@Override
	public void visit(ClassDefinition classDefinition) {

	}

	@Override
	public void visit(Block block) {
		for (Statement statement : block.statements()) {
			statement.accept(this);
			if (returning) {
				break;
			}
		}

	}

	@Override
	public void visit(Parameter parameter) {

	}

	@SneakyThrows
	@Override
	public void visit(OrExpression expression) {
		expression.left().accept(this);
		Object left = clearLastValue();
		expression.right().accept(this);
		Object right = clearLastValue();
		Object result = LogicalComputer.compute(left, right, LogicalOperation.OR);
		if (result != null) {
			lastValue = result;
		} else {
			handleError();
		}
	}

	@SneakyThrows
	@Override
	public void visit(AndExpression expression) {
		expression.left().accept(this);
		Object left = clearLastValue();
		expression.right().accept(this);
		Object right = clearLastValue();
		Object result = LogicalComputer.compute(left, right, LogicalOperation.AND);
		if (result != null) {
			lastValue = result;
		} else {
			handleError();
		}
	}

	@SneakyThrows
	@Override
	public void visit(RelationalExpression expression) {
		expression.left().accept(this);
		Object left = clearLastValue();
		expression.right().accept(this);
		Object right = clearLastValue();
		Object result = RelationalComputer.compute(left, right, OperationMapper.map(expression.relationalType()));
		if (result != null) {
			lastValue = result;
		} else {
			handleError();
		}
	}

	@SneakyThrows
	@Override
	public void visit(ArithmeticExpression expression) {
		expression.left().accept(this);
		Object left = clearLastValue();
		expression.right().accept(this);
		Object right = clearLastValue();
		Object result = MathematicalComputer.compute(left, right, OperationMapper.map(expression.additiveType()));
		if (result != null) {
			lastValue = result;
		} else {
			handleError();
		}
	}

	@SneakyThrows
	@Override
	public void visit(MultiplicativeExpression expression) {
		expression.left().accept(this);
		Object left = clearLastValue();
		expression.right().accept(this);
		Object right = clearLastValue();
		Object result = MathematicalComputer.compute(left, right, OperationMapper.map(expression.multiplicativeType()));
		if (result != null) {
			lastValue = result;
		} else {
			handleError();
		}
	}

	@Override
	public void visit(FunctionCallExpression expression) {
		// TODO
		List<Object> arguments = new ArrayList<>();
		for (int i = 0; i < expression.arguments().size(); ++i) {
			arguments.add(new Object());
		}
		// ==============
		FunctionDefinition functionDefinition = program.functionDefinitions().get(expression.name());
		List<Parameter> parameters = functionDefinition.parameters();
		assertEqualSize(parameters, arguments);
		context.enterFunctionCall();
		for (int i = 0; i < arguments.size(); i++) {
			context.store(parameters.get(i).name(), arguments.get(i));
		}
		functionDefinition.accept(this);
		context.exitFunctionCall();
		if (returning) {
			System.out.println("returned" + lastValue);
		}
	}

	@SneakyThrows
	@Override
	public void visit(IdentifierExpression expression) {
		Object result = context.find(expression.name());
		if (result != null) {
			lastValue = result;
		} else {
			handleError();
		}
	}

	@SneakyThrows
	@Override
	public void visit(NegatedExpression expression) {
		expression.accept(this);
		Object object = clearLastValue();
		Object result = NegationComputer.compute(object);
		if (result != null) {
			lastValue = result;
		} else {
			handleError();
		}
	}

	@Override
	public void visit(LiteralBool expression) {
		lastValue = expression.value();
	}

	@Override
	public void visit(LiteralFloat expression) {
		lastValue = expression.value();
	}

	@Override
	public void visit(LiteralInteger expression) {
		lastValue = expression.value();
	}

	@Override
	public void visit(LiteralText expression) {
		lastValue = expression.value();
	}

	@Override
	public void visit(SelfAccess expression) {

	}

	@Override
	public void visit(ModifyAndAssignStatement statement) {

	}

	@SneakyThrows
	@Override
	public void visit(AssignmentStatement statement) {
		statement.objectAccess().accept(this);
		Object left = clearLastValue();
		statement.expression().accept(this);
		Object right = clearLastValue();
		switch (left) {
			case IdentifierExpression identifierExpression -> context.store(identifierExpression.name(), right);
			case Accessible accessible -> accessible.setTo(right);
			default -> handleError();
		}

	}

	@Override
	public void visit(ForStatement statement) {

	}

	@Override
	public void visit(IfStatement statement) {
		statement.condition().accept(this);
		context.enterScope();
		if (((Boolean) clearLastValue())) {
			statement.blockIfTrue().accept(this);
		} else {
			Block block;
			if ((block = statement.elseBlock()) != null) {
				block.accept(this);
			}
		}
		if (returning) {
			return;
		}
		context.exitScope();

	}

	@SneakyThrows
	@Override
	public void visit(ObjectAccess objectAccess) {
		// left side
		objectAccess.left().accept(this);
		Object left = clearLastValue();
		UserObject accessedObject = null;
		switch (left) {
			case IdentifierExpression identifierLeft -> accessedObject = findUserObject(identifierLeft);
			case UserObject userObject -> accessedObject = userObject;
			case default -> handleError(); // not a valid access
		}
		// right side
		objectAccess.right().accept(this);
		Object right = clearLastValue();
		switch (right) {
			case FunctionCallExpression functionCall -> handleMethodCall(accessedObject, functionCall);
			case IdentifierExpression identifierRight -> handleAttributeAccess(accessedObject, identifierRight);
			default -> handleError(); // not a valid access
		}
	}

	private void handleAttributeAccess(UserObject accessedObject, IdentifierExpression identifierRight) throws SemanticException {
		if (accessedObject.hasAttribute(identifierRight.name())) {
			lastValue = new Accessible(accessedObject, identifierRight.name());
		} else {
			handleError();	// class does not have this attribute
		}
	}

	private void handleMethodCall(UserObject accessedObject, FunctionCallExpression functionCall) throws SemanticException {
		if (accessedObject.hasMethod(functionCall.name())) {
			context.enterObjectScope(accessedObject);
			functionCall.accept(this);
			context.exitObjectScope();
		} else {
			handleError();
		}
	}

	private UserObject findUserObject(IdentifierExpression identifier) throws SemanticException {
		Object object = context.find(identifier.name());
		UserObject userObject = null;
		switch (object) {
			case UserObject instance -> userObject = instance;
			case null -> handleError(); // not in scope
			default -> handleError(); // not a valid access
		}
		return userObject;
	}

	@Override
	public void visit(ReturnStatement statement) {
		// todo
		Object mockValue = 10;
		lastValue = mockValue;
		returning = true;
	}

	@SneakyThrows
	private void assertEqualSize(List<Parameter> a, List<Object> b) {
		if (a.size() != b.size()) {
			handleError();
		}
	}

	private Object clearLastValue() {
		Object temp = lastValue;
		lastValue = null;
		return temp;
	}

	private void handleError() throws SemanticException {
		throw new SemanticException("");
	}
}

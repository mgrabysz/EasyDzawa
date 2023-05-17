package org.example.interpreter;

import lombok.NoArgsConstructor;
import org.example.LanguageProperties;
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

	@Override
	public void visit(OrExpression expression) {

	}

	@Override
	public void visit(AndExpression expression) {

	}

	@Override
	public void visit(RelationalExpression expression) {

	}

	@Override
	public void visit(ArithmeticExpression expression) {

	}

	@Override
	public void visit(MultiplicativeExpression expression) {

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

	@Override
	public void visit(IdentifierExpression expression) {

	}

	@Override
	public void visit(NegatedExpression expression) {

	}

	@Override
	public void visit(LiteralBool expression) {

	}

	@Override
	public void visit(LiteralFloat expression) {

	}

	@Override
	public void visit(LiteralInteger expression) {

	}

	@Override
	public void visit(LiteralText expression) {

	}

	@Override
	public void visit(SelfAccess expression) {

	}

	@Override
	public void visit(ModifyAndAssignStatement statement) {

	}

	@Override
	public void visit(AssignmentStatement statement) {

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

	@Override
	public void visit(ObjectAccess statement) {

	}

	@Override
	public void visit(ReturnStatement statement) {
		// todo
		Object mockValue = 10;
		lastValue = mockValue;
		returning = true;
	}

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

	private void handleError() {

	}
}

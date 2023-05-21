package org.example.interpreter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.example.LanguageProperties;
import org.example.commons.Position;
import org.example.error.ErrorHandler;
import org.example.error.details.ErrorDetails;
import org.example.error.details.ErrorInterpreterDetails;
import org.example.error.enums.ErrorType;
import org.example.error.exception.SemanticException;
import org.example.interpreter.computers.LogicalComputer;
import org.example.interpreter.computers.MathematicalComputer;
import org.example.interpreter.computers.NegationComputer;
import org.example.interpreter.computers.RelationalComputer;
import org.example.interpreter.enums.LogicalOperation;
import org.example.interpreter.enums.MathematicalOperation;
import org.example.interpreter.environment.Environment;
import org.example.programstructure.containers.*;
import org.example.programstructure.expression.*;
import org.example.programstructure.statement.*;
import org.example.visitor.ErrorContextWriter;
import org.example.visitor.Visitor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Interpreter implements Visitor {

    private static final String MAIN = LanguageProperties.get("MAIN");
    private static final String PRINT = LanguageProperties.get("PRINT");
    private static final String ABORT = LanguageProperties.get("ABORT");

    private final Environment environment = new Environment();
    private final Map<String, FunctionDefinition> constructors = new HashMap<>();
    private final ErrorHandler errorHandler;

    private Program program;
    private Object lastValue;
    private boolean returning = false;
    private boolean isMethodCalled = false;
    private boolean testingMode = false;
    private StringBuilder outputBuffer = null;

    public Interpreter(ErrorHandler errorHandler, boolean testingMode) {
        this.errorHandler = errorHandler;
        this.testingMode = testingMode;
        if (testingMode) {
            outputBuffer = new StringBuilder();
        }
    }

    public String getOutput() {
        return outputBuffer.toString();
    }

    public void execute(Program program) {
        this.program = program;
        program.accept(this);
    }

    @SneakyThrows
    @Override
    public void visit(Program program) {
        // extracting all constructors
        for (ClassDefinition classDefinition : program.classDefinitions().values()) {
            classDefinition.accept(this);
        }
        // visiting main function
        FunctionDefinition main = program.functionDefinitions().get(MAIN);
        if (main == null) {
            handleError(ErrorType.MAIN_FUNCTION_MISSING);
        } else {
            environment.enterFunctionCall();
            main.accept(this);
        }
    }

    @Override
    @SneakyThrows
    public void visit(ClassDefinition classDefinition) {
        Optional<FunctionDefinition> optionalConstructor = classDefinition.methods()
                .values()
                .stream()
                .filter(method -> method.name().equals(classDefinition.name()))
                .findFirst();
        if (optionalConstructor.isEmpty()) {
            handleError(ErrorType.CONSTRUCTOR_MISSING);
        }
        boolean hasReturnStatement = optionalConstructor.stream()
                .flatMap(f -> f.block().statements().stream())
                .anyMatch(s -> s instanceof ReturnStatement);
        if (hasReturnStatement) {
            handleError(ErrorType.CONSTRUCTOR_CONTAINS_RETURN);
        }
        FunctionDefinition constructor = optionalConstructor.get();
        constructors.put(constructor.name(), constructor);
    }

    @Override
    public void visit(FunctionDefinition functionDefinition) {
        functionDefinition.block().accept(this);
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

    @SneakyThrows
    @Override
    public void visit(OrExpression expression) {
        expression.left().accept(this);
        Object left = consumeEvaluatedLastValue();
        expression.right().accept(this);
        Object right = consumeEvaluatedLastValue();
        Object result = LogicalComputer.compute(left, right, LogicalOperation.OR);
        if (result != null) {
            lastValue = result;
        } else {
            handleError(ErrorType.OPERATION_NOT_SUPPORTED, expression.position(), ErrorContextWriter.buildContext(expression));
        }
    }

    @SneakyThrows
    @Override
    public void visit(AndExpression expression) {
        expression.left().accept(this);
        Object left = consumeEvaluatedLastValue();
        expression.right().accept(this);
        Object right = consumeEvaluatedLastValue();
        Object result = LogicalComputer.compute(left, right, LogicalOperation.AND);
        if (result != null) {
            lastValue = result;
        } else {
            handleError(ErrorType.OPERATION_NOT_SUPPORTED, expression.position(), ErrorContextWriter.buildContext(expression));
        }
    }

    @SneakyThrows
    @Override
    public void visit(RelationalExpression expression) {
        expression.left().accept(this);
        Object left = consumeEvaluatedLastValue();
        expression.right().accept(this);
        Object right = consumeEvaluatedLastValue();
        Object result = RelationalComputer.compute(left, right, OperationMapper.map(expression.relationalType()));
        if (result != null) {
            lastValue = result;
        } else {
            handleError(ErrorType.OPERATION_NOT_SUPPORTED, expression.position(), ErrorContextWriter.buildContext(expression));
        }
    }

    @SneakyThrows
    @Override
    public void visit(ArithmeticExpression expression) {
        expression.left().accept(this);
        Object left = consumeEvaluatedLastValue();
        expression.right().accept(this);
        Object right = consumeEvaluatedLastValue();
        Object result = MathematicalComputer.compute(left, right, OperationMapper.map(expression.additiveType()));
        if (result != null) {
            lastValue = result;
        } else {
            handleError(ErrorType.OPERATION_NOT_SUPPORTED, expression.position(), ErrorContextWriter.buildContext(expression));
        }
    }

    @SneakyThrows
    @Override
    public void visit(MultiplicativeExpression expression) {
        expression.left().accept(this);
        Object left = consumeEvaluatedLastValue();
        expression.right().accept(this);
        Object right = consumeEvaluatedLastValue();
        MathematicalOperation operation = OperationMapper.map(expression.multiplicativeType());
        if (operation == MathematicalOperation.DIVIDE && right.equals(0)) {
            handleError(ErrorType.ZERO_DIVISION);
        }
        Object result = MathematicalComputer.compute(left, right, operation);
        if (result != null) {
            lastValue = result;
        } else {
            handleError(ErrorType.OPERATION_NOT_SUPPORTED, expression.position(), ErrorContextWriter.buildContext(expression));
        }
    }

    @Override
    public void visit(FunctionCallExpression functionCallExpression) {
        if (isMethodCalled) {
            callMethod(functionCallExpression);
        } else if (constructors.containsKey(functionCallExpression.name())) {
            callConstructor(functionCallExpression);
        } else {
            callFunction(functionCallExpression);
        }
    }

    @SneakyThrows
    private void callFunction(FunctionCallExpression functionCallExpression) {
        // TODO
        if (functionCallExpression.name().equals(PRINT)) {
            List<Object> arguments = resolveArguments(functionCallExpression);
            String toPrint = arguments.stream().map(Object::toString).collect(Collectors.joining());
            if (testingMode) {
                outputBuffer.append(toPrint).append("\n");
            } else {
                System.out.println(toPrint);
            }
            return;
        }
        if (functionCallExpression.name().equals(ABORT)) {
            handleError(ErrorType.ABORTED);
        }
        // =====================================

        FunctionDefinition functionDefinition = program.functionDefinitions().get(functionCallExpression.name());
        List<Parameter> parameters = functionDefinition.parameters();
        List<Object> arguments = resolveArguments(functionCallExpression);
        assertEqualSize(parameters, arguments);
        environment.enterFunctionCall();
        for (int i = 0; i < arguments.size(); ++i) {
            environment.store(parameters.get(i).name(), arguments.get(i));
        }
        functionDefinition.accept(this);
        environment.exitCurrentCall();
        returning = false;
    }

    private void callConstructor(FunctionCallExpression functionCallExpression) {
        FunctionDefinition functionDefinition = constructors.get(functionCallExpression.name());
        List<Parameter> parameters = functionDefinition.parameters();
        List<Object> arguments = resolveArguments(functionCallExpression);
        assertEqualSize(parameters, arguments);
        environment.enterConstructorCall(program.classDefinitions().get(functionDefinition.name()));
        for (int i = 0; i < arguments.size(); ++i) {
            environment.store(parameters.get(i).name(), arguments.get(i));
        }
        functionDefinition.accept(this);
        lastValue = environment.getSelfObject();
        environment.exitCurrentCall();
    }

    private void callMethod(FunctionCallExpression functionCallExpression) {
        isMethodCalled = false;
        UserObject accessedObject = (UserObject) consumeLastValue();
        FunctionDefinition methodDefinition = accessedObject.getMethodDefinition(functionCallExpression.name());
        List<Parameter> parameters = methodDefinition.parameters();
        List<Object> arguments = resolveArguments(functionCallExpression);
        assertEqualSize(parameters, arguments);
        environment.enterMethodCall(accessedObject);
        for (int i = 0; i < arguments.size(); ++i) {
            environment.store(parameters.get(i).name(), arguments.get(i));
        }
        methodDefinition.accept(this);
        environment.exitCurrentCall();
        returning = false;
    }

    private List<Object> resolveArguments(FunctionCallExpression functionCallExpression) {
        List<Object> arguments = new ArrayList<>();
        for (Expression argument : functionCallExpression.arguments()) {
            argument.accept(this);
            arguments.add(consumeEvaluatedLastValue());
        }
        return arguments;
    }


    @SneakyThrows
    @Override
    public void visit(IdentifierExpression expression) {
        Object result = environment.find(expression.name());
        if (result != null) {
            lastValue = result;
        } else {
            handleError(ErrorType.VARIABLE_NOT_DEFINED_IN_SCOPE);
        }
    }

    @SneakyThrows
    @Override
    public void visit(NegatedExpression expression) {
        expression.accept(this);
        Object object = consumeLastValue();
        Object result = NegationComputer.compute(object);
        if (result != null) {
            lastValue = result;
        } else {
            handleError(ErrorType.OPERATION_NOT_SUPPORTED);
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
        // right side
        statement.expression().accept(this);
        Object right = consumeEvaluatedLastValue();
        // left side
        switch (statement.left()) {
            case IdentifierExpression identifier -> environment.store(identifier.name(), right);
            case ObjectAccess objectAccess
                && objectAccess.left() instanceof SelfAccess
                && objectAccess.right() instanceof IdentifierExpression attribute -> // defining attribute
                    environment.storeAttribute(attribute.name(), right);
            default -> {
                statement.left().accept(this);
                Object leftAccess = consumeLastValue();
                if (leftAccess instanceof Accessible accessible) {
                    environment.storeAttribute(accessible.getAttributeName(), right);
                } else {
                    handleError(ErrorType.ASSIGNMENT_INCORRECT);
                }
            }
        }
    }

    @Override
    public void visit(ForStatement statement) {
        // TODO - blocked by list
    }

    @SneakyThrows
    @Override
    public void visit(IfStatement statement) {
        statement.condition().accept(this);
        Object condition = consumeEvaluatedLastValue();
        if (!(condition instanceof Boolean)) {
            handleError(ErrorType.CONDITION_NOT_BOOLEAN);
        }
        environment.enterScope();
        if ((Boolean) condition) {
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
        environment.exitScope();
    }

    @SneakyThrows
    @Override
    public void visit(ObjectAccess objectAccess) {
        // left side
        UserObject accessedObject = null;
        Expression left = objectAccess.left();
        switch (left) {
            case IdentifierExpression identifierLeft -> accessedObject = findUserObject(identifierLeft);
            case SelfAccess ignored -> accessedObject = environment.getSelfObject();
            case ObjectAccess ignored -> accessedObject = extractAccessedObject(left);
            case FunctionCallExpression ignored -> accessedObject = extractAccessedObject(left);
            case default -> {
            }    // situation not allowed by grammar
        }
        // right side
        switch (objectAccess.right()) {
            case FunctionCallExpression functionCall -> handleMethodCall(accessedObject, functionCall);
            case IdentifierExpression identifierRight -> handleAttributeAccess(accessedObject, identifierRight);
            default -> {
            }    // situation not allowed by grammar
        }
    }

    private UserObject extractAccessedObject(Expression expression) throws SemanticException {
        expression.accept(this);
        Object nestedObject = consumeEvaluatedLastValue();
        UserObject accessedObject = null;
        if (nestedObject instanceof UserObject nestedUserObject) {
            accessedObject = nestedUserObject;
        } else {
            handleError(ErrorType.ACCESS_NOT_ALLOWED);    // not a valid access
        }
        return accessedObject;
    }

    private void handleAttributeAccess(UserObject accessedObject, IdentifierExpression identifier) throws Exception {
        if (accessedObject.hasAttribute(identifier.name())) {
            lastValue = new Accessible(accessedObject, identifier.name());
        } else {
            String errorMessage = accessedObject.getClassName() + "." + identifier.name();
            handleError(ErrorType.ATTRIBUTE_NOT_DEFINED, identifier.position(), errorMessage);
        }
    }

    private void handleMethodCall(UserObject accessedObject, FunctionCallExpression functionCall) throws SemanticException {
        if (accessedObject.hasMethod(functionCall.name())) {
            isMethodCalled = true;
            lastValue = accessedObject;
            functionCall.accept(this);
        } else {
            handleError(ErrorType.METHOD_NOT_DEFINED);
        }
    }

    private UserObject findUserObject(IdentifierExpression identifier) throws SemanticException {
        Object object = environment.find(identifier.name());
        UserObject userObject = null;
        switch (object) {
            case UserObject instance -> userObject = instance;
            case null -> handleError(ErrorType.VARIABLE_NOT_DEFINED_IN_SCOPE); // not in scope
            default -> handleError(ErrorType.ACCESS_NOT_ALLOWED); // not a valid access
        }
        return userObject;
    }

    @Override
    public void visit(ReturnStatement statement) {
        statement.expression().accept(this);
        if (lastValue instanceof Accessible accessible) {
            lastValue = accessible.get();
        }
        returning = true;
    }

    @Override
    public void visit(Parameter parameter) {
    }

    @SneakyThrows
    private void assertEqualSize(List<Parameter> a, List<Object> b) {
        if (a.size() != b.size()) {
            handleError(ErrorType.INCORRECT_NUMBER_OF_ARGUMENTS);
        }
    }

    private Object consumeEvaluatedLastValue() {
        Object last = consumeLastValue();
        if (last instanceof Accessible accessible) {
            last = accessible.get();
        }
        return last;
    }

    private Object consumeLastValue() {
        Object temp = lastValue;
        lastValue = null;
        return temp;
    }

    private void handleError(ErrorType errorType) throws SemanticException {
        throw new SemanticException(errorType.toString());
    }

    private void handleError(ErrorType type, Position position, String errorMessage) throws Exception {
        ErrorDetails errorDetails = new ErrorInterpreterDetails(type, position, errorMessage);
        errorHandler.handleError(errorDetails);
        throw new SemanticException(type.toString());
    }
}

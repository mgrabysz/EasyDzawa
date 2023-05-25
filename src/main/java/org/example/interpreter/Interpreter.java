package org.example.interpreter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.example.commons.Position;
import org.example.error.ErrorHandler;
import org.example.error.details.ErrorDetails;
import org.example.error.details.ErrorInterpreterDetails;
import org.example.error.enums.ErrorType;
import org.example.error.exception.SemanticException;
import org.example.interpreter.accessible.UserObject;
import org.example.interpreter.accessible.ValueReference;
import org.example.interpreter.computers.*;
import org.example.interpreter.computers.enums.LogicalOperation;
import org.example.interpreter.computers.enums.MathematicalOperation;
import org.example.interpreter.environment.ContextType;
import org.example.interpreter.environment.Environment;
import org.example.programstructure.containers.*;
import org.example.programstructure.expression.*;
import org.example.programstructure.statement.*;
import org.example.properties.LanguageProperties;
import org.example.visitor.ErrorContextBuilder;
import org.example.visitor.Visitor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Interpreter implements Visitor {

    private static final String MAIN = LanguageProperties.get("MAIN");
    private static final String PRINT = LanguageProperties.get("PRINT");
    private static final String ABORT = LanguageProperties.get("ABORT");
    private static final String THIS = LanguageProperties.get("THIS");

    private final Environment environment = new Environment();
    private final Map<String, FunctionDefinition> constructors = new HashMap<>();
    private final ErrorHandler errorHandler;

    private Program program;
    private Object lastValue;
    private boolean returning = false;
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
            handleError(ErrorType.MAIN_FUNCTION_MISSING, new Position(1, 1), StringUtils.EMPTY);
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
            handleError(ErrorType.CONSTRUCTOR_MISSING, classDefinition.position(), classDefinition.name());
        }
        boolean hasReturnStatement = optionalConstructor.stream()
                .flatMap(f -> f.block().statements().stream())
                .anyMatch(s -> s instanceof ReturnStatement);
        if (hasReturnStatement) {
            handleError(ErrorType.CONSTRUCTOR_CONTAINS_RETURN, classDefinition.position(), classDefinition.name());
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
            consumeLastValue();
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
            handleError(ErrorType.OPERATION_NOT_SUPPORTED, expression.position(), ErrorContextBuilder.buildContext(expression));
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
            handleError(ErrorType.OPERATION_NOT_SUPPORTED, expression.position(), ErrorContextBuilder.buildContext(expression));
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
            handleError(ErrorType.OPERATION_NOT_SUPPORTED, expression.position(), ErrorContextBuilder.buildContext(expression));
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
            handleError(ErrorType.OPERATION_NOT_SUPPORTED, expression.position(), ErrorContextBuilder.buildContext(expression));
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
            handleError(ErrorType.ZERO_DIVISION, expression.position(), ErrorContextBuilder.buildContext(expression));
        }
        Object result = MathematicalComputer.compute(left, right, operation);
        if (result != null) {
            lastValue = result;
        } else {
            handleError(ErrorType.OPERATION_NOT_SUPPORTED, expression.position(), ErrorContextBuilder.buildContext(expression));
        }
    }

    @SneakyThrows
    @Override
    public void visit(FunctionCallExpression functionCallExpression) {
        if (lastValue != null) {
            callMethod(functionCallExpression);
        } else if (constructors.containsKey(functionCallExpression.name())) {
            callConstructor(functionCallExpression);
        } else {
            callFunction(functionCallExpression);
        }
    }

    private void callFunction(FunctionCallExpression functionCallExpression) throws Exception {
        // TODO
        if (functionCallExpression.name().equals(PRINT)) {
            List<ValueReference> arguments = resolveArguments(functionCallExpression);
            String toPrint = arguments.stream()
                    .map(ValueReference::getValue)
                    .map(Object::toString).collect(Collectors.joining());
            if (testingMode) {
                outputBuffer.append(toPrint).append("\n");
            } else {
                System.out.println(toPrint);
            }
            return;
        }
        if (functionCallExpression.name().equals(ABORT)) {
            handleError(ErrorType.ABORTED, functionCallExpression.position(), StringUtils.EMPTY);
        }
        // =====================================

        FunctionDefinition functionDefinition = program.functionDefinitions().get(functionCallExpression.name());
        if (functionDefinition == null) {
            handleError(ErrorType.FUNCTION_NOT_DEFINED, functionCallExpression.position(), functionCallExpression.name());
        }
        List<Parameter> parameters = functionDefinition.parameters();
        List<ValueReference> arguments = resolveArguments(functionCallExpression);
        validateArguments(parameters, arguments, functionCallExpression);
        environment.enterFunctionCall();
        for (int i = 0; i < arguments.size(); ++i) {
            environment.store(parameters.get(i).name(), arguments.get(i));
        }
        functionDefinition.accept(this);
        environment.exitCurrentCall();
        returning = false;
    }

    private void callConstructor(FunctionCallExpression functionCallExpression) throws Exception {
        FunctionDefinition functionDefinition = constructors.get(functionCallExpression.name());
        List<Parameter> parameters = functionDefinition.parameters();
        List<ValueReference> arguments = resolveArguments(functionCallExpression);
        validateArguments(parameters, arguments, functionCallExpression);
        environment.enterConstructorCall();
        ClassDefinition classDefinition = program.classDefinitions().get(functionDefinition.name());
        UserObject userObject = new UserObject(classDefinition.name(), classDefinition.methods());
        ValueReference valueReference = new ValueReference(userObject);
        environment.store(THIS, valueReference);
        for (int i = 0; i < arguments.size(); ++i) {
            environment.store(parameters.get(i).name(), arguments.get(i));
        }
        functionDefinition.accept(this);
        lastValue = valueReference;
        environment.exitCurrentCall();
    }

    private void callMethod(FunctionCallExpression functionCallExpression) throws Exception {
        ValueReference valueReference = (ValueReference) consumeLastValue();
        UserObject accessedObject = (UserObject) valueReference.getValue();
        FunctionDefinition methodDefinition = accessedObject.getMethodDefinition(functionCallExpression.name());
        if (methodDefinition == null) {
            handleError(ErrorType.METHOD_NOT_DEFINED, functionCallExpression.position(),
                    StringUtils.join(accessedObject.getClassName(), ".", functionCallExpression.name(), "()"));
        }
        List<Parameter> parameters = methodDefinition.parameters();
        List<ValueReference> arguments = resolveArguments(functionCallExpression);
        validateArguments(parameters, arguments, functionCallExpression);
        environment.enterMethodCall();
        environment.store(THIS, valueReference);
        for (int i = 0; i < arguments.size(); ++i) {
            environment.store(parameters.get(i).name(), arguments.get(i));
        }
        methodDefinition.accept(this);
        environment.exitCurrentCall();
        returning = false;
    }

    private List<ValueReference> resolveArguments(FunctionCallExpression functionCallExpression) {
        List<ValueReference> arguments = new ArrayList<>();
        for (Expression argument : functionCallExpression.arguments()) {
            argument.accept(this);
            if (lastValue instanceof ValueReference valueReference) {
                if (isPrimitiveType(valueReference.getValue())) {
                    arguments.add(valueReference.clone());
                } else {
                    arguments.add(valueReference);
                }
                consumeLastValue();
            } else {
                ValueReference valueReference = new ValueReference(consumeLastValue());
                arguments.add(valueReference);
            }
        }
        return arguments;
    }

    private boolean isPrimitiveType(Object object) {
        return object instanceof Number || object instanceof Boolean || object instanceof String;
    }


    @SneakyThrows
    @Override
    public void visit(IdentifierExpression expression) {
        if (lastValue == null) {
            // accessing local variable
            Object result = environment.find(expression.name());
            if (result != null) {
                lastValue = result;
            } else if (environment.isAssignment()){
                ValueReference valueReference = new ValueReference();
                environment.store(expression.name(), valueReference);
                lastValue = valueReference;
            } else {
                handleError(ErrorType.VARIABLE_NOT_DEFINED_IN_SCOPE, expression.position(), expression.name());
            }
        } else {
            // accessing object attribute
            ValueReference valueReference = (ValueReference) consumeLastValue();
            UserObject accessedObject = (UserObject) valueReference.getValue();
            Object result = accessedObject.findAttribute(expression.name());
            if (result != null) {
                lastValue = result;
            } else if (environment.isAssignment() && environment.getContextType() == ContextType.CONSTRUCTOR) {
                ValueReference newValueReference = new ValueReference();
                accessedObject.storeAttribute(expression.name(), newValueReference);
                lastValue = newValueReference;
            } else {
                handleError(ErrorType.ATTRIBUTE_NOT_DEFINED, expression.position(),
                        StringUtils.join(accessedObject.getClassName(), '.', expression.name()));
            }
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
            handleError(ErrorType.OPERATION_NOT_SUPPORTED, expression.position(), ErrorContextBuilder.buildContext(expression));
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
    public void visit(ModifyAndAssignStatement statement) {
        environment.setIsAssignment(true);
        // left
        statement.left().accept(this);
        ValueReference valueReference = (ValueReference) consumeLastValue();
        Object oldValue = valueReference.getValue();
        environment.setIsAssignment(false);
        // right
        statement.right().accept(this);
        Object right = consumeEvaluatedLastValue();
        // modify and assign
        Object newValue = MathematicalComputer.compute(oldValue, right, OperationMapper.map(statement.additiveType()));
        valueReference.setValue(newValue);
    }

    @SneakyThrows
    @Override
    public void visit(AssignmentStatement statement) {
        environment.setIsAssignment(true);
        // left
        statement.left().accept(this);
        if (!(lastValue instanceof ValueReference)) {
            handleError(ErrorType.ASSIGNMENT_INCORRECT, statement.left().position(),
                    ErrorContextBuilder.buildContext(statement));
        }
        ValueReference valueReference = (ValueReference) consumeLastValue();
        environment.setIsAssignment(false);
        // right
        statement.right().accept(this);
        Object right = consumeEvaluatedLastValue();
        // assignment
        valueReference.setValue(right);
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
            handleError(ErrorType.CONDITION_NOT_BOOLEAN, statement.position(), ErrorContextBuilder.buildContext(statement));
        }
        environment.enterScope();
        if (condition.equals(Boolean.TRUE)) {
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
        objectAccess.left().accept(this);
        ValueReference valueReference = (ValueReference) lastValue;
        if (!(valueReference.getValue() instanceof UserObject)) {
            handleError(ErrorType.ACCESS_NOT_ALLOWED, objectAccess.position(), ErrorContextBuilder.buildContext((Statement) objectAccess));
        }
        objectAccess.right().accept(this);
    }

    @Override
    public void visit(ReturnStatement statement) {
        statement.expression().accept(this);
        returning = true;
    }

    private void validateArguments(List<Parameter> a, List<ValueReference> b, FunctionCallExpression functionCallExpression) throws Exception {
        if (a.size() != b.size()) {
            handleError(ErrorType.INCORRECT_NUMBER_OF_ARGUMENTS, functionCallExpression.position(),
                    ErrorContextBuilder.buildContext((Expression) functionCallExpression));
        }
    }

    @Override
    public void visit(Parameter parameter) {
    }

    @SneakyThrows
    @Override
    public void visit(SelfAccess expression) {
        ValueReference accessedObjectReference = (ValueReference) environment.find(THIS);
        if (accessedObjectReference != null) {
            lastValue = accessedObjectReference;
        } else {
            handleError(ErrorType.SELF_ACCESS_OUTSIDE_OF_CLASS, expression.position(), THIS);
        }
    }

    private Object consumeEvaluatedLastValue() {
        Object last = consumeLastValue();
        if (last instanceof ValueReference reference) {
            last = reference.getValue();
        }
        return last;
    }

    private Object consumeLastValue() {
        Object temp = lastValue;
        lastValue = null;
        return temp;
    }

    private void handleError(ErrorType type, Position position, String errorMessage) throws Exception {
        ErrorDetails errorDetails = new ErrorInterpreterDetails(type, position, errorMessage);
        errorHandler.handleError(errorDetails);
        throw new SemanticException(type.toString());
    }

}

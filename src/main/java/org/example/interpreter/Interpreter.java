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
import org.example.interpreter.accessible.ObjectInstance;
import org.example.interpreter.accessible.ValueReference;
import org.example.interpreter.builtins.*;
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
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class Interpreter implements Visitor {

    private static final String MAIN = LanguageProperties.get("MAIN");
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

    @SneakyThrows
    public void execute(Program program) {
        Initializer.addBuiltIns(program);
        this.program = program;
        // extracting all constructors
        for (ClassDefinition ClassDefinition : program.classDefinitions().values()) {
            ClassDefinition.accept(this);
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
    public void visit(UserClassDefinition userClassDefinition) {
        Optional<FunctionDefinition> optionalConstructor = userClassDefinition.methods()
                .values()
                .stream()
                .filter(method -> method.name().equals(userClassDefinition.name()))
                .findFirst();
        if (optionalConstructor.isEmpty()) {
            handleError(ErrorType.CONSTRUCTOR_MISSING, userClassDefinition.position(), userClassDefinition.name());
        }
        boolean hasReturnStatement = optionalConstructor.stream()
                .map(functionDefinition -> (UserFunctionDefinition) functionDefinition)
                .flatMap(f -> f.block().statements().stream())
                .anyMatch(s -> s instanceof ReturnStatement);
        if (hasReturnStatement) {
            handleError(ErrorType.CONSTRUCTOR_CONTAINS_RETURN, userClassDefinition.position(), userClassDefinition.name());
        }
        FunctionDefinition constructor = optionalConstructor.get();
        constructors.put(constructor.name(), constructor);
    }

    @Override
    public void visit(UserFunctionDefinition functionDefinition) {
        consumeLastValue();
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
        FunctionDefinition functionDefinition = program.functionDefinitions().get(functionCallExpression.name());
        if (functionDefinition == null) {
            handleError(ErrorType.FUNCTION_NOT_DEFINED, functionCallExpression.position(), functionCallExpression.name());
            return;
        }
        List<Parameter> parameters = functionDefinition.parameters();
        List<ValueReference> arguments = resolveArguments(functionCallExpression);
        validateArguments(parameters, arguments, functionCallExpression);
        environment.enterFunctionCall();
        if (parameters == null) {
            // variable number of parameters
            environment.store(VariadicFunction.ARGS, arguments);
        } else {
            for (int i = 0; i < arguments.size(); ++i) {
                environment.store(parameters.get(i).name(), arguments.get(i));
            }
        }
        lastValue = functionCallExpression;
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
        ObjectInstance objectInstance = new ObjectInstance(classDefinition.name(), classDefinition.methods());
        ValueReference valueReference = new ValueReference(objectInstance);
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
        ObjectInstance accessedObject = (ObjectInstance) valueReference.getValue();
        FunctionDefinition methodDefinition = accessedObject.getMethodDefinition(functionCallExpression.name());
        if (methodDefinition == null) {
            handleError(ErrorType.METHOD_NOT_DEFINED, functionCallExpression.position(),
                    StringUtils.join(accessedObject.getClassName(), ".", functionCallExpression.name(), "()"));
            return;
        }
        List<Parameter> parameters = methodDefinition.parameters();
        List<ValueReference> arguments = resolveArguments(functionCallExpression);
        validateArguments(parameters, arguments, functionCallExpression);
        environment.enterMethodCall();
        environment.store(THIS, valueReference);
        for (int i = 0; i < arguments.size(); ++i) {
            environment.store(parameters.get(i).name(), arguments.get(i));
        }
        lastValue = functionCallExpression;
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
            ObjectInstance accessedObject = (ObjectInstance) valueReference.getValue();
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

    @SneakyThrows
    @Override
    public void visit(ModifyAndAssignStatement statement) {
        environment.setIsAssignment(true);
        // left
        statement.left().accept(this);
        if (!(lastValue instanceof ValueReference)) {
            handleError(ErrorType.ASSIGNMENT_INCORRECT, statement.left().position(),
                    ErrorContextBuilder.buildContext(statement));
        }
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


    @SneakyThrows
    @Override
    public void visit(ForStatement statement) {
        environment.enterScope();
        statement.range().accept(this);
        Object rangeExpression = consumeEvaluatedLastValue();
        if (rangeExpression instanceof ListInstance listInstance) {
            List<Object> range = listInstance.getList();
            for(Object object : range) {
                environment.store(statement.iteratorName(), object);
                statement.block().accept(this);
            }
        } else {
            handleError(ErrorType.RANGE_NOT_ITERABLE, statement.range().position(), ErrorContextBuilder.buildContext(statement));
        }
        environment.exitScope();
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
        if (!(valueReference.getValue() instanceof ObjectInstance)) {
            handleError(ErrorType.ACCESS_NOT_ALLOWED, objectAccess.position(), ErrorContextBuilder.buildContext((Statement) objectAccess));
        }
        objectAccess.right().accept(this);
    }

    @Override
    public void visit(ReturnStatement statement) {
        statement.expression().accept(this);
        returning = true;
    }

    @Override
    public void visit(PrintFunction printFunction) {
        Object args = environment.find(VariadicFunction.ARGS);
        if (!(args instanceof Collection)) {
            throw new IllegalStateException();
        }
        List<?> list = new ArrayList<>((Collection<?>) args);
        String toPrint = list.stream()
                .map(o -> (ValueReference) o)
                .map(ValueReference::getValue)
                .map(Object::toString)
                .collect(Collectors.joining());
        if (testingMode) {
            outputBuffer.append(toPrint).append("\n");
        } else {
            System.out.println(toPrint);
        }
    }

    @SneakyThrows
    @Override
    public void visit(AbortFunction abortFunction) {
        FunctionCallExpression functionCallExpression = (FunctionCallExpression) consumeLastValue();
        handleError(ErrorType.ABORTED, functionCallExpression.position(), StringUtils.EMPTY);
    }

    @SneakyThrows
    @Override
    public void visit(RangeFunction rangeFunction) {
        Integer start = extractNumericArg(RangeFunction.START);
        Integer stop = extractNumericArg(RangeFunction.STOP);
        if (start == null || stop == null) {
            throw new IllegalStateException();
        }
        lastValue = new ListInstance(IntStream.range(start, stop)
                .boxed()
                .collect(Collectors.toList()));
        returning = true;
    }

    @Override
    public void visit(ListDefinition listDefinition) {
        constructors.put(ListConstructor.LIST, new ListConstructor());
    }

    @Override
    public void visit(ListConstructor listConstructor) {
        ValueReference valueReference = (ValueReference) environment.find(THIS);
        valueReference.setValue(new ListInstance());
    }

    @Override
    public void visit(AppendMethod method) {
        ValueReference valueReference = (ValueReference) environment.find(THIS);
        ListInstance listInstance = (ListInstance) valueReference.getValue();
        Object item = environment.find(AppendMethod.ITEM);
        listInstance.getList().add(item);
    }

    @SneakyThrows
    @Override
    public void visit(GetMethod method) {
        Integer index = extractNumericArg(GetMethod.INDEX);
        if (index == null) {
            throw new IllegalStateException();
        }
        ValueReference selfReference = (ValueReference) environment.find(THIS);
        ListInstance listInstance = (ListInstance) selfReference.getValue();
        List<Object> list = listInstance.getList();
        if (index > list.size() - 1 || index < 0) {
            FunctionCallExpression functionCallExpression = (FunctionCallExpression) consumeLastValue();
            handleError(ErrorType.INDEX_OUT_OF_BOUND, functionCallExpression.position(),
                    ErrorContextBuilder.buildContext((Expression) functionCallExpression));
        }
        lastValue = list.get(index);
        returning = true;
    }

    @SneakyThrows
    @Override
    public void visit(RemoveMethod method) {
        Integer index = extractNumericArg(RemoveMethod.INDEX);
        if (index == null) {
            throw new IllegalStateException();
        }
        ValueReference selfReference = (ValueReference) environment.find(THIS);
        ListInstance listInstance = (ListInstance) selfReference.getValue();
        List<Object> list = listInstance.getList();
        if (index > list.size() - 1 || index < 0) {
            FunctionCallExpression functionCallExpression = (FunctionCallExpression) consumeLastValue();
            handleError(ErrorType.INDEX_OUT_OF_BOUND, functionCallExpression.position(),
                    ErrorContextBuilder.buildContext((Expression) functionCallExpression));
        }
        lastValue = list.remove(index);
        returning = true;
    }

    @SneakyThrows
    @Override
    public void visit(LengthMethod method) {
        ValueReference selfReference = (ValueReference) environment.find(THIS);
        ListInstance listInstance = (ListInstance) selfReference.getValue();
        lastValue = listInstance.getList().size();
        returning = true;
    }

    private Integer extractNumericArg(String argName) throws Exception {
        ValueReference valueReference = (ValueReference) environment.find(argName);
        Object arg = valueReference.getValue();
        if (arg instanceof Integer index) {
            return index;
        } else {
            FunctionCallExpression functionCallExpression = (FunctionCallExpression) consumeLastValue();
            handleError(ErrorType.OPERATION_NOT_SUPPORTED, functionCallExpression.position(),
                    ErrorContextBuilder.buildContext((Expression) functionCallExpression));
            return null;
        }
    }

    private void validateArguments(List<Parameter> a, List<ValueReference> b, FunctionCallExpression functionCallExpression) throws Exception {
        if (a == null) {
            return;     // function accepts variable number of parameters
        }
        if (a.size() != b.size()) {
            handleError(ErrorType.INCORRECT_NUMBER_OF_ARGUMENTS, functionCallExpression.position(),
                    ErrorContextBuilder.buildContext((Expression) functionCallExpression));
        }
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

    @Override
    public void visit(Parameter parameter) {
    }

    @Override
    public void visit(Program program) {
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

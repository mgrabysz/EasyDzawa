package org.example.visitor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.LanguageProperties;
import org.example.programstructure.containers.*;
import org.example.programstructure.expression.*;
import org.example.programstructure.expression.enums.AdditiveType;
import org.example.programstructure.expression.enums.MultiplicativeType;
import org.example.programstructure.expression.enums.RelationalType;
import org.example.programstructure.statement.*;

import java.util.Iterator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorContextWriter implements Visitor {

    private final StringBuilder contextBuilder = new StringBuilder();

    public static String buildContext(Expression expression) {
        ErrorContextWriter errorContextWriter = new ErrorContextWriter();
        expression.accept(errorContextWriter);
        return errorContextWriter.contextBuilder.toString();
    }

    @Override
    public void visit(Program program) {

    }

    @Override
    public void visit(FunctionDefinition functionDefinition) {

    }

    @Override
    public void visit(ClassDefinition classDefinition) {

    }

    @Override
    public void visit(Block block) {

    }

    @Override
    public void visit(Parameter parameter) {

    }

    @Override
    public void visit(OrExpression expression) {
        expression.left().accept(this);
        contextBuilder.append(StringUtils.SPACE)
                .append(LanguageProperties.get("OR"))
                .append(StringUtils.SPACE);
        expression.right().accept(this);
    }

    @Override
    public void visit(AndExpression expression) {
        expression.left().accept(this);
        contextBuilder.append(StringUtils.SPACE)
                .append(LanguageProperties.get("AND"))
                .append(StringUtils.SPACE);
        expression.right().accept(this);
    }

    @Override
    public void visit(RelationalExpression expression) {
        expression.left().accept(this);
        contextBuilder.append(StringUtils.SPACE)
                .append(mapSymbol(expression.relationalType()))
                .append(StringUtils.SPACE);
        expression.right().accept(this);
    }

    @Override
    public void visit(ArithmeticExpression expression) {
        expression.left().accept(this);
        contextBuilder.append(StringUtils.SPACE)
                .append(mapSymbol(expression.additiveType()))
                .append(StringUtils.SPACE);
        expression.right().accept(this);
    }

    @Override
    public void visit(MultiplicativeExpression expression) {
        expression.left().accept(this);
        contextBuilder.append(StringUtils.SPACE)
                .append(mapSymbol(expression.multiplicativeType()))
                .append(StringUtils.SPACE);
        expression.right().accept(this);
    }

    @Override
    public void visit(FunctionCallExpression expression) {
        contextBuilder.append(expression.name())
                .append('(');
        Iterator<Expression> it = expression.arguments().iterator();
        while (it.hasNext()) {
            Expression argument = it.next();
            argument.accept(this);
            if (it.hasNext()) {
                contextBuilder.append(',');
            }
        }
        contextBuilder.append(')');
    }

    @Override
    public void visit(IdentifierExpression expression) {
        contextBuilder.append(expression.name());
    }

    @Override
    public void visit(NegatedExpression expression) {
        String minus = "-";
        String symbol = switch (expression.expression()) {
            case LiteralInteger ignored -> minus;
            case LiteralFloat ignored -> minus;
            case ArithmeticExpression ignored -> minus;
            case MultiplicativeExpression ignored -> minus;
            default -> LanguageProperties.get("NOT");
        };
        contextBuilder.append(symbol)
                .append(StringUtils.SPACE);
        expression.expression().accept(this);
    }

    @Override
    public void visit(LiteralBool expression) {
        if (expression.value() == Boolean.TRUE) {
            contextBuilder.append(LanguageProperties.get("TRUE"));
        } else
            contextBuilder.append(LanguageProperties.get("FALSE"));
    }

    @Override
    public void visit(LiteralFloat expression) {
        contextBuilder.append(expression.value());
    }

    @Override
    public void visit(LiteralInteger expression) {
        contextBuilder.append(expression.value());
    }

    @Override
    public void visit(LiteralText expression) {
        contextBuilder.append(expression.value());
    }

    @Override
    public void visit(SelfAccess expression) {
        contextBuilder.append(LanguageProperties.get("THIS"));
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

    }

    @Override
    public void visit(ObjectAccess statement) {
        statement.left().accept(this);
        contextBuilder.append('.');
        statement.right().accept(this);
    }

    @Override
    public void visit(ReturnStatement statement) {

    }

    private String mapSymbol(MultiplicativeType type) {
        return switch (type) {
            case MULTIPLY -> "*";
            case DIVIDE -> "/";
        };
    }

    private String mapSymbol(AdditiveType type) {
        return switch (type) {
            case ADD -> "+";
            case SUBTRACT -> "-";
        };
    }

    private String mapSymbol(RelationalType type) {
        return switch (type) {
            case EQUAL -> "==";
            case NOT_EQUAL -> "!=";
            case GREATER -> ">";
            case LESS -> "<";
            case GREATER_OR_EQUAL -> ">=";
            case LESS_OR_EQUAL -> "<=";
        };
    }
}

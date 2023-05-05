package org.example.programstructure.expression;

import org.example.Position;

import java.util.List;

public record FunctionCallExpression(String name, List<Expression> arguments, Position position) implements Expression {
}

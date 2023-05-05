package org.example.programstructure.expression;

import org.example.Position;

public record NegatedExpression(Expression expression, Position position) implements Expression {
}

package org.example.programstructure.expression;

import org.example.Position;

public record IdentifierExpression(String name, Position position) implements Expression {
}

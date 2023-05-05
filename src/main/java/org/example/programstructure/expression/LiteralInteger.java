package org.example.programstructure.expression;

import org.example.Position;

public record LiteralInteger(Integer value, Position position) implements Expression {
}

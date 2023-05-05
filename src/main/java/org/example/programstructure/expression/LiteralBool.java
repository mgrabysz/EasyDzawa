package org.example.programstructure.expression;

import org.example.Position;

public record LiteralBool(Boolean value, Position position) implements Expression {
}

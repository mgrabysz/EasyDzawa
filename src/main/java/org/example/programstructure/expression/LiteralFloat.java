package org.example.programstructure.expression;

import org.example.Position;

public record LiteralFloat(Double value, Position position) implements Expression {
}

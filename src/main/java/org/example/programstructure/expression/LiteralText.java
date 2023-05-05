package org.example.programstructure.expression;

import org.example.Position;

public record LiteralText(String value, Position position) implements Expression {
}

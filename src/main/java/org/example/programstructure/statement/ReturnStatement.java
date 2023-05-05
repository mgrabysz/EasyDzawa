package org.example.programstructure.statement;

import org.example.programstructure.expression.Expression;

public record ReturnStatement(Expression expression) implements Statement {
}

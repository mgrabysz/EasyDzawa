package org.example.programstructure.statement;

import org.example.programstructure.expression.Expression;

public record AssignmentStatement(Expression objectAccess, Expression expression) implements Statement {
}

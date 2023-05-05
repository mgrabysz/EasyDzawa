package org.example.programstructure.statement;

import org.example.programstructure.expression.Expression;

public record SubtractAndAssignStatement(Expression objectAccess, Expression expression) implements Statement {
}

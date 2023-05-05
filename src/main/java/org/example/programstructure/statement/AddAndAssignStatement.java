package org.example.programstructure.statement;

import org.example.programstructure.expression.Expression;

public record AddAndAssignStatement(Expression objectAccess, Expression expression) implements Statement {
}

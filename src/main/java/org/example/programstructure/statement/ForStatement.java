package org.example.programstructure.statement;

import org.example.programstructure.containers.Block;
import org.example.programstructure.expression.Expression;

public record ForStatement(String iteratorName, Expression range, Block block) implements Statement {
}

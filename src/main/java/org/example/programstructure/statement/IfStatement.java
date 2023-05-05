package org.example.programstructure.statement;

import org.example.programstructure.containers.Block;
import org.example.programstructure.expression.Expression;

public record IfStatement(Expression condition, Block blockIfTrue, Block elseBlock) implements Statement {
}

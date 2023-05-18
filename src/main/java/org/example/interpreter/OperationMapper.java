package org.example.interpreter;

import lombok.experimental.UtilityClass;
import org.example.interpreter.enums.MathematicalOperation;
import org.example.interpreter.enums.RelationalOperation;
import org.example.programstructure.expression.enums.AdditiveType;
import org.example.programstructure.expression.enums.MultiplicativeType;
import org.example.programstructure.expression.enums.RelationalType;

import java.util.Map;

@UtilityClass
public class OperationMapper {

	public static MathematicalOperation map(AdditiveType additiveType) {
		return ADDITIVE.get(additiveType);
	}

	public static MathematicalOperation map(MultiplicativeType multiplicativeType) {
		return MULTIPLICATIVE.get(multiplicativeType);
	}

	public static RelationalOperation map(RelationalType relationalType) {
		return RELATIONAL.get(relationalType);
	}

	private static final Map<AdditiveType, MathematicalOperation> ADDITIVE = Map.of(
			AdditiveType.ADD, MathematicalOperation.ADD,
			AdditiveType.SUBTRACT, MathematicalOperation.SUBTRACT
	);

	private static final Map<MultiplicativeType, MathematicalOperation> MULTIPLICATIVE = Map.of(
			MultiplicativeType.MULTIPLY, MathematicalOperation.MULTIPLY,
			MultiplicativeType.DIVIDE, MathematicalOperation.DIVIDE
	);

	private static final Map<RelationalType, RelationalOperation> RELATIONAL = Map.of(
			RelationalType.EQUAL, RelationalOperation.EQUAL,
			RelationalType.NOT_EQUAL, RelationalOperation.NOT_EQUAL,
			RelationalType.GREATER, RelationalOperation.GREATER,
			RelationalType.GREATER_OR_EQUAL, RelationalOperation.GREATER_OR_EQUAL,
			RelationalType.LESS, RelationalOperation.LESS,
			RelationalType.LESS_OR_EQUAL, RelationalOperation.LESS_OR_EQUAL
	);

}

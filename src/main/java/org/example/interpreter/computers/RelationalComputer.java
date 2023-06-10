package org.example.interpreter.computers;

import lombok.experimental.UtilityClass;
import org.example.interpreter.computers.enums.RelationalOperation;

import java.util.Objects;

@UtilityClass
public class RelationalComputer {

	public static Object compute(Object left, Object right, RelationalOperation operation) {
		if (left instanceof Number leftNum && right instanceof Number rightNum) {
			return computeDoubles(leftNum.doubleValue(), rightNum.doubleValue(), operation);
		}
		return null;
	}

	private static Boolean computeDoubles(Double left, Double right, RelationalOperation operation) {
		return switch (operation) {
			case EQUAL -> Objects.equals(left, right);
			case NOT_EQUAL -> !Objects.equals(left, right);
			case GREATER -> left > right;
			case LESS -> left < right;
			case GREATER_OR_EQUAL -> left >= right;
			case LESS_OR_EQUAL -> left <= right;
		};
	}

}

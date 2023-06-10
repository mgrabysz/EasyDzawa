package org.example.interpreter.computers;

import lombok.experimental.UtilityClass;
import org.example.interpreter.computers.enums.MathematicalOperation;

@UtilityClass
public class MathematicalComputer {

	public static Object compute(Object left, Object right, MathematicalOperation operation) {
		if (!(left instanceof Number leftNum && right instanceof Number rightNum)) {
			return null;
		}
		if (left instanceof Integer leftInt && right instanceof Integer rightInt) {
			return computeIntegers(leftInt, rightInt, operation);
		}
		return computeDoubles(leftNum.doubleValue(), rightNum.doubleValue(), operation);
	}

	private static Integer computeIntegers(Integer left, Integer right, MathematicalOperation operation) {
		return switch (operation) {
			case ADD -> left + right;
			case SUBTRACT -> left - right;
			case MULTIPLY -> left * right;
			case DIVIDE -> left / right;
		};
	}

	private static Double computeDoubles(Double left, Double right, MathematicalOperation operation) {
		return switch (operation) {
			case ADD -> left + right;
			case SUBTRACT -> left - right;
			case MULTIPLY -> left * right;
			case DIVIDE -> left / right;
		};
	}

}

package org.example.interpreter;

public class MathematicalComputer {

	public static Object compute(Object left, Object right, MathematicalOperation operation) {
		Object result;
		switch (left) {
			case Integer leftInt -> {
				switch (right) {
					case Integer rightInt -> result = computeIntegers(leftInt, rightInt, operation);
					case Double rightDouble -> result = computeDoubles(Double.valueOf(leftInt), rightDouble, operation);
					default -> result = null;
				}
			}
			case Double leftDouble -> {
				if (right instanceof Number rightInt) {
					result = computeDoubles(leftDouble, rightInt.doubleValue(), operation);
				} else {
					result = null;
				}
			}
			default -> result = null;
		}
		return result;
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

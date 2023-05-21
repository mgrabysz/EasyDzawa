package org.example.interpreter.computers;

import lombok.experimental.UtilityClass;
import org.example.interpreter.computers.enums.LogicalOperation;

@UtilityClass
public class LogicalComputer {

	public static Object compute(Object left, Object right, LogicalOperation operation) {
		if (left instanceof Boolean lBool && right instanceof Boolean rBool) {
			return computeBooleans(lBool, rBool, operation);
		}
		return null;
	}

	private static Boolean computeBooleans(Boolean left, Boolean right, LogicalOperation operation) {
		return switch (operation) {
			case AND -> left && right;
			case OR -> left || right;
		};
	}

}

package org.example.interpreter.computers;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NegationComputer {

	public static Object compute(Object object) {
		return switch (object) {
			case Integer i -> -i;
			case Double d -> -d;
			case Boolean b -> !b;
			default -> null;
		};
	}
}

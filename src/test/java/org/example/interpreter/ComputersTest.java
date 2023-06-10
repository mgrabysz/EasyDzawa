package org.example.interpreter;

import org.example.interpreter.computers.MathematicalComputer;
import org.example.interpreter.computers.RelationalComputer;
import org.example.interpreter.computers.enums.MathematicalOperation;
import org.example.interpreter.computers.enums.RelationalOperation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComputersTest {

	private static Stream<Arguments> testMathematicalComputer() {
		return Stream.of(
				Arguments.of(10, 4, 6, MathematicalOperation.ADD),
				Arguments.of(-2, 4, 6, MathematicalOperation.SUBTRACT),
				Arguments.of(24, 4, 6, MathematicalOperation.MULTIPLY),
				Arguments.of(1, 3, 2, MathematicalOperation.DIVIDE),
				Arguments.of(5., 3., 2., MathematicalOperation.ADD),
				Arguments.of(1., 3., 2, MathematicalOperation.SUBTRACT),
				Arguments.of(6., 3., 2, MathematicalOperation.MULTIPLY),
				Arguments.of(1.5, 3., 2., MathematicalOperation.DIVIDE),
				Arguments.of(null, "", 2, MathematicalOperation.DIVIDE),
				Arguments.of(null, 2, "", MathematicalOperation.DIVIDE)
		);
	}

	@ParameterizedTest
	@MethodSource
	void testMathematicalComputer(Object expected, Object left, Object right, MathematicalOperation operation) {
		Object actual = MathematicalComputer.compute(left, right, operation);
		assertEquals(expected, actual);
	}

	private static Stream<Arguments> testRelationalComputer() {
		return Stream.of(
				Arguments.of(true, 4, 4, RelationalOperation.EQUAL),
				Arguments.of(false, 4, 4, RelationalOperation.NOT_EQUAL),
				Arguments.of(true, 8., 6, RelationalOperation.GREATER),
				Arguments.of(false, 3., 2., RelationalOperation.LESS),
				Arguments.of(true, 3., 2., RelationalOperation.GREATER_OR_EQUAL),
				Arguments.of(true, 3, 3., RelationalOperation.LESS_OR_EQUAL),
				Arguments.of(null, "", 2, RelationalOperation.EQUAL),
				Arguments.of(null, 2, "", RelationalOperation.NOT_EQUAL)
		);
	}

	@ParameterizedTest
	@MethodSource
	void testRelationalComputer(Object expected, Object left, Object right, RelationalOperation operation) {
		Object actual = RelationalComputer.compute(left, right, operation);
		assertEquals(expected, actual);
	}

}

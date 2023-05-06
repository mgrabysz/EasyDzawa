package org.example.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.Position;

@AllArgsConstructor
@Getter
@Setter
public class ErrorContext {

	private Position position;
	private String context;

}

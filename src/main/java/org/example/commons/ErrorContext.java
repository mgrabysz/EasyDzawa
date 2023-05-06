package org.example.commons;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.commons.Position;

@AllArgsConstructor
@Getter
@Setter
public class ErrorContext {

	private Position position;
	private String context;

}

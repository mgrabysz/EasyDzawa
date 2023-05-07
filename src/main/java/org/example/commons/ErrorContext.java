package org.example.commons;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ErrorContext {

	private Position position;
	private String context;

}

package org.example.programstructure.containers;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;

@RequiredArgsConstructor
public class Program {

	private final HashMap<String, FunctionDefinition> functionDefinitions;
	private final HashMap<String, ClassDefinition> classDefinitions;

}

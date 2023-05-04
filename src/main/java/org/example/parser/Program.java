package org.example.parser;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;

@RequiredArgsConstructor
public class Program extends Node {

	private final HashMap<String, FunctionDefinition> functionDefinitions;
	private final HashMap<String, ClassDefinition> classDefinitions;


}

package org.example.interpreter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Accessible {

	private UserObject userObject;
	private String name;

	public void setTo(Object value) {
		userObject.storeAttribute(name, value);
	}

	public Object get() {
		return userObject.findAttribute(name);
	}

}

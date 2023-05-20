package org.example.interpreter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Accessible {

	private UserObject userObject;
	@Getter
	private String attributeName;

	public void setTo(Object value) {
		userObject.storeAttribute(attributeName, value);
	}

	public Object get() {
		return userObject.findAttribute(attributeName);
	}

}

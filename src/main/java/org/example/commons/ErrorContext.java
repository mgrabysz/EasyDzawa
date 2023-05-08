package org.example.commons;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.example.token.Token;
import org.example.token.TokenType;

@AllArgsConstructor
@Getter
public class ErrorContext {

	private Position position;
	private StringBuilder contextBuilder;

	public String getContext() {
		return contextBuilder.toString();
	}

	public void update(Token token) {
		if (token.getType() == TokenType.DOT) {
			stripLast();
			contextBuilder.append(".");
		} else {
			contextBuilder.append(token.getValue().toString())
					.append(StringUtils.SPACE);
		}
		this.position = token.getPosition();
	}

	public void reset() {
		contextBuilder.delete(0, contextBuilder.length());
	}

	private void stripLast() {
		if (!contextBuilder.isEmpty()) {
			contextBuilder.setLength(contextBuilder.length() - 1);
		}
	}

}

package org.example.error;

import org.example.error.details.ErrorDetails;

public interface ErrorHandler {

	void handleError(ErrorDetails errorDetails) throws Exception;

}

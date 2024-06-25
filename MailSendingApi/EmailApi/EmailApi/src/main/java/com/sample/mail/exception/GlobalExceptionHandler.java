package com.sample.mail.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

	SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(dateTimeFormatter.format(new Date()), ex.getMessage(),
				ex.getStatus(), request.getDescription(false).substring(4));
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	protected ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(dateTimeFormatter.format(new Date()), ex.getMessage(),
				HttpStatus.NOT_FOUND.value(), request.getDescription(false).substring(4));
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(DuplicateResourceException.class)
	protected ResponseEntity<Object> handleDuplicateResource(DuplicateResourceException ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(dateTimeFormatter.format(new Date()), ex.getMessage(),
				HttpStatus.CONFLICT.value(), request.getDescription(false).substring(4));
		return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(dateTimeFormatter.format(new Date()), ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getDescription(false).substring(4));
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Object> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException ex,
			WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(dateTimeFormatter.format(new Date()), "Method Not Allowed",
				HttpStatus.METHOD_NOT_ALLOWED.value(), request.getDescription(false).substring(4));
		return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(Unauthorized.class)
	public ResponseEntity<ErrorResponse> handleUnauthorizedException(Unauthorized ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(dateTimeFormatter.format(new Date()), "UNAUTHORIZED",
				HttpStatus.UNAUTHORIZED.value(), request.getDescription(false).substring(4));
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(dateTimeFormatter.format(new Date()), ex.getMessage(),
				ex.getStatusCode().value(), request.getDescription(false).substring(4));
		return new ResponseEntity<>(errorResponse, ex.getStatusCode());
	}

	class ErrorResponse {
		private String timestamp;
		private String message;
		private int status;
		private String path;

		public String getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public ErrorResponse(String timestamp, String message, int status, String path) {
			super();
			this.timestamp = timestamp;
			this.message = message;
			this.status = status;
			this.path = path;
		}

		public ErrorResponse() {
			super();
		}
	}
}

package br.com.usatec.api.exceptions;

import br.com.usatec.api.security.authorization.AuthorizationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(DomainException.class)
	public ResponseEntity<Map<String, Object>> handleDomain(DomainException ex, HttpServletRequest request) {
		return error(HttpStatus.BAD_REQUEST, ex.getMessageKey(), request);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex, HttpServletRequest request) {
		return error(HttpStatus.CONFLICT, ex.getErrorCode(), request);
	}

	@ExceptionHandler(AuthorizationException.class)
	public ResponseEntity<Map<String, Object>> handleAuthorization(AuthorizationException ex, HttpServletRequest request) {
		return error(HttpStatus.FORBIDDEN, ex.getMessageKey(), request);
	}

	@ExceptionHandler({BadCredentialsException.class, LockedException.class})
	public ResponseEntity<Map<String, Object>> handleAuthentication(Exception ex, HttpServletRequest request) {
		return error(HttpStatus.UNAUTHORIZED, "authentication.failed", request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
		return error(HttpStatus.BAD_REQUEST, "validation.failed", request);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidArgument(IllegalArgumentException ex, HttpServletRequest request) {
		return error(HttpStatus.BAD_REQUEST, "request.invalid", request);
	}

	private ResponseEntity<Map<String, Object>> error(
			HttpStatus status,
			String code,
			HttpServletRequest request
	) {
		return ResponseEntity.status(status).body(Map.of(
				"timestamp", Instant.now().toString(),
				"status", status.value(),
				"code", code,
				"path", request.getRequestURI()
		));
	}
}

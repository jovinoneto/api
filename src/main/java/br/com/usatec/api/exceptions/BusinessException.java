package br.com.usatec.api.exceptions;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
	private final String errorCode;
	private final String field;
	private final Object[] args;

	public BusinessException(String errorCode) {
		this.errorCode = errorCode;
		this.field = null;
		this.args = new Object[0];
	}

	public BusinessException(String errorCode, String field) {
		this.errorCode = errorCode;
		this.field = field;
		this.args = new Object[0];
	}

	public BusinessException(String errorCode, String field, Object[] args) {
		this.errorCode = errorCode;
		this.field = field;
		this.args = args;
	}
}

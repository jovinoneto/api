package br.com.usatec.api.exceptions;

public class DomainException extends RuntimeException {

	private final String messageKey;

	public DomainException(String messageKey) {
		this.messageKey = messageKey;
	}

	public String getMessageKey() {
		return messageKey;
	}
}
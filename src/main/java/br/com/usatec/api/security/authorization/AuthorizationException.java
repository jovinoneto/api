package br.com.usatec.api.security.authorization;

import lombok.Getter;

@Getter
public class AuthorizationException extends RuntimeException {

	private final String messageKey;

	public AuthorizationException(String messageKey) {
		super(messageKey);
		this.messageKey = messageKey;
	}
}

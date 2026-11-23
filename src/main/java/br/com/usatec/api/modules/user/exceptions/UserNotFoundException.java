package br.com.usatec.api.modules.user.exceptions;

import br.com.usatec.api.exceptions.BusinessException;

public class UserNotFoundException extends BusinessException {
	public UserNotFoundException(String email) {
		super("email.not.found", null, new Object[] {email});
	}
}

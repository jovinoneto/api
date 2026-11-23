package br.com.usatec.api.modules.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
		@NotBlank @Size(max = 200) String fullName,
		@NotBlank @Email @Size(max = 320) String email,
		@NotBlank @Size(min = 8, max = 255) String password,
		@Size(max = 30) String phone,
		@Size(max = 2048) String avatarUrl
) {
}

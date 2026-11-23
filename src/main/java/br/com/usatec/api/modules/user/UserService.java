package br.com.usatec.api.modules.user;

import br.com.usatec.api.modules.user.entity.User;
import br.com.usatec.api.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public User create(CreateUserRequest request) {
		String passwordHash = passwordEncoder.encode(
				request.password()
		);

		User user = new User(
				request.fullName(),
				request.email(),
				passwordHash,
				request.phone(),
				request.avatarUrl()
		);

		return userRepository.save(user);
	}
}

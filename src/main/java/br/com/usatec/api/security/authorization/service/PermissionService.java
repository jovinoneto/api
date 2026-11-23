package br.com.usatec.api.security.authorization.service;

import br.com.usatec.api.modules.user.repository.UserRepository;
import br.com.usatec.api.security.authorization.repository.RolePermissionRepository;
import br.com.usatec.api.security.authorization.repository.UserPermissionRepository;
import br.com.usatec.api.security.authorization.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionService {

	private final UserRepository userRepository;
	private final UserPermissionRepository userPermissionRepository;
	private final RolePermissionRepository rolePermissionRepository;
	private final UserRoleRepository userRoleRepository;

	public boolean hasPermission(UUID userId, String permission) {
		if (permission == null || permission.isBlank()) {
			return false;
		}

		boolean denied = userPermissionRepository
				.findAllActiveByUserId(userId, Instant.now())
				.stream()
				.anyMatch(p -> permission.equals(p.getPermissionCode())
						&& "DENY".equalsIgnoreCase(p.getEffect()));
		if (denied) {
			return false;
		}

		boolean explicitlyAllowed = userPermissionRepository
				.findAllActiveByUserId(userId, Instant.now())
				.stream()
				.anyMatch(p -> permission.equals(p.getPermissionCode())
						&& "ALLOW".equalsIgnoreCase(p.getEffect()));
		if (explicitlyAllowed) {
			return true;
		}

		return userRepository.findPermissionCodesByUserId(userId).contains(permission);
	}

	public boolean hasRole(UUID userId, String role) {

		return userRoleRepository.existsByUserIdAndRoleCode(
				userId,
				role
		);
	}
}

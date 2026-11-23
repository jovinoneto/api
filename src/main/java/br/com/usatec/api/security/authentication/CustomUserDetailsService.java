package br.com.usatec.api.security.authentication;

import br.com.usatec.api.modules.user.entity.User;
import br.com.usatec.api.modules.user.repository.UserRepository;
import br.com.usatec.api.security.authorization.projection.UserPermissionProjection;
import br.com.usatec.api.security.authorization.repository.UserPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	private final UserPermissionRepository userPermissionRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email.toLowerCase().trim())
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

		Set<String> effectivePermissions = calculateEffectivePermissions(user);

		Set<GrantedAuthority> authorities = new HashSet<>();
		for (String perm : effectivePermissions) {
			authorities.add(new SimpleGrantedAuthority(perm));
		}

		return CustomUserDetails.builder()
				.id(user.getId())
				.email(user.getEmail())
				.passwordHash(user.getPasswordHash())
				.status(user.getStatus())
				.mustChangePassword(user.isMustChangePassword())
				.lockedUntil(user.getLockedUntil())
				.expiresAt(user.getExpiresAt())
				.authorities(authorities)
				.build();
	}

	private Set<String> calculateEffectivePermissions(User user) {
		Set<String> permissions = new HashSet<>();

		// 1. Permissões herdadas das Roles
		userRepository.findPermissionCodesByUserId(user.getId())
				.forEach(permissions::add);

		// 2. Aplicar User Permissions (ALLOW adiciona, DENY remove)
		List<UserPermissionProjection> directPermissions =
				userPermissionRepository.findAllActiveByUserId(user.getId(), Instant.now());

		for (UserPermissionProjection p : directPermissions) {
			if ("DENY".equalsIgnoreCase(p.getEffect())) {
				permissions.remove(p.getPermissionCode());
			} else if ("ALLOW".equalsIgnoreCase(p.getEffect())) {
				permissions.add(p.getPermissionCode());
			}
		}

		return permissions;
	}
}

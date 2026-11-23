package br.com.usatec.api.security.authentication;

import br.com.usatec.api.modules.user.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {

	private final UUID id;
	private final String email;
	private final String passwordHash;
	private final UserStatus status;
	private final boolean mustChangePassword;
	private final Instant lockedUntil;
	private final Instant expiresAt;
	private final Set<GrantedAuthority> authorities;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return passwordHash;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return expiresAt == null || expiresAt.isAfter(Instant.now());
	}

	@Override
	public boolean isAccountNonLocked() {
		if (status == UserStatus.BLOCKED) return false;
		return lockedUntil == null || lockedUntil.isBefore(Instant.now());
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return status == UserStatus.ACTIVE;
	}
}

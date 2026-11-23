package br.com.usatec.api.modules.user.entity;

import br.com.usatec.api.modules.user.enums.UserStatus;
import br.com.usatec.api.exceptions.DomainException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "full_name", nullable = false, length = 200)
	private String fullName;

	@Column(name = "email", nullable = false, length = 320)
	private  String email;

	@Column(name = "password_hash", nullable = false, length = 255)
	private  String passwordHash;

	@Column(length = 30)
	private String phone;

	@Column(name = "avatar_url", columnDefinition = "TEXT")
	private String avatarUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private UserStatus status = UserStatus.PENDING;

	@Column(name = "email_verified_at")
	private Instant emailVerifiedAt;

	@Column(name = "last_login_at")
	private Instant lastLoginAt;

	@Column(name = "password_changed_at")
	private Instant passwordChangedAt;

	@Column(name = "must_change_password", nullable = false)
	private boolean mustChangePassword = false;

	@Column(name = "failed_login_attempts", nullable = false)
	private int failedLoginAttempts = 0;

	@Column(name = "locked_until")
	private Instant lockedUntil;

	@Column(name = "expires_at")
	private Instant expiresAt;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Column(name = "deleted_at")
	private Instant deletedAt;

	//Constructor: cadastro de usuário
	public User(
			String fullName,
			String email,
			String passwordHash,
			String phone,
			String avatarUrl
	) {
		this.fullName = normalizeName(fullName);
		this.email = normalizeEmail(email);
		this.passwordHash = passwordHash;
		this.phone = phone;
		this.avatarUrl = avatarUrl;
		this.status = UserStatus.PENDING;
	}

	//atualização do próprio usuário
	public void updateMe(
			String fullName,
			String email,
			String phone,
			String avatarUrl
	) {
		if (fullName != null && !fullName.isBlank()) {
			this.fullName = normalizeName(fullName);
		}
		if (email != null && !email.isBlank()) {
			changeEmail(email);
		}
		if (phone != null) {
			this.phone = phone.isBlank() ? null : phone.trim();
		}
		if (avatarUrl != null) {
			this.avatarUrl = avatarUrl.isBlank() ? null : avatarUrl.trim();
		}
	}

	//Atualização administrativa
	public void update(
			String fullName,
			String email,
			String phone,
			String avatarUrl,
			UserStatus status
	) {
		updateMe(fullName, email, phone, avatarUrl);
		if (status != null) {
			this.status = status;
		}
	}


	//Alteração de email
	public void changeEmail(String email) {
		String normalizeEmail = normalizeEmail(email);

		if(!this.email.equals(normalizeEmail)) {
			this.email = normalizeEmail;
			this.emailVerifiedAt = null;
		}
	}

	//Atualização de senha
	public void updatePassword(String passwordHash) {
		this.passwordHash = passwordHash;
		this.passwordChangedAt = Instant.now();
		this.mustChangePassword = false;
	}

	//Registro de login
	public void registerLogin() {
		this.lastLoginAt = Instant.now();
		this.failedLoginAttempts = 0;
		this.lockedUntil = null;
	}

	//Registro de tentativa inválida
	public void registerFailedLogin() {
		this.failedLoginAttempts++;
	}

	//Bloqueio temporário do usuário
	public void lockUntil(Instant lockedUntil) {
		this.lockedUntil = lockedUntil;
	}

	//Desbloqueio do usuário
	public void unlock() {
		this.lockedUntil = null;
		this.failedLoginAttempts = 0;
	}

	//Verificação de email
	public void verifyEmail() {
		this.emailVerifiedAt = Instant.now();
	}

	//Alteração de status
	public void changeStatus(UserStatus status) {
		if(status != null) {
			this.status = status;
		}
	}

	//Troca de senha
	public void requirePasswordChange() {
		this.mustChangePassword = true;
	}

	//Expiração de usuário
	public void setExpiration(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	//Exclusão lógica
	public void softDelete() {
		this.deletedAt = Instant.now();
		this.status = UserStatus.INACTIVE;
	}

	//Normaliza nome
	private String normalizeName(String name) {
		if(name == null || name.isBlank()) {
			throw new DomainException("user.name.required");
		}
		return name.trim();
	}

	//Normaliza email
	private String normalizeEmail(String email) {
		if(email == null || email.isBlank()) {
			throw new DomainException("user.email.required");
		}
		return email.trim().toLowerCase(Locale.ROOT);
	}
}

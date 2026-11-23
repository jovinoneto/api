package br.com.usatec.api.security.authorization.entity;

import br.com.usatec.api.modules.user.entity.User;
import br.com.usatec.api.security.authorization.enums.PermissionEffect;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_permissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPermission {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "permission_id", nullable = false)
	private PermissionEntity permission;

	@Enumerated(EnumType.STRING)
	@Column(name = "effect", nullable = false, length = 10)
	private PermissionEffect effect;

	@Column(name = "granted_by")
	private UUID grantedBy;

	@Column(columnDefinition = "TEXT")
	private String reason;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "expires_at")
	private Instant expiresAt;

	@Column(name = "condition_json", columnDefinition = "jsonb")
	private String conditionJson;
}

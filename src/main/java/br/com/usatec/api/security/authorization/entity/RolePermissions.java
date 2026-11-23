package br.com.usatec.api.security.authorization.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "role_permissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RolePermissions {
	@EmbeddedId
	private RolePermissionId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("roleId")
	@JoinColumn(name = "role_id")
	private Role role;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("permissionId")
	@JoinColumn(name = "permission_id")
	private PermissionEntity permission;

	@CreationTimestamp
	@Column(name = "granted_at", nullable = false, updatable = false)
	private Instant grantedAt;

	@Column(name = "condition_json", columnDefinition = "jsonb")
	private String conditionJson;
}

package br.com.usatec.api.security.authorization.entity;

import br.com.usatec.api.modules.user.entity.User;
import br.com.usatec.api.modules.empresa.entity.Company;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_scopes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserScope {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "scope_type", nullable = false, length = 20)
	private String scopeType;

	@Column(name = "company_id")
	private UUID companyId;

	@Column(name = "branch_id")
	private UUID branchId;

	@Column(name = "department_id")
	private UUID departmentId;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "expires_at")
	private Instant expiresAt;
}

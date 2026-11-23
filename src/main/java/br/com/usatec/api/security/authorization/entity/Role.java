package br.com.usatec.api.security.authorization.entity;

import br.com.usatec.api.modules.empresa.entity.Company;
import br.com.usatec.api.security.authorization.enums.RoleStatus;
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
@Table(name = "roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, length = 80)
	private String code;

	@Column(nullable = false, length = 150)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "is_system", nullable = false)
	private boolean isSystem = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id")
	private Company company;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private RoleStatus status = RoleStatus.ACTIVE;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	// Cadastro de Role
	public Role(
			String code,
			String name,
			String description,
			Company company
	) {
		this.code = normalizeCode(code);
		this.name = normalizeRequired(name, "role.name.required");
		this.description = normalize(description);
		this.company = company;
	}

	// Atualização
	public void update(String name, String description) {

		if (name != null) {
			this.name = normalizeRequired(name, "role.name.required");
		}

		if (description != null) {
			this.description = normalize(description);
		}
	}

	// Alteração de status
	public void changeStatus(RoleStatus status) {
		if (status != null) {
			this.status = status;
		}
	}

	// Normalização
	private String normalizeRequired(String value, String errorCode) {
		if (value == null || value.isBlank()) {
			throw new DomainException(errorCode);
		}

		return value.trim();
	}

	private String normalize(String value) {
		return value == null ? null : value.trim();
	}

	private String normalizeCode(String code) {
		return normalizeRequired(code, "role.code.required")
				.toUpperCase(Locale.ROOT);
	}
}
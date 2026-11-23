package br.com.usatec.api.modules.empresa.entity;

import br.com.usatec.api.modules.empresa.enums.CompanyStatus;
import br.com.usatec.api.exceptions.DomainException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
		name = "companies",
		uniqueConstraints = {
				@UniqueConstraint(name = "uq_companies_code", columnNames = "code")
		}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, length = 50)
	private String code;

	@Column(name = "legal_name", nullable = false, length = 200)
	private String legalName;

	@Column(name = "trade_name", length = 200)
	private String tradeName;

	@Column(name = "tax_id", length = 30)
	private String taxId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private CompanyStatus status = CompanyStatus.ACTIVE;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Column(name = "deleted_at")
	private Instant deletedAt;

	public Company(
			String code,
			String legalName,
			String tradeName,
			String taxId
	) {
		this.code = normalizeRequired(code, "company.code.required").toUpperCase();
		this.legalName = normalizeRequired(legalName, "company.legal_name.required");
		this.tradeName = normalize(tradeName);
		this.taxId = normalizeTaxId(taxId);
	}

	public void update(
			String code,
			String legalName,
			String tradeName,
			String taxId
	) {
		if (code != null) {
			this.code = normalizeRequired(code, "company.code.required").toUpperCase();
		}
		if (legalName != null) {
			this.legalName = normalizeRequired(legalName, "company.legal_name.required");
		}
		if (tradeName != null) {
			this.tradeName = normalize(tradeName);
		}
		if (taxId != null) {
			this.taxId = normalizeTaxId(taxId);
		}
	}

	public void changeStatus(CompanyStatus status) {
		if (status != null) {
			this.status = status;
		}
	}

	public void softDelete() {
		if (this.deletedAt == null) {
			this.deletedAt = Instant.now();
			this.status = CompanyStatus.INACTIVE;
		}
	}

	private String normalizeRequired(String value, String errorCode) {
		if (value == null || value.isBlank()) {
			throw new DomainException(errorCode);
		}
		return value.trim();
	}

	private String normalize(String value) {
		return value == null ? null : value.trim();
	}

	private String normalizeTaxId(String taxId) {
		if (taxId == null || taxId.isBlank()) {
			return null;
		}
		String value = taxId.replaceAll("\\D", "");
		if (value.length() != 14) {
			throw new DomainException("company.cnpj.invalid");
		}
		return value;
	}
}
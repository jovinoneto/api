package br.com.usatec.api.security.authorization.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class RolePermissionId implements Serializable {
	private UUID roleId;
	private UUID permissionId;
}

package br.com.usatec.api.security.authorization;

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
public class UserRoleId implements Serializable {

	private UUID userId;

	private UUID roleId;
}

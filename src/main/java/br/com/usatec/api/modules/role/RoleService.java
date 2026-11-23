package br.com.usatec.api.modules.role;

import br.com.usatec.api.security.authorization.entity.Role;
import br.com.usatec.api.security.authorization.repository.RoleRepository;
import br.com.usatec.api.exceptions.DomainException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class RoleService {

	private final RoleRepository roleRepository;

	public void updateRole(UUID roleId, RoleRequestDTO request) {
		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new DomainException("role.not.found"));

		if (role.isSystem()) {
			throw new DomainException("role.system.cannot.update");
		}

		role.update(
				request.name(),
				request.description()
		);
	}
}

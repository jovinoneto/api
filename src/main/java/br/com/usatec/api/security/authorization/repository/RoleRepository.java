package br.com.usatec.api.security.authorization.repository;

import br.com.usatec.api.security.authorization.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
	boolean existsByCodeAndCompanyId(String code, UUID companyId);
}

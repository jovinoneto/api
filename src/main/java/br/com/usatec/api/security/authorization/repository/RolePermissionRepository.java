package br.com.usatec.api.security.authorization.repository;

import br.com.usatec.api.security.authorization.entity.RolePermissions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RolePermissionRepository extends JpaRepository<RolePermissions, UUID> {
}

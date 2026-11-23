package br.com.usatec.api.security.authorization.repository;

import br.com.usatec.api.security.authorization.projection.UserPermissionProjection;
import br.com.usatec.api.security.authorization.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserPermissionRepository extends JpaRepository<UserPermission, UUID> {

	@Query(value = """
        SELECT p.code AS permissionCode, up.effect AS effect
        FROM user_permissions up
        JOIN permissions p ON up.permission_id = p.id
        WHERE up.user_id = :userId
          AND (up.expires_at IS NULL OR up.expires_at > :now)
    """, nativeQuery = true)
	List<UserPermissionProjection> findAllActiveByUserId(UUID userId, Instant now);
}


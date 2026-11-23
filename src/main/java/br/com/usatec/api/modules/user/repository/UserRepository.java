package br.com.usatec.api.modules.user.repository;

import br.com.usatec.api.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findByEmail(String email);

	@Query(value = """
        SELECT DISTINCT p.code
        FROM user_roles ur
        JOIN role_permissions rp ON ur.role_id = rp.role_id
        JOIN permissions p ON rp.permission_id = p.id
        JOIN roles r ON r.id = ur.role_id
        WHERE ur.user_id = :userId AND r.status = 'ACTIVE'
    """, nativeQuery = true)
	Set<String> findPermissionCodesByUserId(@Param("userId") UUID userId);
}

package br.com.usatec.api.security.authorization.repository;

import br.com.usatec.api.security.authorization.UserRoleId;
import br.com.usatec.api.security.authorization.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

	@Query("""
		SELECT COUNT(ur) > 0 FROM UserRole ur
		WHERE ur.id.userId = :userId
		  AND ur.role.code = :roleCode
		  AND ur.role.status = br.com.usatec.api.security.authorization.enums.RoleStatus.ACTIVE
	""")
	boolean existsByUserIdAndRoleCode(UUID userId, String roleCode);

	List<UserRole> findAllByUserId(UUID userId);

	void deleteByUserIdAndRoleId(UUID userId, UUID roleId);
}

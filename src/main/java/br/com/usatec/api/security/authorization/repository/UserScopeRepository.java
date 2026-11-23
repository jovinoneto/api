package br.com.usatec.api.security.authorization.repository;

import br.com.usatec.api.security.authorization.entity.UserScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserScopeRepository extends JpaRepository<UserScope, UUID> {

	@Query("""
		SELECT s FROM UserScope s
		WHERE s.user.id = :userId
		  AND (s.expiresAt IS NULL OR s.expiresAt > :now)
	""")
	List<UserScope> findAllActiveByUserId(UUID userId, Instant now);
}

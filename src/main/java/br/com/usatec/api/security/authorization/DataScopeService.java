package br.com.usatec.api.security.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import br.com.usatec.api.security.authorization.entity.UserScope;
import br.com.usatec.api.security.authorization.repository.UserScopeRepository;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DataScopeService {

	private final UserScopeRepository userScopeRepository;

	public boolean hasAccess(
			UUID userId,
			UUID companyId,
			UUID branchId) {
		if (userId == null) {
			return false;
		}

		for (UserScope scope : userScopeRepository.findAllActiveByUserId(userId, Instant.now())) {
			switch (scope.getScopeType()) {
				case "GLOBAL" -> {
					return true;
				}
				case "COMPANY" -> {
					if (companyId != null && companyId.equals(scope.getCompanyId())) {
						return true;
					}
				}
				case "BRANCH" -> {
					if (companyId != null && branchId != null
							&& companyId.equals(scope.getCompanyId())
							&& branchId.equals(scope.getBranchId())) {
						return true;
					}
				}
				case "DEPARTMENT" -> {
					if (companyId != null && companyId.equals(scope.getCompanyId())) {
						return true;
					}
				}
				case "OWN" -> {
					// Ownership is resource-specific and must be enforced by repositories/services.
					return true;
				}
				default -> {
				}
			}
		}
		return false;
	}
}

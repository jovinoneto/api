package br.com.usatec.api.security.context;

import br.com.usatec.api.security.authentication.CustomUserDetails;
import br.com.usatec.api.security.authorization.DataScopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("securityService")
@RequiredArgsConstructor
public class SecurityService {

	private final DataScopeService dataScopeService;

	public boolean hasAccessToCompany(UUID companyId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return false;
		}
		if (authentication.getPrincipal() instanceof CustomUserDetails user) {
			return dataScopeService.hasAccess(user.getId(), companyId, null);
		}
		return false;
	}
}

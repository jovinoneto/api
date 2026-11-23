package br.com.usatec.api.security.authorization;

import br.com.usatec.api.security.authorization.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

	private final PermissionService permissionService;
	private final DataScopeService dataScopeService;

	/**
	 * Verifica se o usuário possui determinada permissão.
	 */
	public boolean hasPermission(UUID userId, String permission) {
		return permissionService.hasPermission(userId, permission);
	}

	/**
	 * Verifica permissão e acesso ao escopo solicitado.
	 */
	public boolean hasAccess(
			UUID userId,
			String permission,
			UUID companyId,
			UUID branchId) {

		if (!hasPermission(userId, permission)) {
			return false;
		}

		return dataScopeService.hasAccess(
				userId,
				companyId,
				branchId
		);
	}

	/**
	 * Exige determinada permissão.
	 */
	public void requirePermission(UUID userId, String permission) {

		if (!hasPermission(userId, permission)) {
			throw new AuthorizationException(
					"error.authorization.permission.denied"
			);
		}
	}

	/**
	 * Exige permissão e acesso ao escopo.
	 */
	public void requireAccess(
			UUID userId,
			String permission,
			UUID companyId,
			UUID branchId) {

		if (!hasAccess(userId, permission, companyId, branchId)) {
			throw new AuthorizationException(
					"error.authorization.access.denied"
			);
		}
	}

	/**
	 * Verifica se o usuário possui determinado perfil.
	 */
	public boolean hasRole(UUID userId, String role) {
		return permissionService.hasRole(userId, role);
	}
}

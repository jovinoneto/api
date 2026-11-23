package br.com.usatec.api.security.context;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserScopeContext {
	private final boolean isGlobal;
	private final Set<UUID> companyIds;
	private final Set<UUID> branchIds;
	private final Set<UUID> departmentIds;
	private final boolean ownOnly;
}

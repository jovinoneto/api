package br.com.usatec.api.modules.auth;

import br.com.usatec.api.security.authentication.CustomUserDetails;
import br.com.usatec.api.security.authentication.CustomUserDetailsService;
import br.com.usatec.api.security.authentication.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final CustomUserDetailsService userDetailsService;

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		var authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.email(), request.password())
		);
		CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
		return new AuthResponse(
				jwtService.generateAccessToken(user),
				jwtService.generateRefreshToken(user)
		);
	}

	@PostMapping("/refresh")
	public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
		if (!jwtService.isValid(request.refreshToken()) || !jwtService.isRefreshToken(request.refreshToken())) {
			throw new IllegalArgumentException("Invalid refresh token");
		}
		CustomUserDetails user = (CustomUserDetails) userDetailsService
				.loadUserByUsername(jwtService.getSubject(request.refreshToken()));
		return new AuthResponse(
				jwtService.generateAccessToken(user),
				jwtService.generateRefreshToken(user)
		);
	}

	public record LoginRequest(
			@NotBlank @Email String email,
			@NotBlank String password
	) {}

	public record RefreshRequest(@NotBlank String refreshToken) {}
	public record AuthResponse(String accessToken, String refreshToken) {}
}

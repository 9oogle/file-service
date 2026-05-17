package com.goggles.file_service.infrastructure.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(exclude = {"password", "nickname"})
public class UserDetailsImpl implements UserDetails {

	private final UUID uuid;
	private final String username;
	private final String password;
	private final String userRole;
	private final String name;
	private final String nickname;
	private final boolean enabled;

	@Builder
	public UserDetailsImpl(UUID uuid, String username, String password, String userRole,
		String name, String nickname, boolean enabled) {
		this.uuid = uuid;
		this.username = username;
		this.password = password;
		this.userRole = userRole;
		this.name = name;
		this.nickname = nickname;
		this.enabled = enabled;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (!StringUtils.hasText(userRole)) {
			return List.of(new SimpleGrantedAuthority("ROLE_USER"));
		}
		return Arrays.stream(userRole.split(","))
			.map(String::trim)
			.filter(StringUtils::hasText)
			.map(SimpleGrantedAuthority::new)
			.toList();
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
}
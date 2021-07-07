package com.example.application.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import com.example.application.data.AbstractEntity;

@Entity
public class Usuario extends AbstractEntity {

	@NotNull
	@NotEmpty
	private String username;
	private String passwordSalt;
	private String passwordHash;
	private Role role;
	private String activationCode;
	private boolean active;
	
	
	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Usuario() {
		super();
	}
	
	public Usuario(String username, String password, Role role) {
		super();
		this.username = username;
		this.role = role;
		this.passwordSalt = RandomStringUtils.random(32);
		this.passwordHash = DigestUtils.sha1Hex(password+ passwordSalt);
		this.activationCode = RandomStringUtils.randomAlphabetic(32);
	}
	
	public boolean checkPassword(String password) {
		
		return DigestUtils.sha1Hex(password+ passwordSalt).equals(passwordHash);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordSalt() {
		return passwordSalt;
	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	public String getPassword() {
		return "";
	}
	
	public void setPassword(String password) {
		this.passwordSalt = RandomStringUtils.random(32);
		this.passwordHash = DigestUtils.sha1Hex(password+ passwordSalt);		
	}
	
}

/**
 * 
 */
package org.scigap.iucig.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Viknes
 *
 */
public class User extends org.springframework.security.core.userdetails.User{

	
	private static final long serialVersionUID = 1L;
	
	private String certificate;
	
	public User(String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

	public String getCertificate() {
		return certificate;
	}
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}
	
}

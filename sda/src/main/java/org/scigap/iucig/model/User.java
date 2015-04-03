/**
 * 
 */
package org.scigap.iucig.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Viknes
 *
 */
public class User extends org.springframework.security.core.userdetails.User{

	
	private static final long serialVersionUID = 1L;
	
	public User(String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

}

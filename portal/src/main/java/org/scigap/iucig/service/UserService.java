/**
 * 
 */
package org.scigap.iucig.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.scigap.iucig.model.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Viknes
 *
 */
@Service(value="userService")
public class UserService implements UserDetailsService{

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		// Not needed as we dont authenticate based on DB or website login form.
		// User is authenticated by CILogon and set in SecurityContext
		// This method might be later on used when we add a DB.
		return null;
	}

    
    public Authentication setAuthenticatedUser(String username, String certificate) {
		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
		List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>();
		authorityList.add(authority);
		User user = new User(username,"password",authorityList);
		if(certificate !=null && !certificate.isEmpty()) {
			user.setCertificate(certificate);
		}

        Authentication authentication = new CustomToken(user.getAuthorities(), user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }
    
    
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        } else {
            throw new SecurityException("Could not get the authenticated user!");
        }
    }
    
    public boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return true;
        } else {
            return false;
        }
    }

    public void clearAuthenticatedUser() {
        SecurityContextHolder.clearContext();
    }
	
    private SecurityContext createContext(final User user) {
        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new CustomToken(user.getAuthorities(), user));
        return securityContext;
    }

    class CustomToken extends AbstractAuthenticationToken {
        private static final long serialVersionUID = 1L;

        private User user;

        public CustomToken(Collection<? extends GrantedAuthority> authorities, User user) {
            super(authorities);
            this.user = user;
        }

        public Object getCredentials() {
            return "N/A";
        }

        public Object getPrincipal() {
            return user;
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }
    }
}

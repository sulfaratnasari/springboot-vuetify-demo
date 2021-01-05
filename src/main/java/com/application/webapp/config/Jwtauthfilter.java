package com.application.webapp.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

public class Jwtauthfilter extends OncePerRequestFilter {
	
	public static final long JWT_TOKEN_VALIDITY = 1*60*60 ;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Fetching the authorization header from the request.
		String authenticationHeader = request.getHeader(Iconstants.HEADER);
		Boolean expire = false;
		try {
			SecurityContext context = SecurityContextHolder.getContext();

			if (authenticationHeader != null && authenticationHeader.startsWith("Bearer")) {

				final String bearerTkn = authenticationHeader.replaceAll(Iconstants.BEARER_TOKEN, "");

				try {
					// Parsing the jwt token.
					Jws<Claims> claims = Jwts.parser().requireIssuer(Iconstants.ISSUER)
							.setSigningKey(Iconstants.SECRET_KEY)
							.parseClaimsJws(bearerTkn);
					if (claims.getBody().getExpiration()
							.before(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))) {
						expire = true;
					} else {
						expire = false;
					}

					if (expire) {
						String user = (String) claims.getBody().get("usr");
						String roles = (String) claims.getBody().get("role");

						List<GrantedAuthority> authority = new ArrayList<GrantedAuthority>();
						for (String role : roles.split(","))
							authority.add(new SimpleGrantedAuthority(role));

						Myauthtoken authenticationTkn = new Myauthtoken(user, null, authority);
						context.setAuthentication(authenticationTkn);

					} else {
						throw new JwtException("Expired Token");
					}
				} catch (SignatureException e) {
					throw new ServletException("Invalid token.");
				} catch (JwtException e) {
					e.printStackTrace();
				}
			}
			filterChain.doFilter(request, response);
			context.setAuthentication(null);
		} catch (AuthenticationException ex) {
			throw new ServletException("Authentication exception.");
		}
	}
	
	
}
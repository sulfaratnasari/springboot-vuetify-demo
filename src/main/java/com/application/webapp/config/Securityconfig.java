package com.application.webapp.config;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.application.webapp.dbo.UserDao;
import com.application.webapp.dbo.UserList;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true ,securedEnabled = true, jsr250Enabled = true)
public class Securityconfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDao userDao;
	
	@Bean
	public Jwtauthfilter jwtAuthenticationFilter() {
		return new Jwtauthfilter();
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		List<UserList> users = userDao.getUserList();
		try {
			
			final InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> inMemoryAuthentication = auth
					.inMemoryAuthentication();
			for (UserList x : users) {
				String username = x.getUsername();
				String password = x.getPassword();
				String role = x.getRole();
				inMemoryAuthentication.withUser(username).password(password).roles(role);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	   
	  }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.authorizeRequests()
		.antMatchers("/public/*")
		.permitAll()
		.anyRequest()
		.authenticated()
		.and()
		.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
		.and()
		.csrf().disable()
        .addFilterBefore(jwtAuthenticationFilter(), 
				UsernamePasswordAuthenticationFilter.class)
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
	  @Bean
	    public AuthenticationEntryPoint authenticationEntryPoint(){
	        return new CustomAuthenticationEntryPoint();
	    }
	  
	  @Bean
	  public PasswordEncoder passwordEncoder() {
	      return new BCryptPasswordEncoder();
	  }
	 
}
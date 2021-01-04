package com.application.webapp.controller;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.application.webapp.config.Iconstants;
import com.application.webapp.dbo.UserDao;
import com.application.webapp.dbo.UserList;
import com.application.webapp.model.JwtResponse;
import com.google.gson.Gson;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@CrossOrigin(origins = { "http://localhost:8080"})
@RequestMapping("")
public class Tokencontroller {

	public static final long JWT_TOKEN_VALIDITY = 1 * 60 * 60;
	private static Gson gson = new Gson();
	protected HttpServletRequest request;

	protected HttpServletResponse response;

	@Autowired
	private UserDao userDao;

	@RequestMapping(value = "/public/token", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<JwtResponse> getToken(@RequestBody UserList login) throws ServletException {
		String jwttoken = "";
		UserList user = new UserList();
		JwtResponse resp = new JwtResponse();
		try {

			String name = login.getUsername(), password = login.getPassword();

			if (name.isEmpty() || password.isEmpty()) {
				resp.setResponseMessage("Username or password cannot be empty.");
				return new ResponseEntity<JwtResponse>(resp, HttpStatus.BAD_REQUEST);
			}

			user = userDao.getUserByUsername(name);
			if (user == null) {
				resp.setResponseCode("01");
				resp.setResponseMessage("Invalid Credentials or Deactivate Account");
				return new ResponseEntity<JwtResponse>(resp, HttpStatus.UNAUTHORIZED);
			} else {
				String passwordInput = password;
				String passwordDb = user.getPassword();
				if (checkPassword(passwordInput, passwordDb)) {
					Map<String, Object> claims = new HashMap<String, Object>();
					claims.put("usr", login.getUsername());
					claims.put("sub", "Authentication token");
					claims.put("iss", Iconstants.ISSUER);
					claims.put("role", user.getRole());
					claims.put("iat", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

					jwttoken = Jwts.builder().setClaims(claims).setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000)).signWith(SignatureAlgorithm.HS512, Iconstants.SECRET_KEY).compact();
					System.out.println("Returning the following token to the user= " + jwttoken);
					resp.setToken(jwttoken);
					resp.setUserId(user.getId());
					resp.setUsername(name);
					resp.setResponseMessage("success");
					resp.setResponseCode("00");
				} else {
					resp.setResponseCode("01");
					resp.setResponseMessage("Invalid Credentials");
					return new ResponseEntity<JwtResponse>(resp, HttpStatus.UNAUTHORIZED);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<JwtResponse>(resp, HttpStatus.OK);
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	public boolean checkPassword(String password, String hashPassword) {
		return passwordEncoder.matches(password, hashPassword);
	}
}
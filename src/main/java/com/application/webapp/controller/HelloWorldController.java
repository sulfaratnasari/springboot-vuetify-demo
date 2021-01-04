package com.application.webapp.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.application.webapp.dbo.UserDao;
import com.application.webapp.dbo.UserList;
import com.google.gson.Gson;

@RestController
@CrossOrigin(origins = { "http://localhost:8080"})
@RequestMapping("")
@Transactional
public class HelloWorldController {

	private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private static Gson gson = new Gson();

	public static boolean checkPassword(String password, String hashPassword) {
		return passwordEncoder.matches(password, hashPassword);
	}
	
	@Autowired
	private UserDao userDao;
	
	@RequestMapping(value ="/add/user", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String firstPage(@RequestBody UserList user, HttpServletRequest servletReq) {
		System.out.println("user : " + user);
		String password = passwordEncoder.encode(user.getPassword());
		user.setStatus("active");
		user.setId(null);
		user.setPassword(password);
		System.out.println(user.getEmail());
		System.out.println(user.getPassword());
		System.out.println(user.getUsername());
		System.out.println(user.getStatus());
		Long id = userDao.addNewUser(user);
		String response = "success" + id;
		return response;
	}
	
	@RequestMapping(value ="/user/list", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String getUserList() {
		System.out.println("test");
		List<UserList> userlist = userDao.getUserList();
		return gson.toJson(userlist) ;
	}
	
}

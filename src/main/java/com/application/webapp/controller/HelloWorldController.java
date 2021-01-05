package com.application.webapp.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
	
	@PreAuthorize("hasAnyAuthority('Admin')")
	@RequestMapping(value ="/add/user", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String addUser(@RequestBody UserList user, HttpServletRequest servletReq) {
		String password = passwordEncoder.encode(user.getPassword());
		user.setStatus("active");
		user.setId(null);
		user.setPassword(password);
		Long id = userDao.addNewUser(user);
		return "Success";
	}
	
	@PreAuthorize("hasAnyAuthority('Admin')")
	@RequestMapping(value ="/delete/user", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String deleteUser(@RequestBody UserList user, HttpServletRequest servletReq) {
		userDao.deleteUser(user.getId());		
		return "success";
	}
	
	@RequestMapping(value ="/user/list", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String getUserList() {
		List<UserList> userlist = userDao.getUserList();
		return gson.toJson(userlist) ;
	}
	
}

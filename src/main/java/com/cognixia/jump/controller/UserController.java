package com.cognixia.jump.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognixia.jump.exception.ResourceNotFoundException;
import com.cognixia.jump.model.AuthenticationRequest;
import com.cognixia.jump.model.AuthenticationResponse;
import com.cognixia.jump.model.User;
import com.cognixia.jump.repository.UserRepository;
import com.cognixia.jump.service.MyUserDetailsService;
import com.cognixia.jump.service.UserService;
import com.cognixia.jump.util.JwtUtil;

import io.swagger.annotations.ApiOperation;

@ApiOperation(value = "Controller for accessing User data.")
@RequestMapping("/api")
@RestController
public class UserController {
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	UserService userService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private MyUserDetailsService myUserDetailsService;
	
	@Autowired
	private JwtUtil jwtTokenUtil;
	
	@ApiOperation(value = "Get a list of all Users.")
	@GetMapping("/user")
	public List<User> getAllUsers() {
		return userRepo.findAll();
	}
	
	@ApiOperation(value = "Create a new User.")
	@PostMapping("/add/user")
	public ResponseEntity<?> addUser(@RequestBody AuthenticationRequest registeringUser) throws Exception {
		
		userService.createNewUser(registeringUser);
		
		return ResponseEntity.status(201).body(registeringUser.getUsername() + " has been created.");
		
	}
	
	@ApiOperation(value = "Authenticate a User.")
	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
		
		try {
			
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							authenticationRequest.getUsername(), 
							authenticationRequest.getPassword()));
			
		} catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password.", e);
		} catch (Exception e) {
			throw new Exception(e);
		}
		
		final UserDetails USER_DETAILS = myUserDetailsService
											.loadUserByUsername(authenticationRequest.getUsername());
		
		final String JWT = jwtTokenUtil.generateTokens(USER_DETAILS);
		
		return ResponseEntity.ok(new AuthenticationResponse(JWT));
		
	}
	
	@ApiOperation(value = "Promote a user to admin role.")
	@PatchMapping("/promote/user")
	public ResponseEntity<?> promoteUser(@RequestBody AuthenticationRequest userToPromote) throws Exception {
		
		userService.promoteUserAuthorization(userToPromote);
		
		return ResponseEntity.status(201).body(userToPromote.getUsername() + " has been promoted to admin.");
		
	}
	
	@ApiOperation(value = "Demote a User from admin to user role.")
	@PatchMapping("/demote/user")
	public ResponseEntity<?> demoteUser(@RequestBody AuthenticationRequest userToPromote) throws Exception {
		
		userService.demoteUserAuthorization(userToPromote);
		
		return ResponseEntity.status(201).body(userToPromote.getUsername() + " has been demoted to user.");
		
	}
	
	@ApiOperation(value = "Change a User's username.")
	@PatchMapping("/update/user/username/{newName}")
	public ResponseEntity<?> changeUsername(@RequestBody AuthenticationRequest userToUpdate, @PathVariable String newName) throws ResourceNotFoundException {
		String oldName = userToUpdate.getUsername();
		userService.updateUsername(userToUpdate, newName);
		return ResponseEntity.status(201).body("Username successfully changed from '"
										+ oldName + "' to '" + newName + "'.");
	}
	
	@ApiOperation(value = "Change a User's password.")
	@PatchMapping("/update/user/password")
	public ResponseEntity<?> changePassword(HttpServletRequest req, @RequestBody String newPassword) throws ResourceNotFoundException {
		userService.updatePassword(req, newPassword);
		return ResponseEntity.status(201).body("Password successfully updated.");
	}

}
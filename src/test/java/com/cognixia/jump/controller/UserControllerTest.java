package com.cognixia.jump.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cognixia.jump.exception.ResourceNotFoundException;
import com.cognixia.jump.model.AuthenticationRequest;
import com.cognixia.jump.model.Todo;
import com.cognixia.jump.model.User;
import com.cognixia.jump.model.User.Role;
import com.cognixia.jump.repository.UserRepository;
import com.cognixia.jump.service.MyUserDetailsService;
import com.cognixia.jump.service.UserService;
import com.cognixia.jump.util.JwtUtil;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
	
private final String STARTING_URI = "http://localhost:8080/api/";
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private UserRepository repo;
	
	@MockBean
	private UserService service;
	
	@MockBean
	private MyUserDetailsService myUserDetailsService;
	
	@MockBean
	private JwtUtil jwtUtil;
	
	@InjectMocks
	private UserController controller;
	
	@Test
	@WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
	public void testGetUsers() throws Exception {
		String uri = STARTING_URI + "user";
		List<User> allUsers = Arrays.asList(
				new User((long) 1, "test", "test", true, Role.ROLE_ADMIN, new ArrayList<Todo>()),
				new User((long) 2, "test1", "test2", true, Role.ROLE_ADMIN, new ArrayList<Todo>())
				);
		
		when(repo.findAll()).thenReturn(allUsers);
		
		mockMvc.perform(get(uri))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(jsonPath("$.length()").value(allUsers.size()))
			.andExpect(jsonPath("$[0].id").value(allUsers.get(0).getId()))
			.andExpect(jsonPath("$[0].username").value(allUsers.get(0).getUsername()))
			.andExpect(jsonPath("$[0].password").value(allUsers.get(0).getPassword()))
			.andExpect(jsonPath("$[0].enabled").value(allUsers.get(0).isEnabled()))
			.andExpect(jsonPath("$[0].role").value(allUsers.get(0).getRole().toString()))
			.andExpect(jsonPath("$[1].id").value(allUsers.get(1).getId()))
			.andExpect(jsonPath("$[1].username").value(allUsers.get(1).getUsername()))
			.andExpect(jsonPath("$[1].password").value(allUsers.get(1).getPassword()))
			.andExpect(jsonPath("$[1].enabled").value(allUsers.get(1).isEnabled()))
			.andExpect(jsonPath("$[1].role").value(allUsers.get(1).getRole().toString()));
		
		verify(repo, times(1)).findAll();
		verifyNoMoreInteractions(repo);
				
	}
	
	@Test
	@WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
	public void testAddUser() throws Exception {
		String uri = STARTING_URI + "add/user";
		User user = new User((long) 1, "test", "test", true, Role.ROLE_ADMIN, new ArrayList<Todo>());
		
		String userJson = user.toJson();
		
		when(service.createNewUser(Mockito.any(AuthenticationRequest.class))).thenReturn(true);
		
		mockMvc.perform(post(uri).content(userJson)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated());
	}
	
	@Test
	@WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
	public void testResourceNotFoundException() throws Exception {
		AuthenticationRequest user = new AuthenticationRequest("test", "test");
		String userJson = user.toJson();
		String uri = STARTING_URI + "promote/user";
		Exception exception = new ResourceNotFoundException(user.getUsername() + " could not be found");
		
		when(service.promoteUserAuthorization(Mockito.any(AuthenticationRequest.class))).thenThrow(new ResourceNotFoundException(user.getUsername() + " could not be found"));
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.patch(uri)
	            .contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON)
	            .content(userJson);
		
		mockMvc.perform(mockRequest)
		.andDo(print())
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.message").value(exception.getMessage()));

	}
	
	@Test
	@WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
	public void testPromoteUser() throws Exception {
		AuthenticationRequest user = new AuthenticationRequest("test", "test");
		String userJson = user.toJson();
		String uri = STARTING_URI + "promote/user";
		
		when(service.promoteUserAuthorization(Mockito.any(AuthenticationRequest.class))).thenReturn(true);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.patch(uri)
	            .contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON)
	            .content(userJson);
		
		mockMvc.perform(mockRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", notNullValue()));
	}
	
	
	
	

}

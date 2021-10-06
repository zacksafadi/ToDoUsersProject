package com.cognixia.jump.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.cognixia.jump.model.Todo;
import com.cognixia.jump.model.User;
import com.cognixia.jump.model.User.Role;
import com.cognixia.jump.repository.TodoRepository;
import com.cognixia.jump.service.MyUserDetailsService;
import com.cognixia.jump.service.TodoService;
import com.cognixia.jump.util.JwtUtil;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
public class TodoControllerTest {
	
	private final String STARTING_URI = "http://localhost:8080/api/";
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private TodoRepository repo;
	
	@MockBean
	private TodoService service;
	
	@MockBean
	private MyUserDetailsService myUserDetailsService;
	
	@MockBean
	private JwtUtil jwtUtil;
	
	@InjectMocks
	private TodoController controller;
	
	@Test
	@WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
	public void testGetTodos() throws Exception {
		String uri = STARTING_URI + "todo";
		List<Todo> allTodos = Arrays.asList(
				new Todo(1L, "test1", false, new Date()),
				new Todo(2L, "test2", false, new Date())
				);
		
		when(repo.findAll()).thenReturn(allTodos);
		
		mockMvc.perform(get(uri))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(jsonPath("$.length()").value(allTodos.size()))
		.andExpect(jsonPath("$[0].id").value(allTodos.get(0).getId()))
		.andExpect(jsonPath("$[0].description").value(allTodos.get(0).getDescription()))
		.andExpect(jsonPath("$[0].completed").value(allTodos.get(0).getCompleted()));
		
		verify(repo, times(1)).findAll();
		verifyNoMoreInteractions(repo);
	}
	
	
	// issue deserializing 'java.util.Date'
//	@Test
//	@WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
//	public void testAddTodo() throws Exception {
//		String uri = STARTING_URI + "add/todo";
//		Todo todo = new Todo(1L, "test1", false, new Date());
//		//User user = new User(1L, "test", "test", true, Role.ROLE_ADMIN, new ArrayList<Todo>());
//		HttpServletRequest request = mock(HttpServletRequest.class);
//		
//		String todoJson = todo.toJson();
//		
//		when(service.createNewTodo(todo, request)).thenReturn(todo);
//		
//		mockMvc.perform(post(uri).content(todoJson)
//				.contentType(MediaType.APPLICATION_JSON))
//		.andExpect(status().isCreated());
//	}
	
	

}

package com.cognixia.jump.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognixia.jump.exception.ResourceNotFoundException;
import com.cognixia.jump.exception.UserTodoMismatchException;
import com.cognixia.jump.model.Todo;
import com.cognixia.jump.model.User;
import com.cognixia.jump.repository.TodoRepository;
import com.cognixia.jump.repository.UserRepository;
import com.cognixia.jump.util.JwtUtil;

@Service
public class TodoService {
	
	@Autowired
	TodoRepository todoRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired 
	JwtUtil jwtUtil;
	
	public Todo createNewTodo(Todo todo, HttpServletRequest req) {
		String jwt = req.getHeader("Authorization").substring(7);
		String username = jwtUtil.extractUsername(jwt);
		
		User user = userRepo.findByUsername(username).get();
		todo.setId(-1L);
		todo.setUser(user);
		
		Todo saved = todoRepo.save(todo);
		
		return saved;
	}
	
	public List<Todo> findByUserId(HttpServletRequest req) {
		String jwt = req.getHeader("Authorization").substring(7);
		String username = jwtUtil.extractUsername(jwt);
		
		User user = userRepo.findByUsername(username).get();
		
		return user.getTodos();
	}
	
	public Todo deleteTodo(long id, HttpServletRequest req) throws ResourceNotFoundException, UserTodoMismatchException {
		Optional<Todo> toDelete = todoRepo.findById(id);
		if (toDelete.isEmpty()) {
			throw new ResourceNotFoundException("Todo item with id: " + id
					+ " does not exist, cannot delete.");
		}
		
		String jwt = req.getHeader("Authorization").substring(7);
		String username = jwtUtil.extractUsername(jwt);
		
		User user = userRepo.findByUsername(username).get();
		
		if (user.getTodos().stream()
				.filter(t -> t.getId() == id)
				.findFirst().isEmpty()) {
			throw new UserTodoMismatchException(user.getId(), id, "delete");
		}
		
		todoRepo.deleteTodo(id);
		return toDelete.get();
	}
	
	public List<Todo> deleteAllTodosOfUser(HttpServletRequest req) {
		String jwt = req.getHeader("Authorization").substring(7);
		String username = jwtUtil.extractUsername(jwt);
		
		User user = userRepo.findByUsername(username).get();
		List<Todo> todosToDelete = user.getTodos();
		for (Todo t : todosToDelete) {
			todoRepo.deleteTodo(t.getId());
		}
		return todosToDelete;
	}
	
	public Todo completeTask(long id, HttpServletRequest req) throws ResourceNotFoundException, UserTodoMismatchException {
		Optional<Todo> toUpdate = todoRepo.findById(id);
		if (toUpdate.isEmpty()) {
			throw new ResourceNotFoundException("Todo item with id: " + id
					+ " does not exist, cannot update.");
		}
		
		String jwt = req.getHeader("Authorization").substring(7);
		String username = jwtUtil.extractUsername(jwt);
		
		User user = userRepo.findByUsername(username).get();
		
		if (user.getTodos().stream()
				.filter(t -> t.getId() == id)
				.findFirst().isEmpty()) {
			throw new UserTodoMismatchException(user.getId(), id, "update");
		}
		todoRepo.completeTodo(id);
		toUpdate.get().setCompleted(true);
		todoRepo.save(toUpdate.get());
		return toUpdate.get();
	}
	
	public List<Todo> completeAllTodosOfUser(HttpServletRequest req) {
		String jwt = req.getHeader("Authorization").substring(7);
		String username = jwtUtil.extractUsername(jwt);
		
		User user = userRepo.findByUsername(username).get();
		List<Todo> todosToComplete = user.getTodos().stream()
										.filter(t -> t.getCompleted() == false)
										.toList();
		for (Todo t : todosToComplete) {
			
			todoRepo.completeTodo(t.getId());
			t.setCompleted(true);
			todoRepo.save(t);
			
		}
		return user.getTodos();
	}
	
	public Todo updateDueDate(long id, Date date, HttpServletRequest req) throws ResourceNotFoundException, UserTodoMismatchException, ParseException {
		Optional<Todo> toUpdate = todoRepo.findById(id);
		if (toUpdate.isEmpty()) {
			throw new ResourceNotFoundException("Todo item with id: " + id
					+ " does not exist, cannot update.");
		}
		
		String jwt = req.getHeader("Authorization").substring(7);
		String username = jwtUtil.extractUsername(jwt);
		
		User user = userRepo.findByUsername(username).get();
		
		if (user.getTodos().stream()
				.filter(t -> t.getId() == id)
				.findFirst().isEmpty()) {
			throw new UserTodoMismatchException(user.getId(), id, "update");
		}
//		todoRepo.updateDueDate(date, id);
//		DateFormat formatter = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss.SSSZ");
//		Date dateObject = formatter.parse(date);
		toUpdate.get().setDueDate(date);
		todoRepo.save(toUpdate.get());
		return toUpdate.get();
	
	}
	
}

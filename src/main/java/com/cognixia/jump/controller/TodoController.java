package com.cognixia.jump.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognixia.jump.exception.ResourceNotFoundException;
import com.cognixia.jump.exception.UserTodoMismatchException;
import com.cognixia.jump.model.Todo;
import com.cognixia.jump.repository.TodoRepository;
import com.cognixia.jump.service.TodoService;
import com.cognixia.jump.util.JwtUtil;

import io.swagger.annotations.ApiOperation;

@ApiOperation(value = "Controller for accessing Todo data.")
@RequestMapping("/api")
@RestController
public class TodoController {
	
	@Autowired
	TodoRepository todoRepo;
	
	@Autowired
	TodoService todoService;
	
	@Autowired
	JwtUtil jwtUtil;
	
	@ApiOperation(value = "Get a list of all Todos.")
	@GetMapping("/todo")
	public List<Todo> getAllTodos() {
		return todoRepo.findAll();
	}
	
	@ApiOperation(value = "Get a list of all Todos for a User.")
	@GetMapping("/todo/user")
	public ResponseEntity<?> getTodosByUser(HttpServletRequest req) {
		List<Todo> todos = todoService.findByUserId(req);
		return ResponseEntity.ok(todos);
	}
	
	@ApiOperation(value = "Add a Todo to a User.")
	@PostMapping("/add/todo")
	public ResponseEntity<?> addTodo(@RequestBody Todo todo, HttpServletRequest req) {
		Todo added = todoService.createNewTodo(todo, req);
		return ResponseEntity.status(201).body(added);
	}
	
	@ApiOperation(value = "Delete a Todo from a User.")
	@DeleteMapping("/delete/todo/{id}")
	public ResponseEntity<?> removeTodo(@PathVariable long id, HttpServletRequest req) throws ResourceNotFoundException, UserTodoMismatchException, Exception {
		Todo deleted = todoService.deleteTodo(id, req);
		return new ResponseEntity<>(deleted, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Delete all Todos of a User.")
	@DeleteMapping("/delete/todo/all")
	public ResponseEntity<?> removeTodosOfUser(HttpServletRequest req) {
		List<Todo> deleted = todoService.deleteAllTodosOfUser(req);
		return new ResponseEntity<>(deleted, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Complete a User's Todo.")
	@PatchMapping("/todo/complete/{id}")
	public ResponseEntity<?> completeTodo(@PathVariable long id, HttpServletRequest req) throws ResourceNotFoundException, UserTodoMismatchException, Exception{
		Todo completed = todoService.completeTask(id, req);
		return new ResponseEntity<>(completed, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Complete all Todo's of a User")
	@PatchMapping("/complete/todo/all")
	public ResponseEntity<?> completeTodosOfUser(HttpServletRequest req) {
		List<Todo> completed = todoService.completeAllTodosOfUser(req);
		return new ResponseEntity<>(completed, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Update the due date of a User's Todo.")
	@PatchMapping("/update/todo/dueDate/{id}")
	public ResponseEntity<?> updateDueDate(HttpServletRequest req, @PathVariable long id, @RequestBody Map<String, String> updateInfo) throws ResourceNotFoundException, UserTodoMismatchException, ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date date = formatter.parse(updateInfo.get("dueDate"));
		Todo updated = todoService.updateDueDate(id, date, req);
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}

}

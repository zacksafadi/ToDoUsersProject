package com.cognixia.jump.exception;

public class UserTodoMismatchException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public UserTodoMismatchException(long userId, long todoId, String action) {
		super("User can only delete their own todo items. "
				+ "User: " + userId + " cannot " + action + " Todo item: " + 
				todoId);
	}

}

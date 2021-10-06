package com.cognixia.jump.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public enum Role {
		ROLE_USER, ROLE_ADMIN
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(unique = true, nullable = false)
	@NotBlank
	private String username;
	
	@NotBlank
	@Column(nullable = false)
	private String password;
	
	@Column(columnDefinition = "boolean default true")
	private boolean enabled;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Todo> todos;
	
	public User() {
		this(-1L, "N/A", "N/A", false, Role.ROLE_USER, new ArrayList<>());
	}

	public User(Long id, @NotBlank String username, @NotBlank String password, boolean enabled, Role role, List<Todo> todos) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.role = role;
		this.todos = todos;
	}
	
	public void resetTodoIds() {
		for (Todo t : todos) {
			t.setId(-1L);
			t.setUser(this);
		}
	}
	
	public void attachTodos() {
		for (Todo t : todos) {
			t.setUser(this);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public List<Todo> getTodos() {
		return todos;
	}

	public void setTodos(List<Todo> todos) {
		this.todos = todos;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", enabled=" + enabled
				+ ", role=" + role + ", todos=" + todos + "]";
	}
	
	public String todosToJson() {
		String result = "";
		for (Todo t : todos) {
			result += "{\"id\" : " + t.getId()
						+ ", \"description\" : \"" + t.getDescription() + "\""
						+ ", \"completed\" : " + t.getCompleted() 
						+ ", \"dueDate\" : \"" + t.getDueDate() + "\"" +
						"}, ";
		}
		if (result.length() > 5) {
			return result.substring(0, result.length() - 1);
		}
		return result;
		
	}
	
	public String toJson() {
		return "{\"id\" : " + id
				+ ", \"username\" : \"" + username + "\""
				+ ", \"password\" : \"" + password + "\""
				+ ", \"enabled\" : " + enabled
				+ ", \"role\" : \"" + role + "\"" 
				+ ", \"todos\" : [" + todosToJson() + "]" +
		"}";
	}

}

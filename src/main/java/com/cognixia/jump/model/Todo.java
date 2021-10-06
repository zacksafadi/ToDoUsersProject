package com.cognixia.jump.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class Todo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotBlank
	@Column(columnDefinition = "varchar(100)")
	private String description;
	
	@Column(columnDefinition = "boolean default false")
	private boolean completed;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	@JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS")
	private Date dueDate;
	
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;
	
	public Todo() {
		this(-1L, "N/A", false, new Date());
	}

	public Todo(Long id, @NotBlank String description, Boolean completed, Date dueDate) {
		super();
		this.id = id;
		this.description = description;
		this.completed = completed;
		this.dueDate = dueDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Todo [id=" + id + ", description=" + description + ", completed="
				+ completed + ", dueDate=" + dueDate + "]";
	}
	
	public String toJson() {
		return "{\"id\" : " + id
		+ ", \"description\" : \"" + description + "\""
		+ ", \"completed\" : " + completed 
		+ ", \"dueDate\" : \"" + dueDate + "\"" +
		"}, ";
	}
	
	

}

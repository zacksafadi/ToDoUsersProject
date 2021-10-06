package com.cognixia.jump.repository;

import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cognixia.jump.model.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long>{
	
	@Transactional
	@Modifying
	@Query("UPDATE Todo t SET t.completed = true where t.id = :id")
	public void completeTodo(@Param(value="id") long id);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM Todo t WHERE t.id = ?1")
	public void deleteTodo(@Param(value="id") long id);
	
	@Transactional
	@Modifying
	@Query("UPDATE Todo t SET t.dueDate = :dueDate where t.id = :id")
	public void updateDueDate(@Param(value="dueDate") Date date,@Param(value="id") long id);
	
	Optional<Todo> findById(long id);

}

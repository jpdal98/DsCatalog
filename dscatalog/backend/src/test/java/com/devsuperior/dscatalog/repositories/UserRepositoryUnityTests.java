package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.factory.UserFactory;

@DataJpaTest
public class UserRepositoryUnityTests {

	@Autowired 
	private UserRepository repository;
	
	private long existingId;
	private long nonExistingId;
	private long countTotalUser;
	
	@BeforeEach
	public void setup() throws Exception {
		
		existingId = 1l;
		nonExistingId = 1000L;
		countTotalUser = 2l;
	}
	
	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() throws Exception {

		User user = UserFactory.createUser();
		user.setId(null);
		
		user = repository.save(user);
		
		Assertions.assertNotNull(user.getId());
		Assertions.assertEquals(countTotalUser + 1, user.getId());
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() throws Exception {
		
		repository.deleteById(existingId);
		Optional<User> result = repository.findById(existingId);
		
		Assertions.assertFalse(result.isPresent());
	}
	

	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() throws Exception {
		
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
					repository.deleteById(nonExistingId);
				});
	}
	
	@Test
	public void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {
		
		Optional<User> result = repository.findById(existingId);
		
		Assertions.assertTrue(result.isPresent());
	}
	
	@Test
	public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExists() {
		
		Optional<User> result = repository.findById(nonExistingId);
		
		Assertions.assertTrue(result.isEmpty());
	}
}

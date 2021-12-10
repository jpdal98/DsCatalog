package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.factory.CategoryFactory;

@DataJpaTest
public class CategoryRepositoryUnityTests {

	@Autowired 
	private CategoryRepository repository;
	
	private long existingId;
	private long nonExistingId;
	private long countTotalCategory;
	
	@BeforeEach
	public void setup() throws Exception {
		
		existingId = 1l;
		nonExistingId = 1000L;
		countTotalCategory = 4l;
	}
	
	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() throws Exception {

		Category category = CategoryFactory.createCategory();
		category.setId(null);
		
		category = repository.save(category);
		
		Assertions.assertNotNull(category.getId());
		Assertions.assertEquals(countTotalCategory + 1, category.getId());
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() throws Exception {
		
		repository.deleteById(existingId);
		Optional<Category> result = repository.findById(existingId);
		
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
		
		Optional<Category> result = repository.findById(existingId);
		
		Assertions.assertTrue(result.isPresent());
	}
	
	@Test
	public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExists() {
		
		Optional<Category> result = repository.findById(nonExistingId);
		
		Assertions.assertTrue(result.isEmpty());
	}
}

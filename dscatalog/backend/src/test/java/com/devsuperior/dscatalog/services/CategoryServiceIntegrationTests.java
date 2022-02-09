package com.devsuperior.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dtos.CategoryDTO;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class CategoryServiceIntegrationTests {

	@Autowired
	private CategoryService service;

	@Autowired
	private CategoryRepository categoryRepository;
	
	private Long existingId;
	private Long nonExistingId;
	private Long countTotalCategories;
	
	@BeforeEach
	public void setup() throws Exception {
		
		existingId = 4L;
		nonExistingId = 1000L;
		countTotalCategories = 4L; 
		
	}
	
	@Test
	public void deleteShouldDeleteResourceWhenIdExists() {
		
		service.delete(existingId);
		
		Assertions.assertEquals(countTotalCategories - 1, categoryRepository.count());
	}
	
	@Test
	public void deleteShouldReturnThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}
	
	@Test
	public void findaAllPagedShouldReturnPageWhenPage0Size10() {
		
		PageRequest pageRequest = PageRequest.of(0, 4);
		Page<CategoryDTO> result = service.findAllPaged(pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(0, result.getNumber());
		Assertions.assertEquals(4, result.getSize());
		Assertions.assertEquals(countTotalCategories, result.getTotalElements());
	}
	
	@Test
	public void findaAllPagedShouldReturnEmptyPageWhenPageDoesNotExists() {
		
		PageRequest pageRequest = PageRequest.of(50, 4);
		Page<CategoryDTO> result = service.findAllPaged(pageRequest);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void findaAllPagedShouldReturnSortedPageWhenSortByName() {
		
		PageRequest pageRequest = PageRequest.of(0, 4, Sort.by("name"));
		Page<CategoryDTO> result = service.findAllPaged(pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals("Casa", result.getContent().get(0).getName());
		Assertions.assertEquals("Computadores", result.getContent().get(1).getName());
		Assertions.assertEquals("Eletrônicos", result.getContent().get(2).getName());
		Assertions.assertEquals("Livros", result.getContent().get(3).getName());
	}
	
	@Test
	public void findByIdShouldReturnCategoryDTOWhenIdExists() {
		
		CategoryDTO result = service.findById(existingId);
		
		Assertions.assertEquals(4L, result.getId());
		Assertions.assertEquals("Casa", result.getName());
	}
	
	@Test
	public void findByIdShouldReturnThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
	}
}

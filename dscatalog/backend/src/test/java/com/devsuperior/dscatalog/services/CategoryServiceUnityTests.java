package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dtos.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.factory.CategoryFactory;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class CategoryServiceUnityTests {

	@InjectMocks
	private CategoryService service;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private Category category;
	private CategoryDTO categoryDto;
	private PageImpl<Category> page;
	
	@BeforeEach
	public void setup() throws Exception {
		
		existingId = 1l;
		nonExistingId = 1000L;
		dependentId = 4L;
		category = CategoryFactory.createCategory();
		categoryDto = CategoryFactory.createCategoryDTO();
		page = new PageImpl<>(List.of(category));
		
		Mockito.when(categoryRepository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(categoryRepository.save(ArgumentMatchers.any())).thenReturn(category);
		
		Mockito.when(categoryRepository.findById(existingId)).thenReturn(Optional.of(category));
		Mockito.when(categoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		Mockito.when(categoryRepository.getById(existingId)).thenReturn(category);
		Mockito.when(categoryRepository.getById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.doNothing().when(categoryRepository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(categoryRepository)
			   .deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(categoryRepository)
		   .deleteById(dependentId);
		
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(existingId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
		Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShouldThrowDataBaseExceptionWhenDependentId() {
		
		Assertions.assertThrows(DataBaseException.class, () -> {
			service.delete(dependentId);
		});
		Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() {

		CategoryDTO result = service.update(existingId, categoryDto);
		
		Assertions.assertNotNull(result);
	}
	
	@Test 
	public void updateShouldReturnResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, categoryDto);
		});
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
		
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<CategoryDTO> result = service.findAllPaged(pageable);
		
		Assertions.assertNotNull(result);
		Mockito.verify(categoryRepository, Mockito.times(1)).findAll(pageable);
	}
	
	@Test 
	public void findByIdShouldReturnPageWhenIdExists() {
		
		Optional<Category> result = categoryRepository.findById(existingId);
		
		Assertions.assertNotNull(result);
		Mockito.verify(categoryRepository, Mockito.times(1)).findById(existingId);
	} 
	
	@Test 
	public void findByIdShouldReturnResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
		Mockito.verify(categoryRepository, Mockito.times(1)).findById(nonExistingId);
	} 
}

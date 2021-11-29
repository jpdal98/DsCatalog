package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dtos.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = categoryRepository.save(entity);
		
		return new CategoryDTO(entity);
	}
	
	public void delete(Long id) {
		try {
			categoryRepository.deleteById(id);
		}
		catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
		catch(DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity violation");
		}
	}
	
	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		
		/* Primeira forma
		Optional<Category> obj = repoCategory.findById(id);
		Category entity = obj.get();
		entity.setName(dto.getName());
		entity = repoCategory.save(entity);
		
		return new CategoryDTO(entity);
		*/
		
		// Forma preferida
		try{
			Category entity = categoryRepository.getById(id);
			entity.setName(dto.getName());
			entity = categoryRepository.save(entity);
			
			return new CategoryDTO(entity);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
	}
	
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(Pageable pageable){
		Page<Category> list = categoryRepository.findAll(pageable);
		return list.map(x -> new CategoryDTO(x));
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
	
		Optional<Category> obj = categoryRepository.findById(id);
		Category entity = obj.orElseThrow(() -> 
						  new ResourceNotFoundException("Entity not found!!"));
		
		return new CategoryDTO(entity);
	}
}

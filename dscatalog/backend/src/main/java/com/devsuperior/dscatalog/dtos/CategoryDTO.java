package com.devsuperior.dscatalog.dtos;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.devsuperior.dscatalog.entities.Category;

public class CategoryDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	@Size(min = 5, max = 60, message = "Nome deve ter entre 5 e 60 caracteres!!")
	@NotBlank(message = "Campo obrigatório!!")
	private String name;
	
	
	public CategoryDTO() {

	}

	public CategoryDTO(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public CategoryDTO(Category category) {
		this.id = category.getId();
		this.name = category.getName();
	}
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
}

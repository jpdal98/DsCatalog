package com.devsuperior.dscatalog.factory;

import com.devsuperior.dscatalog.dtos.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;

public class CategoryFactory {

	public static Category createCategory() {
		Category category = new Category(5L, "Celulares");
		return category;
	}
	
	public static CategoryDTO createCategoryDTO() {
		Category category = createCategory();
		return new CategoryDTO(category);
	}
	
}

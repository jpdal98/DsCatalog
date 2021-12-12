package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dtos.CategoryDTO;
import com.devsuperior.dscatalog.factory.CategoryFactory;
import com.devsuperior.dscatalog.utils.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CategoryResourceIntegrationTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	private Long existingId;
	private Long nonExistingId;
	private Long countTotalCategories;
	private CategoryDTO categoryDto;
	private String username;
	private String password;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalCategories = 4L;
		categoryDto = CategoryFactory.createCategoryDTO(); 
		username = "maria@gmail.com";
		password = "123456";
	}
	
	@Test
	public void updateShouldReturnCategoryDTOWhenIdExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		String jsonBody = objectMapper.writeValueAsString(categoryDto);
		
		ResultActions result = 
				mockMvc.perform(put("/categories/{id}", existingId)
					.header("Authorization", "Bearer " + accessToken)
					.content(jsonBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(existingId));
		result.andExpect(jsonPath("$.name").value(categoryDto.getName()));
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		String jsonBody = objectMapper.writeValueAsString(categoryDto);
		
		ResultActions result = mockMvc.perform(put("/categories/{id}", nonExistingId)
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		ResultActions result = mockMvc.perform(get("/categories?page=0&size=4&sort=name,ASC")
				.header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.content").exists());
		result.andExpect(jsonPath("$.totalElements").value(countTotalCategories));
		result.andExpect(jsonPath("$.content[0].name").value("Casa"));
		result.andExpect(jsonPath("$.content[1].name").value("Computadores"));
		result.andExpect(jsonPath("$.content[2].name").value("Eletrônicos"));
		result.andExpect(jsonPath("$.content[3].name").value("Livros"));
	}
	
	@Test
	public void findByIdShouldReturnCategoryDTOWhenIdExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		ResultActions result = mockMvc.perform(get("/categories/{id}", existingId)
				.header("Authorization", "Bearer " + accessToken)				
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(existingId));
		result.andExpect(jsonPath("$.name").value("Livros"));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		ResultActions result = mockMvc.perform(get("/categories/{id}", nonExistingId)
				.header("Authorization", "Bearer " + accessToken)				
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
}

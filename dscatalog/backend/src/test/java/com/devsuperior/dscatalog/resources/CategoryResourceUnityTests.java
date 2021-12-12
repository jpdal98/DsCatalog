package com.devsuperior.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dtos.CategoryDTO;
import com.devsuperior.dscatalog.factory.CategoryFactory;
import com.devsuperior.dscatalog.services.CategoryService;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.utils.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryResourceUnityTests {


	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private CategoryService service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private CategoryDTO categoryDto;
	private PageImpl<CategoryDTO> page;
	private String username;
	private String password;
	
	@BeforeEach
	public void setup() throws Exception {
		
		existingId = 1l;
		nonExistingId = 1000L;
		dependentId = 3L;
		categoryDto = CategoryFactory.createCategoryDTO();
		page = new PageImpl<>(List.of(categoryDto));
		username = "maria@gmail.com";
		password = "123456";
		
		Mockito.when(service.insert(any())).thenReturn(categoryDto);
		
		Mockito.when(service.update(eq(existingId), any())).thenReturn(categoryDto);
		Mockito.when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
		
		Mockito.when(service.findAllPaged(any())).thenReturn(page);	
		Mockito.when(service.findById(existingId)).thenReturn(categoryDto);
		Mockito.when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		doThrow(DataBaseException.class).when(service).delete(dependentId);
		doNothing().when(service).delete(existingId);
		
	}
	
	@Test
	public void insertShouldReturnNoCreatedAndProductDTO() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		String jsonBody = objectMapper.writeValueAsString(categoryDto);
		
		ResultActions result = mockMvc.perform(post("/categories")
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		ResultActions result = mockMvc.perform(delete("/categories/{id}", existingId)
				.header("Authorization", "Bearer " + accessToken));
		
		result.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		ResultActions result = mockMvc.perform(delete("/categories/{id}", nonExistingId)
				.header("Authorization", "Bearer " + accessToken));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		String jsonBody = objectMapper.writeValueAsString(categoryDto);
		
		ResultActions result = mockMvc.perform(put("/categories/{id}", existingId)
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
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
	public void findAllShouldReturnPage() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		ResultActions result = mockMvc.perform(get("/categories")
				.header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		ResultActions result = mockMvc.perform(get("/categories/{id}", existingId)
				.header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
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

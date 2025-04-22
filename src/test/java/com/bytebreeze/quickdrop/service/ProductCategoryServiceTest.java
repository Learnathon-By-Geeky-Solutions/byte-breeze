package com.bytebreeze.quickdrop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bytebreeze.quickdrop.entity.ProductCategoryEntity;
import com.bytebreeze.quickdrop.repository.ProductCategoryRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceTest {

	@Mock
	private ProductCategoryRepository productCategoryRepository;

	@InjectMocks
	private ProductCategoryService productCategoryService;

	private UUID categoryId;
	private ProductCategoryEntity sampleCategory;

	@BeforeEach
	void setUp() {
		categoryId = UUID.randomUUID();
		sampleCategory = new ProductCategoryEntity();
		sampleCategory.setId(categoryId);
		sampleCategory.setCategory("Electronics");
	}

	@Test
	void testGetAllCategories() {
		List<ProductCategoryEntity> categories = Arrays.asList(sampleCategory, new ProductCategoryEntity());
		when(productCategoryRepository.findAll()).thenReturn(categories);

		List<ProductCategoryEntity> result = productCategoryService.getAllCategories();

		assertEquals(2, result.size());
		verify(productCategoryRepository, times(1)).findAll();
	}

	@Test
	void testGetCategoryById_Found() {
		when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(sampleCategory));

		ProductCategoryEntity result = productCategoryService.getCategoryById(categoryId);

		assertNotNull(result);
		assertEquals(categoryId, result.getId());
		verify(productCategoryRepository, times(1)).findById(categoryId);
	}

	@Test
	void testGetCategoryById_NotFound() {
		when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

		ProductCategoryEntity result = productCategoryService.getCategoryById(categoryId);

		assertNull(result);
		verify(productCategoryRepository, times(1)).findById(categoryId);
	}

	@Test
	void testAddCategory() {
		when(productCategoryRepository.save(sampleCategory)).thenReturn(sampleCategory);

		ProductCategoryEntity result = productCategoryService.addCategory(sampleCategory);

		assertNotNull(result);
		assertEquals(categoryId, result.getId());
		verify(productCategoryRepository, times(1)).save(sampleCategory);
	}

	@Test
	void testUpdateCategory_Found() {
		when(productCategoryRepository.existsById(categoryId)).thenReturn(true);
		when(productCategoryRepository.save(sampleCategory)).thenReturn(sampleCategory);

		ProductCategoryEntity result = productCategoryService.updateCategory(categoryId, sampleCategory);

		assertNotNull(result);
		assertEquals(categoryId, result.getId());
		verify(productCategoryRepository, times(1)).existsById(categoryId);
		verify(productCategoryRepository, times(1)).save(sampleCategory);
	}

	@Test
	void testUpdateCategory_NotFound() {
		when(productCategoryRepository.existsById(categoryId)).thenReturn(false);

		ProductCategoryEntity result = productCategoryService.updateCategory(categoryId, sampleCategory);

		assertNull(result);
		verify(productCategoryRepository, times(1)).existsById(categoryId);
		verify(productCategoryRepository, times(0)).save(sampleCategory);
	}

	@Test
	void testDeleteCategory() {
		doNothing().when(productCategoryRepository).deleteById(categoryId);

		productCategoryService.deleteCategory(categoryId);

		verify(productCategoryRepository, times(1)).deleteById(categoryId);
	}
}

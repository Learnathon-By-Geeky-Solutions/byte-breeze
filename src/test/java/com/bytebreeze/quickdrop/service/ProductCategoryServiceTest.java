package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.model.ProductCategory;
import com.bytebreeze.quickdrop.repository.ProductCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceTest {

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @InjectMocks
    private ProductCategoryService productCategoryService;

    private UUID categoryId;
    private ProductCategory sampleCategory;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        sampleCategory = new ProductCategory();
        sampleCategory.setId(categoryId);
        sampleCategory.setCategory("Electronics");
    }

    @Test
    void testGetAllCategories() {
        List<ProductCategory> categories = Arrays.asList(sampleCategory, new ProductCategory());
        when(productCategoryRepository.findAll()).thenReturn(categories);

        List<ProductCategory> result = productCategoryService.getAllCategories();

        assertEquals(2, result.size());
        verify(productCategoryRepository, times(1)).findAll();
    }

    @Test
    void testGetCategoryById_Found() {
        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(sampleCategory));

        ProductCategory result = productCategoryService.getCategoryById(categoryId);

        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        verify(productCategoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testGetCategoryById_NotFound() {
        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        ProductCategory result = productCategoryService.getCategoryById(categoryId);

        assertNull(result);
        verify(productCategoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testAddCategory() {
        when(productCategoryRepository.save(sampleCategory)).thenReturn(sampleCategory);

        ProductCategory result = productCategoryService.addCategory(sampleCategory);

        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        verify(productCategoryRepository, times(1)).save(sampleCategory);
    }

    @Test
    void testUpdateCategory_Found() {
        when(productCategoryRepository.existsById(categoryId)).thenReturn(true);
        when(productCategoryRepository.save(sampleCategory)).thenReturn(sampleCategory);

        ProductCategory result = productCategoryService.updateCategory(categoryId, sampleCategory);

        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        verify(productCategoryRepository, times(1)).existsById(categoryId);
        verify(productCategoryRepository, times(1)).save(sampleCategory);
    }

    @Test
    void testUpdateCategory_NotFound() {
        when(productCategoryRepository.existsById(categoryId)).thenReturn(false);

        ProductCategory result = productCategoryService.updateCategory(categoryId, sampleCategory);

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

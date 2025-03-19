package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.model.ProductCategory;
import com.bytebreeze.quickdrop.repository.ProductCategoryRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ProductCategoryService {

	private ProductCategoryRepository productCategoryRepository;

	public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
		this.productCategoryRepository = productCategoryRepository;
	}

	public List<ProductCategory> getAllCategories() {
		return productCategoryRepository.findAll();
	}

	public ProductCategory getCategoryById(UUID categoryId) {
		Optional<ProductCategory> category = productCategoryRepository.findById(categoryId);
		return category.orElse(null);
	}

	public ProductCategory addCategory(ProductCategory category) {
		return productCategoryRepository.save(category);
	}

	public ProductCategory updateCategory(UUID categoryId, ProductCategory category) {
		if (productCategoryRepository.existsById(categoryId)) {
			category.setId(categoryId); // Ensure the ID is set correctly for update
			return productCategoryRepository.save(category);
		}
		return null;
	}

	public void deleteCategory(UUID categoryId) {
		productCategoryRepository.deleteById(categoryId);
	}
}

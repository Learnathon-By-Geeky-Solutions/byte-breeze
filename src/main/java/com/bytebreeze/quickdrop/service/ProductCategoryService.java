package com.bytebreeze.quickdrop.service;

import com.bytebreeze.quickdrop.entity.ProductCategoryEntity;
import com.bytebreeze.quickdrop.repository.ProductCategoryRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

	private final ProductCategoryRepository productCategoryRepository;

	public List<ProductCategoryEntity> getAllCategories() {
		return productCategoryRepository.findAll();
	}

	public ProductCategoryEntity getCategoryById(UUID categoryId) {
		Optional<ProductCategoryEntity> category = productCategoryRepository.findById(categoryId);
		return category.orElse(null);
	}

	public ProductCategoryEntity addCategory(ProductCategoryEntity category) {
		return productCategoryRepository.save(category);
	}

	public ProductCategoryEntity updateCategory(UUID categoryId, ProductCategoryEntity category) {
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

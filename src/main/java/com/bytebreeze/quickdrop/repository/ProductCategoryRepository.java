package com.bytebreeze.quickdrop.repository;

import com.bytebreeze.quickdrop.model.ProductCategory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {
	// You can add custom queries here if needed
}

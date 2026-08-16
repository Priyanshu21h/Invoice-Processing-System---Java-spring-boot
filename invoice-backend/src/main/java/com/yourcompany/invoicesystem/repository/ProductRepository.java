package com.yourcompany.invoicesystem.repository;

import com.yourcompany.invoicesystem.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Case-insensitive partial match search on product name.
     * Spring Data JPA derives the SQL: WHERE LOWER(name) LIKE LOWER('%keyword%')
     */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Count products with stock below the given threshold, used for dashboard
     * low-stock alert.
     */
    Long countByStockQuantityLessThan(int threshold);
}
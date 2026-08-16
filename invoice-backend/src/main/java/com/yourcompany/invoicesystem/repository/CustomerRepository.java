package com.yourcompany.invoicesystem.repository;

import com.yourcompany.invoicesystem.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Case-insensitive partial match search on customer name.
     * Spring Data JPA derives the SQL: WHERE LOWER(name) LIKE LOWER('%keyword%')
     * Returns a Page for paginated results.
     */
    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

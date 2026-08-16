package com.yourcompany.invoicesystem.repository;

import com.yourcompany.invoicesystem.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    // InvoiceItems are cascaded through Invoice entity (CascadeType.ALL),
    // so direct save/delete here is rarely needed. This repo exists for
    // any future queries (e.g. reporting: "top products by units sold").
}

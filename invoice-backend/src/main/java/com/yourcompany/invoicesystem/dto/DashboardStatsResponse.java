package com.yourcompany.invoicesystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    private BigDecimal totalSales;
    private Long totalInvoices;
    private Long totalCustomers;
    private Long totalProducts;
    private Long lowStockCount;
}
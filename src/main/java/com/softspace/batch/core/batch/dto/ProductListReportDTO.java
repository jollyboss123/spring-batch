package com.softspace.batch.core.batch.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductListReportDTO {
    private String carrierCode;
    private String productCode;
    private String productBrand;
    private String productName;
    private String description;
    private String status;
    private String productType;
    private String productCategory;
    private String flagCommissionable;
    private String flagVirtual;
    private String flagStockCount;
    private String flagAllowPriceOverride;
    private String flagAllowForSales;
    private String salesStart;
    private String salesEnd;
    private String baseCurrency;
    private String price;
    private String taxType;
    private String taxable;
    private String taxCode;
    private String barcode;
    private String createdDate;
    private String createdBy;
}

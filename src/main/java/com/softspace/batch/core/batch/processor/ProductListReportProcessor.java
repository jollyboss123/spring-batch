package com.softspace.batch.core.batch.processor;

import com.softspace.fasspos.common.batch.dto.ProductListReportDTO;
import com.softspace.fasspos.entity.constant.FassposStatus;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductListReportProcessor extends AbstractItemProcessor implements ItemProcessor<ProductListReportDTO, ProductListReportDTO> {

    @Override
    public ProductListReportDTO process(ProductListReportDTO productListReportDTO) throws Exception {

        String carrierCode = Optional.ofNullable(productListReportDTO.getCarrierCode())
                .map(String::trim)
                .orElse("");
        String productCode = Optional.ofNullable(productListReportDTO.getProductCode())
                .map(value -> "\"" + value.trim().replaceAll("\"", "\"\"") + "\"")
                .orElse("");
        String productBrand = Optional.ofNullable(productListReportDTO.getProductBrand())
                .map(value -> "\"" + value.trim().replaceAll("\"", "\"\"") + "\"")
                .orElse("");
        String productName = Optional.ofNullable(productListReportDTO.getProductName())
                .map(value -> "\"" + value.trim().replaceAll("\"", "\"\"") + "\"")
                .orElse("");
        String description = Optional.ofNullable(productListReportDTO.getDescription())
                .map(value -> "\"" + value.trim().replaceAll("\"", "\"\"") + "\"")
                .orElse("");
        String status = Optional.ofNullable(productListReportDTO.getStatus())
                .map(FassposStatus::getValueByEnumString)
                .orElse("");
        String productType = Optional.ofNullable(productListReportDTO.getProductType())
                .map(value -> value.trim().toUpperCase())
                .orElse("");
        String productCategory = Optional.ofNullable(productListReportDTO.getProductCategory())
                .map(value -> value.trim().toUpperCase())
                .orElse("");
        String isCommissionable = Optional.ofNullable(productListReportDTO.getFlagCommissionable())
                .map(value -> value.equals("1") ? "true" : "false")
                .orElse("");
        String isVirtualItem = Optional.ofNullable(productListReportDTO.getFlagVirtual())
                .map(value -> value.equals("1") ? "true" : "false")
                .orElse("");
        String isStockCount = Optional.ofNullable(productListReportDTO.getFlagStockCount())
                .map(value -> value.equals("1") ? "true" : "false")
                .orElse("");
        String isAllowPriceOverride = Optional.ofNullable(productListReportDTO.getFlagAllowPriceOverride())
                .map(value -> value.equals("1") ? "true" : "false")
                .orElse("");
        String isAllowForSale = Optional.ofNullable(productListReportDTO.getFlagAllowForSales())
                .map(value -> value.equals("1") ? "true" : "false")
                .orElse("");
        String salesStart = Optional.ofNullable(productListReportDTO.getSalesStart())
                .map(this::parseToDateString)
                .orElse("");
        String salesEnd = Optional.ofNullable(productListReportDTO.getSalesEnd())
                .map(this::parseToDateString)
                .orElse("");
        String currency = Optional.ofNullable(productListReportDTO.getBaseCurrency()).orElse("");
        String priceValue = Optional.ofNullable(productListReportDTO.getPrice()).orElse("");
        String taxType = Optional.ofNullable(productListReportDTO.getTaxType())
                .map(value -> value.equals("1") ? "Inclusive" : "Exclusive")
                .orElse("");
        String taxable = Optional.ofNullable(productListReportDTO.getTaxable())
                .map(value -> value.equals("1") ? "true" : "false")
                .orElse("");
        String tax = Optional.ofNullable(productListReportDTO.getTaxCode()).orElse("");
        String barcodes = Optional.ofNullable(productListReportDTO.getBarcode()).orElse("");
        String createdDateTime = Optional.ofNullable(productListReportDTO.getCreatedDate()).orElse("");
        String createdBy = Optional.ofNullable(productListReportDTO.getCreatedBy()).orElse("");

        return ProductListReportDTO.builder()
                .carrierCode(carrierCode)
                .productCode(productCode)
                .productBrand(productBrand)
                .productName(productName)
                .description(description)
                .status(status)
                .productType(productType)
                .productCategory(productCategory)
                .flagCommissionable(isCommissionable)
                .flagVirtual(isVirtualItem)
                .flagStockCount(isStockCount)
                .flagAllowPriceOverride(isAllowPriceOverride)
                .flagAllowForSales(isAllowForSale)
                .salesStart(salesStart)
                .salesEnd(salesEnd)
                .baseCurrency(currency)
                .price(priceValue)
                .taxType(taxType)
                .taxable(taxable)
                .taxCode(tax)
                .barcode(barcodes)
                .createdDate(createdDateTime)
                .createdBy(createdBy)
                .build();
    }

}

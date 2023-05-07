package com.softspace.batch.core.batch.rowmapper;

import com.softspace.fasspos.common.batch.dto.ProductListReportDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ProductListReportRowMapper implements RowMapper<ProductListReportDTO> {
    @Override
    public ProductListReportDTO mapRow(ResultSet resultSet, int i) throws SQLException {
        return ProductListReportDTO.builder()
                .carrierCode(resultSet.getString("carrierCode"))
                .productCode(resultSet.getString("productCode"))
                .productBrand(resultSet.getString("productBrand"))
                .productName(resultSet.getString("productName"))
                .description(resultSet.getString("description"))
                .status(resultSet.getString("status"))
                .productType(resultSet.getString("productType"))
                .productCategory(resultSet.getString("productCategory"))
                .flagCommissionable(resultSet.getString("flagCommissionable"))
                .flagVirtual(resultSet.getString("flagVirtual"))
                .flagStockCount(resultSet.getString("flagStockCount"))
                .flagAllowPriceOverride(resultSet.getString("flagAllowPriceOverride"))
                .flagAllowForSales(resultSet.getString("flagAllowForSales"))
                .salesStart(resultSet.getString("salesStart"))
                .salesEnd(resultSet.getString("salesEnd"))
                .baseCurrency(resultSet.getString("baseCurrency"))
                .price(resultSet.getString("price"))
                .taxType(resultSet.getString("isInclusiveTax"))
                .taxable(resultSet.getString("taxable"))
                .taxCode(resultSet.getString("taxCode"))
                .barcode(resultSet.getString("barcode"))
                .createdDate(resultSet.getString("createdDate"))
                .createdBy(resultSet.getString("createdBy"))
                .build();
    }
}

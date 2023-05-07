package com.softspace.batch.core.batch.rowmapper;

import com.softspace.fasspos.common.batch.dto.FlightSalesReportDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FlightSalesReportRowMapper implements RowMapper<FlightSalesReportDTO> {
    @Override
    public FlightSalesReportDTO mapRow(ResultSet resultSet, int i) throws SQLException {
        return FlightSalesReportDTO.builder()
                .carrierCode(resultSet.getString("carrierCode"))
                .flightNumber(resultSet.getString("flightNumber"))
                .origin(resultSet.getString("origin"))
                .destination(resultSet.getString("destination"))
                .countryOfOrigin(resultSet.getString("countryOfOrigin"))
                .countryOfDestination(resultSet.getString("countryOfDestination"))
                .flightDate(resultSet.getString("flightDate"))
                .departureTime(resultSet.getString("departureTime"))
                .productBrand(resultSet.getString("productBrand"))
                .productCategory(resultSet.getString("productCategory"))
                .productType(resultSet.getString("productType"))
                .productCode(resultSet.getString("productCode"))
                .promoRefId(resultSet.getString("promoRefId"))
                .product(resultSet.getString("product"))
                .baseCurrency(resultSet.getString("baseCurrency"))
                .productPrice(resultSet.getString("productPrice"))
                .quantity(resultSet.getString("quantity"))
                .promoType(resultSet.getString("promoType"))
                .promoName(resultSet.getString("promoName"))
                .discountType(resultSet.getString("discountType"))
                .discountRate(resultSet.getString("discountRate"))
                .discountAmount(resultSet.getString("discountAmount"))
                .dutyPaid(resultSet.getString("dutyPaid"))
                .rateType(resultSet.getString("rateType"))
                .rate(resultSet.getString("rate"))
                .grossSalesBase(resultSet.getString("grossSalesBase"))
                .netSalesBase(resultSet.getString("netSalesBase"))
                .billPromoName(resultSet.getString("billPromoName"))
                .billDiscountType(resultSet.getString("billDiscountType"))
                .billDiscountRate(resultSet.getString("billDiscountRate"))
                .billDiscountAmount(resultSet.getString("billDiscountAmount"))
                .billSubtotal(resultSet.getString("billSubtotal"))
                .billGrandTotal(resultSet.getString("billGrandTotal"))
                .build();
    }
}

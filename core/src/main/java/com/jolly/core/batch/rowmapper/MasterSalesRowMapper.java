package com.jolly.core.batch.rowmapper;

import com.softspace.fasspos.common.batch.dto.MasterSalesReportDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Jolly
 */
@Component
public class MasterSalesRowMapper implements RowMapper<MasterSalesReportDTO> {
    @Override
    public MasterSalesReportDTO mapRow(ResultSet rs, int i) throws SQLException {
        MasterSalesReportDTO masterSalesReportDTO = new MasterSalesReportDTO();

        masterSalesReportDTO.setArrivalTime(rs.getString("arrivalTime"));
        masterSalesReportDTO.setAircraftType(rs.getString("aircraftType"));
        masterSalesReportDTO.setAircraftRegistration(rs.getString("aircraftRegistration"));
        masterSalesReportDTO.setAuthorizationDateTime(rs.getString("authDateTime"));

        masterSalesReportDTO.setBaseCurrency(rs.getString("baseCurrency"));
        masterSalesReportDTO.setBillDiscountAmount(rs.getString("billDiscountAmount"));
        masterSalesReportDTO.setBillPromoName(rs.getString("billPromoName"));
        masterSalesReportDTO.setBillSubtotalInBigDecimal(rs.getBigDecimal("billSubtotal"));
        masterSalesReportDTO.setBillGrandTotalInBigDecimal(rs.getBigDecimal("billGrandtotal"));
        masterSalesReportDTO.setBillDiscountRate(rs.getString("billDiscountRate"));
        masterSalesReportDTO.setBillDiscountType(rs.getString("billDiscountType"));
        masterSalesReportDTO.setBillDiscountAmount(rs.getString("billDiscountAmount"));

        masterSalesReportDTO.setCard(rs.getString("cardAmount"));
        masterSalesReportDTO.setCarrierCode(rs.getString("carrierCode"));
        masterSalesReportDTO.setCardNumber(rs.getString("cardNumber"));
        masterSalesReportDTO.setCreditCardType(rs.getString("creditCardType"));
        masterSalesReportDTO.setCloseSyncDateTime(rs.getTimestamp("closeSyncDateTime"));
        masterSalesReportDTO.setCrewId(rs.getString("crewIds"));
        masterSalesReportDTO.setCashPaymentType(rs.getString("cashPaymentType"));

        masterSalesReportDTO.setDeviceName(rs.getString("deviceName"));
        masterSalesReportDTO.setDeviceId(rs.getString("deviceId"));
        masterSalesReportDTO.setDestination(rs.getString("destination"));
        masterSalesReportDTO.setDiscountAmountInBigDecimal(rs.getBigDecimal("discountAmount"));
        masterSalesReportDTO.setDiscountTypeId(rs.getInt("discountType"));
        masterSalesReportDTO.setDiscountRateInDouble(rs.getDouble("discountRate"));
        masterSalesReportDTO.setDepartureTime(rs.getString("departureTime"));

        masterSalesReportDTO.setFlightDate(rs.getString("flightDate"));
        masterSalesReportDTO.setFlightNumber(rs.getString("flightNo"));

        masterSalesReportDTO.setGrossSalesInBigDecimal(rs.getBigDecimal("grossSales"));

        masterSalesReportDTO.setIsPromoEntryInBool(rs.getBoolean("isPromoEntry"));

        masterSalesReportDTO.setNettSalesInBigDecimal(rs.getBigDecimal("nettSales"));

        masterSalesReportDTO.setOrigin(rs.getString("origin"));

        masterSalesReportDTO.setPaymentStatus(rs.getString("paymentStatus"));
        masterSalesReportDTO.setProduct(rs.getString("product"));
        masterSalesReportDTO.setPriceOverwriteInBigDecimal(rs.getBigDecimal("priceOverwrite"));
        masterSalesReportDTO.setPriceVarianceInBigDecimal(rs.getBigDecimal("priceVariance"));
        masterSalesReportDTO.setProductBrand(rs.getString("prodBrand"));
        masterSalesReportDTO.setProductCode(rs.getString("prodCode"));
        masterSalesReportDTO.setProductType(rs.getString("prodType"));
        masterSalesReportDTO.setProductCategory(rs.getString("prodCategory"));
        masterSalesReportDTO.setProductPrice(rs.getString("price"));
        masterSalesReportDTO.setPromoName(rs.getString("promoName"));
        masterSalesReportDTO.setPromoType(rs.getString("promoType"));
        masterSalesReportDTO.setPromoRefId(rs.getString("promoRefID"));
        masterSalesReportDTO.setPriceOverrideRemarks(rs.getString("priceOverrideRemarks"));
        masterSalesReportDTO.setPriceOverrideReason(rs.getString("priceOverrideReason"));

        masterSalesReportDTO.setQuantity(rs.getString("qty"));

        masterSalesReportDTO.setReferencePGRN(rs.getString("referencePGRN"));
        masterSalesReportDTO.setResubmission(rs.getString("resubmission"));
        masterSalesReportDTO.setRejectCode(rs.getString("rejectCode"));
        masterSalesReportDTO.setRefundReason(rs.getString("refundReason"));
        masterSalesReportDTO.setRefundRemarks(rs.getString("refundRemarks"));

        masterSalesReportDTO.setSeatNumber(rs.getString("seatNumber"));

        masterSalesReportDTO.setTransactionId(rs.getString("transactionId"));
        masterSalesReportDTO.setTransactionDateTime(rs.getString("transactionDateTime"));
        masterSalesReportDTO.setTransactionStatus(rs.getString("transactionStatus"));

        return masterSalesReportDTO;
    }
}

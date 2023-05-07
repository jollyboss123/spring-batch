package com.jolly.core.batch.dto;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jolly
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MasterSalesReportDTO {
    private String transactionId;
    private String referencePGRN;
    private String closeSyncDate;
    private String closeSyncTime;
    private String carrierCode;
    private String flightNumber;
    private String origin;
    private String destination;
    private String flightDate;
    private String departureTime;
    private String arrivalTime;
    private String transactionDate;
    private String transactionTime;
    private String originCountry;
    private String destinationCountry;
    private String aircraftRegistration;
    private String aircraftType;
    private String productBrand;
    private String productCategory;
    private String productType;
    private String productCode;
    private String promoRefId;
    private String product;
    private String quantity;
    private String baseCurrency;
    private String productPrice;
    private String priceOverwrite;
    private String priceVariance;
    private String priceOverrideReason;
    private String priceOverrideRemarks;
    private String promoType;
    private String promoName;
    private String discountType;
    private String discountRate;
    private String discountAmount;
    private String isPromoEntry;
    private String grossSales;
    private String nettSales;
    private String billPromoName;
    private String billDiscountType;
    private String billDiscountRate;
    private String billDiscountAmount;
    private String billSubtotal;
    private String billGrandTotal;
    private String rateType;
    private String rate;
    private String cardHolderName;
    private String cardNumber;
    private String creditCardType;
    private String authorizationDate;
    private String authorizationTime;
    private String settlementDate;
    private String paymentStatus;
    private String refundReason;
    private String refundRemarks;
    private String deviceName;
    private String deviceId;
    private String crewId;
    private String seatNumber;
    private String resubmission;
    private String rejectCode;
    private Map<String, BigDecimal> cashInMap = new HashMap<>();
    private String card;

    private Timestamp closeSyncDateTime;
    private String transactionDateTime;
    private String authorizationDateTime;
    private BigDecimal grossSalesInBigDecimal;
    private BigDecimal nettSalesInBigDecimal;
    private BigDecimal priceOverwriteInBigDecimal;
    private BigDecimal priceVarianceInBigDecimal;
    private Integer discountTypeId;
    private Double discountRateInDouble;
    private BigDecimal discountAmountInBigDecimal;
    private Boolean isPromoEntryInBool;
    private String transactionStatus;
    private String cashPaymentType;
    private BigDecimal billGrandTotalInBigDecimal;
    private BigDecimal billSubtotalInBigDecimal;

    public void addCrewCashCount(String columnName, BigDecimal value) {
        cashInMap.put(columnName, value);
    }
}

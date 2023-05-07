package com.softspace.batch.core.batch.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FlightSalesReportDTO {
    private String carrierCode;
    private String flightNumber;
    private String origin;
    private String destination;
    private String countryOfOrigin;
    private String countryOfDestination;
    private String flightDate;
    private String departureTime;
    private String productBrand;
    private String productCategory;
    private String productType;
    private String productCode;
    private String promoRefId;
    private String product;
    private String baseCurrency;
    private String productPrice;
    private String quantity;
    private String promoType;
    private String promoName;
    private String discountType;
    private String discountRate;
    private String discountAmount;
    private String dutyPaid;
    private String rateType;
    private String rate;
    private String grossSalesBase;
    private String netSalesBase;
    private String billPromoName;
    private String billDiscountType;
    private String billDiscountRate;
    private String billDiscountAmount;
    private String billSubtotal;
    private String billGrandTotal;
}
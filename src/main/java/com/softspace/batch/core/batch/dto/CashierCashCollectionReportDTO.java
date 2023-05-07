package com.softspace.batch.core.batch.dto;

import lombok.*;

import java.sql.Timestamp;

/**
 * @author Jolly
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CashierCashCollectionReportDTO {
    private String carrierCode;
    private String flightNumber;
    private String flightSector;
    private String flightDate;
    private String cashbagNumber;
    private String submissionDate;
    private String submissionTime;
    private String cashierId;
    private String totalSales;
    private String cash;
    private Timestamp modifiedDateTime;
}

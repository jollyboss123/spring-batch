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
public class CashbagReportDTO {
    private String carrierCode;
    private String cashbagNumber;
    private String createdDate;
    private String createdTime;
    private String flightNumber;
    private String flightSector;
    private String flightDate;
    private String lastSecCrewId;
    private String deviceName;
    private String deviceId;
    private BigDecimal totalSalesInBigDecimal;
    private BigDecimal cashInBigDecimal;
    private String totalSales;
    private String cash;
    private Map<String, BigDecimal> crewCashCountMap = new HashMap<>();
    private Timestamp createdDateTime;

    public void addCrewCashCount(String columnName, BigDecimal value) {
        crewCashCountMap.put(columnName, value);
    }
}

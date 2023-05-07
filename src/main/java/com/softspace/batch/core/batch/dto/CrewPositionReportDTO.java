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
public class CrewPositionReportDTO {
    private String carrierCode;
    private String flightNo;
    private String origin;
    private String destination;
    private String flightDate;
    private String departureTime;
    private String crewId;
    private String crewName;
    private String position;
    private String createdDate;
    private String createdTime;
    private Timestamp createdDateTime;
}


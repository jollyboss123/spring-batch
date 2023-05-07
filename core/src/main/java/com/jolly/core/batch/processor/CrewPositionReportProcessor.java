package com.jolly.core.batch.processor;

import com.softspace.common.util.FormatterUtil;
import com.softspace.fasspos.common.batch.dto.CrewPositionReportDTO;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;

/**
 * @author Jolly
 */
@Component
public class CrewPositionReportProcessor extends AbstractItemProcessor implements ItemProcessor<CrewPositionReportDTO, CrewPositionReportDTO> {

    @Override
    public CrewPositionReportDTO process(CrewPositionReportDTO crewPositionReportDTO) throws Exception {
        // null check process
        String carrierCode = crewPositionReportDTO.getCarrierCode() != null ? StringUtils.trimWhitespace(crewPositionReportDTO.getCarrierCode()).substring(0, 2) : "";
        String flightNumber = nullCheckForObject(crewPositionReportDTO.getFlightNo());
        String origin = nullCheckForObject(crewPositionReportDTO.getOrigin());
        String destination = nullCheckForObject(crewPositionReportDTO.getDestination());
        String flightDate = nullCheckForObject(crewPositionReportDTO.getFlightDate());
        String departureTime = nullCheckForObject(crewPositionReportDTO.getDepartureTime());
        String crewId = nullCheckForObject(crewPositionReportDTO.getCrewId());
        String crewName = nullCheckForObject(crewPositionReportDTO.getCrewName());
        String position = nullCheckForObject(crewPositionReportDTO.getPosition());

        // extract createdDate and createdTime from createdDateTime
        String createdDate = "", createdTime = "";
        if (crewPositionReportDTO.getCreatedDateTime() != null) {
            Instant createdDateTimeInstant = crewPositionReportDTO.getCreatedDateTime().toInstant();
            createdDate = FormatterUtil.getFormatted(createdDateTimeInstant, REPORT_DATE_FORMAT);
            createdTime = FormatterUtil.getFormatted(createdDateTimeInstant, REPORT_TIME_FORMAT);
        }

        return CrewPositionReportDTO.builder()
                .carrierCode(carrierCode)
                .flightNo(flightNumber)
                .origin(origin)
                .destination(destination)
                .flightDate(flightDate)
                .departureTime(departureTime)
                .crewId(crewId)
                .crewName(crewName)
                .position(position)
                .createdDate(createdDate)
                .createdTime(createdTime)
                .build();
    }
}

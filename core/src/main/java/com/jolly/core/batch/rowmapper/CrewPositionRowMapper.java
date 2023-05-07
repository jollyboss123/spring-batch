package com.jolly.core.batch.rowmapper;

import com.softspace.fasspos.common.batch.dto.CrewPositionReportDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Jolly
 */
@Component
public class CrewPositionRowMapper implements RowMapper<CrewPositionReportDTO> {

    @Override
    public CrewPositionReportDTO mapRow(ResultSet rs, int i) throws SQLException {
        CrewPositionReportDTO crewPositionReportDTO = new CrewPositionReportDTO();

        crewPositionReportDTO.setCarrierCode(rs.getString("carrier_code"));
        crewPositionReportDTO.setFlightNo(rs.getString("flight_no"));
        crewPositionReportDTO.setOrigin(rs.getString("flight_departure_station"));
        crewPositionReportDTO.setDestination(rs.getString("flight_arrival_station"));
        crewPositionReportDTO.setFlightDate(rs.getString("departure_date_local"));
        crewPositionReportDTO.setDepartureTime(rs.getString("departure_time_local"));
        crewPositionReportDTO.setCrewId(rs.getString("crews"));
        crewPositionReportDTO.setCrewName(rs.getString("names"));
        crewPositionReportDTO.setPosition(rs.getString("position"));
        crewPositionReportDTO.setCreatedDateTime(rs.getTimestamp("created_date"));

        return crewPositionReportDTO;
    }
}

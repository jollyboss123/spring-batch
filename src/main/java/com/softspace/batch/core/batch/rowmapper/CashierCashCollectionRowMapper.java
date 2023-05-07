package com.softspace.batch.core.batch.rowmapper;

import com.softspace.fasspos.common.batch.dto.CashierCashCollectionReportDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Jolly
 */
@Component
public class CashierCashCollectionRowMapper implements RowMapper<CashierCashCollectionReportDTO> {
    @Override
    public CashierCashCollectionReportDTO mapRow(ResultSet rs, int i) throws SQLException {
        CashierCashCollectionReportDTO cashierCashCollectionReportDTO = new CashierCashCollectionReportDTO();

        cashierCashCollectionReportDTO.setCashierId(rs.getString("modified_by"));
        cashierCashCollectionReportDTO.setCash(rs.getString("cash_amount"));
        cashierCashCollectionReportDTO.setCarrierCode(rs.getString("carrier_code"));
        cashierCashCollectionReportDTO.setFlightNumber(rs.getString("flight_nos"));
        cashierCashCollectionReportDTO.setFlightDate(rs.getString("flight_dates"));
        cashierCashCollectionReportDTO.setFlightSector(rs.getString("stations"));
        cashierCashCollectionReportDTO.setModifiedDateTime(rs.getTimestamp("modified_date"));
        cashierCashCollectionReportDTO.setTotalSales(rs.getString("total_approved_amt"));
        cashierCashCollectionReportDTO.setCashbagNumber(rs.getString("cashbag_no"));
        return null;
    }
}

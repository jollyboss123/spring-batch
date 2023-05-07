package com.softspace.batch.core.batch.processor;

import com.softspace.common.util.FormatterUtil;
import com.softspace.fasspos.common.batch.dto.CashbagReportDTO;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

/**
 * @author Jolly
 */
@Component
public class CashbagReportProcessor extends AbstractItemProcessor implements ItemProcessor<Map<String, Object>, CashbagReportDTO> {
    @Override
    public CashbagReportDTO process(Map<String, Object> row) throws Exception {
        CashbagReportDTO cashbagReportDTO = new CashbagReportDTO();

        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String columnName = entry.getKey();
            Object value = entry.getValue();

            if (!"carrier_code".equalsIgnoreCase(columnName) &&
            !"cashbag_no".equalsIgnoreCase(columnName) &&
            !"flight_nos".equalsIgnoreCase(columnName) &&
            !"created_date".equalsIgnoreCase(columnName) &&
            !"stations".equalsIgnoreCase(columnName) &&
            !"flight_dates".equalsIgnoreCase(columnName) &&
            !"crew_ids".equalsIgnoreCase(columnName) &&
            !"device_name".equalsIgnoreCase(columnName) &&
            !"device_id".equalsIgnoreCase(columnName) &&
            !"total_approved_amt".equalsIgnoreCase(columnName) &&
            !"pos_recorded_crew_cash_amount".equalsIgnoreCase(columnName)) {
                cashbagReportDTO.addCrewCashCount(columnName, (BigDecimal) value);
            }
        }

        // extract createdDate and createdTime from createdDateTime
        String createdDate = "", createdTime = "";
        Timestamp createdDateTime = (Timestamp) row.get("created_date");
        if (createdDateTime != null) {
            Instant createdDateTimeInstant = createdDateTime.toInstant();
            createdDate = FormatterUtil.getFormatted(createdDateTimeInstant, REPORT_DATE_FORMAT);
            createdTime = FormatterUtil.getFormatted(createdDateTimeInstant, REPORT_TIME_FORMAT);
        }

        return CashbagReportDTO.builder()
                .carrierCode((String) row.get("carrier_code"))
                .cashbagNumber(nullCheckForString((String) row.get("cashbag_no")))
                .createdDate(createdDate)
                .createdTime(createdTime)
                .flightNumber((String) row.get("flight_nos"))
                .flightSector((String) row.get("stations"))
                .flightDate((String) row.get("flight_dates"))
                .lastSecCrewId((String) row.get("crew_ids"))
                .deviceName(nullCheckForString((String) row.get("device_name")))
                .deviceId(nullCheckForString((String) row.get("device_id")))
                .totalSales(nullCheckForMonetary((BigDecimal) row.get("total_approved_amt")))
                .cash(nullCheckForMonetary((BigDecimal) row.get("pos_recorded_crew_cash_amount")))
                .crewCashCountMap(cashbagReportDTO.getCrewCashCountMap())
                .build();
    }
}

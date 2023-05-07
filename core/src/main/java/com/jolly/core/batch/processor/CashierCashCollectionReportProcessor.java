package com.jolly.core.batch.processor;

import com.softspace.common.util.FormatterUtil;
import com.softspace.fasspos.common.batch.dto.CashierCashCollectionReportDTO;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * @author Jolly
 */
@Component
public class CashierCashCollectionReportProcessor extends AbstractItemProcessor
        implements ItemProcessor<CashierCashCollectionReportDTO, CashierCashCollectionReportDTO> {

    @Override
    public CashierCashCollectionReportDTO process(CashierCashCollectionReportDTO cashierCashCollectionReportDTO) throws Exception {
        // extract submissionDate and submissionTime from getModifiedDatetime
        String submissionDate = "", submissionTime = "";
        if (cashierCashCollectionReportDTO.getModifiedDateTime() != null) {
            Instant createdDateTimeInstant = cashierCashCollectionReportDTO.getModifiedDateTime().toInstant();
            submissionDate = FormatterUtil.getFormatted(createdDateTimeInstant, REPORT_DATE_FORMAT);
            submissionTime = FormatterUtil.getFormatted(createdDateTimeInstant, SUBMIT_TIME_FORMAT);
        }

        return CashierCashCollectionReportDTO.builder()
                .carrierCode(cashierCashCollectionReportDTO.getCarrierCode())
                .cash(cashierCashCollectionReportDTO.getCash())
                .cashierId(cashierCashCollectionReportDTO.getCashierId())
                .cashbagNumber(cashierCashCollectionReportDTO.getCashbagNumber())
                .flightDate(cashierCashCollectionReportDTO.getFlightDate())
                .flightSector(cashierCashCollectionReportDTO.getFlightSector())
                .flightNumber(cashierCashCollectionReportDTO.getFlightNumber())
                .totalSales(cashierCashCollectionReportDTO.getTotalSales())
                .submissionDate(submissionDate)
                .submissionTime(submissionTime)
                .build();
    }
}

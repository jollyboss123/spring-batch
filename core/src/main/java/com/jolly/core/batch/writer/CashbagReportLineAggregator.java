package com.jolly.core.batch.writer;

import com.softspace.fasspos.common.batch.dto.CashbagReportDTO;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Jolly
 */
@Component
//@AllArgsConstructor
public class CashbagReportLineAggregator extends AbstractLineAggregator<CashbagReportDTO> {
    private String delimiter;
    private List<String> dynamicKeys;
    private ExecutionContext jobContext;

    @Override
    public String getDelimiter() {
        return this.delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setDynamicKeys(List<String> dynamicKeys) {
        this.dynamicKeys = dynamicKeys;
    }

    @Override
    public void setJobContext(ExecutionContext jobContext) {
        this.jobContext = jobContext;
    }

    @Override
    public String aggregate(CashbagReportDTO cashbagReportDTO) {
        StringBuilder sb = new StringBuilder();

        sb.append(cashbagReportDTO.getCarrierCode());
        sb.append(delimiter);
        sb.append(cashbagReportDTO.getCashbagNumber());
        sb.append(delimiter);
        sb.append(cashbagReportDTO.getCreatedDate());
        sb.append(delimiter);
        sb.append(cashbagReportDTO.getCreatedTime());
        sb.append(delimiter);
        sb.append(cashbagReportDTO.getFlightNumber());
        sb.append(delimiter);
        sb.append(cashbagReportDTO.getFlightSector());
        sb.append(delimiter);
        sb.append(cashbagReportDTO.getFlightDate());
        sb.append(delimiter);
        sb.append(cashbagReportDTO.getLastSecCrewId());
        sb.append(delimiter);
        sb.append(cashbagReportDTO.getDeviceName());
        sb.append(delimiter);
        sb.append(cashbagReportDTO.getDeviceId());
        sb.append(delimiter);
        sb.append(cashbagReportDTO.getTotalSales());
        sb.append(delimiter);
        sb.append(cashbagReportDTO.getCash());
        sb.append(delimiter);

        Map<String, BigDecimal> dynamicProps = cashbagReportDTO.getCrewCashCountMap();
        dynamicKeys = (List<String>) jobContext.get("headers");
        if (dynamicProps != null) {
            for (String key : dynamicKeys) {
                BigDecimal val = dynamicProps.get(key);
                sb.append(val != null ? val.toString() : "");
                sb.append(delimiter);
            }
        }

        sb.setLength(sb.length() - delimiter.length());
        return sb.toString();
    }
}

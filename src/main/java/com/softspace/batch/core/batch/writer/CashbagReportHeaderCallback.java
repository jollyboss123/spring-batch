package com.softspace.batch.core.batch.writer;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * @author Jolly
 */
//@AllArgsConstructor
@Component
public class CashbagReportHeaderCallback extends AbstractFlatFileHeaderCallback {
    private String delimiter;
    private ExecutionContext jobContext;

    @Override
    public String getDelimiter() {
        return this.delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setJobContext(ExecutionContext jobContext) {
        this.jobContext = jobContext;
    }

    @Override
    public void writeHeader(Writer writer) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append("Carrier Code");
        sb.append(delimiter);
        sb.append("Cashbag Number");
        sb.append(delimiter);
        sb.append("Created Date");
        sb.append(delimiter);
        sb.append("Created Time");
        sb.append(delimiter);
        sb.append("Flight Number");
        sb.append(delimiter);
        sb.append("Flight Sector");
        sb.append(delimiter);
        sb.append("Flight Date");
        sb.append(delimiter);
        sb.append("Last Sec Crew ID");
        sb.append(delimiter);
        sb.append("Device Name");
        sb.append(delimiter);
        sb.append("Device ID");
        sb.append(delimiter);
        sb.append("Total Sales (Base)");
        sb.append(delimiter);
        sb.append("Cash (Base)");
        sb.append(delimiter);

        List<String> dynamicProps = (List<String>) jobContext.get("headers");
        for (String val : dynamicProps) {
            sb.append(val);
            sb.append(delimiter);
        }

        sb.setLength(sb.length() - delimiter.length());
        writer.write(sb.toString());
    }
}

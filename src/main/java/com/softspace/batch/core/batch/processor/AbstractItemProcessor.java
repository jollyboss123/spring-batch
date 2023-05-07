package com.softspace.batch.core.batch.processor;

import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author Jolly
 */
public abstract class AbstractItemProcessor {
    protected static final String REPORT_DATE_FORMAT = "dd/MM/yyyy";
    protected static final String REPORT_TIME_FORMAT = "HH:mm:ss";
    protected static final String SUBMIT_TIME_FORMAT = "hh:mm:ss aa";
    protected static final String PIPE_DELIMETER = "|";

    protected String formatDateTime(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    protected String nullCheckForString(String value) {
        return value == null ? "" : StringUtils.trimWhitespace(value);
    }

    protected String nullCheckForMonetary(BigDecimal value) {
        return value == null ? "0.00" : value.toString();
    }

    protected String nullCheckForObject(Object value) {
        return value == null ? "0" : StringUtils.trimWhitespace(value.toString());
    }

    protected String parseToDateString(String dateTimeStr) {
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}

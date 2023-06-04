package com.jolly.starter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author jolly
 */
@Getter
@Setter
@NoArgsConstructor
public class JobProperties {
    private boolean enable = false;
    private String jobName = "job";
    private boolean triggerOnStart = false;
    private String cron = "";
}

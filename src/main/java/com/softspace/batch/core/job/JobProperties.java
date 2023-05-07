package com.softspace.batch.core.job;

public class JobProperties {
    private boolean enable = false;
    private String jobName = "job";
    private boolean triggerOnStart = false;
    private String cron = "";

    public JobProperties() {
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean getTriggerOnStart() {
        return triggerOnStart;
    }

    public boolean isTriggerOnStart() {
        return triggerOnStart;
    }

    public void setTriggerOnStart(boolean triggerOnStart) {
        this.triggerOnStart = triggerOnStart;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }
}

package com.github.cukedoctor.jenkins.model;

import java.io.Serializable;
import java.util.Date;

public class CukedoctorBuild implements Serializable {

    private final FormatType format;
    private final Integer buildNumber;
    private final Date buildTime;


    public CukedoctorBuild(FormatType format, Integer buildNumber, Date buildTime) {
        this.format = format;
        this.buildNumber = buildNumber;
        this.buildTime = buildTime;
    }

    public Integer getBuildNumber() {
        return buildNumber;
    }

    public Date getBuildTime() {
        return buildTime;
    }

    public FormatType getFormat() {
        return format;
    }

    public Boolean isHtmlDocs() {
        return format.equals(FormatType.HTML) || format.equals(FormatType.ALL);
    }

    public Boolean isPdfDocs() {
        return format.equals(FormatType.PDF) || format.equals(FormatType.ALL);
    }
}

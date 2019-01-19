package com.example.examcountdown;

import java.util.Date;

public class Exam {
    private Date dateTime;
    private String code, description;

    public Exam(Date dateTime, String code, String description) {
        this.dateTime = dateTime;
        this.code = code;
        this.description = description;
    }

    public Date getDateTime() { return dateTime; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public String toString() { return getCode() + ": " + getDescription(); }
}

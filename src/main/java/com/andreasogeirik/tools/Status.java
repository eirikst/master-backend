package com.andreasogeirik.tools;

/**
 * Created by eirikstadheim on 31/01/16.
 */
public class Status {
    private int code;
    private String reason;

    public Status() {

    }

    public Status(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

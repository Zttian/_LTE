package com.tky.lte.ui.event;

/**
 * Created by I am on 2018/6/25.
 */

public class TmpGroupBean {
    private int nResult;
    private String szGroupNumber;
    private String szReason;

    public int getnResult() {
        return nResult;
    }

    public void setnResult(int nResult) {
        this.nResult = nResult;
    }

    public String getSzGroupNumber() {
        return szGroupNumber;
    }

    public void setSzGroupNumber(String szGroupNumber) {
        this.szGroupNumber = szGroupNumber;
    }

    public String getSzReason() {
        return szReason;
    }

    public void setSzReason(String szReason) {
        this.szReason = szReason;
    }
}

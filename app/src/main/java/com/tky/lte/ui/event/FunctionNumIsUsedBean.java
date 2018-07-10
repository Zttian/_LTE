package com.tky.lte.ui.event;

/**
 * Created by bhfwg on 2018/6/30.
 */

public class FunctionNumIsUsedBean {
    public int getnResult() {
        return nResult;
    }

    public void setnResult(int nResult) {
        this.nResult = nResult;
    }

    public String getSzFN() {
        return szFN;
    }

    public void setSzFN(String szFN) {
        this.szFN = szFN;
    }

    public String getSzUN() {
        return szUN;
    }

    public void setSzUN(String szUN) {
        this.szUN = szUN;
    }

    public String getSzReason() {
        return szReason;
    }

    public void setSzReason(String szReason) {
        this.szReason = szReason;
    }

    private int nResult;
    private String szFN;
    private String szUN;
    private String szReason;
}

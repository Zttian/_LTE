package com.tky.lte.ui.event;

/**
 * Created by I am on 2018/6/24.
 * int nFunctionNumberType, int nOperationType, int nResult, String szFN, String szReason
 */

public class FunctionRegisterResponseBean {
    private int nFunctionNumberType;
    private int nOperationType;
    private int nResult;
    private String szFN;
    private String szReason;

    public int getnFunctionNumberType() {
        return nFunctionNumberType;
    }

    public void setnFunctionNumberType(int nFunctionNumberType) {
        this.nFunctionNumberType = nFunctionNumberType;
    }

    public int getnOperationType() {
        return nOperationType;
    }

    public void setnOperationType(int nOperationType) {
        this.nOperationType = nOperationType;
    }

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

    public String getSzReason() {
        return szReason;
    }

    public void setSzReason(String szReason) {
        this.szReason = szReason;
    }
}

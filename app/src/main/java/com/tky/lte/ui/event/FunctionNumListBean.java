package com.tky.lte.ui.event;

import java.util.ArrayList;

/**
 * Created by I am on 2018/6/24.
 */

public class FunctionNumListBean{
    private int resultCode;
    private ArrayList<String> funcNumList;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public ArrayList<String> getFuncNumList() {
        return funcNumList;
    }

    public void setFuncNumList(ArrayList<String> funcNumList) {
        this.funcNumList = funcNumList;
    }
}

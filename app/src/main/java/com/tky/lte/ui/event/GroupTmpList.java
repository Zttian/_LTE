package com.tky.lte.ui.event;

import com.tky.lte.lte.GroupTmpCall;

import java.util.ArrayList;

/**
 * Created by I am on 2018/6/26.
 */

public class GroupTmpList {
   private ArrayList<GroupTmpCall> lstGroupTmpCalls;

    public ArrayList<GroupTmpCall> getLstGroupTmpCalls() {
        return lstGroupTmpCalls;
    }

    public void setLstGroupTmpCalls(ArrayList<GroupTmpCall> lstGroupTmpCalls) {

        //
        //this.lstGroupTmpCalls = lstGroupTmpCalls;
        if( this.lstGroupTmpCalls == null )
            this.lstGroupTmpCalls = new ArrayList<GroupTmpCall>();
        else
            this.lstGroupTmpCalls.clear();
        for (GroupTmpCall groupTmpCall:  lstGroupTmpCalls) {
            GroupTmpCall aGroupTmpCall = new GroupTmpCall();
            aGroupTmpCall.Set(groupTmpCall);
            this.lstGroupTmpCalls.add(aGroupTmpCall);
        }
    }
}

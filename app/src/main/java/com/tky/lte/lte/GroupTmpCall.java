package com.tky.lte.lte;

/**
 * Created by bhfwg on 2018/6/23.
 */
import com.jiaxun.android.lte_r.ServiceConstant;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupTmpCall implements Serializable{
    private static final long serialVersionUID = 4851794964082321682L;

    public GroupTmpCall() {
        lstMemberList = new ArrayList<String>();
    }
    public int iCallType = ServiceConstant.CALL_TYPE_TEMPGROUP;//临时组呼
    public String strGroupId = ""; //临时组号码
    public String strGroupName = ""; //临时组呼名称
    public int iPriority = 2; //优先级
    public String strRequester = "";//临时组呼编组人号码
    public int nLifespan = 34;//临时群组的最大生存周期 //
    public ArrayList<String> lstMemberList = new ArrayList<String>() ; //临时组呼人员列表//
    public int nMaxIdleTime = 0; //群组无讲者释放时长 //

    public void Set(GroupTmpCall groupTmpCall){
        iCallType = groupTmpCall.iCallType;
        strGroupId =  groupTmpCall.strGroupId;
        strGroupName =  groupTmpCall.strGroupName;
        iPriority =  groupTmpCall.iPriority;
        strRequester =  groupTmpCall.strRequester;
        nLifespan =  groupTmpCall.nLifespan;
        nMaxIdleTime =  groupTmpCall.nMaxIdleTime;
        lstMemberList.clear();//
        for(int i = 0; i<groupTmpCall.lstMemberList.size(); i++){
            lstMemberList.add(groupTmpCall.lstMemberList.get(i));
        }
    }

}

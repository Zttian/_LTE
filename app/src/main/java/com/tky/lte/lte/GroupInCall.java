package com.tky.lte.lte;

import com.jiaxun.android.lte_r.ServiceConstant;

public class GroupInCall
{
	public  GroupInCall()
	{

	}
	public int iCallType = ServiceConstant.CALL_TYPE_GROUP;//组呼/临时组呼/广播
	public String strGroupId = "";//组呼号码
	public String strCallerFn = "";//发起者功能号码
	public int iPriority = -1;
	public boolean bAutoCall = false;//自动呼叫标志,如果该组呼是用户手动挂掉则为false,如果为被抢占则为true
}

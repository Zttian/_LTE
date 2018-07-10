package com.tky.lte.lte;

public class CALL
{
	public String strCallId = "";//服务器返回的id 区别呼叫
	public int callType;//呼叫类型 01个呼 02组呼03广播(不处理 和主呼一样处理)
	public String peerNumber = "";//号码
	public String groupId = "";//组呼id
	public String funNumber = "";//功能号码
	public int statusCode = 0;//状态码 00空闲状态通话结束 01 呼出状态 02正在回铃状态  03通话中 05振铃状态 06呼叫保持(等待)
	public int priority = 3;//优先级
	public int call_way; // call_in = 1;呼入    call_out = 2;呼出
	public boolean call_handout;//手动挂断的标志
	public int nOnLineUserCount;//组呼中人数
	
	public void CopyFromOne(CALL aCall)
	{
		strCallId = aCall.strCallId;
		callType = aCall.callType;
		
		peerNumber = aCall.peerNumber;
		groupId = aCall.groupId;		
		funNumber = aCall.funNumber;
		
		statusCode = aCall.statusCode;
		priority = aCall.priority;
		call_way = aCall.call_way;
		call_handout = aCall.call_handout;
		nOnLineUserCount = aCall.nOnLineUserCount;
	}
}

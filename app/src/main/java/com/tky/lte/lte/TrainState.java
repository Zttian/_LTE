package com.tky.lte.lte;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TrainState
{
	public static TrainState m_instance = null;

	public static TrainState getInstance()
	{
		if (m_instance == null)
		{
			m_instance = new TrainState();
		}
		return m_instance;
	}


	public byte g_PocStateZhuBoard = 0;

	public byte g_Power = 0x01; // 是否有外部电源 1有电源 0电源
	public byte g_Mode = 0x66; // 机车运行模式 01-64:450M,65:GSMR,0x66:lte,81-e4:450M+lte
	public byte g_MainMMI = 4; // 2代表都是副控,3代表串口3处是主控,4代表串口4处是主控

	public byte g_Hook_Flag = 0; // 摘挂机
	public byte g_PTT_Flag = 0; // 是否按下PPT
	public byte g_Mode_Flag = 0; // '0'自动，'1'手动

	public byte m_BenBu_Status = 0; /* 0:本务机；1:补机 */

	public _TrainNumber g_TrainNumber = new _TrainNumber(); // 车次号
	public _TrainNumber g_DisplayTrainNumber = new _TrainNumber(); // 显示的车次号
	public _EngineNumber g_EngineNumber = new _EngineNumber(); // 机车号

	public _EngineNumber g_MainEngineNumber = new _EngineNumber(); // 主控机车号//1220

	public String g_Line_Name; // 线路名

	public String g_LastTrainNumber; // 上次运行的车次号
	public String g_LastRegisterStatus; // 标识上次注册
	public String g_LastBenBuStatus; // 上次本补状态 0:本务机；1:补机
	public String g_CheckIP1; // 库检中心地址
	public String g_CheckIP2;
	public String g_CheckIP3;
	public byte[] m_CheckIP1 = new byte[6] ; // 库检
	public byte[] m_CheckIP2 = new byte[6] ;
	public byte[] m_CheckIP3 = new byte[6] ;//*.g_DebugLog_Lev3.g_DebugLog_Lev2.g_DebugLog_Lev1
	public byte[] m_CurrentCheckIP = new byte[6] ;
	public String g_CheckPhoneNumber1; // 库检电话号码
	public String g_CheckPhoneNumber2;
	public String g_CheckPhoneNumber3;
	public String g_APN; // APN号

	public byte[] g_BatteryVoltage = new byte[4] ; // g_BatteryVoltage[4],电池电压
	// 0x01:关闭电源过程中,未知状态 0x02:查询中
	public byte[] g_Temperature = new byte[2] ; // g_Temperature[2],温度


	// 库检相关
	public int g_OutputSignalStatus = 0xffff; // 用于自检时给接口板设置打开闭合电源

	public byte[] g_LineCode = new byte[2] ; // 线路代码2
	public int g_TrainNumberInfoToNowGRISTxCounter = 0; // 向GRIS发送车次号次数
	public int g_TrainNumberInfoStartOrStopToNowGRISTxCounter = 0; // 向GRIS发送启动停稳次数
	public byte[] g_CTCMaintenanceInfo = new byte[32] ;
	public byte[] TrainNumberTemp = new byte[7] ; // 7
	public byte[] TrainNumberT1 = new byte[7] ; // 7
	public byte[] TrainNumberT2 = new byte[7] ; // 7
	public byte[] TrainNumberT3 = new byte[7] ; // 7
	public byte[] TrainNumberT4 = new byte[7] ; // 7
	public byte[] EngineNumberTemp = new byte[8] ; // 8
	public byte[] EngineNumberT1 = new byte[8] ; // 8
	public byte[] EngineNumberT2 = new byte[8] ; // 8
	public byte[] EngineNumberT3 = new byte[8] ; // 8
	public byte[] EngineNumberT4 = new byte[8] ; // 8

	// ////LTE/////
	public byte[] m_SourLteAddre = new byte[6] ; // 本机IP地址,即主控lte地址
	public String m_lac = "1234";
	byte[] mnLTELac = new byte[2] ; // 只有在ltehandle中修改
	public String m_ci = "1234";
	byte[] mnLTECi = new byte[4] ; // 只有在ltehandle中修改
	public String mszCellID = "";

	public byte[] m_DMISAddre = new byte[6] ; // DMIS IP地址，目标gris ip

	public byte[] m_TailAddre = new byte[6] ; // 列尾IP地址
	public byte[] m_TailAddre2 = new byte[6] ; // 列尾IP地址

	public byte[] m_GROSAddre = new byte[6] ; // 主用GROS的IP地址
	public String strGrosAddre = "";
	public byte[] m_GROSAddre1 = new byte[6] ; // bei用GROS的IP地址
	public byte[] m_HomeDMISAddre = new byte[6] ; // 归属GRIS的IP地址


	public byte m_Signal_Strenghthen_MainControl = 99; // 模块场强级别//m_SourLteAddre

	public byte m_InqiureGROSCounter = 6; //6// 查询gris ip 地址次数

	// ////GPRS/////

	//public String mLTE_aaaUserName = "";
	public String mLTE_Imsi = "";
	public String mLTE_Msisdn = "";

	public String mLTE_UserName = "";//01板的mLTE_UserName == 7F板的mLTE_UserName2
	public String mLTE_PassWord = "";
	public String mLTE_UserName2 = "";// 另外一块板的用户名密码,若为0x01则为第2字段,若为0x7F则为第1字段
	public String mLTE_PassWord2 = "";
	public String mLTE_SIP_IP = "";
	public String mLTE_SIP_IP2 = "";

    public String g_AutoAnswer = "";

	public String TrainFunctionNumber;
	public String EngineFunctionNumber;
	public byte TrainNumberFNRegisterStatus = 0; // 0和2代表未注册,1代表已注册
	public byte EngineNumberFNRegisterStatus = 0;
	// 0:注销状态
	// 1:功能号已注册到本机SIM卡;
	// 2:功能号已注册到其他设备的SIM卡
	// 3:功能号未被任何SIM卡注册
	// 4:功能号为非法号码
	// 5:未开通Follow Me 业务

	public byte g_DthFlag = 0x01;

	// 初始设置0.0.1.1
	public byte g_DebugLog_Lev4 = 0x00;//网络日志发送

	public byte g_DebugLog_Lev3 = 0x00; // 0不打开,1打开日志, 该日志记录最不需要的记录,如串口数据生命帧数据等

	public byte g_DebugLog_Lev2 = 0x00; // 0不打开,1打开日志, 该日志记录POC的日志

	public byte g_DebugLog_Lev1 = 0x01; // 0不打开,1打开日志, 该日志记录系统运行日志

	public int g_UserSetting_Myself = 0x03; // 工作方式:单端单MMI0x03,单端双MMI0x02,主副控0x01
	public int g_UserMode_Myself = 0x00; // 工作模式:0x00H表示综合角色,0x01H列调角色,0x02H表示重联角色,0xFFH表示降级模式,0x0F未设置
	public int g_UserMode_OtherBoard = (byte) 0xFF;

	public byte iZhuCong = 0x00;//主从
	public byte iZhongJi = 0x00;//中继

	public byte g_bABJie = 0x00;
	public byte g_bJueSe = 0x00;
	public byte g_bLocation = 0x00;



	private TrainState()
	{
		java.util.Arrays.fill(g_BatteryVoltage, (byte) 0);
		java.util.Arrays.fill(g_Temperature, (byte) 0);
		java.util.Arrays.fill(g_LineCode, (byte) 0);
		java.util.Arrays.fill(TrainNumberTemp, (byte) 'X');
		java.util.Arrays.fill(TrainNumberT1, (byte) 'X');
		java.util.Arrays.fill(TrainNumberT2, (byte) 'X');
		java.util.Arrays.fill(TrainNumberT3, (byte) 'X');
		java.util.Arrays.fill(TrainNumberT4, (byte) 'X');
		java.util.Arrays.fill(EngineNumberTemp, (byte) 'X');
		java.util.Arrays.fill(EngineNumberT1, (byte) 'X');
		java.util.Arrays.fill(EngineNumberT2, (byte) 'X');
		java.util.Arrays.fill(EngineNumberT3, (byte) 'X');
		java.util.Arrays.fill(EngineNumberT4, (byte) 'X');
	}

	public void Set_g_CheckPhoneNumber(_RawInfo PackageInfo)
	{
		int datalength = PackageInfo.InfoLenth - 8 - PackageInfo.SourAddreLenth - PackageInfo.DectAddreLenth;
		int i = 0, j = 0, k = 0, begin = 0;
		begin = 0;
		for (i = begin; (i < datalength) && (PackageInfo.Data[i] != ';'); i++)
		{
			;
		}
		g_CheckPhoneNumber1 = new String(PackageInfo.Data, begin, i - begin);
		i++;// 跳过符号;
		begin = i;
		for (j = begin; (j < datalength) && (PackageInfo.Data[j] != ';'); j++)
		{
			;
		}
		g_CheckPhoneNumber2 = new String(PackageInfo.Data, begin, j - begin);
		j++;
		begin = j;
		for (k = begin; (k < datalength) && (PackageInfo.Data[k] != ';'); k++)
		{
			;
		}
		g_CheckPhoneNumber3 = new String(PackageInfo.Data, begin, k - begin);
	}

	public void Set_m_HomeDMISAddre(byte b1, byte b2, byte b3, byte b4)
	{
		m_HomeDMISAddre[0] = b1;
		m_HomeDMISAddre[1] = b2;
		m_HomeDMISAddre[2] = b3;
		m_HomeDMISAddre[3] = b4;
	}

	public void Set_m_CheckIP(byte b1, byte b2, byte b3, byte b4, int flag)
	{
		if (flag == 1)
		{
			m_CheckIP1[0] = b1;
			m_CheckIP1[1] = b2;
			m_CheckIP1[2] = b3;
			m_CheckIP1[3] = b4;
			g_CheckIP1 = String.format(("%d.%d.%d.%d"), b1 & 0x00FF, b2 & 0x00FF, b3 & 0x00FF, b4 & 0x00FF);
		}
		else if (flag == 2)
		{
			m_CheckIP2[0] = b1;
			m_CheckIP2[1] = b2;
			m_CheckIP2[2] = b3;
			m_CheckIP2[3] = b4;
			g_CheckIP2 = String.format(("%d.%d.%d.%d"), b1 & 0x00FF, b2 & 0x00FF, b3 & 0x00FF, b4 & 0x00FF);
		}
		else
		{
			m_CheckIP3[0] = b1;
			m_CheckIP3[1] = b2;
			m_CheckIP3[2] = b3;
			m_CheckIP3[3] = b4;
			g_CheckIP3 = String.format(("%d.%d.%d.%d"), b1 & 0x00FF, b2 & 0x00FF, b3 & 0x00FF, b4 & 0x00FF);
		}
	}

	public int Set_mLte_SIPVOP(_RawInfo PackageInfo, int index, int flag) throws UnsupportedEncodingException
	{
		int j;
		String str = "";
		for (j = index; j < PackageInfo.InfoLenth && PackageInfo.Data[j] != ';'; j++)
		{
			;
		}
		str = new String(PackageInfo.Data, index, j - index, "GBK");
		if (flag == 1)
			mLTE_UserName = str;
		else if (flag == 2)
			mLTE_PassWord = str;
		else if (flag == 3)
			mLTE_UserName2 = str;
		else if (flag == 4)
			mLTE_PassWord2 = str;
		else if (flag == 5)
			mLTE_SIP_IP = str;
		else if (flag == 6)
			mLTE_SIP_IP2 = str;
		// else
		// mLTE_VOP_IP = str;
		return j + 1;
	}

	public void Set_m_GROSAddre(byte b, byte c, byte d, byte e)
	{
		m_GROSAddre[0] = b;
		m_GROSAddre[1] = c;
		m_GROSAddre[2] = d;
		m_GROSAddre[3] = e;
	}

	public void Set_m_GROSAddre1(byte b, byte c, byte d, byte e)
	{
		m_GROSAddre1[0] = b;
		m_GROSAddre1[1] = c;
		m_GROSAddre1[2] = d;
		m_GROSAddre1[3] = e;
	}
}

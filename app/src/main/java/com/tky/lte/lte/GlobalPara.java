package com.tky.lte.lte;

import java.util.Calendar;
import java.util.HashMap;

public class GlobalPara
{
	public static String strPhoneId = "";
	public static String strPhoneImsi = "";

	public static final int BIT0 = 0x01;
	public static final int BIT1 = 0x02;
	public static final int BIT2 = 0x04;
	public static final int BIT3 = 0x08;
	public static final int BIT4 = 0x10;
	public static final int BIT5 = 0x20;
	public static final int BIT6 = 0x40;
	public static final int BIT7 = 0x80;
	public static final int BIT8 = 0x100;
	public static final int BIT9 = 0x200;
	public static final int BIT10 = 0x400;
	public static final int BIT11 = 0x800;
	public static final int BIT12 = 0x1000;
	public static final int BIT13 = 0x2000;
	public static final int BIT14 = 0x4000;
	public static final int BIT15 = 0x8000;
	public static final int ElseRecordNumber = 100;//记录的上限条数


	public static String Tky = "tky";

	//主副板卡同步相关
	public static int iZhuKongFlag = 0x01; // 标识1号位主控或者2号位主控
	public static int iOtherBoardFlag = 0x7F;

	//本机IP地址相关
	public static String strLocalPort = "20000";
	public static String strCTCDestinationPort = "20001";
	public static String strDTEDestinationPort = "30000";
	public static String strLieWeiDestinationPort = "30020";
	public static String strMonitorDestinationPort = "20004";//系统日志服务器
	public static String strLocalAssisantPort = "30000";//本地网口打开的udp,取350日志时用到

	//重联组呼相关
	public static String strDoubleHeadingGroupID = "";//重联组呼号码
	public static String strMainEngineNumber="";//注册的主控机车号码
	//列尾相关
	public static byte[] arrLieWeiZhuangZhi = new byte[3] ;//列尾装置号
	public static byte[] arrDteZhuangZhi = new byte[3] ;//给DTE的装置号,用于存储
	public static byte bCarType = 0x00;//机车属性(主车00,从一 01  从二 02  从三 03)
	public static byte bCarNumber =0x00;//机车数量


	public static long lastLieweiTimelte = -1 /* System.currentTimeMillis()*/; // 上次收到lte列尾信息的时间点
	public static boolean bLieWeiIsOklte = false;
	public static int ilwlte = 0;

	public static String strLoginUsrname=""; //用于注册网络时用的用户名
	public static String strNationalCode = "46030";

	public static HashMap<String, String> htParameters = new HashMap<String, String>();//参数文件
	public static HashMap<String, String> htParameters_Log_Net = new HashMap<String, String>();//登网参数文件

	//主用备用端
	public static boolean bMainUsed = true; //true代表是主用端
	public static long lStartServiceTime = -1; //系统开机时记录时间,一分钟后才pic报告的双串口状态为0才能用来判定MMI故障


	public static String strTempCallId = null;
	public static boolean bHaveFindLteMoulde = false;//模块是否存在

	public static String g_SoftwareVersion = "V4.0.20 16/03/01"; // 主控软件版本//version
	public static String g_PocVersion = "V0.0.00 00/00/00"; // 软件版本



	public static byte iGlobalMMIPort = 4;

	public static String strBatteryVoltage = "00.0";//电池电压

	public static int iInDoubleGroup = 0;//是否成功加入重联组呼

	public static String strPreGroupcall = "50";
	public static String strPreBoardcastcall = "51";
	public static String strGroupcallShortNumber = "789";
	public static String strPreNationNumber = "86";
	public static String strPreGlobalNumber = "086";
	public static String strPreTfnNumber = "2";
	public static String strPreEfnNumber = "3";
	public static String strPreNdc = "146";
	public static String strTailFor299 = "299";

	public static String strCheZhanShortNumber = "1300";
	public static String strDiaoDuShortNumber = "1200";

	public static int iIpIsOk = 0;

	public static int iKuJianType = 0;//0 MMi启动自检 1 库检台启动
	public static int iKuJianAccessWay = 1;//0 450M 1 LTE
	public static byte[] bArrTaxInfo = new byte[23];

	public static boolean bZiping = false;
	public static Calendar cStartCalendar; //记录开机的时间,格式与lStartServiceTime不一致,作用也不一致

	public static byte[] CurrentLongitude = new byte[5] ;
	public static byte[] CurrentLatitude = new byte[4] ;
}

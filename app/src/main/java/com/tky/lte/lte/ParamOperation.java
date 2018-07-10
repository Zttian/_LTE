package com.tky.lte.lte;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Calendar;

public class ParamOperation
{
	public static ParamOperation m_instance = null;

	public static ParamOperation getInstance()
	{
		if (m_instance == null)
		{
			m_instance = new ParamOperation();
		}
		return m_instance;
	}

	public TrainState mTrainState = null;

	private ParamOperation()
	{
		mTrainState = TrainState.getInstance();
	}



	public void Release()
	{
		LogInstance.error(GlobalPara.Tky,"44." + tag + ": ParamOperation release");
	}


	public static String tag = "ParamOperation";

	public String ReadSpecialField(String entry, String strDefault)
	{
		ConfigHelper aConfigHelper = ConfigHelper.getInstance();
		String strRet = aConfigHelper.getString(entry, strDefault);
		return strRet;
	}

	public void SaveSpecialField(String entry, String value)
	{
		ConfigHelper aConfigHelper = ConfigHelper.getInstance();
		if(!aConfigHelper.putString(entry, value))
		{
			//	LogInstance.debug(GlobalPara.Tky, "ConfigHelper putString error, be attention");
		}
		//放入缓存,用于写备份文件
		GlobalPara.htParameters.put(entry, value);//添加或者修改都是函数
	}

	byte[] buffer_Copy = new byte[1024] ;

	public void Copy(String srcPath, String destPath)
	{
		try
		{
			LogInstance.debug(GlobalPara.Tky, "copy from " + srcPath + " to " + destPath);
			String tmpPath = null;
			if(destPath.contains("mnt"))
			{
				tmpPath = "/mnt/sdcard/LocomotiveMain/";
			}
			else
			{
				tmpPath = "/LocomotiveMain/";
			}
			File aFile = new File(tmpPath);
			if(!aFile.exists())
			{
				aFile.mkdir();
				LogInstance.debug(GlobalPara.Tky, "create new directory:"+aFile.getAbsolutePath());
			}
			aFile = null;
			aFile = new File(destPath);
			if(!aFile.exists())
			{
				aFile.createNewFile();
				LogInstance.debug(GlobalPara.Tky, "create new file:"+aFile.getAbsolutePath());
			}
			aFile = null;

			int byteread = 0;
			File oldfile = new File(srcPath);
			if (oldfile.exists())
			{
				InputStream inStream = new FileInputStream(srcPath);
				FileOutputStream fs = new FileOutputStream(destPath);

				while ((byteread = inStream.read(buffer_Copy)) != -1)
				{
					fs.write(buffer_Copy, 0, byteread);
				}
				fs.flush();
				inStream.close();
				fs.close();
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}

	}

	public Boolean g_BackupFileFlag = false;
	public int iSecondsPassed = 0;

	public Boolean CheckParamFile()
	{
		try
		{
			// 初始化模式
			String strWorkMode = "";
			strWorkMode = ReadSpecialField("WorkMode", "");
			if (strWorkMode.equals(""))
			{//
				strWorkMode = GlobalPara.htParameters.get("WorkMode");
				if(strWorkMode == null || strWorkMode.equals(""))
				{
					strWorkMode = String.format("%d", 0x66);
				}
			}
			mTrainState.g_Mode = (byte) Integer.parseInt(strWorkMode);
			SaveSpecialField("WorkMode", strWorkMode);
			LogInstance.debug(GlobalPara.Tky, "mTrainState.g_Mode="+ Integer.toHexString(mTrainState.g_Mode));

			// 初始化线路名
			String g_Line_Name = "";
			g_Line_Name = ReadSpecialField("LineName", "");
			if (g_Line_Name.equals(""))
			{
				g_Line_Name = GlobalPara.htParameters.get("LineName");
				if(g_Line_Name == null || g_Line_Name.equals(""))
				{
					g_Line_Name = "朔黄线";//
				}
			}
			byte[] linenametemp = g_Line_Name.getBytes("GBK");
			g_Line_Name = new String(linenametemp, 0, linenametemp.length, "GBK");
			mTrainState.g_Line_Name = g_Line_Name;
			SaveSpecialField("LineName", g_Line_Name);
			LogInstance.debug(GlobalPara.Tky,  "mTrainState.g_Line_Name="+mTrainState.g_Line_Name);

			// 初始化模式标志
			String strModeFlag = "";
			strModeFlag = ReadSpecialField("ModeFlag", "");
			if (strModeFlag.equals(""))
			{
				strModeFlag = GlobalPara.htParameters.get("ModeFlag");
				if(strModeFlag == null || strModeFlag.equals(""))
				{
					strModeFlag = String.format("%c", (byte) '0');// 原为0自动模式，现改为1固定模式不变
				}
			}
			mTrainState.g_Mode_Flag = (byte) strModeFlag.charAt(0);
			SaveSpecialField("ModeFlag", strModeFlag);
			LogInstance.debug(GlobalPara.Tky,  "mTrainState.g_Mode_Flag="+ Integer.toHexString(mTrainState.g_Mode_Flag));

			String stringtemp = "";
			stringtemp = ReadSpecialField("ABJie", "");
			if (stringtemp.equals(""))
			{//
				stringtemp = GlobalPara.htParameters.get("ABJie");
				if(stringtemp == null || stringtemp.equals(""))
				{
					stringtemp = "1";//
				}
			}
			mTrainState.g_bABJie =(byte) ((byte) stringtemp.charAt(0)-'0');
			SaveSpecialField("ABJie", stringtemp);
			LogInstance.debug(GlobalPara.Tky, "mTrainState.g_bABJie="+stringtemp);

			stringtemp = "";
			stringtemp = ReadSpecialField("JueSe", "");
			if (stringtemp.equals(""))
			{//
				stringtemp = GlobalPara.htParameters.get("JueSe");
				if(stringtemp == null || stringtemp.equals(""))
				{
					stringtemp = "1";//
				}
			}
			mTrainState.g_bJueSe =(byte) ((byte) stringtemp.charAt(0)-'0');
			SaveSpecialField("JueSe", stringtemp);
			//LogInstance.debug(GlobalPara.Tky, "mTrainState.g_bJueSe="+stringtemp);

			stringtemp = "";
			stringtemp = ReadSpecialField("Location", "");
			if (stringtemp.equals(""))
			{//
				stringtemp = GlobalPara.htParameters.get("Location");
				if(stringtemp == null || stringtemp.equals(""))
				{
					stringtemp = "0";//
				}
			}
			mTrainState.g_bLocation =(byte) ((byte) stringtemp.charAt(0)-'0');
			SaveSpecialField("Location", stringtemp);
			//LogInstance.debug(GlobalPara.Tky, "mTrainState.g_bLocation="+stringtemp);

			// 上一次关机时的车次号
			String g_LastTrainNumber = "";
			g_LastTrainNumber = ReadSpecialField("TrainNumber", "");
			if (g_LastTrainNumber.equals("") /*|| ((mTrainState.g_Mode >= (byte) 0x01) && (mTrainState.g_Mode <= (byte) 0x64))*/)// 450M模式
			{
				g_LastTrainNumber = GlobalPara.htParameters.get("TrainNumber");
				if(g_LastTrainNumber == null  || g_LastTrainNumber.equals(""))
				{
					g_LastTrainNumber = "XXXXXXX";//
				}
			}
			mTrainState.g_LastTrainNumber = g_LastTrainNumber;
			LogInstance.debug(GlobalPara.Tky, "mTrainState.g_LastTrainNumber="+mTrainState.g_LastTrainNumber);

			// 上一次关机时的车次号注册状态
			String g_LastRegisterStatus = "";
			g_LastRegisterStatus = ReadSpecialField("RegisterStatus", "");
			if (g_LastRegisterStatus.equals(""))
			{
				g_LastRegisterStatus = GlobalPara.htParameters.get("RegisterStatus");
				if(g_LastRegisterStatus == null  || g_LastRegisterStatus.equals(""))
				{
					g_LastRegisterStatus = "0";//
				}
			}
			mTrainState.g_LastRegisterStatus = g_LastRegisterStatus;
			LogInstance.debug(GlobalPara.Tky, "mTrainState.g_LastRegisterStatus="+mTrainState.g_LastRegisterStatus);

			// 上一次关机时的 本补状态
			String g_LastBenBuStatus = "";
			g_LastBenBuStatus = ReadSpecialField("BenBuStatus", "");
			if (g_LastBenBuStatus.equals("")/* || ((mTrainState.g_Mode >= (byte) 0x01) && (mTrainState.g_Mode <= (byte) 0x64))*/)
			{
				g_LastBenBuStatus = GlobalPara.htParameters.get("BenBuStatus");
				if(g_LastBenBuStatus == null  || g_LastBenBuStatus.equals(""))
				{
					g_LastBenBuStatus = "0";//
				}
			}
			mTrainState.g_LastBenBuStatus = g_LastBenBuStatus;
			LogInstance.debug(GlobalPara.Tky,  "mTrainState.g_LastBenBuStatus="+mTrainState.g_LastBenBuStatus);

			// 初始化机车号设置方式标志
			stringtemp = "";
			stringtemp = ReadSpecialField("ENFlag", "");
			if (stringtemp.equals(""))
			{
				stringtemp = GlobalPara.htParameters.get("ENFlag");
				if(stringtemp == null || stringtemp.equals(""))
				{
					stringtemp = "1";//
				}
			}
			mTrainState.g_EngineNumber.Manual_Flag = (byte)((byte) stringtemp.charAt(0)-'0');
			SaveSpecialField("ENFlag", stringtemp);
			//LogInstance.debug(GlobalPara.Tky, "mTrainState.g_EngineNumber.Manual_Flag="+Integer.toHexString(mTrainState.g_EngineNumber.Manual_Flag));

			// 初始化机车号
			String strEngineNumber = "";
			strEngineNumber = ReadSpecialField("EngineNumber", "");
			if (strEngineNumber.equals(""))
			{
				strEngineNumber = GlobalPara.htParameters.get("EngineNumber");
				if(strEngineNumber == null || strEngineNumber.equals(""))
				{
					strEngineNumber = "XXXXXXXX";//
					LogInstance.error(GlobalPara.Tky, "6." + tag + ": can't read EngineNumber value, set a default value in "+ LogParam.LOG_FILEPATH);
				}
			}
			if(strEngineNumber.length() >= 8)
			{
				for (int i = 0; i < 8; i++)
				{
					mTrainState.g_EngineNumber.Number[i] = (byte) strEngineNumber.charAt(i);
				}
			}
			SaveSpecialField("EngineNumber", strEngineNumber);
			if(strEngineNumber.equals("XXXXXXXX"))
			{
				LogInstance.debug(GlobalPara.Tky, "mTrainState.g_EngineNumber="+strEngineNumber);
			}
			else
			{
				LogInstance.debug(GlobalPara.Tky, "mTrainState.g_EngineNumber="+strEngineNumber
						+", mTrainState.g_EngineNumber.Number="
						+(mTrainState.g_EngineNumber.Number[0]-'0')+(mTrainState.g_EngineNumber.Number[1]-'0')+(mTrainState.g_EngineNumber.Number[2]-'0')+(mTrainState.g_EngineNumber.Number[3]-'0')
						+(mTrainState.g_EngineNumber.Number[4]-'0')+(mTrainState.g_EngineNumber.Number[5]-'0')+(mTrainState.g_EngineNumber.Number[6]-'0')+(mTrainState.g_EngineNumber.Number[7]-'0')
				);
			}

			//设置"机车号1"
			if (!GlobalFunc.MemCmp(mTrainState.g_EngineNumber.Number, "XXXXXXXX", 8))
			{
				GlobalPara.strLoginUsrname = "";
				if(GlobalPara.iZhuKongFlag == 0x01)
				{
					if(mTrainState.g_bABJie == 0x01)
						GlobalPara.strLoginUsrname = strEngineNumber+"01";
					else
						GlobalPara.strLoginUsrname = strEngineNumber+"51";
				}
				else
				{
					if(mTrainState.g_bABJie == 0x01)
						GlobalPara.strLoginUsrname = strEngineNumber+"02";
					else
						GlobalPara.strLoginUsrname = strEngineNumber+"52";
				}
			}
			LogInstance.debug(GlobalPara.Tky, "GlobalPara.strLoginUsrname="+GlobalPara.strLoginUsrname);

			mTrainState.g_TrainNumber.Manual_Flag = (byte) 0;
			mTrainState.g_TrainNumber.Status = (byte) 0;
			mTrainState.m_BenBu_Status = (byte) 0;
			String tmpString = "XXXXXXX   ";
			for (int j = 0; j < 10; j++)
			{
				mTrainState.g_TrainNumber.Number[j] = (byte) tmpString.charAt(j);//改车次号//开机填入xxxxxxx
			}

			mTrainState.g_DisplayTrainNumber.Manual_Flag = 0;
			mTrainState.g_DisplayTrainNumber.Status = 0;
			for (int j = 0; j < 10; j++)
			{
				mTrainState.g_DisplayTrainNumber.Number[j] = (byte) tmpString.charAt(j);
			}

			// 初始化库检设备IP地址1
			String g_CheckIP1 = "";
			g_CheckIP1 = ReadSpecialField("CHECKIP1", "");
			if (g_CheckIP1.equals(""))
			{
				g_CheckIP1 = GlobalPara.htParameters.get("CHECKIP1");
				if(g_CheckIP1 == null || g_CheckIP1.equals(""))
				{
					LogInstance.error(GlobalPara.Tky, "198." + tag + ": can't read g_CheckIP1 value, set a default value in "+ LogParam.LOG_FILEPATH);
					g_CheckIP1 = "0.0.0.0";//
				}
				else
				{

				}
			}
			mTrainState.g_CheckIP1 = g_CheckIP1;
			SaveSpecialField("CHECKIP1", g_CheckIP1);
			int length;
			String IP_Temp;
			IP_Temp = "";
			length = g_CheckIP1.length();
			for (int i = 0, j = 0; i < length; i++)
			{
				if (g_CheckIP1.charAt(i) == '.')
				{
					mTrainState.m_CheckIP1[j] = (byte) Integer.parseInt(IP_Temp);
					j++;
					IP_Temp = "";
				}
				else
				{
					IP_Temp += g_CheckIP1.charAt(i);
				}
			}
			mTrainState.m_CheckIP1[3] = (byte) Integer.parseInt(IP_Temp);
			//LogInstance.debug(GlobalPara.Tky, "mTrainState.g_CheckIP1="+g_CheckIP1);

			// 初始化库检设备IP地址2
			String g_CheckIP2 = "";
			g_CheckIP2 = ReadSpecialField("CHECKIP2", "");
			if (g_CheckIP2.equals(""))
			{
				g_CheckIP2 = GlobalPara.htParameters.get("CHECKIP2");
				if(g_CheckIP2 == null || g_CheckIP2.equals(""))
				{
					LogInstance.error(GlobalPara.Tky, "198." + tag + ": can't read g_CheckIP2 value, set a default value in "+ LogParam.LOG_FILEPATH);
					g_CheckIP2 = "0.0.0.0";//
				}
				else
				{

				}
			}
			mTrainState.g_CheckIP2 = g_CheckIP2;
			SaveSpecialField("CHECKIP2", g_CheckIP2);
			IP_Temp = "";
			length = g_CheckIP2.length();
			for (int i = 0, j = 0; i < length; i++)
			{
				if (g_CheckIP2.charAt(i) == '.')
				{
					mTrainState.m_CheckIP2[j] = (byte) Integer.parseInt(IP_Temp);
					j++;
					IP_Temp = "";
				}
				else
				{
					IP_Temp += g_CheckIP2.charAt(i);
				}
			}
			mTrainState.m_CheckIP2[3] = (byte) Integer.parseInt(IP_Temp);
			//LogInstance.debug(GlobalPara.Tky, "mTrainState.g_CheckIP2="+g_CheckIP2);

			// 初始化库检设备IP地址3
			String g_CheckIP3 = "";
			g_CheckIP3 = ReadSpecialField("CHECKIP3", "");
			if (g_CheckIP3.equals(""))
			{
				g_CheckIP3 = GlobalPara.htParameters.get("CHECKIP3");
				if(g_CheckIP3 == null || g_CheckIP3.equals(""))
				{
					LogInstance.error(GlobalPara.Tky, "198." + tag + ": can't read g_CheckIP3 value, set a default value in "+ LogParam.LOG_FILEPATH);
					g_CheckIP3 = "192.10.10.105";//192.10.10.137
				}
				else
				{

				}
			}
			mTrainState.g_CheckIP3 = g_CheckIP3;
			SaveSpecialField("CHECKIP3", g_CheckIP3);
			IP_Temp = "";
			length = g_CheckIP3.length();
			for (int i = 0, j = 0; i < length; i++)
			{
				if (g_CheckIP3.charAt(i) == '.')
				{
					mTrainState.m_CheckIP3[j] = (byte) Integer.parseInt(IP_Temp);
					j++;
					IP_Temp = "";
				}
				else
				{
					IP_Temp += g_CheckIP3.charAt(i);
				}
			}
			mTrainState.m_CheckIP3[3] = (byte) Integer.parseInt(IP_Temp);
			//LogInstance.debug(GlobalPara.Tky, "mTrainState.g_CheckIP3="+g_CheckIP3);

			// 初始化库检设备电话号码1
			String g_CheckPhoneNumber1 = ("");
			g_CheckPhoneNumber1 = ReadSpecialField(("CHECKPHONENUMBER1"), "");
			if (g_CheckPhoneNumber1.equals(""))
			{
				g_CheckPhoneNumber1 = GlobalPara.htParameters.get("CHECKPHONENUMBER1");
				if(g_CheckPhoneNumber1 == null || g_CheckPhoneNumber1.equals(""))
				{
					LogInstance.error(GlobalPara.Tky, "199." + tag + ": can't read CHECKPHONENUMBER1 value, set a default value in "+ LogParam.LOG_FILEPATH);
					g_CheckPhoneNumber1 = "14689022000";//
				}
				else
				{

				}
			}
			mTrainState.g_CheckPhoneNumber1 = g_CheckPhoneNumber1;
			SaveSpecialField("CHECKPHONENUMBER1", g_CheckPhoneNumber1);
			//LogInstance.debug(GlobalPara.Tky, "mTrainState.CHECKPHONENUMBER1=" + mTrainState.g_CheckPhoneNumber1);

			// 初始化库检设备电话号码2
			String g_CheckPhoneNumber2 = ("");
			g_CheckPhoneNumber2 = ReadSpecialField(("CHECKPHONENUMBER2"), "");
			if (g_CheckPhoneNumber2.equals(""))
			{
				g_CheckPhoneNumber2 = GlobalPara.htParameters.get("CHECKPHONENUMBER2");
				if(g_CheckPhoneNumber2 == null || g_CheckPhoneNumber2.equals(""))
				{
					LogInstance.error(GlobalPara.Tky, "199." + tag + ": can't read CHECKPHONENUMBER2 value, set a default value in "+ LogParam.LOG_FILEPATH);
					g_CheckPhoneNumber2 = "14689022000";//
				}
				else
				{

				}
			}
			mTrainState.g_CheckPhoneNumber2 = g_CheckPhoneNumber2;
			SaveSpecialField("CHECKPHONENUMBER2", g_CheckPhoneNumber2);
			//LogInstance.debug(GlobalPara.Tky, "mTrainState.CHECKPHONENUMBER2=" + mTrainState.g_CheckPhoneNumber2);

			// 初始化库检设备电话号码3
			String g_CheckPhoneNumber3 = ("");
			g_CheckPhoneNumber3 = ReadSpecialField(("CHECKPHONENUMBER3"), "");
			if (g_CheckPhoneNumber3.equals(""))
			{
				g_CheckPhoneNumber3 = GlobalPara.htParameters.get("CHECKPHONENUMBER3");
				if(g_CheckPhoneNumber3 == null || g_CheckPhoneNumber3.equals(""))
				{
					LogInstance.error(GlobalPara.Tky, "199." + tag + ": can't read CHECKPHONENUMBER3 value, set a default value in "+ LogParam.LOG_FILEPATH);
					g_CheckPhoneNumber3 = "14689022000";//
				}
				else
				{

				}
			}
			mTrainState.g_CheckPhoneNumber3 = g_CheckPhoneNumber3;
			SaveSpecialField("CHECKPHONENUMBER3", g_CheckPhoneNumber3);
			//LogInstance.debug(GlobalPara.Tky, "mTrainState.CHECKPHONENUMBER3="+ mTrainState.g_CheckPhoneNumber3);

			// 初始化APN
			String g_APN = ("");
			g_APN = ReadSpecialField(("APN"), "");
			if (g_APN.equals(""))
			{
				g_APN = GlobalPara.htParameters.get("APN");
				if(g_APN == null || g_APN.equals(""))
				{
					g_APN = "cir.pgw";//
				}
			}
			mTrainState.g_APN = g_APN;
			SaveSpecialField("APN", g_APN);
			LogInstance.debug(GlobalPara.Tky, "mTrainState.g_APN="+mTrainState.g_APN);

			// 初始化归属DMIS IP地址(归属GRIS地址)
			String DMISIPAddress = ("");
			DMISIPAddress = ReadSpecialField(("DMISIPAddress"), "");
			if (DMISIPAddress.equals(""))
			{
				DMISIPAddress = GlobalPara.htParameters.get("DMISIPAddress");
				if(DMISIPAddress == null || DMISIPAddress.equals(""))
				{
					DMISIPAddress = "10.51.10.81";//
				}
			}
			SaveSpecialField("DMISIPAddress", DMISIPAddress);
			IP_Temp = ("");
			length = DMISIPAddress.length();
			for (int i = 0, j = 0; i < length; i++)
			{
				if (DMISIPAddress.charAt(i) == '.')
				{
					mTrainState.m_DMISAddre[j] = (byte) Integer.parseInt(IP_Temp);
					j++;
					IP_Temp = ("");
				}
				else
				{
					IP_Temp += DMISIPAddress.charAt(i);
				}
			}
			mTrainState.m_DMISAddre[3] = (byte) Integer.parseInt(IP_Temp);
			LogInstance.debug(GlobalPara.Tky,  "mTrainState.m_DMISAddre="+IP_Temp);

			mTrainState.m_HomeDMISAddre[0] = mTrainState.m_DMISAddre[0] ;
			mTrainState.m_HomeDMISAddre[1] = mTrainState.m_DMISAddre[1] ;
			mTrainState.m_HomeDMISAddre[2] = mTrainState.m_DMISAddre[2] ;
			mTrainState.m_HomeDMISAddre[3] = mTrainState.m_DMISAddre[3] ;

			// 初始化主用GROS地址
			String GROSIPAddress = ("");
			GROSIPAddress = ReadSpecialField(("GROSIPAddress"), "");
			if (GROSIPAddress.equals(""))
			{
				GROSIPAddress = GlobalPara.htParameters.get("GROSIPAddress");
				if(GROSIPAddress == null || GROSIPAddress.equals(""))
				{
					GROSIPAddress = "10.51.10.81";//
				}
			}
			mTrainState.strGrosAddre = GROSIPAddress;
			IP_Temp = ("");
			length = GROSIPAddress.length();
			for (int i = 0, j = 0; i < length; i++)
			{
				if (GROSIPAddress.charAt(i) == '.')
				{
					mTrainState.m_GROSAddre[j] = (byte) Integer.parseInt(IP_Temp);
					j++;
					IP_Temp = ("");
				}
				else
				{
					IP_Temp += GROSIPAddress.charAt(i);
				}
			}
			mTrainState.m_GROSAddre[3] = (byte) Integer.parseInt(IP_Temp);
			//LogInstance.debug(GlobalPara.Tky,  "mTrainState.m_GROSAddre="+IP_Temp);

			// 初始化备用GROS地址
			String GROSIPAddress1 = ("");
			GROSIPAddress1 = ReadSpecialField(("GROSIPAddress1"), "");
			if (GROSIPAddress1.equals(""))
			{
				GROSIPAddress1 = GlobalPara.htParameters.get("GROSIPAddress1");
				if(GROSIPAddress1 == null || GROSIPAddress1.equals(""))
				{
					GROSIPAddress1 = "10.51.10.81";//
				}
			}
			IP_Temp = ("");
			length = GROSIPAddress1.length();
			for (int i = 0, j = 0; i < length; i++)
			{
				if (GROSIPAddress1.charAt(i) == '.')
				{
					mTrainState.m_GROSAddre1[j] = (byte) Integer.parseInt(IP_Temp);
					j++;
					IP_Temp = ("");
				}
				else
				{
					IP_Temp += GROSIPAddress1.charAt(i);
				}
			}
			mTrainState.m_GROSAddre1[3] = (byte) Integer.parseInt(IP_Temp);
			//LogInstance.debug(GlobalPara.Tky,  "mTrainState.m_GROSAddre1="+IP_Temp);

			// sip服务器用户名，密码，SIP,VOP
			String UserName = ("");
			UserName = ReadSpecialField(("UserName"), "");
			if (UserName.equals(""))
			{
				UserName = GlobalPara.htParameters.get("UserName");
				if(UserName == null || UserName.equals(""))
				{
					UserName = "14689099001";//
				}
			}
			mTrainState.mLTE_UserName = UserName;
			SaveSpecialField("UserName", UserName);
			LogInstance.debug(GlobalPara.Tky,  "mTrainState.mLTE_UserName="+mTrainState.mLTE_UserName);

			String PassWord = ("");
			PassWord = ReadSpecialField(("PassWord"), "");
			if (PassWord.equals(""))
			{
				PassWord = GlobalPara.htParameters.get("PassWord");
				if(PassWord == null || PassWord.equals(""))
				{
					PassWord = "123456";//
				}
			}
			mTrainState.mLTE_PassWord = PassWord;
			SaveSpecialField("PassWord", PassWord);
			LogInstance.debug(GlobalPara.Tky,  "mTrainState.mLTE_PassWord="+mTrainState.mLTE_PassWord);

			UserName = ("");
			UserName = ReadSpecialField(("UserName2"), "");
			if (UserName.equals(""))
			{
				UserName = GlobalPara.htParameters.get("UserName2");
				if(UserName == null || UserName.equals(""))
				{
					UserName = "14689099002";//
				}
			}
			mTrainState.mLTE_UserName2 = UserName;
			SaveSpecialField("UserName2", UserName);
			LogInstance.debug(GlobalPara.Tky,  "mTrainState.mLTE_UserName2="+mTrainState.mLTE_UserName2);

			PassWord = ("");
			PassWord = ReadSpecialField(("PassWord2"), "");
			if (PassWord.equals(""))
			{
				PassWord = GlobalPara.htParameters.get("PassWord2");
				if(PassWord == null || PassWord.equals(""))
				{
					PassWord = "123456";//
				}
			}
			mTrainState.mLTE_PassWord2 = PassWord;
			SaveSpecialField("PassWord2", PassWord);
			LogInstance.debug(GlobalPara.Tky, "mTrainState.mLTE_PassWord2="+mTrainState.mLTE_PassWord2);


			if(GlobalPara.iZhuKongFlag != (byte)0x01)//阅后即焚//需要注释掉
			{//
				String tmpUserName = "";// 用户名调换
				tmpUserName = mTrainState.mLTE_UserName;
				mTrainState.mLTE_UserName = mTrainState.mLTE_UserName2;// 第二个用户名
				mTrainState.mLTE_UserName2 = tmpUserName;
				tmpUserName = mTrainState.mLTE_PassWord;// 密码调换
				mTrainState.mLTE_PassWord = mTrainState.mLTE_PassWord2;// 第二个密码
				mTrainState.mLTE_PassWord2 = tmpUserName;
			}

			String Sip_Ip = ("");
			Sip_Ip = ReadSpecialField(("Sip_Ip"), "");
			if (Sip_Ip.equals(""))
			{
				Sip_Ip = GlobalPara.htParameters.get("Sip_Ip");
				if(Sip_Ip == null  || Sip_Ip.equals(""))
				{
					Sip_Ip = "10.51.10.2";//
				}
			}
			mTrainState.mLTE_SIP_IP = Sip_Ip;
			SaveSpecialField("Sip_Ip", Sip_Ip);
			LogInstance.debug(GlobalPara.Tky,  "mTrainState.mLTE_SIP_IP="+mTrainState.mLTE_SIP_IP);

			Sip_Ip = ("");
			Sip_Ip = ReadSpecialField(("Sip_Ip2"), "");
			if (Sip_Ip.equals(""))
			{
				Sip_Ip = GlobalPara.htParameters.get("Sip_Ip2");
				if(Sip_Ip == null  || Sip_Ip.equals(""))
				{
					Sip_Ip = "10.51.10.12";//
				}
			}
			mTrainState.mLTE_SIP_IP2 = Sip_Ip;
			SaveSpecialField("Sip_Ip2", Sip_Ip);
			LogInstance.debug(GlobalPara.Tky,  "mTrainState.mLTE_SIP_IP2="+mTrainState.mLTE_SIP_IP2);


			mTrainState.g_DebugLog_Lev1 = 1;

			mTrainState.g_DebugLog_Lev2 = 0;

			mTrainState.g_DebugLog_Lev3 = 0;

			mTrainState.g_DebugLog_Lev4 = 0;


			String g_UserSetting = "";
			g_UserSetting = ReadSpecialField("UserSetting", "");
			if (g_UserSetting.equals(""))
			{
				g_UserSetting = GlobalPara.htParameters.get("UserSetting");
				if(g_UserSetting == null  || g_UserSetting.equals(""))
				{
					g_UserSetting = "3";//
				}
			}
			mTrainState.g_UserSetting_Myself = (byte)((byte) g_UserSetting.charAt(0)-'0');
			SaveSpecialField("UserSetting", g_UserSetting);
			//LogInstance.debug(GlobalPara.Tky, "mTrainState.g_UserSetting_Myself="+mTrainState.g_UserSetting_Myself);

			stringtemp = "";
			stringtemp = ReadSpecialField("DthFlag", "");
			if (stringtemp.equals(""))
			{//
				stringtemp = GlobalPara.htParameters.get("DthFlag");
				if(stringtemp == null || stringtemp.equals(""))
				{
					stringtemp = "1";//
				}
			}
			mTrainState.g_DthFlag = (byte)((byte) stringtemp.charAt(0)-'0');
			SaveSpecialField("DthFlag", stringtemp);
			//LogInstance.debug(GlobalPara.Tky, "mTrainState.g_DthFlag="+mTrainState.g_DthFlag);


			String strMainEngineNumber = "XXXXXXXX";
			if(strMainEngineNumber.length() >= 8)
			{
				for (int i = 0; i < 8; i++)
				{
					mTrainState.g_MainEngineNumber.Number[i] = (byte) strMainEngineNumber.charAt(i);
				}
			}
			//SaveSpecialField("MainEngineNumber", strMainEngineNumber);


			// 存储装置号
			String strLieWeiNumber = "";
			strLieWeiNumber = ReadSpecialField("LieWeiNumber", "");
			if (strLieWeiNumber.equals(""))
			{
				strLieWeiNumber = GlobalPara.htParameters.get("LieWeiNumber");
				if(strLieWeiNumber == null || strLieWeiNumber.equals(""))
				{
					strLieWeiNumber = "000000";//
				}
			}
			if(strLieWeiNumber.length() == 6)
			{
				GlobalPara.arrDteZhuangZhi[0] = (byte)(((strLieWeiNumber.charAt(0)-'0') << 4) + (strLieWeiNumber.charAt(1)-'0'));
				GlobalPara.arrDteZhuangZhi[1] = (byte)(((strLieWeiNumber.charAt(2)-'0') << 4) + (strLieWeiNumber.charAt(3)-'0'));
				GlobalPara.arrDteZhuangZhi[2] = (byte)(((strLieWeiNumber.charAt(4)-'0') << 4) + (strLieWeiNumber.charAt(5)-'0'));
			}
			SaveSpecialField("LieWeiNumber", strLieWeiNumber);
			//LogInstance.debug(GlobalPara.Tky, "LieWeiNumber:"+strLieWeiNumber );

			// 初始化APN
			String g_BatteryVoltage = ("");
			g_BatteryVoltage = ReadSpecialField(("Battery"), "");
			if (g_BatteryVoltage.equals(""))
			{
				g_BatteryVoltage = GlobalPara.htParameters.get("Battery");
				if(g_BatteryVoltage == null ||  g_BatteryVoltage.equals(""))
				{
					g_BatteryVoltage = "00.0";//
				}
			}
			GlobalPara.strBatteryVoltage = g_BatteryVoltage;
			SaveSpecialField("Battery", g_BatteryVoltage);

            String g_AutoAnswer = ("");
            g_AutoAnswer = ReadSpecialField(("AutoAnswer"), "");
            if (g_AutoAnswer.equals(""))
            {
                g_AutoAnswer = GlobalPara.htParameters.get("AutoAnswer");
                if(g_AutoAnswer == null ||  g_AutoAnswer.equals(""))
                {
                    g_AutoAnswer = "0";//
                }
            }
            mTrainState.g_AutoAnswer   = g_AutoAnswer;
            SaveSpecialField("AutoAnswer", g_AutoAnswer);


            //写入备份文件
			ConfigHelper aConfigHelper = ConfigHelper.getInstance();
			aConfigHelper.SaveToBakfile();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
			return false;
		}
		return true;
	}

	public boolean HandleParamFile()
	{
		try
		{
			boolean blCheckfil = CheckParamFile();
			if (!blCheckfil)
			{
				LogInstance.error(GlobalPara.Tky, "46." + tag + ": ParamFile is not right");
				return false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
			return false;
		}

		return true;
	}

	public void SaveForModeChange_Auto(byte newMode,String newLine )
	{
		try
		{
			String tmpWorkmodechangeauto = "XXXX年XX月XX日XX时XX分XX秒,工作模式自动设置为XXH,                             DA";
			byte[] workmodechangeauto = null;
			try
			{
				workmodechangeauto = tmpWorkmodechangeauto.getBytes("GBK");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				if (e.getMessage() != null && e.getMessage() != "")
				{
					LogInstance.exception(GlobalPara.Tky, e);
				}
				LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
						+ Thread.currentThread().getStackTrace()[2].getLineNumber());
			}

			RandomAccessFile file = new RandomAccessFile((LogParam.strOtherRecordPath + "WorkModeChangeRecord.txt"), "rw");//"/LocomotiveMain/OtherRecord/WorkModeChangeRecord.txt"
			byte counter ;
			if (file.length() > 0)
			{
				file.seek(0);
				counter = file.readByte();
				if (counter >= GlobalPara.ElseRecordNumber)
					counter = 1;
				else
					counter++;
				file.seek(0);
				file.writeByte(counter);
			}
			else
			{
				counter = 1;
				file.seek(0);
				file.writeByte(counter);
			}

			Calendar aCalendar = Calendar.getInstance();
			int n_Year = aCalendar.get(Calendar.YEAR);
			int n_Month = aCalendar.get(Calendar.MONTH) + 1;
			int n_Day = aCalendar.get(Calendar.DAY_OF_MONTH);
			int n_Hour = aCalendar.get(Calendar.HOUR_OF_DAY);
			int n_Min = aCalendar.get(Calendar.MINUTE);
			int n_Sec = aCalendar.get(Calendar.SECOND);

			workmodechangeauto[0] = (byte) (((n_Year / 1000) % 10) + 0x30);
			workmodechangeauto[1] = (byte) (((n_Year / 100) % 10) + 0x30);
			workmodechangeauto[2] = (byte) (((n_Year / 10) % 10) + 0x30);
			workmodechangeauto[3] = (byte) ((n_Year% 10) + 0x30);
			workmodechangeauto[6] = (byte) (((n_Month / 10) % 10) + 0x30);
			workmodechangeauto[7] = (byte) ((n_Month % 10) + 0x30);
			workmodechangeauto[10] = (byte) (((n_Day / 10) % 10) + 0x30);
			workmodechangeauto[11] = (byte) ((n_Day % 10) + 0x30);
			workmodechangeauto[14] = (byte) (((n_Hour / 10) % 10) + 0x30);
			workmodechangeauto[15] = (byte) ((n_Hour % 10) + 0x30);
			workmodechangeauto[18] = (byte) (((n_Min / 10) % 10) + 0x30);
			workmodechangeauto[19] = (byte) ((n_Min % 10) + 0x30);
			workmodechangeauto[22] = (byte) (((n_Sec / 10) % 10) + 0x30);
			workmodechangeauto[23] = (byte) ((n_Sec % 10) + 0x30);
			if (((newMode >> 4) & 0x0f ) > 9)
				workmodechangeauto[45] = (byte) (((mTrainState.g_Mode >> 4) & 0x0f ) + 0x37);//显示为字符A~F
			else
				workmodechangeauto[45] = (byte) (((mTrainState.g_Mode >> 4) & 0x0f) + 0x30);
			if ((newMode & 0x0f) > 9)
				workmodechangeauto[46] = (byte) ((mTrainState.g_Mode & 0x0f) + 0x37);
			else
				workmodechangeauto[46] = (byte) ((mTrainState.g_Mode & 0x0f) + 0x30);

			byte[] tmp_g_Line_Name = newLine.getBytes("GBK");
			int i = 0;
			for (i=0; i < tmp_g_Line_Name.length; i++)
				workmodechangeauto[49 + i] = tmp_g_Line_Name[i] ;//
			for (; i <= 29; i++)
				workmodechangeauto[49 + i] = ' ' ;

			workmodechangeauto[78] = 0x0d;
			workmodechangeauto[79] = 0x0a;
			file.seek(1 + (counter - 1) * 80);
			file.write(workmodechangeauto, 0, 80);
			file.close();

			int iprintlen = workmodechangeauto.length;
			if(workmodechangeauto != null && workmodechangeauto.length > 2)
				iprintlen = workmodechangeauto.length - 2;

			String strLog = new String(workmodechangeauto, 0, iprintlen, "GBK");
			if(mTrainState.g_DebugLog_Lev1!=0x00)
				LogInstance.debug(GlobalPara.Tky, strLog);

			workmodechangeauto = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}

	}
	public void SaveForModeChange(_RawInfo PackageInfo)
	{
		try
		{
			String str_workmodechangemanual = "XXXX年XX月XX日XX时XX分XX秒,工作模式手动设置为XXH,                             DA";
			byte[] workmodechangemanual = null;
			try
			{
				workmodechangemanual = str_workmodechangemanual.getBytes("GBK");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				if (e.getMessage() != null && e.getMessage() != "")
				{
					LogInstance.exception(GlobalPara.Tky, e);
				}
				LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
						+ Thread.currentThread().getStackTrace()[2].getLineNumber());
			}

			RandomAccessFile file = new RandomAccessFile(LogParam.strOtherRecordPath + "WorkModeChangeRecord.txt", "rw");//"/LocomotiveMain/OtherRecord/WorkModeChangeRecord.txt"
			byte counter;
			int i;
			if (file.length() > 0)
			{
				file.seek(0);
				counter = file.readByte();
				if (counter >= GlobalPara.ElseRecordNumber)
					counter = 1;
				else
					counter++;
				file.seek(0);
				file.writeByte(counter);
			}
			else
			{
				counter = 1;
				file.seek(0);
				file.writeByte(counter);
			}
			Calendar aCalendar = Calendar.getInstance();
			int n_Year = aCalendar.get(Calendar.YEAR);
			int n_Month = aCalendar.get(Calendar.MONTH) + 1;
			int n_Day = aCalendar.get(Calendar.DAY_OF_MONTH);
			int n_Hour = aCalendar.get(Calendar.HOUR_OF_DAY);
			int n_Min = aCalendar.get(Calendar.MINUTE);
			int n_Sec = aCalendar.get(Calendar.SECOND);
			workmodechangemanual[0] = (byte) (((n_Year / 1000) % 10) + 0x30);
			workmodechangemanual[1] = (byte) (((n_Year / 100) % 10) + 0x30);
			workmodechangemanual[2] = (byte) (((n_Year / 10) % 10) + 0x30);
			workmodechangemanual[3] = (byte) ((n_Year % 10) + 0x30);
			workmodechangemanual[6] = (byte) (((n_Month / 10) % 10) + 0x30);
			workmodechangemanual[7] = (byte) ((n_Month % 10) + 0x30);
			workmodechangemanual[10] = (byte) (((n_Day / 10) % 10) + 0x30);
			workmodechangemanual[11] = (byte) ((n_Day % 10) + 0x30);
			workmodechangemanual[14] = (byte) (((n_Hour / 10) % 10) + 0x30);
			workmodechangemanual[15] = (byte) ((n_Hour % 10) + 0x30);
			workmodechangemanual[18] = (byte) (((n_Min / 10) % 10) + 0x30);
			workmodechangemanual[19] = (byte) ((n_Min % 10) + 0x30);
			workmodechangemanual[22] = (byte) (((n_Sec / 10) % 10) + 0x30);
			workmodechangemanual[23] = (byte) ((n_Sec % 10) + 0x30);
			if (((PackageInfo.Data[0] >> 4) & 0x0f ) > 9)
				workmodechangemanual[45] = (byte) (((PackageInfo.Data[0] >> 4) & 0x0f ) + 0x37);
			else
				workmodechangemanual[45] = (byte) (((PackageInfo.Data[0] >> 4) & 0x0f ) + 0x30);
			if ((PackageInfo.Data[0] & 0x0f) > 9)
				workmodechangemanual[46] = (byte) ((PackageInfo.Data[0] & 0x0f) + 0x37);
			else
				workmodechangemanual[46] = (byte) ((PackageInfo.Data[0] & 0x0f) + 0x30);
			for (i = 1; PackageInfo.Data[i] != ';'; i++)
			{
				workmodechangemanual[i + 48] = PackageInfo.Data[i] ;
			}
			for (; i <= 29; i++)
				workmodechangemanual[i + 48] = ' ';

			workmodechangemanual[78] = 0x0d;
			workmodechangemanual[79] = 0x0a;
			file.seek(1 + (counter - 1) * 80);
			file.write(workmodechangemanual, 0, 80);
			file.close();

			int iprintlen = workmodechangemanual.length;
			if(workmodechangemanual != null && workmodechangemanual.length > 2)
				iprintlen = workmodechangemanual.length - 2;

			String strLog = new String(workmodechangemanual, 0, iprintlen, "GBK");
			if(mTrainState.g_DebugLog_Lev1!=0x00)
				LogInstance.debug(GlobalPara.Tky, strLog);

			workmodechangemanual = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}

	}

	public void SaveForTrainNumberAuto(_RawInfo PackageInfo, TrainState mTrainState)
	{
		try
		{
			String str_recordtrainnumberauto = "XXXX年XX月XX日XX时XX分XX秒,车次号自动由XXXXXXX设置为XXXXXXX;DA";
			byte[] recordtrainnumberauto = null;
			try
			{
				recordtrainnumberauto = str_recordtrainnumberauto.getBytes("GBK");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				if (e.getMessage() != null && e.getMessage() != "")
				{
					LogInstance.exception(GlobalPara.Tky, e);
				}
				LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
						+ Thread.currentThread().getStackTrace()[2].getLineNumber());
			}

			// 记录车次号的变化情况
			RandomAccessFile file = new RandomAccessFile(LogParam.strOtherRecordPath + "WorkModeChangeRecord.txt", "rw");//"/LocomotiveMain/OtherRecord/WorkModeChangeRecord.txt"
			byte counter;
			int i;
			if (file.length() > 0)
			{
				file.seek(0);
				counter = file.readByte();
				if (counter >= GlobalPara.ElseRecordNumber)
					counter = 1;
				else
					counter++;
				file.seek(0);
				file.writeByte(counter);
			}
			else
			{
				counter = 1;
				file.seek(0);
				file.writeByte(counter);
			}
			Calendar aCalendar = Calendar.getInstance();
			int n_Year = aCalendar.get(Calendar.YEAR);
			int n_Month = aCalendar.get(Calendar.MONTH) + 1;
			int n_Day = aCalendar.get(Calendar.DAY_OF_MONTH);
			int n_Hour = aCalendar.get(Calendar.HOUR_OF_DAY);
			int n_Min = aCalendar.get(Calendar.MINUTE);
			int n_Sec = aCalendar.get(Calendar.SECOND);
			recordtrainnumberauto[0] = (byte) (((n_Year / 1000) % 10) + 0x30);
			recordtrainnumberauto[1] = (byte) (((n_Year / 100) % 10) + 0x30);
			recordtrainnumberauto[2] = (byte) (((n_Year / 10) % 10) + 0x30);
			recordtrainnumberauto[3] = (byte) ((n_Year % 10) + 0x30);
			recordtrainnumberauto[6] = (byte) (((n_Month / 10) % 10) + 0x30);
			recordtrainnumberauto[7] = (byte) ((n_Month % 10) + 0x30);
			recordtrainnumberauto[10] = (byte) (((n_Day / 10) % 10) + 0x30);
			recordtrainnumberauto[11] = (byte) ((n_Day % 10) + 0x30);
			recordtrainnumberauto[14] = (byte) (((n_Hour / 10) % 10) + 0x30);
			recordtrainnumberauto[15] = (byte) ((n_Hour % 10) + 0x30);
			recordtrainnumberauto[18] = (byte) (((n_Min / 10) % 10) + 0x30);
			recordtrainnumberauto[19] = (byte) ((n_Min % 10) + 0x30);
			recordtrainnumberauto[22] = (byte) (((n_Sec / 10) % 10) + 0x30);
			recordtrainnumberauto[23] = (byte) ((n_Sec % 10) + 0x30);

			for (i = 0; i < 7; i++)
				recordtrainnumberauto[39 + i] = mTrainState.g_TrainNumber.Number[i] ;
			for (i = 0; (i < 7) && (PackageInfo.Data[i] != ';'); i++)
				recordtrainnumberauto[52 + i] = PackageInfo.Data[i] ;
			for (; i < 7; i++)
				recordtrainnumberauto[52 + i] = ' ';
			recordtrainnumberauto[60] = 0x0d;
			recordtrainnumberauto[61] = 0x0a;

			file.seek(1 + (counter - 1) * 62);
			file.write(recordtrainnumberauto, 0, 62);
			file.close();

			int iprintlen = recordtrainnumberauto.length;
			if(recordtrainnumberauto != null && recordtrainnumberauto.length > 2)
				iprintlen = recordtrainnumberauto.length - 2;

			String strLog = new String(recordtrainnumberauto, 0, iprintlen, "GBK");
			if(mTrainState.g_DebugLog_Lev1!=0x00)
				LogInstance.debug(GlobalPara.Tky, strLog);

			recordtrainnumberauto = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}

	}

	public void SaveForEngineNumberAuto(_RawInfo PackageInfo, TrainState mTrainState)
	{
		try
		{
			String str_recordenginenumberauto = "XXXX年XX月XX日XX时XX分XX秒,机车号自动由XXXXXXXX设置为XXXXXXXX;DA";
			byte[] recordenginenumberauto = null;
			try
			{
				recordenginenumberauto = str_recordenginenumberauto.getBytes("GBK");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				if (e.getMessage() != null && e.getMessage() != "")
				{
					LogInstance.exception(GlobalPara.Tky, e);
				}
				LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
						+ Thread.currentThread().getStackTrace()[2].getLineNumber());
			}

			RandomAccessFile file = new RandomAccessFile(LogParam.strOtherRecordPath + "WorkModeChangeRecord.txt", "rw");//"/LocomotiveMain/OtherRecord/WorkModeChangeRecord.txt"
			byte counter;
			int i;
			if (file.length() > 0)
			{
				file.seek(0);
				counter = file.readByte();
				if (counter >= GlobalPara.ElseRecordNumber)
					counter = 1;
				else
					counter++;
				file.seek(0);
				file.writeByte(counter);
			}
			else
			{
				counter = 1;
				file.seek(0);
				file.writeByte(counter);
			}
			Calendar aCalendar = Calendar.getInstance();
			int n_Year = aCalendar.get(Calendar.YEAR);
			int n_Month = aCalendar.get(Calendar.MONTH) + 1;
			int n_Day = aCalendar.get(Calendar.DAY_OF_MONTH);
			int n_Hour = aCalendar.get(Calendar.HOUR_OF_DAY);
			int n_Min = aCalendar.get(Calendar.MINUTE);
			int n_Sec = aCalendar.get(Calendar.SECOND);
			recordenginenumberauto[0] = (byte) (((n_Year / 1000) % 10) + 0x30);
			recordenginenumberauto[1] = (byte) (((n_Year / 100) % 10) + 0x30);
			recordenginenumberauto[2] = (byte) (((n_Year / 10) % 10) + 0x30);
			recordenginenumberauto[3] = (byte) ((n_Year % 10) + 0x30);
			recordenginenumberauto[6] = (byte) (((n_Month / 10) % 10) + 0x30);
			recordenginenumberauto[7] = (byte) ((n_Month % 10) + 0x30);
			recordenginenumberauto[10] = (byte) (((n_Day / 10) % 10) + 0x30);
			recordenginenumberauto[11] = (byte) ((n_Day % 10) + 0x30);
			recordenginenumberauto[14] = (byte) (((n_Hour / 10) % 10) + 0x30);
			recordenginenumberauto[15] = (byte) ((n_Hour % 10) + 0x30);
			recordenginenumberauto[18] = (byte) (((n_Min / 10) % 10) + 0x30);
			recordenginenumberauto[19] = (byte) ((n_Min % 10) + 0x30);
			recordenginenumberauto[22] = (byte) (((n_Sec / 10) % 10) + 0x30);
			recordenginenumberauto[23] = (byte) ((n_Sec % 10) + 0x30);

			for (i = 0; i < 8; i++)
				recordenginenumberauto[39 + i] = mTrainState.g_EngineNumber.Number[i] ;
			for (i = 0; i < 8; i++)
				recordenginenumberauto[53 + i] = PackageInfo.Data[7 + i] ;
			recordenginenumberauto[62] = 0x0d;
			recordenginenumberauto[63] = 0x0a;

			file.seek(1 + (counter - 1) * 64);
			file.write(recordenginenumberauto, 0, 64);
			file.close();

			int iprintlen = recordenginenumberauto.length;
			if(recordenginenumberauto != null && recordenginenumberauto.length > 2)
				iprintlen = recordenginenumberauto.length - 2;

			String strLog = new String(recordenginenumberauto, 0, iprintlen, "GBK");
			if(mTrainState.g_DebugLog_Lev1!=0x00)
				LogInstance.debug(GlobalPara.Tky, strLog);

			recordenginenumberauto = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}

	}

	public void SaveForTrainNumberManual(_RawInfo PackageInfo, TrainState mTrainState, byte[] tempnumber)
	{
		try
		{
			String str_recordtrainnumbermanual = "XXXX年XX月XX日XX时XX分XX秒,车次号手动由XXXXXXX设置为XXXXXXX;DA";
			byte[] recordtrainnumbermanual = null;
			try
			{
				recordtrainnumbermanual = str_recordtrainnumbermanual.getBytes("GBK");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				if (e.getMessage() != null && e.getMessage() != "")
				{
					LogInstance.exception(GlobalPara.Tky, e);
				}
				LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
						+ Thread.currentThread().getStackTrace()[2].getLineNumber());
			}

			// 记录车次号的变化情况
			RandomAccessFile file = new RandomAccessFile(LogParam.strOtherRecordPath + "TrainNumberChangeRecord.txt", "rw");//"/LocomotiveMain/OtherRecord/TrainNumberChangeRecord.txt"
			byte counter;
			int i;
			if (file.length() > 0)
			{
				file.seek(0);
				counter = file.readByte();
				if (counter >= GlobalPara.ElseRecordNumber)
					counter = 1;
				else
					counter++;
				file.seek(0);
				file.writeByte(counter);
			}
			else
			{
				counter = 1;
				file.seek(0);
				file.writeByte(counter);
			}
			Calendar aCalendar = Calendar.getInstance();
			int n_Year = aCalendar.get(Calendar.YEAR);
			int n_Month = aCalendar.get(Calendar.MONTH) + 1;
			int n_Day = aCalendar.get(Calendar.DAY_OF_MONTH);
			int n_Hour = aCalendar.get(Calendar.HOUR_OF_DAY);
			int n_Min = aCalendar.get(Calendar.MINUTE);
			int n_Sec = aCalendar.get(Calendar.SECOND);
			recordtrainnumbermanual[0] = (byte) (((n_Year / 1000) % 10) + 0x30);
			recordtrainnumbermanual[1] = (byte) (((n_Year / 100) % 10) + 0x30);
			recordtrainnumbermanual[2] = (byte) (((n_Year / 10) % 10) + 0x30);
			recordtrainnumbermanual[3] = (byte) ((n_Year % 10) + 0x30);
			recordtrainnumbermanual[6] = (byte) (((n_Month / 10) % 10) + 0x30);
			recordtrainnumbermanual[7] = (byte) ((n_Month % 10) + 0x30);
			recordtrainnumbermanual[10] = (byte) (((n_Day / 10) % 10) + 0x30);
			recordtrainnumbermanual[11] = (byte) ((n_Day % 10) + 0x30);
			recordtrainnumbermanual[14] = (byte) (((n_Hour / 10) % 10) + 0x30);
			recordtrainnumbermanual[15] = (byte) ((n_Hour % 10) + 0x30);
			recordtrainnumbermanual[18] = (byte) (((n_Min / 10) % 10) + 0x30);
			recordtrainnumbermanual[19] = (byte) ((n_Min % 10) + 0x30);
			recordtrainnumbermanual[22] = (byte) (((n_Sec / 10) % 10) + 0x30);
			recordtrainnumbermanual[23] = (byte) ((n_Sec % 10) + 0x30);
			for (i = 0; i < 7; i++)
				recordtrainnumbermanual[39 + i] = mTrainState.g_TrainNumber.Number[i] ;
			for (i = 0; i < 7; i++)
				recordtrainnumbermanual[52 + i] = tempnumber[i] ;
			recordtrainnumbermanual[60] = 0x0d;
			recordtrainnumbermanual[61] = 0x0a;
			file.seek(1 + (counter - 1) * 62);
			file.write(recordtrainnumbermanual, 0, 62);
			file.close();

			int iprintlen = recordtrainnumbermanual.length;
			if(recordtrainnumbermanual != null && recordtrainnumbermanual.length > 2)
				iprintlen = recordtrainnumbermanual.length - 2;

			String strLog = new String(recordtrainnumbermanual, 0, iprintlen, "GBK");
			if(mTrainState.g_DebugLog_Lev1!=0x00)
				LogInstance.debug(GlobalPara.Tky, strLog);

			recordtrainnumbermanual = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}

	}

	public void SaveForEngineNumberManual(_RawInfo PackageInfo, TrainState mTrainState)
	{
		try
		{
			String str_recordenginenumbermanual = "XXXX年XX月XX日XX时XX分XX秒,机车号手动由XXXXXXXX设置为XXXXXXXX;DA";
			byte[] recordenginenumbermanual = null;
			try
			{
				recordenginenumbermanual = str_recordenginenumbermanual.getBytes("GBK");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				if (e.getMessage() != null && e.getMessage() != "")
				{
					LogInstance.exception(GlobalPara.Tky, e);
				}
				LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
						+ Thread.currentThread().getStackTrace()[2].getLineNumber());
			}

			RandomAccessFile file = new RandomAccessFile(LogParam.strOtherRecordPath + "EngineNumberChangeRecord.txt", "rw");//"/LocomotiveMain/OtherRecord/EngineNumberChangeRecord.txt"
			byte counter;
			int i;
			if (file.length() > 0)
			{
				file.seek(0);
				counter = file.readByte();
				if (counter >= GlobalPara.ElseRecordNumber)
					counter = 1;
				else
					counter++;
				file.seek(0);
				file.writeByte(counter);
			}
			else
			{
				counter = 1;
				file.seek(0);
				file.writeByte(counter);
			}
			Calendar aCalendar = Calendar.getInstance();
			int n_Year = aCalendar.get(Calendar.YEAR);
			int n_Month = aCalendar.get(Calendar.MONTH) + 1;
			int n_Day = aCalendar.get(Calendar.DAY_OF_MONTH);
			int n_Hour = aCalendar.get(Calendar.HOUR_OF_DAY);
			int n_Min = aCalendar.get(Calendar.MINUTE);
			int n_Sec = aCalendar.get(Calendar.SECOND);
			recordenginenumbermanual[0] = (byte) (((n_Year / 1000) % 10) + 0x30);
			recordenginenumbermanual[1] = (byte) (((n_Year / 100) % 10) + 0x30);
			recordenginenumbermanual[2] = (byte) (((n_Year / 10) % 10) + 0x30);
			recordenginenumbermanual[3] = (byte) ((n_Year % 10) + 0x30);
			recordenginenumbermanual[6] = (byte) (((n_Month / 10) % 10) + 0x30);
			recordenginenumbermanual[7] = (byte) ((n_Month % 10) + 0x30);
			recordenginenumbermanual[10] = (byte) (((n_Day / 10) % 10) + 0x30);
			recordenginenumbermanual[11] = (byte) ((n_Day % 10) + 0x30);
			recordenginenumbermanual[14] = (byte) (((n_Hour / 10) % 10) + 0x30);
			recordenginenumbermanual[15] = (byte) ((n_Hour % 10) + 0x30);
			recordenginenumbermanual[18] = (byte) (((n_Min / 10) % 10) + 0x30);
			recordenginenumbermanual[19] = (byte) ((n_Min % 10) + 0x30);
			recordenginenumbermanual[22] = (byte) (((n_Sec / 10) % 10) + 0x30);
			recordenginenumbermanual[23] = (byte) ((n_Sec % 10) + 0x30);

			for (i = 0; i < 8; i++)
				recordenginenumbermanual[39 + i] = mTrainState.g_EngineNumber.Number[i] ;
			for (i = 0; i < 8; i++)
				recordenginenumbermanual[53 + i] = PackageInfo.Data[i] ;
			recordenginenumbermanual[62] = 0x0d;
			recordenginenumbermanual[63] = 0x0a;

			file.seek(1 + (counter - 1) * 64);
			file.write(recordenginenumbermanual, 0, 64);
			file.close();

			int iprintlen = recordenginenumbermanual.length;
			if(recordenginenumbermanual != null && recordenginenumbermanual.length > 2)
				iprintlen = recordenginenumbermanual.length - 2;

			String strLog = new String(recordenginenumbermanual, 0, iprintlen, "GBK");
			if(mTrainState.g_DebugLog_Lev1!=0x00)
				LogInstance.debug(GlobalPara.Tky, strLog);

			recordenginenumbermanual = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}

	}

	public void SaveForMainEngineNumberManual(String strOldMainEngineNumber , TrainState mTrainState)
	{
		try
		{
			String str_recordenginenumbermanual = "XXXX年XX月XX日XX时XX分XX秒,主控机车号由XXXXXXXX设置为XXXXXXXX;DA";
			byte[] recordenginenumbermanual = null;
			try
			{
				recordenginenumbermanual = str_recordenginenumbermanual.getBytes("GBK");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				if (e.getMessage() != null && e.getMessage() != "")
				{
					LogInstance.exception(GlobalPara.Tky, e);
				}
				LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
						+ Thread.currentThread().getStackTrace()[2].getLineNumber());
			}

			RandomAccessFile file = new RandomAccessFile(LogParam.strOtherRecordPath + "EngineNumberChangeRecord.txt", "rw");//"/LocomotiveMain/OtherRecord/EngineNumberChangeRecord.txt"
			byte counter;
			int i;
			if (file.length() > 0)
			{
				file.seek(0);
				counter = file.readByte();
				if (counter >= GlobalPara.ElseRecordNumber)
					counter = 1;
				else
					counter++;
				file.seek(0);
				file.writeByte(counter);
			}
			else
			{
				counter = 1;
				file.seek(0);
				file.writeByte(counter);
			}
			Calendar aCalendar = Calendar.getInstance();
			int n_Year = aCalendar.get(Calendar.YEAR);
			int n_Month = aCalendar.get(Calendar.MONTH) + 1;
			int n_Day = aCalendar.get(Calendar.DAY_OF_MONTH);
			int n_Hour = aCalendar.get(Calendar.HOUR_OF_DAY);
			int n_Min = aCalendar.get(Calendar.MINUTE);
			int n_Sec = aCalendar.get(Calendar.SECOND);
			recordenginenumbermanual[0] = (byte) (((n_Year / 1000) % 10) + 0x30);
			recordenginenumbermanual[1] = (byte) (((n_Year / 100) % 10) + 0x30);
			recordenginenumbermanual[2] = (byte) (((n_Year / 10) % 10) + 0x30);
			recordenginenumbermanual[3] = (byte) ((n_Year % 10) + 0x30);
			recordenginenumbermanual[6] = (byte) (((n_Month / 10) % 10) + 0x30);
			recordenginenumbermanual[7] = (byte) ((n_Month % 10) + 0x30);
			recordenginenumbermanual[10] = (byte) (((n_Day / 10) % 10) + 0x30);
			recordenginenumbermanual[11] = (byte) ((n_Day % 10) + 0x30);
			recordenginenumbermanual[14] = (byte) (((n_Hour / 10) % 10) + 0x30);
			recordenginenumbermanual[15] = (byte) ((n_Hour % 10) + 0x30);
			recordenginenumbermanual[18] = (byte) (((n_Min / 10) % 10) + 0x30);
			recordenginenumbermanual[19] = (byte) ((n_Min % 10) + 0x30);
			recordenginenumbermanual[22] = (byte) (((n_Sec / 10) % 10) + 0x30);
			recordenginenumbermanual[23] = (byte) ((n_Sec % 10) + 0x30);

			byte[] arrOldMainEngineNumber = new byte[10] ;
			for( i = 0; i < strOldMainEngineNumber.length();i++)
				arrOldMainEngineNumber[i] = (byte)strOldMainEngineNumber.charAt(i);
			while(i<8)
			{
				arrOldMainEngineNumber[i] = ' ';
				i++;
			}

			for (i = 0; i < 8; i++)
				recordenginenumbermanual[39 + i] = arrOldMainEngineNumber[i] ;

			for (i = 0; i < 8; i++)
				recordenginenumbermanual[53 + i] = mTrainState.g_MainEngineNumber.Number[i] ;
			recordenginenumbermanual[62] = 0x0d;
			recordenginenumbermanual[63] = 0x0a;

			file.seek(1 + (counter - 1) * 64);
			file.write(recordenginenumbermanual, 0, 64);
			file.close();

			int iprintlen = recordenginenumbermanual.length;
			if(recordenginenumbermanual != null && recordenginenumbermanual.length > 2)
				iprintlen = recordenginenumbermanual.length - 2;

			String strLog = new String(recordenginenumbermanual, 0, iprintlen, "GBK");
			if(mTrainState.g_DebugLog_Lev1!=0x00)
				LogInstance.debug(GlobalPara.Tky, strLog);

			recordenginenumbermanual = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}

	}

}
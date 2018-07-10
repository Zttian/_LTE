package com.tky.lte.lte;

import java.util.ArrayList;
import java.util.Calendar;

import org.w3c.dom.ls.LSException;

import android.util.Log;


public class FrameGenerate
{
	public FrameGenerate()
	{
	}
	public static int GetFrame_SetMMIMMIDuanKou(byte[] buffertx, byte bDestPort,byte iShuju)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) bDestPort; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x03;
			buffertx[txdatalength++] = 0x42;// cmd
			buffertx[txdatalength++] = (byte) iShuju;
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}
	public static int GetFrame_PowerOnOff_03_46(byte[] buffertx, byte isOnOff, byte bDestCode)
	{// 发送关开电源信息，实现工空板重启时
		// 发送关开电源信息，实现工空板重启时，MMI进入备用状态
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode; // 目的端口 向MMI发送信息
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x03;// 业务类型
			buffertx[txdatalength++] = 0x46; // 开关电源命令
			buffertx[txdatalength++] = isOnOff; // 数据:关电源0x00,开电源0x01
			txdatalength += 2;

			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	public static int GetFrame_DataRecord(byte[] buffertx, int len)
	{// 将数据帧转为记录单元数据
		buffertx[0] = (byte) 0xff;
		buffertx[1] = (byte) 0xff;
		return len;
	}

	public static int GetFrame_Set450Number_E1_0A(byte[] buffertx, TrainState mTrainState)
	{// 设置450MHz电台串号,取八位机车号的后五位
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x05; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0xE1;// 业务类型:450电台测试
			buffertx[txdatalength++] = 0x0a;// cmd,//设置450MHz电台串号,取八位机车号的后五位
			buffertx[txdatalength++] = 0x02;
			buffertx[txdatalength++] = 0x01;
			buffertx[txdatalength++] = (byte) ((mTrainState.g_EngineNumber.Number[3] - 0x30));
			buffertx[txdatalength++] = (byte) (((mTrainState.g_EngineNumber.Number[4] - 0x30) << 4) + (mTrainState.g_EngineNumber.Number[5] - 0x30));
			buffertx[txdatalength++] = (byte) (((mTrainState.g_EngineNumber.Number[6] - 0x30) << 4) + (mTrainState.g_EngineNumber.Number[7] - 0x30));
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	public static int GetFrame_Set450Number_E1_0A(byte[] buffertx, byte[] tmpChuanHao)
	{
		int txdatalength = 2, index = 0;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x05; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0xE1;// 业务类型:450电台测试
			buffertx[txdatalength++] = 0x0a;// cmd,//设置450MHz电台串号,取八位机车号的后五位
			buffertx[txdatalength++] = 0x02;
			buffertx[txdatalength++] = 0x01;
			buffertx[txdatalength++] = (byte) (((tmpChuanHao[index++] - 0x30) << 4) + (tmpChuanHao[index++] - 0x30));
			buffertx[txdatalength++] = (byte) (((tmpChuanHao[index++] - 0x30) << 4) + (tmpChuanHao[index++] - 0x30));
			buffertx[txdatalength++] = (byte) (((tmpChuanHao[index++] - 0x30) << 4) + (tmpChuanHao[index++] - 0x30));
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	public static int GetFrame_Query450Number_E1_0A(byte[] buffertx)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x05; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0xE1;// 业务类型:450电台测试
			buffertx[txdatalength++] = 0x0a;// cmd,//设置450MHz电台串号,取八位机车号的后五位
			buffertx[txdatalength++] = 0x02;
			buffertx[txdatalength++] = 0x02;// 查询
			buffertx[txdatalength++] = (byte) 0x00;
			buffertx[txdatalength++] = (byte) 0x00;
			buffertx[txdatalength++] = (byte) 0x00;
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	public static int GetFrame_Get450Number_E1_0A(byte[] buffertx, _RawInfo PackageInfo, byte bDestCode, TrainState mTrainState)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;// 业务类型
			buffertx[txdatalength++] = (byte) 0xFF;// cmd
			buffertx[txdatalength++] = mTrainState.g_DthFlag;
			byte[] tmpChuanHao = new byte[6] ;
			tmpChuanHao[0] = (byte) ((PackageInfo.Data[1] >> 4) & 0x0f);
			tmpChuanHao[1] = (byte) (PackageInfo.Data[1] & 0x0F);
			tmpChuanHao[2] = (byte) ((PackageInfo.Data[2] >> 4) & 0x0f);
			tmpChuanHao[3] = (byte) (PackageInfo.Data[2] & 0x0F);
			tmpChuanHao[4] = (byte) ((PackageInfo.Data[3] >> 4) & 0x0f);
			tmpChuanHao[5] = (byte) (PackageInfo.Data[3] & 0x0F);
			buffertx[txdatalength++] = 5;// 长度
			for (int i = 1; i < 6; i++)
			{
				buffertx[txdatalength++] = (byte) (tmpChuanHao[i] + 0x30);
			}
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	public static String tag = "FrameGenerate";



	// 主控接口板I/O信号
	public static int GetFrame_IOInfo_E0_00(byte[] buffertx, byte[] bufferrx, int rxdatalength, _RawInfo PackageInfo)
	{
		int txdatalength = 0;
		try
		{
			for (int i = 0; i < 32 && i < rxdatalength; i++)
				buffertx[i] = bufferrx[i] ;
			buffertx[2] = (byte) GlobalPara.iZhuKongFlag;// 源端口号
			buffertx[4 + PackageInfo.SourAddreLenth] = PackageInfo.SourCode; // 改变目的端口号为源端口号，把应答信息发回去
			buffertx[7 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth] = 0x00; // 命令
			buffertx[8 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth] = 'O';// 数据的第1个字节
			buffertx[9 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth] = 'K';// 数据的第2个字节
			txdatalength = 12 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	// 主控对数据的回复
	public static int GetFrame_ZhuKongAck_41_XX(byte[] buffertx, byte[] bufferrx, int rxdatalength, _RawInfo PackageInfo)
	{
		int txdatalength = 2;
		try
		{
			for (int i = 0; i < 32 && i < rxdatalength; i++)
				buffertx[i] = bufferrx[i] ;
			buffertx[2] = (byte) GlobalPara.iZhuKongFlag;// 源端口号
			buffertx[4 + PackageInfo.SourAddreLenth] = PackageInfo.SourCode; // 改变目的端口号为源端口号，把应答信息发回去
			// if(PackageInfo.SourCode != 0x05)
			buffertx[7 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth] = 0x41; // 命令
			// else
			// buffertx[7 + PackageInfo.SourAddreLenth +
			// PackageInfo.DectAddreLenth] = 0x01; //命令
			buffertx[8 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth] = PackageInfo.Command;
			int datalength = PackageInfo.InfoLenth - 8 - PackageInfo.SourAddreLenth - PackageInfo.DectAddreLenth;
			if (datalength > 0)
				buffertx[9 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth] = PackageInfo.Data[0] ;// 数据的第1个字节
			else
				buffertx[9 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth] = (byte) 0xFF;// 数据的第1个字节
			txdatalength = 12 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	// 向MMI发送新的库检设备IP地址和电话号码
	public static int GetFrame_KuJianIpAndPhone_01_60(byte[] buffertx, TrainState mTrainState, byte bDestCode)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode;// 目的端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x01;// 业务
			buffertx[txdatalength++] = 0x60; // 命令:主机输出配置参数
			buffertx[txdatalength++] = 0x05; // 电池容量最高
			buffertx[txdatalength++] = mTrainState.m_CheckIP1[0] ; // 第一个库检设备IP地址
			buffertx[txdatalength++] = mTrainState.m_CheckIP1[1] ;
			buffertx[txdatalength++] = mTrainState.m_CheckIP1[2] ;
			buffertx[txdatalength++] = mTrainState.m_CheckIP1[3] ;
			buffertx[txdatalength++] = mTrainState.m_CheckIP2[0] ; // 第二个库检设备IP地址
			buffertx[txdatalength++] = mTrainState.m_CheckIP2[1] ;
			buffertx[txdatalength++] = mTrainState.m_CheckIP2[2] ;
			buffertx[txdatalength++] = mTrainState.m_CheckIP2[3] ;
			buffertx[txdatalength++] = mTrainState.m_CheckIP3[0] ; // 第三个库检设备IP地址
			buffertx[txdatalength++] = mTrainState.m_CheckIP3[1] ;
			buffertx[txdatalength++] = mTrainState.m_CheckIP3[2] ;
			buffertx[txdatalength++] = mTrainState.m_CheckIP3[3] ;
			for (int i = 0; i < mTrainState.g_CheckPhoneNumber1.length(); i++)
				// 第一个库检设备电话
				buffertx[txdatalength++] = (byte) mTrainState.g_CheckPhoneNumber1.charAt(i);
			buffertx[txdatalength++] = ';';
			for (int i = 0; i < mTrainState.g_CheckPhoneNumber2.length(); i++)
				// 第二个库检设备电话
				buffertx[txdatalength++] = (byte) mTrainState.g_CheckPhoneNumber2.charAt(i);
			buffertx[txdatalength++] = ';';
			for (int i = 0; i < mTrainState.g_CheckPhoneNumber3.length(); i++)
				// 第三个库检设备电话
				buffertx[txdatalength++] = (byte) mTrainState.g_CheckPhoneNumber3.charAt(i);
			buffertx[txdatalength++] = ';';

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	public static int GetFrame_KuJianIpAndPhone_01_61(byte[] buffertx, TrainState mTrainState, byte bDestCode)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode;// 目的端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x01;// 业务
			buffertx[txdatalength++] = 0x61; // 命令:主机输出配置参数

			String tmpIPString = mTrainState.mLTE_SIP_IP;
			int index = tmpIPString.indexOf(".");
			int p1 = Integer.parseInt(tmpIPString.substring(0, index));
			tmpIPString = tmpIPString.substring(index + 1);
			index = tmpIPString.indexOf(".");
			int p2 = Integer.parseInt(tmpIPString.substring(0, index));
			tmpIPString = tmpIPString.substring(index + 1);
			index = tmpIPString.indexOf(".");
			int p3 = Integer.parseInt(tmpIPString.substring(0, index));
			tmpIPString = tmpIPString.substring(index + 1);
			int p4 = Integer.parseInt(tmpIPString);
			buffertx[txdatalength++] = (byte) p1; // 第一个IP地址
			buffertx[txdatalength++] = (byte) p2;
			buffertx[txdatalength++] = (byte) p3;
			buffertx[txdatalength++] = (byte) p4;

			tmpIPString = mTrainState.mLTE_SIP_IP2;
			index = tmpIPString.indexOf(".");
			p1 = Integer.parseInt(tmpIPString.substring(0, index));
			tmpIPString = tmpIPString.substring(index + 1);
			index = tmpIPString.indexOf(".");
			p2 = Integer.parseInt(tmpIPString.substring(0, index));
			tmpIPString = tmpIPString.substring(index + 1);
			index = tmpIPString.indexOf(".");
			p3 = Integer.parseInt(tmpIPString.substring(0, index));
			tmpIPString = tmpIPString.substring(index + 1);
			p4 = Integer.parseInt(tmpIPString);
			buffertx[txdatalength++] = (byte) p1; //
			buffertx[txdatalength++] = (byte) p2;
			buffertx[txdatalength++] = (byte) p3;
			buffertx[txdatalength++] = (byte) p4;

			// sip
			//if (GlobalPara.iZhuKongFlag == (byte) 0x01)
			//{//20150128
			for (int i = 0; i < mTrainState.mLTE_UserName.length(); i++)
				buffertx[txdatalength++] = (byte) mTrainState.mLTE_UserName.charAt(i);
			buffertx[txdatalength++] = ';';
			for (int i = 0; i < mTrainState.mLTE_PassWord.length(); i++)
				buffertx[txdatalength++] = (byte) mTrainState.mLTE_PassWord.charAt(i);
			buffertx[txdatalength++] = ';';
			for (int i = 0; i < mTrainState.mLTE_UserName2.length(); i++)
				buffertx[txdatalength++] = (byte) mTrainState.mLTE_UserName2.charAt(i);
			buffertx[txdatalength++] = ';';
			for (int i = 0; i < mTrainState.mLTE_PassWord2.length(); i++)
				buffertx[txdatalength++] = (byte) mTrainState.mLTE_PassWord2.charAt(i);
			buffertx[txdatalength++] = ';';
			//}
			//else if (GlobalPara.iZhuKongFlag == (byte) 0x7F)
			//{//20150128
			//	for (int i = 0; i < mTrainState.mLTE_UserName2.length(); i++)
			//		buffertx[txdatalength++] = (byte) mTrainState.mLTE_UserName2.charAt(i);
			//	buffertx[txdatalength++] = ';';
			//	for (int i = 0; i < mTrainState.mLTE_PassWord2.length(); i++)
			//		buffertx[txdatalength++] = (byte) mTrainState.mLTE_PassWord2.charAt(i);
			//	buffertx[txdatalength++] = ';';
			//	for (int i = 0; i < mTrainState.mLTE_UserName.length(); i++)
			//		buffertx[txdatalength++] = (byte) mTrainState.mLTE_UserName.charAt(i);
			//	buffertx[txdatalength++] = ';';
			//	for (int i = 0; i < mTrainState.mLTE_PassWord.length(); i++)
			//		buffertx[txdatalength++] = (byte) mTrainState.mLTE_PassWord.charAt(i);
			//	buffertx[txdatalength++] = ';';
			//}
			//
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	// MMI查询主机软件版本号
	public static int GetFrame_Version_01_AA(byte[] buffertx, TrainState mTrainState, byte bDestCode)
	{
		int txdatalength = 2, i = 0;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode;// 目的端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x01;// 业务
			buffertx[txdatalength++] = (byte) 0xaa; // 命令:主机向MMI报告版本号
			for (i = 0; i < GlobalPara.g_SoftwareVersion.length(); i++)
				buffertx[txdatalength++] = (byte) GlobalPara.g_SoftwareVersion.charAt(i);
			// 添加poc版本
			buffertx[txdatalength++] = ',';
			for (i = 0; i < GlobalPara.g_PocVersion.length(); i++)
				buffertx[txdatalength++] = (byte) GlobalPara.g_PocVersion.charAt(i);

			buffertx[txdatalength++] = ';';
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	// 主机向各单元查询软件版本号
	public static int GetFrame_Version_01_A5(byte[] buffertx, byte dest)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = dest;// 目的端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x01;// 业务
			buffertx[txdatalength++] = (byte) 0xA5; // 命令:主机向MMI查询版本号

			if(dest == 0x07)
				buffertx[txdatalength++] = (byte) 0x55;

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky,"0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	// 主机向各单元查询软件版本号
	public static int GetFrame_Version_ForMMI450M_01_A5(byte[] buffertx, byte dest, byte bDeviceCode)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = dest;// 目的端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0xE2;// 业务
			buffertx[txdatalength++] = (byte) 0x04; // 命令:主机向MMI/450M查询版本号
			buffertx[txdatalength++] = bDeviceCode;
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	// 主机向各单元查询软件版本号
	public static int GetFrame_Version_01_FA(byte[] buffertx, byte dest)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = dest;// 目的端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x01;// 业务
			buffertx[txdatalength++] = (byte) 0xFA; // 命令:主机向MMI报告版本号

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky,"0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	// 返回时钟
	public static int GetFrame_Clock_01_91(byte[] buffertx, TrainState mTrainState, byte bDestCode)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode;// 目的端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x01;// 业务
			buffertx[txdatalength++] = (byte) 0x91; // 命令:返回时钟

			Calendar aCalendar = Calendar.getInstance();
			int iYear = aCalendar.get(Calendar.YEAR);
			int iMonth = aCalendar.get(Calendar.MONTH) + 1;
			int iDay = aCalendar.get(Calendar.DAY_OF_MONTH);
			int iHour = aCalendar.get(Calendar.HOUR_OF_DAY);
			int iMinutes = aCalendar.get(Calendar.MINUTE);
			int iSecond = aCalendar.get(Calendar.SECOND);

			/*
			 * buffertx[txdatalength++] = (byte) (((((iYear % 100) / 10) & 0x0F)  << 4) | ((iYear % 1000) & 0x0F)); buffertx[txdatalength++] =(byte) ((((iMonth / 10) & 0x0F) << 4) | ((iMonth % 10) & 0x0F));
			 * buffertx[txdatalength++] = (byte) ((((iDay / 10) & 0x0F) << 4) |  ((iDay % 10) & 0x0F)); buffertx[txdatalength++] = (byte) ((((iHour / 10) & 0x0F) << 4) | ((iHour % 10) & 0x0F));
			 * buffertx[txdatalength++] = (byte) ((((iMinutes / 10) & 0x0F) <<  4) | ((iMinutes % 10) & 0x0F)); buffertx[txdatalength++] = (byte)  ((((iSecond / 10) & 0x0F) << 4) | ((iSecond % 10) & 0x0F));
			 */
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iYear - 2000);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iMonth);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iDay);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iHour);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iMinutes);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iSecond);
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	// 主机控制扩展板1的输入信号状态
	public static int GetFrame_SetIOInfo_E0_21(byte[] buffertx, TrainState mTrainState, int isOnOff)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x7e;// 目的端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0xE0;
			buffertx[txdatalength++] = 0x21; // 主机控制扩展板1的输出电平
			if (isOnOff == 1)
				mTrainState.g_OutputSignalStatus = mTrainState.g_OutputSignalStatus | GlobalPara.BIT3;// 置高电平闭合电池电压
			else
				mTrainState.g_OutputSignalStatus = mTrainState.g_OutputSignalStatus & (~GlobalPara.BIT3);// 低电平断开电池
			// buffertx[txdatalength++] = HIBYTE(g_OutputSignalStatus);//
			buffertx[txdatalength++] = (byte) ((mTrainState.g_OutputSignalStatus >> 8) & 0xFF);
			buffertx[txdatalength++] = (byte) mTrainState.g_OutputSignalStatus;
			// BIT3:CON-BATT:1,闭合电池电源
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	public static int GetFrame_QueryIOInfo_E0_22(byte[] buffertx)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x7E;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0xe0;
			buffertx[txdatalength++] = 0x22;
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	public static void ModifyLocalIp(byte[] buffertx, TrainState mTrainState)
	{
		try
		{
			buffertx[4] = mTrainState.m_SourLteAddre[0] ; // 本机IP地址,须由主机填入
			buffertx[5] = mTrainState.m_SourLteAddre[1] ;
			buffertx[6] = mTrainState.m_SourLteAddre[2] ;
			buffertx[7] = mTrainState.m_SourLteAddre[3] ;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky,"0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
	}



	public static int GetFrame_OperateVoiceSwitch_E3_21(byte[] buffertx, byte g_Mmi1Aim ,byte g_Mmi2Aim)
	{
		int len = 2;
		try
		{
			// 发送给音频矩阵开关
			buffertx[len++] = (byte) GlobalPara.iZhuKongFlag;// 源
			buffertx[len++] = 0x00;
			buffertx[len++] = 0x7B;// 目的
			buffertx[len++] = 0x00;
			buffertx[len++] = (byte) 0xE3;
			buffertx[len++] = 0x21;
			buffertx[len++] = g_Mmi1Aim;
			buffertx[len++] = g_Mmi2Aim;
			len += 2;
			buffertx[0] = (byte) ((len - 2) / 256);
			buffertx[1] = (byte) ((len - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return len;
	}

	public static int GetFrame_OperateVoiceAB_E3_24(byte[] buffertx, byte bAB)
	{
		int len = 2;
		try
		{
			// 发送给音频矩阵开关
			buffertx[len++] = (byte) GlobalPara.iZhuKongFlag;// 源
			buffertx[len++] = 0x00;
			buffertx[len++] = 0x7B;// 目的
			buffertx[len++] = 0x00;
			buffertx[len++] = (byte) 0xE3;
			buffertx[len++] = 0x24;
			buffertx[len++] = bAB;
			len += 2;
			buffertx[0] = (byte) ((len - 2) / 256);
			buffertx[1] = (byte) ((len - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return len;
	}

	public static int GetFrame_OperateVoicePtt_E3_28(byte[] buffertx, byte bPttOperate)
	{
		int len = 2;
		try
		{
			// 发送给音频矩阵开关
			buffertx[len++] = (byte) GlobalPara.iZhuKongFlag;// 源
			buffertx[len++] = 0x00;
			buffertx[len++] = 0x7B;// 目的
			buffertx[len++] = 0x00;
			buffertx[len++] = (byte) 0xE3;
			buffertx[len++] = 0x28;
			buffertx[len++] = bPttOperate;//1按下,0松开
			len += 2;
			buffertx[0] = (byte) ((len - 2) / 256);
			buffertx[1] = (byte) ((len - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return len;
	}



	public static int GetFrame_ResponseUpdateGrisIp_0f_02(byte[] buffertx, TrainState mTrainState, _RawInfo PackageInfo)
	{
		int i, txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x04;
			buffertx[txdatalength++] = mTrainState.m_SourLteAddre[0] ; // 本机IP地址,须由主机填入
			buffertx[txdatalength++] = mTrainState.m_SourLteAddre[1] ;
			buffertx[txdatalength++] = mTrainState.m_SourLteAddre[2] ;
			buffertx[txdatalength++] = mTrainState.m_SourLteAddre[3] ;
			buffertx[txdatalength++] = 0x27;
			buffertx[txdatalength++] = 0x04;
			buffertx[txdatalength++] = PackageInfo.SourAddre[0] ;
			buffertx[txdatalength++] = PackageInfo.SourAddre[1] ;
			buffertx[txdatalength++] = PackageInfo.SourAddre[2] ;
			buffertx[txdatalength++] = PackageInfo.SourAddre[3] ;
			buffertx[txdatalength++] = 0x0f;// 业务类型
			buffertx[txdatalength++] = 0x02;// 命令
			buffertx[txdatalength++] = 0x08; // 机车号长度
			for (i = 0; i < 8; i++)
				buffertx[txdatalength++] = mTrainState.g_EngineNumber.Number[i] ; // 机车号
			buffertx[txdatalength++] = (byte) 0xff;
			buffertx[txdatalength++] = (byte) 0xff;
			for (i = 0; i < 4; i++)
				buffertx[txdatalength++] = mTrainState.m_DMISAddre[i] ; // 当前接口服务器IP地址
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	public static int GetFrame_WuXianCheCiHao_05_80(byte[] buffertx)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x7E;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x05;
			buffertx[txdatalength++] = (byte) 0x80;
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}


	public static int GetFrame_ReportVoltageTemperate_30_60(byte[] buffertx, _RawInfo PackageInfo, byte bDestPort)
	{
		int txdatalength = 2, i;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestPort;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x03;
			buffertx[txdatalength++] = 0x60; // 向MMI发送电池电压及温度
			for (i = 0; i < 6; i++)
				buffertx[txdatalength++] = PackageInfo.Data[i] ;
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}





	public static int GetFrame_CheCiHao_05_6a(byte[] buffertx, _RawInfo PackageInfo, TrainState mTrainState)
	{
		int i, txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x7E;// 1220 7B改7E
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x05;
			if (PackageInfo.Command == 0x01)
				buffertx[txdatalength++] = 0x6a; // CIR发送车次号信息
			if (PackageInfo.Command == 0x0e)
				buffertx[txdatalength++] = 0x6e; // 发送车次号测试数据（短按测试键）
			if (PackageInfo.Command == 0x0f)
				buffertx[txdatalength++] = 0x6f; // 发送车次号测试数据（长按测试键）
			if (PackageInfo.Data[0] == 0x39)// 旧版tax数据
			{
				for (i = 0; i < 39; i++)
					buffertx[txdatalength++] = PackageInfo.Data[i] ; // 监测装置数据
			}
			else
			{
				for (i = 0; i < 72; i++)
				{
					// 使用CIR存储的车次号
					if (i == 14) // 机车型号扩充字节
						buffertx[txdatalength++] = (byte) (((mTrainState.g_EngineNumber.Number[0] - 0x30) * 100
								+ (mTrainState.g_EngineNumber.Number[1] - 0x30) * 10 + (mTrainState.g_EngineNumber.Number[2] - 0x30)) / 0x100);
					else if (i == 64) // 机车号低字节
						buffertx[txdatalength++] = (byte) (((mTrainState.g_EngineNumber.Number[3] - 0x30) * 10000
								+ (mTrainState.g_EngineNumber.Number[4] - 0x30) * 1000 + (mTrainState.g_EngineNumber.Number[5] - 0x30) * 100
								+ (mTrainState.g_EngineNumber.Number[6] - 0x30) * 10 + (mTrainState.g_EngineNumber.Number[7] - 0x30)) % 0x100);
					else if (i == 65) // 机车号高字节
						buffertx[txdatalength++] = (byte) ((((mTrainState.g_EngineNumber.Number[3] - 0x30) * 10000
								+ (mTrainState.g_EngineNumber.Number[4] - 0x30) * 1000 + (mTrainState.g_EngineNumber.Number[5] - 0x30) * 100
								+ (mTrainState.g_EngineNumber.Number[6] - 0x30) * 10 + (mTrainState.g_EngineNumber.Number[7] - 0x30)) / 0x100) % 0x100);
					else if (i == 66) // 机车型号
						buffertx[txdatalength++] = (byte) (((mTrainState.g_EngineNumber.Number[0] - 0x30) * 100
								+ (mTrainState.g_EngineNumber.Number[1] - 0x30) * 10 + (mTrainState.g_EngineNumber.Number[2] - 0x30)) % 0x100);
					else
						buffertx[txdatalength++] = PackageInfo.Data[i] ; // 监测装置数据
				}
			}
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}



	// TAX箱适配器测试按键消息,向MMI发送当前车次号和机车号
	public static int GetFrame_01_61(byte[] buffertx, _RawInfo PackageInfo, byte bDestPost)
	{
		int i, txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestPost;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x01;
			buffertx[txdatalength++] = 0x6f;
			for (i = 0; i < 15; i++)
				buffertx[txdatalength++] = PackageInfo.Data[i] ;
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	public static int GetFrame_IsGprs_08_f0(byte[] buffertx)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x12;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x08;
			buffertx[txdatalength++] = (byte) 0xf0; // 主机发送查询应答
			// if(g_GPRS.m_Status==0x02)
			buffertx[txdatalength++] = 0x02; // CSD方式
			// else
			// buffertx[txdatalength++] = 0x01; //GPRS方式
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	public static int GetFrame_DiaoCheJianKong_F5_0X(byte[] buffertx, byte cmd)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x12;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x08;
			buffertx[txdatalength++] = (byte) 0xf5;
			buffertx[txdatalength++] = cmd; // 进入调车监控状态成功
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	public static int GetFrame_03_XX(byte[] buffertx, _RawInfo PackageInfo, TrainState mTrainState, byte cmd, byte flag)
	{
		int txdatalength = 0;
		try
		{
			buffertx[2] = (byte) GlobalPara.iZhuKongFlag;// 源端口号
			buffertx[4 + PackageInfo.SourAddreLenth] = 0x05; // 目的端口号
			buffertx[6 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth] = 0x03; // 业务类型
			txdatalength = 11 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
			buffertx[7 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth] = cmd;// 命令//发送摘挂机状态
			buffertx[8 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth] = flag;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	public static int GetFrame_SendMsisdn_03_1e(byte[] buffertx, TrainState mTrainState, byte bDestCode)
	{
		int i, txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x03;
			buffertx[txdatalength++] = 0x1e; // 向MMI发送本机MSISDN号码
			if(mTrainState.mLTE_Msisdn.equals(""))
			{
				String strTemp = "00000000000";
				for (i = 0; i < strTemp.length(); i++)
					buffertx[txdatalength++] = (byte) strTemp.charAt(i);
			}
			else
			{
				for (i = 0; i < mTrainState.mLTE_Msisdn.length(); i++)
					buffertx[txdatalength++] = (byte) mTrainState.mLTE_Msisdn.charAt(i);
			}
			buffertx[txdatalength++] = ';';
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}

	public static int GetFrame_RegisterMainEngineResponse_03_57( byte[] buffertx, byte bDestCode, TrainState mTrainState,
																 byte result, byte reason, byte times )
	{
		int i, txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x03;
			buffertx[txdatalength++] = 0x57; // 机车号注册/注销结果
			buffertx[txdatalength++] = result; // 机车号注册成功
			buffertx[txdatalength++] = reason; // 其他原因
			buffertx[txdatalength++] = times; // 注册注销次数
			for (i = 0; i < 8; i++)
				buffertx[txdatalength++] = mTrainState.g_MainEngineNumber.Number[i] ;
			buffertx[txdatalength++] = ';';
			if(!GlobalPara.strDoubleHeadingGroupID.equals(""))
			{
				for (i = 0; i < GlobalPara.strDoubleHeadingGroupID.length(); i++)
				{
					buffertx[txdatalength++] = (byte) GlobalPara.strDoubleHeadingGroupID.charAt(i);
				}
			}
			buffertx[txdatalength++] = ';';
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_QueryRegisterTrainNum_03_58(byte[] buffertx, byte bDestCode, TrainState mTrainState, byte result, byte reason,
														   byte times)
	{
		int i, txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x03;
			buffertx[txdatalength++] = 0x58; // 车次号注册/注销结果
			buffertx[txdatalength++] = result; // 车次号注册成功
			buffertx[txdatalength++] = reason; // 其他原因
			buffertx[txdatalength++] = times; // 注册注销次数
			for (i = 0; i < 7; i++)
				buffertx[txdatalength++] = mTrainState.g_TrainNumber.Number[i] ;
			buffertx[txdatalength++] = ';';
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_QueryRegisterEngine_03_59(byte[] buffertx, byte bDestCode, TrainState mTrainState, byte result, byte reason, byte times)
	{
		int i, txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x03;
			buffertx[txdatalength++] = 0x59; // 机车号注册/注销结果
			buffertx[txdatalength++] = result; // 机车号注册成功
			buffertx[txdatalength++] = reason; // 其他原因
			buffertx[txdatalength++] = times; // 注册注销次数
			for (i = 0; i < 8; i++)
				buffertx[txdatalength++] = mTrainState.g_EngineNumber.Number[i] ;
			buffertx[txdatalength++] = ';';
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_RequestCheCiHaoInfo_05_08(byte[] buffertx)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x7E;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x05;
			buffertx[txdatalength++] = (byte) 0x80; // 申请输出车次号信息
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_KuJianYingDa_13_8C(byte[] buffertx, TrainState mTrainState)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x04;
			buffertx[txdatalength++] = mTrainState.m_SourLteAddre[0] ; // 本机IP地址,须由主机填入
			buffertx[txdatalength++] = mTrainState.m_SourLteAddre[1] ;
			buffertx[txdatalength++] = mTrainState.m_SourLteAddre[2] ;
			buffertx[txdatalength++] = mTrainState.m_SourLteAddre[3] ;
			buffertx[txdatalength++] = 0x31;
			buffertx[txdatalength++] = 0x04;
			buffertx[txdatalength++] = mTrainState.m_CurrentCheckIP[0] ;
			buffertx[txdatalength++] = mTrainState.m_CurrentCheckIP[1] ;
			buffertx[txdatalength++] = mTrainState.m_CurrentCheckIP[2] ;
			buffertx[txdatalength++] = mTrainState.m_CurrentCheckIP[3] ;
			buffertx[txdatalength++] = 0x13;
			buffertx[txdatalength++] = (byte) 0x8c;

			buffertx[txdatalength++] = (byte) 0x0A;//厂商代号
			for(int i=0 ; i<8; i++)
			{
				buffertx[txdatalength++] = mTrainState.g_EngineNumber.Number[i];
			}
			buffertx[txdatalength++] = (byte)(mTrainState.g_bABJie );

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}





	public static int GetFrame_Test(byte[] buffertx, int destport, byte in)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = 0x06;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) destport;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x04;// 测试can帧
			buffertx[txdatalength++] = 0x04;//
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			buffertx[txdatalength++] = in;
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}


	public static int GetFrame_OperateCommSerials(byte[] buffertx, int destport, byte com3, byte com4, byte com450)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) destport;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x01;//
			buffertx[txdatalength++] = 0x20;//
			buffertx[txdatalength++] = com3;
			buffertx[txdatalength++] = com4;
			buffertx[txdatalength++] = com450;
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_TelRecWhichMMIisUsed(byte[] buffertx, TrainState mTrainState)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x07;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x01;//
			buffertx[txdatalength++] = (byte)0xB0;//
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			if((mTrainState.g_UserMode_Myself == (byte)0x02 && mTrainState.g_UserMode_OtherBoard == (byte)0x00)
					||(mTrainState.g_UserMode_Myself == (byte)0x00 && mTrainState.g_UserMode_OtherBoard == (byte)0x02))
			{
				buffertx[txdatalength++] = mTrainState.g_MainMMI;
			}
			else {
				if(mTrainState.g_UserMode_Myself == (byte)0x00)
					buffertx[txdatalength++] = 0x34;
				else
					buffertx[txdatalength++] = 0x00;
			}
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}



	public static int GetFrame_ReportCallResult_03_5B(byte[] buffertx, byte bMmiPort,byte bResult)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) bMmiPort;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x03;//
			buffertx[txdatalength++] = (byte)0x5B;//
			buffertx[txdatalength++] = bResult;
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_QueryCommSerials(byte[] buffertx, int destport)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) destport;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x01;//
			buffertx[txdatalength++] = 0x21;//
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_GetUserSettingModeInfo(byte[] buffertx, int iOtherBoardFlag, TrainState mTrainState)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) iOtherBoardFlag; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = 0x40;//
			buffertx[txdatalength++] = (byte) mTrainState.g_UserSetting_Myself;// 工作方式//0
			buffertx[txdatalength++] = (byte) mTrainState.g_UserMode_Myself;// 工作模式//1
			buffertx[txdatalength++] = (byte) mTrainState.m_Signal_Strenghthen_MainControl;//2本方场强
			buffertx[txdatalength++] = mTrainState.g_PocStateZhuBoard;//3本地POC状态
			if(!GlobalPara.bLieWeiIsOklte)
				buffertx[txdatalength++] = 0;//4本地列尾状态
			else
				buffertx[txdatalength++] = 1;//4
			buffertx[txdatalength++] = mTrainState.g_MainMMI; //5本地主用MMI
			buffertx[txdatalength++] = GlobalPara.arrLieWeiZhuangZhi[0] ;//6
			buffertx[txdatalength++] = GlobalPara.arrLieWeiZhuangZhi[1] ;
			buffertx[txdatalength++] = GlobalPara.arrLieWeiZhuangZhi[2] ;

			buffertx[txdatalength++] = GlobalPara.iGlobalMMIPort;//9 //20140104
			if(GlobalPara.bMainUsed)
				buffertx[txdatalength++] = 0x01;//10 //20140104
			else
				buffertx[txdatalength++] = 0x00;//10 //20140104

			byte tempdate = 0x00;
			if(GlobalPara.bHaveFindLteMoulde)
			{
				tempdate =(byte)( tempdate | GlobalPara.BIT0);
				tempdate =(byte)( tempdate | GlobalPara.BIT7);
			}
			if(GlobalPara.iIpIsOk == 1)
			{
				tempdate =(byte)( tempdate | GlobalPara.BIT4);
			}
			if(mTrainState.TrainNumberFNRegisterStatus == 1)
			{
				tempdate =(byte)( tempdate | GlobalPara.BIT5);
			}
			if(mTrainState.EngineNumberFNRegisterStatus == 1)
			{
				tempdate =(byte)( tempdate | GlobalPara.BIT6);
			}
			buffertx[txdatalength++] = tempdate;//11

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}


	public static int GetFrame_GetTongBuInfo(byte[] buffertx, int iOtherBoardFlag, TrainState mTrainState)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) iOtherBoardFlag; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = (byte)0x61;
			//ip
			buffertx[txdatalength++] = mTrainState.m_SourLteAddre[0] ;//0本地IP
			buffertx[txdatalength++] = mTrainState.m_SourLteAddre[1] ;
			buffertx[txdatalength++] = mTrainState.m_SourLteAddre[2] ;
			buffertx[txdatalength++] = mTrainState.m_SourLteAddre[3] ;
			buffertx[txdatalength++] = mTrainState.m_TailAddre[0] ;//4列尾ip
			buffertx[txdatalength++] = mTrainState.m_TailAddre[1] ;
			buffertx[txdatalength++] = mTrainState.m_TailAddre[2] ;
			buffertx[txdatalength++] = mTrainState.m_TailAddre[3] ;
			buffertx[txdatalength++] = mTrainState.m_TailAddre2[0] ;//8列尾ip
			buffertx[txdatalength++] = mTrainState.m_TailAddre2[1] ;
			buffertx[txdatalength++] = mTrainState.m_TailAddre2[2] ;
			buffertx[txdatalength++] = mTrainState.m_TailAddre2[3] ;
			if( !GlobalPara.bLieWeiIsOklte)
				buffertx[txdatalength++] = 0;//12本地列尾状态
			else
				buffertx[txdatalength++] = 1;//12
			//车次号机车号
			for (int i = 0; i < 7; i++)
				buffertx[txdatalength++] = mTrainState.g_TrainNumber.Number[i] ;//13
			for (int i = 0; i < 8; i++)
				buffertx[txdatalength++] = mTrainState.g_EngineNumber.Number[i] ;//20
			buffertx[txdatalength++] = mTrainState.m_BenBu_Status;//28
			buffertx[txdatalength++] = mTrainState.g_bABJie;//29
			//for (int i = 0; i < 8; i++)//30//不需要该信息,都取自dte
			//buffertx[txdatalength++] = mTrainState.g_MainEngineNumber.Number[i] ;
			buffertx[txdatalength++] = (byte)mTrainState.g_UserSetting_Myself;//30
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_SetUserSetting(byte[] buffertx, TrainState mTrainState)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) GlobalPara.iOtherBoardFlag; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = 0x42;// cmd
			buffertx[txdatalength++] = (byte) mTrainState.g_UserSetting_Myself;// 工作方式
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_GetUserSetting(byte[] buffertx, TrainState mTrainState, byte bDestCode)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = (byte) 0xFD;// cmd
			buffertx[txdatalength++] = (byte) mTrainState.g_UserSetting_Myself;// 工作方式
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_GetABJieInfo_01_C1(byte[] buffertx, TrainState mTrainState, byte bDestCode)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = (byte) 0xC1;// cmd
			buffertx[txdatalength++] = (byte) mTrainState.g_bABJie;// 工作方式
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_GetLocationInfo_01_C1(byte[] buffertx, TrainState mTrainState, byte bDestCode)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = (byte) 0xC1;// cmd
			buffertx[txdatalength++] = (byte) mTrainState.g_bLocation;//
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}


	public static int GetFrame_GetMainUsedInfo_01_C3(byte[] buffertx,  byte bDestCode)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = (byte) 0xC3;// cmd
			if(GlobalPara.bMainUsed)
				buffertx[txdatalength++] = (byte) 0x01;
			else
				buffertx[txdatalength++] = (byte) 0x00;
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}
	public static int GetFrame_GetCarNumberInfo_01_C5(byte[] buffertx,  byte bDestCode)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = (byte) 0xC5;// cmd
			buffertx[txdatalength++] =  GlobalPara.bCarNumber;
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_SetUserMode(byte[] buffertx, int iUsrMode)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) GlobalPara.iOtherBoardFlag; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = 0x43;// cmd
			buffertx[txdatalength++] = (byte) iUsrMode;// 工作方式
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_SetMMIOK(byte[] buffertx, int iOkOrNot)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) GlobalPara.iOtherBoardFlag; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = 0x59;// cmd
			buffertx[txdatalength++] = (byte) iOkOrNot;// 工作方式
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}
	public static int GetFrame_SetMainUsed(byte[] buffertx)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) GlobalPara.iOtherBoardFlag; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = 0x64;// cmd
			byte iOkOrNot = 0x00;
			if(GlobalPara.bMainUsed)
				iOkOrNot = 0x01;
			buffertx[txdatalength++] =  iOkOrNot;// 工作方式
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}
	public static int GetFrame_ShowInfoOnMMI_F1_5A(byte[] buffertx,byte bDestPort, String strInfo)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestPort; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0xF1;
			buffertx[txdatalength++] = 0x5A;//

			buffertx[txdatalength++] = 0x01;// 1代表状态区
			buffertx[txdatalength++] = 0x00;// 0代表MMI自己选择
			buffertx[txdatalength++] = (byte)0x88;// 4字节,单位为毫秒，表示显示时长//0xD0
			buffertx[txdatalength++] = 0x13;//0x07
			buffertx[txdatalength++] = 0x00;//
			buffertx[txdatalength++] = 0x00;//
			buffertx[txdatalength++] = 92;// 字体
			buffertx[txdatalength++] = 1;//
			buffertx[txdatalength++] = 100;//
			buffertx[txdatalength++] = 92;// 颜色
			buffertx[txdatalength++] = 2;//
			buffertx[txdatalength++] = (byte)255;//
			buffertx[txdatalength++] = 0;//
			buffertx[txdatalength++] = 0;//
			byte[] arr = strInfo.getBytes("GBK");
			buffertx[txdatalength++] = (byte)arr.length;//	字符长度
			for (int i = 0; i < arr.length; i++)
			{
				buffertx[txdatalength++] = arr[i] ;//
			}

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_ShowInfoOnMMI2_F1_5A(byte[] buffertx,byte bDestPort, String strInfo)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestPort; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0xF1;
			buffertx[txdatalength++] = 0x5A;//

			buffertx[txdatalength++] = 0x01;// 1代表状态区2代表提示框3代表全屏显示4代表告警框
			buffertx[txdatalength++] = 0x00;// 0代表MMI自己选择
			buffertx[txdatalength++] = (byte)0xE8;// 4字节,单位为毫秒，表示显示时长//0xD0
			buffertx[txdatalength++] = 0x03;//0x07
			buffertx[txdatalength++] = 0x00;//
			buffertx[txdatalength++] = 0x00;//
			buffertx[txdatalength++] = 92;// 字体
			buffertx[txdatalength++] = 1;//
			buffertx[txdatalength++] = 100;//100
			buffertx[txdatalength++] = 92;// 颜色
			buffertx[txdatalength++] = 2;//
			buffertx[txdatalength++] = (byte)255;//
			buffertx[txdatalength++] = 0;//
			buffertx[txdatalength++] = 0;//
			byte[] arr = strInfo.getBytes("GBK");
			buffertx[txdatalength++] = (byte)arr.length;//	字符长度
			for (int i = 0; i < arr.length; i++)
			{
				buffertx[txdatalength++] = arr[i] ;//
			}

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_KNotifySipParameter_01_44(byte[] buffertx, TrainState mTrainState)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) GlobalPara.iOtherBoardFlag;// 目的端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x01;// 业务
			buffertx[txdatalength++] = 0x44; // 命令:主机输出配置参数
			// sip
			//if (GlobalPara.iZhuKongFlag == (byte) 0x01)
			//{//20150128
			for (int i = 0; i < mTrainState.mLTE_UserName.length(); i++)
				buffertx[txdatalength++] = (byte) mTrainState.mLTE_UserName.charAt(i);
			buffertx[txdatalength++] = ';';
			for (int i = 0; i < mTrainState.mLTE_PassWord.length(); i++)
				buffertx[txdatalength++] = (byte) mTrainState.mLTE_PassWord.charAt(i);
			buffertx[txdatalength++] = ';';
			for (int i = 0; i < mTrainState.mLTE_UserName2.length(); i++)
				buffertx[txdatalength++] = (byte) mTrainState.mLTE_UserName2.charAt(i);
			buffertx[txdatalength++] = ';';
			for (int i = 0; i < mTrainState.mLTE_PassWord2.length(); i++)
				buffertx[txdatalength++] = (byte) mTrainState.mLTE_PassWord2.charAt(i);
			buffertx[txdatalength++] = ';';
			//}
			//else if (GlobalPara.iZhuKongFlag == (byte) 0x7F)
			//{//20150128
			//	for (int i = 0; i < mTrainState.mLTE_UserName2.length(); i++)
			//		buffertx[txdatalength++] = (byte) mTrainState.mLTE_UserName2.charAt(i);
			//	buffertx[txdatalength++] = ';';
			//	for (int i = 0; i < mTrainState.mLTE_PassWord2.length(); i++)
			//		buffertx[txdatalength++] = (byte) mTrainState.mLTE_PassWord2.charAt(i);
			//	buffertx[txdatalength++] = ';';
			//	for (int i = 0; i < mTrainState.mLTE_UserName.length(); i++)
			//		buffertx[txdatalength++] = (byte) mTrainState.mLTE_UserName.charAt(i);
			//	buffertx[txdatalength++] = ';';
			//	for (int i = 0; i < mTrainState.mLTE_PassWord.length(); i++)
			//		buffertx[txdatalength++] = (byte) mTrainState.mLTE_PassWord.charAt(i);
			//	buffertx[txdatalength++] = ';';
			//}
			for (int i = 0; i < mTrainState.mLTE_SIP_IP.length(); i++)
				buffertx[txdatalength++] = (byte) mTrainState.mLTE_SIP_IP.charAt(i);
			buffertx[txdatalength++] = ';';
			for (int i = 0; i < mTrainState.mLTE_SIP_IP2.length(); i++)
				buffertx[txdatalength++] = (byte) mTrainState.mLTE_SIP_IP2.charAt(i);
			buffertx[txdatalength++] = ';';
			//
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_LuJuLineInfo_F4_04(byte[] buffertx, byte bDestCode, int num, int sum, byte bQueryType, String str, int index,
												  ArrayList<String> lstLines)
	{
		int i, txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口号
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode; // 目的端口号
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0xF4;
			buffertx[txdatalength++] = 0x04;// 命令:返回第n级线路数据
			buffertx[txdatalength++] = (byte) num;// 本包号
			buffertx[txdatalength++] = (byte) sum;// 总包号
			buffertx[txdatalength++] = bQueryType;// 数据类型
			if (bQueryType == (byte) 0x01)
			{
				for (i = 0; i < 32; i++)
					buffertx[txdatalength++] = 0x00;
			}
			else if (bQueryType == (byte) 0x02)
			{
				if (str.equals(""))
					for (i = 0; i < 32; i++)
						buffertx[txdatalength++] = 0x00;
				else
				{
					byte[] tmpLuJu = str.getBytes("GBK");
					for (i = 0; i < tmpLuJu.length && i < 32; i++)
					{
						buffertx[txdatalength++] = tmpLuJu[i] ;//
					}
					for (; i < 32; i++)
					{// 不足32字节凑0x00
						buffertx[txdatalength++] = 0x00;
					}
				}
			}
			for (i = 0; i < 32; i++)
				buffertx[txdatalength++] = 0x00;// 预留上级线路区段名
			// 路局或线路
			i = 0;
			while (index < lstLines.size() && i < 5)
			{
				String tmpStr = lstLines.get(index);
				if (tmpStr == null || tmpStr.equals(""))
				{
					buffertx[txdatalength++] = 0x00;
				}
				else
				{
					byte[] tmpName = tmpStr.getBytes("GBK");
					buffertx[txdatalength++] = (byte) tmpName.length;
					for (int j = 0; j < tmpName.length; j++)
					{
						buffertx[txdatalength++] = tmpName[j] ;//
					}
				}
				i++;
				index++;
			}
			//
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_LuJuPhoneInfo_F4_06(byte[] buffertx, byte bDestCode, int num, int sum, byte bQueryType, String str, String str1,
												   int index, ArrayList<String> lstLines)
	{// str 上级路局名, str1 上级线路名
		int i, txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口号
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode; // 目的端口号
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0xF4;
			buffertx[txdatalength++] = 0x06;// 命令:返回第n级电话本数据
			buffertx[txdatalength++] = (byte) num;// 本包号
			buffertx[txdatalength++] = (byte) sum;// 总包号
			buffertx[txdatalength++] = bQueryType;// 数据类型
			if (bQueryType == (byte) 0x01)
			{
				for (i = 0; i < 32; i++)
					buffertx[txdatalength++] = 0x00;
				for (i = 0; i < 32; i++)
					buffertx[txdatalength++] = 0x00;// 预留上级线路区段名
			}
			else if (bQueryType == (byte) 0x02)
			{
				if (str.equals(""))
					for (i = 0; i < 32; i++)
						buffertx[txdatalength++] = 0x00;
				else
				{
					byte[] tmpLuJu = str.getBytes("GBK");
					for (i = 0; i < tmpLuJu.length && i < 32; i++)
					{
						buffertx[txdatalength++] = tmpLuJu[i] ;//
					}
					for (; i < 32; i++)
					{// 不足32字节凑0x00
						buffertx[txdatalength++] = 0x00;
					}
				}
				for (i = 0; i < 32; i++)
					buffertx[txdatalength++] = 0x00;// 预留上级线路区段名
			}
			else if (bQueryType == (byte) 0x03)
			{
				if (str.equals(""))
					for (i = 0; i < 32; i++)
						buffertx[txdatalength++] = 0x00;
				else
				{
					byte[] tmpLuJu = str.getBytes("GBK");
					for (i = 0; i < tmpLuJu.length && i < 32; i++)
					{
						buffertx[txdatalength++] = tmpLuJu[i] ;//
					}
					for (; i < 32; i++)
					{// 不足32字节凑0x00
						buffertx[txdatalength++] = 0x00;
					}
				}

				if (str1.equals(""))
					for (i = 0; i < 32; i++)
						buffertx[txdatalength++] = 0x00;// 预留上级线路区段名
				else
				{
					byte[] tmpXianLu = str1.getBytes("GBK");
					for (i = 0; i < tmpXianLu.length && i < 32; i++)
					{
						buffertx[txdatalength++] = tmpXianLu[i] ;//
					}
					for (; i < 32; i++)
					{// 不足32字节凑0x00
						buffertx[txdatalength++] = 0x00;
					}
				}
			}
			// 路局或线路或区段名
			i = 0;
			while (index < lstLines.size() && i < 5)
			{
				String tmpStr = lstLines.get(index);
				if (tmpStr == null || tmpStr.equals(""))
				{
					buffertx[txdatalength++] = 0x00;
				}
				else
				{
					byte[] tmpName = tmpStr.getBytes("GBK");
					buffertx[txdatalength++] = (byte) tmpName.length;
					for (int j = 0; j < tmpName.length; j++)
					{
						buffertx[txdatalength++] = tmpName[j] ;//
					}
				}
				i++;
				index++;
			}
			//
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}



	public static int GetFrame_NotifyWorkModeFlagChange_01_45(byte[] buffertx, _RawInfo PackageInfo, int datalength)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) GlobalPara.iOtherBoardFlag;// 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = 0x45;// cmd
			for (int i = 0; i < datalength; i++)
			{
				buffertx[txdatalength++] = PackageInfo.Data[i] ;
			}

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_NotifySomeParameterChange_01_46(byte[] buffertx, _RawInfo PackageInfo, int datalength)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) GlobalPara.iOtherBoardFlag;// 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = 0x46;// cmd
			for (int i = 0; i < datalength; i++)
			{
				buffertx[txdatalength++] = PackageInfo.Data[i] ;
			}

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}



	public static int GetFrame_StartABCall_Notify_E3_25(byte[] buffertx)
	{// 通知音频单元,开始振铃
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x7B;// 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0xE3;
			buffertx[txdatalength++] = (byte) 0x25;// cmd

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_StartABCall_Notify_E3_29(byte[] buffertx)
	{// 通知音频单元,开始回玲
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x7B;// 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0xE3;
			buffertx[txdatalength++] = (byte) 0x29;// cmd

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_ABCall_Notify_E3_27(byte[] buffertx, byte bSour, byte bDest, byte cmd)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = bSour;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDest;// 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x03;
			buffertx[txdatalength++] = (byte) 0x5E;// cmd

			buffertx[txdatalength++] = cmd;// 发起命令

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_ABCall_Response_E3_26(byte[] buffertx, TrainState mTrainState)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) mTrainState.g_MainMMI;// 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x03;
			buffertx[txdatalength++] = (byte) 0x5E;// cmd

			buffertx[txdatalength++] = (byte) 0x02;//

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_FenPeiZhuKong_03_43(byte[] buffertx, byte bDestPort, byte bZhuKongPort)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag; // 源端口号
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestPort;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x03;
			buffertx[txdatalength++] = 0x43;
			buffertx[txdatalength++] = bZhuKongPort; // 通知主控端口号
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_ZhuCong_01_13(byte[] buffertx, TrainState mTrainState,byte bDestCode)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode;// 目的端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x01;// 业务
			buffertx[txdatalength++] = 0x13; //
			buffertx[txdatalength++] = mTrainState.iZhuCong; //
			buffertx[txdatalength++] = mTrainState.iZhongJi; //
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_LieWeiQuery(byte[] buffertx,TrainState mTrainState, byte destport,int flag)
	{//20150210
		int txdatalength = 2;
		try
		{
			if ( flag == 0)
			{
				buffertx[txdatalength++] = (byte) 0x02;
				buffertx[txdatalength++] = 0x00;
				buffertx[txdatalength++] = (byte) destport;
				buffertx[txdatalength++] = 0x00;
			}
			else if ( flag == 1)
			{
				buffertx[txdatalength++] = (byte) 0x02;
				buffertx[txdatalength++] = 0x04;
				buffertx[txdatalength++] = mTrainState.m_SourLteAddre[0] ;
				buffertx[txdatalength++] = mTrainState.m_SourLteAddre[1] ;
				buffertx[txdatalength++] = mTrainState.m_SourLteAddre[2] ;
				buffertx[txdatalength++] = mTrainState.m_SourLteAddre[3] ;
				buffertx[txdatalength++] = (byte) destport;
				buffertx[txdatalength++] = 0x04;
				buffertx[txdatalength++] = mTrainState.m_TailAddre[0] ;
				buffertx[txdatalength++] = mTrainState.m_TailAddre[1] ;
				buffertx[txdatalength++] = mTrainState.m_TailAddre[2] ;
				buffertx[txdatalength++] = mTrainState.m_TailAddre[3] ;
			}
			else if ( flag == 2)
			{
				buffertx[txdatalength++] = (byte) 0x02;
				buffertx[txdatalength++] = 0x04;
				buffertx[txdatalength++] = mTrainState.m_SourLteAddre[0] ;
				buffertx[txdatalength++] = mTrainState.m_SourLteAddre[1] ;
				buffertx[txdatalength++] = mTrainState.m_SourLteAddre[2] ;
				buffertx[txdatalength++] = mTrainState.m_SourLteAddre[3] ;
				buffertx[txdatalength++] = (byte) destport;
				buffertx[txdatalength++] = 0x04;
				buffertx[txdatalength++] = mTrainState.m_TailAddre2[0] ;
				buffertx[txdatalength++] = mTrainState.m_TailAddre2[1] ;
				buffertx[txdatalength++] = mTrainState.m_TailAddre2[2] ;
				buffertx[txdatalength++] = mTrainState.m_TailAddre2[3] ;
			}
			buffertx[txdatalength++] = 0x04;// 业务类型
			buffertx[txdatalength++] = (byte)0x91;// 命令
			//主控机车号
			buffertx[txdatalength++] = GlobalFunc.IntToBCD((mTrainState.g_EngineNumber.Number[0] - '0')*10 + (mTrainState.g_EngineNumber.Number[1] - '0'));
			buffertx[txdatalength++] = GlobalFunc.IntToBCD((mTrainState.g_EngineNumber.Number[2] - '0')*10 + (mTrainState.g_EngineNumber.Number[3] - '0'));
			buffertx[txdatalength++] = GlobalFunc.IntToBCD((mTrainState.g_EngineNumber.Number[4] - '0')*10 + (mTrainState.g_EngineNumber.Number[5] - '0'));
			buffertx[txdatalength++] = GlobalFunc.IntToBCD((mTrainState.g_EngineNumber.Number[6] - '0')*10 + (mTrainState.g_EngineNumber.Number[7] - '0'));
			buffertx[txdatalength++] = 0x01;
			//尾部装置号
			buffertx[txdatalength++] = GlobalPara.arrLieWeiZhuangZhi[0] ;
			buffertx[txdatalength++] = GlobalPara.arrLieWeiZhuangZhi[1] ;
			buffertx[txdatalength++] = GlobalPara.arrLieWeiZhuangZhi[2] ;
			//机车属性
			buffertx[txdatalength++] = GlobalPara.bCarType;
			//编组数量
			buffertx[txdatalength++] = GlobalPara.bCarNumber;
			//时间
			Calendar aCalendar = Calendar.getInstance();
			int iYear = aCalendar.get(Calendar.YEAR);
			int iMonth = aCalendar.get(Calendar.MONTH) + 1;
			int iDay = aCalendar.get(Calendar.DAY_OF_MONTH);
			int iHour = aCalendar.get(Calendar.HOUR_OF_DAY);
			int iMinutes = aCalendar.get(Calendar.MINUTE);
			int iSecond = aCalendar.get(Calendar.SECOND);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iYear - 2000);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iMonth);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iDay);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iHour);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iMinutes);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iSecond);
			//网络状态字节
			if (mTrainState.g_Mode == 0x66 && GlobalPara.bLieWeiIsOklte )
				buffertx[txdatalength++] = 1;
			else
				buffertx[txdatalength++] = 0;
			//预留
			txdatalength += 13;

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_LieWeiHeartBeat(boolean isHaveIp, byte[] buffertx,TrainState mTrainState, byte destport,int flag)
	{//20150210
		int txdatalength = 2;
		try
		{
			if ( flag == 0)
			{
				buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
				buffertx[txdatalength++] = 0x00;
				buffertx[txdatalength++] = (byte) destport;
				buffertx[txdatalength++] = 0x00;
			}
			else if ( flag == 1)
			{
				buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
				buffertx[txdatalength++] = 0x04;
				buffertx[txdatalength++] = mTrainState.m_SourLteAddre[0] ;
				buffertx[txdatalength++] = mTrainState.m_SourLteAddre[1] ;
				buffertx[txdatalength++] = mTrainState.m_SourLteAddre[2] ;
				buffertx[txdatalength++] = mTrainState.m_SourLteAddre[3] ;
				buffertx[txdatalength++] = (byte) destport;
				buffertx[txdatalength++] = 0x04;
				buffertx[txdatalength++] = mTrainState.m_TailAddre[0] ;
				buffertx[txdatalength++] = mTrainState.m_TailAddre[1] ;
				buffertx[txdatalength++] = mTrainState.m_TailAddre[2] ;
				buffertx[txdatalength++] = mTrainState.m_TailAddre[3] ;
			}
			else if ( flag == 2)
			{
				buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
				buffertx[txdatalength++] = 0x04;
				buffertx[txdatalength++] = mTrainState.m_SourLteAddre[0] ;
				buffertx[txdatalength++] = mTrainState.m_SourLteAddre[1] ;
				buffertx[txdatalength++] = mTrainState.m_SourLteAddre[2] ;
				buffertx[txdatalength++] = mTrainState.m_SourLteAddre[3] ;
				buffertx[txdatalength++] = (byte) destport;
				buffertx[txdatalength++] = 0x04;
				buffertx[txdatalength++] = mTrainState.m_TailAddre2[0] ;
				buffertx[txdatalength++] = mTrainState.m_TailAddre2[1] ;
				buffertx[txdatalength++] = mTrainState.m_TailAddre2[2] ;
				buffertx[txdatalength++] = mTrainState.m_TailAddre2[3] ;
			}
			buffertx[txdatalength++] = 0x04;// 业务类型
			buffertx[txdatalength++] = 0x2F;// 命令
			//主控机车号
			buffertx[txdatalength++] = GlobalFunc.IntToBCD((mTrainState.g_EngineNumber.Number[0] - '0')*10 + (mTrainState.g_EngineNumber.Number[1] - '0'));
			buffertx[txdatalength++] = GlobalFunc.IntToBCD((mTrainState.g_EngineNumber.Number[2] - '0')*10 + (mTrainState.g_EngineNumber.Number[3] - '0'));
			buffertx[txdatalength++] = GlobalFunc.IntToBCD((mTrainState.g_EngineNumber.Number[4] - '0')*10 + (mTrainState.g_EngineNumber.Number[5] - '0'));
			buffertx[txdatalength++] = GlobalFunc.IntToBCD((mTrainState.g_EngineNumber.Number[6] - '0')*10 + (mTrainState.g_EngineNumber.Number[7] - '0'));
			buffertx[txdatalength++] = 0x01;
			//尾部装置号
			buffertx[txdatalength++] = GlobalPara.arrLieWeiZhuangZhi[0] ;
			buffertx[txdatalength++] = GlobalPara.arrLieWeiZhuangZhi[1] ;
			buffertx[txdatalength++] = GlobalPara.arrLieWeiZhuangZhi[2] ;
			//机车属性
			buffertx[txdatalength++] = GlobalPara.bCarType;
			//编组数量
			buffertx[txdatalength++] = GlobalPara.bCarNumber;
			//时间
			Calendar aCalendar = Calendar.getInstance();
			int iYear = aCalendar.get(Calendar.YEAR);
			int iMonth = aCalendar.get(Calendar.MONTH) + 1;
			int iDay = aCalendar.get(Calendar.DAY_OF_MONTH);
			int iHour = aCalendar.get(Calendar.HOUR_OF_DAY);
			int iMinutes = aCalendar.get(Calendar.MINUTE);
			int iSecond = aCalendar.get(Calendar.SECOND);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iYear - 2000);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iMonth);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iDay);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iHour);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iMinutes);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iSecond);
			//网络状态字节
			if (/*mTrainState.g_Mode == 0x66 && */ isHaveIp && GlobalPara.bLieWeiIsOklte  )
				buffertx[txdatalength++] = 1;
			else
				buffertx[txdatalength++] = 0;
			//预留
			txdatalength += 13;

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}







	public static int GetFrame_TellOtherboardWhichMMIisUsed(byte[] buffertx, byte g_MainMMI,byte bReason)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) GlobalPara.iOtherBoardFlag; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] =(byte) 0x54;//
			buffertx[txdatalength++] = (byte)g_MainMMI;// 主用MMI
			buffertx[txdatalength++] = bReason;// 端口号变化的原因值
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_TellOtherboardMMIDown(byte[] buffertx, byte bMMIState)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) GlobalPara.iOtherBoardFlag; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] =(byte) 0x56;//
			buffertx[txdatalength++] = (byte)bMMIState;// 1为down
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_TellOtherboardMyPttStatus(byte[] buffertx, String _WhichCall, int iPttStatus)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) GlobalPara.iOtherBoardFlag; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = (byte)0x55;//
			buffertx[txdatalength++] = (byte)(_WhichCall.charAt(0)-'0');// 呼叫类型210 220 299 789, 或者00代表ab-call liewei-call
			buffertx[txdatalength++] = (byte)(_WhichCall.charAt(1)-'0');
			buffertx[txdatalength++] = (byte)(_WhichCall.charAt(2)-'0');
			buffertx[txdatalength++] = (byte)iPttStatus;// ptt状态
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_TellOtherboardLieWeiXiaoHaoQueRen(byte[] buffertx)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) GlobalPara.iOtherBoardFlag; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = (byte)0x60;//
			buffertx[txdatalength++] = (byte)0x01;// 状态位,暂时未用
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_350LogOpearate(byte[] buffertx, int iOpType, int iValue)
	{
		int len = 2;
		try
		{//
			buffertx[len++] = (byte) 0xFF;// 源
			buffertx[len++] = 0x00;
			buffertx[len++] = (byte) 0xFF;// 目的
			buffertx[len++] = 0x00;
			buffertx[len++] = (byte) 0x00;
			buffertx[len++] = (byte)iOpType;
			buffertx[len++] = (byte)iValue;
			len += 2;
			buffertx[0] = (byte) ((len - 2) / 256);
			buffertx[1] = (byte) ((len - 2) % 256);
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
		return len;
	}
	public static int GetFrame_350LogData(byte[] buffertx, int iOpType, int iValue,int length,byte[] arr)
	{//iValue序号
		int len = 2;
		try
		{//
			buffertx[len++] = (byte) 0xFF;// 源
			buffertx[len++] = 0x00;
			buffertx[len++] = (byte) 0xFF;// 目的
			buffertx[len++] = 0x00;
			buffertx[len++] = (byte) 0x00;
			buffertx[len++] = (byte)iOpType;

			buffertx[len++] = (byte)(iValue/256);//帧序号
			buffertx[len++] = (byte)(iValue%256);

			for(int i = 0; i < length && i< arr.length ; i++)
			{
				buffertx[len++] = arr[i++] ;
			}
			len += 2;
			buffertx[0] = (byte) ((len - 2) / 256);
			buffertx[1] = (byte) ((len - 2) % 256);
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
		return len;
	}

	public static int GetFrame_TellMMIZhuangZhiHao(byte[] buffertx, byte bDestPost)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestPost;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x01;
			buffertx[txdatalength++] = 0x36;
			buffertx[txdatalength++] = GlobalPara.arrLieWeiZhuangZhi[0] ;
			buffertx[txdatalength++] = GlobalPara.arrLieWeiZhuangZhi[1] ;
			buffertx[txdatalength++] = GlobalPara.arrLieWeiZhuangZhi[2] ;

			buffertx[txdatalength++] = GlobalPara.bCarNumber;
			if(GlobalPara.bMainUsed)
				buffertx[txdatalength++] = 0x01;
			else
				buffertx[txdatalength++] = 0x00;
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_SetMainEngineFor400k(byte[] buffertx, TrainState mTrainState)
	{
		int   txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = 0x02;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x14;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x04;
			buffertx[txdatalength++] = 0x19;
			buffertx[txdatalength++] = (byte)((mTrainState.g_EngineNumber.Number[0] - '0') << 4 + (mTrainState.g_EngineNumber.Number[1]-'0'));
			buffertx[txdatalength++] = (byte)((mTrainState.g_EngineNumber.Number[2] - '0') << 4 + (mTrainState.g_EngineNumber.Number[3]-'0'));
			buffertx[txdatalength++] = (byte)((mTrainState.g_EngineNumber.Number[4] - '0') << 4 + (mTrainState.g_EngineNumber.Number[5]-'0'));
			buffertx[txdatalength++] = (byte)((mTrainState.g_EngineNumber.Number[6] - '0') << 4 + (mTrainState.g_EngineNumber.Number[7]-'0'));
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = GlobalPara.bCarType;
			buffertx[txdatalength++] = GlobalPara.bCarNumber;

			Calendar aCalendar = Calendar.getInstance();
			int iYear = aCalendar.get(Calendar.YEAR);
			int iMonth = aCalendar.get(Calendar.MONTH) + 1;
			int iDay = aCalendar.get(Calendar.DAY_OF_MONTH);
			int iHour = aCalendar.get(Calendar.HOUR_OF_DAY);
			int iMinutes = aCalendar.get(Calendar.MINUTE);
			int iSecond = aCalendar.get(Calendar.SECOND);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iYear - 2000);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iMonth);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iDay);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iHour);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iMinutes);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iSecond);

			if(GlobalPara.bLieWeiIsOklte  )
				buffertx[txdatalength++] = 0x01;
			else
				buffertx[txdatalength++] = 0x00;

			buffertx[txdatalength++] = (byte)((mTrainState.g_MainEngineNumber.Number[0] - '0') << 4 + (mTrainState.g_MainEngineNumber.Number[1]-'0'));
			buffertx[txdatalength++] = (byte)((mTrainState.g_MainEngineNumber.Number[2] - '0') << 4 + (mTrainState.g_MainEngineNumber.Number[3]-'0'));
			buffertx[txdatalength++] = (byte)((mTrainState.g_MainEngineNumber.Number[4] - '0') << 4 + (mTrainState.g_MainEngineNumber.Number[5]-'0'));
			buffertx[txdatalength++] = (byte)((mTrainState.g_MainEngineNumber.Number[6] - '0') << 4 + (mTrainState.g_MainEngineNumber.Number[7]-'0'));
			buffertx[txdatalength++] = 0x00;

			txdatalength += 10;

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}


	public static int GetFrame_QueryLieWeiIp(byte[] buffertx,int iOtherBoardFlag, TrainState mTrainState)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) iOtherBoardFlag; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = (byte)0x62;

			if(!GlobalPara.bLieWeiIsOklte )
				buffertx[txdatalength++] = 0;//本地列尾状态
			else
				buffertx[txdatalength++] = 1;//

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}
	public static int GetFrame_NotifyLieWeiIp(byte[] buffertx,int iOtherBoardFlag, TrainState mTrainState)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) iOtherBoardFlag; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = (byte)0x63;
			//ip
			buffertx[txdatalength++] = mTrainState.m_TailAddre[0] ;//0列尾ip
			buffertx[txdatalength++] = mTrainState.m_TailAddre[1] ;
			buffertx[txdatalength++] = mTrainState.m_TailAddre[2] ;
			buffertx[txdatalength++] = mTrainState.m_TailAddre[3] ;
			buffertx[txdatalength++] = mTrainState.m_TailAddre2[0] ;//4列尾ip
			buffertx[txdatalength++] = mTrainState.m_TailAddre2[1] ;
			buffertx[txdatalength++] = mTrainState.m_TailAddre2[2] ;
			buffertx[txdatalength++] = mTrainState.m_TailAddre2[3] ;

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}
	public static int GetFrame_ChangePocStateForTest(byte[] buffertx, int iOkOrNot)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) GlobalPara.iOtherBoardFlag; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = 0x65;// cmd
			buffertx[txdatalength++] = (byte) iOkOrNot;// 设置对方的poc状态,测试用
			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_RecStartServiceTime_01_36(byte[] buffertx, TrainState mTrainState, byte bDestCode)
	{

		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode;// 目的端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x01;// 业务
			buffertx[txdatalength++] = (byte) 0x36; // 命令:返回开机时间

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;

	}





	public static int GetFrame_InfoOfOneAudioForTest(byte[] buffertx,byte g_MainMMI)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) 0x07;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = g_MainMMI;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte)0xEB;
			buffertx[txdatalength++] = (byte)0x81;

			String strES = "朔黄LTE通话";
			byte[] tmp_Line_Name = strES.getBytes("GBK");
			int i = 0;
			for (i=0; i < tmp_Line_Name.length && i< 32; i++)
			{
				buffertx[txdatalength++] = tmp_Line_Name[i] ;//
			}
			for(;i<32;i++)
			{
				buffertx[txdatalength++] = ' ';
			}
			int num = 1000;
			buffertx[txdatalength++] = (byte)( num%256);
			buffertx[txdatalength++] = (byte)( num/256);
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = g_MainMMI;
			buffertx[txdatalength++] = 0x00;

			Calendar aCalendar = Calendar.getInstance();
			int iYear = aCalendar.get(Calendar.YEAR);
			int iMonth = aCalendar.get(Calendar.MONTH) + 1;
			int iDay = aCalendar.get(Calendar.DAY_OF_MONTH);
			int iHour = aCalendar.get(Calendar.HOUR_OF_DAY);
			int iMinutes = aCalendar.get(Calendar.MINUTE);
			int iSecond = aCalendar.get(Calendar.SECOND);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iYear - 2000);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iMonth);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iDay);

			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iHour);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iMinutes);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iSecond);

			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iHour);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iMinutes);
			buffertx[txdatalength++] = (byte)(GlobalFunc.WordToBCD(iSecond+15));

			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x00;

			strES = "朔黄-肃宁北";
			tmp_Line_Name = strES.getBytes("GBK");
			i = 0;
			for (i=0; i < tmp_Line_Name.length && i< 32; i++)
			{
				buffertx[txdatalength++] = tmp_Line_Name[i] ;//
			}
			for(;i<32;i++)
			{
				buffertx[txdatalength++] = ' ';
			}

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}

	public static int GetFrame_StartTime(byte[] buffertx, int iOtherBoardFlag, byte bCmd)
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) iOtherBoardFlag; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x01;
			buffertx[txdatalength++] = bCmd;// 0x69查询 0x70通知

			int iYear = GlobalPara.cStartCalendar.get(Calendar.YEAR);
			int iMonth = GlobalPara.cStartCalendar.get(Calendar.MONTH) + 1;
			int iDay = GlobalPara.cStartCalendar.get(Calendar.DAY_OF_MONTH);
			int iHour = GlobalPara.cStartCalendar.get(Calendar.HOUR_OF_DAY);
			int iMinutes = GlobalPara.cStartCalendar.get(Calendar.MINUTE);
			int iSecond = GlobalPara.cStartCalendar.get(Calendar.SECOND);

			/*
			 * buffertx[txdatalength++] = (byte) (((((iYear % 100) / 10) & 0x0F)  << 4) | ((iYear % 1000) & 0x0F)); buffertx[txdatalength++] =(byte) ((((iMonth / 10) & 0x0F) << 4) | ((iMonth % 10) & 0x0F));
			 * buffertx[txdatalength++] = (byte) ((((iDay / 10) & 0x0F) << 4) |  ((iDay % 10) & 0x0F)); buffertx[txdatalength++] = (byte) ((((iHour / 10) & 0x0F) << 4) | ((iHour % 10) & 0x0F));
			 * buffertx[txdatalength++] = (byte) ((((iMinutes / 10) & 0x0F) <<  4) | ((iMinutes % 10) & 0x0F)); buffertx[txdatalength++] = (byte)  ((((iSecond / 10) & 0x0F) << 4) | ((iSecond % 10) & 0x0F));
			 */
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iYear - 2000);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iMonth);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iDay);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iHour);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iMinutes);
			buffertx[txdatalength++] = GlobalFunc.WordToBCD(iSecond);

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;
	}
	public static int GetFrame_LteState_03_54(byte[] buffertx, TrainState mTrainState,byte bDestCode)//fes测试
	{
		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bDestCode;// 目的端
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = 0x03;// 业务
			buffertx[txdatalength++] = (byte) 0x54; //

			buffertx[txdatalength++] = (byte) 0x02;


			if(GlobalPara.iIpIsOk ==  0x01)
			{
				buffertx[txdatalength++] = 0x01;
			}
			else
			{
				buffertx[txdatalength++] = 0x00;
			}

			if(mTrainState.g_PocStateZhuBoard == 0x01)
			{
				buffertx[txdatalength++] = 0x01;
			}
			else
			{
				buffertx[txdatalength++] = 0x00;
			}

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " +Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return txdatalength;
	}
	public static int GetFrame_AtCommand_04_03(String szATCommand, byte[] buffertx, byte bPort)//fes测试
	{

		int txdatalength = 2;
		try
		{
			buffertx[txdatalength++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = bPort; // 目的端口
			buffertx[txdatalength++] = 0x00;
			buffertx[txdatalength++] = (byte) 0x04;
			buffertx[txdatalength++] = 0x03;//


			byte[] arr = szATCommand.getBytes();
			for (int i = 0; i < arr.length; i++)
			{
				buffertx[txdatalength++] = arr[i] ;//
			}

			buffertx[txdatalength++] = (byte) 0x0d;
			buffertx[txdatalength++] = (byte) 0x0a;//

			txdatalength += 2;
			buffertx[0] = (byte) ((txdatalength - 2) / 256);
			buffertx[1] = (byte) ((txdatalength - 2) % 256);
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
		return txdatalength;

	}
}

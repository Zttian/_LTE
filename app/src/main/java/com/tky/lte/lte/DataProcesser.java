//g_MainMMI=03对应MMI2,
//iZhuKongFlag=0x01对应LTE2(0x02)
//现在的作法:本务机注册车次功能号,补机不注册车次功能号
package com.tky.lte.lte;

import java.util.ArrayList;
import android.util.Log;


public class DataProcesser
{
	public LteHandle mLteHandle = null; // 处理LTE,相当于前GRPS模块+话音单元
	public TrainState mTrainState = null;
	public ParamOperation mParamOperation = null;
	public static String tag = "DataProcesser";
	public DataProcesser()
	{
		mParamOperation = ParamOperation.getInstance();
		mTrainState = TrainState.getInstance();
	}

	public void SendInfo(byte[] buffertx, int txdatalength, byte destport, byte ACK_Flag)
	{
		_SendData pSendData = null;
		byte[] SendBuf = new byte[1024] ;
		int SendLen;

		byte[] crc_result = new byte[2] ;
		GlobalFunc.cal_crc(buffertx, txdatalength - 2, crc_result);// 先算crc
		buffertx[txdatalength - 2] = crc_result[0] ;// CRC
		buffertx[txdatalength - 1] = crc_result[1] ;

		switch (destport)
		{
			default:
				LogInstance.error(GlobalPara.Tky, "error dest code:" + destport);
				break;
		}
		SendBuf = null;
		crc_result = null;
	}

	public void SendInfo(byte[] buffertx, int txdatalength, String DestinationIPAddress, String DestinationPort)
	{
		_GPRSSendData pGPRSSendData = new _GPRSSendData();
		byte[] SendBuf = new byte[1024] ;
		int SendLen;
		byte[] crc_result = new byte[2] ;
		GlobalFunc.cal_crc(buffertx, txdatalength - 2, crc_result);
		buffertx[txdatalength - 2] = crc_result[0] ;// CRC
		buffertx[txdatalength - 1] = crc_result[1] ;
		SendLen = GlobalFunc.FormSCITxFrame(buffertx, txdatalength, SendBuf);
		GlobalFunc.MemCpy(pGPRSSendData.buffertx, SendBuf, SendLen);
		pGPRSSendData.txdatalength = SendLen;
		pGPRSSendData.DestinationIPAddress = DestinationIPAddress;
		pGPRSSendData.DestinationPort = DestinationPort;
		pGPRSSendData.DelayTime = 0;
		mLteHandle.g_GPRSData_List.add(pGPRSSendData);
		synchronized (mLteHandle.g_GPRSSend_Event)
		{
			mLteHandle.g_GPRSSend_Event.notifyAll();
		}
		SendBuf = null;
		crc_result = null;

	}

	public boolean bNeedToSave = false;
	public void HandleReceiveData(byte[] buf, int len)// bHandleCode为外围单位填的目的代码,本机向外发填的源代码,01或7f
	{//不带1002 1003
		try
		{
			ConfigHelper aConfigHelper = ConfigHelper.getInstance();

			int i;
			int datalength;
			byte[] bufferrx = new byte[1024] ;
			byte[] buffertx = null;
			byte[] CrcResult = new byte[2] ;
			int rxdatalength, txdatalength = 0;

			_RawInfo PackageInfo = new _RawInfo();
			String DestinationIPAddress;
			for (i = 0; i < len; i++)
			{
				bufferrx[i] = buf[i] ;
			}
			PackageInfo.InfoLenth = buf[0] * 256 + buf[1] ;
			rxdatalength = len;

			if ((PackageInfo.InfoLenth != (rxdatalength - 2)))
			{//暂时注释,使用时需要打开.杨居丰音频板转发的列尾电台数据可能存在这个问题,长度不一致
				if (mTrainState.g_DebugLog_Lev2 != 0x00)
					LogInstance.error(GlobalPara.Tky, "32." + tag + ": HandleReceiveData: data length=" + rxdatalength + " is wrong, " + GlobalFunc.bytesToHexString2(bufferrx, PackageInfo.InfoLenth + 2));
			}

			GlobalFunc.cal_crc(bufferrx, rxdatalength, CrcResult);// crc校验,rxdatalength包含了最后两个crc字节,因此计算结果为0
			if ((CrcResult[0] == 0) && (CrcResult[1] == 0))
			{
				try
				{
					PackageInfo.SourCode = bufferrx[2] ;
					PackageInfo.SourAddreLenth = bufferrx[3] ;
					GlobalFunc.ZeroMemory(PackageInfo.SourAddre, 6);
					if ((PackageInfo.SourAddreLenth != 0) && (PackageInfo.SourAddreLenth != 4) && (PackageInfo.SourAddreLenth != 6))
					{// 调试提示:应该提示收到错协议
						if (mTrainState.g_DebugLog_Lev1 != 0x00)
							LogInstance.error(GlobalPara.Tky, "33." + tag + ": HandleReceiveData: sour address lenth is wrong:"+PackageInfo.SourAddreLenth);
						bufferrx = null;
						CrcResult = null;
						PackageInfo = null;
						return;
					}
					GlobalFunc.MemCpy(PackageInfo.SourAddre, 0, bufferrx, 4, PackageInfo.SourAddreLenth);
					PackageInfo.DectCode = bufferrx[4 + PackageInfo.SourAddreLenth] ;
					PackageInfo.DectAddreLenth = bufferrx[5 + PackageInfo.SourAddreLenth] ;
					GlobalFunc.ZeroMemory(PackageInfo.DectAddre, 6);
					if ((PackageInfo.DectAddreLenth != 0) && (PackageInfo.DectAddreLenth != 4) && (PackageInfo.DectAddreLenth != 6))
					{// 调试提示:应该提示收到错协议
						if (mTrainState.g_DebugLog_Lev1 != 0x00)
							LogInstance.error(GlobalPara.Tky, "34." + tag + ": HandleReceiveData: dest address lenth is wrong:"+PackageInfo.DectAddreLenth);
						bufferrx = null;
						CrcResult = null;
						PackageInfo = null;
						return;
					}
					GlobalFunc.MemCpy(PackageInfo.DectAddre, 0, bufferrx, 6 + PackageInfo.SourAddreLenth, PackageInfo.DectAddreLenth);
					PackageInfo.ServiceType = bufferrx[6 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth] ;
					PackageInfo.Command = bufferrx[7 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth] ;
					datalength = PackageInfo.InfoLenth - 8 - PackageInfo.SourAddreLenth - PackageInfo.DectAddreLenth;
					if (datalength > 0)
						GlobalFunc.MemCpy(PackageInfo.Data, 0, bufferrx, 8 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth, datalength);
					if(PackageInfo.ServiceType == (byte)0x15)
					{
						//给400K的存储数据,不处理
						if(mTrainState.g_DebugLog_Lev2 != 0x00)
						{
							LogInstance.debug(GlobalPara.Tky, "data to 400k for rec, don't handle: " + GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
						}
						bufferrx = null;
						CrcResult = null;
						PackageInfo = null;
						return;
					}

					{// 接收到为非应答信息, 处理数据
						switch (PackageInfo.DectCode)
						{// 根据目的端口代码分开处理
							case (byte) 0x00:/* 目的0x00 */
							case (byte) 0x01:/* 目的0x01 */
							case (byte) 0x7F:/* 目的0x7F */
							case (byte) 0x77:/* 目的0x77 */
								switch (PackageInfo.SourCode)
								{// 源端口号
									// ///////from mmi to 01,7F/////////mmi-01
									case (byte) 0x02:
									case (byte) 0x03:
									case (byte) 0x04:
										switch (PackageInfo.ServiceType)
										{
											case (byte) 0x01: // 业务类型维护
												switch (PackageInfo.Command)
												{
													case (byte) 0x30: // 设置DMIS归属IP地址 // have test
														subSetDMISAddre(PackageInfo.Data[0], PackageInfo.Data[1], PackageInfo.Data[2], PackageInfo.Data[3]);
														break;
													case (byte) 0x31: // 设置库检设备IP地址 // have test
														subSetCheckIP(PackageInfo);
														break;
													case (byte) 0x32: // 设置库检设备电话号码 // have test
														subSetCheckPhoneNumber(PackageInfo);
														break;
													case (byte) 0x35: // MMI设置主机当前时钟 // have test



														break;
													case (byte) 0xD3: // MMI设置SIP参数 // have test
														subSetSipParameter(PackageInfo);
														break;
													default:
														break;
												}
												break;
											case (byte) 0x03: // 业务类型:调度通信
												switch (PackageInfo.Command)
												{
													case (byte) 0x13: // 呼叫操作
														subApplyForCall(PackageInfo);
														break;
													case (byte) 0x09: // 摘/挂机//
														subApplyForHookAction(bufferrx, rxdatalength, PackageInfo);
														break;
													case (byte) 0x15: // 接通/保持/挂机电话
														subApplyForHoldCall(PackageInfo);
														break;
													case (byte) 0x0A: // MMI(PTT)操作
														subApplyForPttAction(bufferrx, rxdatalength, PackageInfo);
														break;
													case (byte) 0x11: // MMI向主机请求车次号注册或注销,或手动输入车次号//
													{
													}
													break;
													case (byte) 0x12: // MMI向主机请求机车号注册或注销,或手动输入车次号
													{

													}
													break;
													case (byte) 0x14: // MMI向主控LTE单元请求"主控机车号"设置、主控机车车功能号注册或注销
													{

													}
													break;
													case (byte) 0x16: // MMI(呼叫转移选择)操作
														subApplyForForwardCall(PackageInfo);
														break;
													case (byte) 0x20:// MMI向主机报告手动输入机车号信息 //
														subSetEngineNumber(PackageInfo);
														break;
													case (byte) 0x0F: // MMI向主控单元工控机发送机车号来源/归属GPRS接口服务器IP地址参数设置指令//
														subSetSomeParameter(PackageInfo, datalength);
														break;

												}
												break;
											case (byte) 0xF4: // MMI向主机请求线路电话本
												switch (PackageInfo.Command)
												{
													case (byte) 0x03: // 请求线路
														break;
													default:
														break;
												}
												break;
										}
										break;

								}
								break;

							// //from anyone to liewei/////////////// anyone-liewei
							case (byte) 0x14: // 发给列尾车载电台
							case (byte) 0x19: // 0x19已废弃
								subApplyForLieWeiFromMMI(bufferrx, rxdatalength, PackageInfo);
								break;

							case (byte) 0x25: // 调车监控应用服务器
								DestinationIPAddress = String.format("%d.%d.%d.%d", bufferrx[6 + PackageInfo.SourAddreLenth] & 0x00FF,
										bufferrx[7 + PackageInfo.SourAddreLenth] & 0x00FF, bufferrx[8 + PackageInfo.SourAddreLenth] & 0x00FF,
										bufferrx[9 + PackageInfo.SourAddreLenth] & 0x00FF);
								SendInfo(bufferrx, rxdatalength, DestinationIPAddress, GlobalPara.strCTCDestinationPort);
								break;

							default:// 转发
								if (mTrainState.g_DebugLog_Lev1 != 0x00)
									LogInstance.error(GlobalPara.Tky, "37." + tag + ": HandleReceiveData: can data should't zhuanfa: " + bufferrx[4 + PackageInfo.SourAddreLenth] + ":" + GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
								break;
						}
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
			else
			{
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
				{
					int minLen = rxdatalength < 128 ? rxdatalength : 128;
					Log.v(GlobalPara.Tky, "HandleReceiveData(crc error): "+GlobalFunc.bytesToHexString2(bufferrx, minLen));
				}
			}
			bufferrx = null;
			CrcResult = null;
			PackageInfo = null;
			buffertx = null;

			if(bNeedToSave)
				aConfigHelper.SaveToBakfile();
			bNeedToSave = false;
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

	public void HandleReceiveGprsData(byte[] buf, int len)
	{
		try
		{
			int i;
			int datalength;
			byte[] bufferrx = new byte[1024] ;
			byte[] buffertx = null;
			byte[] CrcResult = new byte[2] ;
			int rxdatalength, txdatalength;
			_RawInfo PackageInfo = new _RawInfo();

			for (i = 0; i < len; i++)
			{
				bufferrx[i] = buf[i] ;
			}
			PackageInfo.InfoLenth = (int) buf[0] * 256 + (int) buf[1] ;
			rxdatalength = len;
			if ((rxdatalength < 10) || (((int) bufferrx[0] * 256 + (int) bufferrx[1]) != (int) (rxdatalength - 2)))
			{
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "HandleReceiveGprsData: data lenth is wrong");
				return;
			}
			GlobalFunc.cal_crc(bufferrx, rxdatalength, CrcResult);// crc校验
			if ((CrcResult[0] == 0) && (CrcResult[1] == 0))
			{
				try
				{
					/****************** 结构体赋值 ***********/
					PackageInfo.SourCode = bufferrx[2] ;
					PackageInfo.SourAddreLenth = bufferrx[3] ;
					if (PackageInfo.SourAddreLenth != 0 && PackageInfo.SourAddreLenth != 4 && PackageInfo.SourAddreLenth != 6)
					{
						if (mTrainState.g_DebugLog_Lev1 != 0x00)
							LogInstance.debug(GlobalPara.Tky, "HandleReceiveGprsData: sour address lenth is wrong");
						return;
					}
					GlobalFunc.ZeroMemory(PackageInfo.SourAddre, 6);
					GlobalFunc.MemCpy(PackageInfo.SourAddre, 0, bufferrx, 4, PackageInfo.SourAddreLenth);
					// String DestinationIPAddress = String.format(("%d.%d.%d.%d"),PackageInfo.SourAddre[0], PackageInfo.SourAddre[1],
					// PackageInfo.SourAddre[2], PackageInfo.SourAddre[3]);
					// String DestinationPort = String.format(("%d"), PackageInfo.SourCode);
					PackageInfo.DectCode = bufferrx[4 + PackageInfo.SourAddreLenth] ;
					PackageInfo.DectAddreLenth = bufferrx[5 + PackageInfo.SourAddreLenth] ;
					if (PackageInfo.DectAddreLenth != 0 && PackageInfo.DectAddreLenth != 4 && PackageInfo.DectAddreLenth != 6)
					{
						if (mTrainState.g_DebugLog_Lev1 != 0x00)
							LogInstance.debug(GlobalPara.Tky, "HandleReceiveGprsData: dest address lenth is wrong");
						return;
					}
					GlobalFunc.ZeroMemory(PackageInfo.DectAddre, 6);
					GlobalFunc.MemCpy(PackageInfo.DectAddre, 0, bufferrx, 6 + PackageInfo.SourAddreLenth, PackageInfo.DectAddreLenth);
					PackageInfo.ServiceType = bufferrx[6 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth] ;
					PackageInfo.Command = bufferrx[7 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth] ;
					datalength = PackageInfo.InfoLenth - 8 - PackageInfo.SourAddreLenth - PackageInfo.DectAddreLenth;
					if (datalength > 0)
						GlobalFunc.MemCpy(PackageInfo.Data, 0, bufferrx, 8 + PackageInfo.SourAddreLenth + PackageInfo.DectAddreLenth, datalength);
					// 接收到目标端口是主机的数据，向记录单元转发
					// if (PackageInfo.DectCode == 0x01)
					// {
					// buffertx[0] = (byte) 0xff;
					// buffertx[1] = (byte) 0xff;
					// for (i = 2; i < rxdatalength; i++)
					// buffertx[i] = bufferrx[i] ;
					// SendInfo(buffertx, rxdatalength, (byte) 0x07, (byte) 1);
					// }
					switch (PackageInfo.DectCode)
					{
						case (byte) 0x01: // 发送给主控单元
						case (byte) 0x7F: // 发送给主控单元
						case (byte) 0x02:
						case (byte) 0x03:
						case (byte) 0x04:
							switch (PackageInfo.SourCode)
							{
								case (byte) 0x27: // 来源GRIS接口服务器
									subApplyForGROS(PackageInfo);
									break;
								case (byte) 0x14:// 来源成都列尾
									subApplyForLieWeiByGprs(bufferrx, rxdatalength, PackageInfo);
									break;
								case 0x23:// 来源CTC
								case 0x24:// 来源DIMS
								case 0x31:// 来源出入库库检
									switch (PackageInfo.ServiceType)
									{
										case (byte) 0x05: // 车次号信息
											if (mTrainState.g_DebugLog_Lev1 != 0x00)
												LogInstance.debug(GlobalPara.Tky, "receive query train number command from ctc, dmis or liewei");

											if (PackageInfo.Command == (byte) 0x80) // DMIS/CTC申请CIR输出车次号信息
											{
												buffertx = new byte[32] ;
												txdatalength = FrameGenerate.GetFrame_RequestCheCiHaoInfo_05_08(buffertx);
												SendInfo(buffertx, txdatalength, (byte) 0x7E, (byte) 1);
											}
											break;
										case (byte) 0x11: // CTC 向MMI发送图形方式列车进路自动预告信息
											if (mTrainState.g_DebugLog_Lev1 != 0x00)
												LogInstance.debug(GlobalPara.Tky, "receive jin lu yu gao info from ctc");
											SendInfo(bufferrx, rxdatalength, (byte) bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);
											break;
										case (byte) 0x12: // CTC维护数据信息
											for (i = 0; i < 32; i++)
												mTrainState.g_CTCMaintenanceInfo[i] = PackageInfo.Data[i] ;
											break;
										case (byte) 0x13:// 库检业务
											subApplyForKuJianGprs(PackageInfo);
											break;

										default:
											SendInfo(bufferrx, rxdatalength, (byte) bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);
											break;
									}
									break;
								default:
									SendInfo(bufferrx, rxdatalength, (byte) bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);
									break;
							}
							break;
						case (byte) 0x11://DMIS总机发送给数据采集处理编码器发送数据
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "HandleReceiveGprsData: from 0x11");
							SendInfo(bufferrx, rxdatalength, bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);
							break;
						case (byte) 0x12:
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "HandleReceiveGprsData: from 0x12");
							SendInfo(bufferrx, rxdatalength, (byte) 0x12, (byte) 1); // 将收到的数据进行转发
							break;

						default:
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "HandleReceiveGprsData: zhuanfa data to: " +Integer.toHexString( bufferrx[4 + PackageInfo.SourAddreLenth] )+ ":"
										+ GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
							SendInfo(bufferrx, rxdatalength, bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);//localtest
							LogInstance.error(GlobalPara.Tky,"localtest zhuanfa:"+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
							break;
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
			else
			{
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
				{
					int minLen = rxdatalength < 128 ? rxdatalength : 128;
					Log.v(GlobalPara.Tky, "HandleReceiveGprsData(crc error): "+GlobalFunc.bytesToHexString2(bufferrx, minLen));
				}
			}
			bufferrx = null;
			CrcResult = null;
			PackageInfo = null;
			buffertx = null;
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

	public void subSetCheckPhoneNumber(_RawInfo PackageInfo)
	{
		try
		{
			if (PackageInfo.InfoLenth - 8 - PackageInfo.SourAddreLenth - PackageInfo.DectAddreLenth > 0)
			{//set
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "receive and set check number from mmi");
				mTrainState.Set_g_CheckPhoneNumber(PackageInfo);
				mParamOperation.SaveSpecialField("CHECKPHONENUMBER1", mTrainState.g_CheckPhoneNumber1);
				mParamOperation.SaveSpecialField("CHECKPHONENUMBER2", mTrainState.g_CheckPhoneNumber2);
				mParamOperation.SaveSpecialField("CHECKPHONENUMBER3", mTrainState.g_CheckPhoneNumber3);
				bNeedToSave = true;
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
				{
					LogInstance.debug(GlobalPara.Tky, "receive and set CHECKPHONENUMBER1=" + mTrainState.g_CheckPhoneNumber1 + " from mmi");
					LogInstance.debug(GlobalPara.Tky, "receive and set CHECKPHONENUMBER2=" + mTrainState.g_CheckPhoneNumber2 + " from mmi");
					LogInstance.debug(GlobalPara.Tky, "receive and set CHECKPHONENUMBER3=" + mTrainState.g_CheckPhoneNumber3 + " from mmi");
					//LogInstance.debug(GlobalPara.Tky, "check phone number: " + GlobalFunc.bytesToHexString2(PackageInfo.Data, PackageInfo.InfoLenth - 8 - PackageInfo.SourAddreLenth - PackageInfo.DectAddreLenth));
				}
			}
			else
			{// ==0代表查询
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "receive and query CHECKPHONENUMBER from mmi");
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

	public void subSetCheckIP(_RawInfo PackageInfo)
	{
		try
		{
			if (PackageInfo.InfoLenth - 8 - PackageInfo.SourAddreLenth - PackageInfo.DectAddreLenth > 0)
			{// 设置
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "receive and set check ip from mmi");

				mTrainState.Set_m_CheckIP(PackageInfo.Data[0], PackageInfo.Data[1], PackageInfo.Data[2], PackageInfo.Data[3], 1);
				mParamOperation.SaveSpecialField("CHECKIP1", mTrainState.g_CheckIP1);
				mTrainState.Set_m_CheckIP(PackageInfo.Data[4], PackageInfo.Data[5], PackageInfo.Data[6], PackageInfo.Data[7], 2);
				mParamOperation.SaveSpecialField("CHECKIP2", mTrainState.g_CheckIP2);
				mTrainState.Set_m_CheckIP(PackageInfo.Data[8], PackageInfo.Data[9], PackageInfo.Data[10], PackageInfo.Data[11], 3);
				mParamOperation.SaveSpecialField("CHECKIP3", mTrainState.g_CheckIP3);

				bNeedToSave = true;
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
				{
					LogInstance.debug(GlobalPara.Tky, "receive and set CHECKIP1=" + mTrainState.g_CheckIP1 + " from mmi");
					LogInstance.debug(GlobalPara.Tky, "receive and set CHECKIP2=" + mTrainState.g_CheckIP2 + " from mmi");
					LogInstance.debug(GlobalPara.Tky, "receive and set CHECKIP3=" + mTrainState.g_CheckIP3 + " from mmi");
					//LogInstance.debug(GlobalPara.Tky, "check ip: " + GlobalFunc.bytesToHexString2(PackageInfo.Data, PackageInfo.InfoLenth - 8 - PackageInfo.SourAddreLenth - PackageInfo.DectAddreLenth));
				}
			}
			else
			{//==0代表查询
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "receive and query check ip from mmi");
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

	public void subSetDMISAddre(byte p1, byte p2, byte p3, byte p4)
	{
		try
		{
			mTrainState.Set_m_HomeDMISAddre(p1, p2, p3, p4);
			String DMISIPAddress = String.format(("%d.%d.%d.%d"), p1 & 0x00FF, p2 & 0x00FF, p3 & 0x00FF, p4 & 0x00FF);
			mParamOperation.SaveSpecialField("DMISIPAddress", DMISIPAddress);
			bNeedToSave = true;
			if (mTrainState.g_DebugLog_Lev1 != 0x00)
				LogInstance.debug(GlobalPara.Tky, "receive and set DMIS=" + DMISIPAddress + " from mmi");
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


	public void subApplyForForwardCall(_RawInfo PackageInfo)
	{
		if (mTrainState.g_DebugLog_Lev1 != 0x00)
			LogInstance.debug(GlobalPara.Tky,"receive forward call action from mmi in lte mode");
		if (mLteHandle != null)
		{
			mLteHandle.HandleForMMIForwardCall_16(PackageInfo);
		}
	}

	public void subApplyForHoldCall(_RawInfo PackageInfo)
	{
		if (mTrainState.g_DebugLog_Lev1 != 0x00)
			LogInstance.debug(GlobalPara.Tky,"receive hold call action from mmi");
		if (mLteHandle != null)
		{
			mLteHandle.DealCall(PackageInfo.Data[0], PackageInfo.Data[1]);
		}
	}

	public void subSetSipParameter(_RawInfo PackageInfo)
	{
		try
		{
			if (PackageInfo.InfoLenth - 8 - PackageInfo.SourAddreLenth - PackageInfo.DectAddreLenth > 0)
			{
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "receive and set sip parameter from mmi");

				int p1 = PackageInfo.Data[0] ;
				int p2 = PackageInfo.Data[1] ;
				int p3 = PackageInfo.Data[2] ;
				int p4 = PackageInfo.Data[3] ;
				mTrainState.mLTE_SIP_IP = String.format("%d.%d.%d.%d", p1 & 0x00FF, p2 & 0x00FF, p3 & 0x00FF, p4 & 0x00FF);
				mParamOperation.SaveSpecialField("Sip_Ip", mTrainState.mLTE_SIP_IP);
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "mTrainState.mLTE_SIP_IP:" + mTrainState.mLTE_SIP_IP);

				p1 = PackageInfo.Data[4] ;
				p2 = PackageInfo.Data[5] ;
				p3 = PackageInfo.Data[6] ;
				p4 = PackageInfo.Data[7] ;
				mTrainState.mLTE_SIP_IP2 = String.format("%d.%d.%d.%d", p1 & 0x00FF, p2 & 0x00FF, p3 & 0x00FF, p4 & 0x00FF);
				mParamOperation.SaveSpecialField("Sip_Ip2", mTrainState.mLTE_SIP_IP2);
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "mTrainState.mLTE_SIP_IP2:" + mTrainState.mLTE_SIP_IP2);

				int index = 8;

				if (GlobalPara.iZhuKongFlag == (byte) 0x01)
				{//20150128// 参数:前面的为一号板的用户名密码,后面的为二号板的用户名密码
					index = mTrainState.Set_mLte_SIPVOP(PackageInfo, index, 1);
					index = mTrainState.Set_mLte_SIPVOP(PackageInfo, index, 2);
					index = mTrainState.Set_mLte_SIPVOP(PackageInfo, index, 3);
					index = mTrainState.Set_mLte_SIPVOP(PackageInfo, index, 4);

					mParamOperation.SaveSpecialField("UserName", mTrainState.mLTE_UserName);
					mParamOperation.SaveSpecialField("PassWord", mTrainState.mLTE_PassWord);
					mParamOperation.SaveSpecialField("UserName2", mTrainState.mLTE_UserName2);
					mParamOperation.SaveSpecialField("PassWord2", mTrainState.mLTE_PassWord2);

					//单独记录sip账号
					ArrayList<NameValueInstance> lstInstances = new ArrayList<NameValueInstance>();
					NameValueInstance aNameValueInstance = new NameValueInstance("UserName", mTrainState.mLTE_UserName);
					lstInstances.add(aNameValueInstance);
					NameValueInstance aNameValueInstance2 = new NameValueInstance("PassWord", mTrainState.mLTE_PassWord);
					lstInstances.add(aNameValueInstance2);
					NameValueInstance aNameValueInstance3 = new NameValueInstance("UserName2", mTrainState.mLTE_UserName2);
					lstInstances.add(aNameValueInstance3);
					NameValueInstance aNameValueInstance4 = new NameValueInstance("PassWord2", mTrainState.mLTE_PassWord2);
					lstInstances.add(aNameValueInstance4);
					ConfigHelper.getInstance().SetParameterForLogNet(lstInstances);

					if (mTrainState.g_DebugLog_Lev1 != 0x00)
					{
						LogInstance.debug(GlobalPara.Tky, "mTrainState.mLTE_UserName:" + mTrainState.mLTE_UserName);
						LogInstance.debug(GlobalPara.Tky, "mTrainState.mLTE_PassWord:" + mTrainState.mLTE_PassWord);
						LogInstance.debug(GlobalPara.Tky, "mTrainState.mLTE_UserName2:" + mTrainState.mLTE_UserName2);
						LogInstance.debug(GlobalPara.Tky, "mTrainState.mLTE_PassWord2:" + mTrainState.mLTE_PassWord2);
					}
				}
				else if (GlobalPara.iZhuKongFlag == (byte) 0x7F)
				{//20150128// 参数:前面的为一号板的用户名密码,后面的为二号板的用户名密码,所以要调换一下
					index = mTrainState.Set_mLte_SIPVOP(PackageInfo, index, 1);
					index = mTrainState.Set_mLte_SIPVOP(PackageInfo, index, 2);
					index = mTrainState.Set_mLte_SIPVOP(PackageInfo, index, 3);
					index = mTrainState.Set_mLte_SIPVOP(PackageInfo, index, 4);

					mParamOperation.SaveSpecialField("UserName", mTrainState.mLTE_UserName);
					mParamOperation.SaveSpecialField("PassWord", mTrainState.mLTE_PassWord);
					mParamOperation.SaveSpecialField("UserName2", mTrainState.mLTE_UserName2);
					mParamOperation.SaveSpecialField("PassWord2", mTrainState.mLTE_PassWord2);

					//单独记录sip账号
					ArrayList<NameValueInstance> lstInstances = new ArrayList<NameValueInstance>();
					NameValueInstance aNameValueInstance = new NameValueInstance("UserName", mTrainState.mLTE_UserName);
					lstInstances.add(aNameValueInstance);
					NameValueInstance aNameValueInstance2 = new NameValueInstance("PassWord", mTrainState.mLTE_PassWord);
					lstInstances.add(aNameValueInstance2);
					NameValueInstance aNameValueInstance3 = new NameValueInstance("UserName2", mTrainState.mLTE_UserName2);
					lstInstances.add(aNameValueInstance3);
					NameValueInstance aNameValueInstance4 = new NameValueInstance("PassWord2", mTrainState.mLTE_PassWord2);
					lstInstances.add(aNameValueInstance4);
					ConfigHelper.getInstance().SetParameterForLogNet(lstInstances);

					String tmpString = "";// 用户名调换
					tmpString = mTrainState.mLTE_UserName;
					mTrainState.mLTE_UserName = mTrainState.mLTE_UserName2;// 第二个用户名
					mTrainState.mLTE_UserName2 = tmpString;
					tmpString = mTrainState.mLTE_PassWord;// 密码调换
					mTrainState.mLTE_PassWord = mTrainState.mLTE_PassWord2;// 第二个密码
					mTrainState.mLTE_PassWord2 = tmpString;
					if (mTrainState.g_DebugLog_Lev1 != 0x00)
					{
						LogInstance.debug(GlobalPara.Tky, "mTrainState.mLTE_UserName:" + mTrainState.mLTE_UserName);
						LogInstance.debug(GlobalPara.Tky, "mTrainState.mLTE_PassWord:" + mTrainState.mLTE_PassWord);
						LogInstance.debug(GlobalPara.Tky, "mTrainState.mLTE_UserName2:" + mTrainState.mLTE_UserName2);
						LogInstance.debug(GlobalPara.Tky, "mTrainState.mLTE_PassWord2:" + mTrainState.mLTE_PassWord2);
					}
				}


				bNeedToSave = true;
			}
			else
			{
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "receive and query sip parameter from mmi");
			}

			if (PackageInfo.InfoLenth - 8 - PackageInfo.SourAddreLenth - PackageInfo.DectAddreLenth > 0)
			{
				if (mLteHandle != null && mLteHandle.mblLTE_IP )
				{
					if (mTrainState.g_DebugLog_Lev1 != 0x00)
						LogInstance.debug(GlobalPara.Tky, "sip parameter change, so need to logout and login on poc sever again");
					mLteHandle.POC_UnRegister();
					mLteHandle.POC_Register();
				}
				else
				{
					if (mTrainState.g_DebugLog_Lev1 != 0x00)
						LogInstance.debug(GlobalPara.Tky, "can't login on poc sever again because of haven't ip address or mltehandle=null");
				}
			}
			else
			{
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "can't login on poc sever again because of sip parameter haven't change");
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

	public void subApplyForLieWeiFromMMI(byte[] bufferrx, int rxdatalength, _RawInfo PackageInfo)
	{
		if(mTrainState.g_DebugLog_Lev1!=0x00)
		{
			if(PackageInfo.Command == (byte) 0x41 /* || PackageInfo.Command == (byte) 0x2F */)
			{//20150210
				if(mTrainState.g_DebugLog_Lev2!=0x00)
					LogInstance.debug(GlobalPara.Tky/*"liewei"*/, "(lwdata)mmi to liewei: "+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
			}
			else
			{
				LogInstance.debug(GlobalPara.Tky/*"liewei"*/, "(lwdata)mmi to liewei: "+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
			}
		}
		String DestinationIPAddress = null;
		switch (PackageInfo.SourCode)
		{
			case (byte) 0x02:
			case (byte) 0x03:
			case (byte) 0x04:
				switch (PackageInfo.ServiceType)
				{
					case (byte) 0x04:// 风压业务
						switch (PackageInfo.Command)
						{
							case (byte) 0x41: //应答
								if(bufferrx[3]!=0x00)
								{
									bufferrx[4] = mTrainState.m_SourLteAddre[0] ;
									bufferrx[5] = mTrainState.m_SourLteAddre[1] ;
									bufferrx[6] = mTrainState.m_SourLteAddre[2] ;
									bufferrx[7] = mTrainState.m_SourLteAddre[3] ;
								}
								if(/*mTrainState.g_Mode == (byte) 0x66 &&*/ mLteHandle.mblLTE_IP && GlobalPara.bLieWeiIsOklte
										 )
								{
									DestinationIPAddress = String.format("%d.%d.%d.%d", bufferrx[10] & 0x00FF, bufferrx[11] & 0x00FF,
											bufferrx[12] & 0x00FF, bufferrx[13] & 0x00FF);
									if(mTrainState.g_DebugLog_Lev2!=0x00)
										LogInstance.debug(GlobalPara.Tky, "(lwdata)receive ying da 0x41 ack from mmi to lte liewei:"+DestinationIPAddress);
									SendInfo(bufferrx, rxdatalength, DestinationIPAddress, GlobalPara.strLieWeiDestinationPort);
								}
								break;
							case (byte) 0x91: //风压查询
								if(mTrainState.g_DebugLog_Lev1!=0x00)
									LogInstance.debug(GlobalPara.Tky, "(lwdata)receive feng ya cha xun from mmi to liewei, command = 0x91");

								if(bufferrx[3]!=0x00)
								{
									bufferrx[4] = mTrainState.m_SourLteAddre[0] ;
									bufferrx[5] = mTrainState.m_SourLteAddre[1] ;
									bufferrx[6] = mTrainState.m_SourLteAddre[2] ;
									bufferrx[7] = mTrainState.m_SourLteAddre[3] ;
								}

								bufferrx[16+bufferrx[3]+bufferrx[5+bufferrx[3]]] = GlobalPara.bCarType;
								bufferrx[17+bufferrx[3]+bufferrx[5+bufferrx[3]]] = GlobalPara.bCarNumber;
								if (/*mTrainState.g_Mode == 0x66 && */ mLteHandle.mblLTE_IP && GlobalPara.bLieWeiIsOklte )
									bufferrx[24+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x01;
								else
									bufferrx[24+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x00;

								if(/*mTrainState.g_Mode == (byte) 0x66 &&*/ mLteHandle.mblLTE_IP && GlobalPara.bLieWeiIsOklte
										 )
								{
									DestinationIPAddress = String.format("%d.%d.%d.%d", mTrainState.m_TailAddre[0] & 0x00FF, mTrainState.m_TailAddre[1] & 0x00FF,
											mTrainState.m_TailAddre[2] & 0x00FF, mTrainState.m_TailAddre[3] & 0x00FF);
									if(!(mTrainState.m_TailAddre[0]==0x00 && mTrainState.m_TailAddre[1]==0x00
											&& mTrainState.m_TailAddre[2]==0x00 && mTrainState.m_TailAddre[3]==0x00))
										SendInfo(bufferrx, rxdatalength, DestinationIPAddress, GlobalPara.strLieWeiDestinationPort);

									DestinationIPAddress = String.format("%d.%d.%d.%d", mTrainState.m_TailAddre2[0] & 0x00FF, mTrainState.m_TailAddre2[1] & 0x00FF,
											mTrainState.m_TailAddre2[2] & 0x00FF, mTrainState.m_TailAddre2[3] & 0x00FF);
									if(!(mTrainState.m_TailAddre2[0]==0x00 && mTrainState.m_TailAddre2[1]==0x00
											&& mTrainState.m_TailAddre2[2]==0x00 && mTrainState.m_TailAddre2[3]==0x00))
										SendInfo(bufferrx, rxdatalength, DestinationIPAddress, GlobalPara.strLieWeiDestinationPort);
								}
								break;
							case (byte) 0x24: //常用排风
								if(mTrainState.g_DebugLog_Lev1!=0x00)
									LogInstance.debug(GlobalPara.Tky, "(lwdata)receive chang yong pai feng from mmi to liewei, command = 0x24");

								if(bufferrx[3]!=0x00)
								{
									bufferrx[4] = mTrainState.m_SourLteAddre[0] ;
									bufferrx[5] = mTrainState.m_SourLteAddre[1] ;
									bufferrx[6] = mTrainState.m_SourLteAddre[2] ;
									bufferrx[7] = mTrainState.m_SourLteAddre[3] ;
								}

								//20140104//修改机车属性和编组数量
								bufferrx[16+bufferrx[3]+bufferrx[5+bufferrx[3]]] = GlobalPara.bCarType;
								bufferrx[17+bufferrx[3]+bufferrx[5+bufferrx[3]]] = GlobalPara.bCarNumber;
								if (/*mTrainState.g_Mode == 0x66 &&*/mLteHandle.mblLTE_IP && GlobalPara.bLieWeiIsOklte  )
									bufferrx[24+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x01;
								else
									bufferrx[24+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x00;

								if(/*mTrainState.g_Mode == (byte) 0x66 &&*/ mLteHandle.mblLTE_IP && GlobalPara.bLieWeiIsOklte
										 )
								{
									DestinationIPAddress = String.format("%d.%d.%d.%d", mTrainState.m_TailAddre[0] & 0x00FF, mTrainState.m_TailAddre[1] & 0x00FF,
											mTrainState.m_TailAddre[2] & 0x00FF, mTrainState.m_TailAddre[3] & 0x00FF);
									if(!(mTrainState.m_TailAddre[0]==0x00 && mTrainState.m_TailAddre[1]==0x00
											&& mTrainState.m_TailAddre[2]==0x00 && mTrainState.m_TailAddre[3]==0x00))
										SendInfo(bufferrx, rxdatalength, DestinationIPAddress, GlobalPara.strLieWeiDestinationPort);

									DestinationIPAddress = String.format("%d.%d.%d.%d", mTrainState.m_TailAddre2[0] & 0x00FF, mTrainState.m_TailAddre2[1] & 0x00FF,
											mTrainState.m_TailAddre2[2] & 0x00FF, mTrainState.m_TailAddre2[3] & 0x00FF);
									if(!(mTrainState.m_TailAddre2[0]==0x00 && mTrainState.m_TailAddre2[1]==0x00
											&& mTrainState.m_TailAddre2[2]==0x00 && mTrainState.m_TailAddre2[3]==0x00))
										SendInfo(bufferrx, rxdatalength, DestinationIPAddress, GlobalPara.strLieWeiDestinationPort);
								}
								break;
							case (byte) 0x26: //紧急排风
								if(mTrainState.g_DebugLog_Lev1!=0x00)
									LogInstance.debug(GlobalPara.Tky, "(lwdata)receive jin ji pai feng from mmi to liewei, command = 0x26");
								if(bufferrx[3]!=0x00)
								{
									bufferrx[4] = mTrainState.m_SourLteAddre[0] ;
									bufferrx[5] = mTrainState.m_SourLteAddre[1] ;
									bufferrx[6] = mTrainState.m_SourLteAddre[2] ;
									bufferrx[7] = mTrainState.m_SourLteAddre[3] ;
								}

								//20140104//修改机车属性和编组数量
								bufferrx[16+bufferrx[3]+bufferrx[5+bufferrx[3]]] = GlobalPara.bCarType;
								bufferrx[17+bufferrx[3]+bufferrx[5+bufferrx[3]]] = GlobalPara.bCarNumber;
								if (/*mTrainState.g_Mode == 0x66 &&*/ mLteHandle.mblLTE_IP && GlobalPara.bLieWeiIsOklte )
									bufferrx[24+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x01;
								else
									bufferrx[24+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x00;

								if(/*mTrainState.g_Mode == (byte) 0x66 &&*/ mLteHandle.mblLTE_IP && GlobalPara.bLieWeiIsOklte
										 )
								{
									DestinationIPAddress = String.format("%d.%d.%d.%d", mTrainState.m_TailAddre[0] & 0x00FF, mTrainState.m_TailAddre[1] & 0x00FF,
											mTrainState.m_TailAddre[2] & 0x00FF, mTrainState.m_TailAddre[3] & 0x00FF);
									if(!(mTrainState.m_TailAddre[0]==0x00 && mTrainState.m_TailAddre[1]==0x00
											&& mTrainState.m_TailAddre[2]==0x00 && mTrainState.m_TailAddre[3]==0x00))
										SendInfo(bufferrx, rxdatalength, DestinationIPAddress, GlobalPara.strLieWeiDestinationPort);

									DestinationIPAddress = String.format("%d.%d.%d.%d", mTrainState.m_TailAddre2[0] & 0x00FF, mTrainState.m_TailAddre2[1] & 0x00FF,
											mTrainState.m_TailAddre2[2] & 0x00FF, mTrainState.m_TailAddre2[3] & 0x00FF);
									if(!(mTrainState.m_TailAddre2[0]==0x00 && mTrainState.m_TailAddre2[1]==0x00
											&& mTrainState.m_TailAddre2[2]==0x00 && mTrainState.m_TailAddre2[3]==0x00))
										SendInfo(bufferrx, rxdatalength, DestinationIPAddress, GlobalPara.strLieWeiDestinationPort);
								}
								break;
							case (byte) 0x83: //MMI发销号命令
								if(mTrainState.g_DebugLog_Lev1!=0x00)
									LogInstance.debug(GlobalPara.Tky, "(lwdata)receive xiao hao command from mmi to liewei, command = 0x83");
								if(bufferrx[3]!=0x00)
								{
									bufferrx[4] = mTrainState.m_SourLteAddre[0] ;
									bufferrx[5] = mTrainState.m_SourLteAddre[1] ;
									bufferrx[6] = mTrainState.m_SourLteAddre[2] ;
									bufferrx[7] = mTrainState.m_SourLteAddre[3] ;
								}

								bufferrx[16+bufferrx[3]+bufferrx[5+bufferrx[3]]] = GlobalPara.bCarType;
								bufferrx[17+bufferrx[3]+bufferrx[5+bufferrx[3]]] = GlobalPara.bCarNumber;
								if (/*mTrainState.g_Mode == 0x66 &&*/mLteHandle.mblLTE_IP && GlobalPara.bLieWeiIsOklte  )
									bufferrx[24+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x01;
								else
									bufferrx[24+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x00;

								if(/*mTrainState.g_Mode == (byte) 0x66 &&*/ mLteHandle.mblLTE_IP && GlobalPara.bLieWeiIsOklte
										 )
								{
									DestinationIPAddress = String.format("%d.%d.%d.%d", mTrainState.m_TailAddre[0] & 0x00FF, mTrainState.m_TailAddre[1] & 0x00FF,
											mTrainState.m_TailAddre[2] & 0x00FF, mTrainState.m_TailAddre[3] & 0x00FF);
									if(!(mTrainState.m_TailAddre[0]==0x00 && mTrainState.m_TailAddre[1]==0x00
											&& mTrainState.m_TailAddre[2]==0x00 && mTrainState.m_TailAddre[3]==0x00))
										SendInfo(bufferrx, rxdatalength, DestinationIPAddress, GlobalPara.strLieWeiDestinationPort);

									DestinationIPAddress = String.format("%d.%d.%d.%d", mTrainState.m_TailAddre2[0] & 0x00FF, mTrainState.m_TailAddre2[1] & 0x00FF,
											mTrainState.m_TailAddre2[2] & 0x00FF, mTrainState.m_TailAddre2[3] & 0x00FF);
									if(!(mTrainState.m_TailAddre2[0]==0x00 && mTrainState.m_TailAddre2[1]==0x00
											&& mTrainState.m_TailAddre2[2]==0x00 && mTrainState.m_TailAddre2[3]==0x00))
										SendInfo(bufferrx, rxdatalength, DestinationIPAddress, GlobalPara.strLieWeiDestinationPort);
								}
								break;
							case (byte) 0xB9: //CIR回送连接请求确认信息
								if(PackageInfo.Data[17]==0x01)
								{//司机确认通过
									GlobalPara.lastLieweiTimelte = System.currentTimeMillis();
									GlobalPara.arrLieWeiZhuangZhi[0] = PackageInfo.Data[5] ;
									GlobalPara.arrLieWeiZhuangZhi[1] = PackageInfo.Data[6] ;
									GlobalPara.arrLieWeiZhuangZhi[2] = PackageInfo.Data[7] ;
									GlobalPara.arrDteZhuangZhi[0] = GlobalPara.arrLieWeiZhuangZhi[0] ;
									GlobalPara.arrDteZhuangZhi[1] = GlobalPara.arrLieWeiZhuangZhi[1] ;
									GlobalPara.arrDteZhuangZhi[2] = GlobalPara.arrLieWeiZhuangZhi[2] ;

									String strZhuangzhi = "("+Integer.toHexString(GlobalPara.arrLieWeiZhuangZhi[0]) +","+Integer.toHexString(GlobalPara.arrLieWeiZhuangZhi[1] & 0xFF)+","+Integer.toHexString(GlobalPara.arrLieWeiZhuangZhi[2] & 0xFF)+")";
									LogInstance.debug(GlobalPara.Tky, "(lwdata)B9:"+strZhuangzhi);
									if(mTrainState.g_DebugLog_Lev1!=0x00)
										LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie lian jie response successful from mmi to liewei, command = 0xB9");

									GlobalPara.bLieWeiIsOklte = true;
									GlobalPara.ilwlte = 1;
									if(mTrainState.g_DebugLog_Lev1!=0x00)
										LogInstance.debug(GlobalPara.Tky,"(lwdata)liewei connected now8");


									String strLieWeiNumber = String.format("%c%c%c%c%c%c",
											((((GlobalPara.arrDteZhuangZhi[0] & 0xFF) >> 4) & 0x0F ) + '0'),(((GlobalPara.arrDteZhuangZhi[0] & 0xFF) & 0x0F) + '0')
											,((((GlobalPara.arrDteZhuangZhi[1] & 0xFF) >> 4) & 0x0F )+ '0'),(((GlobalPara.arrDteZhuangZhi[1] & 0xFF) & 0x0F)+ '0')
											,((((GlobalPara.arrDteZhuangZhi[2] & 0xFF) >> 4) & 0x0F )+ '0'),(((GlobalPara.arrDteZhuangZhi[2] & 0xFF) & 0x0F)+ '0'));
									mParamOperation.SaveSpecialField("LieWeiNumber", strLieWeiNumber);
									LogInstance.debug(GlobalPara.Tky, "(lwdata)save LieWeiNumber="+strLieWeiNumber);
									bNeedToSave  =  true;
								}
								else
								{
									GlobalPara.arrLieWeiZhuangZhi[0] = 0x00;
									GlobalPara.arrLieWeiZhuangZhi[1] = 0x00;
									GlobalPara.arrLieWeiZhuangZhi[2] = 0x00;
									GlobalPara.arrDteZhuangZhi[0] = GlobalPara.arrLieWeiZhuangZhi[0] ;
									GlobalPara.arrDteZhuangZhi[1] = GlobalPara.arrLieWeiZhuangZhi[1] ;
									GlobalPara.arrDteZhuangZhi[2] = GlobalPara.arrLieWeiZhuangZhi[2] ;
									LogInstance.debug(GlobalPara.Tky, "(lwdata)zhuang zhi hao:000000, ju jue liewei lian jie request, be attention");
									if(mTrainState.g_DebugLog_Lev1!=0x00)
										LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie lian jie response failure from mmi to liewei, command = 0xB9");

									GlobalPara.bLieWeiIsOklte = false;
									GlobalPara.ilwlte = 0;
									if(mTrainState.g_DebugLog_Lev1!=0x00)
										LogInstance.error(GlobalPara.Tky,"179." + tag + ": (lwdata)liewei disconnected now8");

									String strLieWeiNumber = String.format("%c%c%c%c%c%c",
											((((GlobalPara.arrDteZhuangZhi[0] & 0xFF) >> 4) & 0x0F ) + '0'),(((GlobalPara.arrDteZhuangZhi[0] & 0xFF) & 0x0F) + '0')
											,((((GlobalPara.arrDteZhuangZhi[1] & 0xFF) >> 4) & 0x0F )+ '0'),(((GlobalPara.arrDteZhuangZhi[1] & 0xFF) & 0x0F)+ '0')
											,((((GlobalPara.arrDteZhuangZhi[2] & 0xFF) >> 4) & 0x0F )+ '0'),(((GlobalPara.arrDteZhuangZhi[2] & 0xFF) & 0x0F)+ '0'));
									mParamOperation.SaveSpecialField("LieWeiNumber", strLieWeiNumber);
									LogInstance.debug(GlobalPara.Tky, "(lwdata)save LieWeiNumber="+strLieWeiNumber);
									bNeedToSave  =  true;
								}

								if(bufferrx[3]!=0x00)
								{
									bufferrx[4] = mTrainState.m_SourLteAddre[0] ;
									bufferrx[5] = mTrainState.m_SourLteAddre[1] ;
									bufferrx[6] = mTrainState.m_SourLteAddre[2] ;
									bufferrx[7] = mTrainState.m_SourLteAddre[3] ;
								}

								bufferrx[16+bufferrx[3]+bufferrx[5+bufferrx[3]]] = GlobalPara.bCarType;
								bufferrx[17+bufferrx[3]+bufferrx[5+bufferrx[3]]] = GlobalPara.bCarNumber;
								if (/*mTrainState.g_Mode == 0x66 &&*/mLteHandle.mblLTE_IP && GlobalPara.bLieWeiIsOklte )
									bufferrx[24+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x01;
								else
									bufferrx[24+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x00;

								if(/*mTrainState.g_Mode == (byte) 0x66 &&*/ mLteHandle.mblLTE_IP
										&& (!(mTrainState.m_TailAddre[0]==0x00 && mTrainState.m_TailAddre[1]==0x00 && mTrainState.m_TailAddre[2]==0x00 && mTrainState.m_TailAddre[3]==0x00)
										||!(mTrainState.m_TailAddre2[0]==0x00 && mTrainState.m_TailAddre2[1]==0x00 && mTrainState.m_TailAddre2[2]==0x00 && mTrainState.m_TailAddre2[3]==0x00)))
								{
									DestinationIPAddress = String.format("%d.%d.%d.%d", mTrainState.m_TailAddre[0] & 0x00FF, mTrainState.m_TailAddre[1] & 0x00FF,
											mTrainState.m_TailAddre[2] & 0x00FF, mTrainState.m_TailAddre[3] & 0x00FF);
									if(!(mTrainState.m_TailAddre[0]==0x00 && mTrainState.m_TailAddre[1]==0x00
											&& mTrainState.m_TailAddre[2]==0x00 && mTrainState.m_TailAddre[3]==0x00))
									{
										SendInfo(bufferrx, rxdatalength, DestinationIPAddress, GlobalPara.strLieWeiDestinationPort);
									}

									DestinationIPAddress = String.format("%d.%d.%d.%d", mTrainState.m_TailAddre2[0] & 0x00FF, mTrainState.m_TailAddre2[1] & 0x00FF,
											mTrainState.m_TailAddre2[2] & 0x00FF, mTrainState.m_TailAddre2[3] & 0x00FF);
									if(!(mTrainState.m_TailAddre2[0]==0x00 && mTrainState.m_TailAddre2[1]==0x00
											&& mTrainState.m_TailAddre2[2]==0x00 && mTrainState.m_TailAddre2[3]==0x00))
									{
										SendInfo(bufferrx, rxdatalength, DestinationIPAddress, GlobalPara.strLieWeiDestinationPort);
									}
								}

								if(PackageInfo.Data[17]==0x01)
								{//司机确认通过
									//启动列尾心跳线程
									if(/*mTrainState.g_Mode == (byte) 0x66 &&*/ mLteHandle.mblLTE_IP
											&& (!(mTrainState.m_TailAddre[0]==0x00 && mTrainState.m_TailAddre[1]==0x00 && mTrainState.m_TailAddre[2]==0x00 && mTrainState.m_TailAddre[3]==0x00)
											||!(mTrainState.m_TailAddre2[0]==0x00 && mTrainState.m_TailAddre2[1]==0x00 && mTrainState.m_TailAddre2[2]==0x00 && mTrainState.m_TailAddre2[3]==0x00)))
									{
										mLteHandle.StartLieWeiHeartBeat();
									}
								}
								else
								{
									mLteHandle.EndLieWeiHeartBeat();
								}
								break;
							case (byte) 0x86: //CIR发送销号确认信息
								if(mTrainState.g_DebugLog_Lev1!=0x00)
									LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie xiao hao que ren from mmi to liewei, command = 0x86");
								if(bufferrx[3]!=0x00)
								{
									bufferrx[4] = mTrainState.m_SourLteAddre[0] ;
									bufferrx[5] = mTrainState.m_SourLteAddre[1] ;
									bufferrx[6] = mTrainState.m_SourLteAddre[2] ;
									bufferrx[7] = mTrainState.m_SourLteAddre[3] ;
								}

								bufferrx[16+bufferrx[3]+bufferrx[5+bufferrx[3]]] = GlobalPara.bCarType;
								bufferrx[17+bufferrx[3]+bufferrx[5+bufferrx[3]]] = GlobalPara.bCarNumber;
								if (/*mTrainState.g_Mode == 0x66 &&*/mLteHandle.mblLTE_IP && GlobalPara.bLieWeiIsOklte )
									bufferrx[24+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x01;
								else
									bufferrx[24+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x00;

								if(/*mTrainState.g_Mode == (byte) 0x66 &&*/ mLteHandle.mblLTE_IP && GlobalPara.bLieWeiIsOklte
										 )
								{
									DestinationIPAddress = String.format("%d.%d.%d.%d", mTrainState.m_TailAddre[0] & 0x00FF, mTrainState.m_TailAddre[1] & 0x00FF,
											mTrainState.m_TailAddre[2] & 0x00FF, mTrainState.m_TailAddre[3] & 0x00FF);
									if(!(mTrainState.m_TailAddre[0]==0x00 && mTrainState.m_TailAddre[1]==0x00
											&& mTrainState.m_TailAddre[2]==0x00 && mTrainState.m_TailAddre[3]==0x00))
										SendInfo(bufferrx, rxdatalength, DestinationIPAddress, GlobalPara.strLieWeiDestinationPort);

									DestinationIPAddress = String.format("%d.%d.%d.%d", mTrainState.m_TailAddre2[0] & 0x00FF, mTrainState.m_TailAddre2[1] & 0x00FF,
											mTrainState.m_TailAddre2[2] & 0x00FF, mTrainState.m_TailAddre2[3] & 0x00FF);
									if(!(mTrainState.m_TailAddre2[0]==0x00 && mTrainState.m_TailAddre2[1]==0x00
											&& mTrainState.m_TailAddre2[2]==0x00 && mTrainState.m_TailAddre2[3]==0x00))
										SendInfo(bufferrx, rxdatalength, DestinationIPAddress, GlobalPara.strLieWeiDestinationPort);
								}

								GlobalPara.arrLieWeiZhuangZhi[0] = 0x00;
								GlobalPara.arrLieWeiZhuangZhi[1] = 0x00;
								GlobalPara.arrLieWeiZhuangZhi[2] = 0x00;
								GlobalPara.arrDteZhuangZhi[0] = GlobalPara.arrLieWeiZhuangZhi[0] ;
								GlobalPara.arrDteZhuangZhi[1] = GlobalPara.arrLieWeiZhuangZhi[1] ;
								GlobalPara.arrDteZhuangZhi[2] = GlobalPara.arrLieWeiZhuangZhi[2] ;
								LogInstance.debug(GlobalPara.Tky, "(lwdata)zhuang zhi hao:000000, cir xiao hao que ren ying da(0x86), be attention");
								if(mTrainState.g_DebugLog_Lev1!=0x00)
									LogInstance.error(GlobalPara.Tky,"179." + tag + ": (lwdata)liewei disconnected now7");

								mLteHandle.EndLieWeiHeartBeat();
								GlobalPara.bLieWeiIsOklte = false;
								GlobalPara.ilwlte = 0;

								String strLieWeiNumber = String.format("%c%c%c%c%c%c",
										((((GlobalPara.arrDteZhuangZhi[0] & 0xFF) >> 4) & 0x0F ) + '0'),(((GlobalPara.arrDteZhuangZhi[0] & 0xFF) & 0x0F) + '0')
										,((((GlobalPara.arrDteZhuangZhi[1] & 0xFF) >> 4) & 0x0F )+ '0'),(((GlobalPara.arrDteZhuangZhi[1] & 0xFF) & 0x0F)+ '0')
										,((((GlobalPara.arrDteZhuangZhi[2] & 0xFF) >> 4) & 0x0F )+ '0'),(((GlobalPara.arrDteZhuangZhi[2] & 0xFF) & 0x0F)+ '0'));
								mParamOperation.SaveSpecialField("LieWeiNumber", strLieWeiNumber);
								LogInstance.debug(GlobalPara.Tky, "(lwdata)save LieWeiNumber="+strLieWeiNumber);
								bNeedToSave  =  true;
								break;
							default:// 转发
								LogInstance.error(GlobalPara.Tky/*"liewei"*/, "182." + tag + ": (lwdata)dirty data to liewei: "+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
								break;
						}
						break;
					default:// 转发
						LogInstance.error(GlobalPara.Tky/*"liewei"*/, "182." + tag + ": (lwdata)dirty data to liewei: "+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
						break;
				}
				break;
			default:// 转发
				LogInstance.error(GlobalPara.Tky/*"liewei"*/, "182." + tag + ": (lwdata)dirty data to liewei: "+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
				break;
		}
	}

	public void subApplyForKuJianGprs(_RawInfo PackageInfo)
	{
		try
		{
			int txdatalength;
			switch (PackageInfo.Command)
			{
				case (byte) 0x8D: // 地面出入库检测设备发送库检结果应答
					if (mTrainState.g_DebugLog_Lev1 != 0x00)
						LogInstance.debug(GlobalPara.Tky, "receive ku jian result ack from lte");

					break;
				case (byte) 0x8B: // 地面启动车载台库检
					if(mTrainState.g_UserMode_Myself == (byte) 0x00)
					{
						if (mTrainState.g_DebugLog_Lev1 != 0x00)
							LogInstance.debug(GlobalPara.Tky,"receive ku jian command from dimian, g_UserMode_Myself=0x00, so response");

						mTrainState.m_CurrentCheckIP[0] = PackageInfo.SourAddre[0] ;
						mTrainState.m_CurrentCheckIP[1] = PackageInfo.SourAddre[1] ;
						mTrainState.m_CurrentCheckIP[2] = PackageInfo.SourAddre[2] ;
						mTrainState.m_CurrentCheckIP[3] = PackageInfo.SourAddre[3] ;
						GlobalPara.iKuJianType = 1;
						GlobalPara.iKuJianAccessWay = 1;
						// 向地面库检设备发送应答信息
						byte[] buffertx = new byte[32] ;
						txdatalength = FrameGenerate.GetFrame_KuJianYingDa_13_8C(buffertx, mTrainState);
						String DestinationIPAddress = String.format(("%d.%d.%d.%d"), mTrainState.m_CurrentCheckIP[0] & 0x00FF,
								mTrainState.m_CurrentCheckIP[1] & 0x00FF, mTrainState.m_CurrentCheckIP[2] & 0x00FF, mTrainState.m_CurrentCheckIP[3] & 0x00FF);
						SendInfo(buffertx, txdatalength, DestinationIPAddress, GlobalPara.strCTCDestinationPort);
						buffertx = null;
					}
					else
					{
						if (mTrainState.g_DebugLog_Lev1 != 0x00)
							LogInstance.debug(GlobalPara.Tky,"receive ku jian command from dimian, g_UserMode_Myself=0x0F, so need not response");
					}
					break;
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
	int iHeartSeqForLteLiewei = 0;
	public void subApplyForLieWeiByGprs(byte[] bufferrx, int rxdatalength, _RawInfo PackageInfo)
	{
		try
		{
			if(mTrainState.g_DebugLog_Lev1!=0x00)
			{
				if(PackageInfo.Command == (byte) 0x41 )
				{//20150210
					if(mTrainState.g_DebugLog_Lev2!=0x00)
						LogInstance.debug(GlobalPara.Tky/*"liewei"*/, "(lwdata)ltelw to mmi: "+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
				}
				else if(  PackageInfo.Command == (byte) 0x2F )
				{
					iHeartSeqForLteLiewei++;
					if((iHeartSeqForLteLiewei % 6) == 0)//30秒记录一次
						LogInstance.debug(GlobalPara.Tky/*"liewei"*/, "(lwdata)lte heartbeat r:"+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
				}
				else
				{
					LogInstance.debug(GlobalPara.Tky/*"liewei"*/, "(lwdata)ltelw to mmi: "+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
				}
			}

			String DestinationIPAddress;
			switch (PackageInfo.ServiceType)
			{
				case 0x04:// LTE列尾信息
					switch (PackageInfo.Command)
					{
						case (byte) 0x41: //应答
							if (mTrainState.g_DebugLog_Lev2 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "(lwdata)receive ack 0x41 from lte liewei");
							SendInfo(bufferrx, rxdatalength, (byte) bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);
							break;
						case (byte) 0x31: //列尾报告列尾IP帧,没放发送链接请求时会收到,目的是防止出现无法查询列尾IP地址的情况
							if(mTrainState.g_DebugLog_Lev1!=0x00)
								LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie lte liewei report his ip, command = 0x31");

							if((mTrainState.g_EngineNumber.Number[0]-'0' ==  ((PackageInfo.Data[0] & 0xFF) >> 4))
									&& (mTrainState.g_EngineNumber.Number[1]-'0' ==  (PackageInfo.Data[0] & 0x0F))
									&& (mTrainState.g_EngineNumber.Number[2]-'0' ==  ((PackageInfo.Data[1] & 0xFF) >> 4))
									&& (mTrainState.g_EngineNumber.Number[3]-'0' ==  (PackageInfo.Data[1] & 0x0F))
									&& (mTrainState.g_EngineNumber.Number[4]-'0' ==  ((PackageInfo.Data[2] & 0xFF) >> 4))
									&& (mTrainState.g_EngineNumber.Number[5]-'0' ==  (PackageInfo.Data[2] & 0x0F))
									&& (mTrainState.g_EngineNumber.Number[6]-'0' ==  ((PackageInfo.Data[3] & 0xFF) >> 4))
									&& (mTrainState.g_EngineNumber.Number[7]-'0' ==  (PackageInfo.Data[3] & 0x0F))
									)
							{
								mTrainState.m_TailAddre[0] = PackageInfo.Data[8] ;
								mTrainState.m_TailAddre[1] = PackageInfo.Data[9] ;
								mTrainState.m_TailAddre[2] = PackageInfo.Data[10] ;
								mTrainState.m_TailAddre[3] = PackageInfo.Data[11] ;

								mTrainState.m_TailAddre2[0] = PackageInfo.Data[12] ;
								mTrainState.m_TailAddre2[1] = PackageInfo.Data[13] ;
								mTrainState.m_TailAddre2[2] = PackageInfo.Data[14] ;
								mTrainState.m_TailAddre2[3] = PackageInfo.Data[15] ;
							}
							else
							{
								if(mTrainState.g_DebugLog_Lev1!=0x00)
									LogInstance.debug(GlobalPara.Tky,"(lwdata)engine number in not right in liewei report his ip command!");
							}
							break;
						case (byte) 0x2F: //生命帧应答
							if(mTrainState.g_DebugLog_Lev2!=0x00)
								LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie heart beat response from lte liewei to mmi, command = 0x2F");
							GlobalPara.lastLieweiTimelte = System.currentTimeMillis();
							if(/*(GlobalPara.arrLieWeiZhuangZhi[0] == 0x00 && GlobalPara.arrLieWeiZhuangZhi[1] == 0x00 && GlobalPara.arrLieWeiZhuangZhi[2] == 0x00)
							&& */ !(PackageInfo.Data[5] == 0x00 && PackageInfo.Data[6] == 0x00 && PackageInfo.Data[7] == 0x00))
							{
								if(mTrainState.g_DebugLog_Lev2 != 0x00)
									LogInstance.debug(GlobalPara.Tky, "(lwdata)record zhuang zhi hao from heartbeat, attention");
								GlobalPara.arrLieWeiZhuangZhi[0] = PackageInfo.Data[5] ;
								GlobalPara.arrLieWeiZhuangZhi[1] = PackageInfo.Data[6] ;
								GlobalPara.arrLieWeiZhuangZhi[2] = PackageInfo.Data[7] ;

								if(mTrainState.g_DebugLog_Lev2 != 0x00)
								{
									String strZhuangzhi = "("+Integer.toHexString(GlobalPara.arrLieWeiZhuangZhi[0]) +","+Integer.toHexString(GlobalPara.arrLieWeiZhuangZhi[1] & 0xFF)+","+Integer.toHexString(GlobalPara.arrLieWeiZhuangZhi[2] & 0xFF)+")";
									LogInstance.debug(GlobalPara.Tky, "(lwdata)2F:"+strZhuangzhi);
								}
							}
							break;
						case (byte) 0x92:// 风压查询反馈
							if((PackageInfo.SourAddre[0] == mTrainState.m_TailAddre[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre[3])
									|| (PackageInfo.SourAddre[0] == mTrainState.m_TailAddre2[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre2[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre2[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre2[3]))
							{
								if(mTrainState.g_DebugLog_Lev1!=0x00)
									LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie lte feng ya cha xun fan kui from liewei to mmi, command = 0x92");
								//GlobalPara.arrLieWeiZhuangZhi[0] = PackageInfo.Data[5] ;
								//GlobalPara.arrLieWeiZhuangZhi[1] = PackageInfo.Data[6] ;
								//GlobalPara.arrLieWeiZhuangZhi[2] = PackageInfo.Data[7] ;
								//GlobalPara.arrDteZhuangZhi[0] = GlobalPara.arrLieWeiZhuangZhi[0] ;
								//GlobalPara.arrDteZhuangZhi[1] = GlobalPara.arrLieWeiZhuangZhi[1] ;
								//GlobalPara.arrDteZhuangZhi[2] = GlobalPara.arrLieWeiZhuangZhi[2] ;
								SendInfo(bufferrx, rxdatalength, bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);

								if(PackageInfo.Data[11] == 0x00)
								{
									//发送给音频单元,由音频单元发送给列尾进行存储
									if(mTrainState.g_UserMode_Myself == (byte)0x00)
									{//只有主控板卡存储
										bufferrx[6+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x15;
										SendInfo(bufferrx, rxdatalength, (byte)0x7B, (byte)1);

										LogInstance.debug(GlobalPara.Tky, "(lwdata)record data"+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
									}
								}
								else
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "(lwdata)receive data from dte which transform from lte liewei, need not to rec");
								}
							}
							break;
						case (byte) 0x25:// 常用排风确认反馈
							if((PackageInfo.SourAddre[0] == mTrainState.m_TailAddre[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre[3])
									|| (PackageInfo.SourAddre[0] == mTrainState.m_TailAddre2[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre2[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre2[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre2[3]))
							{
								if(mTrainState.g_DebugLog_Lev1!=0x00)
									LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie lte chang yong pai feng fan kui from liewei to mmi, command = 0x25");
								SendInfo(bufferrx, rxdatalength, bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);

								if(PackageInfo.Data[11] == 0x00)
								{
									//发送给音频单元,由音频单元发送给列尾进行存储
									if(mTrainState.g_UserMode_Myself == (byte)0x00)
									{//只有主控板卡存储
										bufferrx[6+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x15;
										SendInfo(bufferrx, rxdatalength, (byte)0x7B, (byte)1);

										LogInstance.debug(GlobalPara.Tky, "(lwdata)record data"+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
									}
								}
								else
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "(lwdata)receive data from dte which transform from lte liewei, need not to rec");
								}
							}
							break;
						case (byte) 0x27: //紧急排风确认反馈
							if((PackageInfo.SourAddre[0] == mTrainState.m_TailAddre[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre[3])
									|| (PackageInfo.SourAddre[0] == mTrainState.m_TailAddre2[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre2[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre2[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre2[3]))
							{
								if(mTrainState.g_DebugLog_Lev1!=0x00)
									LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie  lte jin ji pai feng fan kui from liewei to mmi, command = 0x27");
								SendInfo(bufferrx, rxdatalength, bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);

								if(PackageInfo.Data[11] == 0x00)
								{
									//发送给音频单元,由音频单元发送给列尾进行存储
									if(mTrainState.g_UserMode_Myself == (byte)0x00)
									{//只有主控板卡存储
										bufferrx[6+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x15;
										SendInfo(bufferrx, rxdatalength, (byte)0x7B, (byte)1);

										LogInstance.debug(GlobalPara.Tky, "(lwdata)record data"+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
									}
								}
								else
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "(lwdata)receive data from dte which transform from lte liewei, need not to rec");
								}
							}
							break;
						case (byte) 0x93: //尾部风压自动反馈
							if((PackageInfo.SourAddre[0] == mTrainState.m_TailAddre[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre[3])
									|| (PackageInfo.SourAddre[0] == mTrainState.m_TailAddre2[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre2[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre2[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre2[3]))
							{
								if(mTrainState.g_DebugLog_Lev1!=0x00)
									LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie lte wei bu feng ya zi dong fan kui from liewei to mmi, command = 0x93"  );
								//GlobalPara.arrLieWeiZhuangZhi[0] = PackageInfo.Data[5] ;
								//GlobalPara.arrLieWeiZhuangZhi[1] = PackageInfo.Data[6] ;
								//GlobalPara.arrLieWeiZhuangZhi[2] = PackageInfo.Data[7] ;
								//GlobalPara.arrDteZhuangZhi[0] = GlobalPara.arrLieWeiZhuangZhi[0] ;
								//GlobalPara.arrDteZhuangZhi[1] = GlobalPara.arrLieWeiZhuangZhi[1] ;
								//GlobalPara.arrDteZhuangZhi[2] = GlobalPara.arrLieWeiZhuangZhi[2] ;
								SendInfo(bufferrx, rxdatalength, bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);

								if(PackageInfo.Data[11] == 0x00)
								{
									//发送给音频单元,由音频单元发送给列尾进行存储
									if(mTrainState.g_UserMode_Myself == (byte)0x00)
									{//只有主控板卡存储
										bufferrx[6+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x15;
										SendInfo(bufferrx, rxdatalength, (byte)0x7B, (byte)1);

										LogInstance.debug(GlobalPara.Tky, "(lwdata)record data"+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
									}
								}
								else
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "(lwdata)receive data from dte which transform from lte liewei, need not to rec");
								}
							}
							break;
						case (byte) 0x66: //排风阀故障自动反馈
							if((PackageInfo.SourAddre[0] == mTrainState.m_TailAddre[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre[3])
									|| (PackageInfo.SourAddre[0] == mTrainState.m_TailAddre2[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre2[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre2[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre2[3]))
							{
								if(mTrainState.g_DebugLog_Lev1!=0x00)
									LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie lte pai feng fa gu zhang fan kui from liewei to mmi, command = 0x66");
								SendInfo(bufferrx, rxdatalength, bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);

								if(PackageInfo.Data[11] == 0x00)
								{
									//发送给音频单元,由音频单元发送给列尾进行存储
									if(mTrainState.g_UserMode_Myself == (byte)0x00)
									{//只有主控板卡存储
										bufferrx[6+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x15;
										SendInfo(bufferrx, rxdatalength, (byte)0x7B, (byte)1);
										LogInstance.debug(GlobalPara.Tky, "(lwdata)record data"+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
									}
								}
								else
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "(lwdata)receive data from dte which transform from lte liewei, need not to rec");
								}
							}
							break;
						case (byte) 0xB0: //低电压自动反馈
							if((PackageInfo.SourAddre[0] == mTrainState.m_TailAddre[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre[3])
									|| (PackageInfo.SourAddre[0] == mTrainState.m_TailAddre2[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre2[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre2[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre2[3]))
							{
								if(mTrainState.g_DebugLog_Lev1!=0x00)
									LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie lte di dian ya zi dong fan kui from liewei to mmi, command = 0xB0");
								SendInfo(bufferrx, rxdatalength, bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);

								if(PackageInfo.Data[11] == 0x00)
								{
									//发送给音频单元,由音频单元发送给列尾进行存储
									if(mTrainState.g_UserMode_Myself == (byte)0x00)
									{//只有主控板卡存储
										bufferrx[6+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x15;
										SendInfo(bufferrx, rxdatalength, (byte)0x7B, (byte)1);

										LogInstance.debug(GlobalPara.Tky, "(lwdata)record data"+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
									}
								}
								else
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "(lwdata)receive data from dte which transform from lte liewei, need not to rec");
								}
							}
							break;
						case (byte) 0x28: //常用排风结束自动反馈
							if((PackageInfo.SourAddre[0] == mTrainState.m_TailAddre[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre[3])
									|| (PackageInfo.SourAddre[0] == mTrainState.m_TailAddre2[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre2[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre2[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre2[3]))
							{
								if(mTrainState.g_DebugLog_Lev1 != 0x00)
									LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie lte chang yong pai feng jie shu zi dong fan kui from liewei to mmi, command = 0x28");
								SendInfo(bufferrx, rxdatalength, bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);

								if(PackageInfo.Data[11] == 0x00)
								{
									//发送给音频单元,由音频单元发送给列尾进行存储
									if(mTrainState.g_UserMode_Myself == (byte)0x00)
									{//只有主控板卡存储
										bufferrx[6+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x15;
										SendInfo(bufferrx, rxdatalength, (byte)0x7B, (byte)1);
										LogInstance.debug(GlobalPara.Tky, "(lwdata)record data"+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
									}
								}
								else
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "(lwdata)receive data from dte which transform from lte liewei, need not to rec");
								}
							}
							break;
						case (byte) 0x30: //低风压自动反馈
							if((PackageInfo.SourAddre[0] == mTrainState.m_TailAddre[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre[3])
									|| (PackageInfo.SourAddre[0] == mTrainState.m_TailAddre2[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre2[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre2[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre2[3]))
							{
								if(mTrainState.g_DebugLog_Lev1!=0x00)
									LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie di feng ya zi dong fan kui from lte liewei to mmi, command = 0x30");
								SendInfo(bufferrx, rxdatalength, bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);

								if(PackageInfo.Data[11] == 0x00)
								{
									//发送给音频单元,由音频单元发送给列尾进行存储
									if(mTrainState.g_UserMode_Myself == (byte)0x00)
									{//只有主控板卡存储
										bufferrx[6+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x15;
										SendInfo(bufferrx, rxdatalength, (byte)0x7B, (byte)1);
										LogInstance.debug(GlobalPara.Tky, "(lwdata)record data"+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
									}
								}
								else
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "(lwdata)receive data from dte which transform from lte liewei, need not to rec");
								}
							}
							break;
						case (byte) 0xB8: //尾部置号连接请求
							if(mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie lte zhi hao request from liewei to mmi, command = 0xB8");

							if(GlobalPara.bLieWeiIsOklte)
							{//已连接
								if(GlobalPara.arrLieWeiZhuangZhi[0]==0x00 && GlobalPara.arrLieWeiZhuangZhi[1]==0x00
										&& GlobalPara.arrLieWeiZhuangZhi[2]==0x00)
								{
									//该种情况下只有400k连接上会有,不处理,直接通知MMI
								}
								else if(!((mTrainState.g_EngineNumber.Number[0]-'0' ==  ((PackageInfo.Data[0] & 0xFF) >> 4))
										&& (mTrainState.g_EngineNumber.Number[1]-'0' ==  ((PackageInfo.Data[0] & 0xFF) & 0x0F))
										&& (mTrainState.g_EngineNumber.Number[2]-'0' ==  ((PackageInfo.Data[1] & 0xFF) >> 4))
										&& (mTrainState.g_EngineNumber.Number[3]-'0' ==  ((PackageInfo.Data[1] & 0xFF) & 0x0F))
										&& (mTrainState.g_EngineNumber.Number[4]-'0' ==  ((PackageInfo.Data[2] & 0xFF) >> 4 ))
										&& (mTrainState.g_EngineNumber.Number[5]-'0' ==  ((PackageInfo.Data[2] & 0xFF) & 0x0F))
										&& (mTrainState.g_EngineNumber.Number[6]-'0' ==  ((PackageInfo.Data[3] & 0xFF) >> 4))
										&& (mTrainState.g_EngineNumber.Number[7]-'0' ==  ((PackageInfo.Data[3] & 0xFF) & 0x0F))
								))
								{//已连接时,收到机车号不一致的连接请求帧,丢弃
									LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie error zhi hao request from liewei to mmi (B8)1, enginenumber is wrong");
									break;
								}
								else if(!(GlobalPara.arrLieWeiZhuangZhi[0]==PackageInfo.Data[5] && GlobalPara.arrLieWeiZhuangZhi[1]==PackageInfo.Data[6]
										&& GlobalPara.arrLieWeiZhuangZhi[2]==PackageInfo.Data[7])
										)
								{//已连接时,收到机车号一致,但是装置号不一致的连接请求帧
									LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie error zhi hao request from liewei to mmi (B8)2, zhuang zhi hao is wrong");

									break;//(原同时执行后面的代码,报告司机,现改成不再提示)
								}
								else
								{
									//已连接时,收到机车号一致,装置号一致,不需要操作,丢弃
									LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie right zhi hao request from liewei to mmi");
									GlobalPara.lastLieweiTimelte = System.currentTimeMillis();
									break;
								}
							}
							else
							{//未连接状态
								if(!((mTrainState.g_EngineNumber.Number[0]-'0' ==  ((PackageInfo.Data[0] & 0xFF) >> 4))
										&& (mTrainState.g_EngineNumber.Number[1]-'0' ==  ((PackageInfo.Data[0] & 0xFF) & 0x0F))
										&& (mTrainState.g_EngineNumber.Number[2]-'0' ==  ((PackageInfo.Data[1] & 0xFF) >> 4))
										&& (mTrainState.g_EngineNumber.Number[3]-'0' ==  ((PackageInfo.Data[1] & 0xFF) & 0x0F))
										&& (mTrainState.g_EngineNumber.Number[4]-'0' ==  ((PackageInfo.Data[2] & 0xFF) >> 4 ))
										&& (mTrainState.g_EngineNumber.Number[5]-'0' ==  ((PackageInfo.Data[2] & 0xFF) & 0x0F))
										&& (mTrainState.g_EngineNumber.Number[6]-'0' ==  ((PackageInfo.Data[3] & 0xFF) >> 4 ))
										&& (mTrainState.g_EngineNumber.Number[7]-'0' ==  ((PackageInfo.Data[3] & 0xFF) & 0x0F))
								))
								{//且机车号不一致,丢弃(实际上是收不到该数据的)
									LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie error enginenumber in zhuang zhi hao request from liewei to mmi (B8)3");
									break;
								}
								else
								{//机车号一致
									if((GlobalPara.arrLieWeiZhuangZhi[0]==PackageInfo.Data[5] && GlobalPara.arrLieWeiZhuangZhi[1] == PackageInfo.Data[6]
											&& GlobalPara.arrLieWeiZhuangZhi[2]==PackageInfo.Data[7]))
									{//未连接态,装置号完全一致,提示司机并自动恢复连接(因为是无心跳的连接断开),开机的时候是肯定不会一致的
										GlobalPara.lastLieweiTimelte = System.currentTimeMillis();
										LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie right enginenumber and zhuang zhi hao in zhuang zhi hao request from liewei to mmi (B8)4");
										LogInstance.debug(GlobalPara.Tky,"(lwdata)liewei connected now13");
										mLteHandle.StartLieWeiHeartBeat();

										//同时执行后面的代码,报告司机
									}
									else
									{//未连接且装置号不一致,提示司机
										String strZhuangzhi = "("+Integer.toHexString(PackageInfo.Data[5]) +","+Integer.toHexString(PackageInfo.Data[6] & 0xFF )+","+Integer.toHexString(PackageInfo.Data[7] & 0xFF)+")";
										LogInstance.debug(GlobalPara.Tky, "(lwdata)new zhuang zhi hao, B8:"+strZhuangzhi);

										//20150525//
										//if(GlobalPara.arrLieWeiZhuangZhi[0]==0x00 && GlobalPara.arrLieWeiZhuangZhi[1] == 0x00 && GlobalPara.arrLieWeiZhuangZhi[2]==0x00)
										//{//此处不能自动修改,在12976的if中将自动连接上
										//	GlobalPara.arrLieWeiZhuangZhi[0] = PackageInfo.Data[5] ;
										//	GlobalPara.arrLieWeiZhuangZhi[1] = PackageInfo.Data[6] ;
										//	GlobalPara.arrLieWeiZhuangZhi[2] = PackageInfo.Data[7] ;
										//}

										//同时执行后面的代码,报告司机
									}
								}
							}
							LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie right enginenumber in zhuang zhi hao request from liewei to mmi (B8)5");
							SendInfo(bufferrx, rxdatalength, bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);

							if(PackageInfo.Data[11] == 0x00)
							{
								//发送给音频单元,由音频单元发送给列尾进行存储
								if(mTrainState.g_UserMode_Myself == (byte)0x00)
								{//只有主控板卡存储
									bufferrx[6+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x15;
									SendInfo(bufferrx, rxdatalength, (byte)0x7B, (byte)1);
								}
							}
							else
							{
								if (mTrainState.g_DebugLog_Lev1 != 0x00)
									LogInstance.debug(GlobalPara.Tky, "(lwdata)receive data from dte which transform from lte liewei, need not to rec");
							}

							if((mTrainState.g_EngineNumber.Number[0]-'0' ==  ((PackageInfo.Data[0] & 0xFF) >> 4))
									&& (mTrainState.g_EngineNumber.Number[1]-'0' ==  ((PackageInfo.Data[0] & 0xFF) & 0x0F))
									&& (mTrainState.g_EngineNumber.Number[2]-'0' ==  ((PackageInfo.Data[1] & 0xFF) >> 4))
									&& (mTrainState.g_EngineNumber.Number[3]-'0' ==  ((PackageInfo.Data[1] & 0xFF) & 0x0F))
									&& (mTrainState.g_EngineNumber.Number[4]-'0' ==  ((PackageInfo.Data[2] & 0xFF) >> 4))
									&& (mTrainState.g_EngineNumber.Number[5]-'0' ==  ((PackageInfo.Data[2] & 0xFF) & 0x0F))
									&& (mTrainState.g_EngineNumber.Number[6]-'0' ==  ((PackageInfo.Data[3] & 0xFF) >> 4))
									&& (mTrainState.g_EngineNumber.Number[7]-'0' ==  ((PackageInfo.Data[3] & 0xFF) & 0x0F))
									)
							{//机车号码一致
								if(!(PackageInfo.Data[12] == 0x00 && PackageInfo.Data[13] == 0x00 &&
										PackageInfo.Data[14] == 0x00 && PackageInfo.Data[15] == 0x00)
										&& !(PackageInfo.Data[16] == 0x00 && PackageInfo.Data[17] == 0x00 &&
										PackageInfo.Data[18] == 0x00 && PackageInfo.Data[19] == 0x00)	)
								{
									mTrainState.m_TailAddre[0] = PackageInfo.Data[12] ;
									mTrainState.m_TailAddre[1] = PackageInfo.Data[13] ;
									mTrainState.m_TailAddre[2] = PackageInfo.Data[14] ;
									mTrainState.m_TailAddre[3] = PackageInfo.Data[15] ;

									mTrainState.m_TailAddre2[0] = PackageInfo.Data[16] ;
									mTrainState.m_TailAddre2[1] = PackageInfo.Data[17] ;
									mTrainState.m_TailAddre2[2] = PackageInfo.Data[18] ;
									mTrainState.m_TailAddre2[3] = PackageInfo.Data[19] ;
								}
								DestinationIPAddress = String.format("%d.%d.%d.%d", mTrainState.m_TailAddre[0] & 0x00FF, mTrainState.m_TailAddre[1] & 0x00FF,
										mTrainState.m_TailAddre[2] & 0x00FF, mTrainState.m_TailAddre[3] & 0x00FF);
								if(!(mTrainState.m_TailAddre[0]==0x00 && mTrainState.m_TailAddre[1]==0x00
										&& mTrainState.m_TailAddre[2]==0x00 && mTrainState.m_TailAddre[3]==0x00))
									LogInstance.debug(GlobalPara.Tky, "(lwdata)mTrainState.m_TailAddre:"+DestinationIPAddress);
								DestinationIPAddress = String.format("%d.%d.%d.%d", mTrainState.m_TailAddre2[0] & 0x00FF, mTrainState.m_TailAddre2[1] & 0x00FF,
										mTrainState.m_TailAddre2[2] & 0x00FF, mTrainState.m_TailAddre2[3] & 0x00FF);
								if(!(mTrainState.m_TailAddre2[0]==0x00 && mTrainState.m_TailAddre2[1]==0x00
										&& mTrainState.m_TailAddre2[2]==0x00 && mTrainState.m_TailAddre2[3]==0x00))
									LogInstance.debug(GlobalPara.Tky, "(lwdata)mTrainState.m_TailAddre2:"+DestinationIPAddress);
							}
							else
							{
								if(mTrainState.g_DebugLog_Lev1!=0x00)
									LogInstance.debug(GlobalPara.Tky,"(lwdata)engine number in not right in liewei report his ip command!");
								LogInstance.debug(GlobalPara.Tky, ""+(mTrainState.g_EngineNumber.Number[0]-'0')+"=="+((PackageInfo.Data[0] & 0xFF) >> 4));
								LogInstance.debug(GlobalPara.Tky, ""+(mTrainState.g_EngineNumber.Number[1]-'0')+"=="+((PackageInfo.Data[0] & 0xFF) & 0x0F));
								LogInstance.debug(GlobalPara.Tky, ""+(mTrainState.g_EngineNumber.Number[2]-'0')+"=="+((PackageInfo.Data[1] & 0xFF) >> 4));
								LogInstance.debug(GlobalPara.Tky, ""+(mTrainState.g_EngineNumber.Number[3]-'0')+"=="+((PackageInfo.Data[1] & 0xFF) & 0x0F));
								LogInstance.debug(GlobalPara.Tky, ""+(mTrainState.g_EngineNumber.Number[4]-'0')+"=="+((PackageInfo.Data[2] & 0xFF) >> 4));
								LogInstance.debug(GlobalPara.Tky, ""+(mTrainState.g_EngineNumber.Number[5]-'0')+"=="+((PackageInfo.Data[2] & 0xFF)& 0x0F));
								LogInstance.debug(GlobalPara.Tky, ""+(mTrainState.g_EngineNumber.Number[6]-'0')+"=="+((PackageInfo.Data[3] & 0xFF) >> 4));
								LogInstance.debug(GlobalPara.Tky, ""+(mTrainState.g_EngineNumber.Number[7]-'0')+"=="+((PackageInfo.Data[3] & 0xFF)& 0x0F));
							}

							break;
						case (byte) 0x85: //尾部红外销号请求
							if((PackageInfo.SourAddre[0] == mTrainState.m_TailAddre[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre[3])
									|| (PackageInfo.SourAddre[0] == mTrainState.m_TailAddre2[0] && PackageInfo.SourAddre[1] == mTrainState.m_TailAddre2[1]
									&& PackageInfo.SourAddre[2] == mTrainState.m_TailAddre2[2] && PackageInfo.SourAddre[3] == mTrainState.m_TailAddre2[3]))
							{
								if(mTrainState.g_DebugLog_Lev1 != 0x00)
									LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie lte wei bu xiao hao request from liewei to mmi, command = 0x85");
								SendInfo(bufferrx, rxdatalength, bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);

								if(PackageInfo.Data[11] == 0x00)
								{
									//发送给音频单元,由音频单元发送给列尾进行存储
									if(mTrainState.g_UserMode_Myself == (byte)0x00)
									{//只有主控板卡存储
										bufferrx[6+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x15;
										SendInfo(bufferrx, rxdatalength, (byte)0x7B, (byte)1);
									}
								}
								else
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "(lwdata)receive data from dte which transform from lte liewei, need not to rec");
								}
							}
							break;
						case (byte) 0x84: //尾部销号成功应答
							if(mTrainState.g_DebugLog_Lev1!=0x00)
								LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie lte wei bu xiao hao cheng gong ying da from liewei to mmi, command = 0x84");

							if(mTrainState.g_DebugLog_Lev1!=0x00)
								LogInstance.error(GlobalPara.Tky,"194." + tag + ": (lwdata)liewei disconnected now1");
							//for (i = 0; i < 4; i++)
							//	mTrainState.m_TailAddre[i] = 0x00;
							GlobalPara.arrLieWeiZhuangZhi[0] = 0x00;
							GlobalPara.arrLieWeiZhuangZhi[1] = 0x00;
							GlobalPara.arrLieWeiZhuangZhi[2] = 0x00;
							GlobalPara.arrDteZhuangZhi[0] = GlobalPara.arrLieWeiZhuangZhi[0] ;
							GlobalPara.arrDteZhuangZhi[1] = GlobalPara.arrLieWeiZhuangZhi[1] ;
							GlobalPara.arrDteZhuangZhi[2] = GlobalPara.arrLieWeiZhuangZhi[2] ;
							SendInfo(bufferrx, rxdatalength, bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);
							LogInstance.debug(GlobalPara.Tky, "(lwdata)zhuang zhi hao:000000, liewei send xiao hao cheng gong ying da(lte), be attention");
							GlobalPara.bLieWeiIsOklte = false;
							GlobalPara.ilwlte = 0;
							//if(mTrainState.g_DebugLog_Lev1!=0x00)
							//	LogInstance.error(GlobalPara.Tky,"(lwdata)bLieWeiIsOklte = false3");
							mLteHandle.EndLieWeiHeartBeat();

							if(PackageInfo.Data[11] == 0x00)
							{
								//发送给音频单元,由音频单元发送给列尾进行存储
								if(mTrainState.g_UserMode_Myself == (byte)0x00)
								{//只有主控板卡存储
									bufferrx[6+bufferrx[3]+bufferrx[5+bufferrx[3]]] = 0x15;
									SendInfo(bufferrx, rxdatalength, (byte)0x7B, (byte)1);
								}
							}
							else
							{
								if (mTrainState.g_DebugLog_Lev1 != 0x00)
									LogInstance.debug(GlobalPara.Tky, "(lwdata)receive data from dte which transform from lte liewei, need not to rec");
							}

							String strLieWeiNumber = String.format("%c%c%c%c%c%c",
									((((GlobalPara.arrDteZhuangZhi[0] & 0xFF) >> 4) & 0x0F ) + '0'),(((GlobalPara.arrDteZhuangZhi[0] & 0xFF) & 0x0F) + '0')
									,((((GlobalPara.arrDteZhuangZhi[1] & 0xFF) >> 4) & 0x0F )+ '0'),(((GlobalPara.arrDteZhuangZhi[1] & 0xFF) & 0x0F)+ '0')
									,((((GlobalPara.arrDteZhuangZhi[2] & 0xFF) >> 4) & 0x0F )+ '0'),(((GlobalPara.arrDteZhuangZhi[2] & 0xFF) & 0x0F)+ '0'));
							mParamOperation.SaveSpecialField("LieWeiNumber", strLieWeiNumber);
							LogInstance.debug(GlobalPara.Tky, "(lwdata)save LieWeiNumber="+strLieWeiNumber);
							bNeedToSave  =  true;
							break;

						case (byte) 0x62: //
						case (byte) 0x64: //
							//if(mTrainState.g_UserMode_Myself == (byte)0x00)
							//{//是否只让主控发
							if(PackageInfo.Command == (byte)0x62)
							{
								if(mTrainState.g_DebugLog_Lev1!=0x00)
									LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie pai feng start response from lte liewei to mmi(for 400 liewei really), command = 0x62");
							}
							else if(PackageInfo.Command == (byte)0x64)
							{
								if(mTrainState.g_DebugLog_Lev1!=0x00)
									LogInstance.debug(GlobalPara.Tky,"(lwdata)recevie pai feng stop response from lte liewei to mmi(for 400 liewei really), command = 0x64");
							}
							bufferrx[2] = (byte)0x02;
							bufferrx[4 + bufferrx[3]] = (byte)0x14;
							SendInfo(bufferrx, rxdatalength, (byte)0x7B, (byte)1);
							//}
							break;

						default:
							//20150128
							//SendInfo(bufferrx, rxdatalength, (byte) bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);

							LogInstance.error(GlobalPara.Tky/*"liewei"*/, "196." + tag + ": (lwdata)dirty data from liewei: "+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
							break;
					}
					break;
				default:
					//20150128
					//SendInfo(bufferrx, rxdatalength, (byte) bufferrx[4 + PackageInfo.SourAddreLenth], (byte) 1);

					//20150128
					LogInstance.error(GlobalPara.Tky/*"liewei"*/, "196." + tag + ": (lwdata)dirty data from liewei: "+GlobalFunc.bytesToHexString2(bufferrx, rxdatalength));
					break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": (lwdata)" + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
	}

	public void subApplyForGROS(_RawInfo PackageInfo)
	{
		try
		{
			int txdatalength;
			String DestinationIPAddress;
			byte[] buffertx = new byte[64] ;
			switch (PackageInfo.Command)
			{
				case (byte) 0x81: // CIR查询的响应 ,IP地址更新
				case (byte) 0x83: // GRIS触发
					mTrainState.m_InqiureGROSCounter = 0;
					mTrainState.m_DMISAddre[0] = PackageInfo.Data[11] ;//
					mTrainState.m_DMISAddre[1] = PackageInfo.Data[12] ;
					mTrainState.m_DMISAddre[2] = PackageInfo.Data[13] ;
					mTrainState.m_DMISAddre[3] = PackageInfo.Data[14] ;
					mTrainState.g_TrainNumberInfoToNowGRISTxCounter = 0;
					mTrainState.g_TrainNumberInfoStartOrStopToNowGRISTxCounter = 0;
					if (mTrainState.g_DebugLog_Lev1 != 0x00)
					{
						LogInstance.debug(GlobalPara.Tky, "receive gris ip update info because of cir query or gris action");
						LogInstance.debug(GlobalPara.Tky, "gris ip:" + String.format(("%d.%d.%d.%d"), mTrainState.m_DMISAddre[0] & 0x00FF, mTrainState.m_DMISAddre[1] & 0x00FF,
								mTrainState.m_DMISAddre[2] & 0x00FF, mTrainState.m_DMISAddre[3] & 0x00FF));
					}
					// CIR向GRIS响应IP地址更新
					txdatalength = FrameGenerate.GetFrame_ResponseUpdateGrisIp_0f_02(buffertx, mTrainState, PackageInfo);
					DestinationIPAddress = String.format(("%d.%d.%d.%d"), PackageInfo.SourAddre[0] & 0x00FF, PackageInfo.SourAddre[1] & 0x00FF,
							PackageInfo.SourAddre[2] & 0x00FF, PackageInfo.SourAddre[3] & 0x00FF);
					SendInfo(buffertx, txdatalength, DestinationIPAddress, GlobalPara.strCTCDestinationPort);
					break;
				case (byte) 0x82: // 82H:CTC触发 IP地址更新
					mTrainState.m_DMISAddre[0] = PackageInfo.Data[11] ;
					mTrainState.m_DMISAddre[1] = PackageInfo.Data[12] ;
					mTrainState.m_DMISAddre[2] = PackageInfo.Data[13] ;
					mTrainState.m_DMISAddre[3] = PackageInfo.Data[14] ;
					mTrainState.g_TrainNumberInfoToNowGRISTxCounter = 0;
					mTrainState.g_TrainNumberInfoStartOrStopToNowGRISTxCounter = 0;
					if (mTrainState.g_DebugLog_Lev1 != 0x00)
					{
						LogInstance.debug(GlobalPara.Tky, "receive gris ip update info because of ctc action");
						LogInstance.debug(GlobalPara.Tky, "gris ip:" + String.format(("%d.%d.%d.%d"), mTrainState.m_DMISAddre[0] & 0x00FF, mTrainState.m_DMISAddre[1] & 0x00FF,
								mTrainState.m_DMISAddre[2] & 0x00FF, mTrainState.m_DMISAddre[3] & 0x00FF));
					}
					// CIR向GRIS响应IP地址更新
					txdatalength = FrameGenerate.GetFrame_ResponseUpdateGrisIp_0f_02(buffertx, mTrainState, PackageInfo);
					DestinationIPAddress = String.format(("%d.%d.%d.%d"), PackageInfo.SourAddre[0] & 0x00FF, PackageInfo.SourAddre[1] & 0x00FF,
							PackageInfo.SourAddre[2] & 0x00FF, PackageInfo.SourAddre[3] & 0x00FF);
					SendInfo(buffertx, txdatalength, DestinationIPAddress, GlobalPara.strCTCDestinationPort);
					break;
			}
			buffertx = null;
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



	public void subSetSomeParameter(_RawInfo PackageInfo, int datalength) throws Exception
	{
		try
		{
			if (mTrainState.g_DebugLog_Lev1 != 0x00)
				LogInstance.debug(GlobalPara.Tky,"receive and set parameter from mmi");

			String str, GROSIPAddress, GROSIPAddress1;
			if (PackageInfo.InfoLenth - 8 - PackageInfo.SourAddreLenth - PackageInfo.DectAddreLenth > 0)
			{
				int i;
				switch (PackageInfo.Data[0])
				{
					case (byte) 0x21:
						break;
					case (byte) 0x22:// 设置归属GPRS接口服务器IP地址参数
						subSetDMISAddre(PackageInfo.Data[1], PackageInfo.Data[2], PackageInfo.Data[3], PackageInfo.Data[4]);
						break;
					case (byte) 0x23:// 设置归属APN
						mTrainState.g_APN = "";
						str = "";
						for (i = 1; (i < (int) datalength) && (PackageInfo.Data[i] != ';'); i++)
						{
							;
						}
						str = new String(PackageInfo.Data, 1, i - 1, "GBK");
						mTrainState.g_APN = str;
						mParamOperation.SaveSpecialField("APN", mTrainState.g_APN);
						bNeedToSave = true;

						break;
					case (byte) 0x24:// 设置主用GROSIP地址
						mTrainState.Set_m_GROSAddre(PackageInfo.Data[1], PackageInfo.Data[2], PackageInfo.Data[3], PackageInfo.Data[4]);
						GROSIPAddress = String.format(("%d.%d.%d.%d"), PackageInfo.Data[1] & 0x00FF, PackageInfo.Data[2] & 0x00FF,
								PackageInfo.Data[3] & 0x00FF, PackageInfo.Data[4] & 0x00FF);
						mParamOperation.SaveSpecialField("GROSIPAddress", GROSIPAddress);
						bNeedToSave = true;
						break;
					case (byte) 0x25:// 设置备用GROSIP地址
						mTrainState.Set_m_GROSAddre1(PackageInfo.Data[1], PackageInfo.Data[2], PackageInfo.Data[3], PackageInfo.Data[4]);
						GROSIPAddress1 = String.format(("%d.%d.%d.%d"), PackageInfo.Data[1] & 0x00FF, PackageInfo.Data[2] & 0x00FF,
								PackageInfo.Data[3] & 0x00FF, PackageInfo.Data[4] & 0x00FF);
						mParamOperation.SaveSpecialField("GROSIPAddress1", GROSIPAddress1);
						bNeedToSave = true;
						break;
					default:
						break;
				}
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

	public void subSetEngineNumber(_RawInfo PackageInfo) throws Exception
	{
		try
		{
			if (mTrainState.g_DebugLog_Lev1 != 0x00)
				LogInstance.debug(GlobalPara.Tky,"receive and set enginenumber from mmi");

			String str;
			if (GlobalFunc.MemCmp(mTrainState.g_EngineNumber.Number, PackageInfo.Data, 8))
			{
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky,"enginenumber have not be changed");
				return;
			}
			if (!GlobalFunc.MemCmp(mTrainState.g_EngineNumber.Number, PackageInfo.Data, 8))
			{ // 记录机车号的变化情况
				mParamOperation.SaveForEngineNumberManual(PackageInfo, mTrainState);
				GlobalFunc.MemCpy(mTrainState.g_EngineNumber.Number, PackageInfo.Data, 8);
			}
			str = String.format(("%c%c%c%c%c%c%c%c"), mTrainState.g_EngineNumber.Number[0], mTrainState.g_EngineNumber.Number[1],
					mTrainState.g_EngineNumber.Number[2], mTrainState.g_EngineNumber.Number[3], mTrainState.g_EngineNumber.Number[4],
					mTrainState.g_EngineNumber.Number[5], mTrainState.g_EngineNumber.Number[6], mTrainState.g_EngineNumber.Number[7]);
			mParamOperation.SaveSpecialField("EngineNumber", str);

			//单独记录机车号
			ConfigHelper.getInstance().SetParameterForLogNet("EngineNumber", str);

			mTrainState.g_EngineNumber.Manual_Flag = 1;
			bNeedToSave = true;
			// 不再向MMI发送机车号报警信息

			//设置"机车号1"
			if (!GlobalFunc.MemCmp(mTrainState.g_EngineNumber.Number, "XXXXXXXX", 8))
			{
				GlobalPara.strLoginUsrname = "";
				if(GlobalPara.iZhuKongFlag == 0x01)
				{
					if(mTrainState.g_bABJie == 0x01)
						GlobalPara.strLoginUsrname = str+"01";
					else
						GlobalPara.strLoginUsrname = str+"51";
				}
				else
				{
					if(mTrainState.g_bABJie == 0x01)
						GlobalPara.strLoginUsrname = str+"02";
					else
						GlobalPara.strLoginUsrname = str+"52";
				}
				if(mTrainState.g_DebugLog_Lev1!=0x00)
					LogInstance.debug(GlobalPara.Tky, "LoginUsrname = "+GlobalPara.strLoginUsrname);
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


	public void subApplyForCall(_RawInfo PackageInfo)
	{// 该函数只当Lte网络下呼叫时调用
		try
		{
			if (mTrainState.g_DebugLog_Lev1 != 0x00)
				LogInstance.debug(GlobalPara.Tky,"receive call action from mmi in lte mode");

			if (mTrainState.g_Mode == (byte) 0x66 && mLteHandle != null)
			{
				int headdata = 0, j;
				int priority = PackageInfo.Data[headdata + 1] - 0x30;// ASCII码转二进制
				if(priority == 9)
					priority = -1;

				String peerNum ="";
				byte[] bpeerNum;
				bpeerNum = new byte[20] ;
				for (j = 0; j < PackageInfo.Data.length && PackageInfo.Data[j + 2] != ';'; j++)
					bpeerNum[j] = (byte) PackageInfo.Data[j + 2] ;
				peerNum = new String(bpeerNum, 0, j, "GBK");
				peerNum = peerNum.trim();

				if(PackageInfo.Data[headdata] == 0x02)
				{//组呼
					peerNum = GlobalPara.strPreGroupcall + peerNum;
				}
				else if(PackageInfo.Data[headdata] == 0x03)
				{//广播
					peerNum = GlobalPara.strPreBoardcastcall + peerNum;
				}
				int calltype = PackageInfo.Data[headdata];
				mLteHandle.StartCall(calltype, priority, peerNum );
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





	public void subApplyForPttAction(byte[] bufferrx, int rxdatalength, _RawInfo PackageInfo)
	{
		try
		{
			mTrainState.g_PTT_Flag = PackageInfo.Data[0] ;

			mLteHandle.PttOperation(PackageInfo.Data[0]);

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

	public void subApplyForHookAction(byte[] bufferrx, int rxdatalength, _RawInfo PackageInfo)
	{//
		try
		{
			if (mTrainState.g_DebugLog_Lev1 != 0x00)
			{
				if (PackageInfo.Data[0] == 0x00)
					LogInstance.debug(GlobalPara.Tky,"receive hook gua ji action from mmi");
				else
					LogInstance.debug(GlobalPara.Tky,"receive hook zhai ji action from mmi");
			}

			mTrainState.g_Hook_Flag = PackageInfo.Data[0] ;

			mLteHandle.AnswerOrEndCall(PackageInfo.Data[0]);

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

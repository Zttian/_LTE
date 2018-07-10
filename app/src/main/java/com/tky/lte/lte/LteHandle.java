// /*
package com.tky.lte.lte;

import android.content.Context;
import android.text.TextUtils;

import com.jiaxun.android.lte_r.ServiceConstant;
import com.jiaxun.android.lte_r.ServiceEventListener;
import com.jiaxun.android.lte_r.ServiceManager;
import com.tky.lte.LTEApp;
import com.tky.lte.MainActivity;
import com.tky.lte.constants.Config;
import com.tky.lte.ui.event.ForceUnregisterResponseBean;
import com.tky.lte.ui.event.FunctionNumIsUsedBean;
import com.tky.lte.ui.event.FunctionNumListBean;
import com.tky.lte.ui.event.FunctionRegisterResponseBean;
import com.tky.lte.ui.event.GroupTmpList;
import com.tky.lte.ui.event.MainBean;
import com.tky.lte.ui.event.PttStateBean;
import com.tky.lte.ui.event.TmpGroupBean;
import com.tky.lte.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class LteHandle {

	public static LteHandle m_instance = null;
	public MainActivity mMainActivity = null ;

	public static LteHandle getInstance()
	{
		if (m_instance == null)
		{
			m_instance = new LteHandle();
		}
		return m_instance;
	}

	public DataProcesser mDataProcesser = null;
	public TrainState mTrainState = null;
	public ParamOperation mParamOperation = null;
	public LTEApp mApplication = null;
	public String tag = "LteHandle";

	public int bIsHoldSuccessful = -1;

	private LteHandle()
	{
		mParamOperation = ParamOperation.getInstance();
		mTrainState = TrainState.getInstance();
		mDataProcesser = new DataProcesser();
		LogInstance.debug(GlobalPara.Tky, "LteHandle be called");
	}
	public void SetApplication(LTEApp lteApp)
	{
		mApplication = lteApp;
	}

	public void InitSoft()
	{
		try
		{
			if (mTrainState.g_DebugLog_Lev1 != 0x00)
				LogInstance.debug(GlobalPara.Tky, "*********LteHandle InitSoft");

			bSend_LTE_UDP_Thread = true;
			bRead_LTE_UDP_Thread = true;
			try
			{
				if (mRead_LTE_UDP_Thread != null)
				{
					LogInstance.error(GlobalPara.Tky, "25." + tag + ": mRead_LTE_UDP_Thread be interrupted");
					mRead_LTE_UDP_Thread.interrupt();
					mRead_LTE_UDP_Thread = null;
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
			try
			{
				if (mSend_LTE_UDP_Thread != null)
				{
					LogInstance.error(GlobalPara.Tky,"25." + tag + ": mSend_LTE_UDP_Thread be interrupted");
					mSend_LTE_UDP_Thread.interrupt();
					mSend_LTE_UDP_Thread = null;
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

			int i;
			for (i = 0; i < mTrainState.mnLTELac.length; i++)
			{
				mTrainState.mnLTELac[i] = 0x00;
			}
			mTrainState.m_lac = new String(mTrainState.mnLTELac);
			for (i = 0; i < mTrainState.mnLTECi.length; i++)
			{
				mTrainState.mnLTECi[i] = 0x00;
			}
			mTrainState.m_ci = new String(mTrainState.mnLTECi);
			for (i = 0; i < mTrainState.m_SourLteAddre.length; i++)
			{
				mTrainState.m_SourLteAddre[i] = 0x00;
			}
			mTrainState.mszCellID = "";
			mtrainNum = "";
			mLocofunNum = "";

			mblDhcpOk = false;
			mblLTE_IP = false;
			GlobalPara.iIpIsOk = 0;

			mblPOCRegisterService_Started = false;
			mblPOCRegister = false;
			mblInitUdp = false;
			mnLTEPTTStatus = 0;
			mnCIRPTTOldStatus = -1;

			synchronized (pocevent)
			{
				callList.clear();
			}
			clear_currentCall();

			POC_UnRegister();
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

	protected void clear_currentCall()
	{
		synchronized (obj)
		{
			mCurrentCall.strCallId = "";
			mCurrentCall.callType = ServiceConstant.CALL_TYPE_SINGLE;

			mCurrentCall.peerNumber = "";
			mCurrentCall.groupId = "";
			mCurrentCall.funNumber = "";

			mCurrentCall.statusCode = 0x00;
			mCurrentCall.priority = -1;

			mCurrentCall.call_handout = false;
			mCurrentCall.call_way = 0x00;
			if (mTrainState.g_DebugLog_Lev1 != 0x00)
				LogInstance.debug(GlobalPara.Tky, "mCurrentCall is null");
		}
	}

	Object obj = new Object();
	Object objPttOpeation = new Object();

	public void copy_call2Currentcall(CALL call)
	{
		synchronized (obj)
		{
			mCurrentCall.strCallId = call.strCallId;
			mCurrentCall.callType = call.callType;

			mCurrentCall.peerNumber = call.peerNumber;
			mCurrentCall.groupId = call.groupId;
			mCurrentCall.funNumber = call.funNumber;

			mCurrentCall.statusCode = call.statusCode;
			mCurrentCall.priority = call.priority;

			mCurrentCall.call_way = call.call_way;
			mCurrentCall.call_handout = call.call_handout;

			//LogInstance.debug(GlobalPara.Tky, "in copy_call2Currentcall, mCurrentCall is  peerNumber=" + call.peerNumber + " funNumber=" + call.funNumber
			//			+ " groupId=" + call.groupId   +" callType=" + call.callType + " priority=" + call.priority + " statusCode=" + call.statusCode+ " call_way=" + call.call_way + " strCallId="+ call.strCallId );
		}
	}

	public boolean mblLTE_IP = false; // 只有在重新登网或拨号的时候才置为false,当登上网后置为true
	public boolean mblDhcpOk = false; // 为true时代表已获得了ip地址

	public ServiceManager serviceMgr = null;
	public ServiceEventListener listener = null;
	public boolean mblPOCRegisterService_Started = false;
	public boolean mblPOCRegister = false;


	boolean mblInitUdp = false; // 当lte数据接收线程启动时为true
	public DatagramSocket LTE_UDP_DS = null;
	public DatagramPacket lTE_UDP_DP_Send, LTE_UDP_Receive = null;
	public Read_LTE_UDP_Thread mRead_LTE_UDP_Thread = null;
	public Send_LTE_UDP_Thread mSend_LTE_UDP_Thread = null;
	public List<_GPRSSendData> g_GPRSData_List = new ArrayList<_GPRSSendData>();
	public final Object g_GPRSSend_Event = new Object();

	public short call_in = 1;
	public short call_out = 2;
	public short call_wait = 1;
	public short call_current = 2;
	public int mnRegisterType = 1;


	public String mfuncNum = ""; // 注册的功能号
	public String mtrainNum = ""; // 车次号
	public String mLocofunNum = ""; // 机车号
	public int mnCIRPTTOldStatus;
	public int mnLTEPTTStatus = 0;

	public static final List<CALL> callList = new ArrayList<CALL>();
	public static final CALL mCurrentCall = new CALL();
	public static final Object pocevent = new Object();

	public void initDataProcesser()
	{
		mDataProcesser.mLteHandle = this;
	}


	public String strImsi = "";
	public String strMsIsdn = "";

	public byte[] buffReader = new byte[1024] ;// 网口接收数据后去掉1002 1003以及重复的10后放入buffReader中
	public int buffLen = -1;
	public int FormValidData(byte[] tmpBuf, int tmpBufLen)
	{//去除1002 1003,去除多10
		int len = 0;
		byte bLastData = 0x00;
		if (tmpBufLen < 22)
			return -1;
		if (tmpBuf[0] != 0x10 || tmpBuf[1] != 0x02 || tmpBuf[tmpBufLen - 2] != 0x10 || tmpBuf[tmpBufLen - 1] != 0x03)
			return -1;
		for (int i = 2; i < tmpBufLen - 2; i++)
		{
			if (tmpBuf[i] == 0x10 && bLastData == 0x10)
			{
				bLastData = 0x00;// 防止出现2个10以上(10 10 10 10)的情况
				continue;
			}
			else
			{
				buffReader[len++] = tmpBuf[i] ;
				bLastData = tmpBuf[i] ;
			}
		}
		return len;
	}

	public boolean bRead_LTE_UDP_Thread = true;
	public class Read_LTE_UDP_Thread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				super.run();
				byte[] data = new byte[1024] ;
				while (!isInterrupted())
				{
					if (bRead_LTE_UDP_Thread)
					{
						if (mblLTE_IP)
						{
							LTE_UDP_Receive = new DatagramPacket(data, data.length);
							try
							{
								LTE_UDP_DS.receive(LTE_UDP_Receive);
								buffLen = FormValidData(LTE_UDP_Receive.getData(), LTE_UDP_Receive.getLength());

								if (buffLen == -1)
								{
									LogInstance.error(GlobalPara.Tky, "101." + tag + ": lte receive dirty data: "
											+ GlobalFunc.bytesToHexString2(LTE_UDP_Receive.getData(), LTE_UDP_Receive.getLength()));
								}
								else
								{
									DoReceiveData(buffReader, buffLen);
								}
							}
							catch (IOException e)
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
					else
						return;
				}
				data = null;
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

	public boolean bSend_LTE_UDP_Thread = true;
	public class Send_LTE_UDP_Thread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				super.run();
				while (!isInterrupted())
				{
					try
					{
						if (bSend_LTE_UDP_Thread)
						{
							if (mblLTE_IP && LTE_UDP_DS != null)
							{
								if (g_GPRSData_List.isEmpty())
								{
									synchronized (g_GPRSSend_Event)
									{
										g_GPRSSend_Event.wait();
									}
								}
								else
								{
									try
									{
										_GPRSSendData pGPRSSendData = g_GPRSData_List.get(0);
										if (pGPRSSendData == null)
										{
											if (mTrainState.g_DebugLog_Lev1 != 0x00)
												LogInstance.debug(GlobalPara.Tky, "Send_LTE_UDP_Thread: pGPRSSendData should not be null");
											if(g_GPRSData_List.size()>0)
												g_GPRSData_List.remove(0);
											continue;
										}
										byte[] buffertx = pGPRSSendData.buffertx;
										int txdatalength = pGPRSSendData.txdatalength;

										if (buffertx != null && txdatalength > 10)
										{
											try
											{
												String mszLTE_Aim_IP = pGPRSSendData.DestinationIPAddress;
												int mszLTE_Aim_Port = Integer.parseInt(pGPRSSendData.DestinationPort);
												lTE_UDP_DP_Send = new DatagramPacket(buffertx, txdatalength, InetAddress.getByName(mszLTE_Aim_IP),mszLTE_Aim_Port);
												LTE_UDP_DS.send(lTE_UDP_DP_Send);
												if(mTrainState.g_DebugLog_Lev1 != 0x00)
												{//20150128
													if(txdatalength > 17 && buffertx[17] != (byte)0x2F && buffertx[17] != (byte)0x41)
													{
														String tmpPrintString = GlobalFunc.bytesToHexString2(buffertx, txdatalength);
														LogInstance.debug(GlobalPara.Tky,"netudp-send("+mszLTE_Aim_IP+","+mszLTE_Aim_Port+"): " + tmpPrintString);
														tmpPrintString = null;
													}
												}
											}
											catch (Exception e)
											{
												LogInstance.debug(GlobalPara.Tky,e.getMessage());
												e.printStackTrace();
											}
											if(g_GPRSData_List.size()>0)
												g_GPRSData_List.remove(0);// 最后删除
										}
									}
									catch (Exception e)
									{
										if(g_GPRSData_List.size()>0)
											g_GPRSData_List.remove(0);// 最后删除

										e.printStackTrace();
										if (e.getMessage() != null && e.getMessage() != "")
										{
											LogInstance.exception(GlobalPara.Tky, e);
										}
										LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName()
												+ ": " + Thread.currentThread().getStackTrace()[2].getLineNumber());
									}
								}
							}
						}
						else
						{
							return;
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

	public void DoReceiveData(byte[] buf, int len)
	{
		try
		{
			if(mTrainState.g_DebugLog_Lev1 != 0x00)
			{//20150128
				if(len > 15 && buf[15] != (byte)0x2F && buf[15] != (byte)0x41)
					LogInstance.debug(GlobalPara.Tky, "netudp-receive: "+GlobalFunc.bytesToHexString2(buf, len));
			}
			mDataProcesser.HandleReceiveGprsData(buf, len);
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

	public void StartLieWeiHeartBeat()
	{//
		if(!bLieWeiHeartBeatThread)
		{
			if(mTrainState.g_DebugLog_Lev1!=0x00)
				LogInstance.debug(GlobalPara.Tky,"start liewei heartbeat thread");
			bLieWeiHeartBeatThread = true;
			mLieWeiHeartBeatThread = new LieWeiHeartBeatThread();
			mLieWeiHeartBeatThread.start();
		}
	}

	public void EndLieWeiHeartBeat()
	{
		if (mLieWeiHeartBeatThread != null)//
		{
			if(mTrainState.g_DebugLog_Lev1!=0x00)
				LogInstance.debug(GlobalPara.Tky,"end liewei heartbeat thread");
			bLieWeiHeartBeatThread = false;
			mLieWeiHeartBeatThread.interrupt();
			mLieWeiHeartBeatThread = null;
		}
	}

	int iHeartSeqForLteLiewei = 0;
	public boolean bLieWeiHeartBeatThread = false;
	public LieWeiHeartBeatThread mLieWeiHeartBeatThread = null;
	public class LieWeiHeartBeatThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				super.run();
				while (!isInterrupted())
				{
					if (bLieWeiHeartBeatThread)
					{
						try
						{
							iHeartSeqForLteLiewei++;
							byte[] buffertx = new byte[64] ;
							int txdatalength = FrameGenerate.GetFrame_LieWeiHeartBeat(mblLTE_IP,buffertx,mTrainState, (byte) 0x14,1);
							String DestinationIPAddress = String.format("%d.%d.%d.%d", mTrainState.m_TailAddre[0] & 0x00FF, mTrainState.m_TailAddre[1] & 0x00FF,
									mTrainState.m_TailAddre[2] & 0x00FF, mTrainState.m_TailAddre[3] & 0x00FF);
							if(!(mTrainState.m_TailAddre[0] == 0x00 && mTrainState.m_TailAddre[1]==0x00
									&& mTrainState.m_TailAddre[2] == 0x00 && mTrainState.m_TailAddre[3]==0x00))
							{
								mDataProcesser.SendInfo(buffertx, txdatalength, DestinationIPAddress, GlobalPara.strLieWeiDestinationPort);
								if((mTrainState.g_DebugLog_Lev1 != 0x00) && ((iHeartSeqForLteLiewei % 6) == 0x00) )
								{
									String strDebugLog = GlobalFunc.bytesToHexString(buffertx, txdatalength);
									LogInstance.debug(GlobalPara.Tky,"(lwdata)lte heartbeat s("+DestinationIPAddress+") " +strDebugLog);
									strDebugLog = null;
								}
							}
							Thread.sleep(5000);

							txdatalength = FrameGenerate.GetFrame_LieWeiHeartBeat(mblLTE_IP,buffertx,mTrainState, (byte) 0x14,2);
							DestinationIPAddress = String.format("%d.%d.%d.%d", mTrainState.m_TailAddre2[0] & 0x00FF, mTrainState.m_TailAddre2[1] & 0x00FF,
									mTrainState.m_TailAddre2[2] & 0x00FF, mTrainState.m_TailAddre2[3] & 0x00FF);
							if(!(mTrainState.m_TailAddre2[0] == 0x00 && mTrainState.m_TailAddre2[1]==0x00
									&& mTrainState.m_TailAddre2[2] == 0x00 && mTrainState.m_TailAddre2[3]==0x00))
							{
								mDataProcesser.SendInfo(buffertx, txdatalength, DestinationIPAddress, GlobalPara.strLieWeiDestinationPort);
								if((mTrainState.g_DebugLog_Lev1 != 0x00) && ((iHeartSeqForLteLiewei % 6) == 0x00) )
								{
									String strDebugLog = GlobalFunc.bytesToHexString(buffertx, txdatalength);
									LogInstance.debug(GlobalPara.Tky,"(lwdata)lte heartbeat s:("+DestinationIPAddress+") " +strDebugLog);
									strDebugLog = null;
								}
							}
							Thread.sleep(5000);

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
					else
						return;
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
	}


	public String getLocalIpAddress()
	{
		try
		{
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
			{
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress())
					{
						String tmpIPString = inetAddress.getHostAddress().toString().trim();
						LogInstance.debug(GlobalPara.Tky, "1.ltehandle, search ip: "+tmpIPString);
					}
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
			return null;
		}


		try
		{
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
			{
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress())
					{
						String tmpIPString = inetAddress.getHostAddress().toString().trim();
						LogInstance.debug(GlobalPara.Tky, "2.ltehandle, search ip: "+tmpIPString);
						// LTE的IP地址以6.6开始
						if (tmpIPString != null && tmpIPString != "" && !tmpIPString.equals("0.0.0.0")
								&& !tmpIPString.startsWith("127") && !tmpIPString.startsWith("169") && GlobalFunc.isRightIp(tmpIPString))
						{
							//本地调试时该段需要修改,地址匹配
							LogInstance.debug(GlobalPara.Tky, "have found usb0 ip: "+tmpIPString);
							return tmpIPString;

						}
					}
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
			return null;
		}
		return null;
	}

	public void StartUdpService()
	{
		try
		{
			try
			{
				if (mRead_LTE_UDP_Thread != null)
				{
					LogInstance.error(GlobalPara.Tky, "25." + tag + ": mRead_LTE_UDP_Thread be interrupted");
					mRead_LTE_UDP_Thread.interrupt();
					mRead_LTE_UDP_Thread = null;
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

			try
			{
				if (mSend_LTE_UDP_Thread != null)
				{
					LogInstance.error(GlobalPara.Tky, "25." + tag + ": mSend_LTE_UDP_Thread be interrupted");
					mSend_LTE_UDP_Thread.interrupt();
					mSend_LTE_UDP_Thread = null;
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


			if(mTrainState.m_SourLteAddre[0] == 0x00 )
			{
				LogInstance.error(GlobalPara.Tky, "103." + tag + ": have no lte ip, so can not start udp service");
				return;
			}
			byte[] arrLteIp = new byte[4] ;
			arrLteIp[0] = mTrainState.m_SourLteAddre[0] ;
			arrLteIp[1] = mTrainState.m_SourLteAddre[1] ;
			arrLteIp[2] = mTrainState.m_SourLteAddre[2] ;
			arrLteIp[3] = mTrainState.m_SourLteAddre[3] ;
			String _lte_ip = String.format("%d.%d.%d.%d",arrLteIp[0] & 0x00FF,arrLteIp[1] & 0x00FF,arrLteIp[2] & 0x00FF,arrLteIp[3] & 0x00FF);
			LogInstance.debug(GlobalPara.Tky, "Lte_Udp_IP:"+_lte_ip);

			//使用SO_REUSEADDR选项时有两点需要注意:
			//1.  必须在调用bind方法之前使用setReuseAddress方法来打开SO_REUSEADDR选项。因此，要想使用SO_REUSEADDR选项，就不能通过Socket类的构造方法来绑定端口。
			//2.  必须将绑定同一个端口的所有的Socket对象的SO_REUSEADDR选项都打开才能起作用。如在例程4-12中，socket1和socket2都使用了setReuseAddress方法打开了各自的SO_REUSEADDR选项。

			if (LTE_UDP_DS == null)
			{
				try
				{
					LogInstance.debug(GlobalPara.Tky, "StartUdpService1");
					LTE_UDP_DS = new DatagramSocket(null);
					LTE_UDP_DS.setReuseAddress(true);
					LTE_UDP_DS.bind(new InetSocketAddress(InetAddress.getByName(_lte_ip), Integer.parseInt(GlobalPara.strLocalPort)));
					LogInstance.debug(GlobalPara.Tky, "StartUdpService2");
				}
				catch (Exception e)
				{
					if(mTrainState.g_Mode == (byte)0x66)
					{
						LogInstance.error(GlobalPara.Tky,"80." + tag + ": DoHealth:3.can't start udp services, need to restart, bWatchDog = false");
					}

					e.printStackTrace();
					if (e.getMessage() != null && e.getMessage() != "")
					{
						LogInstance.exception(GlobalPara.Tky, e);
					}
					LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
							+ Thread.currentThread().getStackTrace()[2].getLineNumber());

				}
			}

			// Create a receiving thread
			bRead_LTE_UDP_Thread = true;
			mRead_LTE_UDP_Thread = new Read_LTE_UDP_Thread();
			mRead_LTE_UDP_Thread.start();
			bSend_LTE_UDP_Thread = true;
			mSend_LTE_UDP_Thread = new Send_LTE_UDP_Thread();
			mSend_LTE_UDP_Thread.start();
			mblInitUdp = true;
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


	public void POC_UnRegister()//POC去初始化
	{
		try
		{
			synchronized (pocevent)
			{
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "POC_UnRegister");
				callList.clear();
				if (listener != null && serviceMgr != null)
				{
					serviceMgr.unRegisterServiceEventListener();
					serviceMgr.stopService(ServiceConstant.MODE_NORMAL_MASTER_OFFLINE);
					serviceMgr.unInitialize();
				}
				mblPOCRegisterService_Started = false;
				mblPOCRegister = false;
				listener = null;
				serviceMgr = null;
				mTrainState.g_PocStateZhuBoard = 0;
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

	public Context thisContext = null; //
	public ArrayList<GroupInCall> lstGroupInCalls = new ArrayList<GroupInCall>();//呼叫中组列表
	public ArrayList<GroupTmpCall> lstGroupTmpCalls = new ArrayList<GroupTmpCall>();//临时组呼列表
	public ArrayList<String> lstFuncNum = new ArrayList<String>();//功能号列表
	public int iShowPocOff = 0;

	public void POC_Register()// POC初始化
	{
		try
		{
			synchronized (pocevent)
			{
				if (mblPOCRegisterService_Started)
				{
					return;
				}
				mblPOCRegisterService_Started = true;

				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "POC Register start");

				serviceMgr = ServiceManager.getInstance();
				serviceMgr.setClientType(ServiceConstant.CLIENT_TYPE_MOBILE);//ServiceConstant.CLIENT_TYPE_CAR
				serviceMgr.setLogPath( LogParam.LOG_FILEPATH, LogParam.LOG_MAXSIZE_FOR_POC);
				serviceMgr.setDebugLogEnabled(false);

                if(mTrainState.m_CheckIP3[3] == 101)//测试代码
                {
                    mTrainState.mLTE_SIP_IP = "192.168.30.232";
                    mTrainState.mLTE_SIP_IP2 = "0.0.0.0";
                    mTrainState.mLTE_Msisdn = "14689099001";
                    mTrainState.mLTE_Imsi="123456";
					if(GlobalPara.strPhoneId.equals("")) {
						GlobalPara.strPhoneId = mTrainState.mLTE_Msisdn;
						ShowInfoOnWindows("phoneid", GlobalPara.strPhoneId,"");////
					}
                }
                else if(mTrainState.m_CheckIP3[3] == 103)
                {
                    mTrainState.mLTE_SIP_IP = "192.168.30.232";
                    mTrainState.mLTE_SIP_IP2 = "0.0.0.0";
                    mTrainState.mLTE_Msisdn = "14689099003";
                    mTrainState.mLTE_Imsi="123456";
					if(GlobalPara.strPhoneId.equals("")) {
						GlobalPara.strPhoneId = mTrainState.mLTE_Msisdn;
						ShowInfoOnWindows("phoneid", GlobalPara.strPhoneId,"");////
					}
                }
                else if(mTrainState.m_CheckIP3[3] == 105)
                {
                    mTrainState.mLTE_SIP_IP = "192.168.30.232";
                    mTrainState.mLTE_SIP_IP2 = "0.0.0.0";
                    mTrainState.mLTE_Msisdn = "14689099005";
                    mTrainState.mLTE_Imsi="123456";
					if(GlobalPara.strPhoneId.equals("")) {
						GlobalPara.strPhoneId = mTrainState.mLTE_Msisdn;
						ShowInfoOnWindows("phoneid", GlobalPara.strPhoneId,"");////
					}
                }

				if(!mTrainState.mLTE_Msisdn.equals(""))
				{
					if(mTrainState.g_DebugLog_Lev1!=0x00)
						LogInstance.debug(GlobalPara.Tky, "poc username(isdn): "+mTrainState.mLTE_Msisdn+"-"+mTrainState.mLTE_Imsi);
					serviceMgr.setUserAccount(mTrainState.mLTE_Msisdn, mTrainState.mLTE_Imsi);
					LogInstance.debug(GlobalPara.Tky, "username:"+mTrainState.mLTE_Msisdn+", password:"+mTrainState.mLTE_Imsi);
				}
				else
				{
					if(mTrainState.g_DebugLog_Lev1!=0x00)
						LogInstance.debug(GlobalPara.Tky, "poc username(username): "+mTrainState.mLTE_UserName+"-"+mTrainState.mLTE_PassWord);
					serviceMgr.setUserAccount(mTrainState.mLTE_UserName, mTrainState.mLTE_PassWord);
					LogInstance.debug(GlobalPara.Tky, "username:"+mTrainState.mLTE_UserName+", password:"+mTrainState.mLTE_PassWord);
				}

				if(mTrainState.mLTE_SIP_IP2.equals("") || mTrainState.mLTE_SIP_IP2.equals("0.0.0.0"))
				{
					serviceMgr.setServerAddress(ServiceConstant.ADDRESS_TYPE_IP, mTrainState.mLTE_SIP_IP, "" /*mTrainState.mLTE_SIP_IP2*/);//单中心用"",双中心用另外一个IP
					if(mTrainState.g_DebugLog_Lev1!=0x00)
						LogInstance.debug(GlobalPara.Tky, "single poc center");
					LogInstance.debug(GlobalPara.Tky, "adress ip:"+mTrainState.mLTE_SIP_IP);
				}
				else
				{
					serviceMgr.setServerAddress(ServiceConstant.ADDRESS_TYPE_IP, mTrainState.mLTE_SIP_IP, mTrainState.mLTE_SIP_IP2 );// 双中心用另外一个IP
					if(mTrainState.g_DebugLog_Lev1!=0x00)
						LogInstance.debug(GlobalPara.Tky, "double poc center");
					LogInstance.debug(GlobalPara.Tky, "adress ip1:"+mTrainState.mLTE_SIP_IP + ", ip2:" + mTrainState.mLTE_SIP_IP2 );
				}

				if(!mTrainState.mLTE_Msisdn.equals(""))
				{
					serviceMgr.setUserNum(mTrainState.mLTE_Msisdn);//20141009:mTrainState.mLTE_UserName mTrainState.mLTE_Msisdn
					LogInstance.debug(GlobalPara.Tky, "usernum:"+mTrainState.mLTE_Msisdn);
				}
				else {
					serviceMgr.setUserNum(mTrainState.mLTE_UserName);//20141009:mTrainState.mLTE_UserName mTrainState.mLTE_Msisdn
					LogInstance.debug(GlobalPara.Tky, "usernum:"+mTrainState.mLTE_UserName);
				}

				thisContext = mApplication.getApplicationContext();
				serviceMgr.setContext(thisContext);
				serviceMgr.initialize(ServiceConstant.MODE_MASTER);

				String tmpVersion = serviceMgr.getPocSdkVersion();// MDS6800_POC_SDK_V3.0.13_D20130918
				int tmpIndex = tmpVersion.indexOf('_');
				tmpVersion = tmpVersion.substring(tmpIndex + 1);// POC_SDK_V3.0.13_D20130918
				tmpIndex = tmpVersion.indexOf('_');
				tmpVersion = tmpVersion.substring(tmpIndex + 1);// SDK_V3.0.13_D20130918
				tmpIndex = tmpVersion.indexOf('_');
				tmpVersion = tmpVersion.substring(tmpIndex + 1);// V3.0.13_D20130918
				tmpIndex = tmpVersion.indexOf('_');
				GlobalPara.g_PocVersion = tmpVersion.substring(0, tmpIndex) + " ";// V3.0.13
				tmpVersion = tmpVersion.substring(tmpIndex + 1);// D20130918
				GlobalPara.g_PocVersion = GlobalPara.g_PocVersion + tmpVersion.substring(3, 5) + "/" + tmpVersion.substring(5, 7) + "/" + tmpVersion.substring(7);
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "Poc Version:" + GlobalPara.g_PocVersion);

				listener = new ServiceEventListener()
				{

					@Override
					public int serviceStatusNotify(int nStatus, String szReason)
					{
						if (nStatus == ServiceConstant.UPLINE)
							LogInstance.debug(GlobalPara.Tky, "serviceStatusNotify: on");
						else
							LogInstance.debug(GlobalPara.Tky, "serviceStatusNotify: off"+","+szReason);

						try
						{
							synchronized (pocevent)
							{
								if (nStatus == ServiceConstant.UPLINE)
								{//上线
									mTrainState.g_PocStateZhuBoard = 1;
									if (!mblPOCRegister)
									{
										mblPOCRegister = true;
										LogInstance.debug(GlobalPara.Tky, "POC 上线");
										ShowInfoOnWindows("pocstate","on",szReason);
									}
								}
								else
								{//下线
									mTrainState.g_PocStateZhuBoard = 0;
									mTrainState.TrainNumberFNRegisterStatus = 0;
									mTrainState.EngineNumberFNRegisterStatus = 0;

									if (mblPOCRegister)//
									{
										mblPOCRegister = false;
										ShowInfoOnWindows("pocstate","off",szReason);
										synchronized (pocevent)
										{
											callList.clear();
										}
									}else{
										//
										if(iShowPocOff % 3 ==0){
											ShowInfoOnWindows("pocstate","off",szReason);
										}
										iShowPocOff++;
									}
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
						return 0;
					}


					@Override
					public int incomingCallNotify(String szCallID, int nCallType, String szCallerNum, String szCreateNum /*szCallerFN*/,
												  String szCalleeUN, int nPriority, int nCallRole)
					{//szCallerNum单呼时为主叫号码,组呼时为组呼号,szCallerFN主叫的功能码,szCalleeUN被叫号
						if (mTrainState.g_DebugLog_Lev1 != 0x00)
							LogInstance.debug(GlobalPara.Tky, "incomingCallNotify: szCallID=" + szCallID + " nCallType=" + nCallType + " szCallerNum="
									+ szCallerNum + " szCreateNum =" + szCreateNum  + " szCalleeUN=" + szCalleeUN+ " nPriority=" + nPriority+ " nCallRole=" + nCallRole);

						try
						{
							//防止重复的incomingCallNotify通知
							for (int i = 0; i < callList.size(); i++)
							{
								if(callList.get(i).strCallId.equals(szCallID))
								{
									if(mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.error(GlobalPara.Tky, "117." + tag + ": incomingCallNotify error, a new incomingCallNotify whose szCallID is already in calllist");
									return 0;
								}
							}

							synchronized (pocevent)
							{
								CALL call = new CALL();
								call.strCallId = szCallID;
								call.callType = nCallType;
								if(nCallRole == ServiceConstant.CALL_ROLE_CREATE)
									call.call_way = call_out;
								else
									call.call_way = call_in;

								if(nCallType == ServiceConstant.CALL_TYPE_SINGLE)
								{//个呼
									call.peerNumber = szCallerNum;
									call.groupId = "";
									if(szCreateNum!=null && !szCreateNum.equals("") &&(szCreateNum.startsWith(GlobalPara.strPreGlobalNumber ) || szCreateNum.startsWith(GlobalPara.strPreTfnNumber) || szCreateNum.startsWith(GlobalPara.strPreEfnNumber)))
										call.funNumber = szCreateNum ;
								}
								else
								{//组呼、广播
									call.peerNumber = "";
									call.groupId = szCallerNum;
									if(szCreateNum!=null && !szCreateNum.equals(""))
									{
										if(szCreateNum.startsWith(GlobalPara.strPreGlobalNumber) || szCreateNum.startsWith(GlobalPara.strPreTfnNumber) || szCreateNum.startsWith(GlobalPara.strPreEfnNumber))
											call.funNumber = szCreateNum ;
										else
											call.peerNumber = szCreateNum;
									}
								}
								call.priority = nPriority;
								call.call_way = call_in;
								call.statusCode = ServiceConstant.CALL_STATE_INCOMING ;// 来呼振铃状态
								call.call_handout = false;//


								if(call.callType != ServiceConstant.CALL_TYPE_SINGLE && call.priority == 0 && (call.groupId != null && !call.groupId.equals("") && call.groupId.endsWith(GlobalPara.strTailFor299) ))
								{//299紧急呼叫
									for (int i = 0; i < callList.size(); i++)
									{// 新接入组呼时,应该是挂掉所有个呼(比如当前正有两个通话,只挂掉当前是不够的)
										CALL tCall = callList.get(i);
										if (tCall.callType == ServiceConstant.CALL_TYPE_SINGLE && !tCall.strCallId.equals(call.strCallId))// 个呼
										{
											if (mTrainState.g_DebugLog_Lev1 != 0x00)
												LogInstance.debug(GlobalPara.Tky, "end call because of 299: " + tCall.peerNumber);
											if(tCall.statusCode == ServiceConstant.CALL_STATE_IN_CALL
													|| tCall.statusCode == ServiceConstant.CALL_STATE_HOLD
													|| tCall.statusCode == ServiceConstant.CALL_STATE_HOLDED)
											{//通话中则为endcall
												if (mTrainState.g_DebugLog_Lev1 != 0x00)
													LogInstance.debug(GlobalPara.Tky, "serviceMgr.endCall: peerNumber="+tCall.peerNumber + " funNumber="+tCall.funNumber + " callType="+tCall.callType + " priority="+tCall.priority
															+" strCallId=" + tCall.strCallId);
												int retCode = serviceMgr.endCall(tCall.strCallId, ServiceConstant.CALL_REASON_SEIZED);
												if (retCode != ServiceConstant.METHOD_SUCCESS)
												{
													LogInstance.error(GlobalPara.Tky, "118." + tag + ": serviceMgr.endCall failed");
													if(callList.size()>0)
													{
														i--;//在for循环中动态删除
														callList.remove(tCall);
														LogInstance.debug(GlobalPara.Tky, "should remove this call from calllist1");
													}
												}
											}
											else
											{//其他状态,如果主叫则cancelcall,如果被叫则为rejectcall
												if(tCall.call_way == call_in)
												{//rejectcall
													if (mTrainState.g_DebugLog_Lev1 != 0x00)
														LogInstance.debug(GlobalPara.Tky, "rejectCall because of 299: peerNumber="+tCall.peerNumber + " funNumber="+tCall.funNumber
																+ " callType="+tCall.callType + " priority="+tCall.priority +" strCallId=" + tCall.strCallId);
													int retCode = serviceMgr.rejectCall(tCall.strCallId, ServiceConstant.CALL_REASON_SEIZED);
													if (retCode != ServiceConstant.METHOD_SUCCESS)
														LogInstance.error(GlobalPara.Tky, "119." + tag + ": serviceMgr.rejectCall failed");
												}
												else
												{//cancelcall
													if (mTrainState.g_DebugLog_Lev1 != 0x00)
														LogInstance.debug(GlobalPara.Tky, "cancelCall because of 299: peerNumber="+tCall.peerNumber + " funNumber="+tCall.funNumber
																+ " callType="+tCall.callType + " priority="+tCall.priority +" strCallId=" + tCall.strCallId);
													int retCode = serviceMgr.cancelCall(tCall.strCallId, ServiceConstant.CALL_REASON_SEIZED);
													if (retCode != ServiceConstant.METHOD_SUCCESS)
														LogInstance.error(GlobalPara.Tky, "120." + tag + ": serviceMgr.cancelCall failed");
												}
											}
										}
										else if (tCall.callType != ServiceConstant.CALL_TYPE_SINGLE && !tCall.strCallId.equals(call.strCallId))
										{
											//组呼参与者或者发起者
											if (mTrainState.g_DebugLog_Lev1 != 0x00)
												LogInstance.debug(GlobalPara.Tky, "exit group call because of 299: callee");
											String strCurrentGroupId = tCall.groupId;

											if(tCall.call_way == call_in)
											{
												if (mTrainState.g_DebugLog_Lev1 != 0x00)
													LogInstance.debug(GlobalPara.Tky, "exitGroupCall because of 299: groupId=" + strCurrentGroupId + " callType="+tCall.callType + " priority="+tCall.priority
															+" strCallId="+tCall.strCallId);

												int retCode = serviceMgr.exitCall(tCall.strCallId, ServiceConstant.CALL_REASON_SEIZED);
												if (retCode != ServiceConstant.METHOD_SUCCESS)
													LogInstance.error(GlobalPara.Tky, "121." + tag + ": serviceMgr.exitCall failed");
											}
											else
											{
												if (mTrainState.g_DebugLog_Lev1 != 0x00)
													LogInstance.debug(GlobalPara.Tky, "endGroupCall because of 299: groupId=" + strCurrentGroupId + " callType="+tCall.callType + " priority="+tCall.priority
															+" strCallId="+tCall.strCallId);

												int retCode = serviceMgr.endCall(tCall.strCallId, ServiceConstant.CALL_REASON_SEIZED);
												if (retCode != ServiceConstant.METHOD_SUCCESS)
													LogInstance.error(GlobalPara.Tky, "121." + tag + ": serviceMgr.strCallId failed");
											}
										}
									}

									callList.add(call);
									if (callList.size() == 1)
									{
										//LogInstance.debug(GlobalPara.Tky, "call copy_call2Currentcall3 when callList.size() = 1 in incomingCallNotify");
										copy_call2Currentcall(call);
									}

									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "answerCall 299: groupId=" + call.groupId + " callType="+call.callType + " priority="+call.priority+ " strCallId="+call.strCallId);
									int retCode = serviceMgr.answerCall(call.strCallId, ServiceConstant.ANSWER_TYPE_AUTO);
									if (retCode != ServiceConstant.METHOD_SUCCESS)
										LogInstance.error(GlobalPara.Tky, "122." + tag + ": serviceMgr.answerCall failed");
								}
								else if(call.priority > 2)
								{//3级和4级低级呼叫
									if(callList.size() == 0)
									{//新呼叫为唯一呼叫
										callList.add(call);

										//LogInstance.debug(GlobalPara.Tky, "call copy_call2Currentcall4 when answer one call in incomingCallNotify");

										copy_call2Currentcall(call);

										//若为组呼且为唯一的呼叫,则应该自动接听
										if(call.callType != ServiceConstant.CALL_TYPE_SINGLE)
										{
											if (mTrainState.g_DebugLog_Lev1 != 0x00)
												LogInstance.debug(GlobalPara.Tky, "auto answerCall this only group call: peerNumber=" + call.peerNumber +" funNumber=" + call.funNumber
														+" groupId=" + call.groupId + " callType="+call.callType + " priority="+call.priority +" strCallId="+call.strCallId);

											int retCode = serviceMgr.answerCall(call.strCallId, ServiceConstant.ANSWER_TYPE_AUTO);
											if (retCode != ServiceConstant.METHOD_SUCCESS)
												LogInstance.error(GlobalPara.Tky, " serviceMgr.answerCall failed");
										}
										else
										{
                                            if(mTrainState.g_AutoAnswer.equals("1"))//测试自动接听,打进来的电话自动接听
                                            {
                                                if (mTrainState.g_DebugLog_Lev1 != 0x00)
                                                    LogInstance.debug(GlobalPara.Tky, "fes test: auto answerCall this only group call: peerNumber=" + call.peerNumber +" funNumber=" + call.funNumber
                                                            +" groupId=" + call.groupId + " callType="+call.callType + " priority="+call.priority +" strCallId="+call.strCallId);

                                                int retCode = serviceMgr.answerCall(call.strCallId, ServiceConstant.ANSWER_TYPE_AUTO);
                                                if (retCode != ServiceConstant.METHOD_SUCCESS)
                                                    LogInstance.error(GlobalPara.Tky, "fes test: serviceMgr.answerCall failed");
                                            }
										}
									}
									else
									{//已存在呼叫,此时mCurrentCall应该不为空
										if(mCurrentCall != null && mCurrentCall.strCallId !=null  && !mCurrentCall.strCallId.equals("")&& mCurrentCall.priority < 3
												&&( mCurrentCall.statusCode == ServiceConstant.CALL_STATE_IN_CALL || mCurrentCall.statusCode == ServiceConstant.CALL_STATE_HOLD|| mCurrentCall.statusCode == ServiceConstant.CALL_STATE_HOLDED))
										{//当前处于通话中的通话优先级(0/1/2)比新呼叫(3/4)高,则直接拒绝新的呼叫
											if (mTrainState.g_DebugLog_Lev1 != 0x00)
												LogInstance.debug(GlobalPara.Tky, "rejectCall a new low priority call when a high priority call is doing: peerNumber=" + call.peerNumber +" funNumber=" + call.funNumber
														+" groupId=" + call.groupId + " callType="+call.callType + " priority="+call.priority +" strCallId="+call.strCallId);

											int retCode =serviceMgr.rejectCall(call.strCallId, ServiceConstant.CALL_REASON_HANDLE );
											if (retCode != ServiceConstant.METHOD_SUCCESS)
												LogInstance.error(GlobalPara.Tky, "124." + tag + ":serviceMgr.rejectCall failed");
										}
										else
										{//都为同级呼叫,加入呼叫列表
											if (mTrainState.g_DebugLog_Lev1 != 0x00)
												LogInstance.debug(GlobalPara.Tky, "add a new low priority call in callist: peerNumber=" + call.peerNumber +" funNumber=" + call.funNumber
														+" groupId=" + call.groupId + " callType="+call.callType + " priority="+call.priority+" strCallId="+call.strCallId);
											callList.add(call);
										}
									}
								}
								else if (call.priority < 3)
								{//012级高级呼叫
									if(callList.size() == 0)
									{//新呼叫为唯一呼叫
										callList.add(call);
										//LogInstance.debug(GlobalPara.Tky, "call copy_call2Currentcall5 when answer one call in incomingCallNotify");

										copy_call2Currentcall(call);
										//应该自动接听该高优先级呼叫,不管是否是组呼
										if (mTrainState.g_DebugLog_Lev1 != 0x00)
											LogInstance.debug(GlobalPara.Tky, "auto answerCall this high priroty call: peerNumber=" + call.peerNumber +" funNumber=" + call.funNumber
													+" groupId=" + call.groupId + " callType="+call.callType + " priority="+call.priority+ " strCallId="+call.strCallId);
										int retCode = serviceMgr.answerCall(call.strCallId, ServiceConstant.ANSWER_TYPE_AUTO);
										if (retCode != ServiceConstant.METHOD_SUCCESS)
											LogInstance.error(GlobalPara.Tky, "125." + tag + ": serviceMgr.answerCall failed");
									}
									else
									{//已存在呼叫,此时mCurrentCall应该不为空
										if( mCurrentCall != null && mCurrentCall.strCallId !=null  && !mCurrentCall.strCallId.equals("")
												&& mCurrentCall.priority <= call.priority
												&&( mCurrentCall.statusCode == ServiceConstant.CALL_STATE_IN_CALL || mCurrentCall.statusCode == ServiceConstant.CALL_STATE_HOLD|| mCurrentCall.statusCode == ServiceConstant.CALL_STATE_HOLDED))
										{//当前处于通话中的通话优先级(0/1/2)比新呼叫(0/1/2)高,则直接将其添加入呼叫列表即可，由用户决定是否接听
											if (mTrainState.g_DebugLog_Lev1 != 0x00)
												LogInstance.debug(GlobalPara.Tky, "add a new high priority call in the high priority callist: peerNumber=" + call.peerNumber +" funNumber=" + call.funNumber
														+" groupId=" + call.groupId + " callType="+call.callType + " priority="+call.priority+" strCallId="+call.strCallId);

											callList.add(call);
										}
										else
										{//当前处于通话中的通话为低优先级(3/4)来了新的高优先级呼叫(0/1/2),如果新呼叫为组呼或者原呼叫为组呼则挂掉原呼叫自动接听新高优先级呼叫，只有在新呼叫原呼叫都为个呼时才保持
											if(mCurrentCall.callType == ServiceConstant.CALL_TYPE_SINGLE && call.callType == ServiceConstant.CALL_TYPE_SINGLE)
											{//保持原个呼,自动接入新的高级个呼
												if (mTrainState.g_DebugLog_Lev1 != 0x00)
													LogInstance.debug(GlobalPara.Tky, "add and answer a new high priority call in the high priority callist: peerNumber=" + call.peerNumber +" funNumber=" + call.funNumber
															+" groupId=" + call.groupId + " callType="+call.callType + " priority="+call.priority+" strCallId="+call.strCallId);

												callList.add(call);

												//标记开始操作holdcall
												bIsHoldSuccessful = -1;
												int iTimersForHoldcallCheck = 0;
												int retCode = serviceMgr.holdCall(mCurrentCall.strCallId);
												if (retCode != ServiceConstant.METHOD_SUCCESS)
													LogInstance.error(GlobalPara.Tky,"126." + tag + ": serviceMgr.holdCall single call failed");
												//等待hold成功
												while(iTimersForHoldcallCheck < 10)
												{
													if(bIsHoldSuccessful != -1)
														break;
													Thread.sleep(100);
													LogInstance.debug(GlobalPara.Tky, "iTimersForHoldcallCheck:"+iTimersForHoldcallCheck);
													iTimersForHoldcallCheck++;
												}
												if(bIsHoldSuccessful == 1)
												{
													if (mTrainState.g_DebugLog_Lev1!=0x00)
														LogInstance.debug(GlobalPara.Tky, " start to answercall because holdcall successful");
													retCode = serviceMgr.answerCall(call.strCallId, ServiceConstant.ANSWER_TYPE_AUTO);
													if (retCode != ServiceConstant.METHOD_SUCCESS)
														LogInstance.error(GlobalPara.Tky, "127." + tag + ": serviceMgr.answerCall highe single call failed");
												}
												else if (bIsHoldSuccessful == 0)
												{
													if (mTrainState.g_DebugLog_Lev1!=0x00)
														LogInstance.error(GlobalPara.Tky, "128." + tag + ": don't answercall because holdcall failed, attention!");
												}
											}
											else
											{
												if (mTrainState.g_DebugLog_Lev1 != 0x00)
													LogInstance.debug(GlobalPara.Tky, "close low priority calls, then answer new high priority call: peerNumber=" + call.peerNumber +" funNumber=" + call.funNumber
															+" groupId=" + call.groupId + " callType="+call.callType + " priority="+call.priority+" strCallId="+call.strCallId);

												for (int i = 0; i < callList.size(); i++)
												{// 新接入组呼时,应该是挂掉所有个呼(比如当前正有两个通话,只挂掉当前是不够的)
													CALL tCall = callList.get(i);
													if (tCall.callType == ServiceConstant.CALL_TYPE_SINGLE && !tCall.strCallId.equals(call.strCallId))// 个呼
													{
														if (mTrainState.g_DebugLog_Lev1 != 0x00)
															LogInstance.debug(GlobalPara.Tky, "sized to end: " + tCall.peerNumber);
														if(tCall.statusCode == ServiceConstant.CALL_STATE_IN_CALL
																|| tCall.statusCode == ServiceConstant.CALL_STATE_HOLD
																|| tCall.statusCode == ServiceConstant.CALL_STATE_HOLDED)
														{//通话中则为endcall
															if (mTrainState.g_DebugLog_Lev1 != 0x00)
																LogInstance.debug(GlobalPara.Tky, "serviceMgr.endCall: peerNumber="+tCall.peerNumber + " funNumber="+tCall.funNumber + " callType="+tCall.callType + " priority="+tCall.priority+" strCallId=" + tCall.strCallId);
															int retCode = serviceMgr.endCall(tCall.strCallId, ServiceConstant.CALL_REASON_SEIZED);

															if (retCode != ServiceConstant.METHOD_SUCCESS)
															{
																LogInstance.error(GlobalPara.Tky, "129." + tag + ": serviceMgr.endCall failed");
																if(callList.size()>0)
																{
																	i--;//在for循环中动态删除
																	callList.remove(tCall);
																	LogInstance.debug(GlobalPara.Tky, "should remove this call from calllist1");
																}
															}
														}
														else
														{//其他状态,如果主叫则cancelcall,如果被叫则为rejectcall
															if(tCall.call_way == call_in)
															{//rejectcall
																if (mTrainState.g_DebugLog_Lev1 != 0x00)
																	LogInstance.debug(GlobalPara.Tky, "serviceMgr.rejectCall: peerNumber="+tCall.peerNumber + " funNumber="+tCall.funNumber + " callType="+tCall.callType + " priority="+tCall.priority+" strCallId=" + tCall.strCallId);
																int retCode = serviceMgr.rejectCall(tCall.strCallId, ServiceConstant.CALL_REASON_SEIZED);
																if (retCode != ServiceConstant.METHOD_SUCCESS)
																	LogInstance.error(GlobalPara.Tky,"130." + tag + ": serviceMgr.rejectCall failed");
															}
															else
															{//cancelcall
																if (mTrainState.g_DebugLog_Lev1 != 0x00)
																	LogInstance.debug(GlobalPara.Tky, "serviceMgr.cancelCall: peerNumber="+tCall.peerNumber + " funNumber="+tCall.funNumber + " callType="+tCall.callType + " priority="+tCall.priority+ " strCallId=" + tCall.strCallId);
																int retCode = serviceMgr.cancelCall(tCall.strCallId, ServiceConstant.CALL_REASON_SEIZED);
																if (retCode != ServiceConstant.METHOD_SUCCESS)
																	LogInstance.error(GlobalPara.Tky, "131." + tag + ": serviceMgr.cancelCall failed");
															}
														}
													}
													else if (tCall.callType != ServiceConstant.CALL_TYPE_SINGLE && !tCall.strCallId.equals(call.strCallId))
													{
														//组呼参与者或发起者都是退出
														if (mTrainState.g_DebugLog_Lev1 != 0x00)
															LogInstance.debug(GlobalPara.Tky, "seized to end: exit group call, callee");
														String strCurrentGroupId = tCall.groupId;

														if(tCall.call_way == call_in)
														{
															if (mTrainState.g_DebugLog_Lev1 != 0x00)
																LogInstance.debug(GlobalPara.Tky, "serviceMgr.exitGroupCall: groupId=" + strCurrentGroupId + " callType="+tCall.callType + " priority="+tCall.priority+" strCallId="+tCall.strCallId);
															int retCode = serviceMgr.exitCall(tCall.strCallId, ServiceConstant.CALL_REASON_SEIZED);
															if (retCode != ServiceConstant.METHOD_SUCCESS)
																LogInstance.error(GlobalPara.Tky, "132." + tag + ": serviceMgr.exitCall failed");
														}
														else {
															if (mTrainState.g_DebugLog_Lev1 != 0x00)
																LogInstance.debug(GlobalPara.Tky, "serviceMgr.endGroupCall: groupId=" + strCurrentGroupId + " callType="+tCall.callType + " priority="+tCall.priority+" strCallId="+tCall.strCallId);
															int retCode = serviceMgr.endCall(tCall.strCallId, ServiceConstant.CALL_REASON_SEIZED);
															if (retCode != ServiceConstant.METHOD_SUCCESS)
																LogInstance.error(GlobalPara.Tky, "132." + tag + ": serviceMgr.endCall failed");
														}
													}
												}

												callList.add(call);
												if (callList.size() == 1)
												{
													//LogInstance.debug(GlobalPara.Tky, "call copy_call2Currentcall6 when allList.size() == 1 in incomingCallNotify");
													copy_call2Currentcall(call);
												}
												if (mTrainState.g_DebugLog_Lev1 != 0x00)
													LogInstance.debug(GlobalPara.Tky, "answerCall new high call: groupId=" + call.groupId + " callType="+call.callType + " priority="+call.priority+" strCallId="+call.strCallId);
												int retCode = serviceMgr.answerCall(call.strCallId, ServiceConstant.ANSWER_TYPE_AUTO);
												if (retCode != ServiceConstant.METHOD_SUCCESS)
													LogInstance.error(GlobalPara.Tky, "133." + tag + ": serviceMgr.answerCall failed");
											}
										}
									}
								}

								ShowCallChangeOnWindows();
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
						return 0;
					}

					@Override
					public int callStatusNotify(String szCallID, int nCallType, int nStatus, int nPriority, int nCallRole, String szReason)
					{//空闲,通话,保持,被保持
						if (mTrainState.g_DebugLog_Lev1 != 0x00)
							LogInstance.debug(GlobalPara.Tky, "callStatusNotify: szCallID=" + szCallID+ " nCallType=" + nCallType + " nStatus=" + nStatus
									+ " nPriority=" + nPriority + " nCallRole=" + nCallRole + " szReason=" + szReason);

						try
						{
							boolean findcall = false;
							int i;
							CALL call = null;
							synchronized (pocevent)
							{
								if (szCallID != null && !szCallID.equals("") && mCurrentCall.strCallId != null
										&& mCurrentCall.strCallId.equals(szCallID))
								{
									mCurrentCall.callType = nCallType;
									mCurrentCall.statusCode = nStatus;
									mCurrentCall.priority = nPriority;
									if (nCallRole == ServiceConstant.CALL_ROLE_CREATE)// 呼出
										mCurrentCall.call_way = call_out;
									else if (nCallRole == ServiceConstant.CALL_ROLE_JOIN)
										mCurrentCall.call_way = call_in;
									//如果此时还无列表,添加之
									if(nStatus != ServiceConstant.CALL_STATE_IDLE && callList.size()==0)
									{
										call = new CALL();
										call.strCallId = szCallID;
										call.callType = nCallType;
										call.peerNumber = mCurrentCall.peerNumber;
										call.funNumber = mCurrentCall.funNumber;
										call.groupId = mCurrentCall.groupId;
										call.statusCode = nStatus;
										call.priority = nPriority;

										if (nCallRole == ServiceConstant.CALL_ROLE_CREATE)// 呼出
											call.call_way = call_out;
										else if (nCallRole == ServiceConstant.CALL_ROLE_JOIN)
											call.call_way = call_in;
										call.call_handout = false;
										callList.add(call);
									}
								}

								if (nStatus == ServiceConstant.CALL_STATE_IDLE)// 呼叫空闲，删除呼叫
								{
									for (i = callList.size() - 1; i >= 0; i--)
									{
										call = callList.get(i);
										if (call.strCallId != null && !call.strCallId.equals(""))
										{
											if (call.strCallId.equals(szCallID))
											{
												callList.remove(i);
												if(mTrainState.g_DebugLog_Lev1 != 0x00)
													LogInstance.debug(GlobalPara.Tky, "remove a call from callList");
												break;
											}
										}
									}
									call = null;
								}
								else
								{// 通话,保持,被保持状态
									for (i = 0; i < callList.size(); i++)
									{
										call = callList.get(i);
										if ((call.strCallId != null && !call.strCallId.equals("") && call.strCallId.equals(szCallID)))
										{
											findcall = true;
											call.callType = nCallType;
											call.statusCode = nStatus;
											call.priority = nPriority;
											if (nCallRole == ServiceConstant.CALL_ROLE_CREATE)// 呼出
												call.call_way = call_out;
											else if (nCallRole == ServiceConstant.CALL_ROLE_JOIN)
												call.call_way = call_in;
											break;
										}
									}

									if (!findcall)
									{
										call = new CALL();
										call.strCallId = szCallID;
										call.callType = nCallType;

										if( mCurrentCall.strCallId != null && !mCurrentCall.strCallId.equals("") && mCurrentCall.strCallId.equals(szCallID))
										{
											call.peerNumber = mCurrentCall.peerNumber;
											call.funNumber = mCurrentCall.funNumber;
											call.groupId = mCurrentCall.groupId;
										}

										call.statusCode = nStatus;
										call.priority = nPriority;

										if (nCallRole == ServiceConstant.CALL_ROLE_CREATE)// 呼出
											call.call_way = call_out;
										else if (nCallRole == ServiceConstant.CALL_ROLE_JOIN)
											call.call_way = call_in;
										call.call_handout = false;
										callList.add(call);
									}
								}

								if (callList.size() == 0)
								{
									clear_currentCall();
								}
								else if (callList.size() == 1)
								{
									//LogInstance.debug(GlobalPara.Tky, "call copy_call2Currentcall1 when callList.size() == 1 in callStatusNotify");

									copy_call2Currentcall(callList.get(0));

									if(callList.get(0).statusCode == ServiceConstant.CALL_STATE_HOLD )
									{
										if(mTrainState.g_DebugLog_Lev1!=0x00)
											LogInstance.debug(GlobalPara.Tky, "only one call in calllist and its statecode == CALL_STATE_HOLD, so should recoveryCall");

										int  retCode2 = serviceMgr.recoveryCall(callList.get(0).strCallId);
										if (retCode2 != ServiceConstant.METHOD_SUCCESS)
											LogInstance.error(GlobalPara.Tky, "105." + tag + ": serviceMgr.recoveryCall failed");
									}
								}
								else
								{//两个或更多通话存在,(如果此时call==null,即有个通话挂断了),选择当前通话中的那个放入currentcall,如果都不是通话状态则需要从剩余的通话取出一个放入mCurrentCall
									if(call == null)
									{
										CALL aCall = null;
										for (CALL tmpCall : callList)
										{
											if(tmpCall.statusCode == ServiceConstant.CALL_STATE_IN_CALL)
											{
												aCall = tmpCall;
												break;
											}
											if(aCall == null)
											{
												aCall = tmpCall;
												continue;
											}
											if(tmpCall.priority < aCall.priority)
											{//优先级更高的通话
												aCall = tmpCall;
											}
										}
										if(aCall != null)
										{
											//LogInstance.debug(GlobalPara.Tky, "call copy_call2Currentcall2 when one call finish and callList.size() > 1 in callStatusNotify");

											copy_call2Currentcall(aCall);
										}
									}
								}

								ShowCallChangeOnWindows();
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
						return 0;
					}

					@Override
					public int preemptPTTNotify(String szCallID, String szReason)
					{//强制终端释放话权通知
						if (mTrainState.g_DebugLog_Lev1 != 0x00)
							LogInstance.debug(GlobalPara.Tky, "preemptPTTNotify: szCallID=" + szCallID + " szReason=" + szReason);

						try
						{
							synchronized (objPttOpeation)
							{
								if ((szCallID != null && !szCallID.equals("")))
								{ // 需要修改,这种判断条件会导致抢占失败信息不会发向MMI
									//mnLTEPTTStatus:0 - PTT空闲，1:PTT占用忙；2:PTT请求占用成功，3:PTT请求失败。
									mnLTEPTTStatus = 0;//被强制释放

									if (mTrainState.g_DebugLog_Lev1 != 0x00)//20141011
										LogInstance.debug(GlobalPara.Tky,"mnLTEPTTStatus="+mnLTEPTTStatus);

									ShowPttStateOnWindows(mnLTEPTTStatus, szCallID,  "");

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
						return 0;
					}

					@Override
					public int pttStatusNotify(String szCallID, int nStatus, String szSpeakerFnNum)
					{//服务器通知
						if (nStatus == ServiceConstant.PTT_PRESS)
							LogInstance.debug(GlobalPara.Tky, "pttStatusNotify: nStatus=PTT_PRESS"  + " szSpeakerFnNum=" + szSpeakerFnNum);
						else if (nStatus == ServiceConstant.PTT_RELEASE)
							LogInstance.debug(GlobalPara.Tky, "pttStatusNotify: nStatus=PTT_RELEASE"  + " szSpeakerFnNum=" + szSpeakerFnNum);
						else if (nStatus == ServiceConstant.PTT_IDEL)
							LogInstance.debug(GlobalPara.Tky, "pttStatusNotify: nStatus=PTT_IDEL"  + " szSpeakerFnNum=" + szSpeakerFnNum);
						else if (nStatus == ServiceConstant.PTT_TALK)
							LogInstance.debug(GlobalPara.Tky, "pttStatusNotify: nStatus=PTT_TALK"  + " szSpeakerFnNum=" + szSpeakerFnNum);

						try
						{
							synchronized (objPttOpeation)
							{
								if ( nStatus == ServiceConstant.PTT_IDEL)// 对应于其他讲者释放话权也通告车载可以抢占话权了 *
								{
									mnLTEPTTStatus = 0;
								}
								else if( nStatus == ServiceConstant.PTT_TALK && !szSpeakerFnNum.equals(mtrainNum) && !szSpeakerFnNum.equals(mLocofunNum))
								{
									mnLTEPTTStatus = 4;//或者换一个状态,听者状态
									if(mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "some on is talking, please listen");
								}
								else
								{//组呼开始时自动获得授权
									mnLTEPTTStatus = 2;
								}

								if (mTrainState.g_DebugLog_Lev1 != 0x00)//20141011
									LogInstance.debug(GlobalPara.Tky,"mnLTEPTTStatus="+mnLTEPTTStatus);

								ShowPttStateOnWindows(mnLTEPTTStatus, szCallID,  szSpeakerFnNum);
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
						return 0;
					}

					@Override
					public int pttOperateResponse(String szCallID, int nCode, int nResult, String szReason)
					{//ptt抢权释放话权是否成功响应
						if (nResult == ServiceConstant.RESPONSE_SUCCESS) {//
							if (nCode == ServiceConstant.PTT_PRESS)
								LogInstance.debug(GlobalPara.Tky, "pttOperateResponse: nCode=PTT_PRESS" + " nResult=RESPONSE_SUCCESS");
							else
								LogInstance.debug(GlobalPara.Tky, "pttOperateResponse: nCode=PTT_RELEASE" + " nResult=RESPONSE_SUCCESS");
						}else {
							if (nCode == ServiceConstant.PTT_PRESS)
								LogInstance.debug(GlobalPara.Tky, "pttOperateResponse: nCode=PTT_PRESS" + " nResult=RESPONSE_FAILED");
							else
								LogInstance.debug(GlobalPara.Tky, "pttOperateResponse: nCode=PTT_RELEASE" + " nResult=RESPONSE_FAILED");
						}
						try
						{
							synchronized (objPttOpeation)
							{
								if ((szCallID != null && !szCallID.equals("")))
								{
									//mnLTEPTTStatus:0 - PTT空闲，1:PTT占用忙；2:PTT请求占用成功，3:PTT请求失败。
									if(nCode == ServiceConstant.PTT_PRESS && nResult == ServiceConstant.RESPONSE_SUCCESS)
									{//申请成功
										mnLTEPTTStatus = 2;
									}
									else if(nCode == ServiceConstant.PTT_PRESS && nResult == ServiceConstant.RESPONSE_FAILED)
									{//申请失败,占用忙
										mnLTEPTTStatus = 1;
										//mnLTEPTTStatus = 4;//20141011 统一改成4
									}
									else if(nCode == ServiceConstant.PTT_RELEASE && nResult == ServiceConstant.RESPONSE_SUCCESS)
									{//释放成功,空闲
										mnLTEPTTStatus = 0;
									}
									else if(nCode == ServiceConstant.PTT_RELEASE && nResult == ServiceConstant.RESPONSE_FAILED)
									{//释放失败,仍然占用着
										//mnLTEPTTStatus = 2;//释放失败仍然
									}

									if (mTrainState.g_DebugLog_Lev1 != 0x00)//20141011
										LogInstance.debug(GlobalPara.Tky,"mnLTEPTTStatus="+mnLTEPTTStatus);

									ShowPttStateOnWindows(mnLTEPTTStatus, szCallID,  "");
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
						return 0;
					}

					@Override
					public int fnQueryListResponse(ArrayList<String> funcNumList, int resultCode)
					{
						String strFuncNumList = "";
						if(funcNumList != null)
							for (String str : funcNumList)
								strFuncNumList += str + ",";

						if (resultCode == ServiceConstant.RESPONSE_SUCCESS)
							LogInstance.debug(GlobalPara.Tky, "fnQueryListResponse: funcNumList="+strFuncNumList+" resultCode=RESPONSE_SUCCESS");
						else
							LogInstance.debug(GlobalPara.Tky, "fnQueryListResponse: funcNumList="+strFuncNumList+" resultCode=RESPONSE_FAILED");

						try
						{
							if (resultCode == ServiceConstant.RESPONSE_SUCCESS)
							{
								lstFuncNum.clear();
								for (String str : funcNumList)
									lstFuncNum.add(str);

								int i = 0 ;
								boolean bFind = false;
								for(i = 0; i< lstFuncNum.size(); i++){
									if(lstFuncNum.get(i).startsWith("2")||lstFuncNum.get(i).startsWith("0862")) {
										mtrainNum = lstFuncNum.get(i);
										bFind = true;
										break;
									}
								}
								if(!bFind)
									mtrainNum = "";

								bFind = false;
								for(i = 0; i< lstFuncNum.size(); i++){
									if(lstFuncNum.get(i).startsWith("3") || lstFuncNum.get(i).startsWith("0863")) {
										mLocofunNum = lstFuncNum.get(i);

										bFind = true;//fwg2109

										break;
									}
								}
								if(!bFind)
									mLocofunNum = "";

								ShowFunctionNumListOnWindows(0 , funcNumList);

								//
								for(i = 0; i< lstFuncNum.size(); i++) {
									if (lstFuncNum.get(i).startsWith("2") || lstFuncNum.get(i).startsWith("0862")
											|| lstFuncNum.get(i).startsWith("3") || lstFuncNum.get(i).startsWith("0863")) {
										ShowInfoOnWindows("shengfenstate", lstFuncNum.get(i),"");
										break;
									}
								}
							}
							else
							{
								ShowFunctionNumListOnWindows(-1 , funcNumList);
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
						return 0;
					}

					@Override
					public int fnQueryResponse(String szFN, String szUN, int nResult, String szReason)
					{//查询某个功能码被谁使用
						try{//

							if(nResult == ServiceConstant.RESPONSE_SUCCESS)
							{//若为ServiceConstant.RESPONSE_FAILED表示该功能号尚未被注册,不用查出来强制注销
								LogInstance.debug(GlobalPara.Tky, "fnQueryResponse successful: szFN="+szFN+", szUN="+szUN+ ", nResult=RESPONSE_SUCCESS"+", szReason=" + szReason);

								ShowFunctionNumIsUsedOnWindows(0,szFN,szUN,szReason);
							}
							else
							{
								LogInstance.debug(GlobalPara.Tky, "fnQueryResponse failed: szFN="+szFN+", szUN="+szUN+ ", nResult=RESPONSE_FAILED"+", szReason=" + szReason);
								bCouldRegisterFunctionNumber = true;

								ShowFunctionNumIsUsedOnWindows(-1,szFN,szUN,szReason);//
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
						return 0;
					}

					@Override
					public int fnRegisterResponse(String szFN, int nResult, String szReason)
					{
						if(nResult == ServiceConstant.RESPONSE_SUCCESS)
							LogInstance.debug(GlobalPara.Tky, "fnRegisterResponse: szFN="+szFN+", nResult=RESPONSE_SUCCESS" +", szReason=" + szReason);
						else
							LogInstance.debug(GlobalPara.Tky, "fnRegisterResponse: szFN="+szFN+", nResult=RESPONSE_FAILED" +", szReason=" + szReason);

						try
						{
							if (szFN.startsWith(GlobalPara.strPreTfnNumber) || szFN.startsWith(GlobalPara.strPreGlobalNumber + GlobalPara.strPreTfnNumber))
							{
								if (nResult == ServiceConstant.RESPONSE_SUCCESS )
								{
									mTrainState.TrainNumberFNRegisterStatus = 1;
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "4.TrainNumberFNRegisterStatus=" + mTrainState.TrainNumberFNRegisterStatus);

									mParamOperation.SaveSpecialField("RegisterStatus", ("1"));
									mTrainState.TrainFunctionNumber = szFN;
									mtrainNum = szFN;

									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "trainNumber register successful: " + nResult);

									ShowFunctionRegisterResponseOnWindows(2,1, 0, szFN, szReason);
								}
								else if (nResult == ServiceConstant.RESPONSE_FAILED )
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.error(GlobalPara.Tky, "109." + tag + ": trainNumber register failed: " + nResult);

									ShowFunctionRegisterResponseOnWindows(2, 1, -1, szFN, szReason);
								}
							}
							if (szFN.startsWith(GlobalPara.strPreEfnNumber) || szFN.startsWith(GlobalPara.strPreGlobalNumber + GlobalPara.strPreEfnNumber))//
							{
								if (nResult == ServiceConstant.RESPONSE_SUCCESS)
								{
									mTrainState.EngineNumberFNRegisterStatus = 1; // 功能号已注册到本机号码
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "4.EngineNumberFNRegisterStatus=" + mTrainState.EngineNumberFNRegisterStatus);

									mTrainState.EngineFunctionNumber = szFN;
									mLocofunNum = szFN;

									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "enginenumber register successful: " + nResult);

									ShowFunctionRegisterResponseOnWindows(3, 1, 0, szFN, szReason);
								}
								else if (nResult == ServiceConstant.RESPONSE_FAILED )
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.error(GlobalPara.Tky, "111." + tag + ": enginenumber register failed: " + nResult);

									ShowFunctionRegisterResponseOnWindows(2, 1, -1, szFN, szReason);

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

						return 0;
					}

					@Override
					public int fnUnregisterResponse(String szFN, int nResult, String szReason)
					{
						if (nResult == ServiceConstant.RESPONSE_SUCCESS)
							LogInstance.debug(GlobalPara.Tky, "fnUnregisterResponse: szFN=" + szFN + " nResult=RESPONSE_SUCCESS" + " szReason=" + szReason);
						else
							LogInstance.debug(GlobalPara.Tky, "fnUnregisterResponse: szFN=" + szFN + " nResult=RESPONSE_FAILED" + " szReason=" + szReason);

						try
						{
							if (szFN.startsWith( GlobalPara.strPreTfnNumber) || szFN.startsWith(GlobalPara.strPreGlobalNumber + GlobalPara.strPreTfnNumber))
							{
								if (nResult == ServiceConstant.RESPONSE_SUCCESS)
								{
									mTrainState.TrainNumberFNRegisterStatus = 0; // 原是mTrainState.TrainNumberFNRegisterStatus = 1,应该是错误的,0代表注销;
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "2.TrainNumberFNRegisterStatus = " + mTrainState.TrainNumberFNRegisterStatus);

									mParamOperation.SaveSpecialField("RegisterStatus", ("0"));
									mTrainState.TrainFunctionNumber = "";
									mtrainNum = "";

									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "trainNumber unregister successful: " + nResult);

									ShowFunctionRegisterResponseOnWindows(2, 2, 0, szFN, szReason);
								}
								else if (nResult == ServiceConstant.RESPONSE_FAILED)
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.error(GlobalPara.Tky, "113." + tag + ": trainNumber unregister failed: " + nResult);

									ShowFunctionRegisterResponseOnWindows(2, 2, -1, szFN, szReason);

								}
							}
							if (szFN.startsWith(GlobalPara.strPreEfnNumber) || szFN.startsWith(GlobalPara.strPreGlobalNumber + GlobalPara.strPreEfnNumber))//
							{
								if (nResult == ServiceConstant.RESPONSE_SUCCESS)
								{
									mTrainState.EngineNumberFNRegisterStatus = 0;// 原是mTrainState.EngineNumberFNRegisterStatus  =  1,应该是错误的,0代表注销;
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "2.EngineNumberFNRegisterStatus=" + mTrainState.EngineNumberFNRegisterStatus);

									mTrainState.EngineFunctionNumber = "";
									mLocofunNum = "";

									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "enginenumber unregister successful: " + nResult);

									ShowFunctionRegisterResponseOnWindows(3, 2, 0, szFN, szReason);
								}
								else if (nResult == ServiceConstant.RESPONSE_FAILED)
								{
									LogInstance.error(GlobalPara.Tky, "114." + tag + ": fnUnregisterResponse failed: szFN="+szFN+" szReason="+szReason);

									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.error(GlobalPara.Tky, "115." + tag + ": enginenumber unregister failed: " + nResult);

									ShowFunctionRegisterResponseOnWindows(3, 2, -1, szFN, szReason);

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
						return 0;
					}

					@Override
					public int fnForceUnregisterResponse(String szFN, String szUN, int nResult, String szReason)
					{//强制注销响应
						if(nResult == ServiceConstant.RESPONSE_FAILED)
						{//该功能号未被注册,可以注册
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.error(GlobalPara.Tky, "104." + tag + ": fnForceUnregisterResponse failed: szFN="+szFN+", szUN="+szUN+
										", nResult=RESPONSE_FAILED" +", szReason=" + szReason);

							bCouldRegisterFunctionNumber = true;
						}
						else
						{//强制注销成功,可以注册
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "fnForceUnregisterResponse successful: szFN="+szFN+", szUN="+szUN+
										", nResult=REPONSE_SUCCESS" +", szReason=" + szReason);

							bCouldRegisterFunctionNumber = true;
						}

						try{//
							ShowForceUnregisterResponseOnWindows(szFN,szUN,nResult,szReason);
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
						return 0;
					}

					@Override
					public int fnForceUnregisteredNotify(String szFN, String szReason, String szUN)
					{//被其他人强制注销本机的功能号
						if (mTrainState.g_DebugLog_Lev1 != 0x00)
							LogInstance.debug(GlobalPara.Tky, "fnForceUnregisteredNotify: szFN="+szFN+", szReason=" + szReason+", szUN="+szUN);

						try
						{
							int iUnregisteredType = -1;//0车次号注销1机车号注销
							if(szFN != null && !szFN.equals(""))
							{
								if(szFN.startsWith(GlobalPara.strPreTfnNumber) || szFN.startsWith(GlobalPara.strPreGlobalNumber + GlobalPara.strPreTfnNumber))
									iUnregisteredType = 0;
								else if(szFN.startsWith(GlobalPara.strPreEfnNumber) || szFN.startsWith(GlobalPara.strPreGlobalNumber + GlobalPara.strPreEfnNumber))
									iUnregisteredType = 1;
							}

							if (iUnregisteredType == 0)// 车次号注销
							{
								if(szFN.equals(mtrainNum))
								{
									mTrainState.TrainNumberFNRegisterStatus = 0; // 原是mTrainState.TrainNumberFNRegisterStatus = 1,应该是错误的,0代表注销;
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "5.TrainNumberFNRegisterStatus=" + mTrainState.TrainNumberFNRegisterStatus);

									mParamOperation.SaveSpecialField("RegisterStatus", ("0"));
									mTrainState.TrainFunctionNumber = "";
									mtrainNum = "";

									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "trainNumber unregister successful forcely by:"+szUN);

									ShowFunctionRegisterResponseOnWindows(2, 2, 0, szFN, szReason);
								}
							}
							else if (iUnregisteredType == 1)// 机车号注销
							{
								if(szFN.equals(mLocofunNum))
								{
									mTrainState.EngineNumberFNRegisterStatus = 0;// 原是mTrainState.EngineNumberFNRegisterStatus  =  1,应该是错误的,0代表注销;
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "5.EngineNumberFNRegisterStatus=" + mTrainState.EngineNumberFNRegisterStatus);

									mTrainState.EngineFunctionNumber = "";
									mLocofunNum = "";

									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "engineNumber unregister successful forcely by:"+szUN);

									ShowFunctionRegisterResponseOnWindows(3, 2, 0, szFN, szReason);
								}
							}

							serviceMgr.fnQueryList();//
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
						return 0;
					}

					@Override
					public int joinDoubleHeadingGroupResponse(String szMasterEN,
															  String szHostEN, String szGroupRefID, int nResult, String szReason)
					{
						if (nResult == ServiceConstant.RESPONSE_SUCCESS)
							LogInstance.debug(GlobalPara.Tky, "joinDoubleHeadingGroupResponse: szMasterEN="+szMasterEN +" szHostEN="+szHostEN+" szGroupRefID="+szGroupRefID+" nResult=RESPONSE_SUCCESS"+" szReason="+szReason);
						else
							LogInstance.debug(GlobalPara.Tky, "joinDoubleHeadingGroupResponse: szMasterEN="+szMasterEN +" szHostEN="+szHostEN+" szGroupRefID="+szGroupRefID+" nResult=RESPONSE_FAILED"+" szReason="+szReason);

						try
						{
							if(nResult == ServiceConstant.RESPONSE_SUCCESS)
							{
								if (mTrainState.g_DebugLog_Lev1 != 0x00)
									LogInstance.debug(GlobalPara.Tky,"poc notify double group id=" +szGroupRefID+ " register successful");
								if(szGroupRefID != null && !szGroupRefID.equals(""))
								{
									GlobalPara.strDoubleHeadingGroupID = szGroupRefID.substring(szGroupRefID.length()-3);
								}

								//修改g_MainEngineNumber移到此处
								if(szMasterEN!=null && !szMasterEN.equals("") && szMasterEN.length()>=8)
								{
									for (int i = 0; i < 8; i++)
										mTrainState.g_MainEngineNumber.Number[i] = (byte) szMasterEN.charAt(i) ;
									LogInstance.debug(GlobalPara.Tky,"g_MainEngineNumber be set");
								}
								else
								{
									LogInstance.debug(GlobalPara.Tky,"szMasterEN is null, it is not right");
								}

								GlobalPara.iInDoubleGroup = 1;
								GlobalPara.strMainEngineNumber = szMasterEN;
							}
							else
							{//回复MMI主控机车号注册失败
								if (mTrainState.g_DebugLog_Lev1 != 0x00)
									LogInstance.debug(GlobalPara.Tky,"poc notify double group main enginenumber register failed");

								GlobalPara.iInDoubleGroup = 0;
								GlobalPara.strDoubleHeadingGroupID = "";
								GlobalPara.strMainEngineNumber = "";
							}

							ShowJoinDoubleHeadingGroupResponseOnWindow( 1, szMasterEN,	 szHostEN,  szGroupRefID,  nResult,  szReason);
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
						return 0;
					}

					@Override
					public int unjoinDoubleHeadingGroupResponse(String szMasterEN,
																String szHostEN, String szGroupRefID, int nResult, String szReason)
					{
						if (nResult == ServiceConstant.RESPONSE_SUCCESS)
							LogInstance.debug(GlobalPara.Tky, "unjoinDoubleHeadingGroupResponse: szMasterEN"+szMasterEN +" szHostEN="+szHostEN+" szGroupRefID="+szGroupRefID+" nResult=RESPONSE_SUCCESS"+" szReason="+szReason);
						else
							LogInstance.debug(GlobalPara.Tky, "unjoinDoubleHeadingGroupResponse: szMasterEN"+szMasterEN +" szHostEN="+szHostEN+" szGroupRefID="+szGroupRefID+" nResult=RESPONSE_FAILED"+" szReason="+szReason);

						try
						{
							if(nResult == ServiceConstant.RESPONSE_SUCCESS)
							{
								if (mTrainState.g_DebugLog_Lev1 != 0x00)
									LogInstance.debug(GlobalPara.Tky,"poc notify double group id unregister successful");

								for(int i = 0; i < 8; i++)
									mTrainState.g_MainEngineNumber.Number[i] = 'X';

								GlobalPara.iInDoubleGroup = 0;
								GlobalPara.strDoubleHeadingGroupID = "";
								GlobalPara.strMainEngineNumber = "";
							}
							else
							{
								if (mTrainState.g_DebugLog_Lev1 != 0x00)
									LogInstance.debug(GlobalPara.Tky,"poc notify double group id unregister failed");

								GlobalPara.iInDoubleGroup = 1;
								GlobalPara.strDoubleHeadingGroupID = szGroupRefID.substring(szGroupRefID.length()-3);
								GlobalPara.strMainEngineNumber = szMasterEN;
							}

							ShowJoinDoubleHeadingGroupResponseOnWindow( 2, szMasterEN,	 szHostEN,  szGroupRefID,  nResult,  szReason);

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
						return 0;
					}

					@Override
					public int publishLocationResult(String szCellID, int nResult, String szReason)
					{
						if (nResult == ServiceConstant.RESPONSE_SUCCESS)
							LogInstance.debug(GlobalPara.Tky, "publishLocationResult: szCellID="+szCellID   +" nResult=RESPONSE_SUCCESS"+" szReason="+szReason);
						else
							LogInstance.debug(GlobalPara.Tky, "publishLocationResult: szCellID="+szCellID   +" nResult=RESPONSE_FAILED"+" szReason="+szReason);

						try{//
							if (nResult != ServiceConstant.RESPONSE_SUCCESS)// 不成功
							{
								if (mTrainState.mszCellID.length() > 0)
								{
									String cellid_ = String.format("%x", Integer.valueOf(mTrainState.mszCellID, 16));
									if(mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.error(GlobalPara.Tky, "134." + tag + ": attention! failed serviceMgr.publishLocation: " + cellid_+", reason=" + szReason);
								}
							}

							if (nResult == ServiceConstant.RESPONSE_SUCCESS)
							{
								ShowInfoOnWindows("locationstate", szCellID,szReason);//
							}
							else{
								ShowInfoOnWindows("locationstate", "",szReason);//
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
						return 0;
					}

					@Override
					public int holdCallResponse(String szCallID, int nResult, String szReason)
					{
						if (nResult == ServiceConstant.RESPONSE_SUCCESS)
							LogInstance.debug(GlobalPara.Tky, "holdCallResponse: nResult=RESPONSE_SUCCESS"+" szReason="+szReason);
						else
							LogInstance.debug(GlobalPara.Tky, "holdCallResponse: nResult=RESPONSE_FAILED"+" szReason="+szReason);

						if(nResult == ServiceConstant.RESPONSE_SUCCESS)
						{
							bIsHoldSuccessful = 1;
							if(mTrainState.g_DebugLog_Lev1!=0x00)
								LogInstance.debug(GlobalPara.Tky, "holdCallResponse:true");
						}
						else if (nResult == ServiceConstant.RESPONSE_FAILED)
						{
							bIsHoldSuccessful = 0;
							if(mTrainState.g_DebugLog_Lev1!=0x00)
								LogInstance.error(GlobalPara.Tky, "116." + tag + ": holdCallResponse:false");
						}

						try{//
							ShowHoldRecovyCallResponseOnWindows(1,szCallID, nResult, szReason);
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
						return 0;
					}

					@Override
					public int recoveryCallResponse(String szCallID, int nResult, String szReason)
					{
						if (nResult == ServiceConstant.RESPONSE_SUCCESS)
							LogInstance.debug(GlobalPara.Tky, "recoveryCallResponse: nResult=RESPONSE_SUCCESS"+" szReason="+szReason);
						else
							LogInstance.debug(GlobalPara.Tky, "recoveryCallResponse: nResult=RESPONSE_FAILED"+" szReason="+szReason);

						if(nResult == ServiceConstant.RESPONSE_SUCCESS)
						{
							if(mTrainState.g_DebugLog_Lev1!=0x00)
								LogInstance.debug(GlobalPara.Tky, "recoveryCallResponse:true");
						}
						else if (nResult == ServiceConstant.RESPONSE_FAILED)
						{
							if(mTrainState.g_DebugLog_Lev1!=0x00)
								LogInstance.error(GlobalPara.Tky, "116." + tag + ": recoveryCallResponse:false");
						}

						try{//
							ShowHoldRecovyCallResponseOnWindows(2, szCallID, nResult, szReason);
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
						return 0;
					}

					@Override
					public int activeGroupListNotify(List<String[]> groupList)
					{//呼叫中组通知, 呼叫中组列表(群组类型，群组参考，发起者用户号码或功能号码，优先级，自动呼叫标识)
						try
						{
							lstGroupInCalls.clear();
							for (String[] strings : groupList)
							{
								if(strings != null)
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky,"activeGroupListNotify: nCallType=" + strings[0] + " strGroupId=" + strings[1] +
												" strCallerFn=" + strings[2] + 	" nPriority=" + strings[3] + " bAutoCall=" + strings[4] );

									GroupInCall aGroupInCall = new GroupInCall();
									aGroupInCall.iCallType = Integer.parseInt(strings[0]);
									aGroupInCall.strGroupId =  strings[1] ;
									aGroupInCall.strCallerFn = strings[2] ;
									aGroupInCall.iPriority = Integer.parseInt(strings[3]);
									aGroupInCall.bAutoCall = Boolean.parseBoolean(strings[4]);
									lstGroupInCalls.add(aGroupInCall);
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

						return 0;
					}

					@Override
					public int groupNumberNotify(String szCallID, String[] szFN, String[] szUN, int nOnLineUserCount)
					{
						try{//
							LogInstance.debug(GlobalPara.Tky, "groupNumberNotify be called");
							for(int i =0 ; i<callList.size();i++) {
								if(callList.get(i).strCallId.equals(szCallID) )
									callList.get(i).nOnLineUserCount = nOnLineUserCount;
							}

							if(mCurrentCall.strCallId.equals(szCallID))
								mCurrentCall.nOnLineUserCount = nOnLineUserCount;
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
						return 0;
					}

					@Override
					public int tmpGroupCreateResponse(int nResult, String[] objTempGroup, String szReason)
					{
						try{//
							LogInstance.debug(GlobalPara.Tky, "tmpGroupCreateResponse be called");

							if(nResult == ServiceConstant.RESPONSE_SUCCESS) {
								GroupTmpCall groupTmpCall = new GroupTmpCall();
								groupTmpCall.iCallType = Integer.parseInt( objTempGroup[0] );
								groupTmpCall.strGroupId =  objTempGroup[1] ;
								groupTmpCall.strGroupName =  objTempGroup[2] ;
								groupTmpCall.iPriority =  Integer.parseInt( objTempGroup[3] );
								groupTmpCall.strRequester =  objTempGroup[4] ;
								groupTmpCall.nLifespan =  Integer.parseInt( objTempGroup[5] );
								groupTmpCall.nMaxIdleTime =  Integer.parseInt( objTempGroup[7] );
								String[] arrMembers = objTempGroup[6].split(",");
								if(arrMembers!=null)
									for(int j = 0; j<arrMembers.length; j++)
										groupTmpCall.lstMemberList.add(arrMembers[j]);

								GroupTmpCall aGroupTmpCall = null;
								int i = 0;
								while (i < lstGroupTmpCalls.size()) {
									if (lstGroupTmpCalls.get(i).strGroupId.equals(objTempGroup[1])) {
										aGroupTmpCall = lstGroupTmpCalls.get(i);
										break;
									}
									i++;
								}

								if(aGroupTmpCall == null) {
									lstGroupTmpCalls.add(groupTmpCall);
								}else{
									lstGroupTmpCalls.get(i).Set(groupTmpCall);
								}
							}

							ShowTmpGroupOperateResponseOnWindow(nResult,objTempGroup[1],szReason);
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
						return 0;
					}

					@Override
					public int tmpGroupModifyResponse(int nResult, String[] objTempGroup, String szReason)
					{
						try{//
							LogInstance.debug(GlobalPara.Tky, "tmpGroupModifyResponse be called");

							if(nResult == ServiceConstant.RESPONSE_SUCCESS) {
								GroupTmpCall groupTmpCall = new GroupTmpCall();
								if (!TextUtils.isEmpty(objTempGroup[0])){
                                    groupTmpCall.iCallType = Integer.parseInt( objTempGroup[0] );
                                }
                                groupTmpCall.strGroupId =  objTempGroup[1] ;
                                groupTmpCall.strGroupName =  objTempGroup[2];
                                if (!TextUtils.isEmpty(objTempGroup[3])){
                                    groupTmpCall.iPriority =  Integer.parseInt( objTempGroup[3] );
                                }
                                groupTmpCall.strRequester =  objTempGroup[4] ;

                                if (!TextUtils.isEmpty(objTempGroup[5])){
                                    groupTmpCall.nLifespan =  Integer.parseInt( objTempGroup[5] );
                                }
                                if (!TextUtils.isEmpty(objTempGroup[7])){
                                    groupTmpCall.nMaxIdleTime =  Integer.parseInt( objTempGroup[7] );
                                }
                                String[] arrMembers = objTempGroup[6].split(",");
								if(arrMembers!=null)
									for(int j = 0; j<arrMembers.length; j++)
										groupTmpCall.lstMemberList.add(arrMembers[j]);

								GroupTmpCall aGroupTmpCall = null;
								int i = 0;
								while (i < lstGroupTmpCalls.size()) {
									if (lstGroupTmpCalls.get(i).strGroupId.equals(objTempGroup[1])) {
										aGroupTmpCall = lstGroupTmpCalls.get(i);
										break;
									}
									i++;
								}

								if(aGroupTmpCall == null) {
									lstGroupTmpCalls.add(groupTmpCall);
								}else{
									lstGroupTmpCalls.get(i).Set(groupTmpCall);
								}
							}

							ShowTmpGroupOperateResponseOnWindow(nResult,objTempGroup[1],szReason);
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
						return 0;
					}

					@Override
					public int tmpGroupRemoveResponse(int nResult, String szGroupNumber, String szReason)
					{
						try{//
							LogInstance.debug(GlobalPara.Tky, "tmpGroupRemoveResponse be called");

							if(nResult == ServiceConstant.RESPONSE_SUCCESS) {
								int i = 0;
								while (i < lstGroupTmpCalls.size()) {
									if (lstGroupTmpCalls.get(i).strGroupId.equals(szGroupNumber)) {
										lstGroupTmpCalls.remove(i);
										continue;
									}
									i++;
								}
							}

							ShowTmpGroupOperateResponseOnWindow(nResult,szGroupNumber,szReason);
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
						return 0;
					}

					@Override
					public int groupListNotify(List<String[]> groupList)
					{
						try{//
							LogInstance.debug(GlobalPara.Tky, "groupListNotify be called:"+groupList.size());//

							lstGroupTmpCalls.clear();

							for(int i = 0; i < groupList.size(); i++){
								LogInstance.debug(GlobalPara.Tky, "nGroupType="+groupList.get(i)[0]+", szGroupNumber="+groupList.get(i)[1]
										+", szGroupName="+groupList.get(i)[2]+", nPriority="+groupList.get(i)[3]+", szRequester;="+groupList.get(i)[4]
										+", nLifespan="+groupList.get(i)[5]+", szMemberList="+groupList.get(i)[6]+", nMaxIdleTime="+groupList.get(i)[7]);//

								if(Integer.parseInt( groupList.get(i)[0] ) == ServiceConstant.CALL_TYPE_TEMPGROUP){
									GroupTmpCall groupTmpCall = new GroupTmpCall();

									//
									try {
										if(!groupList.get(i)[0].equals(""))
											groupTmpCall.iCallType = Integer.parseInt( groupList.get(i)[0] );
									}
									catch (Exception e) {
										LogInstance.debug(GlobalPara.Tky,"can not change string to int 1");
									}
									groupTmpCall.strGroupId =  groupList.get(i)[1] ;
									groupTmpCall.strGroupName =  groupList.get(i)[2] ;
									try {
										if(!groupList.get(i)[3].equals(""))
											groupTmpCall.iPriority =  Integer.parseInt( groupList.get(i)[3] );
									}
									catch (Exception e) {
										LogInstance.debug(GlobalPara.Tky,"can not change string to int 2");
									}
									groupTmpCall.strRequester =  groupList.get(i)[4] ;
									try {
										if(!groupList.get(i)[5].equals(""))
											groupTmpCall.nLifespan =  Integer.parseInt( groupList.get(i)[5] );
									}
									catch (Exception e) {
										LogInstance.debug(GlobalPara.Tky,"can not change string to int 3");
									}
									try {
										if(!groupList.get(i)[7].equals(""))
											groupTmpCall.nMaxIdleTime =  Integer.parseInt( groupList.get(i)[7] );
									}
									catch (Exception e) {
										LogInstance.debug(GlobalPara.Tky,"can not change string to int 4");
									}
									try {
										if(!groupList.get(i)[6].equals("")) {
											String[] arrMembers = groupList.get(i)[6].split(",");
											if (arrMembers != null)
												for (int j = 0; j < arrMembers.length; j++)
													groupTmpCall.lstMemberList.add(arrMembers[j]);
										}
									}
									catch (Exception e) {
											LogInstance.debug(GlobalPara.Tky,"can not get lstMemberList");
									}
									lstGroupTmpCalls.add(groupTmpCall);
								}
							}
							sendNotifyTmpGroupCallsChange(lstGroupTmpCalls);
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
						return 0;
					}

					@Override
					public int preemptUnjoinDoubleHeadingGroupNotify(String szMasterEN, String szHostEN, String szGroupRefID)
					{
						try{//
							LogInstance.debug(GlobalPara.Tky, "preemptUnjoinDoubleHeadingGroupNotify: "+szMasterEN+" "+szHostEN+" "+szGroupRefID);

							for(int i = 0; i < 8; i++)
								mTrainState.g_MainEngineNumber.Number[i] = 'X';

							GlobalPara.iInDoubleGroup = 0;
							GlobalPara.strDoubleHeadingGroupID = "";
							GlobalPara.strMainEngineNumber = "";

							ShowJoinDoubleHeadingGroupResponseOnWindow( 2, szMasterEN,	 szHostEN,  szGroupRefID,  0,  "preempt unjoin");
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
						return 0;
					}

					@Override
					public int heartbeatEvent()
					{
						LogInstance.debug(GlobalPara.Tky, "heartbeatEvent be called");
						return 0;
					}

					@Override
					public int linkStatusNotify(int nStatus, String szReason)
					{//poc和服务器的连接心跳,并不代表已上线
						LogInstance.debug(GlobalPara.Tky, "linkStatusNotify be called");
						return 0;
					}

					@Override
					public int groupSANotify(String arg0, String arg1, String arg2, int arg3)
					{
						LogInstance.debug(GlobalPara.Tky, "groupSANotify be called");
						return 0;
					}

					@Override
					public int callForwardSetQueryResponse(int nCode, int nEnable, String szFwNum, int nTime, int nResult, String szReason)
					{
						LogInstance.debug(GlobalPara.Tky, "callForwardSetQueryResponse be called");

						return 0;
					}

					@Override
					public int callForwardSetResponse(int nCode, int nResult, String szReason)
					{
						LogInstance.debug(GlobalPara.Tky, "callForwardSetResponse be called");

						return 0;
					}

					@Override
					public int callWaitingSetQueryResponse(int nCode, int nResult, String szReason)
					{
						LogInstance.debug(GlobalPara.Tky, "callWaitingSetQueryResponse be called");

						return 0;
					}

					@Override
					public int callWaitingSetResponse(int nCode, int nResult, String szReason)
					{
						LogInstance.debug(GlobalPara.Tky, "callWaitingSetResponse be called");

						return 0;
					}

					@Override
					public int newNumberPlanning(String szVersion, String szFilePath)
					{
						//if (mTrainState.g_DebugLog_Lev1 != 0x00)
						//	LogInstance.debug(GlobalPara.Tky, "newNumberPlanning: szVersion="+szVersion +" szFilePath="+szFilePath);

						//try
						//{
						//	ConfigParam.NUMBER_PLANNING_VERSION = szVersion;
						//    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						//    DocumentBuilder db = dbf.newDocumentBuilder();
						//    Document document = db.parse(new File(szFilePath));
						//
						//    NodeList list = document.getElementsByTagName("CC");
						//    String retValue = getNodeValue(list);
						//    GlobalPara.strPreNationNumber = retValue;
						//    //LogInstance.debug(GlobalPara.Tky,"CC:" + retValue);
						//
						//    list = document.getElementsByTagName("NDC");
						//    retValue = getNodeValue(list);
						//    GlobalPara.strPreNdc = retValue;
						//    //LogInstance.debug(GlobalPara.Tky,"NDC:" +retValue);
						//
						//    list = document.getElementsByTagName("DGID");
						//    retValue = getNodeValue(list);
						//    GlobalPara.strGroupcallShortNumber = retValue;
						//    //LogInstance.debug(GlobalPara.Tky,"DGID:" +retValue);
						//
						//    list = document.getElementsByTagName("IC");
						//    retValue = getNodeAttr(list,"v");
						//    GlobalPara.strPreGlobalNumber = retValue;
						//    //LogInstance.debug(GlobalPara.Tky,"IC:" +retValue);
						//
						//    list = document.getElementsByTagName("TFN");
						//    retValue = getNodeValue(list);
						//    GlobalPara.strPreTfnNumber = retValue;
						//    //LogInstance.debug(GlobalPara.Tky,"TFN:" +retValue);
						//
						//    list = document.getElementsByTagName("EFN");
						//    retValue = getNodeValue(list);
						//    GlobalPara.strPreEfnNumber = retValue;
						//    //LogInstance.debug(GlobalPara.Tky,"EFN:" +retValue);
						//
						//    list = document.getElementsByTagName("VGCS");
						//    retValue = getNodeValue(list);
						//    GlobalPara.strPreGroupcall = retValue;
						//    //LogInstance.debug(GlobalPara.Tky,"VGCS:" +retValue);
						//
						//    list = document.getElementsByTagName("VBS");
						//    retValue = getNodeValue(list);
						//    GlobalPara.strPreBoardcastcall = retValue;
						//    //LogInstance.debug(GlobalPara.Tky,"VBS:" +retValue);
						//	list = null;
						//
						//    document = null;
						//    db = null;
						//    dbf = null;
						//}
						//catch (Exception e2)
						//{
						//	e2.printStackTrace();
						//	if (e2.getMessage() != null && e2.getMessage() != "")
						//	{
						//		LogInstance.exception(GlobalPara.Tky, e2);
						//	}
						//	LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
						//			+ Thread.currentThread().getStackTrace()[2].getLineNumber());
						//}

						return 0;
					}

					@Override
					public int doubleCenterSwitchNotify()
					{
						LogInstance.debug(GlobalPara.Tky, "doubleCenterSwitchNotify be called");

						try
						{
							//上报小区
							if (mTrainState.mszCellID != "")
							{
								String cellid_ = String.format("%x", Integer.valueOf(mTrainState.mszCellID, 16));
								if(mTrainState.g_DebugLog_Lev1!= 0x00)
									LogInstance.debug(GlobalPara.Tky, "publishLocation for doubleCenterSwitchNotify: "+cellid_);

								int result = serviceMgr.publishLocation(cellid_);//
								if (result != ServiceConstant.METHOD_SUCCESS)
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.error(GlobalPara.Tky, "136." + tag + ": publishLocation be called failed");
									serviceMgr.publishLocation(cellid_);//
								}
								else
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "publishLocation be called successful");
								}
							}
							else
							{
								if(mTrainState.g_DebugLog_Lev1!= 0x00)
									LogInstance.error(GlobalPara.Tky, "137." + tag + ": can not publishLocation for doubleCenterSwitchNotify because of cellid is null");
							}
							//注册功能号
							if(!mtrainNum.equals(""))
							{
								if (mTrainState.g_DebugLog_Lev1 != 0x00)
									LogInstance.debug(GlobalPara.Tky, "funcNumRegister for doubleCenterSwitchNotify: " + mtrainNum);

								int result = serviceMgr.fnRegister(mtrainNum);
								if (result != ServiceConstant.METHOD_SUCCESS)
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.error(GlobalPara.Tky, "138." + tag + ": fnRegister be called failed");
									serviceMgr.fnRegister(mtrainNum);
								}
								else
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "fnRegister be called successful");
								}
							}
							else
							{
								if(mTrainState.g_DebugLog_Lev1!= 0x00)
									LogInstance.error(GlobalPara.Tky, "139." + tag + ": can not funcNumRegister for doubleCenterSwitchNotify because of mtrainNum is null");
							}

							if(!mLocofunNum.equals(""))
							{
								if (mTrainState.g_DebugLog_Lev1 != 0x00)
									LogInstance.debug(GlobalPara.Tky, "funcNumRegister for doubleCenterSwitchNotify: " + mLocofunNum);

								int result = serviceMgr.fnRegister(mLocofunNum);
								if (result != ServiceConstant.METHOD_SUCCESS)
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.error(GlobalPara.Tky, "138." + tag + ": fnRegister be called failed");
									serviceMgr.fnRegister(mLocofunNum);
								}
								else
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "fnRegister be called successful");
								}
							}
							else
							{
								if(mTrainState.g_DebugLog_Lev1!= 0x00)
									LogInstance.error(GlobalPara.Tky, "139." + tag + ": can not funcNumRegister for doubleCenterSwitchNotify because of mLocofunNum is null");
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
						return 0;
					}

				};

				serviceMgr.registerServiceEventListener(listener);

				String strLteIp = String.format(("%d.%d.%d.%d"), mTrainState.m_SourLteAddre[0] & 0x00FF, mTrainState.m_SourLteAddre[1] & 0x00FF,
						mTrainState.m_SourLteAddre[2] & 0x00FF, mTrainState.m_SourLteAddre[3] & 0x00FF);
				if(mTrainState.g_DebugLog_Lev1!=0x00)
					LogInstance.debug(GlobalPara.Tky, "local ip in poc:"+strLteIp);
				serviceMgr.startService(ServiceConstant.MODE_NORMAL_MASTER_UPLINE, null,strLteIp);

				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "POC Register end");
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

	public static String getNodeValue(NodeList list)
	{
		String ret = null;
		Element element =null;
		if(list != null && list.getLength()>0)
			element = (Element)list.item(0);
		if(element != null)
		{
			NodeList childNodes = element.getChildNodes();
			Node node =null;
			if(childNodes != null && childNodes.getLength()>0)
				node = (Node)childNodes.item(0);
			if(node != null)
			{
				int nodeType = node.getNodeType();
				switch (nodeType)
				{
					case Node.TEXT_NODE:
						ret = node.getNodeValue();
						break;
					default:
						ret = null;
				}
			}
			node = null;
			childNodes = null;
		}
		element =null;
		return ret;
	}

	public static String getNodeAttr(NodeList list, String strName)
	{
		String ret = null;
		Node node =null;
		if(list != null && list.getLength()>0)
			node = (Node)list.item(0);
		if(node != null)
		{
			NamedNodeMap attrs = node.getAttributes();
			if(attrs != null)
			{
				for(int i = 0 ; i < attrs.getLength() ; i++)
				{
					Node attItem = (Node)attrs.item(i);
					//System.out.println(" ("+attItem.getNodeName()+","+attItem.getNodeValue()+")");
					if(strName.equals(attItem.getNodeName()))
					{
						ret = attItem.getNodeValue();
						break;
					}
				}
			}
		}
		node =null;
		return ret;
	}



	public int GetCallListSize()
	{
		return callList.size();
	}

	// LTE单元报告组呼上行链路状态及PTT请求结果
	public int GetFrame_PTT_RESULT_03_52(byte[] buffer, byte resultcode, byte bDestCode)
	{

		int len = 2;
		buffer[len++] = (byte) GlobalPara.iZhuKongFlag;// 源端口
		buffer[len++] = 0x00;// 源通信地址长度
		// for (i = 0; i < 4; i++)
		// buffer[len++] = mTrainState.m_SourLteAddre[i] ;// 源通信地址
		buffer[len++] = bDestCode;// 目的端口
		buffer[len++] = 0x00;// 目的通信地址长度

		buffer[len++] = 0x03;// 业务类型
		buffer[len++] = 0x52;// 命令
		buffer[len++] = resultcode;

		len += 2;
		buffer[0] = (byte) ((len - 2) / 256);
		buffer[1] = (byte) ((len - 2) % 256);

		// byte[] crc_result = new byte[2] ;
		// GlobalFunc.cal_crc(buffer, len - 2, crc_result);
		// buffer[len - 2] = crc_result[0] ;//CRC
		// buffer[len - 1] = crc_result[1] ;
		return len;
	}



	// MMI进行LTE呼叫转移操作 第一字节:类型 31H:无条件前转 32H:无应答前转 33H:遇忙前转
	// 34H:不可到达前转 第二字节:操作 32H:查询呼叫转移状态 33H:启动呼叫转移 34H:取消呼叫转移
	// 第三字节开始为电话号码；ASCII码，以“；”结束。 然后是无应答前转的延时时间，
	// ASCII码，以“；”结束。MMI只能输入5秒、10秒、15秒、20秒、25秒、30秒。
	public void HandleForMMIForwardCall_16(_RawInfo PackageInfo)
	{
		try
		{
			int size = PackageInfo.InfoLenth;
			int headdata = 0, j;
			if (mblPOCRegister)
			{
				int type, enable, time = 0, jj;
				String number = "";
				type = PackageInfo.Data[headdata] - 0x31;

				if (PackageInfo.Data[headdata + 1] == 0x32)
					;//serviceMgr.callForwardQuery(type);
				else
				{
					enable = PackageInfo.Data[headdata + 1] - 0x33;
					for (j = 0; j < size - headdata - 3; j++)
					{
						if (PackageInfo.Data[headdata + 2 + j] == ';')
						{
							if (j > 0)
								number = new String(PackageInfo.Data, headdata + 2, j);
							break;
						}
					}

					jj = j + 1;
					for (j = jj; j < size - headdata - 3; j++)
					{
						if (PackageInfo.Data[headdata + 2 + j] == ';')
						{
							if (j - jj > 0)
								time = Integer.parseInt(new String(PackageInfo.Data, headdata + 2 + jj, j - jj));
							break;
						}
					}
					serviceMgr.callForwardSet(type, enable, number, time);
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


	public int CallListSize()
	{
		return callList.size();
	}

	/**
	 * POC接口-被动:状态发生变化,需要更新界面(登网后,是否获得IP, 是否POC注册上线成功,脱网,POC下线时调用)
	 * @param strWhichChange 哪项状态变化  "ipstate" "pocstate"
	 * @param strChangeValue 变化值 "on" "off"
	 */
	public void ShowInfoOnWindows(String strWhichChange, String strChangeValue, String strReason)//
	{
		if(mTrainState.g_DebugLog_Lev1 != 0x00)
			LogInstance.error(GlobalPara.Tky, "change app state on windows because of some thing change, strWhichChange="
					+strWhichChange+" strChangeValue="+strChangeValue);
        MainBean mainBean = new MainBean();
        mainBean.setStrWhichChange(strWhichChange);
        mainBean.setStrChangeValue(strChangeValue);
		mainBean.setStrReason(strReason);//
        EventBus.getDefault().post(mainBean);
	}

	/**
	 * POC接口-被动:呼叫状态发生变化,需要更新界面(在来呼、呼叫状态发生变化时会被调用)
	 * 呼叫列表calllist状态发生变化,需要反映到呼叫界面
	 */
	public void ShowCallChangeOnWindows()
	{
		if(mTrainState.g_DebugLog_Lev1 != 0x00)
			LogInstance.error(GlobalPara.Tky, "change call windows because of call list changed");
            EventBus.getDefault().post(Config.Calling);//发送到MainActivity
	}

	/**
	 * POC接口-被动:状态发生变化,需要更新界面(组呼时,用户操作完PTT后,网络反馈操作是否成功,或者有其他用户操作ptt导致状态变化时会调用)
	 * @param iPttState 0:PTT空闲，1:PTT占用忙；2:PTT请求占用成功，3:PTT请求失败  4:其他人正在占用
	 * @param szCallID 呼叫ID,可以不用
	 * @param szSpeakerFnNum 当前讲者的号码,可能为空
	 */
	public void ShowPttStateOnWindows(int iPttState, String szCallID, String szSpeakerFnNum)
	{
		if(mTrainState.g_DebugLog_Lev1 != 0x00)
			LogInstance.debug(GlobalPara.Tky, "change ptt state on windows because of ptt change, iPttState="
					+iPttState+" szCallID="+szCallID+" szSpeakerFnNum="+szSpeakerFnNum);
        PttStateBean pttStateBean = new PttStateBean();
        pttStateBean.setiPttState(iPttState);
        EventBus.getDefault().post(pttStateBean);
	}

	/**
	 * POC接口-被动:功能号列表查询结果,需要更新界面(用户查询功能号列表时会调用)
	 * @param resultCode 0:查询成功 -1:查询失败
	 * @param funcNumList 已注册功能号码列表
	 */
	public void ShowFunctionNumListOnWindows( int resultCode, ArrayList<String> funcNumList)
	{
		String strfuncNumList = "";
		if(funcNumList != null) {
			for (String str:funcNumList )
				strfuncNumList += str + ", ";
			LogInstance.debug(GlobalPara.Tky, "change function number list on windows when query, resultCode="
					+ resultCode + " funcNumList="+strfuncNumList);
		}
            FunctionNumListBean functionNumListBean = new FunctionNumListBean();
            functionNumListBean.setResultCode(resultCode);
            functionNumListBean.setFuncNumList(funcNumList);
            EventBus.getDefault().post(functionNumListBean);
	}

	/**
	 * POC接口-被动:查询某个功能号是否被注册或者谁正在使用,需要更新界面
	 * @param nResult 0:查询成功 -1:查询失败(该号码无人使用)
	 * @param szFN 查询的功能号码
	 * @param szUN 该号码被注册的用户
	 */
	public void ShowFunctionNumIsUsedOnWindows(int nResult, String szFN, String szUN,String szReason)
	{
		if(mTrainState.g_DebugLog_Lev1 != 0x00)
			LogInstance.debug(GlobalPara.Tky, "change function number is used on windows when query, nResult="
					+nResult+" szFN="+szFN+" szUN="+szUN);

		//
		FunctionNumIsUsedBean functionNumIsUsedBean = new FunctionNumIsUsedBean();
		functionNumIsUsedBean.setnResult(nResult);
		functionNumIsUsedBean.setSzFN(szFN);
		functionNumIsUsedBean.setSzUN(szUN);
		functionNumIsUsedBean.setSzReason(szReason);
		EventBus.getDefault().post(functionNumIsUsedBean);
	}

	/**
	 * POC接口-被动:功能号注册注销响应,需要更新界面
	 * @param nFunctionNumberType   2:车次号功能号 3:机车功能号
	 * @param nOperationType  1:功能号注册操作 2:功能号注销操作
	 * @param nResult 0:操作成功 -1:操作失败
	 * @param szFN  待注册的功能号码
	 * @param szReason 失败原因
	 */
	public void ShowFunctionRegisterResponseOnWindows(int nFunctionNumberType, int nOperationType, int nResult, String szFN, String szReason)
	{
		if(mTrainState.g_DebugLog_Lev1 != 0x00)
			LogInstance.debug(GlobalPara.Tky, "change function number register result on windows when register, nFunctionNumberType="
					+nFunctionNumberType + " nOperationType="+nOperationType+" nResult="+nResult+" szFN="+szFN+" szReason="+szReason);
        FunctionRegisterResponseBean bean = new FunctionRegisterResponseBean();
        bean.setnFunctionNumberType(nFunctionNumberType);
        bean.setnOperationType(nOperationType);
        bean.setnResult(nResult);
        bean.setSzFN(szFN);
        bean.setSzReason(szReason);
        EventBus.getDefault().post(bean);
	}

	/**
	 * POC接口-被动:强制注销响应,需要更新界面
	 * @param szFN 强制注销的功能号码
	 * @param szUN 强制注销的用户号码
	 * @param nResult 结果
	 * @param szReason 失败原因
	 */
	public void  ShowForceUnregisterResponseOnWindows(String szFN, String szUN, int nResult, String szReason)
	{
		if(mTrainState.g_DebugLog_Lev1 != 0x00)
			LogInstance.debug(GlobalPara.Tky, "force unregister function number response on windows when force unregister, szFN="
					+szFN+" szUN="+szUN+" nResult="+nResult+" szReason="+szReason);

		//
		ForceUnregisterResponseBean forceUnregisterResponseBean = new ForceUnregisterResponseBean();
		forceUnregisterResponseBean.setnResult(nResult);
		forceUnregisterResponseBean.setSzFN(szFN);
		forceUnregisterResponseBean.setSzUN(szUN);
		forceUnregisterResponseBean.setSzReason(szReason);
		EventBus.getDefault().post(forceUnregisterResponseBean);
	}

	/**
	 * POC接口(放弃重联组业务)-被动:加入重联组呼的响应,需要更新界面
	 * @param nOperationType 1:加入重联组 2:退出重联组
	 * @param szMasterEN 主控机车号
	 * @param szHostEN 本机车机车号
	 * @param szGroupRefID 群组号:50 + 编号 + 789（默认）
	 * @param nResult 0:成功 -1:失败
	 * @param szReason 失败原因
	 */
	public void ShowJoinDoubleHeadingGroupResponseOnWindow(int nOperationType, String szMasterEN, String szHostEN, String szGroupRefID, int nResult, String szReason)
	{
		if(mTrainState.g_DebugLog_Lev1 != 0x00)
			LogInstance.debug(GlobalPara.Tky, "hold call response on windows when hold call, nOperationType="
					+nOperationType+" szMasterEN="+szMasterEN+" szHostEN="+szHostEN+" szGroupRefID="+szGroupRefID+" nResult="+nResult+" szReason="+szReason);
		//放弃此业务
	}

	/**
	 * POC接口-被动:呼叫保持响应,需要更新界面(暂时不用实现)
	 * @param nOperationType 1:保持呼叫 2:恢复呼叫
	 * @param szCallID 呼叫ID,可以不用
	 * @param nResult  0:成功 -1:失败
	 * @param szReason 失败原因
	 */
	public void  ShowHoldRecovyCallResponseOnWindows(int nOperationType, String szCallID, int nResult, String szReason)
	{
		if(mTrainState.g_DebugLog_Lev1 != 0x00)
			LogInstance.debug(GlobalPara.Tky, "hold call response on windows when hold call, nOperationType="
					+nOperationType+" szCallID="+szCallID+" nResult="+nResult+" szReason="+szReason);

	}

	/**
	 * POC接口-被动:修改临时群组
	 * @param nResult
	 * @param szGroupNumber
	 * @param szReason
	 */
	public void ShowTmpGroupOperateResponseOnWindow(int nResult, String szGroupNumber, String szReason)
	{
		if(mTrainState.g_DebugLog_Lev1 != 0x00)
			LogInstance.debug(GlobalPara.Tky, "operate tmp group response on windows nResult=" +nResult+" szGroupNumber="+szGroupNumber+" szReason="+szReason);
        TmpGroupBean bean = new TmpGroupBean();
        bean.setnResult(nResult);
        bean.setSzGroupNumber(szGroupNumber);
        bean.setSzReason(szReason);
        EventBus.getDefault().post(bean);
	}

	public void Release()
	{
		LogInstance.error(GlobalPara.Tky, "170." + tag + ": LteHandle release");

		try
		{
			POC_UnRegister();
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

		try
		{
			if (mRead_LTE_UDP_Thread != null)
			{
				LogInstance.error(GlobalPara.Tky, "25." + tag + ": mRead_LTE_UDP_Thread be interrupted");
				bRead_LTE_UDP_Thread = false;
				mRead_LTE_UDP_Thread.interrupt();
				mRead_LTE_UDP_Thread = null;
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

		try
		{
			if (mSend_LTE_UDP_Thread != null)
			{
				LogInstance.error(GlobalPara.Tky, "25." + tag + ": mSend_LTE_UDP_Thread be interrupted");
				bSend_LTE_UDP_Thread = false;
				mSend_LTE_UDP_Thread.interrupt();
				mSend_LTE_UDP_Thread = null;
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

	public int FunctionNumberFNRegisterEvent(String strFn)
	{
		if (mblPOCRegister)
		{
			int result = serviceMgr.fnRegister(strFn);

			if (mTrainState.g_DebugLog_Lev1 != 0x00)
				LogInstance.debug(GlobalPara.Tky, "serviceMgr.funcNumRegister: " +strFn);

			if (result != ServiceConstant.RESPONSE_SUCCESS)
			{
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "serviceMgr.funcNumRegister: failed (" + strFn+")");
			}
			else
			{
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "serviceMgr.funcNumRegister: successfully (" + strFn+")");
				return  0;
			}
		}

		return  -1;
	}

	public int FunctionNumberFNUnRegisterEvent(String strFn)
	{
		if (mblPOCRegister)
		{
			int result = serviceMgr.fnUnregister(strFn);	//20141020
			if (mTrainState.g_DebugLog_Lev1 != 0x00)
				LogInstance.debug(GlobalPara.Tky, "serviceMgr.funcNumUnregister: " + strFn);//20141020
			if (result != ServiceConstant.RESPONSE_SUCCESS)
			{
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "serviceMgr.fnUnregister: failed (" + strFn+")");
			}
			else
			{
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "serviceMgr.fnUnregister: successfully (" + strFn+")");
				return 0;
			}
		}
		return  -1;
	}

	public int JoinDoubleHeadingGroup(String strMainEngineNumber)
	{
		try
		{
			int ret = ServiceConstant.METHOD_FAILED;
			if (mblPOCRegister)
			{
				//String strCurEngineNumber = String.format(("%c%c%c%c%c%c%c%c"), mTrainState.g_EngineNumber.Number[0],
				//		mTrainState.g_EngineNumber.Number[1], mTrainState.g_EngineNumber.Number[2],
				//		mTrainState.g_EngineNumber.Number[3], mTrainState.g_EngineNumber.Number[4],
				//		mTrainState.g_EngineNumber.Number[5], mTrainState.g_EngineNumber.Number[6],
				//		mTrainState.g_EngineNumber.Number[7]);

				String strCurEngineNumber = GlobalPara.strLoginUsrname;
				ret = serviceMgr.joinDoubleHeadingGroup(strMainEngineNumber, strCurEngineNumber);
				if(ret != ServiceConstant.METHOD_SUCCESS)
				{
					if (mTrainState.g_DebugLog_Lev1 != 0x00)
						LogInstance.error(GlobalPara.Tky, "175." + tag + ": join double heading group failed");
					//ret = serviceMgr.joinDoubleHeadingGroup(strMainEngineNumber, strCurEngineNumber);
					return 1;
				}
				else
				{
					if (mTrainState.g_DebugLog_Lev1 != 0x00)
						LogInstance.debug(GlobalPara.Tky, "join double heading group be called successful");
					return 0;
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
		return -1;
	}

	public int UnJoinDoubleHeadingGroup(String strMainEngineNumber)
	{
		try
		{
			int ret = ServiceConstant.METHOD_FAILED;
			if (mblPOCRegister)
			{
				if(!GlobalPara.strDoubleHeadingGroupID.equals(""))
				{
					//String strCurEngineNumber = String.format(("%c%c%c%c%c%c%c%c"), mTrainState.g_EngineNumber.Number[0],
					//		mTrainState.g_EngineNumber.Number[1], mTrainState.g_EngineNumber.Number[2],
					//		mTrainState.g_EngineNumber.Number[3], mTrainState.g_EngineNumber.Number[4],
					//		mTrainState.g_EngineNumber.Number[5], mTrainState.g_EngineNumber.Number[6],
					//		mTrainState.g_EngineNumber.Number[7]);
					//ret = serviceMgr.unjoinDoubleHeadingGroup(strMainEngineNumber, strCurEngineNumber,GlobalPara.strDoubleHeadingGroupID);
					String strCurEngineNumber = GlobalPara.strLoginUsrname;//20141020
					ret = serviceMgr.unjoinDoubleHeadingGroup(strMainEngineNumber, strCurEngineNumber,GlobalPara.strDoubleHeadingGroupID);//20141020
					if(ret != ServiceConstant.METHOD_SUCCESS)
					{
						if (mTrainState.g_DebugLog_Lev1 != 0x00)
							LogInstance.error(GlobalPara.Tky, "175." + tag + ": join double heading group failed, result is "+ret);
						return 1;
					}
				}
				else
				{
					if (mTrainState.g_DebugLog_Lev1 != 0x00)
						LogInstance.debug(GlobalPara.Tky, "join double heading group failed, group id is null");
					return -1;
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
		return -1;
	}

	public void SetDebugLogEnabled()
	{
		if (mTrainState.g_DebugLog_Lev3 == 0x00 && serviceMgr != null)
		{
			serviceMgr.setDebugLogEnabled(false);// 关闭日志
			if(mTrainState.g_DebugLog_Lev1 != 0x00)
				LogInstance.debug(GlobalPara.Tky, "close debug interface for poc");//debug/
		}
		else if (mTrainState.g_DebugLog_Lev3 != 0x00 && serviceMgr != null)
		{
			serviceMgr.setDebugLogEnabled(true);// 打开日志
			if(mTrainState.g_DebugLog_Lev1 != 0x00)
				LogInstance.debug(GlobalPara.Tky, "open debug interface for poc");//debug/
		}
	}

	public int iSleepType = 0;//20150210
	public boolean bCouldRegisterFunctionNumber = false;
	public void UnRegisterFunctionNumber(String strUnRegisterFunctionNumber)
	{
		if (mblPOCRegister && serviceMgr!=null)
		{
			bCouldRegisterFunctionNumber = false;
			serviceMgr.fnQuery(strUnRegisterFunctionNumber);
		}
	}

	/**
	 * POC接口-主动:下线
	 * @return
	 */
	public int OffLine()
	{
		POC_UnRegister();
		return 0;
	}

	/**
	 * POC接口-主动:上线
	 * @return
	 */
	public int UpLine()
	{
		String LTE_IP = getLocalIpAddress();// 获取IP
		if (mTrainState.g_DebugLog_Lev1 != 0x00)
			LogInstance.debug(GlobalPara.Tky, "getLocalIpAddress: " + LTE_IP);

		if (LTE_IP != null && !LTE_IP.equals(""))
		{
			try
			{
				int findindex1, findindex2;
				findindex1 = LTE_IP.indexOf(".");
				mTrainState.m_SourLteAddre[0] = Integer.decode(LTE_IP.substring(0, findindex1)).byteValue();
				findindex2 = LTE_IP.indexOf(".", findindex1 + 1);
				mTrainState.m_SourLteAddre[1] = Integer.decode(LTE_IP.substring(findindex1 + 1, findindex2)).byteValue();
				findindex1 = findindex2;
				findindex2 = LTE_IP.indexOf(".", findindex1 + 1);
				mTrainState.m_SourLteAddre[2] = Integer.decode(LTE_IP.substring(findindex1 + 1, findindex2)).byteValue();
				findindex1 = findindex2;
				mTrainState.m_SourLteAddre[3] = Integer.decode(LTE_IP.substring(findindex1 + 1)).byteValue();

				mblDhcpOk = true;
				mblLTE_IP = true;
				GlobalPara.iIpIsOk = 1;

				if (!mTrainState.mLTE_Msisdn.equals("") || mTrainState.mLTE_UserName.length() > 0)
				{
					POC_UnRegister();
					POC_Register();
				}
				if (!mblInitUdp)
				{
					StartUdpService();
				}
				return 0;
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
			mTrainState.m_SourLteAddre[0] = 0x00;
			mTrainState.m_SourLteAddre[1] = 0x00;
			mTrainState.m_SourLteAddre[2] = 0x00;
			mTrainState.m_SourLteAddre[3] = 0x00;

			mblDhcpOk = false;
			mblLTE_IP = false;
			GlobalPara.iIpIsOk = 0;

			return -1;
		}

		return -1;
	}

	/**
	 * POC接口-主动:发起通话(用户拨号发起呼叫时调用)
	 * @param calltype 01H:个呼 02H:组呼呼叫
	 * @param priority 不指定时填-1
	 * @param peerNum  号码
	 * @return -1呼叫失败,立即显示"呼叫失败"; 0发起成功 ,等待呼叫状态变化
	 */
	public int StartCall(int calltype, int priority,String peerNum)//_RawInfo PackageInfo
	{
		try
		{
			int retCode = -1, j;;

			if (peerNum.equals(""))
			{
				if (mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.error(GlobalPara.Tky, "153." + tag + ": peerNum should not be null");
				return -1;
			}

			String strTmpPeerNum = peerNum;//

			if(peerNum.startsWith(GlobalPara.strPreGlobalNumber))
			{//去除前面的0 8 6
				peerNum = peerNum.substring(3);
			}
			else if(peerNum.startsWith(GlobalPara.strPreNationNumber))
			{//去除前面的8 6
				peerNum = peerNum.substring(2);
			}

			if (mblPOCRegister)
			{
				//检查是否有重复的通话
				for(int i =0 ; i<callList.size();i++)
				{
					if(callList.get(i).callType == ServiceConstant.CALL_TYPE_SINGLE)
					{//个呼
						try
						{
							if(callList.get(i).peerNumber.equals(peerNum) || callList.get(i).funNumber.equals(peerNum) )
							{//存在同样的通话
								if(mTrainState.g_DebugLog_Lev1!= 0x00)
									LogInstance.debug(GlobalPara.Tky, "callist hava a same single call:"+peerNum+", so should makecall again, return now");

								return -1;
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
					{//组呼
						try
						{
							String str1 = callList.get(i).groupId.substring(callList.get(i).groupId.length()-3);
							String str2 = peerNum.substring(peerNum.length()-3);
							if(str1.equals(str2))
							{//存在同样的通话
								if(mTrainState.g_DebugLog_Lev1!= 0x00)
									LogInstance.debug(GlobalPara.Tky, "callist hava a same group call:"+peerNum+", so should makecall again, return now");

								return -1;
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
				}

				if(mTrainState.g_DebugLog_Lev1 != 0x00)
					LogInstance.debug(GlobalPara.Tky, "want to make call: "+peerNum);

				//StringBuffer sbCallId = new StringBuffer();
				mCurrentCall.callType = calltype ;
				mCurrentCall.peerNumber = peerNum;//在此处将peerNumber,groupId都置上,用哪个根据calltype判断,不影响呼叫
				mCurrentCall.groupId = peerNum;//在此处将peerNumber,groupId都置上,用哪个根据calltype判断,不影响呼叫

				switch (calltype)
				{// 呼叫类型
					case 0x01:// MSISDN或ISDN呼叫
						if (!mtrainNum.equals(""))
						{
							retCode = serviceMgr.makeCall(ServiceConstant.CALL_TYPE_SINGLE, mtrainNum, peerNum, priority ,new ServiceManager.CallIdListener()
							{
								@Override
								public void callIdNotify(String callId)
								{
									GlobalPara.strTempCallId = callId;
								}
							}  );// 不带优先级,由网络指定
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "serviceMgr.makeCall 1: priority=" + priority + " peerNum=" + peerNum + " mtrainNum="
										+ mtrainNum + " strCallId="+ GlobalPara.strTempCallId );
						}
						else if (!mLocofunNum.equals(""))
						{
							retCode = serviceMgr.makeCall(ServiceConstant.CALL_TYPE_SINGLE, mLocofunNum, peerNum, priority , new ServiceManager.CallIdListener()
							{
								@Override
								public void callIdNotify(String callId)
								{
									GlobalPara.strTempCallId = callId;
								}
							}  );// 不带优先级,由网络指定
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "serviceMgr.makeCall 2: priority=" + priority + " peerNum=" + peerNum + " mLocofunNum="
										+ mLocofunNum + " strCallId="+ GlobalPara.strTempCallId );
						}
						else
						{//自己未有功能号,个呼可以
							retCode = serviceMgr.makeCall(ServiceConstant.CALL_TYPE_SINGLE, "", peerNum, priority , new ServiceManager.CallIdListener()
							{
								@Override
								public void callIdNotify(String callId)
								{
									GlobalPara.strTempCallId = callId;
								}
							}  );// 不带优先级,由网络指定
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "serviceMgr.makeCall 3: priority=" + priority + " peerNum=" + peerNum + " funNum=, strCallId="+ GlobalPara.strTempCallId );
						}
						break;
					case 0x02:// 组呼呼叫
						if (!mtrainNum.equals(""))
						{
							retCode = serviceMgr.makeCall(ServiceConstant.CALL_TYPE_GROUP,mtrainNum, peerNum, priority ,new ServiceManager.CallIdListener()
							{
								@Override
								public void callIdNotify(String callId)
								{
									GlobalPara.strTempCallId = callId;
								}
							}   );// 不带优先级,由网络指定
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "serviceMgr.makeGroupCall: priority=" + priority + " peerNum=" + peerNum
										+ " mtrainNum=" + mtrainNum + " strCallId="+ GlobalPara.strTempCallId );
						}
						else if (!mLocofunNum.equals(""))
						{
							retCode = serviceMgr.makeCall(ServiceConstant.CALL_TYPE_GROUP, mLocofunNum, peerNum ,  priority , new ServiceManager.CallIdListener()
							{
								@Override
								public void callIdNotify(String callId)
								{
									GlobalPara.strTempCallId = callId;
								}
							}   );// 不带优先级,由网络指定
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "serviceMgr.makeGroupCall: priority=" + priority + " peerNum=" + peerNum
										+ " mLocofunNum=" + mLocofunNum + " strCallId="+ GlobalPara.strTempCallId );
						}
						else
						{//自己未有功能号,组呼发不起来,因此用默认的
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "have not funcNum, please attention");
							retCode = serviceMgr.makeCall(ServiceConstant.CALL_TYPE_GROUP, "", peerNum ,  priority , new ServiceManager.CallIdListener()
							{
								@Override
								public void callIdNotify(String callId)
								{
									GlobalPara.strTempCallId = callId;
								}
							}   );// 不带优先级,由网络指定
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "serviceMgr.makeGroupCall: priority=" + priority + " peerNum=" + peerNum
										+ " myfunctionnumber=" + "" + " strCallId="+ GlobalPara.strTempCallId );
						}
						break;
					case 0x03://
						String strFn = "";
						if (!mtrainNum.equals(""))
							strFn = mtrainNum;
						else if (!mLocofunNum.equals(""))
							strFn = mLocofunNum;
						else
							strFn = "";

						retCode = serviceMgr.makeCall(ServiceConstant.CALL_TYPE_BROADCAST, strFn, peerNum ,  priority , new ServiceManager.CallIdListener()
						{
							@Override
							public void callIdNotify(String callId)
							{
								GlobalPara.strTempCallId = callId;
							}
						} );// 不带优先级,由网络指定
						if (mTrainState.g_DebugLog_Lev1 != 0x00)
							LogInstance.debug(GlobalPara.Tky, "serviceMgr.makeBroadcastCall7: priority=" + priority + " peerNum=" + peerNum
									+ " strFn=" + strFn + " strCallId="+ GlobalPara.strTempCallId);
						break;
					case 0x04://
						if (!mtrainNum.equals(""))
						{
							retCode = serviceMgr.makeCall(ServiceConstant.CALL_TYPE_TEMPGROUP,mtrainNum, peerNum, priority ,new ServiceManager.CallIdListener()
							{
								@Override
								public void callIdNotify(String callId)
								{
									GlobalPara.strTempCallId = callId;
								}
							}   );// 不带优先级,由网络指定
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "serviceMgr.makeGroupCall: priority=" + priority + " peerNum=" + peerNum
										+ " mtrainNum=" + mtrainNum + " strCallId="+ GlobalPara.strTempCallId );
						}
						else if (!mLocofunNum.equals(""))
						{
							retCode = serviceMgr.makeCall(ServiceConstant.CALL_TYPE_TEMPGROUP, mLocofunNum, peerNum ,  priority , new ServiceManager.CallIdListener()
							{
								@Override
								public void callIdNotify(String callId)
								{
									GlobalPara.strTempCallId = callId;
								}
							}   );// 不带优先级,由网络指定
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "serviceMgr.makeGroupCall: priority=" + priority + " peerNum=" + peerNum
										+ " mLocofunNum=" + mLocofunNum + " strCallId="+ GlobalPara.strTempCallId );
						}
						else
						{//自己未有功能号,组呼发不起来,因此用默认的
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "have not funcNum, please attention");
							retCode = serviceMgr.makeCall(ServiceConstant.CALL_TYPE_TEMPGROUP, "", peerNum ,  priority , new ServiceManager.CallIdListener()
							{
								@Override
								public void callIdNotify(String callId)
								{
									GlobalPara.strTempCallId = callId;
								}
							}   );// 不带优先级,由网络指定
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "serviceMgr.makeGroupCall: priority=" + priority + " peerNum=" + peerNum
										+ " myfunctionnumber=" + "" + " strCallId="+ GlobalPara.strTempCallId );
						}
						break;
				}

				if (retCode == ServiceConstant.METHOD_SUCCESS)// 呼叫成功
				{//呼叫成功
					if (mCurrentCall == null)
					{
						if (mTrainState.g_DebugLog_Lev1 != 0x00)
							LogInstance.debug(GlobalPara.Tky, "mCurrentCall should not be null");
						return -1;
					}
					synchronized (pocevent)
					{//20140829改
						mCurrentCall.strCallId = GlobalPara.strTempCallId;
						mCurrentCall.callType = calltype ;

						mCurrentCall.peerNumber = peerNum;//在此处将peerNumber,groupId都置上,用哪个根据calltype判断,不影响呼叫
						mCurrentCall.groupId = peerNum;//在此处将peerNumber,groupId都置上,用哪个根据calltype判断,不影响呼叫

						//
						if(calltype == 0x01  &&  (strTmpPeerNum.startsWith("0862") || strTmpPeerNum.startsWith("0863") ||
								strTmpPeerNum.startsWith("2") || strTmpPeerNum.startsWith("3")   ) ) {
							if(!strTmpPeerNum.startsWith("086"))
								mCurrentCall.funNumber = "086" + strTmpPeerNum;
						}

						if(callList.size() == 0)
						{//先运行
							CALL call = new CALL();
							call.strCallId = mCurrentCall.strCallId;
							call.callType = mCurrentCall.callType;
							call.peerNumber = mCurrentCall.peerNumber;
							call.groupId = mCurrentCall.groupId;
							call.call_way = call_out;
							call.call_handout = false;

							//
							call.funNumber = mCurrentCall.funNumber;

							callList.add(call);
							if(mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "add a new call in makecall function");
						}
						else
						{//在callStatusNotify后运行
							for(int i=0 ; i< callList.size(); i++)
							{
								if(mCurrentCall.strCallId.equals(callList.get(i).strCallId))
								{
									callList.get(i).peerNumber = mCurrentCall.peerNumber;
									callList.get(i).groupId = mCurrentCall.groupId;

									//
									callList.get(i).funNumber = mCurrentCall.funNumber;

									if(mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "modify a call in makecall function");
								}
							}
						}
					}

					return 0;
				}
				else
				{//呼叫失败
					if (mTrainState.g_DebugLog_Lev1 != 0x00)
						LogInstance.debug(GlobalPara.Tky, "serviceMgr.makeCall 8: failed");

					return -1;
				}
			}
			else
			{//未登录poc时所有呼叫都按呼叫失败处理
				return -1;
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
		return -1;
	}

	/**
	 * POC接口-主动:接听或者挂机(用户操作接听或挂断时调用)
	 * @param bOperate 00H:挂机，01H:接听
	 * return -1:调用失败,立即显示"通话结束" ,0 调用成功,等待通话状态变化
	 */
	public int AnswerOrEndCall(byte bOperate)//bOperate==0x00 挂 bOperate==0x01 接听
	{
		try
		{
			int j;
			if (mblPOCRegister)
			{
				switch (bOperate)
				{
					case 0x00: // 挂机
						LogInstance.debug(GlobalPara.Tky, "close handler to finish");

						if(callList.size() == 0)
							return -1;

						if (mCurrentCall != null && mCurrentCall.statusCode != ServiceConstant.CALL_STATE_IDLE)
						{
							if (mCurrentCall.callType == ServiceConstant.CALL_TYPE_SINGLE)// 挂个呼
							{
								if (mTrainState.g_DebugLog_Lev1 != 0x00)
									LogInstance.debug(GlobalPara.Tky, "close handler: end single call");
								if (mCurrentCall.strCallId != null && !mCurrentCall.strCallId.equals(""))
								{
									if(mCurrentCall.statusCode == ServiceConstant.CALL_STATE_IN_CALL
											|| mCurrentCall.statusCode == ServiceConstant.CALL_STATE_HOLD
											|| mCurrentCall.statusCode == ServiceConstant.CALL_STATE_HOLDED)
									{//通话中则为endcall
										if (mTrainState.g_DebugLog_Lev1 != 0x00)
											LogInstance.debug(GlobalPara.Tky, "serviceMgr.endCall: peerNumber="+mCurrentCall.peerNumber + " funNumber="+mCurrentCall.funNumber + " callType="+mCurrentCall.callType + " priority="+mCurrentCall.priority+" strCallId=" + mCurrentCall.strCallId);
										int retCode = serviceMgr.endCall(mCurrentCall.strCallId, ServiceConstant.CALL_REASON_HANDLE);
										if (retCode != ServiceConstant.METHOD_SUCCESS)
										{
											LogInstance.error(GlobalPara.Tky, "145." + tag + ": serviceMgr.endCall failed");
											CALL tCall = null;
											for(int i = 0 ; i<callList.size() ; i++)
											{
												tCall = callList.get(i);
												if(callList.get(i).strCallId.equals(mCurrentCall.strCallId))
												{
													callList.remove(tCall);
													LogInstance.debug(GlobalPara.Tky, "should remove this call from calllist1");
													break;
												}
											}
										}
										else
										{//调用成功,等待状态变化
											return 0;
										}
									}
									else
									{//其他状态,如果主叫则cancelcall,如果被叫则为rejectcall
										if(mCurrentCall.call_way == call_in)
										{//rejectcall
											if (mTrainState.g_DebugLog_Lev1 != 0x00)
												LogInstance.debug(GlobalPara.Tky, "serviceMgr.rejectCall: peerNumber="+mCurrentCall.peerNumber + " funNumber="+mCurrentCall.funNumber + " callType="+mCurrentCall.callType + " priority="+mCurrentCall.priority+" strCallId=" + mCurrentCall.strCallId);
											int retCode = serviceMgr.rejectCall(mCurrentCall.strCallId, ServiceConstant.CALL_REASON_HANDLE);
											if (retCode != ServiceConstant.METHOD_SUCCESS)
											{
												LogInstance.error(GlobalPara.Tky, "146." + tag + ": serviceMgr.rejectCall failed");
											}
											else
											{//调用成功,等待状态变化
												return 0;
											}
										}
										else
										{//cancelcall
											if (mTrainState.g_DebugLog_Lev1 != 0x00)
												LogInstance.debug(GlobalPara.Tky, "serviceMgr.cancelCall: peerNumber="+mCurrentCall.peerNumber + " funNumber="+mCurrentCall.funNumber + " callType="+mCurrentCall.callType + " priority="+mCurrentCall.priority+" strCallId=" + mCurrentCall.strCallId);
											int retCode = serviceMgr.cancelCall(mCurrentCall.strCallId, ServiceConstant.CALL_REASON_HANDLE);
											if (retCode != ServiceConstant.METHOD_SUCCESS)
											{
												LogInstance.error(GlobalPara.Tky, "147." + tag + ": serviceMgr.cancelCall failed");
											}
											else
											{//调用成功,等待状态变化
												return 0;
											}
										}
									}
								}
							}
							else if (mCurrentCall.callType != ServiceConstant.CALL_TYPE_SINGLE )// 挂组呼广播
							{
								if (mCurrentCall.call_way == call_in)
								{//组呼参与者
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "close handler: exit group call, callee");
									String strCurrentGroupId = mCurrentCall.groupId;
									//if(mCurrentCall.priority == 0 && (mCurrentCall.groupId != null && !mCurrentCall.groupId.equals("") && mCurrentCall.groupId.endsWith(GlobalPara.strTailFor299) ))//299组呼被叫者,不允许退出
									if(mCurrentCall.priority <= 2 && (mCurrentCall.groupId != null && !mCurrentCall.groupId.equals("") ))
									{//2级及以下组呼被叫不能退出
										if (mTrainState.g_DebugLog_Lev1 != 0x00)
                                            LogInstance.debug(GlobalPara.Tky, "callee in high group call, can't exit");
                                        ToastUtil.showShortToast("非发起者,无法退出");

										return 0;//不能退出,等待状态变化
									}
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "serviceMgr.exitGroupCall: groupId=" + strCurrentGroupId + " callType="+mCurrentCall.callType + " priority="+mCurrentCall.priority+" strCallId="+mCurrentCall.strCallId);

									int retcode = serviceMgr.exitCall(mCurrentCall.strCallId, ServiceConstant.CALL_REASON_HANDLE);
									if (retcode == ServiceConstant.RESPONSE_SUCCESS)
									{//调用成功,等待状态变化
										synchronized (pocevent)
										{
											CALL call;
											for (j = 0; j < callList.size(); j++)// 手动退出
											{
												call = callList.get(j);
												if (call.call_way == call_in)
												{
													if (strCurrentGroupId.equals(call.groupId))
													{
														call.call_handout = true;
														if (mTrainState.g_DebugLog_Lev1 != 0x00)
															LogInstance.debug(GlobalPara.Tky, "set call_handout = true: peerNumber=" + call.peerNumber
																	+ " funNumber=" + call.funNumber + " groupId=" + call.groupId + " callType="+mCurrentCall.callType + " priority="+mCurrentCall.priority+" strCallId="+call.strCallId);
														break;
													}
												}
											}
										}
										return 0;
									}
									else
									{
										LogInstance.error(GlobalPara.Tky, "147." + tag + ": serviceMgr.exitCall failed");
									}
								}
								else
								{//组呼发起者
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "close handler: end group call, caller");
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "serviceMgr.endGroupCall: groupId=" + mCurrentCall.groupId + " callType="+mCurrentCall.callType + " priority="+mCurrentCall.priority+" strCallId="+mCurrentCall.strCallId);
									int retCode = serviceMgr.endCall(mCurrentCall.strCallId, ServiceConstant.CALL_REASON_HANDLE);
									if (retCode != ServiceConstant.METHOD_SUCCESS)
									{
										LogInstance.error(GlobalPara.Tky, "148." + tag + ": serviceMgr.endCall failed");
										CALL tCall = null;
										for(int i = 0 ; i<callList.size() ; i++)
										{
											tCall = callList.get(i);
											if(callList.get(i).strCallId.equals(mCurrentCall.strCallId))
											{
												callList.remove(tCall);
												LogInstance.error(GlobalPara.Tky, "149." + tag + ": should remove this call from calllist1");
												break;
											}
										}
									}
									else
									{//调用成功,等待状态变化
										return 0;
									}
								}
							}
						}
						break;
					case 0x01: // 摘机
						LogInstance.debug(GlobalPara.Tky, "pick up handler");

						if(callList.size() == 0)
							return -1;

						CALL aCall = null;
						for (CALL tmpCall : callList)
						{
							if(tmpCall.statusCode == ServiceConstant.CALL_STATE_IN_CALL)
							{//已有正处于通话中的电话,不用再去接听其他的,直接退出即可
								return -1;
							}
							if(aCall == null)
							{
								aCall = tmpCall;
								continue;
							}
							if(tmpCall.priority < aCall.priority)
							{//发现优先级更高的通话
								aCall = tmpCall;
							}
						}
						if(aCall != null)
						{//执行到这说明都不是处于通话状态
							if(aCall.strCallId != null && !aCall.strCallId.equals(""))
							{
								if(aCall.statusCode == ServiceConstant.CALL_STATE_HOLD || aCall.statusCode == ServiceConstant.CALL_STATE_HOLDED)
								{//保持恢复
									LogInstance.debug(GlobalPara.Tky, "pick up handler to recoveryCall: peerNumber=" + aCall.peerNumber
											+ " funNumber="+aCall.funNumber + " groupId="+aCall.groupId + " callType="+aCall.callType + " priority="+aCall.priority+" strCallId="+aCall.strCallId);
									int retCode = serviceMgr.recoveryCall(aCall.strCallId);
									if (retCode != ServiceConstant.METHOD_SUCCESS)
									{
										LogInstance.error(GlobalPara.Tky, "150." + tag + ": serviceMgr.recoveryCall failed");
									}
									else
									{//调用成功,等待状态变化
										return 0;
									}
								}
								else if(aCall.statusCode == ServiceConstant.CALL_STATE_INCOMING )
								{//被叫接听
									LogInstance.debug(GlobalPara.Tky, "pick up handler to answerCall: peerNumber=" + aCall.peerNumber
											+ " funNumber="+aCall.funNumber + " groupId="+aCall.groupId + " callType="+aCall.callType + " priority="+aCall.priority+" strCallId="+aCall.strCallId);
									int retCode = serviceMgr.answerCall(aCall.strCallId, ServiceConstant.ANSWER_TYPE_HANDLE);
									if (retCode != ServiceConstant.METHOD_SUCCESS)
									{
										LogInstance.error(GlobalPara.Tky, "151." + tag + ": serviceMgr.answerCall failed");
									}
									else
									{//调用成功,等待状态变化
										return 0;
									}
								}
								else
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "pick up handler: should do nothing, because statecode is not right");
								}
							}
						}
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
		return -1;
	}

	/**
	 * POC接口-主动:接通、保持、挂断电话(多路通话时选择接听某个电话时调用,作用类似AnswerOrEndCall)
	 * @param bOperater 00H时:挂断当前通话 01H时:接入指定序号的通话
	 * @param iSequence 接通电话时为需要接入通话的序号(16进制),挂机时不看该参数指挂当前电话
	 * @return  -1 操作失败 提示"操作失败" ; 0 操作成功 等待呼叫状态变化
	 */
	public int DealCall(int bOperater, int iSequence)
	{
		try
		{
			if (mblPOCRegister)
			{
				int retcode = ServiceConstant.METHOD_FAILED;
				switch (bOperater)
				{
					case 0x00:// 挂断
						// synchronized (pocevent)
						// {
						if (mTrainState.g_DebugLog_Lev1 != 0x00)
							LogInstance.debug(GlobalPara.Tky, "close call by hold command 0x15");
						if(callList.size() == 0)
						{//清除
							return -1;
						}
						if (mCurrentCall != null && mCurrentCall.statusCode != ServiceConstant.CALL_STATE_IDLE)
						{
							if (mCurrentCall.callType == ServiceConstant.CALL_TYPE_SINGLE)
							{
								if (mTrainState.g_DebugLog_Lev1 != 0x00)
									LogInstance.debug(GlobalPara.Tky, "hold command 0x15: end single call");
								if (mCurrentCall.strCallId != null && !mCurrentCall.strCallId.equals(""))
								{
									if(mCurrentCall.statusCode == ServiceConstant.CALL_STATE_IN_CALL
											|| mCurrentCall.statusCode == ServiceConstant.CALL_STATE_HOLD
											|| mCurrentCall.statusCode == ServiceConstant.CALL_STATE_HOLDED)
									{//通话中则为endcall
										if (mTrainState.g_DebugLog_Lev1 != 0x00)
											LogInstance.debug(GlobalPara.Tky, "serviceMgr.endCall: peerNumber="+mCurrentCall.peerNumber + " funNumber="+mCurrentCall.funNumber + " callType="+mCurrentCall.callType + " priority="+mCurrentCall.priority+" strCallId=" + mCurrentCall.strCallId);
										int retCode = serviceMgr.endCall(mCurrentCall.strCallId, ServiceConstant.CALL_REASON_HANDLE);
										if (retCode != ServiceConstant.METHOD_SUCCESS)
										{
											LogInstance.error(GlobalPara.Tky, "154." + tag + ": serviceMgr.endCall failed");
											CALL tCall = null;
											for(int i = 0 ; i<callList.size() ; i++)
											{
												tCall = callList.get(i);
												if(callList.get(i).strCallId.equals(mCurrentCall.strCallId))
												{
													callList.remove(tCall);
													LogInstance.debug(GlobalPara.Tky, "should remove this call from calllist1");
													break;
												}
											}
										}
										else
										{//调用成功,等待状态变化
											return 0;
										}
									}
									else
									{//其他状态,如果主叫则cancelcall,如果被叫则为rejectcall
										if(mCurrentCall.call_way == call_in)
										{//rejectcall
											if (mTrainState.g_DebugLog_Lev1 != 0x00)
												LogInstance.debug(GlobalPara.Tky, "serviceMgr.rejectCall: peerNumber="+mCurrentCall.peerNumber + " funNumber="+mCurrentCall.funNumber + " callType="+mCurrentCall.callType + " priority="+mCurrentCall.priority+" strCallId=" + mCurrentCall.strCallId);
											int retCode = serviceMgr.rejectCall(mCurrentCall.strCallId, ServiceConstant.CALL_REASON_HANDLE);
											if (retCode != ServiceConstant.METHOD_SUCCESS)
											{
												LogInstance.error(GlobalPara.Tky, "155." + tag + ": serviceMgr.rejectCall failed");
											}
											else
											{//调用成功,等待状态变化
												return 0;
											}
										}
										else
										{//cal
											if (mTrainState.g_DebugLog_Lev1 != 0x00)
												LogInstance.debug(GlobalPara.Tky, "serviceMgr.cancelCall: peerNumber="+mCurrentCall.peerNumber + " funNumber="+mCurrentCall.funNumber + " callType="+mCurrentCall.callType + " priority="+mCurrentCall.priority+" strCallId=" + mCurrentCall.strCallId);
											int retCode = serviceMgr.cancelCall(mCurrentCall.strCallId, ServiceConstant.CALL_REASON_HANDLE);
											if (retCode != ServiceConstant.METHOD_SUCCESS)
											{
												LogInstance.error(GlobalPara.Tky, "156." + tag + ": serviceMgr.cancelCall failed");
											}
											else
											{//调用成功,等待状态变化
												return 0;
											}
										}
									}
								}
							}
							else if (mCurrentCall.callType != ServiceConstant.CALL_TYPE_SINGLE )// 挂组呼广播
							{
								if (mCurrentCall.call_way == call_in)
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "hold command 0x15: exit group call, callee");
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "serviceMgr.exitGroupCall: groupId=" + mCurrentCall.groupId + " callType="+mCurrentCall.callType + " priority="+mCurrentCall.priority);

									//if(mCurrentCall.priority == 0 && (mCurrentCall.groupId != null && !mCurrentCall.groupId.equals("") && mCurrentCall.groupId.endsWith(GlobalPara.strTailFor299) ))//299组呼被叫者,不允许退出
									if(mCurrentCall.priority <= 2 && (mCurrentCall.groupId != null && !mCurrentCall.groupId.equals("")  ))
									{//2级及以下组呼被叫不能退出
										if (mTrainState.g_DebugLog_Lev1 != 0x00)
                                            LogInstance.debug(GlobalPara.Tky, "callee in high group call which prority <= 2 can't exit");
                                        ToastUtil.showShortToast("非发起者,无法退出");

										return 0;
									}
									int retCode = serviceMgr.exitCall(mCurrentCall.strCallId, ServiceConstant.CALL_REASON_HANDLE);
									if (retCode != ServiceConstant.METHOD_SUCCESS)
									{
										LogInstance.error(GlobalPara.Tky, "157." + tag + ": serviceMgr.exitCall failed");
									}
									else
									{//调用成功,等待状态变化
										return 0;
									}
								}
								else
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "hold command 0x15: end group call, caller");
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "serviceMgr.endGroupCall: groupId=" + mCurrentCall.groupId + " callType="+mCurrentCall.callType + " priority="+mCurrentCall.priority);
									int retCode = serviceMgr.endCall(mCurrentCall.strCallId, ServiceConstant.CALL_REASON_HANDLE);
									if (retCode != ServiceConstant.METHOD_SUCCESS)
									{
										LogInstance.error(GlobalPara.Tky, "158." + tag + ": serviceMgr.endCall failed");
										CALL tCall = null;
										for(int i = 0 ; i<callList.size() ; i++)
										{
											tCall = callList.get(i);
											if(callList.get(i).strCallId.equals(mCurrentCall.strCallId))
											{
												callList.remove(tCall);
												LogInstance.debug(GlobalPara.Tky, "should remove this call from calllist1");
												break;
											}
										}

									}
									else
									{//调用成功,等待状态变化
										return 0;
									}
								}
							}
						}
						// }
						break;
					case 0x01:// 接通
						// synchronized (pocevent)
						// {
						if (callList.size() > 0)
						{
							//if ((PackageInfo.Data[headdata + 1] - 1) < callList.size())// 20140307改
							if (iSequence < callList.size())// 指定呼叫序号<总呼叫数
							{
								CALL call = null;
								call = callList.get(iSequence);//call = callList.get(PackageInfo.Data[headdata + 1] - 1);// 20140307改
								if (mCurrentCall.callType == ServiceConstant.CALL_TYPE_SINGLE && !mCurrentCall.strCallId.equals("")
										&& !mCurrentCall.strCallId.equals(call.strCallId))
								{//当前为个呼
									if (call.callType != ServiceConstant.CALL_TYPE_SINGLE)
									{ // 要接入的是组呼广播,挂断原单呼
										synchronized (pocevent)
										{
											for (int i = 0; i < callList.size(); i++)
											{// 新接入组呼时,应该是挂掉所有个呼(比如当前正有两个通话,只挂掉当前是不够的)
												CALL tCall = callList.get(i);
												if (tCall.callType == ServiceConstant.CALL_TYPE_SINGLE && !tCall.strCallId.equals(call.strCallId))// 个呼
												{
													if (mTrainState.g_DebugLog_Lev1 != 0x00)
														LogInstance.debug(GlobalPara.Tky, "want to end: " + tCall.peerNumber);
													if(tCall.statusCode == ServiceConstant.CALL_STATE_IN_CALL
															|| tCall.statusCode == ServiceConstant.CALL_STATE_HOLD
															|| tCall.statusCode == ServiceConstant.CALL_STATE_HOLDED)
													{//通话中则为endcall
														if (mTrainState.g_DebugLog_Lev1 != 0x00)
															LogInstance.debug(GlobalPara.Tky, "serviceMgr.endCall: peerNumber="+tCall.peerNumber + " funNumber="+tCall.funNumber + " callType="+tCall.callType + " priority="+tCall.priority+" strCallId=" + tCall.strCallId);
														int retCode = serviceMgr.endCall(tCall.strCallId, ServiceConstant.CALL_REASON_HANDLE);
														if (retCode != ServiceConstant.METHOD_SUCCESS)
														{
															LogInstance.error(GlobalPara.Tky, "159." + tag + ": serviceMgr.endCall failed");
															if(callList.size()>0)
															{
																i--;//在for循环中动态删除
																callList.remove(tCall);
																LogInstance.debug(GlobalPara.Tky, "should remove this call from calllist1");
															}
														}
													}
													else
													{//其他状态,如果主叫则cancelcall,如果被叫则为rejectcall
														if(tCall.call_way == call_in)
														{//rejectcall
															if (mTrainState.g_DebugLog_Lev1 != 0x00)
																LogInstance.debug(GlobalPara.Tky, "serviceMgr.rejectCall: peerNumber="+tCall.peerNumber + " funNumber="+tCall.funNumber + " callType="+tCall.callType + " priority="+tCall.priority+" strCallId=" + tCall.strCallId);
															int retCode = serviceMgr.rejectCall(tCall.strCallId, ServiceConstant.CALL_REASON_HANDLE);
															if (retCode != ServiceConstant.METHOD_SUCCESS)
																LogInstance.error(GlobalPara.Tky, "160." + tag + ": serviceMgr.rejectCall failed");
														}
														else
														{//cancelcall
															if (mTrainState.g_DebugLog_Lev1 != 0x00)
																LogInstance.debug(GlobalPara.Tky, "serviceMgr.cancelCall: peerNumber="+tCall.peerNumber + " funNumber="+tCall.funNumber + " callType="+tCall.callType + " priority="+tCall.priority+" strCallId=" + tCall.strCallId);
															int retCode = serviceMgr.cancelCall(tCall.strCallId, ServiceConstant.CALL_REASON_HANDLE);
															if (retCode != ServiceConstant.METHOD_SUCCESS)
																LogInstance.error(GlobalPara.Tky, "161." + tag + ": serviceMgr.cancelCall failed");
														}
													}
												}
												else if(tCall.callType != ServiceConstant.CALL_TYPE_SINGLE && !tCall.strCallId.equals(call.strCallId))
												{
													//组呼参与者或发起者都是退出~  20140918修改成判断是否是发起者,发起者仍然需要endcall
													if (mTrainState.g_DebugLog_Lev1 != 0x00)
														LogInstance.debug(GlobalPara.Tky, "want to end: exit group call, callee");
													String strCurrentGroupId = tCall.groupId;
													if (mTrainState.g_DebugLog_Lev1 != 0x00)
														LogInstance.debug(GlobalPara.Tky, "serviceMgr.exitGroupCall: groupId=" + strCurrentGroupId + " callType="+tCall.callType + " priority="+tCall.priority+" strCallId="+tCall.strCallId);
													if(tCall.call_way == call_in)
													{
														int retCode = serviceMgr.exitCall(tCall.strCallId, ServiceConstant.CALL_REASON_HANDLE);
														if (retCode != ServiceConstant.METHOD_SUCCESS)
															LogInstance.error(GlobalPara.Tky, "162." + tag + ": serviceMgr.exitCall failed");
													}
													else
													{
														int retCode = serviceMgr.endCall(tCall.strCallId, ServiceConstant.CALL_REASON_HANDLE);
														if (retCode != ServiceConstant.METHOD_SUCCESS)
															LogInstance.error(GlobalPara.Tky, "163." + tag + ": serviceMgr.endCall failed");
													}
												}
											}
										}
									}
									else
									{// 新呼叫仍然是单呼,保持原单呼
										if (mTrainState.g_DebugLog_Lev1 != 0x00)
											LogInstance.debug(GlobalPara.Tky, "want to hold: " + mCurrentCall.peerNumber);
										if (mTrainState.g_DebugLog_Lev1 != 0x00)
											LogInstance.debug(GlobalPara.Tky, "serviceMgr.holdCall: " + mCurrentCall.peerNumber + " callType="+mCurrentCall.callType + " priority="+mCurrentCall.priority);

										//标记开始操作holdcall
										bIsHoldSuccessful = -1;
										int iTimersForHoldcallCheck = 0;
										int retCode = serviceMgr.holdCall(mCurrentCall.strCallId);
										if (retCode != ServiceConstant.METHOD_SUCCESS)
											LogInstance.error(GlobalPara.Tky, "164." + tag + ": serviceMgr.holdCall failed");
										//等待hold成功
										while(iTimersForHoldcallCheck < 10)
										{
											if(bIsHoldSuccessful != -1)
												break;
											Thread.sleep(100);
											LogInstance.debug(GlobalPara.Tky, "iTimersForHoldcallCheck:"+iTimersForHoldcallCheck);
											iTimersForHoldcallCheck++;
										}
										if(bIsHoldSuccessful == 1)
										{
											if (mTrainState.g_DebugLog_Lev1!=0x00)
												LogInstance.debug(GlobalPara.Tky, "holdcall successful");
										}
										else if (bIsHoldSuccessful == 0)
										{
											if (mTrainState.g_DebugLog_Lev1!=0x00)
												LogInstance.error(GlobalPara.Tky, "165." + tag + ": holdcall failed, attention!");
										}
									}
								}
								else if (mCurrentCall.callType != ServiceConstant.CALL_TYPE_SINGLE && !mCurrentCall.strCallId.equals("") && !mCurrentCall.strCallId.equals(call.strCallId))
								{// 原呼叫为组呼广播,直接退出原组呼 ~ 20140918修改成判断是否是发起者,发起者仍然需要endcall
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "want to exit old group call: " + mCurrentCall.groupId);
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "serviceMgr.exitCall: " + mCurrentCall.groupId + " callType="+mCurrentCall.callType + " priority="+mCurrentCall.priority);

									if(mCurrentCall.call_way == call_in)
									{
										int retCode = serviceMgr.exitCall(mCurrentCall.strCallId, ServiceConstant.CALL_REASON_HANDLE);
										if (retCode != ServiceConstant.METHOD_SUCCESS)
											LogInstance.error(GlobalPara.Tky, "166." + tag + ": serviceMgr.exitCall failed");
									}
									else
									{
										int retCode = serviceMgr.endCall(mCurrentCall.strCallId, ServiceConstant.CALL_REASON_HANDLE);
										if (retCode != ServiceConstant.METHOD_SUCCESS)
											LogInstance.error(GlobalPara.Tky, "167." + tag + ": serviceMgr.endCall failed");
									}
								}

								Thread.sleep(200);
								if (call.statusCode == ServiceConstant.CALL_STATE_HOLD || call.statusCode == ServiceConstant.CALL_STATE_HOLDED)
								{//以前为保持则激活
									if (call.strCallId != null && !call.strCallId.equals(""))
									{
										if (mTrainState.g_DebugLog_Lev1 != 0x00)
											LogInstance.debug(GlobalPara.Tky, "serviceMgr.recoveryCall: peerNumber=" + call.peerNumber + ", groupId="+call.groupId + " callType="+mCurrentCall.callType + " priority="+mCurrentCall.priority);
										retcode = serviceMgr.recoveryCall(call.strCallId);
									}

									if (retcode == ServiceConstant.METHOD_SUCCESS)
									{
										//LogInstance.debug(GlobalPara.Tky, "call copy_call2Currentcall7 when recoveryCall");
										copy_call2Currentcall(call);
										return 0;
									}
									else
									{
										LogInstance.error(GlobalPara.Tky, "168." + tag + ": recoveryCall failed");
									}
								}
								else if (call.statusCode == ServiceConstant.CALL_STATE_INCOMING)// 来呼振铃
								{
									if (call.call_way == call_in)
									{
										if (mTrainState.g_DebugLog_Lev1 != 0x00)
											LogInstance.debug(GlobalPara.Tky, "serviceMgr.answerCall in hold call command: a call in coming, now to answer it");
										if (call.strCallId != null && !call.strCallId.equals(""))
										{
											if (mTrainState.g_DebugLog_Lev1 != 0x00)
												LogInstance.debug(GlobalPara.Tky, "serviceMgr.answerCall: peerNumber=" + call.peerNumber
														+ " funNumber="+call.funNumber + " groupId="+call.groupId + " callType="+mCurrentCall.callType + " priority="+mCurrentCall.priority);
											retcode = serviceMgr.answerCall(call.strCallId, ServiceConstant.ANSWER_TYPE_HANDLE );
										}

										if (retcode == ServiceConstant.METHOD_SUCCESS)
										{
											//LogInstance.debug(GlobalPara.Tky, "call copy_call2Currentcall8 when answerCall");
											copy_call2Currentcall(call);
											return 0;
										}
										else
										{
											if (mTrainState.g_DebugLog_Lev1 != 0x00)
												LogInstance.debug(GlobalPara.Tky, "answerCall failed in hold call command");
										}
									}
									else
									{
										if (mTrainState.g_DebugLog_Lev1 != 0x00)
											LogInstance.debug(GlobalPara.Tky,  "attention, statuscode is incoming but it is not call_in!") ;
									}
								}
								else
								{
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky,  "attention, statuscode is not incoming or hold!") ;
								}
							}
						}

						// }
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
		return -1;
	}

	/**
	 * POC接口-主动:抢占释放PTT  (在组呼呼叫界面, 用户按下或松开PTT时调用)
	 * @param bOperation 00H按下PTT, 01H释放PTT
	 * @return -1操作失败 立即显示"话权失败"; 0操作成功,等待话权变化通知
	 */
	public int PttOperation(int bOperation)//00H按下PTT, 01H释放PTT
	{
		int retCode = -1;
		try
		{
			if (mblPOCRegister)
			{
				if (mCurrentCall != null && (mCurrentCall.callType != ServiceConstant.CALL_TYPE_SINGLE ))
				{// 当前为组呼
					synchronized (objPttOpeation)
					{
						if (mnCIRPTTOldStatus != bOperation)
						{
							mnCIRPTTOldStatus = bOperation ;
						}
						else
						{
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "ptt old status is same to the current");
						}
						if (mnLTEPTTStatus != 0x01 /*&& mnLTEPTTStatus != 0x04*/)//mnLTEPTTStatus记录当前组呼话权状态: 0空闲 14忙 2抢站上//
						{// 当前为空闲或自己已抢用状态 ,0x01为他人已抢占
							if (!mtrainNum.equals("") || !mLocofunNum.equals(""))
							{
								if (mnCIRPTTOldStatus == 0)
								{//抢占
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "ptt push down");
									if (!mtrainNum.equals(""))
									{
										retCode = serviceMgr.pttOperate(mCurrentCall.strCallId, ServiceConstant.PTT_PRESS, mtrainNum);
										LogInstance.debug(GlobalPara.Tky, "serviceMgr.pushToTalk: " + mCurrentCall.groupId + " " + mnCIRPTTOldStatus + " " + mtrainNum);
										if (retCode != ServiceConstant.METHOD_SUCCESS)
											return -1;
									}
									else if (!mLocofunNum.equals(""))
									{
										retCode = serviceMgr.pttOperate(mCurrentCall.strCallId, ServiceConstant.PTT_PRESS, mLocofunNum);
										LogInstance.debug(GlobalPara.Tky, "serviceMgr.pushToTalk: " + mCurrentCall.groupId + " " + mnCIRPTTOldStatus + " " + mLocofunNum);
										if (retCode != ServiceConstant.METHOD_SUCCESS)
											return -1;
									}
								}
								else if (mnCIRPTTOldStatus == 1)
								{//释放
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "ptt pop up");

									if (!mtrainNum.equals(""))
										retCode = PopUpPTT(mCurrentCall.strCallId, ServiceConstant.PTT_RELEASE, mtrainNum);
									else if (!mLocofunNum.equals(""))
										retCode = PopUpPTT(mCurrentCall.strCallId, ServiceConstant.PTT_RELEASE, mLocofunNum);

									if (retCode != ServiceConstant.METHOD_SUCCESS)
										return -1;

								}
							}
							else
							{//未注册车次号或机车号,该部分不会被执行,没有功能号不能进行组呼呼叫
								if (mnCIRPTTOldStatus == 0)
								{//抢占
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "ptt push down");

									//20150128
									if(!mTrainState.mLTE_Msisdn.equals(""))
										retCode = serviceMgr.pttOperate(mCurrentCall.strCallId, ServiceConstant.PTT_PRESS, mTrainState.mLTE_Msisdn);
									else
										retCode = serviceMgr.pttOperate(mCurrentCall.strCallId, ServiceConstant.PTT_PRESS, mTrainState.mLTE_UserName);

									LogInstance.debug(GlobalPara.Tky, "serviceMgr.pushToTalk: " + mCurrentCall.groupId + " " + mnCIRPTTOldStatus + " " + mTrainState.mLTE_UserName);

									if (retCode != ServiceConstant.METHOD_SUCCESS)
										return -1;
								}
								else if (mnCIRPTTOldStatus == 1)
								{////释放 如果是松开PTT需要特殊操作,延时500ms发送,防止放的过早被截音
									if (mTrainState.g_DebugLog_Lev1 != 0x00)
										LogInstance.debug(GlobalPara.Tky, "ptt pop up");

									if(!mTrainState.mLTE_Msisdn.equals(""))
										retCode = PopUpPTT(mCurrentCall.strCallId, ServiceConstant.PTT_RELEASE, mTrainState.mLTE_Msisdn);
									else
										retCode = PopUpPTT(mCurrentCall.strCallId, ServiceConstant.PTT_RELEASE, mTrainState.mLTE_UserName);

									if (retCode != ServiceConstant.METHOD_SUCCESS)
										return -1;
								}
							}

							return 0;
						}
						else
						{//==0x01
							mnLTEPTTStatus = (byte)0x04;//
							if (mTrainState.g_DebugLog_Lev1 != 0x00)//20141011
								LogInstance.debug(GlobalPara.Tky,"mnLTEPTTStatus="+mnLTEPTTStatus);

							//
							if(callList.size()>0)
								ShowPttStateOnWindows(mnLTEPTTStatus, callList.get(0).strCallId,  "");
							else
								ShowPttStateOnWindows(mnLTEPTTStatus, "",  "");
						}
					}
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

		return -1;
	}

	public int PopUpPTT(String groupId, int op, String funcnum)
	{
		int retCode =-1;
		try
		{
			retCode = serviceMgr.pttOperate(groupId, op, funcnum);
			if (mTrainState.g_DebugLog_Lev1 != 0x00)
				LogInstance.debug(GlobalPara.Tky, "serviceMgr.pushToTalk: " + mCurrentCall.groupId + " " + mnCIRPTTOldStatus + " " + funcnum);
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
		return retCode;
	}

	/**
	 * POC接口-主动:加入重联组呼
	 * @param iOperater 0x01注册 0x00注销
	 * @param tempnumber  7字节Ascii码的车次号
	 * @return  -1 提示操作失败 0等待注册注销结果
	 * @throws Exception
	 */
	public int subApplyMainEngineNumberRegisterOrNot(int iOperater, byte[] tempnumber) throws Exception //_RawInfo PackageInfo
	{
		try
		{
			if (mTrainState.g_DebugLog_Lev1 != 0x00) {
				if (iOperater == 0x01)
					LogInstance.debug(GlobalPara.Tky,"receive and register main enginenumber from mmi");
				else
					LogInstance.debug(GlobalPara.Tky,"receive and unregister main enginenumber from mmi");
			}

			int i;
			String tempcstring = "";
			if (iOperater== 0x01) // 注册
			{
				tempcstring = String.format(("%c%c%c%c%c%c%c%c"), tempnumber[0], tempnumber[1], tempnumber[2],
						tempnumber[3], tempnumber[4], tempnumber[5], tempnumber[6], tempnumber[7]);

				String strOldMainEngineNumber = "";
				if ((!GlobalFunc.MemCmp(mTrainState.g_MainEngineNumber.Number, "XXXXXXXX", 8))
						&& (!GlobalFunc.MemCmp( mTrainState.g_MainEngineNumber.Number,tempnumber, 8)))
				{// // 不空且不等 (原先已定义主控机车号,且这次的主控机车号有变化),则需要注销原先的主控机车号
					strOldMainEngineNumber = String.format( ("%c%c%c%c%c%c%c%c"),
							mTrainState.g_MainEngineNumber.Number[0],
							mTrainState.g_MainEngineNumber.Number[1],
							mTrainState.g_MainEngineNumber.Number[2],
							mTrainState.g_MainEngineNumber.Number[3],
							mTrainState.g_MainEngineNumber.Number[4],
							mTrainState.g_MainEngineNumber.Number[5],
							mTrainState.g_MainEngineNumber.Number[6],
							mTrainState.g_MainEngineNumber.Number[7]);

					if (strOldMainEngineNumber != null && !strOldMainEngineNumber.equals(""))
					{
						LogInstance.debug(GlobalPara.Tky, "test and don't unregister mainenginenumber in subApplyForReceiveMainEngineNumberFromDte2");
						UnJoinDoubleHeadingGroup(tempcstring);
					}

					if (tempcstring != null && !tempcstring.equals(""))
					{//lte模式下需要进行加入重联组
						if (mTrainState.g_DebugLog_Lev1 != 0x00)
							LogInstance.debug(GlobalPara.Tky, "register new main enginenumber now because of mmi set");
						JoinDoubleHeadingGroup(tempcstring);
						return 0;
					}
				}
				else
				{//空或者相等
					if ((mTrainState.g_Mode == 0x66) )// LTE
					{// lte模式下需要进行加入重联组
						if (tempcstring != null && !tempcstring.equals(""))
						{
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "register new main enginenumber now because of mmi set");
							JoinDoubleHeadingGroup(tempcstring);
							return 0;
						}
					}
				}
				tempnumber = null;
			}
			else if (iOperater == 0x00) // 注销
			{//
				if (!GlobalFunc.MemCmp(mTrainState.g_MainEngineNumber.Number,"XXXXXXXX", 8))
				{
					if (mTrainState.g_Power == 0x01 && mTrainState.g_Mode == 0x66)
					{// lte模式下进行脱离重联组
						tempcstring = String.format(("%c%c%c%c%c%c%c%c"),
								mTrainState.g_MainEngineNumber.Number[0],
								mTrainState.g_MainEngineNumber.Number[1],
								mTrainState.g_MainEngineNumber.Number[2],
								mTrainState.g_MainEngineNumber.Number[3],
								mTrainState.g_MainEngineNumber.Number[4],
								mTrainState.g_MainEngineNumber.Number[5],
								mTrainState.g_MainEngineNumber.Number[6],
								mTrainState.g_MainEngineNumber.Number[7]);
						if (tempcstring != null && !tempcstring.equals(""))
						{
							if (mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky,"unregister main enginenumber now because of mmi set");
							UnJoinDoubleHeadingGroup(tempcstring);
							return 0;
						}
					}
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
			LogInstance.error(GlobalPara.Tky, "0." + tag+ ": "+ Thread.currentThread().getStackTrace()[2].getMethodName()+ ": "+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}

		return -1;
	}

	/**
	 * POC接口-主动:用户在查询功能号码列表时调用
	 * @return 0:调用成功 -1:调用失败
	 */
	public int QueryFunctionNumberList()	{
        int ret = -1;
		if(serviceMgr != null && mblPOCRegister)	{
			 ret = serviceMgr.fnQueryList();
			if(ret == ServiceConstant.METHOD_SUCCESS) {
                LogInstance.debug(GlobalPara.Tky, "serviceMgr.QueryFunctionNumberList METHOD_SUCCESS");
            }else {
                LogInstance.debug(GlobalPara.Tky, "serviceMgr.QueryFunctionNumberList METHOD_FAILED");
            }
		}
		return ret;
	}

	/**
	 *
	 * POC接口-主动:用户在查询某个功能号码是否被注册时调用
	 * @param szFN
	 * @return 0:调用成功 1:调用失败
	 */
	public int QueryFunctionNumberIsUsed(String szFN)
	{
		if(serviceMgr != null && mblPOCRegister)
		{
			int ret = serviceMgr.fnQuery(szFN);//
			if(ret == ServiceConstant.METHOD_SUCCESS)
				LogInstance.debug(GlobalPara.Tky, "serviceMgr.fnQuery METHOD_SUCCESS");
			else
				LogInstance.debug(GlobalPara.Tky, "serviceMgr.fnQuery METHOD_FAILED");

			return ret;
		}
		return -1;
	}

	/**
	 * POC接口-主动:用户在强制注销某个功能号码时调用
	 * @param szFN 强制注销的功能号码
	 * @param szUN 强制注销的用户号码(注册这个功能号码的用户)
	 * @return 0:调用成功 1:调用失败
	 */
	public int ForceUnRegisterFunctionNumber(String szFN, String szUN)
	{
		if(serviceMgr != null && mblPOCRegister)
		{
			int ret = serviceMgr.fnForceUnregister(szFN, szUN);//
			if(ret == ServiceConstant.METHOD_SUCCESS)
				LogInstance.debug(GlobalPara.Tky, "serviceMgr.fnForceUnregister METHOD_SUCCESS");
			else
				LogInstance.debug(GlobalPara.Tky, "serviceMgr.fnForceUnregister METHOD_FAILED");

			return ret;
		}
		return -1;
	}

	/**
	 * POC接口-主动:上报小区位置
	 * @param strLocaton
	 */
	public void ReportLocation(String strLocaton)
	{
		if(serviceMgr != null && mblPOCRegister)
		{
			serviceMgr.publishLocation(strLocaton);//
			if(mTrainState.g_DebugLog_Lev1 != 0x00)
				LogInstance.debug(GlobalPara.Tky, "serviceMgr.publishLocation be called: "+strLocaton);
		}
	}

	/**
	 * POC接口-主动:注册注销车次功能
	 * @param str 车次号 如K1234、123456
	 * @param iStatus 角色代码 0x00 0x01 0x02 0x03 0x04
	 * @param bRegisterOrUnRegister true注册 false注销
	 * @return
	 */
	public int TrainNumberRegisterOrNot(String str, int iStatus, boolean bRegisterOrUnRegister)
	{
		try
		{
			String strFn = GlobalFunc.TrainNumberToFunctionNumber(str, iStatus);

			if(bRegisterOrUnRegister) {
				LogInstance.debug(GlobalPara.Tky, "train number register now:"+strFn);
				return FunctionNumberFNRegisterEvent(strFn);
			}
			else {
				LogInstance.debug(GlobalPara.Tky, "train number unregister now:"+strFn);
				return FunctionNumberFNUnRegisterEvent(strFn);
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
		return -1;
	}

	/**
	 * POC接口-主动:注册注销机车功能
	 * @param str 机车号 如23107124 22500181
	 * @param iStatus 角色代码 0x00 0x01 0x02 0x03 0x04
	 * @param bRegisterOrUnRegister true注册 false注销
	 * @return
	 */
	public int EngineNumberRegisterOrNot(String str,int iStatus, boolean bRegisterOrUnRegister)
	{

        String runnumberstatus = "";
        if (iStatus < 10){
            runnumberstatus = "0" + String.valueOf(iStatus);
        }else{
            runnumberstatus = String.valueOf(iStatus);
        }

		try
		{
			String strFn = "";

            strFn = GlobalPara.strPreEfnNumber + str + runnumberstatus;

			if(bRegisterOrUnRegister) {
				LogInstance.debug(GlobalPara.Tky, "engine number register now:"+strFn);
				return FunctionNumberFNRegisterEvent(strFn);
			}
			else {
				LogInstance.debug(GlobalPara.Tky, "engine number unregister now:"+strFn);
				return FunctionNumberFNUnRegisterEvent(strFn);
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
		return  -1;
	}

	/**
	 * poc接口-主动:用于注册注销功能号码
	 * @param str 完整的功能号 如20000123401 32310712401
	 * @param bRegisterOrUnRegister true注册 false注销
	 * @return
	 */
	public int FunctionNumberRegisterOrNot(String str, boolean bRegisterOrUnRegister)
	{
		try
		{
			String strFn = str;


			if(bRegisterOrUnRegister) {
				LogInstance.debug(GlobalPara.Tky, "function number register now:"+strFn);
				return FunctionNumberFNRegisterEvent(strFn);
			}
			else {
				LogInstance.debug(GlobalPara.Tky, "function number unregister now:"+strFn);
				return FunctionNumberFNUnRegisterEvent(strFn);
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
		return  -1;
	}

	/**
	 * POC接口-主动:获得呼叫中组列表
	 * @return 呼叫中组列表
	 */
	public ArrayList<GroupInCall> GetActiveGroupCalls(){
		return lstGroupInCalls;
	}

	/**
	 * POC接口-主动:加入某个呼叫中组
	 * @param nCallType
	 * @param strGroupId
	 * @param szCallerFN
	 * @param nPriority
	 * @return -1加入失败 0成功
	 */
	public int JoinActiveGroupCall(int nCallType,  String strGroupId, String szCallerFN, int nPriority){

		int retCode = -1;
		mCurrentCall.callType = nCallType;
		mCurrentCall.peerNumber = strGroupId;//在此处将peerNumber,groupId都置上,用哪个根据calltype判断,不影响呼叫
		mCurrentCall.groupId = strGroupId;//
		mCurrentCall.priority = nPriority;//
		retCode = serviceMgr.joinActiveGroup(nCallType, strGroupId, szCallerFN, nPriority, new ServiceManager.CallIdListener()
		{
			@Override
			public void callIdNotify(String callId)
			{
				GlobalPara.strTempCallId = callId;
			}
		} );
		if (mTrainState.g_DebugLog_Lev1 != 0x00)
			LogInstance.debug(GlobalPara.Tky, "serviceMgr.joinActiveGroup1 for group in call: nCallType="+ nCallType +" iPriority=" + nPriority + " strGroupId=" + strGroupId
					+ " mtrainNum=" + mtrainNum + " strCallId="+ GlobalPara.strTempCallId);


		if (retCode == ServiceConstant.METHOD_SUCCESS)// 呼叫成功
		{
			mCurrentCall.strCallId = GlobalPara.strTempCallId;
			mCurrentCall.callType = nCallType;

			mCurrentCall.peerNumber = strGroupId;//在此处将peerNumber,groupId都置上,用哪个根据calltype判断,不影响呼叫
			mCurrentCall.groupId = strGroupId;//在此处将peerNumber,groupId都置上,用哪个根据calltype判断,不影响呼叫

			//mCurrentCall.statusCode = ServiceConstant.CALL_STATE_OUTGOING;//这个时候还不能确定
			mCurrentCall.priority = nPriority;//此处可以确定优先级在
			mCurrentCall.call_handout = false;
			//mCurrentCall.call_way = call_in;//这个时候还不能确定,都有可能,在notify中定

			synchronized (pocevent)
			{
				if(callList.size() == 0)
				{//先运行
					CALL call2 = new CALL();
					call2.strCallId = mCurrentCall.strCallId;
					call2.callType = mCurrentCall.callType;
					call2.peerNumber = mCurrentCall.peerNumber;
					call2.groupId = mCurrentCall.groupId;
					call2.call_way = call_out;
					call2.call_handout = false;
					callList.add(call2);
					if(mTrainState.g_DebugLog_Lev1 != 0x00)
						LogInstance.debug(GlobalPara.Tky, "add a new call in joinActiveGroup");
				}
				else
				{//在callStatusNotify后运行
					for(int k=0 ; k< callList.size(); k++)
					{
						if(mCurrentCall.strCallId.equals(callList.get(k).strCallId))
						{
							callList.get(k).peerNumber = mCurrentCall.peerNumber;
							callList.get(k).groupId = mCurrentCall.groupId;
							if(mTrainState.g_DebugLog_Lev1 != 0x00)
								LogInstance.debug(GlobalPara.Tky, "modify a call in joinActiveGroup function");
						}
					}
				}
			}

			if(callList.size() > 0){
				ShowCallChangeOnWindows();
			}
		}

		return retCode;
	}

	/**
	 * POC接口-主动:获得临时组呼列表
	 * @return 临时组呼列表
	 */
	public ArrayList<GroupTmpCall> GetTmpGroupCalls(){
		return lstGroupTmpCalls;
	}

    /**
	 * POC接口-被动:通知临时组呼列表变化
	 *
     * 操作临时组的时候发送通知
     */
    public void sendNotifyTmpGroupCallsChange(ArrayList<GroupTmpCall> lstGroupTmpCalls){
        GroupTmpList bean = new GroupTmpList();
        bean.setLstGroupTmpCalls(lstGroupTmpCalls);
        EventBus.getDefault().post(bean);
    }

	/**
	 * POC接口-主动:创建临时群组
	 * @param szGroupName  群组名称
	 * @param nGroupPriority  群组优先级
	 * @param nGroupMaxIdleTime   群组无讲者释放时长
	 * @param nGroupLifespan 群组生命周期
	 * @param objMemberList 群组成员列表
	 * @return
	 */
	public int CreateTmpGroup( String szGroupName,  int nGroupPriority,  int nGroupMaxIdleTime,  int nGroupLifespan,
							   ArrayList<String> objMemberList){
		int ret = -1;
		if(serviceMgr != null && mblPOCRegister)
		{
			ret = serviceMgr.createTmpGroup(szGroupName, nGroupPriority, nGroupMaxIdleTime, nGroupLifespan, objMemberList);
			if(mTrainState.g_DebugLog_Lev1 != 0x00)
				LogInstance.debug(GlobalPara.Tky, "serviceMgr.createTmpGroup be called");
		}
		return ret;
	}

	/**
	 * POC接口-主动:删除临时群组
	 * @param szGroupNumber 群组号码
	 * @return
	 */
	public int RemoveTmpGroup( String szGroupNumber ){
		int ret = -1;
		if(serviceMgr != null && mblPOCRegister)
		{
			ret = serviceMgr.removeTmpGroup(szGroupNumber);
			if(mTrainState.g_DebugLog_Lev1 != 0x00)
				LogInstance.debug(GlobalPara.Tky, "serviceMgr.removeTmpGroup be called");
		}
		return ret;
	}

	/**
	 * POC接口-主动:修改临时群组
	 * @param szGroupNumber 群组号码
	 * @param nGroupPriority 群组优先级
	 * @param objMemberList 群组成员列表
	 * @return
	 */
	public int ModifyTmpGroup( String szGroupNumber,  int nGroupPriority,  ArrayList<String> objMemberList){
		int ret = -1;
		if(serviceMgr != null && mblPOCRegister)
		{
			ret = serviceMgr.modifyTmpGroup(szGroupNumber, nGroupPriority , objMemberList);
			if(mTrainState.g_DebugLog_Lev1 != 0x00)
				LogInstance.debug(GlobalPara.Tky, "serviceMgr.modifyTmpGroup be called");
		}
		return ret;
	}


}

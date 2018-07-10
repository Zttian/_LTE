package com.tky.lte.lte;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.jiaxun.android.lte_r.ServiceConstant;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GlobalFunc
{
	public static String tag = "GlobalFunc";

	public static void cal_crc(byte[] ptr, int size, byte[] crc_result)
	{
		int i, j;
		byte bit_temp; /* bit calculate *//* procedure of calculate CRC */
		byte temp_data;

		byte CRC_HIGH = 0x10;
		byte CRC_LOW = 0x21;
		crc_result[0] = 0;
		crc_result[1] = 0;

		for (i = 0; i < size; i++)
		{
			temp_data = ptr[i] ;

			for (j = 0; j < 8; j++)
			{
				bit_temp = (byte) (crc_result[0] ^ temp_data); // XOR the first
				// bit
				bit_temp = (byte) (bit_temp & 0x80);

				temp_data = (byte) (temp_data << 1);

				crc_result[0] = (byte) (crc_result[0] << 1); // left shift crc
				// result buffer

				if ((byte) (crc_result[1] & 0x80) != 0)
				{
					crc_result[0] = (byte) (crc_result[0] | 0x01);
				}

				crc_result[1] = (byte) (crc_result[1] << 1);

				if (bit_temp == (byte) 0x80) // after XOR, if the first bit is
				// '1'
				{
					crc_result[0] = (byte) (crc_result[0] ^ CRC_HIGH);
					crc_result[1] = (byte) (crc_result[1] ^ CRC_LOW);
				}
			}
		}
	}

	public static int FormSCITxFrame(byte[] ptr, int count, byte[] ptr_dest)
	{// 发送数据前需要添加帧头帧尾并检查是否含有0x10字节
		int i = 0;
		int counter = 0;
		int dest = 0;// 新帧下标
		int src = 0;//
		ptr_dest[dest++] = 0x10;
		ptr_dest[dest++] = 0x02; // DLE,STX added
		counter += 2;
		for (i = 0; i < count; i++, src++, dest++, counter++) // DLE,DLE added
		{
			if (ptr[src] == 0x10)
			{
				ptr_dest[dest] = 0x10;
				dest++;
				ptr_dest[dest] = 0x10;// 遇到0x10添加一个0x10
				counter++;
			}
			else
			{
				ptr_dest[dest] = ptr[src] ;
			}
		}

		ptr_dest[dest] = 0x10; // DLE,ETX added
		dest++;
		ptr_dest[dest] = 0x03;
		counter += 2;
		return counter;
	}


	public static int BCDToInt(short BCD)
	{// BCD码转int,原为unsigned char小心有溢出问题
		int High, Low;
		High = ((BCD & 0xf0) >> 4) * 10;
		Low = BCD & 0x0f;
		return High + Low;
	}

	public static byte IntToBCD(int Int)
	{
		byte BCD;
		Int &= 0xff;
		BCD = (byte) (((Int / 10) << 4) + (Int % 10));
		return BCD;
	}

	public static byte WordToBCD(int Word)
	{
		byte BCD;
		Word &= 0xff;
		BCD = (byte) (((Word / 10) << 4) + (Word % 10));
		return BCD;
	}

	public static String TrainNumberToFunctionNumber(String trainnumber, int runningnumberstatus)
	{// 把车次号转为车次号功能号
        String runnumberstatus = "";
        if (runningnumberstatus < 10){
            runnumberstatus = "0" + String.valueOf(runningnumberstatus);
        }else{
            runnumberstatus = String.valueOf(runningnumberstatus);
        }

		String tempstring;
		byte tempchar, tempchar1;
		// int length;
		tempchar = (byte) trainnumber.charAt(0);
		tempchar1 = (byte) trainnumber.charAt(1);

		if (((tempchar >= 'A') && (tempchar <= 'Z')) || ((tempchar >= 'a') && (tempchar <= 'z')) || (tempchar == '0')) // 第1位是A~Z或a~z或0
		{
			if (((tempchar1 >= 'A') && (tempchar1 <= 'Z')) || ((tempchar1 >= 'a') && (tempchar1 <= 'z'))) // 第2位是A~Z或a~z
			{
				// length = trainnumber.length();
				tempstring = String.format("%d%d", tempchar, tempchar1);
				trainnumber = tempstring + trainnumber.substring(2);

                trainnumber = GlobalPara.strPreTfnNumber + trainnumber + runnumberstatus;
			}
			else if ((tempchar1 == '0') && (tempchar == '0')) // 第1位是和第2位都是0
			{
				// length = trainnumber.length();
				tempstring = String.format("%d%d", tempchar, tempchar1);
				trainnumber = tempstring + trainnumber.substring(2);

                trainnumber = GlobalPara.strPreTfnNumber + trainnumber + runnumberstatus;
			}
			else
			{// 第2位是其他数字(1~9)或者 第1位为字母时第2位为0
				// length = trainnumber.length();
				tempstring = String.format("%d", tempchar);
				trainnumber = tempstring + trainnumber.substring(1);

                trainnumber = GlobalPara.strPreTfnNumber + "00" + trainnumber + runnumberstatus;
			}
		}
		else
		{// 第1位为其他数字(1~9)
            trainnumber = GlobalPara.strPreTfnNumber + "0000" + trainnumber + runnumberstatus;
		}

		return trainnumber;
	}

	public static boolean MemCmp(byte[] arr1, byte[] arr2, int len)
	{//相等true 不等false
		if (arr1.length < len || arr2.length < len)
		{
			return false;
		}
		for (int i = 0; i < len; i++)
		{
			if (arr1[i] != arr2[i])
			{
				return false;
			}
		}
		return true;
	}

	public static boolean MemCmp(byte[] arr1, int offset1, byte[] arr2, int offset2, int len)
	{//相等true 不等false
		if (arr1 == null || arr2 == null || arr1.length < offset1 + len || arr2.length < offset2 + len)
		{
			return false;
		}
		for (int i = 0; i < len; i++)
		{
			if (arr1[offset1 + i] != arr2[offset2 + i])
			{
				return false;
			}
		}
		return true;
	}

	public static boolean MemCmp(byte[] arr1, String str, int len)
	{//相等true 不等false
		if (arr1.length < len || str.length() < len)
		{
			return false;
		}
		byte[] arr2 = str.getBytes();
		return MemCmp(arr1, arr2, len);
	}

	public static void MemCpy(byte[] dest, String src, int len)
	{
		for (int i = 0; i < len && i < src.length(); i++)
		{
			dest[i] = (byte) src.charAt(i);
		}
	}

	public static void MemCpy(byte[] dest, byte[] src, int len)
	{
		for (int i = 0; i < len && i < src.length && i < dest.length; i++)
		{
			dest[i] = src[i] ;
		}
	}

	public static void MemCpy(byte[] dest, int offset1, byte[] src, int offset2, int len)
	{
		if (dest == null || src == null)
		{
			Log.e(GlobalPara.Tky, "GlobalFunc MemCpy can't be null");
			return;
		}
		for (int j = 0; j < len && j < src.length; j++)
		{
			dest[offset1 + j] = src[offset2 + j] ;
		}
	}

	public static void MemCpy(byte[] dest, int offset1, String src, int offset2, int len)
	{
		for (int j = 0; j < len && j < src.length(); j++)
		{
			dest[offset1 + j] = (byte) src.charAt(offset2 + j);
		}
	}

	public static void ZeroMemory(byte[] src, int offset, int len)
	{
		for (int i = offset; i < offset + len; i++)
		{
			src[i] = 0;
		}
	}

	public static void MemSet(byte[] src, int offset, int len, byte val)
	{
		for (int i = offset; i < offset + len; i++)
		{
			src[i] = val;
		}
	}

	public static void ZeroMemory(byte[] src, int len)
	{
		ZeroMemory(src, 0, len);
	}

	public static void MemSet(byte[] src, int len, byte val)
	{
		MemSet(src, 0, len, val);
	}

	public static byte HIBYTE(int param)
	{
		byte ret = (byte) ((param >> 8) & 0xFF);
		return ret;
	}

	public static boolean Little(byte _val, byte _end)
	{
		int val = (int) (_val & 0xff);
		int end = (int) (_end & 0xff);
		if (val <= end)
		{
			return true;
		}
		return false;
	}

	public static boolean Large(byte _val, byte _begin)
	{
		int val = (int) (_val & 0xff);
		int begin = (int) (_begin & 0xff);
		if (val >= begin)
		{
			return true;
		}
		return false;
	}

	public static boolean Between(byte _val, byte _begin, byte _end)
	{
		if (_begin == _end)
		{
			if (_val == _begin)
			{
				return true;
			}
			else
				return false;
		}
		int val = (int) (_val & 0xff);
		int begin = (int) (_begin & 0xff);
		int end = (int) (_end & 0xff);
		int temp = begin;
		begin = begin <= end ? begin : end;
		end = temp <= end ? end : temp;
		if (val >= begin && val <= end)
		{
			return true;
		}
		return false;
	}

//	public static void SetCurrentTime(_RawInfo PackageInfo)
//	{
//		int iYear = 2000 + (((PackageInfo.Data[0] & 0xFF) >> 4) & 0x0f) * 10 + (PackageInfo.Data[0] & 0x0F) * 1;
//		int iMonth = (((PackageInfo.Data[1] & 0xFF) >> 4) & 0x0f) * 10 + (PackageInfo.Data[1] & 0x0F) * 1;
//		int iDay = (((PackageInfo.Data[2] & 0xFF) >> 4) & 0x0f) * 10 + (PackageInfo.Data[2] & 0x0F) * 1;
//		int iHour = (((PackageInfo.Data[3] & 0xFF) >> 4) & 0x0f) * 10 + (PackageInfo.Data[3] & 0x0F) * 1;
//		int iMin = (((PackageInfo.Data[4] & 0xFF) >> 4) & 0x0f) * 10 + (PackageInfo.Data[4] & 0x0F) * 1;
//		int iSecond = (((PackageInfo.Data[5] & 0xFF) >> 4) & 0x0f) * 10 + (PackageInfo.Data[5] & 0x0F) * 1;
//		SetCurrentTime(iYear, iMonth, iDay, iHour, iMin, iSecond);
//	}

//	public static boolean SetCurrentTime(int iYear, int iMonth, int iDay, int iHour, int iMin, int iSecond)
//	{
//		try
//		{
//			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd.HHmmss");
//			Calendar aCalendar = Calendar.getInstance();
//			aCalendar.set(iYear, iMonth - 1, iDay, iHour, iMin, iSecond);
//			Date dd = aCalendar.getTime();
//
//			GlobalPara.strSetTime = sDateFormat.format(dd);
//			LogInstance.debug(GlobalPara.Tky, "SetCurrentTime = "+GlobalPara.strSetTime);
//
//
//			GlobalActivity mAnGlobal = GlobalActivity.getInstance();
//			MainActivity mMainActivity = mAnGlobal.getMainActivity();
//
//			if(mMainActivity == null)
//			{
//				LogInstance.error(GlobalPara.Tky, "63." + tag + ": mMainControl is null");
//				return false;
//			}
//			SimpleDateFormat sDateFormat5 = new SimpleDateFormat("yyyyMMdd.HHmmss");
//			Calendar aCalendar5 = Calendar.getInstance();
//			aCalendar.set(iYear, iMonth - 1, iDay, iHour, iMin, iSecond);
//			Date dd5 = aCalendar.getTime();
//			String strNowTime = sDateFormat.format(dd5);
//			LogInstance.debug(GlobalPara.Tky, "set strNowTime = "+strNowTime);
//			try
//			{
//				Intent mIntent = new Intent( );
//		        ComponentName comp = new ComponentName("android.tieke.settime", "android.tieke.settime.MainActivity");
//		        mIntent.setComponent(comp);
//		        Bundle bundle = new Bundle();
//		        bundle.putString("nowtime",strNowTime);
//		        mIntent.putExtras(bundle);
//		        mIntent.setAction("android.intent.action.VIEW");
//		        mMainActivity.startActivity(mIntent);
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//				if (e.getMessage() != null && e.getMessage() != "")
//				{
//					LogInstance.exception(GlobalPara.Tky, e);
//				}
//				LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
//						+ Thread.currentThread().getStackTrace()[2].getLineNumber());
//			}
//
//			return true;
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			if (e.getMessage() != null && e.getMessage() != "")
//			{
//				LogInstance.exception(GlobalPara.Tky, e);
//			}
//			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName()
//					+ ": " + Thread.currentThread().getStackTrace()[2].getLineNumber());
//			return false;
//		}
//	}

	public static boolean SetCurrentTime_ForSystemUser(int iYear, int iMonth, int iDay, int iHour, int iMin, int iSecond)
	{
		try
		{
			Calendar canlendar = Calendar.getInstance();
			canlendar.set(iYear, iMonth - 1, iDay, iHour, iMin, iSecond);
			long curTime = canlendar.getTimeInMillis();
			SystemClock.setCurrentTimeMillis(curTime);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky, "0." + tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName()
					+ ": " + Thread.currentThread().getStackTrace()[2].getLineNumber());
			return false;
		}
	}

	public static String bytesToHexString(byte[] src, int len)
	{
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || len <= 0)
		{
			Log.v(GlobalPara.Tky, "bytesToHexString print error,size is not right");
			return null;
		}
		if (src[0] == 0x10 && src[1] == 0x02)
		{
			for (int i = 2; i < len - 2; i++)
			{
				int v = src[i] & 0xFF;
				String hv = Integer.toHexString(v);
				if (hv.length() < 2)
				{
					stringBuilder.append(0);
				}
				stringBuilder.append(hv);
				if (i == 8)
					stringBuilder.append("-");
				else
					stringBuilder.append(" ");
			}
		}
		else
		{
			for (int i = 0; i < len; i++)
			{
				int v = src[i] & 0xFF;
				String hv = Integer.toHexString(v);
				if (hv.length() < 2)
				{
					stringBuilder.append(0);
				}
				stringBuilder.append(hv);

				if (i == 6)
					stringBuilder.append("-");
				else
					stringBuilder.append(" ");
			}
		}
		String retString =stringBuilder.toString();
		stringBuilder = null;
		return retString;
	}

	public static String bytesToHexString2(byte[] src, int len)
	{
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || len <= 0)
		{
			Log.v(GlobalPara.Tky, "bytesToHexString2 print error,size is not right");
			return "";
		}

		for (int i = 0; i < len; i++)
		{
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2)
			{
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
			stringBuilder.append(" ");
		}
		String retString =stringBuilder.toString();
		stringBuilder = null;
		return retString;
	}

	public static String GetCurrentTime()
	{
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd.HHmmss");
		Calendar aCalendar = Calendar.getInstance();
		Date d = aCalendar.getTime();
		String date = sDateFormat.format(d);
		return date;
	}

	public static String ReadString(RandomAccessFile readfile) throws IOException
	{
		String read = "";
		byte[] retString = new byte[1024] ;
		int i = 0;
		byte[] buf = new byte[2] ;
		while (readfile.read(buf, 0, 1) == 1)
		{
			if (buf[0] == 0x0A && buf[1] == 0x0D)
			{
				read = new String(retString, 0, i, "GBK");
				return read;
			}
			if (buf[0] != 0x0D && buf[0] != 0x0A)
			{
				retString[i++] = buf[0] ;
			}
			buf[1] = buf[0] ;
		}
		return read;
	}

	public static String GetIPByName(String name)
	{
		InetAddress address = null;
		try
		{
			address = InetAddress.getByName(name);
			return address.getHostAddress().toString();
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
			return "";
		}
	}

	public static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	public static byte hexStringToBytes(String hexString)
	{
		if (hexString == null || hexString.equals("")) {
			return -1;
		}
		hexString = hexString.toUpperCase();
		char[] hexChars = hexString.toCharArray();
		byte ret = (byte) (charToByte(hexChars[0]) << 4 | charToByte(hexChars[1]));
		return ret;
	}


	public static boolean isRightIp(String ipAddress)
	{
		String ips[] = ipAddress.split("\\.");
		if(ips.length==4){
			for(String ip : ips){
				System.out.println(ip);
				if(Integer.parseInt(ip)<0 || Integer.parseInt(ip)>255){
					return false;
				}
			}
			return true;
		}else{
			return false;
		}
	}

	public static String getFnTranslateContext(String strFnIn)
	{
		String strFnOut = "";
		if(strFnIn.startsWith("086"))
			strFnIn = strFnIn.substring(3);

		if(strFnIn.startsWith("14689"))
			return strFnIn;
		else if(strFnIn.startsWith("3") ){
			if(strFnIn.endsWith("01")){
				strFnOut=strFnIn.substring(1,strFnIn.length()-2)+" 本务司机";
			}
			else if(strFnIn.endsWith("02")){
				strFnOut= strFnIn.substring(1,strFnIn.length()-2)+" 补机1";
			}
			else if(strFnIn.endsWith("03")){
				strFnOut= strFnIn.substring(1,strFnIn.length()-2)+" 补机2";
			}
			else if(strFnIn.endsWith("04")){
				strFnOut= strFnIn.substring(1,strFnIn.length()-2)+" 补机3";
			}
			else if(strFnIn.endsWith("05")){
				strFnOut= strFnIn.substring(1,strFnIn.length()-2)+" 补机4";
			}
			else{
				strFnOut="未知号码";
			}

		}
		else if(strFnIn.startsWith("2")  ){
			if(strFnIn.endsWith("01")){
				if(strFnIn.startsWith("20000") )
					strFnOut=strFnIn.substring(5,strFnIn.length()-2)+"车次 本务司机";
				else if(strFnIn.startsWith("200") ) {
					char c = (char) Integer.parseInt(strFnIn.substring(3,5));
					strFnOut = c+ strFnIn.substring(5, strFnIn.length() - 2) + "车次 本务司机";
				}
				else {
					char c1 = (char) Integer.parseInt(strFnIn.substring(1,3));
					char c2 = (char) Integer.parseInt(strFnIn.substring(3,5));
					strFnOut = c1+"" + c2 + strFnIn.substring(5, strFnIn.length() - 2) + "车次 本务司机";
				}
			}
			else if(strFnIn.endsWith("02")){
				if(strFnIn.startsWith("20000") )
					strFnOut=strFnIn.substring(5,strFnIn.length()-2)+"车次  补机1";
				else if(strFnIn.startsWith("200") ) {
					char c = (char) Integer.parseInt(strFnIn.substring(3,5));
					strFnOut = c+ strFnIn.substring(5, strFnIn.length() - 2) + "车次  补机1";
				}
				else {
					char c1 = (char) Integer.parseInt(strFnIn.substring(1,3));
					char c2 = (char) Integer.parseInt(strFnIn.substring(3,5));
					strFnOut = c1 + c2 + strFnIn.substring(5, strFnIn.length() - 2) + "车次  补机1";
				}
			}
			else if(strFnIn.endsWith("03")){
				if(strFnIn.startsWith("20000") )
					strFnOut=strFnIn.substring(5,strFnIn.length()-2)+"车次  补机2";
				else if(strFnIn.startsWith("200") ) {
					char c = (char) Integer.parseInt(strFnIn.substring(3,5));
					strFnOut = c+ strFnIn.substring(5, strFnIn.length() - 2) + "车次  补机2";
				}
				else {
					char c1 = (char) Integer.parseInt(strFnIn.substring(1,3));
					char c2 = (char) Integer.parseInt(strFnIn.substring(3,5));
					strFnOut = c1 + c2 + strFnIn.substring(5, strFnIn.length() - 2) + "车次  补机2";
				}
			}
			else if(strFnIn.endsWith("04")){
				if(strFnIn.startsWith("20000") )
					strFnOut=strFnIn.substring(5,strFnIn.length()-2)+"车次  补机3";
				else if(strFnIn.startsWith("200") ) {
					char c = (char) Integer.parseInt(strFnIn.substring(3,5));
					strFnOut = c+ strFnIn.substring(5, strFnIn.length() - 2) + "车次  补机3";
				}
				else {
					char c1 = (char) Integer.parseInt(strFnIn.substring(1,3));
					char c2 = (char) Integer.parseInt(strFnIn.substring(3,5));
					strFnOut = c1 + c2 + strFnIn.substring(5, strFnIn.length() - 2) + "车次  补机3";
				}
			}
			else if(strFnIn.endsWith("05")){
				if(strFnIn.startsWith("20000") )
					strFnOut=strFnIn.substring(5,strFnIn.length()-2)+"车次  补机4";
				else if(strFnIn.startsWith("200") ) {
					char c = (char) Integer.parseInt(strFnIn.substring(3,5));
					strFnOut = c+ strFnIn.substring(5, strFnIn.length() - 2) + "车次  补机4";
				}
				else {
					char c1 = (char) Integer.parseInt(strFnIn.substring(1,3));
					char c2 = (char) Integer.parseInt(strFnIn.substring(3,5));
					strFnOut = c1 + c2 + strFnIn.substring(5, strFnIn.length() - 2) + "车次  补机4";
				}
			}
			else{
				strFnOut="未知号码";
			}
		}
		else if(strFnIn.equals("1200"))
			strFnOut="列车调度台";
		else if(strFnIn.equals("1300"))
			strFnOut="列车值班台";
		else
			strFnOut="未知号码";
		return strFnOut;
	}

	public static String convertEFN(int efn){
		if(efn==225){
			return "韶四";
		}
		else if(efn==231){
			return "神八";
		}
		else if(efn==232){
			return "神十二";
		}
		else{
			return efn+"";
		}
	}

	public	static String translateFN(String number, int CallType)
	{
		if(number.isEmpty())
			return ("");
		int FF;
		String strTemp = "", strTemp1="";

		if(number.length() > 3)
			strTemp = number.substring(0,3);

		if(strTemp.equals("086"))
		{
			number = number.substring(3);
		}

		if(CallType != ServiceConstant.CALL_TYPE_SINGLE)
		{
			if(number.endsWith("220"))
			{
				return ("邻站组呼");
			}
			else if(number.endsWith("210"))
			{
				return ("站内组呼");
			}
			else if(number.endsWith("299"))
			{
				return ("紧急呼叫");
			}
			else if(number.endsWith("890"))
			{
				return ("临时组呼");
			}
			else if(number.endsWith("789"))
			{
				return ("重联组呼");
			}
			number += ("组呼");
			return number;
		}

		if(number.equals("1200") || number.equals("1210"))
		{
			return ("调度员");
		}
		else if(number.equals("1201") || number.equals("1211"))
		{
			return ("前方调度员");
		}
		else if(number.equals("1203"))
		{
			return ("后方调度员");
		}
		else if(number.equals("1300") || number.equals("1310"))
		{
			return ("车站值班员");
		}
		else if(number.equals("1301") || number.equals("1311"))
		{
			return ("前站值班员");
		}
		else if(number.equals("1302") || number.equals("1312"))
		{
			return ("后站值班员");
		}
		else if(number.equals("1303"))
		{
			return ("车站外勤助理值班员");
		}
		else if(number.equals("1400"))
		{
			return ("机车调度员");
		}
		else if(number.equals("1401"))
		{
			return ("机务段运转值班员");
		}
		else if(number.equals("1402"))
		{
			return ("机务折返段运转值班员");
		}
		else if(number.equals("1403"))
		{
			return ("列车段(车务段、客运段)值班员");
		}

		if(number.length() > 1)
			strTemp = number.substring(0,1);

		if(strTemp.equals("2"))//车次功能号
		{
			strTemp = strTemp1 = ("");
			char c , c1;
			if(!number.substring(1,3).equals("00")) {
				c = (char) Integer.parseInt((number.substring(1, 3)));
				strTemp = c+"";
			}
			if(!number.substring(3,5).equals("00")) {
				c1 = (char) Integer.parseInt((number.substring(3, 5)));
				strTemp1 = c1+"";
			}
			FF = Integer.parseInt(number.substring(number.length()-2));
			switch(FF)
			{
				case 1:
				case 81:
					if(number.length()-5>0)
						return strTemp+strTemp1+number.substring(5,number.length()-2)+("次本务机司机");
				case 2:
				case 82:
					if(number.length()-5>0)
						return strTemp+strTemp1+number.substring(5,number.length()-2)+("次补机1司机");
				case 3:
				case 83:
					if(number.length()-5>0)
						return strTemp+strTemp1+number.substring(5,number.length()-2)+("次补机2司机");
				case 4:
				case 84:
					if(number.length()-5>0)
						return strTemp+strTemp1+number.substring(5,number.length()-2)+("次补机3司机");
				case 5:
				case 85:
					if(number.length()-5>0)
						return strTemp+strTemp1+number.substring(5,number.length()-2)+("次补机4司机");
				case 86:
					if(number.length()-5>0)
						return strTemp+strTemp1+number.substring(5,number.length()-2)+("次运转车长1");
				case 87:
					if(number.length()-5>0)
						return strTemp+strTemp1+number.substring(5,number.length()-2)+("次运转车长2");
				case 10:
					if(number.length()-5>0)
						return strTemp+strTemp1+number.substring(5,number.length()-2)+("次列车长1");
				case 11:
					if(number.length()-5>0)
						return strTemp+strTemp1+number.substring(5,number.length()-2)+("次列车长2");
				case 31:
					if(number.length()-5>0)
						return strTemp+strTemp1+number.substring(5,number.length()-2)+("次乘警长1");
				case 32:
					if(number.length()-5>0)
						return strTemp+strTemp1+number.substring(5,number.length()-2)+("次乘警长2");
			}
		}
		else if(strTemp.equals("3"))//机车功能号
		{
			strTemp = number.substring(1,4);
			strTemp = convertEFN(Integer.parseInt(strTemp))+("-");
			if(number.substring(number.length()-2).equals("01"))
			{
				if(number.length()-4>0)
					return strTemp+number.substring(4,number.length()-2)+("本务机司机");
			}
			else if(number.substring(number.length()-2).equals("02"))
			{
				if(number.length()-4>0)
					return strTemp+number.substring(4,number.length()-2)+("补机1司机");
			}
			else if(number.substring(number.length()-2).equals("03"))
			{
				if(number.length()-4>0)
					return strTemp+number.substring(4,number.length()-2)+("补机2司机");
			}
			else if(number.substring(number.length()-2).equals("04"))
			{
				if(number.length()-4>0)
					return strTemp+number.substring(4,number.length()-2)+("补机3司机");
			}
			else if(number.substring(number.length()-2).equals("05"))
			{
				if(number.length()-4>0)
					return strTemp+number.substring(4,number.length()-2)+("补机4司机");
			}
		}
		strTemp = number.substring(0,2);
		if(strTemp == ("50"))//组呼叫
		{
			strTemp = number.substring(number.length()-3);
			if(strTemp.equals("210"))
				return ("站内组呼");
			else if(strTemp.equals("221")
					|| strTemp.equals("220"))
				return ("邻站组呼");
		}
		else if(strTemp.equals("91"))
		{
			strTemp = number.substring(number.length()-2);
			FF = Integer.parseInt(strTemp);
			if(strTemp.equals("00"))
				return ("通用调度员");
			else if(strTemp.equals("01"))
				return ("列车调度员");
			else if(strTemp.equals("02"))
				return ("列车助理调度员");
			else if(FF == 0x05)
				return ("车站(场)值班员");
			else if(FF >= 0x06 && FF <= 0x09)
				return ("车站(场)值班员");
			else if(strTemp.equals("10"))
			{
				if(number.length()-2>0)
				{
					return ("车站(场)调度值班员");
				}
			}
			else if(FF >= 11 && FF <= 19)
			{
				return ("车站(场)其他值班员");
			}
			else if(strTemp.equals("20"))
			{
				return ("客运调度员");
			}
			else if(strTemp.equals("21"))
			{
				return ("车站(场)1站台外勤助理值班员");
			}
			else if(strTemp.equals("22"))
			{
				return ("车站(场)2站台外勤助理值班员");
			}
			else if(strTemp.equals ("23"))
			{
				return ("车站(场)3站台外勤助理值班员");
			}
			else if(strTemp.equals ("24"))
			{
				return ("车站(场)4站台外勤助理值班员");
			}
			else if(strTemp.equals("25"))
			{
				return ("车站(场)5站台外勤助理值班员");
			}
			else if(strTemp.equals ("26"))
			{
				return ("车站(场)6站台外勤助理值班员");
			}
			else if(strTemp.equals("27"))
			{
				return ("车站(场)7站台外勤助理值班员");
			}
			else if(strTemp.equals ("28"))
			{
				return ("车站(场)8站台外勤助理值班员");
			}
			else if(strTemp.equals ("29"))
			{
				return ("车站(场)9站台外勤助理值班员");
			}
			else if(strTemp.equals ("30"))
			{
				return ("机车/动车调度员");
			}
			else if(strTemp.equals ("38"))
			{
				return ("动车司机调度员");
			}
			else if(strTemp.equals ("50"))
			{
				return ("牵引供电调度员");
			}
			else if(strTemp.equals ("80"))
			{
				return ("电务调度员");
			}
			else if(strTemp.equals ("99"))
			{
				return ("通信机房试验台");
			}
		}

		return number;
	}
}

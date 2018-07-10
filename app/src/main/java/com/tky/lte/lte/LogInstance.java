package com.tky.lte.lte;

import android.util.Log;

import com.tky.lte.util.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogInstance
{
	private static File logFile = null;
	private static PrintStream out_stream = null;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private static BlockingQueue<String> logQueue = new LinkedBlockingQueue<String>();
	private static Thread logThread = null;
	public static String tag = "LogInstance";
	private static String bakLogName = ".bak";
	private static TrainState mTrainState = TrainState.getInstance();

	public static String strRemoteIp = "";
	/** 记录日志线程 */
	public static void startLogFile()
	{
		if (logThread != null)
		{
			return;
		}

		logger = Logger.getLogger(GlobalPara.Tky);

		initLog();//初始化
		logThread = new Thread()
		{
			@Override
			public void run()
			{
				//int count = 0;//计数器//20150128
				while (LogParam.LOG_OUTFILE)
				{
					try
					{
						String logStr = logQueue.take();
						if(LogParam.LOG_OUTFILE)
						{//记录日志文件
							if(out_stream != null)
								out_stream.print(logStr);//写入
						}
					}
					catch (Exception e)
					{
						Log.e(GlobalPara.Tky, "Log record error!");
						if (e.getMessage() != null && e.getMessage() != "")
						{
							Log.e(GlobalPara.Tky, e.getMessage());
						}
						Log.e(GlobalPara.Tky, tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
								+ Thread.currentThread().getStackTrace()[2].getLineNumber());
						e.printStackTrace();
					}
				}
				logThread = null;// 结束日志输出置空
			}
		};
		logThread.start();
	}
	public static void throwable(String tag, Throwable e)
	{
		log(tag, ExceptionPrinter.getStackTraceOf(e), LogParam.EXCEPTION);
	}
	public static void exception(String tag, Exception e)
	{
		try
		{
			String strExceptionInfo = ExceptionPrinter.getStackTraceOf(e);
			if(strExceptionInfo != null && !strExceptionInfo.equals(""))
			{
				strExceptionInfo = strExceptionInfo.replaceAll("\n", ";");
				log(tag,"-1.exception: " + strExceptionInfo, LogParam.EXCEPTION);
			}
			else
			{
				log(tag, "-1.exception: context info is null", LogParam.EXCEPTION);
			}
		}
		catch (Exception e2)
		{
		}
	}

	public static void error(String tag, String message)
	{
		log(tag, message, LogParam.ERROR);
	}

	public static void debug(String tag, String message)
	{
		log(tag, message, LogParam.DEBUG);
	}

	private static void log(String tag, String message, int level)
	{
		if (LogParam.LOG_OUTFILE)
		{// 输出到文件
			try
			{
				logQueue.add(packLog(tag, message, level));//写文件
				logsb.delete(0, logsb.length());
			}
			catch (Exception e)
			{
				e.printStackTrace();
				if (e.getMessage() != null && e.getMessage() != "")
				{
					Log.e(GlobalPara.Tky, e.getMessage());
				}
				Log.e(GlobalPara.Tky, tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
						+ Thread.currentThread().getStackTrace()[2].getLineNumber());
			}
		}

		if (LogParam.LOG_LOGCAT)
		{// 输出到控制台
			logcat(tag, message, level);// 输出到logcat
		}
	}

	private static StringBuffer logsb = new StringBuffer();

	public static void FlushLog()
	{
		try
		{
			if (out_stream != null)
			{
				out_stream.flush();
			}
		}
		catch (Exception e)
		{
			Log.e(GlobalPara.Tky, "flush log error!");
			if (e.getMessage() != null && e.getMessage() != "")
			{
				Log.e(GlobalPara.Tky, e.getMessage());
			}
			Log.e(GlobalPara.Tky, tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
			e.printStackTrace();
		}
	}

	public static void CloseLog()
	{
		try
		{
			LogParam.LOG_OUTFILE = false;

			if (out_stream != null)
			{
				out_stream.close();
				out_stream = null;
			}

		}
		catch (Exception e)
		{
			Log.e(GlobalPara.Tky, "close log error!");
			if (e.getMessage() != null && e.getMessage() != "")
			{
				Log.e(GlobalPara.Tky, e.getMessage());
			}
			Log.e(GlobalPara.Tky, tag + ": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
			e.printStackTrace();
		}
	}

	public static String packLog(String tag, String message, int level)
	{
		logsb.append(sdf.format(new Date())).append(": ");
		//if (level == LogParam.DEBUG)
		//{
		//	logsb.append("DEBUG: ");
		//}
		//else if (level == LogParam.EXCEPTION)
		//{
		//	logsb.append("EXCEPTION: ");
		//}
		//else if (level == LogParam.ERROR)
		//{
		//	logsb.append("ERROR: ");
		//}
		logsb.append(tag).append(":");
		logsb.append(level).append(":");
		logsb.append(message).append("\r\n");
		return logsb.toString();
	}

	private static Logger logger;
	public static void logcat(String tag, String message, int level)
	{
		logger.log(Level.INFO, message);

//		if (LogParam.LOG_LOGCAT)
//		{// 输出到控制台
//			if (level == LogParam.DEBUG)
//			{
//				android.util.Log.d(tag, message);
//			}
//			else if (level == LogParam.EXCEPTION)
//			{
//				android.util.Log.w(tag, message);
//			}
//			else if (level == LogParam.ERROR)
//			{
//				android.util.Log.e(tag, message);
//			}
//		}

	}

	/**
	 * 初始化日志处理
	 */
	public static void initLog()
	{
		try
		{
			new File(LogParam.LOG_FILEPATH).mkdirs();
			logFile = new File(LogParam.LOG_FILEPATH + LogParam.LOG_NAME);
			if(!logFile.exists())
			{//日志文件不存在，则创建
				logFile.createNewFile();
			}
			try
			{
				long size = logFile.length()/ (1024 * 1024);//20150128

				if(size >= LogParam.LOG_MAX_SIZE_FOR_CIR)
				{//超出大小
					boolean flag = logFile.renameTo(new File(LogParam.LOG_FILEPATH + LogParam.LOG_NAME + bakLogName));//20150128
					if(flag)
					{//20150128
						Log.e(GlobalPara.Tky, "renameto logfile successful");
						//logFile.delete(); //不用删除,已改名删除必然失败
						logFile = null;
					}
					else
					{
						Log.e(GlobalPara.Tky, "renameto logfile failed");
					}
				}
			}
			catch (Exception e)
			{//20150128
				Log.e(GlobalPara.Tky,"initLog error1");
				e.printStackTrace();
			}
			if(logFile == null)
			{//文件被备份
				logFile = new File(LogParam.LOG_FILEPATH + LogParam.LOG_NAME);
				if(!logFile.exists())
				{//日志文件不存在，则创建
					logFile.createNewFile();
				}
			}
			out_stream = new PrintStream(new FileOutputStream(logFile, true), true);//追加写入，自动flush
		}
		catch (Exception e)
		{
			Log.e(GlobalPara.Tky,"initLog error2");
			e.printStackTrace();
		}
	}
}

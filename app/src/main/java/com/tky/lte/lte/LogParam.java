package com.tky.lte.lte;

public class LogParam
{
	public static final int ERROR = 1; // 错误级别
	public static final int EXCEPTION = 3; // 异常级别
	public static final int DEBUG = 6; // 调试级别

	public static boolean TEST_GROUPCALL = false; // 组呼打点
	public static boolean TEST_SINGLECALL = false; // 单呼打点
	public static boolean TEST_PTT = false; //
	public static boolean TEST_XINLING = false; //

	public static boolean LOG_LOGCAT = true; // 日志输出到控制台
	public static boolean LOG_OUTFILE = true; // 日志输出到文件
	public static int LOG_MAXSIZE_FOR_POC = 100; // POC日志最大容量，单位:m*(1024 * 1024)
	public static int LOG_MAX_SIZE_FOR_SYSTEM = 200; // 系统日志最大容量，单位:m*(1024 * 1024)
	public static int LOG_MAX_SIZE_FOR_CIR = 200; // CIR日志最大容量，单位:m*(1024 * 1024)//压缩后大约8M

	public static int LOG_MAX_NUM = 300; //150
	public static int LOG_DEL_NUM = 200; //75

	//public static String SHARED_PREFS_FILE = "localconfigdata";//本地存储的数据

	public static String LOG_FILEPATH = "/mnt/sdcard/LocomotiveMain/"; // 日志输出路径//"/LocomotiveMain/"
	public static String LOG_FILEPATH_UPDATE = "/mnt/sdcard/UpdateInfo/"; // 日志输出路径//"/UpdateInfo/"

	public static String LOG_NAME = "cir.log"; // 日志输出路径
	public static String LOG_NAME_POC = "pocdroid.log"; // 日志输出路径
	public static String LOG_SYS_NAME = "cir.system.test.log"; // 日志输出路径

	public static String PARAMETERFILE = LOG_FILEPATH + "para.bak"; // 参数记录
	public static String PARA_LOG_NET = LOG_FILEPATH + "para_log_net.bak"; // 参赛日志
	public static String PARAMETERFILE_UPDATE = LOG_FILEPATH_UPDATE + "para.bak"; // 参数记录
	public static String VERSIONFILE_UPDATE = LOG_FILEPATH_UPDATE + "version.txt"; // 版本记录
	public static String strExceptionLogPath = LOG_FILEPATH + "exception.log";
	public static String strAllCommandPath = LOG_FILEPATH + "AllCommand.txt";
	public static String strDispatchCommandPath = LOG_FILEPATH + "DispatchCommand/";
	public static String strRuningTokenPath = LOG_FILEPATH + "RuningToken/";
	public static String strShuntingOperationPath = LOG_FILEPATH + "ShuntingOperation/";
	public static String strAdvanceNoticePath = LOG_FILEPATH + "AdvanceNotice/";
	public static String strOtherCommandPath = LOG_FILEPATH + "OtherCommand/";
	public static String strLossPackagePath = LOG_FILEPATH + "LossPackage/";
	public static String strOtherRecordPath = LOG_FILEPATH + "OtherRecord/";

	public static int iLOG_MAXSIZE_FOR_POC_FLASH = 50;//poc日志flash存储大小
	public static int iLOG_MAX_SIZE_FOR_SYSTEM_FLASH = 100;//system日志flash存储大小
	public static int iLOG_MAX_SIZE_FOR_CIR_FLASH = 100;//cir日志flash存储大小

	public static int iLOG_MAXSIZE_FOR_POC_SDCARD = 100;//poc日志sdcard存储大小
	public static int iLOG_MAX_SIZE_FOR_SYSTEM_SDCARD = 200;//system日志sdcard存储大小
	public static int iLOG_MAX_SIZE_FOR_CIR_SDCARD = 200;//cir日志sdcard存储大小


}

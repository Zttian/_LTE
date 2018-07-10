package com.tky.lte.lte;

import android.os.Looper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AppExcepitonHandler implements UncaughtExceptionHandler
{
	public static String tag = "AppExcepitonHandler";
    private static AppExcepitonHandler mInstance;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

    private AppExcepitonHandler(){

    }
    
    public static AppExcepitonHandler getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new AppExcepitonHandler();
        }
        return mInstance;
    }

    public void SetUnExceptionHandler()
    {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(mInstance);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        ex.printStackTrace();
        if (!handleException(ex))
        {
            mDefaultHandler.uncaughtException(thread, ex);            
        }
        else
        {            
        }        
        LogInstance.error(GlobalPara.Tky, "1." + tag +": uncaughtException");
        LogInstance.FlushLog();
        
    }

    private boolean handleException(final Throwable ex)
    {
        if (ex == null)
        {
            return false;
        }
        new Thread()
        {
            @Override
            public void run()
            {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PrintStream st = new PrintStream(bos);
                ex.printStackTrace(st);
                postReport(new String(bos.toByteArray()));
                try
                {
                    bos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    if (e.getMessage() != null && e.getMessage() != "")
    				{
    					LogInstance.exception(GlobalPara.Tky, e);
    				}
    				LogInstance.error(GlobalPara.Tky,  "0." + tag +": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
    						+ Thread.currentThread().getStackTrace()[2].getLineNumber());
                }
            }

        }.start();
        return true;
    }
    

    private void postReport(String message)
    {
        Looper.prepare();
        File file = new File(LogParam.strExceptionLogPath );//"/LocomotiveMain/exception.log"
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write("\r\n\r\n".getBytes("GBK"));

            StringBuffer logsb = new StringBuffer();
            logsb.append(sdf.format(new Date())).append(": ");
            logsb.append(System.currentTimeMillis()).append(": ");
            logsb.append(message);

            fileOutputStream.write(logsb.toString().getBytes("GBK"));                                    
            fileOutputStream.flush();
            fileOutputStream.close();
                        
            LogInstance.error(GlobalPara.Tky, "1." + tag +": uncaughtException: "+logsb.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if (e.getMessage() != null && e.getMessage() != "")
			{
				LogInstance.exception(GlobalPara.Tky, e);
			}
			LogInstance.error(GlobalPara.Tky,   "0." + tag +": " + Thread.currentThread().getStackTrace()[2].getMethodName() + ": "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
        }
        Looper.loop();
        
        LogInstance.FlushLog();
    }

}

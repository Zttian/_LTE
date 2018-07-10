package com.tky.lte.lte;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ConfigHelper
{
	public static String tag = "ConfigHelper";
    private final static String TAG = ConfigHelper.class.getName();
    private static ConfigHelper instance;
    //private SharedPreferences mSettings;//
    //private SharedPreferences.Editor mSettingsEditor;//
    private boolean isTestMode;

    private ConfigHelper()
    {
        
    }
    
    public void setContext(Context _Context)
    {
    	final Context applicationContext = _Context;
        if (applicationContext != null)
        {
            //mSettings = applicationContext.getSharedPreferences(LogParam.SHARED_PREFS_FILE, Context.MODE_PRIVATE); //
            //mSettingsEditor = mSettings.edit();
        }
	}
    public static synchronized ConfigHelper getInstance()
    {
        if (instance == null)
        {
            instance = new ConfigHelper();
        }
        return instance;
    }

    public boolean isTestMode()
    {
        return isTestMode;
    }

    public void setTestMode(boolean isTestMode)
    {
        this.isTestMode = isTestMode;
    }


    private boolean putString(final String entry, String value, boolean commit)
    {
        //if (mSettingsEditor == null)//
        //{
        //	LogInstance.error(GlobalPara.Tky, "Settings are null");
        //    return false;
        //}
        // mSettingsEditor.putString(entry.toString(), value);
        //if (commit)
        //{
        //    return mSettingsEditor.commit();
        //}
        return true;
    }

    public boolean putString(final String entry, String value)
    {
        return putString(entry, value, true);
    }

    private boolean putInt(final String entry, int value, boolean commit)
    {
        //if (mSettingsEditor == null)//
    	//{
    	//    LogInstance.error(GlobalPara.Tky, "Settings are null");
    	//    return false;
    	//}
    	//mSettingsEditor.putInt(entry.toString(), value);
    	//if (commit)
    	//{
    	//    return mSettingsEditor.commit();
    	//}
        return true;
    }

    public boolean putInt(final String entry, int value)
    {
        return putInt(entry, value, true);
    }

    private boolean putFloat(final String entry, float value, boolean commit)
    {
    	//if (mSettingsEditor == null)//
    	//{
    	//	LogInstance.error(GlobalPara.Tky, "Settings are null");
    	//	  return false;
    	//}
    	//mSettingsEditor.putFloat(entry.toString(), value);
    	//if (commit)
    	//{
    	//    return mSettingsEditor.commit();
    	//}
        return true;
    }

    public boolean putFloat(final String entry, float value)
    {
        return putFloat(entry, value, true);
    }

    private boolean putBoolean(final String entry, boolean value, boolean commit)
    {
    	//if (mSettingsEditor == null)//
    	//{
    	//	LogInstance.error(GlobalPara.Tky, "Settings are null");
    	//    return false;
    	//}
    	//mSettingsEditor.putBoolean(entry.toString(), value);
    	//if (commit)
    	//{
    	//    return mSettingsEditor.commit();
    	//}
        return true;
    }

    public boolean putBoolean(final String entry, boolean value)
    {
        return putBoolean(entry, value, true);
    }

    public String getString(final String entry, String defaultValue)
    {
    	//if (mSettingsEditor == null)//
    	//{
    	//	LogInstance.error(GlobalPara.Tky, "Settings are null");
    	//    return defaultValue;
    	//}
    	// try
    	//{
    	//    return mSettings.getString(entry.toString(), defaultValue);
    	//}
    	//catch (Exception e)
    	// {
    	//	LogInstance.exception(GlobalPara.Tky, e);
    	//     return defaultValue;
    	//}
    	return defaultValue;
    }

    public int getInt(final String entry, int defaultValue)
    {
		//if (mSettingsEditor == null)//
		//{
		//	LogInstance.error(GlobalPara.Tky, "Settings are null");
		//    return defaultValue;
		//}
		//try
		//{
		//    return mSettings.getInt(entry.toString(), defaultValue);
		//}
		//catch (Exception e)
		//{
		//	LogInstance.exception(GlobalPara.Tky, e);
		//    return defaultValue;
		//}
    	return defaultValue;
    }

    public float getFloat(final String entry, float defaultValue)
    {
		//if (mSettingsEditor == null)//
		//{
		//	LogInstance.error(GlobalPara.Tky, "Settings are null");
		//    return defaultValue;
		//}
		//try
		//{
		//    return mSettings.getFloat(entry.toString(), defaultValue);
		//}
		//catch (Exception e)
		//{
		//	LogInstance.exception(GlobalPara.Tky, e);
		//    return defaultValue;
		//}
    	return defaultValue;
    }

    public boolean getBoolean(final String entry, boolean defaultValue)
    {
		//if (mSettingsEditor == null)//
		//{
		//	LogInstance.error(GlobalPara.Tky, "Settings are null");
		//    return defaultValue;
		//}
		//try
		//{
		//    return mSettings.getBoolean(entry.toString(), defaultValue);
		//}
		//catch (Exception e)
		//{
		//	LogInstance.exception(GlobalPara.Tky, e);
		//    return defaultValue;
		//}
    	return defaultValue;
    }

    private boolean commit()
    {
		//if (mSettingsEditor == null)//
		//{
		//	LogInstance.error(GlobalPara.Tky, "Settings are null");
		//    return false;
		//}
		//return mSettingsEditor.commit();
    	return true;
    }
    
    public static Object obj = new Object();
    public void SaveToBakfile()
    {
    	synchronized (obj) 
    	{					
	    	try 
	    	{	
	    		File aFile = new File(LogParam.PARAMETERFILE);
				FileWriter fw=new FileWriter(aFile, false);
			    BufferedWriter bw=new BufferedWriter(fw);
			    
			    Iterator iter = GlobalPara.htParameters.entrySet().iterator();
				while (iter.hasNext())
				{
					Map.Entry entry = (Map.Entry) iter.next();
					String strKey = (String) entry.getKey();
					String strValue = (String) entry.getValue();
					String strWrite = strKey+":"+strValue;
					bw.write(strWrite);
					bw.newLine();
				}
				
				bw.flush();
				bw.close();
				fw.close();
				bw = null;
				fw = null;
				aFile = null;
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
    	LogInstance.debug(GlobalPara.Tky, "configHelper save to bakfile successful");
	}
    public void ClearhtParameters() 
    {
		GlobalPara.htParameters.clear();
	}
    public void GethtParameters() 
    {
    	try 
    	{		
	    	ClearhtParameters() ;
	    	File aFile = new File(LogParam.PARAMETERFILE);
			if (aFile.exists())
			{
				FileReader fr=new FileReader(aFile);
				BufferedReader br=new BufferedReader(fr);
				String s=br.readLine();
	            while(null!=s && !s.equals(""))  
	            {
	            	byte[] linenametemp = s.getBytes("GBK");
	    			s = new String(linenametemp, 0, linenametemp.length, "GBK");
	    			
	            	String arr[] = s.split(":");
	            	if(arr.length == 2)
	            	{
	            		String key = arr[0].trim();
	            		String value = arr[1].trim();
	            		if (!GlobalPara.htParameters.containsKey(key))
						{
	            			GlobalPara.htParameters.put(key, value);
						}
	            	}
	                s=br.readLine();  
	            }  	  
	            br.close(); 
	            fr.close();
	            br = null;
	            fr = null;
	            aFile = null;
			}
			
			//print
			Iterator iter = GlobalPara.htParameters.entrySet().iterator();
			while (iter.hasNext())
			{
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				//Log.v(GlobalPara.Tky, key+":"+value);
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
    
    public void SaveToLogNetFile()
    {
    	try 
    	{
    		File aFile = new File(LogParam.PARA_LOG_NET);
			FileWriter fw=new FileWriter(aFile, false);
		    BufferedWriter bw=new BufferedWriter(fw);
		    
		    Iterator iter = GlobalPara.htParameters_Log_Net.entrySet().iterator();
			while (iter.hasNext())
			{
				Map.Entry entry = (Map.Entry) iter.next();
				String strKey = (String) entry.getKey();
				String strValue = (String) entry.getValue();
				String strWrite = strKey+":"+strValue;
				bw.write(strWrite);
				bw.newLine();
			}
			
			bw.flush();
			bw.close();
			fw.close();
			bw = null;
			fw = null;
			aFile = null;
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
    		if(LogParam.PARA_LOG_NET.contains("sdcard"))
    		{
    			
    			File _File = new File("/LocomotiveMain/");
    			if(!_File.exists())
    			{
    				_File.mkdir();
    				LogInstance.debug(GlobalPara.Tky, "create new directory: "+_File.getAbsolutePath());
    			}
    			_File = null;
    			
    			File aFile = new File("/LocomotiveMain/para_log_net.bak");
    			FileWriter fw=new FileWriter(aFile, false);
    		    BufferedWriter bw=new BufferedWriter(fw);
    		    
    		    Iterator iter = GlobalPara.htParameters_Log_Net.entrySet().iterator();
    			while (iter.hasNext())
    			{
    				Map.Entry entry = (Map.Entry) iter.next();
    				String strKey = (String) entry.getKey();
    				String strValue = (String) entry.getValue();
    				String strWrite = strKey+":"+strValue;
    				bw.write(strWrite);
    				bw.newLine();
    			}
    			
    			bw.flush();
    			bw.close();
    			fw.close();
    			bw = null;
    			fw = null;
    			aFile = null;
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
    public void ClearhtLogNetParameters() 
    {
		GlobalPara.htParameters_Log_Net.clear();
	}
    public void GethtLogNetParameters() 
    {
    	try 
    	{		
	    	ClearhtLogNetParameters() ;
	    	File aFile = new File(LogParam.PARA_LOG_NET);
			if (aFile.exists())
			{
				FileReader fr=new FileReader(aFile);
				BufferedReader br=new BufferedReader(fr);
				String s=br.readLine();
	            while(null!=s && !s.equals(""))  
	            {
	            	byte[] linenametemp = s.getBytes("GBK");
	    			s = new String(linenametemp, 0, linenametemp.length, "GBK");
	    			
	            	String arr[] = s.split(":");
	            	if(arr.length == 2)
	            	{
	            		String key = arr[0].trim();
	            		String value = arr[1].trim();
	            		if (!GlobalPara.htParameters_Log_Net.containsKey(key))
						{
	            			GlobalPara.htParameters_Log_Net.put(key, value);
						}
	            	}
	                s=br.readLine();  
	            }  	  
	            br.close(); 
	            fr.close();
	            br = null;
	            fr = null;
	            aFile = null;
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
    public void SetParameterForLogNet(String _name, String _value)
    {
    	//LogInstance.error(GlobalPara.Tky, "save parameter for login net: EngineNumber or ABJie");
    	ArrayList<NameValueInstance> lstInstances = new ArrayList<NameValueInstance>();
    	NameValueInstance aNameValueInstance = new NameValueInstance(_name, _value);
    	lstInstances.add(aNameValueInstance);
    	SetParameterForLogNet(lstInstances);
	} 
    public void SetParameterForLogNet(ArrayList<NameValueInstance> lstInstances)
    {
    	if(lstInstances == null)
    		return;
    	for (NameValueInstance nameValueInstance : lstInstances)
    	{
    		String _name = nameValueInstance.Name;
    		String _value = nameValueInstance.Value;
    		GlobalPara.htParameters_Log_Net.put(_name, _value);
		}
    	SaveToLogNetFile();
    	LogInstance.debug(GlobalPara.Tky, "save parameter for login net: EngineNumber or ABJie");
	} 
}

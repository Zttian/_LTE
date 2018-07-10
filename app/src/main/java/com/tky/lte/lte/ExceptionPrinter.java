package com.tky.lte.lte;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ExceptionPrinter
{
	public static String getStackTraceOf(Exception e)
	{ 
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(err));
		return err.toString();
	}

	public static String getStackTraceOf(Throwable t)
	{ 
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		t.printStackTrace(new PrintStream(err));
		return err.toString();
	}
}

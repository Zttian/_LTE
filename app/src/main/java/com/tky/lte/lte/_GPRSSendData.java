package com.tky.lte.lte;

public class _GPRSSendData
{
	public byte[] buffertx;
	public int txdatalength;
	public String DestinationIPAddress;
	public String DestinationPort;
	public long DelayTime;

	public _GPRSSendData()
	{
		buffertx = new byte[1024] ;
		txdatalength = 0;
		DelayTime = 0;
	}
}

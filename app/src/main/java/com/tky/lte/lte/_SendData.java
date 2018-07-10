package com.tky.lte.lte;

public class _SendData
{
	public byte[] Info = null;
	public int Length;
	public byte ACK_Flag;

	public _SendData()
	{
		Length = 0;
		ACK_Flag = 0;
		Info = new byte[1024] ;
	}
}

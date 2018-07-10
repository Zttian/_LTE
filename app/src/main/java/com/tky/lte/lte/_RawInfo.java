package com.tky.lte.lte;

public class _RawInfo
{
	public int InfoLenth;
	public byte SourCode;
	public byte SourAddreLenth;
	public byte[] SourAddre = null;
	public byte DectCode;
	public byte DectAddreLenth;
	public byte[] DectAddre = null;
	public byte ServiceType;
	public byte Command;
	public byte[] Data = null;

	public _RawInfo()
	{
		InfoLenth = 0;
		SourCode = 0;
		SourAddreLenth = 0;
		SourAddre = new byte[6] ;
		DectCode = 0;
		DectAddreLenth = 0;
		DectAddre = new byte[6] ;
		ServiceType = 0;
		Command = 0;
		Data = new byte[1024] ;
	}

	public void Clear()
	{
		SourAddre = null;
		DectAddre = null;
		Data = null;
	}
}

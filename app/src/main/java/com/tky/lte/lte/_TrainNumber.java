package com.tky.lte.lte;

public class _TrainNumber
{
	public byte[] Number = null;
	public byte Manual_Flag;
	public byte Status;

	public _TrainNumber()
	{
		Status = 0;
		Manual_Flag = 0;
		Number = new byte[10] ;
		Number[0] = 'X';//20150604
		Number[1] = 'X';
		Number[2] = 'X';
		Number[3] = 'X';
		Number[4] = 'X';
		Number[5] = 'X';
		Number[6] = 'X';
	}
}

package com.ddlab.webservice.core;

public class TestNumbers 
{
	public static int getSumOfNumbers( String noStr )
	{
		int totalNo = 0;
		for( int i = 0 ; i < noStr.length() ; i++ )
			totalNo = totalNo + Integer.parseInt( Character.toString(noStr.charAt(i)) );
		if( totalNo >= 10 )
			totalNo = getSumOfNumbers(String.valueOf(totalNo));
		return totalNo;
	}

//	public static int getSumOfNumbers( int no )
//	{
//		String noStr = String.valueOf(no);
//		int totalNo = 0;
//		for( int i = 0 ; i < noStr.length() ; i++ )
//		{
//			totalNo = totalNo + Integer.parseInt( Character.toString(noStr.charAt(i)) );
//		}
//		if( totalNo >= 10 )
//			totalNo = getSumOfNumbers(totalNo);
//		return totalNo;
//	}

	public static void main(String[] args) 
	{
		int date = 27;
		int month = 07;
		int year = 1975;

		String dateStr = String.valueOf(date)+String.valueOf(month)+String.valueOf(year);
		System.out.println(dateStr);
		System.out.println(getSumOfNumbers(dateStr));

	}

}

package com.ddlab.webservice.server;

public class AllPrimitive 
{
	public int[] getIntData( int[] intDatas )
	{
		System.out.println("*************** Data received at Server side *****************");
		for( int i = 0 ; i < intDatas.length ; i++ )
			System.out.println(intDatas[i]);
		System.out.println("*************** Data received at Server side *****************");
		return intDatas;
	}
	
	public int[] getFloatData( int[] floatDatas )
	{
		System.out.println("*************** Data received at Server side *****************");
		for( int i = 0 ; i < floatDatas.length ; i++ )
			System.out.println(floatDatas[i]);
		System.out.println("*************** Data received at Server side *****************");
		return floatDatas;
	}
	
	public int[] getDoubleData( int[] doubleDatas )
	{
		System.out.println("*************** Data received at Server side *****************");
		for( int i = 0 ; i < doubleDatas.length ; i++ )
			System.out.println(doubleDatas[i]);
		System.out.println("*************** Data received at Server side *****************");
		return doubleDatas;
	}
	
	public int[] getStringData( int[] stringDatas )
	{
		System.out.println("*************** Data received at Server side *****************");
		for( int i = 0 ; i < stringDatas.length ; i++ )
			System.out.println(stringDatas[i]);
		System.out.println("*************** Data received at Server side *****************");
		return stringDatas;
	}
	
}

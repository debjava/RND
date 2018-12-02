package com.webservice.generic.test;

public class CalculatorGeneric 
{
	public int getAddedInt( int a , int b )
	{
		return a+b;
	}
	
	public float getAddedFlaot( float a , float b )
	{
		return a+b;
	}
	
	public long getAddedLong( long a , long b )
	{
		return a+b;
	}
	
	public double getAddedLong( double a , double b )
	{
		return a+b;
	}
	
	public int getArrayAddInt( int[] a )
	{
		int result = 0;
		for( int val : a )
		{
			result += val;
		}
		return result;
	}
	
	public String getAddedString( String a , String b )
	{
		return a+b;
	}
	
	public String getAddedStringArr( String[] str )
	{
		String value = null;
		for( String s : str )
			value += s;
		return value;
	}

}

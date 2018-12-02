package com.ddlab.webservice.core;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateValidator 
{

	public static void main(String[] args) 
	{
//		String dt = "04/31/2005";  //Invalid Date
		String dt = "13/28/2005";  //Invalid Date
        //String dt = "02/29/2004";  //Valid Date
        try
        {
                DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
                df.setLenient(false);  // this is important!
                Date dt2 = df.parse(dt);
                System.out.println("Date is ok = " + dt2);
        }
        catch (ParseException e)
        {
                System.out.println("Invalid date " + dt);
        }
        catch (IllegalArgumentException e)
        {
                System.out.println("Invalid date " + dt);
        }

	}

}

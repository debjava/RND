package com.ideal.interfacelayer;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Reader
{
	public static String getFileContetns( String filePath )
	{
		String contents = null;
		try
		{
			File file = new File( filePath );
			FileReader fr = new FileReader( file );
			char[] charBuff = new char[(int)file.length()];
			fr.read( charBuff );
			contents = new String(charBuff);
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return contents;
	}
	
	private static String getDate()
	{
		String dateString = null;
		Date sysDate = new Date( System.currentTimeMillis() );
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
		dateString = sdf.format(sysDate);
		return dateString;
	}

	public static void main(String[] args) throws Exception
	{
//		String filePath = "C:/dev/RND/mdbdmproj/xml/Reply3.xml";
//		String contents = getFileContetns(filePath);
//		System.out.println(contents);
//		System.out.println(getDate());
		
		
		
		String dtStr = "20080909";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date dt = format.parse(dtStr);
		long time = dt.getTime() ;
		
		java.sql.Timestamp timestamp = new java.sql.Timestamp( time );
		
		
		
		
		
		java.sql.Date sqlDate = new java.sql.Date( time );
		System.out.println(sqlDate);
		
		
		
		
	}

}

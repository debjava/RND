package com.ideal.interfacelayer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class Test 
{
	private static Map successMap = null;
	private static List childList = null;
	private static Map errorMap = null;
	
	public static Connection getConnection()
	{
		Connection conn = null;
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
//			conn = DriverManager.getConnection("jdbc:oracle:thin:@10.70.24.9:1521:DNBTST", "DNBDM30","DM03DNB");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@10.70.24.9:1521:DNBDM", "FIOPEDV","FIOPEDV");
		}
		catch( SQLException se )
		{
			se.printStackTrace();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return conn;
	}
	
	private static Map getTableColumnMap( Connection conn , String tableName )
	{
		Map tableColMap = new LinkedHashMap();
		try
		{
			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet rs = dbmd.getColumns(null, null, tableName , null);
			while( rs.next() )
			{
				int cLength = rs.getInt ( "COLUMN_SIZE" ) ; 
				tableColMap.put(rs.getString("COLUMN_NAME"), rs.getString("TYPE_NAME"));
			}
		}
		catch( SQLException se )
		{
			System.out.println(" SQLException at getTableColumnMap - "+se);
			System.out.println(" SQLException at getTableColumnMap - "+se.getMessage());
			se.printStackTrace();
		}
		catch( Exception e )
		{
			System.out.println("Exception at getTableColumnMap - "+e);
			System.out.println("Exception at getTableColumnMap - "+e.getMessage());
			e.printStackTrace();
		}
		return tableColMap;
	}
	
//	/**
//	 * Method used to get the system date in
//	 * defined format.
//	 * @return the date in a particular format
//	 */
//	private static String getDate()
//	{
//		String dateString = null;
//		Date sysDate = new Date( System.currentTimeMillis() );
//		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
//		dateString = sdf.format(sysDate);
//		return dateString;
//	}


	public static void parseXml(String dtdName, String messageXML) 
	{
		Document msgDocument;
		try 
		{
			msgDocument = DocumentHelper.parseText(messageXML);
			Element rootEle = msgDocument.getRootElement();
			Element msgEle = rootEle.element("REPLY_ACCBAL");
			String status = msgEle.element("TXNFLAG").getText();
			System.out.println(status);
			if( status.equalsIgnoreCase("s"))
			{
				/*
				 * Parse the XML file for success tags and store into success table
				 */
				populateSuccessMap(rootEle);
				populateSuccessDbTable();
				populateSuccessChildDBTable();
			}
			else
			{
				/*
				 * Parse the XML file for error tags and store into error table
				 */
				populateErrorMap(rootEle);
				populateErrorMapInDB();
			}
		}
		catch( Exception e )
		{
			System.out.println("Exception at parseXml - "+e);
			System.out.println("Exception at parseXml - "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void populateErrorMap( Element rootEle )
	{
		errorMap = new LinkedHashMap();
		try
		{
			Iterator iterator = rootEle.element("REPLY_ACCBAL").elements().iterator();
			while (iterator.hasNext()) 
			{
				Element ele = (Element) iterator.next();
				String name = ele.getName();
				String value = ele.getText();
				if( name.equalsIgnoreCase("ERROR"))
				{
					Iterator it = ele.elements().iterator();
					System.out.println("For sub element");
					while (it.hasNext()) 
					{
						Element subEle = (Element) it.next();
						System.out.println(subEle.getName()+"========"+subEle.getText());
						errorMap.put(subEle.getName(), subEle.getText());
					}	
				}
				else
				{
					errorMap.put(name, value);
					System.out.println(name+"--------"+value);
				}
			}	
			
		}
		catch( Exception e )
		{
			System.out.println("Exception at populateErrorMap - "+e);
			System.out.println("Exception at populateErrorMap - "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void populateSuccessDbTable()
	{
		populateSuccessMapInDB();
	}
	
	private static void populateSuccessChildDBTable()
	{
		Connection conn = getConnection();//It has to be changed as new OPEACCBALENQ().getDBConnection();
		PreparedStatement pstmt = null;
		Map tableColMap = getTableColumnMap(conn,"DMOPE_REPLY_BALANCE_INTLIQ_S");
		String dynamicQuery = getDynamicQuery(tableColMap, "DMOPE_REPLY_BALANCE_INTLIQ_S");
		System.out.println( "Dynamic for child Table===="+dynamicQuery );
		try
		{
			pstmt = conn.prepareStatement(dynamicQuery);
			for( int i = 0 ; i < childList.size() ; i++ )
			{
				Map childMap = (Map)childList.get(i);
				Iterator itr = tableColMap.entrySet().iterator();
				int c = 1;
				while( itr.hasNext() )
				{
					Map.Entry me = ( Map.Entry )itr.next();
					String key = (String)me.getKey();
//					System.out.println("Key-------"+key);
					String value = (String)me.getValue();
//					System.out.println("Value-----"+value);
					String dataVal = (String)childMap.get(key);
//					System.out.println("DataVal-----"+dataVal);
					if(key.equalsIgnoreCase("REPLY_DATE"))
					{
//						System.out.println("It is a case reply date");
						pstmt.setTimestamp(c, new Timestamp( System.currentTimeMillis()));
//						System.out.println("Count----->>>"+c);
					}
					if( value.equalsIgnoreCase("VARCHAR2") )
					{
//						System.out.println(dataVal);
						pstmt.setString(c, dataVal);
//						System.out.println("VARCHAR2");
//						System.out.println("Count----->>>"+c);
					}
					if( value.equalsIgnoreCase("NUMBER") && dataVal != null )
					{
//						System.out.println("inside dataval-----"+Double.parseDouble(dataVal));
						pstmt.setDouble(c, Double.parseDouble(dataVal));
//						System.out.println("NUMBER");
//						System.out.println("Count----->>>"+c);
					}
					if( value.equalsIgnoreCase("DATE"))
					{
						System.out.println(dataVal+"@@@@@@@@@");
						pstmt.setDate(c, getSqlDate(dataVal) );
					}
					if( value.equalsIgnoreCase("TimeStamp"))
					{
						pstmt.setTimestamp(c, new java.sql.Timestamp(System.currentTimeMillis()));
					}
					c++;
				}	
				pstmt.execute();
				conn.commit();

			}
		}
		catch( SQLException se )
		{
			System.out.println("SQLException at populateSuccessChildDBTable - "+se);
			System.out.println("SQLException at populateSuccessChildDBTable - "+se.getMessage());
			se.printStackTrace();
		}
		catch( Exception e )
		{
			System.out.println("Exception at populateSuccessChildDBTable - "+e);
			System.out.println("Exception at populateSuccessChildDBTable - "+e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			try
			{
				pstmt.close();
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	}
	
	private static void populateErrorMapInDB()
	{
		Connection conn = getConnection();//It has to be changed as new OPEACCBALENQ().getDBConnection();
		PreparedStatement pstmt = null;
		Map tableColMap = getTableColumnMap(conn,"DMOPE_REPLY_BALANCE_F");
		String dynamicQuery = getDynamicQuery(tableColMap, "DMOPE_REPLY_BALANCE_F");
		System.out.println( "Dynamic===="+dynamicQuery );
		try
		{
			pstmt = conn.prepareStatement(dynamicQuery);
			Iterator itr = tableColMap.entrySet().iterator();
			int c = 1;
			while( itr.hasNext() )
			{
				Map.Entry me = ( Map.Entry )itr.next();
				String key = (String)me.getKey();
				String value = (String)me.getValue();
//				System.out.println("-----"+value+"------");
				String dataVal = (String)errorMap.get(key);
//				System.out.println(key+"-------"+dataVal);
				if(key.equalsIgnoreCase("REPLY_DATE"))
				{
//					System.out.println("It is a case reply date");
					pstmt.setTimestamp(c, new Timestamp( System.currentTimeMillis()));
//					System.out.println("Count----->>>"+c);
				}
				if( value.equalsIgnoreCase("VARCHAR2") )
				{
					pstmt.setString(c, dataVal);
//					System.out.println("VARCHAR2");
//					System.out.println("Count----->>>"+c);
				}
				if( value.equalsIgnoreCase("NUMBER") && dataVal != null )
				{
					pstmt.setDouble(c, Double.parseDouble(dataVal));
//					System.out.println("NUMBER");
//					System.out.println("Count----->>>"+c);
				}
				if( value.equalsIgnoreCase("DATE"))
				{
					pstmt.setDate(c, getSqlDate(dataVal) );
				}
				c++;
//				System.out.println("Total count "+c);
			}
			pstmt.execute();
			conn.commit();
		}
		catch( SQLException se )
		{
			System.out.println("SQLException at populateErrorMapInDB - "+se);
			System.out.println("SQLException at populateErrorMapInDB - "+se.getMessage());
			se.printStackTrace();
		}
		catch( Exception e )
		{
			System.out.println("Exception at populateErrorMapInDB - "+e);
			System.out.println("Exception at populateErrorMapInDB - "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void populateSuccessMapInDB()
	{
		Connection conn = getConnection();//It has to be changed as new OPEACCBALENQ().getDBConnection();
		PreparedStatement pstmt = null;
		Map tableColMap = getTableColumnMap(conn,"DMOPE_REPLY_BALANCE_S");
		String dynamicQuery = getDynamicQuery(tableColMap, "DMOPE_REPLY_BALANCE_S");
		System.out.println( "Dynamic===="+dynamicQuery );
		try
		{
			pstmt = conn.prepareStatement(dynamicQuery);
			Iterator itr = tableColMap.entrySet().iterator();
			int c = 1;
			while( itr.hasNext() )
			{
				Map.Entry me = ( Map.Entry )itr.next();
				String key = (String)me.getKey();
				String value = (String)me.getValue();
//				System.out.println("-----"+value+"------");
				String dataVal = (String)successMap.get(key);
//				System.out.println(key+"-------"+dataVal);
				if(key.equalsIgnoreCase("REPLY_DATE"))
				{
//					System.out.println("It is a case reply date");
					pstmt.setTimestamp(c, new Timestamp( System.currentTimeMillis()));
//					System.out.println("Count----->>>"+c);
				}
				if( value.equalsIgnoreCase("VARCHAR2") )
				{
					pstmt.setString(c, dataVal);
//					System.out.println("VARCHAR2");
//					System.out.println("Count----->>>"+c);
				}
				if( value.equalsIgnoreCase("NUMBER") && dataVal != null )
				{
					pstmt.setDouble(c, Double.parseDouble(dataVal));
//					System.out.println("NUMBER");
//					System.out.println("Count----->>>"+c);
				}
				if( value.equalsIgnoreCase("DATE"))
				{
					
					pstmt.setDate(c, getSqlDate(dataVal) );
				}
				c++;
//				System.out.println("Total count "+c);
			}
			pstmt.execute();
			conn.commit();
		}
		catch( SQLException se )
		{
			System.out.println("SQLException at populateSuccessMapInDB - "+se);
			System.out.println("SQLException at populateSuccessMapInDB - "+se.getMessage());
			se.printStackTrace();
		}
		catch( Exception e )
		{
			System.out.println("Exception at populateSuccessMapInDB - "+e);
			System.out.println("Exception at populateSuccessMapInDB - "+e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			try
			{
				pstmt.close();
			}
			catch( SQLException se )
			{
				se.printStackTrace();
			}
		}
	}
	
	private static java.sql.Date getSqlDate( String dtStr )
	{
		java.sql.Date sqlDate = null;
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date dt = format.parse(dtStr);
			long time = dt.getTime() ;
			sqlDate = new java.sql.Date( time );
		}
		catch( NullPointerException npe )
		{
			System.out.println("Caught NullPointerException and handling properly");
			sqlDate = null;
		}
		catch( Exception e )
		{
			System.out.println("Error in converting the SQL Date");
			System.out.println(e);
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return sqlDate;
	}
	private static String getDynamicQuery( Map dataMap , String tableName )
	{
		String query = "insert into "+tableName+"(";
		String valueStr = " values(";
		Iterator itr = dataMap.entrySet().iterator();
		while( itr.hasNext() )
		{
			Map.Entry me = (Map.Entry)itr.next();
			String key = (String)me.getKey();
			query += key+",";
			valueStr += "?,";
		}
		valueStr = valueStr.substring(0,valueStr.lastIndexOf(","))+")";
		query = query.substring(0, query.lastIndexOf(","))+")"+valueStr;
		return query;
	}
	
	private static Map populateSuccessMap( Element rootEle )
	{
		successMap = new LinkedHashMap();
		childList = new LinkedList();
		String accountName = null;
		String accountNo = null;
		Iterator iterator = rootEle.element("REPLY_ACCBAL").elements().iterator();
		while (iterator.hasNext())
		{
			Map childMap = new LinkedHashMap();
			Element ele = (Element) iterator.next();
			String name = ele.getName();
			String value = ele.getText();
			if( name.equalsIgnoreCase("ACC"))
			{
				accountName = name;
				accountNo = value;
//				System.out.println("Account No----->>>"+accountNo);
			}
			if(name.equalsIgnoreCase("INTLIQ"))
			{
				/*
				 * Get the child element
				 */
				Iterator itr = ele.elements().iterator();
//				System.out.println("---------------------------------");
				 
				while( itr.hasNext())
				{
					Element elmt = (Element)itr.next();
					String subName = elmt.getName();
					String subValue = elmt.getText();
//					System.out.println("Name====>>>"+subName+" Value===>>>"+subValue);
					childMap.put(accountName, accountNo);
					childMap.put(subName, subValue);
				}
				childList.add(childMap);
//				System.out.println("---------------------------------");
			}
			else
			{
//				System.out.println("Name====>>>"+name+" Value===>>>"+value);
				successMap.put(name, value);
			}
		}		
		return successMap;
	}

	public static void main(String[] args) 
	{
//		String filePath = "C:/dev/RND/mdbdmproj/xml/Text.xml";
//		String filePath = "C:/dev/RND/mdbdmproj/xml/REPLY_ERROR.xml";
		
		String filePath = "C:/dev/RND/mdbdmproj/xml/Temp150109.xml";
		
		Connection conn = getConnection();
		try
		{
//			Map dataMap = getTableColumnMap(conn);
//			System.out.println(dataMap);
			String msgXML = Reader.getFileContetns(filePath);
//			OPEACCBALENQ ope = new OPEACCBALENQ();
//			ope.parseXml(null, msgXML);
			parseXml(null, msgXML);
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}

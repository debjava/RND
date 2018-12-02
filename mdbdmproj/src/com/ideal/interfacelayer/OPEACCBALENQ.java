package com.ideal.interfacelayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import com.idealinvent.dm.DAO.OracleInternalMessage;
import com.idealinvent.dm.xmlInterface.GenericXMLInterface;

public class OPEACCBALENQ implements GenericXMLInterface {

	/**
	 * The String accountNo is used to store the reference
	 * in the database table column to refer to the
	 * INTLIQ tag data values. For each reference of accountNo,
	 * there will be one record of INTLIQ data. Added by
	 * Debadatta Mishra
	 */
	private static String accountNo = null;// Added by Deba
	/**
	 * This list is used to maintain the data for updation
	 * of the child table. Added by Debadatta Mishra
	 */
//	private static List childList = null;//new LinkedList();//Added by Deba
	/**
	 * Global time stamp for both the table.
	 */
	private static java.sql.Timestamp timeStamp = null;//Added by Deba
	/**
	 * Global time stamp in String
	 */
	private static String timeStampStr = null;//Added by Deba

	private static List successList;
	private static List errorList;
	private static String TAG_ROOT_SERVICE = "FCCACCSERVICE";
	private static final String filepath = "InterfaceDatabaseConn.properties";
	
	private static String[] successTagArray = { "REPLY_ACCBAL", "XREF",
		"TXNFLAG", "BRN", "ACC", "CCY", "OPNBAL", "CURBAL", "AVLBAL",
		"UNCOL_BAL", "MTDTOVCR", "MTDTOVDR", "ACY_BKD_AMT", "ACCR_DR",
		"ACCR_CR", "SUBLIMIT", "TOD_LIMIT", "TOD_ST_DT", "TOD_EXP_DT",
		"LINEID", "INTPRD", "NXLIQDT", "LIQDAYS", "LIQMNTHS","LIQYRS" };
	/*
	 * Commented out by Debadatta Mishra
	 */
//	private static String[] successTagArray = { "REPLY_ACCBAL", "XREF",
//			"TXNFLAG", "BRN", "ACC", "CCY", "OPNBAL", "CURBAL", "AVLBAL",
//			"UNCOL_BAL", "MTDTOVCR", "MTDTOVDR", "ACY_BKD_AMT", "ACCR_DR",
//			"ACCR_CR", "SUBLIMIT", "TOD_LIMIT", "TOD_ST_DT", "TOD_EXP_DT",
//			"LINEID", "INTLIQ", "INTPRD", "NXLIQDT", "LIQDAYS", "LIQMNTHS",
//			"LIQYRS" };

//	private static String[] successTabColName = { "XREF", "TXNFLAG", "BRANCH",
//			"ACC_NO", "CCY", "OPNBAL", "CURBAL", "AVLBAL", "UNCOLBAL",
//			"MTDTOVCR", "MTDTOVDR", "ACY_BKD_AMT", "ACCR_DR", "ACCR_CR",
//			"SUBLIMIT", "TOD_LIMIT", "TOD_ST_DT", "TOD_EXP_DT", "LINEID",
//			"INTPRD", "NXTLIQDT", "LIQDAYS", "LIQMNTHS", "LIQYRS" };
	
	
	private static String[] successTabColName = { "XREF", "TXNFLAG", "BRANCH",
		"ACC_NO", "CCY", "OPNBAL", "CURBAL", "AVLBAL", "UNCOLBAL",
		"MTDTOVCR", "MTDTOVDR", "ACY_BKD_AMT", "ACCR_DR", "ACCR_CR",
		"SUBLIMIT", "TOD_LIMIT", "TOD_ST_DT", "TOD_EXP_DT", "LINEID",
		"INTPRD", "NXTLIQDT", "LIQDAYS", "LIQMNTHS", "LIQYRS","REPLY_DATE" };
	
	private static String[] successTabColType = { "VARCHAR2", "CHAR", "CHAR",
			"NUMBER", "CHAR", "NUMBER", "NUMBER", "NUMBER", "NUMBER", "NUMBER",
			"NUMBER", "NUMBER", "NUMBER", "NUMBER", "NUMBER", "NUMBER", "DATE",
			"DATE", "VARCHAR2", "VARCHAR2", "DATE", "NUMBER", "NUMBER",
			"NUMBER","TIMESTAMP" };

	private static String[] errorTagArray = { "REPLY_ACCBAL", "XREF",
			"TXNFLAG", "ERROR", "ECODE", "EDESC" };  

	private static String[] errorTabColName = { "XREF", "TXNFLAG", "ECODE",
			"EDESC" };
	private static String[] errorTabColType = { "VARCHAR2", "CHAR", "VARCHAR2",
			"VARCHAR2" };
	
	
	
	private static Map successMap = null;
	private static List childList = null;
	private static Map errorMap = null;
	
	
	public void parseXml(String dtdName, String messageXML) 
	{
		System.out.println("OPEACCBALENQ : parseXml");
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
		System.out.println("OPEACCBALENQ : populateErrorMap");
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

	
	private static void populateErrorMapInDB()
	{
		System.out.println("OPEACCBALENQ : populateErrorMapInDB");
		Connection conn = new OPEACCBALENQ().getDBConnection();//It has to be changed as new OPEACCBALENQ().getDBConnection();
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

	
	private static void populateSuccessChildDBTable()
	{
		System.out.println("OPEACCBALENQ : populateSuccessChildDBTable");
		Connection conn = new OPEACCBALENQ().getDBConnection();//It has to be changed as new OPEACCBALENQ().getDBConnection();
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

	
	private static Map populateSuccessMap( Element rootEle )
	{
		System.out.println("OPEACCBALENQ : populateSuccessMap");
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
	
	private static void populateSuccessDbTable()
	{
		System.out.println("OPEACCBALENQ : populateSuccessDbTable");
		populateSuccessMapInDB();
	}
	
	private static void populateSuccessMapInDB()
	{
		System.out.println("OPEACCBALENQ : populateSuccessMapInDB");
		Connection conn = new OPEACCBALENQ().getDBConnection();//It has to be changed as new OPEACCBALENQ().getDBConnection();
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
	
	private static Map getTableColumnMap( Connection conn , String tableName )
	{
		System.out.println("OPEACCBALENQ : getTableColumnMap");
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
	
	
	
	private static String getDynamicQuery( Map dataMap , String tableName )
	{
		System.out.println("OPEACCBALENQ : getDynamicQuery");
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





	
	
	
	
	
	
	
	
	

//	public void parseXml(String dtdName, String messageXML) {
//
//		System.out.println(":::::::::inside umesh Anand OPEACCBALENQ::::::::::::::::::");
//		
//		Document msgDocument;
//		try {
//			msgDocument = DocumentHelper.parseText(messageXML);
//			Element rootEle = msgDocument.getRootElement();
//			String status = getStatus(rootEle, messageXML);
//			if (status.equals("S")) {
//				doServiceProcessing(rootEle, messageXML);
//				doDatabaseUpdate(successList, status);
//				updateChildTable(childList);
//			}
//			if (status.equals("F")) {
//				doErrorProcessing(rootEle, messageXML);
//				doDatabaseUpdate(errorList, status);
//			}
//		} catch (DocumentException e) {
//
//			e.printStackTrace();
//		}
//	}
	
//	private void doDatabaseUpdate()
//	{
//		
//	}
	
//	private static void doServiceProcessing(Element rootEle, String messageText) 
//	{
//		List tagList = Arrays.asList(successTagArray);//Added by Deba
//		childList = new LinkedList();//Added by Deba
////		successList = new ArrayList();
//		successList = new LinkedList();
//		Iterator iterator = rootEle.element(successTagArray[0]).elements().iterator();
//		String queryString = "";
//		while (iterator.hasNext())
//		{
//			try
//			{
//				Element ele = (Element) iterator.next();
//				String name = ele.getName();
//				String value = ele.getText();
//				/*
//				 * Added by Debadatta Mishra
//				 * Update the database table directly.
//				 */
////				stmt.executeQuery("insert into DMOPE_REPLY_BALANCE_S(" + cols+",REPLY_DATE"
////						+ ") values(" + values+","+"To_Timestamp('" + timeStampStr + "','yyyy-mm-dd HH24:mi:ss')"+ ")");
////				queryString += name;
//				if( name.equalsIgnoreCase("INTLIQ"))
//				{
//					System.out.println("Found");
//					//Handle in a different manner
//					Iterator itr = ele.elements().iterator();
//					Map dataMap = new HashMap();
//					while( itr.hasNext())
//					{
//						Element elmt = (Element)itr.next();
//						String subName = elmt.getName();
//						String subValue = elmt.getText();
//						dataMap.put(subName, subValue);
//						dataMap.put("ACC_NO", accountNo);
//						dataMap.put("REPLY_DATE", getDate());
//						/*
//						 * Insert the followings into database table 
//						 * dataMap
//						 * account no
//						 * system date
//						 */
//					}
//					childList.add(dataMap);
//				}
//				else
//				{
//					if( tagList.contains(name) && value != null && !value.equals(""))
//					{
//						successList.add(value);
//						if( name.equalsIgnoreCase("ACC") )
//							accountNo = value;
//					}
//					else
//						successList.add(null);
//				}
//
//			}
//			catch(IndexOutOfBoundsException ibe )
//			{
//				System.out.println("IndexOutOfBoundsException thrown");
//				ibe.printStackTrace();
//			}
//			catch( Exception e )
//			{
//				System.out.println("Other type of exception caught");
//				e.printStackTrace();
//			}
//		}
//		queryString += "REPLY_DATE";
//		
//	}
//	
//	/**This method is used to insert into the 
//	 * child table for the records of the tag
//	 * <INTLIQ>.
//	 * @param childList of type List 
//	 */
//	private static void updateChildTable(List childList)
//	{
//		String queryString = "insert into DMOPE_REPLY_BALANCE_INTLIQ_S" +
//		"( INTPRD,NXTLIQDT,LIQDAYS,LIQMNTHS,LIQYRS,ACC_NO,REPLY_DATE)" +
//		" values(?,?,?,?,?,?,?)";
//		Connection con = null ;
//		PreparedStatement prestmt = null;
//		try
//		{
//			con = new OPEACCBALENQ().getDBConnection();
//			prestmt = con.prepareStatement(queryString);
//			for( int i = 0 ; i < childList.size() ; i++ )
//			{
//				Map dataMap = (Map)childList.get(i);
//				prestmt = con.prepareStatement(queryString);
//				prestmt.setString(1, (String)dataMap.get("INTPRD"));
//				String dtStr = (String)dataMap.get("NXLIQDT");//NXLIQDT
//				java.sql.Date sqlDate = getSqlDate(dtStr);
//				prestmt.setDate(2,sqlDate);
//				prestmt.setInt(3, Integer.parseInt((String)dataMap.get("LIQDAYS")));
//				prestmt.setInt(4, Integer.parseInt((String)dataMap.get("LIQMNTHS")));
//				prestmt.setInt(5, Integer.parseInt((String)dataMap.get("LIQYRS")));
//				prestmt.setLong(6, Long.parseLong((String)dataMap.get("ACC_NO")));
//				prestmt.setTimestamp(7, timeStamp);
//				prestmt.executeUpdate();
//				con.commit();
//				System.out.println("Data inserted into child table successfully");
//			}
//		}
//		catch(NullPointerException npe )
//		{
//			System.out.println("NullPointerException thrown due to null value");
//			npe.printStackTrace();
//		}
//		catch( SQLException se )
//		{
//			System.out.println("SQLException - Error in processing database operation");
//			se.printStackTrace();
//		}
//		catch( Exception e )
//		{
//			System.out.println("Other exception thrown due to internal error");
//			e.printStackTrace();
//		}
//		finally
//		{
//			try
//			{
//				prestmt.close();
//			}
//			catch( SQLException se )
//			{
//				se.printStackTrace();
//			}
//		}
//				
//	}
	
	private static java.sql.Date getSqlDate( String dtStr )
	{
		System.out.println("getSQLDate : dtStr ----->>>"+dtStr);
		java.sql.Date sqlDate = null;
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			Date dt = format.parse(dtStr);
			long time = dt.getTime() ;
			sqlDate = new java.sql.Date( time );
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
//
//	private static void doErrorProcessing(Element rootEle, String messageText) {
//		errorList = new ArrayList();
//		Iterator iterator = rootEle.element(errorTagArray[0]).elements().iterator();
//		int i = 1;
//		while (iterator.hasNext()) {
//			Element ele = (Element) iterator.next();
//			String name = ele.getName();
//			String value = ele.getText();
//			value = value.trim();
//			if (name.equals(errorTagArray[i]) && value != null
//					&& !value.equals("")) {
//				errorList.add(value);
//				System.out.println("tag name:::" + name);
//				System.out.println("value::::::::::" + value);
//			}
//			if (name.equals(errorTagArray[3])) {
//				Iterator it = ele.elements().iterator();
//
//				while (it.hasNext()) {
//					Element subEle = (Element) it.next();
//					i++;
//					if (subEle.getName().equals(errorTagArray[i])
//							&& subEle.getText() != null
//							&& !subEle.getText().equals("")) {
//						errorList.add(subEle.getText());
//
//					}
//
//				}
//			}
//			i++;
//		}
//	}
//
//	private static String getStatus(Element rootEle, String messageText) {
//		Element msgEle = rootEle.element(successTagArray[0]);
//		return msgEle.element(successTagArray[2]).getText();
//	}
//
//	private static void doDatabaseUpdate(List list, String status)
//	{
//		Connection con = null;
//		Statement stmt = null;
//		String values = "";
//		String cols = "";
//		Iterator itr = list.iterator();
//
//		int i = 0;
//		try {
//			/*Class.forName("oracle.jdbc.driver.OracleDriver");
//			con = DriverManager.getConnection("jdbc:oracle:thin:@idealdb01:1521:ideal", "dmcheck","dmcheck");*/
//			con =  new OPEACCBALENQ().getDBConnection();
//			stmt = con.createStatement();
//
//			if (status.equals("S")) {
//				while (itr.hasNext()) {
//					String val = (String) itr.next();
//					System.out.println(val);
//					
////					if( val.equalsIgnoreCase(successTabColName[i]))
////					{
////						System.out.println("Coming to if block %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
////						if (!successTabColType[i].equals("NUMBER")
////								&& !successTabColType[i].equals("DATE"))
////							val = "'" + val + "'";
////						if (successTabColType[i].equals("DATE"))
////							val = "To_Date('" + val + "','yyyy-mm-dd')";
////						
////						if (i == list.size() - 1) 
////						{
////							values = values + val;
////							cols = cols + successTabColName[i];
////						}
////						else
////						{
////							values = values + val + ",";
////							cols = cols + successTabColName[i] + ",";
////						}
////						i++;
////					}
////					else
////					{
////						System.out.println("Coming to else to continue.................");
////						i++;
////						continue;
////					}
//					
//					System.out.println("--------------------------------------------");
//						if (!successTabColType[i].equals("NUMBER")
//								&& !successTabColType[i].equals("DATE"))
//							val = "'" + val + "'";
//						if (successTabColType[i].equals("DATE"))
//							val = "To_Date('" + val + "','yyyy-mm-dd')";
//						
//						if (i == list.size() - 1) 
//						{
//							values = values + val;
//							cols = cols + successTabColName[i];
//						}
//						else
//						{
//							values = values + val + ",";
//							cols = cols + successTabColName[i] + ",";
//						}
//						i++;
//					
//					System.out.println("--------------------------------------------");
//					
//				}
//				System.out.println("insert into DMOPE_REPLY_BALANCE_S(" + cols
//						+ ") values(" + values + ")");
//				timeStamp = new java.sql.Timestamp( System.currentTimeMillis() );
//				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				timeStampStr = format.format(timeStamp);
//				System.out.println("insert into DMOPE_REPLY_BALANCE_S(" + cols+",REPLY_DATE"
//						+ ") values(" + values+","+"To_Timestamp('" + timeStampStr + "','yyyy-mm-dd HH24:mi:ss')"+ ")");
//				stmt.executeQuery("insert into DMOPE_REPLY_BALANCE_S(" + cols+",REPLY_DATE"
//						+ ") values(" + values+","+"To_Timestamp('" + timeStampStr + "','yyyy-mm-dd HH24:mi:ss')"+ ")");
//				System.out.println("Message successfully inserted");
//				con.commit();
//				con.close();
//			}
//			i = 0;
//			if (status.equals("F")) {
//				while (itr.hasNext()) {
//					String val = (String) itr.next();
//					val = "'" + val + "'";
//					if (i == list.size() - 1) {
//						values = values + val;
//						cols = cols + errorTabColName[i];
//					} else {
//						values = values + val + ",";
//						cols = cols + errorTabColName[i] + ",";
//					}
//					i++;
//				}
//				
//				timeStamp = new java.sql.Timestamp( System.currentTimeMillis() );
//				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				timeStampStr = format.format(timeStamp);
////				stmt.executeQuery("insert into DMOPE_REPLY_BALANCE_F(" + cols + ") values(" + values + ")");
//				System.out.println("insert into DMOPE_REPLY_BALANCE_F(" + cols+",REPLY_DATE"
//						+ ") values(" + values+","+"To_Timestamp('" + timeStampStr + "','yyyy-mm-dd HH24:mi:ss')"+ ")");
//				stmt.executeQuery("insert into DMOPE_REPLY_BALANCE_F(" + cols+",REPLY_DATE"
//						+ ") values(" + values+","+"To_Timestamp('" + timeStampStr + "','yyyy-mm-dd HH24:mi:ss')"+ ")");
//				System.out.println("Message successfully inserted");
//				con.commit();
//				con.close();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("Exception while updating Flexml Response into Database : "+ e);
//		}
//	}
	/**
	 * 15-Oct-2008
	 * Method is used for get the oracle connection object
	 * @return
	 */	
	public Connection getDBConnection(){
		Connection connection = null;
		Properties properties = null;
		try {
			ClassLoader classLoader =  OPEACCBALENQ.class.getClassLoader();
			InputStream is = classLoader.getResourceAsStream(filepath);
			if (is != null) {
				properties = new Properties();
				properties.load(is);
			} else {
				System.out.println("Property file does not exist");
			}
		} catch (IOException e) {
			System.out.println("Error(IOException) =>" + e.getMessage());
		}
		
		try {
			Class.forName(properties.getProperty("driverclass"));
			connection = DriverManager.getConnection(properties
					.getProperty("dbcp_dburl"), properties
					.getProperty("dbcp_dbuser"), properties
					.getProperty("dbcp_dbpassword"));
			
			System.out.println("Got the DB Connection :::::::");
		} catch (SQLException sq) {
			System.out.println("ORACLEINTERNALMESSAGE Could not obtain OracleConnection..");
		} catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		return connection;
	}
}
		
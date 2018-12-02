package com.ideal.interfacelayer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import oracle.jdbc.OracleTypes;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.idealinvent.dm.xmlInterface.GenericXMLInterface;

public class ReutersRateUpload implements GenericXMLInterface
{
	private static final String filepath = "InterfaceDatabaseConn.properties";
	
	private static String nextChildElementName = null;
	private static long xrefValue = 0L; 
	private static String ecode = null;
	private static String edesc = null;
	
	private void populateDataFromXML( Element rootElement )
	{
		Element element = (Element)rootElement.elements().get(0);
		nextChildElementName = element.getName();
		List subElements = element.elements();
		for( int i = 0 ; i < subElements.size() ; i++ )
		{
			Element subElement = (Element)subElements.get(i);
			
			if(subElement.getName().equalsIgnoreCase("XREF"))
			{
				xrefValue = subElement.getText() != null ? Long.parseLong(subElement.getText()):0L;
			}
			else if( subElement.getName().equalsIgnoreCase("ERROR"))
			{
				populateErrorDetails(subElement);
			}
		}
	}
	
	private void populateErrorDetails(Element subElement )
	{
		List errElementList = subElement.elements();
		for( int i = 0 ; i < errElementList.size() ; i++ )
		{
			Element subErrElement = (Element)errElementList.get(i);
			if(subErrElement.getName().equalsIgnoreCase("ECODE"))
				ecode = subErrElement.getText();
			if(subErrElement.getName().equalsIgnoreCase("EDESC"))
				edesc = subErrElement.getText();
		}
	}
	
	public void parseXml( String dtdName, String messageXML ) 
	{
		Document msgDocument;
		try
		{
			msgDocument = DocumentHelper.parseText(messageXML);
			Element rootEle = msgDocument.getRootElement();
			String rootElementName = rootEle.getName();
			System.out.println("RootElementName -------->>>"+rootElementName);
			populateDataFromXML(rootEle);
			System.out.println("Next Child Elment Name ----->>>"+nextChildElementName);
			System.out.println("XREF value------->>>"+xrefValue);
			System.out.println("Error Code------>>>"+ecode);
			System.out.println("Error desc------>>>"+edesc);
			
			if( rootElementName.equalsIgnoreCase("FCCRTFLT"))
			{
				//REUTERS_RATE_UPLD.PROCESS_REPLY_LDMM
				System.out.println("Calling Database function for REUTERS_RATE_UPLD.PROCESS_REPLY_LDMM");
				processReplyLDMM(xrefValue,nextChildElementName,messageXML);
			}
			else if( rootElementName.equalsIgnoreCase("FCCDVRATE"))
			{
				//REUTERS_RATE_UPLD.PROCESS_REPLY_DV
				System.out.println("Calling Database function for REUTERS_RATE_UPLD.PROCESS_REPLY_DV");
				processREPLYDV(xrefValue,nextChildElementName,messageXML);
			}
			else if( rootElementName.equalsIgnoreCase("FCCRTFWD"))
			{
				//REUTERS_RATE_UPLD.PROCESS_REPLY_FWD
				System.out.println("Calling Database function for REUTERS_RATE_UPLD.PROCESS_REPLY_FWD");
				processREPLYFWD(xrefValue,nextChildElementName,messageXML);
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.out.println("Exception thrown while parsing");
		}
	}
	
	private void processReplyLDMM( long xrefValue , String nextChildName , String xmlContents  )
	{
		Connection conn = getDBConnection();
		CallableStatement ocs = null;
		String queryString = "{? = call REUTERS_RATE_UPLD.PROCESS_REPLY_LDMM(?,?,?,?,?)}";
		try
		{
			ocs = (CallableStatement)conn.prepareCall(queryString);
			ocs.registerOutParameter(1, OracleTypes.NUMERIC);
			ocs.setLong(2, xrefValue);
			ocs.setString(3, nextChildName);
			ocs.setString(4, ecode);
			ocs.setString(5, edesc);
			ocs.setString(6, xmlContents);
			
			boolean executeFlag = ocs.execute();
			int result = ocs.getInt(1);
			if( result == 1 )
				System.out.println("Database procedure executed successfully for REPLY_LDMM");
			else
				System.out.println("Error in executing database procedure for REPLY_LDMM");
		}
		catch( SQLException se )
		{
			se.printStackTrace();
			System.out.println("Error in processing SQL operation for REPLY_LDMM");
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.out.println("Unexpected Error in processing for REPLY_LDMM");
		}
		finally
		{
			close(ocs);
			close(conn);
			ecode = null;
			edesc = null;
		}
	}
	
	private void processREPLYDV( long xrefValue , String nextChildName , String xmlContents )
	{
		Connection conn = getDBConnection();
		CallableStatement ocs = null;
		String queryString = "{? = call REUTERS_RATE_UPLD.PROCESS_REPLY_DV(?,?,?,?,?)}";
		try
		{
			ocs = (CallableStatement)conn.prepareCall(queryString);
			ocs.registerOutParameter(1, OracleTypes.NUMERIC);
			ocs.setLong(2, xrefValue);
			ocs.setString(3, nextChildName);
			ocs.setString(4, ecode);
			ocs.setString(5, edesc);
			ocs.setString(6, xmlContents);
			boolean executeFlag = ocs.execute();
			int result = ocs.getInt(1);
			if( result == 1 )
				System.out.println("Database procedure executed successfully for REPLY_DV");
			else
				System.out.println("Error in executing database procedure for REPLY_DV");
		}
		catch( SQLException se )
		{
			se.printStackTrace();
			System.out.println("Error in processing SQL operation for REPLY_DV");
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.out.println("Unexpected Error in processing for REPLY_DV");
		}
		finally
		{
			close(ocs);
			close(conn);
			ecode = null;
			edesc = null;
		}
	}
	
	private void processREPLYFWD( long xrefValue , String nextChildName , String xmlContents )
	{
		Connection conn = getDBConnection();
		CallableStatement ocs = null;
		String queryString = "{? = call REUTERS_RATE_UPLD.PROCESS_REPLY_FWD(?,?,?,?,?)}";
		try
		{
			ocs = (CallableStatement)conn.prepareCall(queryString);
			ocs.registerOutParameter(1, OracleTypes.NUMERIC);
			ocs.setLong(2, xrefValue);
			ocs.setString(3, nextChildName);
			ocs.setString(4, ecode);
			ocs.setString(5, edesc);
			ocs.setString(6, xmlContents);
			boolean executeFlag = ocs.execute();
			int result = ocs.getInt(1);
			if( result == 1 )
				System.out.println("Database procedure executed successfully for REPLY_FWD");
			else
				System.out.println("Error in executing database procedure for REPLY_FWD");
		}
		catch( SQLException se )
		{
			se.printStackTrace();
			System.out.println("Error in processing SQL operation for REPLY_FWD");
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.out.println("Unexpected Error in processing for REPLY_FWD");
		}
		finally
		{
			close(ocs);
			close(conn);
			ecode = null;
			edesc = null;
		}
	}
	
	public Connection getDBConnection()
	{
		Connection connection = null;
		Properties properties = null;
		try {
			ClassLoader classLoader =  ReutersRateUpload.class.getClassLoader();
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
	
	private void close( Object obj )
	{
		try
		{
			if( obj instanceof CallableStatement )
			{
				CallableStatement ocs = ( CallableStatement )obj;
				ocs.close();
			}
			else if( obj instanceof Connection )
			{
				Connection conn = ( Connection )obj;
				conn.close();
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.out.println("Error in closing the connection");
		}
	}
	
//~~ Uncomment the following methods for unit testing	
	
//	/**Method for unit testing , remove it
//	 * @return
//	 */
//	public static Connection getConnection()
//	{
//		Connection conn = null;
//		try
//		{
//			Class.forName("oracle.jdbc.driver.OracleDriver");
//			conn = DriverManager.getConnection("jdbc:oracle:thin:@10.70.24.9:1521:DNBDM", "EEDMDV31","EEDMDV31");
//		}
//		catch( SQLException se )
//		{
//			se.printStackTrace();
//		}
//		catch( Exception e )
//		{
//			e.printStackTrace();
//		}
//		return conn;
//	}
	
	
//	public static void main(String[] args) 
//	{
//		String filePath = "xml/pra/FCCDVRATE_ERROR.xml";//C:\dev\RND\mdbdmproj\xml\pra\FCCDVRATE_ERROR.xml
//		String msgXML = Reader.getFileContetns(filePath);
//		new ReutersRateUpload().parseXml(null, msgXML);
//	}
	
}

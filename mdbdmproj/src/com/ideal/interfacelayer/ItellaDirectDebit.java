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

public class ItellaDirectDebit //implements GenericXMLInterface
{
	private static final String filepath = "InterfaceDatabaseConn.properties";
	
	private static String xrefValue = null;
	private static String fccrefValue = null;
	private static String ecode = null;
	private static String edesc = null;
	private static String nextChildElementName = null;
	
//	public Connection getDBConnection()
//	{
//		Connection connection = null;
//		Properties properties = null;
//		try {
//			ClassLoader classLoader =  ItellaDirectDebit.class.getClassLoader();
//			InputStream is = classLoader.getResourceAsStream(filepath);
//			if (is != null) {
//				properties = new Properties();
//				properties.load(is);
//			} else {
//				System.out.println("Property file does not exist");
//			}
//		} catch (IOException e) {
//			System.out.println("Error(IOException) =>" + e.getMessage());
//		}
//		
//		try {
//			Class.forName(properties.getProperty("driverclass"));
//			connection = DriverManager.getConnection(properties
//					.getProperty("dbcp_dburl"), properties
//					.getProperty("dbcp_dbuser"), properties
//					.getProperty("dbcp_dbpassword"));
//			
//			System.out.println("Got the DB Connection :::::::");
//		} catch (SQLException sq) {
//			System.out.println("ORACLEINTERNALMESSAGE Could not obtain OracleConnection..");
//		} catch (ClassNotFoundException e) 
//		{
//			e.printStackTrace();
//		}
//		
//		return connection;
//	}
	/*
	 * For unit testing
	 */
	public static Connection getConnection()
	{
		Connection conn = null;
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@10.70.24.9:1521:DNBDM", "EEDMDV31","EEDMDV31");
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
				xrefValue = subElement.getText() ;
			}
			else if( subElement.getName().equalsIgnoreCase("ERROR"))
			{
				populateErrorDetails(subElement);
			}
		}
		System.out.println("populateDataFromXML : xref value-------->"+xrefValue);
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
	private void processPC_DD_ERROR( String xref, String tagName, String xmlContents )
	{
		Connection conn = getConnection();//getDBConnection();
		CallableStatement ocs = null;
		String queryString = "{? = call PK_ITELLA_AGREEMENT.PROCESS_REPLY_AGRMNT(?,?,?,?,?,?)}";
		try
		{
			ocs = (CallableStatement)conn.prepareCall(queryString);
			ocs.registerOutParameter(1, OracleTypes.NUMERIC);
			ocs.setString(2, xref);
			ocs.setString(3, tagName);
			ocs.setString(4, null);
			ocs.setString(5, ecode);
			ocs.setString(6, edesc);
			ocs.setString(7, xmlContents);
			
			boolean executeFlag = ocs.execute();
			int result = ocs.getInt(1);
			if( result == 1 )
				System.out.println("Database procedure executed successfully for PC_DD_ERROR");
			else
				System.out.println("Error in executing database procedure for PC_DD_ERROR");
		}
		catch( SQLException se )
		{
			se.printStackTrace();
			System.out.println("Error in processing SQL operation for PC_DD_ERROR");
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.out.println("Unexpected Error in processing for PC_DD_ERROR");
		}
		finally
		{
			close(ocs);
			close(conn);
			ecode = null;
			edesc = null;
		}
	}
	
	private void processREPLY_PC_CONTRACT( String xref, String fcRef , String replyTagName, String xmlContents )
	{
		System.out.println("processREPLY_PC_CONTRACT : xref-------->"+xref);
		System.out.println("processREPLY_PC_CONTRACT : fcRef-------->"+fcRef);
		System.out.println("processREPLY_PC_CONTRACT : replyTagName-------->"+replyTagName);
		Connection conn = getConnection();//getDBConnection();
		CallableStatement ocs = null;
		String queryString = "{? = call PK_ITELLA_INSTRUCTION.PROCESS_REPLY_INSTR(?,?,?,?,?,?)}";
		try
		{
			ocs = (CallableStatement)conn.prepareCall(queryString);
			ocs.registerOutParameter(1, OracleTypes.NUMERIC);
			ocs.setString(2, xrefValue);
			ocs.setString(3, replyTagName);
			ocs.setString(4, fccrefValue);
			ocs.setString(5, ecode);
			ocs.setString(6, edesc);
			ocs.setString(7, xmlContents);
			
			boolean executeFlag = ocs.execute();
			int result = ocs.getInt(1);
			if( result == 1 )
				System.out.println("Database procedure executed successfully for REPLY_PC_CONTRACT");
			else
				System.out.println("Error in executing database procedure for REPLY_PC_CONTRACT");
		}
		catch( SQLException se )
		{
			se.printStackTrace();
			System.out.println("Error in processing SQL operation for REPLY_PC_CONTRACT");
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.out.println("Unexpected Error in processing for REPLY_PC_CONTRACT");
		}
		finally
		{
			close(ocs);
			close(conn);
			xrefValue = null;
			fccrefValue = null;
			ecode = null;
			edesc = null;
		}
	}
	
	private void populateDataForREPLY_PC( Element element )
	{
		Element pcConDetailElemt = element.element("PCCONTDETAILS");
		List pcDetlsEleList = pcConDetailElemt.elements();
		for( int i = 0 , n = pcDetlsEleList.size() ; i < n ; i++ )
		{
			Element subElemt = (Element)pcDetlsEleList.get(i);
			if( subElemt.getName().equalsIgnoreCase("xref"))
				xrefValue = subElemt.getText();
			if( subElemt.getName().equalsIgnoreCase("FCCREF") )
				fccrefValue = subElemt.getText();
		}
	}
	
	private void processPC_CONTRACT_ERROR( String xrefValue, String nextChildElementName, String messageXML )
	{
		Connection conn = getConnection();//getDBConnection();
		CallableStatement ocs = null;
		String queryString = "{? = call PK_ITELLA_INSTRUCTION.PROCESS_REPLY_INSTR(?,?,?,?,?,?)}";
		try
		{
			ocs = (CallableStatement)conn.prepareCall(queryString);
			ocs.registerOutParameter(1, OracleTypes.NUMERIC);
			ocs.setString(2, xrefValue);
			ocs.setString(3, nextChildElementName );
			ocs.setString(4, null);
			ocs.setString(5, ecode);
			ocs.setString(6, edesc);
			ocs.setString(7, messageXML);
			
			boolean executeFlag = ocs.execute();
			int result = ocs.getInt(1);
			if( result == 1 )
				System.out.println("Database procedure executed successfully for PC_CONTRACT_ERROR");
			else
				System.out.println("Error in executing database procedure for PC_CONTRACT_ERROR");
		}
		catch( SQLException se )
		{
			se.printStackTrace();
			System.out.println("Error in processing SQL operation for PC_CONTRACT_ERROR");
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.out.println("Unexpected Error in processing for PC_CONTRACT_ERROR");
		}
		finally
		{
			close(ocs);
			close(conn);
			ecode = null;
			edesc = null;
			xrefValue = null;
		}
	}
	
	private void populateREPLY_PC_DD( Element element )
	{
		Element xrefElemt = element.element("XREF");
		xrefValue = xrefElemt.getText();
		System.out.println("populateREPLY_PC_DD : xrefValue------->"+xrefElemt.getName());
//		Element pcDdDetailsElemt = element.element("PCDDDETAILS");
//		List pcDetlsEleList = pcDdDetailsElemt.elements();
//		System.out.println(pcDetlsEleList.size());
//		for( int i = 0 , n = pcDetlsEleList.size() ; i < n ; i++ )
//		{
//			Element subElemt = (Element)pcDetlsEleList.get(i);
//			if( subElemt.getName().equalsIgnoreCase("xref"))
//				xrefValue = subElemt.getText();
//			if( subElemt.getName().equalsIgnoreCase("FCCREF") )
//				fccrefValue = subElemt.getText();
//		}
	}
	
	private void processREPLY_PC_DD( String xref, String fcRef , String replyTagName, String xmlContents )
	{
		Connection conn = getConnection();//getDBConnection();
		CallableStatement ocs = null;
		String queryString = "{? = call PK_ITELLA_AGREEMENT.PROCESS_REPLY_AGRMNT(?,?,?,?,?,?)}";
		try
		{
			ocs = (CallableStatement)conn.prepareCall(queryString);
			ocs.registerOutParameter(1, OracleTypes.NUMERIC);
			ocs.setString(2, xrefValue);
			ocs.setString(3, nextChildElementName );
			ocs.setString(4, fcRef);
			ocs.setString(5, ecode);
			ocs.setString(6, edesc);
			ocs.setString(7, xmlContents);
			
			boolean executeFlag = ocs.execute();
			int result = ocs.getInt(1);
			if( result == 1 )
				System.out.println("Database procedure executed successfully for REPLY_PC_DD");
			else
				System.out.println("Error in executing database procedure for REPLY_PC_DD");
		}
		catch( SQLException se )
		{
//			se.printStackTrace();
			System.out.println("Error in processing SQL operation for REPLY_PC_DD");
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.out.println("Unexpected Error in processing for REPLY_PC_DD");
		}
		finally
		{
			close(ocs);
			close(conn);
			ecode = null;
			edesc = null;
			xrefValue = null;
			fccrefValue = null;
		}

	}
	
	public void parseXml(String dtdName, String messageXML) 
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
			if( nextChildElementName.equalsIgnoreCase("ERROR_PC_DD"))
			{
				processPC_DD_ERROR(xrefValue, nextChildElementName, messageXML);
			}
			else if( nextChildElementName.equalsIgnoreCase("REPLY_PC_CONTRACT"))
			{
				xrefValue = null;
				Element element = (Element)rootEle.elements().get(0);
				populateDataForREPLY_PC(element);
				processREPLY_PC_CONTRACT(xrefValue, fccrefValue, nextChildElementName, messageXML);
			}
			else if( nextChildElementName.equalsIgnoreCase("ERROR_PC_CONTRACT"))
			{
				processPC_CONTRACT_ERROR(xrefValue, nextChildElementName, messageXML);
			}
			else if( nextChildElementName.equalsIgnoreCase("REPLY_PC_DD") )
			{
				xrefValue = null;
				Element element = (Element)rootEle.elements().get(0);
				populateREPLY_PC_DD(element);
				processREPLY_PC_DD(xrefValue, fccrefValue, nextChildElementName, messageXML);
			}
			else if( nextChildElementName.equalsIgnoreCase("REPLY_PC_DD_CANC") )
			{
//				xrefValue = null;
//				Element element = (Element)rootEle.elements().get(0);
//				populateREPLY_PC_DD(element);
//				processREPLY_PC_DD(xrefValue, fccrefValue, nextChildElementName, messageXML);
			}
				
		}
		catch( NullPointerException npe )
		{
			npe.printStackTrace();
			System.out.println("NullPointer Exception thrown, value is null");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("Other Exception thrown");
		}
	}
	
	
}

package com.ideal.interfacelayer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import oracle.jdbc.driver.OracleCallableStatement;
import oracle.jdbc.driver.OracleTypes;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class TestOPEREPLYPAMT 
{


	/**
	 * Map containing the data to update the database table.
	 * It contains data for both success message and failure 
	 * message.
	 */
	private static Map statusMap = null;
	private static List successList;
	private static List errorList;
	private static String TAG_ROOT_SERVICE="FCCCPMNT";
	private static final String filepath = "InterfaceDatabaseConn.properties";

	private static String []successTagArray={"REPLY_CPMNT","XREF","SCODE","STATUS","FCCREF","QUEUE"};

	private static String[] successTabColName={"XREF","SCODE","STATUS","FCCREF","QUEUE"};

	private static String []successTabColType={"VARCHAR2","VARCHAR2","VARCHAR2","VARCHAR2","VARCHAR2"};

	private static String []errorTagArray={"ERROR_CPMNT","XREF","ERROR","ECODE","EDESC"};

	private static String[] errorTabColName={"XREF","ECODE","EDESC"};

	private static String[] errorTabColType={"VARCHAR2","VARCHAR2","VARCHAR2"};
	
	/**
	 * Global time stamp for both the table.
	 */
	private static java.sql.Timestamp timeStamp = null;//Added by Deba
	/**
	 * Global time stamp in String
	 */
	private static String timeStampStr = null;//Added by Deba

	public   void parseXml(String dtdName, String messageXML) {

		System.out.println("inside ope reply parsing::::::::::::::::::"+messageXML);

		Document msgDocument;
		try {
			msgDocument = DocumentHelper.parseText(messageXML);
			Element rootEle = msgDocument.getRootElement();

			String status=getStatus(rootEle,messageXML);
			System.out.println("status::"+status);
			if(status.equals("REPLY_CPMNT"))
			{
				doServiceProcessing(rootEle,messageXML);
				doDatabaseUpdate(successList,status);
				//added by Debadatta Mishra
				updateStatus();
			}
			if(status.equals("ERROR_CPMNT")){
				doErrorProcessing(rootEle,messageXML);
				doDatabaseUpdate(errorList,status);
				//added by Debadatta Mishra
				updateStatus();
			}
		} catch (DocumentException e) {

			e.printStackTrace();
		}
	}

	private static void doServiceProcessing(Element rootEle, String messageText)
	{
		successList=new ArrayList();
		//Added by Debadatta Mishra
		statusMap = new HashMap();
		Iterator iterator=rootEle.element(successTagArray[0]).elements().iterator();
		int i=1;
		while(iterator.hasNext())
		{
			Element ele=(Element)iterator.next();
			String name = ele.getName();
			String value=ele.getText();
			//Added by Debadatta Mishra
			statusMap.put(name, value);
			if (name.equals(successTagArray[i]) && value != null && !value.equals(""))
			{
				successList.add(value);
			}
			i++;
		}
	}

	private static void doErrorProcessing(Element rootEle, String messageText)
	{

		errorList=new ArrayList();
		//Added by Debadatta Mishra
		statusMap = new HashMap();
		Iterator iterator=rootEle.element(errorTagArray[0]).elements().iterator();
		int i=1;
		while(iterator.hasNext())
		{
			Element ele=(Element)iterator.next();
			String name=ele.getName();
			String value=ele.getText();
			value=value.trim();
			//Added by Debadatta Mishra
			statusMap.put("STATUS", "F");
			if(name.equals(errorTagArray[i]) && value!=null && !value.equals(""))
			{
				errorList.add(value);
				//Added by Debadatta Mishra
				statusMap.put(name, value);
				System.out.println("tag name:::"+name);
				System.out.println("value::::::"+value);
			}

			if(name.equals(errorTagArray[2]))
			{
				Iterator it=ele.elements().iterator();

				while(it.hasNext())
				{
					Element subEle=(Element)it.next();
					i++;
					if(subEle.getName().equals(errorTagArray[i]) && subEle.getText()!=null && !subEle.getText().equals("")){
						errorList.add(subEle.getText());
						statusMap.put(subEle.getName(), subEle.getText());
						
					}
				}
			}
			i++;
		}
	}
	private static String getStatus(Element rootEle, String messageText)
	{
		Element msgEle = null;
		String rootName = null;
		try{
			msgEle = rootEle.element(successTagArray[0]);
			rootName = msgEle.getName();
		}catch(NullPointerException npe){}
		try{
			msgEle = rootEle.element(errorTagArray[0]);
			rootName = msgEle.getName();
		}catch(NullPointerException npe){}
		System.out.println("rootName:::::: "+rootName);
		return rootName;
	}
	
	/**
	 * Method used to update the database table
	 * for status.
	 */
	private static void updateStatus()
	{
		System.out.println("SuccessMap contents------"+statusMap);
		Connection conn = null;
		OracleCallableStatement oraCallStmt = null;
		try
		{
			conn = getConnection();
			final String storedProcname = "{? = call opepk_trxns_status.fn_change_txn_status(?,?,?,?,?)}";
			oraCallStmt = (OracleCallableStatement) conn
			.prepareCall(storedProcname);
			oraCallStmt.registerOutParameter(1, OracleTypes.NUMERIC);
			System.out.println("XREF----------->>>"+(String)statusMap.get("XREF"));
			System.out.println("STATUS----------->>>"+(String)statusMap.get("STATUS"));
			System.out.println("FCCREF----------->>>"+(String)statusMap.get("FCCREF"));
			System.out.println("ECODE----------->>>"+(String)statusMap.get("ECODE"));
			System.out.println("EDESC----------->>>"+(String)statusMap.get("EDESC"));
			
			oraCallStmt.setString(2, (String)statusMap.get("XREF"));
			oraCallStmt.setString(3, (String)statusMap.get("STATUS"));
			oraCallStmt.setString(4, (String)statusMap.get("FCCREF"));
			oraCallStmt.setString(5, (String)statusMap.get("ECODE"));
			oraCallStmt.setString(6, (String)statusMap.get("EDESC"));
			boolean executeFlag = oraCallStmt.execute();
			final int res = oraCallStmt.getInt(1);
			if (res == 1) 
			{
				System.out.println("Database Procedure executed successfully");
			}
			else
			{
				System.out.println("@@@@@@@@@@ Database Procedure failed to execute @@@@@@@@");
			}
			
		}
		catch( SQLException se )
		{
			se.printStackTrace();
			System.out.println(se);
			System.out.println(se.getMessage());
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.out.println(e);
			System.out.println(e.getMessage());
		}
	}

	private static void doDatabaseUpdate(List list,String status)
	{ 
		Connection con=null;
		Statement stmt=null;
		String values="";
		String cols="";
		Iterator itr=list.iterator();

		int i=0;
		try
		{
			/*Class.forName("oracle.jdbc.driver.OracleDriver");
			con=DriverManager.getConnection("jdbc:oracle:thin:@idealdb01:1521:ideal","dmcheck","dmcheck");*/
			con =  getConnection();
			stmt=con.createStatement();

			if(status.equals("REPLY_CPMNT"))
			{
				while(itr.hasNext())
				{
					String val=(String)itr.next();
					System.out.println(val);
					if(!successTabColType[i].equals("NUMBER") && !successTabColType[i].equals("DATE"))
						val="'"+val+"'";
					if(successTabColType[i].equals("DATE"))
						val="To_Date('"+val+"','yyyy-mm-dd')";
					if(i==list.size()-1)
					{
						values=values+val;
						cols=cols+successTabColName[i];
					}
					else
					{
						values=values+val+",";
						cols=cols+successTabColName[i]+",";
					}
					i++;
				}
				
				timeStamp = new java.sql.Timestamp( System.currentTimeMillis() );
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				timeStampStr = format.format(timeStamp);
				
				
				System.out.println("insert into DMOPE_REPLY_CPMNT(" + cols+",REPLY_DATE"
						+ ") values(" + values+","+"To_Timestamp('" + timeStampStr + "','yyyy-mm-dd HH24:mi:ss')"+ ")");
				stmt.executeQuery("insert into DMOPE_REPLY_CPMNT(" + cols+",REPLY_DATE"
						+ ") values(" + values+","+"To_Timestamp('" + timeStampStr + "','yyyy-mm-dd HH24:mi:ss')"+ ")");
				
//				System.out.println("insert into DMOPE_REPLY_CPMNT("+cols+") values("+values+")");
//				stmt.executeQuery("insert into DMOPE_REPLY_CPMNT("+cols+") values("+values+")");
				System.out.println("Message successfully inserted");
				con.commit();
				con.close();
			}
			i=0;
			if(status.equals("ERROR_CPMNT"))
			{
				while(itr.hasNext())
				{
					String val=(String)itr.next();
					val="'"+val+"'";

					if(i==list.size()-1)
					{
						values=values+val;
						cols=cols+errorTabColName[i];
					}
					else
					{
						values=values+val+",";
						cols=cols+errorTabColName[i]+",";
					}
					i++;
				}
				stmt.executeQuery("insert into DMOPE_ERROR_CPMNT("+cols+") values("+values+")");
				System.out.println("Message successfully inserted");
				con.commit();
				con.close();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception while updating Flexml Response into Database : "
					+ e);
		}
	}

	/**
	 * 17-Oct-2008
	 * Method is used for get the oracle connection object
	 * @return
	 */	
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
	
	public static void main(String[] args) 
	{
		String filePath = "C:/dev/RND/mdbdmproj/xml/Test123.xml";
		String msgXML = Reader.getFileContetns(filePath);
		new TestOPEREPLYPAMT().parseXml(null, msgXML);
	}

}

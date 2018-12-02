package com.ideal.interfacelayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class TestXML 
{
	/**
	 * The String accountNo is used to store the reference
	 * in the database table column to refer to the
	 * INTLIQ tag data values. For each reference of accountNo,
	 * there will be one record of INTLIQ data.
	 */
	private static String accountNo = null;// Added by Deba
	private static List successList;
	private static String[] successTagArray = { "REPLY_ACCBAL", "XREF",
		"TXNFLAG", "BRN", "ACC", "CCY", "OPNBAL", "CURBAL", "AVLBAL",
		"UNCOL_BAL", "MTDTOVCR", "MTDTOVDR", "ACY_BKD_AMT", "ACCR_DR",
		"ACCR_CR", "SUBLIMIT", "TOD_LIMIT", "TOD_ST_DT", "TOD_EXP_DT",
		"LINEID", "INTPRD", "NXLIQDT", "LIQDAYS", "LIQMNTHS","LIQYRS" };
	

	public void parseXml(String dtdName, String messageXML)
	{
		Document msgDocument;
		try
		{
			msgDocument = DocumentHelper.parseText(messageXML);
			Element rootEle = msgDocument.getRootElement();
			String status = getStatus(rootEle, messageXML);
			if (status.equals("S")) 
			{
				doServiceProcessing1(rootEle, messageXML);
				//				System.out.println(successList);
				//				doDatabaseUpdate(successList, status);
			}
		}
		catch( NullPointerException npe )
		{
			npe.printStackTrace();
		}
		catch (DocumentException e)
		{
			e.printStackTrace();
		}
	}

	private static String getStatus(Element rootEle, String messageText)
	{
		Element msgEle = rootEle.element(successTagArray[0]);
		return msgEle.element(successTagArray[2]).getText();
	}
	
	
	private static void doServiceProcessing1(Element rootEle, String messageText) 
	{
		List tagList = Arrays.asList(successTagArray);//Added by Deba
		successList = new ArrayList();
		Iterator iterator = rootEle.element(successTagArray[0]).elements().iterator();
		
		while (iterator.hasNext())
		{
			try
			{
				Element ele = (Element) iterator.next();
				String name = ele.getName();
				String value = ele.getText();
				if( name.equalsIgnoreCase("INTLIQ"))
				{
					System.out.println("Found");
					//Handle in a different manner
					Iterator itr = ele.elements().iterator();
					Map dataMap = new HashMap();
					while( itr.hasNext())
					{
						Element elmt = (Element)itr.next();
						String subName = elmt.getName();
						String subValue = elmt.getText();
//						System.out.println("Sub Name----"+subName+" Sub Value----"+subValue);
						dataMap.put(subName, subValue);
						dataMap.put("ACC_NO", accountNo);
						dataMap.put("REPLY_DATE", getDate());
						/*
						 * Insert the followings into database table 
						 * dataMap
						 * account no
						 * system date
						 */
					}
					updateChildTable(dataMap);
				}
				else
				{
					if( tagList.contains(name) && value != null && !value.equals(""))
					{
						successList.add(value);
						if( name.equalsIgnoreCase("ACC") )
							accountNo = value;
					}
				}

			}
			catch(IndexOutOfBoundsException ibe )
			{
				System.out.println("IndexOutOfBoundsException thrown");
				ibe.printStackTrace();
			}
			catch( Exception e )
			{
				System.out.println("Other type of exception caught");
				e.printStackTrace();
			}
		}
		System.out.println("successList---->>>"+successList);
		
	}
	
	private static void updateChildTable(Map dataMap)
	{
		Iterator itr = dataMap.entrySet().iterator();
		while( itr.hasNext() )
		{
			Map.Entry me = ( Map.Entry )itr.next();
			System.out.println(me.getKey()+"-------"+me.getValue());
		}
		System.out.println("----------------------------------");
	}

	private static void doServiceProcessing(Element rootEle, String messageText) 
	{
		successList = new ArrayList();
		Iterator iterator = rootEle.element(successTagArray[0]).elements().iterator();
		int i = 1;
		
		
//		List tagList = Arrays.asList(successTagArray);
//		System.out.println(tagList);
		
		
		
		
		while (iterator.hasNext())
		{
			try
			{
				Element ele = (Element) iterator.next();
				String name = ele.getName();
				String value = ele.getText();
				System.out.println(name+"<<<--------->>>"+value);
				if (name.equals(successTagArray[i]) && value != null
						&& !value.equals(""))
					successList.add(value);

				// if in case INTLIQ tags values we want to insert into the database
				// then this code should be uncommented
				/**13-Oct-2008
				 * Code added by anand to fix the INTLIQ child elements insertion into data base
				 */
				if (name.equals(successTagArray[20])) {
					Iterator it = ele.elements().iterator();
					while (it.hasNext()) {
						Element subEle = (Element) it.next();
						i++;
						try {
							System.out.println("name::::" + subEle.getName());
							System.out.println("value::::" + subEle.getText());
							if (subEle.getName().equals(successTagArray[i])
									&& subEle.getText() != null
									&& !subEle.getText().equals(""))
								successList.add(subEle.getText());
						} catch (ArrayIndexOutOfBoundsException aioe) {
							successList.add(subEle.getText());
						}
					}
				}
				i++;
			}
			catch(IndexOutOfBoundsException ibe )
			{
				System.out.println("IndexOutOfBoundsException thrown");
				ibe.printStackTrace();
			}
			catch( Exception e )
			{
				System.out.println("Other type of exception caught");
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * Method used to get the system date in
	 * defined format.
	 * @return the date in a particular format
	 */
	private static String getDate()
	{
		String dateString = null;
		Date sysDate = new Date( System.currentTimeMillis() );
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
		dateString = sdf.format(sysDate);
		return dateString;
	}


	public static void main(String[] args)
	{
//		String filePath = "C:/dev/RND/mdbdmproj/xml/Reply3.xml";
		String filePath = "C:/dev/RND/mdbdmproj/xml/FC_REPLY.XML";
//		TestXML test = new TestXML();
//		test.parseXml(null, Reader.getFileContetns(filePath));
		
		String msgXML = Reader.getFileContetns(filePath);
		try
		{
			Document msgDocument = DocumentHelper.parseText(msgXML);
			Element rootEle = msgDocument.getRootElement();
			System.out.println(rootEle.getName());
			
			Iterator iterator=rootEle.element("REPLY_CPMNT").elements().iterator();
			while(iterator.hasNext())
			{
				Element ele=(Element)iterator.next();
				String name = ele.getName();
				String value=ele.getText();
				System.out.println(name+"-------"+value);
			}	
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}

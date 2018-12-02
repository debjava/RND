import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.om.OMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Test1 
{

	public static void main(String[] args) throws Exception
	{
		
//		File file = new File("soapxml/request.xml");//MessageFactory.newInstance();
		File file = new File("soapxml/request4.xml");
		InputStream in = new FileInputStream(file);
		int len = (int)file.length();
		byte[] datas = new byte[len];
		in.read(datas);
		String str = new String( datas );
		//		System.out.println(str);
		SOAPConnectionFactory sfc = SOAPConnectionFactory.newInstance();
		SOAPConnection connection = sfc.createConnection();

//		MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
		MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
//		MessageFactory mf = MessageFactory.newInstance();
		SOAPMessage sm = mf.createMessage();//createMessage();
		SOAPPart soapPart             = sm.getSOAPPart();   
		ByteArrayInputStream stream   = new ByteArrayInputStream(datas);   
		StreamSource source           = new StreamSource(stream);   

		soapPart.setContent(source);   

		// -- DONE   
		sm.saveChanges();

		sm.writeTo(System.out);   
		


		URL endpoint = new URL("http://localhost:8080/primitivearrayservice/services/primitivearrayservice");
		SOAPMessage response = connection.call(sm, endpoint);
		response.setContentDescription("text/xml");//ContentType("text/xml");

		System.out.println(response.getContentDescription());

		SOAPBody soapBody = response.getSOAPBody();  
		System.out.println("----------- From Data -------------");
		System.out.println(soapBody);
		
		
//		System.out.println(soapBody.getChildNodes().getLength());
//		OMElement element = (OMElement)soapBody.getParentElement();
//		System.out.println(element.getText());
		
		
		NodeList nl = soapBody.getChildNodes(); 
		
		
		for( int i = 0 ; i < nl.getLength() ; i++ )
		{
			Node n1 = nl.item(i);
			
			NodeList n2 = n1.getChildNodes();
			for( int j = 0 ; j < n2.getLength(); j++ )
			{
				System.out.println( n2.item(j).getFirstChild().getTextContent()  );
			}
			
		}
		
		
//        
		
		
		
//		SOAPBody soapBody = sm.getSOAPBody();   
//        Iterator i = soapBody.getChildElements(); 
//        while( i.hasNext() )
//        {
//        	Node node = (Node) i.next();
//        	 System.out.println("-------"+node.getNodeValue());
//        }
        
        
        
        
       
//        System.out.println( node.getChildNodes().item(0).getNodeValue());
        
        
//        System.out.println(   
//             node.getChildNodes().item(0)       // "isbn"   
//             .getChildNodes().item(0)           // text node inside "isbn"    
//                  .getNodeValue());   



		//	      URL url = new URL(hostname+port+oburl);
		//	        URLConnection conn = endpoint.openConnection();
		//	        conn.setRequestProperty("Content-Type", "text/xml");
		//	        conn.setDoOutput(true);
		//	        conn.setDoInput(true);
		//	        conn.setUseCaches(false);
		//	        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		//	        wr.writeBytes(str);
		//	        wr.flush();
		//	        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		//	        String line;
		//	        while ((line = rd.readLine()) != null) {
		//	            System.out.println(line);
		//	        }
		//	        wr.close();
		//	        rd.close();





	}

}

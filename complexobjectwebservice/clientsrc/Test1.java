import java.io.StringReader;
import java.util.Iterator;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.saaj.util.SAAJUtil;
import org.w3c.dom.Element;


public class Test1
{
	private static EndpointReference targetEPR = 
		new EndpointReference(
		"http://localhost:8080/orgservice/services/orgservice");

	public static OMElement getPricePayload( )
	{
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(
				"http://service.org/xsd", "tns");

		OMElement method = fac.createOMElement("getEmp", omNs);
		

		OMElement empValue = fac.createOMElement("Employee", omNs);
		//		orgValue.addChild(fac.createOMText(orgValue, "123"));
		
		OMElement empValue1 = fac.createOMElement("id", omNs);
		empValue1.addChild(fac.createOMText(empValue1, "123"));
		empValue.addChild(empValue1);
		
		OMElement empValue2 = fac.createOMElement("name", omNs);
		empValue2.addChild(fac.createOMText(empValue2, "DDDDDDDDDDD"));
		empValue.addChild(empValue2);
		
		OMElement empValue3 = fac.createOMElement("salary", omNs);
		empValue3.addChild(fac.createOMText(empValue3, "23.45"));
		empValue.addChild(empValue3);
		
		
		OMElement adrsValue = fac.createOMElement("adrs", omNs);//,empValue

		OMElement adrsgvalue1 = fac.createOMElement("permanetAdrs", omNs);
		adrsgvalue1.addChild(fac.createOMText(adrsgvalue1, "Bolangir"));
		adrsValue.addChild(adrsgvalue1);

		OMElement adrsvalue2 = fac.createOMElement("temporaryAdrs", omNs);
		adrsvalue2.addChild(fac.createOMText(adrsvalue2, "Bangalore"));
		adrsValue.addChild(adrsvalue2);
		
		
		empValue.addChild(adrsValue);

		method.addChild(empValue);

		return method;
	}
	
	public static SOAPEnvelope getSoapEvelope( OMElement omElement )
	{
		SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        SOAPEnvelope envelope = fac.getDefaultEnvelope();
        OMNamespace omNs =   fac.createOMNamespace(
            "http://ws.apache.org/axis2/xsd", "ns1");
        envelope.getBody().addChild(omElement);
        return envelope;
	}
	
	public static void showResponseResult( Iterator itr ) throws Exception
	{
		while( itr.hasNext() )
		{
			OMElement result11 = (OMElement) itr.next();
			System.out.println(  getSoapEvelope(result11).toStringWithConsume());
			
			StringReader sr = new StringReader(getSoapEvelope(result11).toStringWithConsume());
			XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(sr);
			
//			XMLStreamReader r = result11.getXMLStreamReader();
			
			
			StAXSOAPModelBuilder builder = new StAXSOAPModelBuilder(parser, null);
			System.out.println(builder.getDocumentElement().getLocalName());
			SOAPEnvelope envelope = (SOAPEnvelope) builder.getDocumentElement();
			SOAPBody soapBody = envelope.getBody();
			Iterator itr1 = soapBody.getAllAttributes();
			while( itr1.hasNext() )
			{
				System.out.println(itr1.next());
			}
			
//			StringReader sr = new StringReader(result11.toStringWithConsume());
			
			
			
			
			
//			System.out.println(result11.getLocalName()+"-----------"+result11.getText());
//			Iterator innerItr = result11.getChildElements();
//			if( innerItr != null )
//			{
//				showResponseResult(innerItr);
//			}
			
			
		}
	}
	
	public static void main(String[] args) throws Exception 
	{
		OMElement getPricePayload = getPricePayload( );

		Options options = new Options();
		options.setTo(targetEPR);
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

		ServiceClient sender = new ServiceClient();
		sender.setOptions(options);

		OMElement result = sender.sendReceive(getPricePayload);
		System.out.println(result.getQName());
		
		System.out.println("--------------------XML Message----------------------");
		System.out.println(getPricePayload.toStringWithConsume());
		System.out.println("--------------------XML Message----------------------");
		
		
		
		SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        SOAPEnvelope envelope = fac.getDefaultEnvelope();
        OMNamespace omNs =   fac.createOMNamespace(
            "http://ws.apache.org/axis2/xsd", "ns1");
        envelope.getBody().addChild(getPricePayload);
        System.out.println(envelope);

		
		
		Iterator itr = result.getChildElements();
		showResponseResult(itr);
//		Iterator innerItr;
//		while( itr.hasNext() )
//		{
//			OMElement result11 = (OMElement) itr.next();
//			innerItr = result11.getChildElements();
//
//			while( innerItr.hasNext() )
//			{
//				OMElement e = (OMElement) innerItr.next();
//				System.out.println(e.getLocalName()+"-----------"+e.getText());
////				System.out.println(((OMElement) innerItr.next()).getText());
//			}
//
//		}


	}
}

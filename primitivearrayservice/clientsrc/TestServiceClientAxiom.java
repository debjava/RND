import java.util.Iterator;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;


public class TestServiceClientAxiom 
{
	private static String webserviceURL = "http://localhost:8080/primitivearrayservice/services/primitivearrayservice";
	private static EndpointReference targetEPR = new EndpointReference( webserviceURL );
	
	public static OMElement getPricePayload( )
	{
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace("http://service.primitivearrayservice/xsd", "tns");

		OMElement method = fac.createOMElement("getIntData", omNs);

		
		OMElement value11 = fac.createOMElement("inDatas", omNs);
		value11.addChild(fac.createOMText(value11, "123"));
		method.addChild(value11);

		OMElement value1 = fac.createOMElement("inDatas", omNs);
		value1.addChild(fac.createOMText(value1, "11"));
		method.addChild(value1);
		
		OMElement value2 = fac.createOMElement("inDatas", omNs);
		value2.addChild(fac.createOMText(value2, "320"));
		method.addChild(value2);
		
		OMElement value3 = fac.createOMElement("inDatas", omNs);
		value3.addChild(fac.createOMText(value3, "33"));
		method.addChild(value3);

		return method;
	}
	
	public static void showResponseResult( Iterator itr ) throws Exception
	{
		while( itr.hasNext() )
		{
			OMElement result11 = (OMElement) itr.next();
			System.out.println(result11.getLocalName()+"-----------"+result11.getText());
			Iterator innerItr = result11.getChildElements();
			if( innerItr != null )
			{
				showResponseResult(innerItr);
			}
		}
	}
	
//	public static SOAPEnvelope getSoapEvelope( OMElement omElement )
//	{
//		SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
//        SOAPEnvelope envelope = fac.getDefaultEnvelope();
//        OMNamespace omNs =   fac.createOMNamespace(
//            "http://ws.apache.org/axis2/xsd", "ns1");
//        envelope.getBody().addChild(omElement);
//        return envelope;
//	}
	
	public static SOAPEnvelope getSoapEvelope( OMElement omElement )
	{
		SOAPFactory fac = OMAbstractFactory.getSOAP12Factory();
        SOAPEnvelope envelope = fac.getDefaultEnvelope();
        OMNamespace omNs =   fac.createOMNamespace(
            "http://ws.apache.org/axis2/xsd", "ns1");
        envelope.getBody().addChild(omElement);
        return envelope;
	}

	
	public static void main(String[] args) throws Exception 
	{
		OMElement getPricePayload = getPricePayload( );

		Options options = new Options();
		options.setTo(targetEPR);
		
		//Setting the soap version
		options.setSoapVersionURI(org.apache.axiom.soap.SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
		options.setSoapVersionURI(org.apache.axiom.soap.SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
		
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
		System.out.println("------------------ Request Soap XML -------------- ");
		System.out.println(getSoapEvelope(getPricePayload));
		ServiceClient sender = new ServiceClient();
		sender.setOptions(options);

		OMElement result = sender.sendReceive(getPricePayload);
		System.out.println("------------------ Response Soap XML -------------- ");
		System.out.println(getSoapEvelope(result));
		showResponseResult(result.getChildElements());
	}

}

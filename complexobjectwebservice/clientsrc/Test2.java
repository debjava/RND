import java.util.Iterator;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;


public class Test2 
{
	private static EndpointReference targetEPR = 
		new EndpointReference(
		"http://localhost:8080/orgservice/services/orgservice");
	
	public static SOAPEnvelope getSoapEvelope( OMElement omElement )
	{
		SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        SOAPEnvelope envelope = fac.getDefaultEnvelope();
        OMNamespace omNs =   fac.createOMNamespace(
            "http://ws.apache.org/axis2/xsd", "ns1");
        envelope.getBody().addChild(omElement);
        return envelope;
	}
	
	public static OMElement getPricePayload( )
	{
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(
				"http://service.org/xsd", "tns");

		//Webservice Method creation
		OMElement method = fac.createOMElement("getInfo", omNs);
		
		OMElement orgValue = fac.createOMElement("Organisation", omNs);
		
		OMElement orgValue1 = fac.createOMElement("name", omNs);
		orgValue1.addChild(fac.createOMText(orgValue1, "JP Nagar"));
		orgValue.addChild(orgValue1);
		
		OMElement orgValue2 = fac.createOMElement("location", omNs);
		orgValue2.addChild(fac.createOMText(orgValue2, "karnataka"));
		orgValue.addChild(orgValue2);
		
		//Create the emp field
		OMElement empValue = fac.createOMElement("emp", omNs);
		
		OMElement empValue1 = fac.createOMElement("id", omNs);
		empValue1.addChild(fac.createOMText(empValue1, "123"));
		empValue.addChild(empValue1);
		
		OMElement empValue2 = fac.createOMElement("name", omNs);
		empValue2.addChild(fac.createOMText(empValue2, "DDDDDDDDDDD"));
		empValue.addChild(empValue2);
		
		OMElement empValue3 = fac.createOMElement("salary", omNs);
		empValue3.addChild(fac.createOMText(empValue3, "23.45"));
		empValue.addChild(empValue3);
		//Create the adrs field
		OMElement adrsValue = fac.createOMElement("adrs", omNs);//,empValue

		OMElement adrsgvalue1 = fac.createOMElement("permanetAdrs", omNs);
		adrsgvalue1.addChild(fac.createOMText(adrsgvalue1, "Bolangir"));
		adrsValue.addChild(adrsgvalue1);

		OMElement adrsvalue2 = fac.createOMElement("temporaryAdrs", omNs);
		adrsvalue2.addChild(fac.createOMText(adrsvalue2, "Bangalore"));
		adrsValue.addChild(adrsvalue2);
		
		empValue.addChild(adrsValue);
		
		orgValue.addChild(empValue);

		method.addChild(orgValue);
		

		return method;
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


	}
}

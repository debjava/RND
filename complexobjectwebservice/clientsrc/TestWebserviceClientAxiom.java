import java.util.Iterator;


import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;


public class TestWebserviceClientAxiom 
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
		
		OMElement orgValue = fac.createOMElement("Organisation", omNs);
//		orgValue.addChild(fac.createOMText(orgValue, "123"));
		OMElement orgValue1 = fac.createOMElement("location", omNs);
		orgValue1.addChild(fac.createOMText(orgValue1, "DDDDDDDDDDD"));
		orgValue.addChild(orgValue1);
		
		OMElement orgValue2 = fac.createOMElement("name", omNs);
		orgValue2.addChild(fac.createOMText(orgValue2, "TCS"));
		orgValue.addChild(orgValue2);
		

		OMElement empValue = fac.createOMElement("Employee", omNs);
		empValue.addChild(fac.createOMText(empValue, "123"));
		method.addChild(empValue);

		OMElement value1 = fac.createOMElement("id", omNs);
		value1.addChild(fac.createOMText(value1, "123"));
		empValue.addChild(value1);

		OMElement value2 = fac.createOMElement("name", omNs);
		value2.addChild(fac.createOMText(value2, "Hati"));
		empValue.addChild(value2);

		OMElement value3 = fac.createOMElement("salary", omNs);
		value3.addChild(fac.createOMText(value3, "1234"));
		empValue.addChild(value3);
		
		OMElement adrsValue = fac.createOMElement("Address", omNs);
		
		OMElement adrsgvalue1 = fac.createOMElement("permanetAdrs", omNs);
		adrsgvalue1.addChild(fac.createOMText(adrsgvalue1, "sasa"));
		adrsValue.addChild(adrsgvalue1);
		
		OMElement adrsvalue2 = fac.createOMElement("temporaryAdrs", omNs);
		adrsvalue2.addChild(fac.createOMText(adrsvalue2, "sasaffffffff"));
		adrsValue.addChild(adrsvalue2);
		
		
		OMElement empValue4 = fac.createOMElement("adrs", omNs);
		empValue4.addChild(fac.createOMText(empValue4, adrsValue.getQName()));
		empValue4.addChild(adrsValue);
		
		
		OMElement value5 = fac.createOMElement("emp", omNs);
//		empValue4.addChild(fac.createOMText(empValue4, adrsValue));
		value5.addChild(empValue);
		
		//Add the adress to employee
		empValue.addChild(adrsValue);
		//Add the oraganisation
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

		Iterator itr = result.getChildElements();
		Iterator innerItr;
		while( itr.hasNext() )
		{
			OMElement result11 = (OMElement) itr.next();
			innerItr = result11.getChildElements();

			while( innerItr.hasNext() )
			{
				OMElement e = (OMElement) innerItr.next();
				System.out.println(e.getLocalName()+"-----------"+e.getText());
//				System.out.println(((OMElement) innerItr.next()).getText());
			}

		}


	}
}

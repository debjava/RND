import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;

import com.ddlab.webservice.client.PrimitivearrayserviceStub;
import com.ddlab.webservice.client.PrimitivearrayserviceStub.GetIntData;
import com.ddlab.webservice.client.PrimitivearrayserviceStub.GetIntDataResponse;


public class TestServiceUsingstub 
{
	public static SOAPEnvelope getSoapEvelope( OMElement omElement )
	{
		SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        SOAPEnvelope envelope = fac.getDefaultEnvelope();
        OMNamespace omNs =   fac.createOMNamespace(
            "http://ws.apache.org/axis2/xsd", "ns1");
        envelope.getBody().addChild(omElement);
        return envelope;
	}
	

	public static void main(String[] args) 
	{
		try 
		{
			PrimitivearrayserviceStub stub = new PrimitivearrayserviceStub();
			GetIntData gid = new GetIntData();
			int[] intdatas = new int[] {1,2,3,4,5};
			gid.setIntDatas(intdatas);
			System.out.println("------------------ Request Soap XML -------------- ");
			System.out.println(getSoapEvelope(gid.getOMElement(new QName("getIntData"), null)));
			GetIntDataResponse gids = stub.getIntData(gid);
			System.out.println("------------------ Response Soap XML -------------- ");
			System.out.println(getSoapEvelope(gids.getOMElement(new QName("getIntData"), null)));
			int[] result = gids.get_return();
			for( int i : result )
			{
				System.out.println(i);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}

}

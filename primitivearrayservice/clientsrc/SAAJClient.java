import java.util.Iterator;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.w3c.dom.Node;


public class SAAJClient 
{
	public static void main (String args[])
    {
//        String arg1 = args[0];
//        String arg2 = args[1];

        String operation = "getIntData";    // could also be "subtract"
        String urn = "primitivearrayservice";//"primitivearrayservice";
        String destination = "http://localhost:8080/primitivearrayservice/services/primitivearrayservice";

        try
        {
            // First create the connection
//            SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
        	SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = soapConnFactory.createConnection();

            // Next, create the actual message
            MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
            SOAPMessage message = messageFactory.createMessage();

            SOAPPart soapPart = message.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();

            // This method demonstrates how to set HTTP and SOAP headers.
            // setOptionalHeaders(message, envelope);

            // Create and populate the body
            SOAPBody body = envelope.getBody();

            // Create the main element and namespace
            SOAPElement bodyElement = body.addChildElement(
                    envelope.createName(operation, "ns1", "http://service.primitivearrayservice/xsd"));
            
//            SOAPElement bodyElement = body.addChildElement(
//                    envelope.createName(operation));
            
           
            
            
            // Add parameters
            bodyElement.addChildElement("ns1").addTextNode("1");
            bodyElement.addChildElement("ns1").addTextNode("2");

            // Save the message
            message.saveChanges();
            System.out.println(envelope);
            // Check the input
            /*
            System.out.println("\nRequest:\n");
            message.writeTo(System.out);
            System.out.println();
            */

            // Send the message and get the reply
            SOAPMessage reply = connection.call(message, destination);

            // Retrieve the result - no error checking is done: BAD!
            soapPart = reply.getSOAPPart();
            envelope = soapPart.getEnvelope();
            body = envelope.getBody();

            Iterator iter = body.getChildElements();
            Node resultOuter = ((Node) iter.next()).getFirstChild();
            Node result = resultOuter.getFirstChild();

//            System.out.println("add("","+arg2+") = "+result.getNodeValue());
            System.out.println( result.getNodeValue());
            

            // Check the output
            /*
            System.out.println("\nResponse:\n");
            // Create the transformer
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            // Extract the content of the reply
            Source sourceContent = reply.getSOAPPart().getContent();
            // Set the output for the transformation
            StreamResult result = new StreamResult(System.out);
            transformer.transform(sourceContent, result);
            System.out.println();
            */

            // Close the connection           
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}

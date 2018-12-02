import java.io.File;
import java.util.Iterator;

import org.jvnet.wom.api.WSDLDefinitions;
import org.jvnet.wom.api.WSDLService;
import org.jvnet.wom.api.WSDLSet;
import org.jvnet.wom.api.parser.WOMParser;

import com.sun.xml.xsom.parser.XSOMParser;


public class TestWomParser1 
{
	public static void main(String[] args) throws Exception
	{
		String wsdlLocation = "http://localhost:8080/orgservice/services/orgservice?wsdl";
		WOMParser parser = new WOMParser();
		
		
		//create my own XSOMParser
		XSOMParser xsParser = new XSOMParser();
		xsParser.setErrorHandler(parser.getErrorHandler());
		xsParser.setEntityResolver(parser.getEntityResolver());

		//set my XSOMParser to receive parsing events
		parser.setSchemaContentHandler(xsParser.getParserHandler());

		
		
		
		WSDLSet wsdlSet = parser.parse( new File("testdata/orgservice.wsdl"));
		
		Iterator itr = wsdlSet.getWSDLs();
		while( itr.hasNext() )
		{
			System.out.println(itr.next());
		}
		
//		WSDLDefinitions def = wsdlSet.getWSDLs().next();
//		System.out.println(def.getName());
		
		
	}

}

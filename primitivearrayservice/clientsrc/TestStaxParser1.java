import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.axiom.om.impl.llom.OMTextImpl;


public class TestStaxParser1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		File file = new File("soapxml/request4.xml");
		InputStream in = new FileInputStream(file);
		
		StAXOMBuilder stAXOMBuilder = new StAXOMBuilder(in);
		OMDocument doc = stAXOMBuilder.getDocument();
		
		Iterator itr = doc.getChildren();
		while( itr.hasNext() )
		{
			OMElement el = (OMElement)itr.next();
			Iterator itr1 = el.getChildren();
			while( itr1.hasNext() )
			{
				Object obj = itr1.next();
//				System.out.println( obj.getClass() );
				if(  obj instanceof OMTextImpl )
				{
					OMTextImpl ot = (OMTextImpl)obj;
//					System.out.println(ot.getTextAsQName());
				}
				else if( obj instanceof OMElementImpl )
				{
					OMElementImpl ol = (OMElementImpl)obj;
//					System.out.println(ol.getChildren());
					
					Iterator it = ol.getChildren();
					while( it.hasNext() )
					{
//						OMElement ol1 = (OMElement)it.next();
//						OMTextImpl ol1 = (OMTextImpl)it.next();
						System.out.println(it.next().getClass());
					}
					
					
					
				}
				
//				
			}
			
			
		}

	}

}

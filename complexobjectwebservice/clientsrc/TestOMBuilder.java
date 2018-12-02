import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

public class TestOMBuilder {

    /**
     * Pass the file name as an argument
     * @param args
     */
    public static void main(String[] args) {
        try {
            //create the parser
            XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream("data/orgservice.wsdl"));
            StAXOMBuilder builder = new StAXOMBuilder(parser);
            //get the root element
            OMElement documentElement = builder.getDocumentElement();

            //dump the out put to console with caching
//            System.out.println(documentElement.toStringWithConsume());
            
            System.out.println(documentElement.getFirstElement().getText());
            
            Iterator itr = documentElement.getChildren();//getChildrenWithLocalName("orgservice");
            while( itr.hasNext() )
            {
//            	OMElement o1 = (OMElement)itr.next();
//            	System.out.println(o1.getText());
            	
            	 OMNode child = (OMNode)itr.next();
//            	 System.out.println(child);
                 int type = child.getType();
                 System.out.println("Type"+type);
                 if (type == OMNode.TEXT_NODE) 
                 {
                	 
                 }
//                     summary.addContent(((OMText)child).getText().length());
//                 } else if (type == OMNode.ELEMENT_NODE) {
//                     summary.addElements(1);
//                     walkElement((OMElement)child, summary);
//                 }

            	
            	
            	
            	
            }
            

        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
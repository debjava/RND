import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import org.apache.wsif.WSIFException;
import org.apache.wsif.WSIFMessage;
import org.apache.wsif.WSIFOperation;
import org.apache.wsif.WSIFPort;
import org.apache.wsif.WSIFService;
import org.apache.wsif.WSIFServiceFactory;
import org.apache.wsif.schema.ComplexType;
import org.apache.wsif.schema.Parser;
import org.apache.wsif.schema.SequenceElement;
import org.apache.wsif.util.WSIFUtils;

import com.ibm.wsdl.ServiceImpl;


public class TestParser1 {

	public static void showMap( Map map )
	{
		Iterator itr = map.entrySet().iterator();
		while( itr.hasNext() )
		{
			Map.Entry me = (Map.Entry)itr.next();
			//			System.out.println(me.getKey().getClass());
			ServiceImpl si = (ServiceImpl)me.getValue();
			javax.xml.namespace.QName qname = si.getQName();
			System.out.println( qname.getLocalPart());



			//			System.out.println(me.getValue().getClass());
		}
	}
	
	private static void unWrapIfWrappedDocLit(List parts, String operationName, Definition def) throws WSIFException {
		   Part p = WSIFUtils.getWrappedDocLiteralPart(parts, operationName);
		   if (p != null) {
			  List unWrappedParts = WSIFUtils.unWrapPart(p, def);
			  parts.remove(p);
			  parts.addAll(unWrappedParts);
		   }
	}
	
	private static void retrieveSignature(
	        List parts,
	        String[] names,
	        Class[] types) {
	        // get parts in correct order
	        for (int i = 0; i < names.length; ++i) {
	            Part part = (Part) parts.get(i);
	            names[i] = part.getName();
	            System.out.println(names[i]);
	            QName partType = part.getTypeName();
	            if (partType == null) 
	            {
	               partType = part.getElementName();
	            }
	            if (partType == null) {
	                throw new RuntimeException(
	                    "part " + names[i] + " must have type name declared");
	            }
	            // only limited number of types is supported
	            // cheerfully ignoring schema namespace ...
	            String s = partType.getLocalPart();
	            System.out.println("datatype ::::"+s);
	            if ("string".equals(s)) {
	                types[i] = String.class;
	            } else if ("double".equals(s)) {
	                types[i] = Integer.TYPE;
	            } else if ("float".equals(s)) {
	                types[i] = Float.TYPE;
	            } else if ("int".equals(s)) {
	                types[i] = Integer.TYPE;
	            } else if ("boolean".equals(s)) {
	                types[i] = Boolean.TYPE;
	            } else 
	            {
	                throw new RuntimeException(
	                    "part type " + partType + " not supported in this sample");
//	                unWrapIfWrappedDocLit(parts, name, def);
//	            	retrieveSignature(parts, names, types);
	            }
	        }
	    }

	public static void main(String[] args) 
	{
//		String wsdlLocation = "http://localhost:8080/webcalculator/services/webcalculator?wsdl";
		
//		String wsdlLocation = "http://localhost:8080/empservice/services/empservice?wsdl";
		String wsdlLocation = "http://localhost:8080/orgservice/services/orgservice?wsdl";
		
		try 
		{
			Definition def = WSIFUtils.readWSDL(null, wsdlLocation);
			Map map = def.getServices();
			String serviceName = null;
			String portName = null;
			String portTypeNS = null;
			String portTypeName = null;
			Iterator itr = map.entrySet().iterator();
			while( itr.hasNext() )
			{
				Map.Entry me = (Map.Entry)itr.next();
				ServiceImpl si = (ServiceImpl)me.getValue();
				javax.xml.namespace.QName qname = si.getQName();
				serviceName = qname.getLocalPart();
				System.out.println( serviceName );
			}
			Service service = WSIFUtils.selectService(def, null, serviceName);
			
//			Map portTypes = def.getPortTypes();
			Map portTypes = WSIFUtils.getAllItems(def, "PortType");
			// Really there should be a way to specify the portType
			// for now just try to find one with the portName 
			if (portTypes.size() > 1 && portName != null) 
			{
				for (Iterator i=portTypes.keySet().iterator(); i.hasNext(); ) 
				{
					QName qn = (QName) i.next();
					if (portName.equals(qn.getLocalPart())) 
					{
						portTypeName = qn.getLocalPart();
						System.out.println("PortType Name--->"+portTypeName);
						portTypeNS = qn.getNamespaceURI();
						System.out.println("portTypeNS--->"+portTypeNS);
						break;
					}
				}
			}
			PortType portType = WSIFUtils.selectPortType(def, portTypeNS, portTypeName);
//			System.out.println(portType);
			WSIFServiceFactory factory = WSIFServiceFactory.newInstance();
	        WSIFService dpf = factory.getService(def, service, portType);
	        WSIFPort port = null;
	        if (portName == null)
	            port = dpf.getPort();
	        else
	            port = dpf.getPort(portName);
	        String inputName = null;
	        String outputName = null;
	        String operationName = null;
	        
	        if (inputName == null && outputName == null) {
	            // retrieve list of operations
	            List operationList = portType.getOperations();
	            System.out.println(operationList.size());
	            // try to find input and output names for the operation specified
	            boolean found = false;
	            for (Iterator i = operationList.iterator(); i.hasNext();) 
	            {
	                Operation op = (Operation) i.next();
	                String name = op.getName();
//	                System.out.println("Operation Name ::::"+name);
	                operationName = name;
//	                if (!name.equals(operationName)) {
//	                    continue;
//	                }
//	                if (found) {
//	                    throw new RuntimeException(
//	                        "Operation '"
//	                            + operationName
//	                            + "' is overloaded. "
//	                            + "Please specify the operation in the form "
//	                            + "'operationName:inputMessageName:outputMesssageName' to distinguish it");
//	                }
//	                found = true;
	                Input opInput = op.getInput();
	                inputName = (opInput.getName() == null) ? null : opInput.getName();
	                System.out.println("inputName ::: "+inputName);
	                Output opOutput = op.getOutput();
	                outputName = (opOutput.getName() == null) ? null : opOutput.getName();
	                System.out.println("outputName ::: "+outputName);
	            }
	        }
	        WSIFOperation operation =
	            port.createOperation(operationName, inputName, outputName);
	        WSIFMessage input = operation.createInputMessage();
	        WSIFMessage output = operation.createOutputMessage();
	        WSIFMessage fault = operation.createFaultMessage();

	        // retrieve list of names and types for input and names for output
	        List operationList = portType.getOperations();
	        String[] outNames = new String[0];
	        Class[] outTypes = new Class[0];
	        for (Iterator i = operationList.iterator(); i.hasNext();) 
	        {

	            Operation op = (Operation) i.next();
	            String name = op.getName();
	            System.out.println("Operation Name ::::"+name);
//	            if (!name.equals(operationName)) {
//	                continue;
//	            }
//	            if (found) {
//	                throw new RuntimeException("overloaded operations are not supported in this sample");
//	            }
//	            found = true;

	            //System.err.println("op = "+op);
	            Input opInput = op.getInput();

	            // first determine list of arguments
	            String[] inNames = new String[0];
	            Class[] inTypes = new Class[0];
	            if (opInput != null) {
	                List parts = opInput.getMessage().getOrderedParts(null);
	                unWrapIfWrappedDocLit(parts, name, def);
	                int count = parts.size();
	                inNames = new String[count];
	                inTypes = new Class[count];
	                
	                
	                
	                
//	                retrieveSignature(parts, inNames, inTypes);
	                
	                //For testing
	                
	                for (int j = 0; j < inNames.length; j++)
	                {
	                	Part part = (Part) parts.get(j);
	                	inNames[j] = part.getName();
	    	            System.out.println("ZZZZZZZZZZZZ "+inNames[j]);
	    	            QName partType = part.getTypeName();
	    	            
	    	            
	    	            
	    	            if (partType == null) 
	    	            {
	    	               partType = part.getElementName();
	    	            }
	    	            if (partType == null) {
	    	                throw new RuntimeException(
	    	                    "part " + inNames[j] + " must have type name declared");
	    	            }
	    	            System.out.println(partType.toString());
	    	            // only limited number of types is supported
	    	            // cheerfully ignoring schema namespace ...
	    	            String s = partType.getLocalPart();
	    	            System.out.println("datatype ::::"+s);
	    	            List ll = new ArrayList();
	    	            
	    	            System.out.println("--------------------------------------");
	    	            Parser.getAllSchemaTypes(def, ll, null);
	    	            System.out.println(ll);
	    	            ComplexType ct = (ComplexType)ll.get(2);
	    	            SequenceElement[] se = ct.getSequenceElements();
	    	            
	    	            for( int a = 0 ; a < se.length ; a++ )
	    	            {
	    	            	System.out.println(se[a].getTypeName().getLocalPart());
	    	            	System.out.println(se[a].getElementType().getLocalPart());
	    	            }
	    	            
	    	            
	    	            System.out.println("--------------------------------------");
	    	            
	    	            ll.add(part);
	    	            List l1 = WSIFUtils.unWrapPart(part, def);
	    	            
	    	            System.out.println(l1);
	    	            
//	    	            unWrapIfWrappedDocLit(ll, name, def);
//	    	            Part part1 = opInput.getMessage().getPart("Employee");
//	    	            System.out.println("00000 "+part1.getName());
	    	            
	    	            Part part2 = opInput.getMessage().getPart("Employee");
	    	            System.out.println("---------"+part2);
	    	            for( int k = 0 ; k < inNames.length ; k++ )
	    	            {
	    	            	part = (Part) ll.get(k);
		                	inNames[k] = part.getName();
		    	            System.out.println(inNames[k]);
		    	            partType = null;//part.getTypeName();
		    	            if (partType == null) 
		    	            {
		    	               partType = part.getExtensionAttribute(new QName("Employee"));//tElementName();
		    	            }
		    	            if (partType == null) {
		    	                throw new RuntimeException(
		    	                    "part " + inNames[k] + " must have type name declared");
		    	            }
		    	            // only limited number of types is supported
		    	            // cheerfully ignoring schema namespace ...
		    	            s = partType.getLocalPart();
		    	            System.out.println("datatype ::::"+s);
	    	            }
	    	            
	    	            
	    	            
	                }
	                
	                
	                
	                
	                //For testing
	                
	            }
	            
	            // now prepare out parameters
//	            System.out.println(inNames.length);
//	            for (int pos = 0; pos < inNames.length; ++pos)
//	            {
////	                String arg = args[pos ];
//	            	
//	                Object value = null;
//	                Class c = inTypes[pos];
//	                System.out.println("print out");
//	                System.out.println(c.getName());
////	                if (c.equals(String.class)) {
////	                    value = arg;
////	                } else if (c.equals(Double.TYPE)) {
////	                    value = new Double(arg);
////	                } else if (c.equals(Float.TYPE)) {
////	                    value = new Float(arg);
////	                } else if (c.equals(Integer.TYPE)) {
////	                    value = new Integer(arg);
////	                } else if (c.equals(Boolean.TYPE)) {
////	                    value = new Boolean(arg);
////	                } else {
////	                    throw new RuntimeException("not know how to convert '" + arg + "' into " + c);
////	                }
//
//	                input.setObjectPart(inNames[pos], value);
//	            }

//	            Output opOutput = op.getOutput();
//	            if (opOutput != null) {
//	                List parts = opOutput.getMessage().getOrderedParts(null);
//	                unWrapIfWrappedDocLit(parts, name+"Response", def);
//	                int count = parts.size();
//	                outNames = new String[count];
//	                outTypes = new Class[count];
//	                retrieveSignature(parts, outNames, outTypes);
//	            }

	        
	        }

	        
	        
	        
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}

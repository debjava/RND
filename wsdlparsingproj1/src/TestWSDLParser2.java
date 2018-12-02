import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import org.apache.wsif.WSIFException;
import org.apache.wsif.WSIFPort;
import org.apache.wsif.WSIFService;
import org.apache.wsif.WSIFServiceFactory;
import org.apache.wsif.util.WSIFUtils;

import com.ibm.wsdl.ServiceImpl;


public class TestWSDLParser2 
{
	public static String getServiceName( Definition def )
	{
		String serviceName = null;
		try 
		{
			Map map = def.getServices();
			Iterator itr = map.entrySet().iterator();
			while( itr.hasNext() )
			{
				Map.Entry me = (Map.Entry)itr.next();
				ServiceImpl si = (ServiceImpl)me.getValue();
				javax.xml.namespace.QName qname = si.getQName();
				serviceName = qname.getLocalPart();
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return serviceName;
	}
	
	private static void retrieveSignature(
	        List parts,
	        String[] names,
	        Class[] types) {
	        // get parts in correct order
	        for (int i = 0; i < names.length; ++i) {
	            Part part = (Part) parts.get(i);
	            names[i] = part.getName();
	            System.out.println("Input Parameter Name = "+names[i]);
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
	            } else {
	                throw new RuntimeException(
	                    "part type " + partType + " not supported in this sample");
	            }
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
	
	public static Map<String,String> getInputParametersDetailsMap(Definition def , Operation op)
	{
		Map<String,String> inputParamsMap = new LinkedHashMap<String, String>();
		
		String[] inNames = new String[0];
        Class[] inTypes = new Class[0];
        Input opInput = op.getInput();
        
        try 
        {
			if (opInput != null)
			{
			    List parts = opInput.getMessage().getOrderedParts(null);
			    unWrapIfWrappedDocLit(parts, op.getName(), def);
			    int count = parts.size();
			    inNames = new String[count];
			    inTypes = new Class[count];
			    for (int i = 0; i < inNames.length; ++i)
			    {

		            Part part = (Part) parts.get(i);
		            inNames[i] = part.getName();
//		            System.out.println("Input Parameter Name = "+inNames[i]);
		            QName partType = part.getTypeName();
		            if (partType == null) 
		            {
		               partType = part.getElementName();
		            }
		            if (partType == null) {
		                throw new RuntimeException(
		                    "part " + inNames[i] + " must have type name declared");
		            }
		            // only limited number of types is supported
		            // cheerfully ignoring schema namespace ...
		            String datatype = partType.getLocalPart();
//		            System.out.println("datatype ::::"+datatype);
		            if ("string".equals(datatype)) {
		            	inTypes[i] = String.class;
		            } else if ("double".equals(datatype)) {
		            	inTypes[i] = Integer.TYPE;
		            } else if ("float".equals(datatype)) {
		            	inTypes[i] = Float.TYPE;
		            } else if ("int".equals(datatype)) {
		            	inTypes[i] = Integer.TYPE;
		            } else if ("boolean".equals(datatype)) {
		            	inTypes[i] = Boolean.TYPE;
		            } else {
		                throw new RuntimeException(
		                    "part type " + partType + " not supported in this sample");
		            }
		            
		            inputParamsMap.put(inNames[i], datatype);
		        
			    }
//			    retrieveSignature(parts, inNames, inTypes);
			}
		}
        catch (Exception e) 
		{
			e.printStackTrace();
		}
		return inputParamsMap;
	}
	
	public static void main(String[] args) 
	{
		String wsdlLocation = "http://localhost:8080/webcalculator/services/webcalculator?wsdl";
		try 
		{
			Definition def = WSIFUtils.readWSDL(null, wsdlLocation);
			String serviceName = getServiceName(def);
			System.out.println("Name of Service = "+serviceName);
			
			Service service = WSIFUtils.selectService(def, null, serviceName);
			PortType portType = WSIFUtils.selectPortType(def, null, null);
	        List operationList = portType.getOperations();
//            System.out.println(operationList.size());
	        String operationName = null;
	        for( int i = 0 ; i < operationList.size();i++ )
	        {
	        	Operation op = (Operation) operationList.get(i);
                String name = op.getName();
                System.out.println("Operation Name ::::"+name);
                operationName = name;
                System.out.println(getInputParametersDetailsMap(def, op));
                
	        }
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}

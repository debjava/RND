import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;

import com.rnd.test.CalculatorStub;
import com.rnd.test.CalculatorStub.AddResponse;


public class TestClientInt {

	
	public static void main(String[] args) 
	{
		String endPoint = "http://localhost:8080/axis2/services/Calculator?wsdl";
		try
		{
			CalculatorStub stub = new CalculatorStub(endPoint);
			CalculatorStub.Add add = new CalculatorStub.Add();
			add.setA(10);
			add.setB(20);
			AddResponse res =  stub.add(add);
			System.out.println(res.get_return());
		}
		catch( AxisFault af )
		{
			af.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

}

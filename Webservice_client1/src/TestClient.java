import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis2.AxisFault;

import com.rnd.test.CalculatorStub;
import com.rnd.test.CalculatorStub.AddResponse;


public class TestClient 
{
	
	public static void main(String[] args) throws RemoteException 
	{
		try 
		{
			CalculatorStub stub = new CalculatorStub();
//			CalculatorStub.Add add = new CalculatorStub.Add();
//			add.setA("20");
//			add.setB("10");
//			AddResponse res =  stub.add(add);
//			int value = res.get_return();
//			System.out.println(value);
			
			CalculatorStub.Add add = new CalculatorStub.Add();
			List<Integer> l1 = new ArrayList<Integer>();
			l1.add( new Integer(10));
			l1.add( new Integer(20));
			l1.add( new Integer(30));
			add.setList(l1);
			AddResponse res =  stub.add(add);
			int value = res.get_return();
			System.out.println(value);
			
		} 
		catch (AxisFault e) 
		{
			e.printStackTrace();
		}
	}

}

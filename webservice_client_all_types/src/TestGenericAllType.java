import org.apache.axis2.AxisFault;

import com.rnd.test.CalculatorStub;


public class TestGenericAllType 
{
	public static void main(String[] args) 
	{
		try
		{
			CalculatorStub stub = new CalculatorStub();
			CalculatorStub.GetArrayAddInt ai = new CalculatorStub.GetArrayAddInt();
			int[] a1 = new int[5];
			for( int i=0 ; i < 5 ; i++ )
			{
				int c = i == 0 ? 1 : i;
				System.out.println("C------->"+c);
				a1[i] = c*10;
			}
			ai.setA(a1);
			CalculatorStub.GetArrayAddIntResponse res =  stub.getArrayAddInt(ai);
			System.out.println(res.get_return());
		}
		catch( AxisFault af )
		{
			af.printStackTrace();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}

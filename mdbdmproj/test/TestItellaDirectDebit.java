import java.io.File;
import java.io.FileReader;

import com.ideal.interfacelayer.ItellaDirectDebit;


public class TestItellaDirectDebit 
{
	public static String getFileContetns( String filePath )
	{
		String contents = null;
		try
		{
			File file = new File( filePath );
			FileReader fr = new FileReader( file );
			char[] charBuff = new char[(int)file.length()];
			fr.read( charBuff );
			contents = new String(charBuff);
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return contents;
	}
	
	public static void main(String[] args) 
	{
//		String filePath = "xml/pra/PC_CONTRACT_SUCCESS.xml";
//		String filePath = "xml/pra/ERROR_PC_CONTRACT.xml";
//		String filePath = "xml/pra/PC_DD_SUCCESS.xml";
		String filePath = "xml/pra/SamplePC_DD.xml";
		String xmlFileContents = getFileContetns(filePath);
		ItellaDirectDebit itellaDirectDebit = new ItellaDirectDebit();
		itellaDirectDebit.parseXml(null, xmlFileContents);
	}
}

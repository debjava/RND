import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;


public class FileEncodingConverter 
{
	public static void copyFileInUTF8( String srcPath , String destnPath )
	{
		try
		{
			InputStream inStream = new FileInputStream( srcPath );
			OutputStream outStream = new FileOutputStream( destnPath );
			String fromEncoding = System.getProperty("file.encoding");
			String toEncoding = "UTF-8";

			Reader reader = new BufferedReader(new InputStreamReader(inStream, fromEncoding));
			Writer writer = new BufferedWriter(new OutputStreamWriter(outStream, toEncoding));

			char[] buffer = new char[4096];
			int len;
			while ((len = reader.read(buffer)) != -1)
				writer.write(buffer, 0, len);
			reader.close();
			writer.close();
		}
		catch( FileNotFoundException fnfe )
		{
			fnfe.printStackTrace();
			System.out.println("File is not available in the specified location");
		}
		catch( IOException ie )
		{
			ie.printStackTrace();
			System.out.println("Error in IO operation, contact your operator");
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.out.println("Unexpected error");
		}
	}
	
	public static void main(String[] args) 
	{
		copyFileInUTF8("files/TestHashEuroChar.txt", "files/TestHashEuroChar_UTF8.txt");
	}
}

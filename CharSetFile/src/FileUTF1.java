import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;


public class FileUTF1 
{
	public static void copyFileInUTF8( String srcPath , String destnPath )
	{
		try
		{
			File file = new File( srcPath );
			InputStream in = new FileInputStream( file );
			System.out.println("----->"+in.available());
			OutputStream out = new FileOutputStream( destnPath );
			String from = System.getProperty("file.encoding");
			String to = "UTF-8";
			
			Reader r = new BufferedReader(new InputStreamReader(in, from));
		    Writer w = new BufferedWriter(new OutputStreamWriter(out, to));
		    
		    char[] buffer = new char[4096];
		    int len;
		    while ((len = r.read(buffer)) != -1)
		      // Read a block of input.
		      w.write(buffer, 0, len); // And write it out.
		    r.close(); // Close the input.
		    w.close(); // Flush and close output.


			
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) 
	{
//		copyFileInUTF8("files/Pra.txt", "files/Pra_UTF8.txt");
		copyFileInUTF8("files/Pra2.txt", "files/Pra2_UTF8.txt");
	}
}

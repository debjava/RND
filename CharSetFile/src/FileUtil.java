import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Scanner;


public class FileUtil 
{
	public static byte[] getFileBytes( String filePath )
	{
		byte[] fileContents = null;
		try
		{
			InputStream in = new FileInputStream( filePath );
			fileContents = new byte[in.available()];
			in.read(fileContents, 0, fileContents.length);
			in.close();
		}
		catch( FileNotFoundException fnfe )
		{
			fnfe.printStackTrace();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return fileContents;
	}

	public static String getFileAsString( String filePath )
	{
		StringBuilder builder = new StringBuilder();
		try
		{
			Scanner scanner = new Scanner( new File(filePath) );
			while( scanner.hasNextLine() )
			{
				String str = scanner.nextLine();
				System.out.println(str);
				builder.append(str);
				builder.append("\n");
			}
			scanner.close();
		}
		catch( Exception fnfe )
		{
			fnfe.printStackTrace();
		}
		return builder.toString();
	}

	public static void writeFileInUTF8( String filePath, String contents, String encoding )
	{
		try
		{
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter
					(new FileOutputStream(filePath),encoding));
			out.write(contents);
			out.close();
		}
		catch( FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}
		catch( IOException ie )
		{
			ie.printStackTrace();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	public static void writeFile( String filePath , byte[] fileBytes , String encoding )
	{
		try
		{
			OutputStream out = new FileOutputStream( filePath );
			out.write(fileBytes);
			out.close();
		}
		catch( FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}
		catch( IOException ie )
		{
			ie.printStackTrace();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	public static void copy(ReadableByteChannel channel, Writer writer, Charset charset)
	throws IOException {
		// Get and configure the CharsetDecoder we'll use
		CharsetDecoder decoder = charset.newDecoder();
		decoder.onMalformedInput(CodingErrorAction.IGNORE);
		decoder.onUnmappableCharacter(CodingErrorAction.IGNORE);

		// Get the buffers we'll use, and the backing array for the CharBuffer.
		ByteBuffer bytes = ByteBuffer.allocateDirect(2 * 1024);
		CharBuffer chars = CharBuffer.allocate(2 * 1024);
		char[] array = chars.array();

		while (channel.read(bytes) != -1) { // Read from channel until EOF
			bytes.flip(); // Switch to drain mode for decoding
			// Decode the byte buffer into the char buffer.
			// Pass false to indicate that we're not done.
			decoder.decode(bytes, chars, false);

			// Put the char buffer into drain mode, and write its contents
			// to the Writer, reading them from the backing array.
			chars.flip();
			writer.write(array, chars.position(), chars.remaining());

			// Discard all bytes we decoded, and put the byte buffer back into
			// fill mode. Since all characters were output, clear that buffer.
			bytes.compact(); // Discard decoded bytes
			chars.clear(); // Clear the character buffer
		}

		// At this point there may still be some bytes in the buffer to decode
		// So put the buffer into drain mode call decode() a final time, and
		// finish with a flush().
		bytes.flip();
		decoder.decode(bytes, chars, true); // True means final call
		decoder.flush(chars); // Flush any buffered chars
		// Write these final chars (if any) to the writer.
		chars.flip();
		writer.write(array, chars.position(), chars.remaining());
		writer.flush();
	}


	public static void main(String[] args) throws Exception
	{
		String fileName = "files/LMP300090323205212.txt";
		//		byte[] fileBytes = getFileBytes(fileName);
		String fileContents = getFileAsString(fileName);
		System.out.println("All Files---->>\n"+fileContents);
		FileChannel c = new FileInputStream(fileName).getChannel();
		OutputStream os = new FileOutputStream("files/test1_UTF8.txt");
//		OutputStreamWriter w = new OutputStreamWriter(System.out);
		OutputStreamWriter w = new OutputStreamWriter(os);
		Charset utf8 = Charset.forName("UTF-8");
	    copy(c, w, utf8);
	    c.close();
	    w.close();

//	    System.out.println(System.getProperty("file.encoding"));
		
		
		
		//		writeFileInUTF8("files/LMP300090323205212_UTF8.txt", fileContents, "UTF-8");

		//		File file = new File("files/LMP300090324174505_File2UTF2.txt");
		//		System.out.println("--------------In Normal form---------------------");
		//		String normalContents = new String( fileBytes );
		//		System.out.println(normalContents);
		//		System.out.println("--------------In Normal form---------------------");
		//		
		//		System.out.println("--------------In UTF8 form---------------------");
		//		String utf8Contents = new String( fileBytes , "UTF-8");
		//		System.out.println(utf8Contents);
		//		System.out.println("--------------In UTF8 form---------------------");
		//		String newUTF = new String( normalContents.getBytes("UTF-8") );
		//		System.out.println(newUTF);
		//		System.out.println("-------------------");
		//		String ss = new String( fileBytes,"ISO8859_1");
		//		System.out.println(ss);
		//		byte[] newBytes = ss.getBytes("UTF-8");
		//		writeFile("files/testutf1.txt", fileBytes, null);
		//		String contentsOfFile = new String( dataFileByteArray,0,dataFileByteArray.length,"UTF-8");

		//		BufferedWriter out = new BufferedWriter(new OutputStreamWriter
		//                (new FileOutputStream(file),"UTF8"));

		//		out.write("WelCome to RoseIndia.Net");







	}
}

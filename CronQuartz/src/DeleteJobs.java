import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class DeleteJobs implements Job 
{
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException 
	{
		System.out.println("DeleteJobs : execute - Quartz scheduler for directory deletion is running");
		String filepath = (String) jobExecutionContext.getJobDetail()
		.getJobDataMap().get("pathToDelete");
		System.out.println("Directory path to clean up------->"+filepath);
		try
		{
			if( filepath == null )
				throw new NullPointerException();
			else
				cleanupDirectory(filepath);
		}
		catch( NullPointerException npe )
		{
			System.out.println("File path is null, temp directory inside DMZ can not be cleaned up");
		}
		catch( Exception e )
		{
			System.out.println("Other exception thrown while temp directory clean up ");
		}
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyy:hh:mm:ss");
		System.out.println("Date and time in directory clean up::::"+sdf.format(date));
	}

	private void cleanupDirectory( String path )
	{
		File file = new File( path ) ;
		try
		{
			File[] files = file.listFiles();
			for( int i = 0,n = files.length ; i < n ; i++ )
			{
				deleteDir( files[i] );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.out
			.println("Error in deltion of files and folders\n possible " +
					"error may be due to insufficient file previllages" +
			" in the directory");
		}
	}

	/**This method is used to delete the folders recurrsively.
	 * @param dir of type {@link File}
	 * @return true if the folder or file is deleted successfully
	 * @throws Exception
	 */
	public static boolean deleteDir(File dir) throws Exception
	{
		if (dir.isDirectory()) 
		{
			String[] children = dir.list();
			for (int i=0; i<children.length; i++)
			{
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) 
				{
					return false;
				}
			}
		}
		return dir.delete();
	}

}

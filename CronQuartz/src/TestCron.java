
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;


public class TestCron 
{
	public static void execute()
	{
		SchedulerFactory sf = new StdSchedulerFactory();
		try 
		{
			Scheduler sched = sf.getScheduler();
			sched.start();
			JobDetail job = new JobDetail("deleteJob1", "deleteGroup1", DeleteJobs.class);
			CronTrigger trigger = new CronTrigger("deleteTrigger", "deleteGroup1", "deleteJob1", "deleteGroup1", "0 59 23 * * ?");
			
			JobDataMap jobDataMap = job.getJobDataMap();
			jobDataMap.put("pathToDelete", "C:/tt/temp");
			sched.scheduleJob(job, trigger);
			
		
			
		}
		catch (SchedulerException e) 
		{
			e.printStackTrace();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) 
	{
		execute();
		
//		File file1 = new File("");
//		System.out.println("Simple path :::"+file1.getAbsolutePath());
//		System.out.println( "User dir path :::"+System.getProperty("user.dir") );
		
	}

}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class TestDB
{
	public static Connection getConnection()
	{
		Connection conn = null;
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@10.70.24.9:1521:DNBDM", "EEDMDV31","EEDMDV31");
		}
		catch( SQLException se )
		{
			se.printStackTrace();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return conn;
	}
	
	private static void executeQuery()
	{
		Connection conn = getConnection();
		try
		{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM TAB");
		}
		catch( SQLException se )
		{
			se.printStackTrace();
		}
	}
	
	public static void main(String[] args) 
	{
		for( int i = 0 ; i < 1000 ; i++ )
		{
			executeQuery();
			System.out.println(i+"   Executed successfully");
		}
	}

}

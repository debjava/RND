import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.sql.PooledConnection;

import oracle.jdbc.pool.OracleConnectionPoolDataSource;


public class DBUtil 
{
	public static Connection getPooledDbConnection(String userName,
			String password, String dbURL) throws Exception {
		OracleConnectionPoolDataSource ocpds = new OracleConnectionPoolDataSource();
		// Set connection parameters
		ocpds.setURL(dbURL);
		ocpds.setUser(userName);
		ocpds.setPassword(password);
		// Create a pooled connection
		PooledConnection pc = ocpds.getPooledConnection();
		// Get a Logical connection
		Connection conn = pc.getConnection();
		return conn;
	}
	
	public static Connection getConnection() throws Exception
	{
		String dbURL = new StringBuffer("jdbc:oracle:thin:@").
		append("10.70.24.9")
		.append(":").append("1521").
		append(":").append("DNBDM")
		.toString();
		Connection conn = getPooledDbConnection("LVDMDVIF", "LVDMDVIF", dbURL);
		return conn;
	}
	
	public static void insertRecord( String data )
	{
		try
		{
			Connection conn = getConnection();
			System.out.println("Connection------->"+conn);
			String queryString = "insert into testcharset_tab values(?)";
			PreparedStatement pstmt = conn.prepareStatement(queryString);
			pstmt.setString(1, data);
			boolean executeFlag = pstmt.execute();
			System.out.println("Query executed successfully and flag------>"+executeFlag);
			pstmt.close();
			conn.commit();
			conn = null;
		}
		catch( SQLException se )
		{
			se.printStackTrace();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public static List<String> getDataFromDB()
	{
		List<String> dataList = new LinkedList<String>();
		try
		{
			Connection conn = getConnection();
			String queryString = "select * from TESTCHARSET_TAB";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(queryString);
			while( rs.next())
			{
				dataList.add( rs.getString(1));
			}
		}
		catch( SQLException se )
		{
			se.printStackTrace();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return dataList;
	}


}

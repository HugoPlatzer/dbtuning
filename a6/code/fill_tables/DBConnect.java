/*
Fill the database with 100k employees, 100k students, and 10 technical departments.

Only  about  10%  of  the  employees  are  in  a  technical  department.

The types of the attributes should make sense (e.g., ssnum should be an integer),
but the values need not be meaningful (e.g., names can be random strings).
*/

import java.sql.*;
import java.util.Random;

public class DBConnect
{
	private static Random random = new Random();	
	
	public static void main(String[] args) throws SQLException
	{
		String host = "biber.cosy.sbg.ac.at";
		String port = "5432";
		String database = "dbtuning_ws2016";
		String user = "hplatzer";
		String pwd = "Aicae4paed4e";
		String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;

		try (Connection con = DriverManager.getConnection(url, user, pwd);)
		{
		    String query = "DELETE FROM Accounts;";
		    con.createStatement().executeUpdate(query);
		
			insertAccounts(con);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private static void insertAccounts(Connection con) throws SQLException
	{
		String query = "INSERT INTO Accounts (account, balance) values (?, ?)";
		PreparedStatement stmt = con.prepareStatement(query);

		//Company account
		stmt.setInt(1, 0);
		stmt.setInt(2, 100);
		stmt.addBatch();
		
		for(int i = 1; i <= 100; i++)
		{
			stmt.setInt(1, i);
			stmt.setInt(2, 0);
			stmt.addBatch();
		}
		
		stmt.executeBatch();
		stmt.close();
	}
}

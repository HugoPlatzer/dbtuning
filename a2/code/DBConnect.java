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
		int batchSize = 10000;

		try (Connection con = DriverManager.getConnection(url, user, pwd);)
		{
			insertTechdepts(con, batchSize);
			insertEmployees(con, batchSize);
			insertStudents(con, batchSize);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private static void insertEmployees(Connection con, int batchSize) throws SQLException
	{
		String query = "INSERT INTO Employee (ssnum, name, manager, dept, salary, numfriends) values (?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = con.prepareStatement(query);
		
		for(int i = 0; i < 100000; i++)
		{
			stmt.setInt(1, randomNum(1000000));
			stmt.setString(2, randomName(15));
			stmt.setString(3, randomManager());
			
			if(random.nextDouble() <= 0.1)
			{
				stmt.setString(4, dept(random.nextInt(10)));
			}
			else
			{
				stmt.setString(4, null);
			}
			stmt.setInt(5, randomNum(100000));
			stmt.setInt(6, randomNum(100));
			stmt.addBatch();
			
			if (i % batchSize == 0)
			{
				stmt.executeBatch();
			}
		}
		
		stmt.executeBatch();
		stmt.close();
	}
	
	private static void insertStudents(Connection con, int batchSize) throws SQLException
	{
		String query = "INSERT INTO Student (ssnum, name, course, grade) values (?, ?, ?, ?)";
		PreparedStatement stmt = con.prepareStatement(query);
		
		for(int i = 0; i < 100000; i++)
		{
			stmt.setInt(1, randomNum(1000000));
			stmt.setString(2, randomName(15));
			stmt.setString(3, randomName(25));
			stmt.setInt(4, randomNum(5));
			stmt.addBatch();
			
			if (i % batchSize == 0)
			{
				stmt.executeBatch();
			}
		}
		
		stmt.executeBatch();
		stmt.close();
	}
	
	private static void insertTechdepts(Connection con, int batchSize) throws SQLException
	{
		String query = "INSERT INTO Techdept (dept, manager, location) values (?, ?, ?)";
		PreparedStatement stmt = con.prepareStatement(query);
		
		for(int i = 0; i < 10; i++)
		{
			stmt.setString(1, dept(i));
			stmt.setString(2, randomManager());
			stmt.setString(3, randomName(25));
			stmt.addBatch();
			
			if (i % batchSize == 0)
			{
				stmt.executeBatch();
			}
		}
		
		stmt.executeBatch();
		stmt.close();
	}
	
	private static int randomNum(int max)
	{
		return random.nextInt(max) + 1;
	}
	
	private static String randomName(int length)
	{
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < length; i++)
		{
    		char c = chars[random.nextInt(chars.length)];
    		sb.append(c);
		}
		
		return sb.toString();
	}
	
	private static String dept(int pos)
	{
		String[] depts = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
		
		return depts[pos];
	}
	
	private static String randomManager()
	{
		String[] managers = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
													"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
													
		return managers[random.nextInt(managers.length)];
	}
}

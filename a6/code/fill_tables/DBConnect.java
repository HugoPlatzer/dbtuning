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
		int n = 100000;

		try (Connection con = DriverManager.getConnection(url, user, pwd);)
		{
		    String query = "DELETE FROM Employee; DELETE FROM Techdept; Delete from Student;";
		    con.createStatement().executeUpdate(query);
		
			insertTechdepts(con);
			insertEmployees(con, n);
			insertStudents(con, n);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private static void insertEmployees(Connection con, int n) throws SQLException
	{
		String query = "INSERT INTO Employee (ssnum, name, manager, dept, salary, numfriends) values (?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = con.prepareStatement(query);
		
		for(int i = 0; i < n; i++)
		{
			stmt.setInt(1, i + 1);
			stmt.setString(2, randomName(20));
			stmt.setString(3, "Mgr"+randomUppercaseLetter());
			stmt.setString(4, randomUppercaseLetter());
			stmt.setInt(5, randomNum(100000));
			stmt.setInt(6, randomNum(100));
			stmt.addBatch();
		}
		
		stmt.executeBatch();
		stmt.close();
	}
	
	private static void insertStudents(Connection con, int n) throws SQLException
	{
		String query = "INSERT INTO Student (ssnum, name, course, grade) values (?, ?, ?, ?)";
		PreparedStatement stmt = con.prepareStatement(query);
		
		for(int i = 0; i < n; i++)
		{
			stmt.setInt(1, i + 1);
			stmt.setString(2, randomName(20));
			stmt.setString(3, randomName(5));
			stmt.setInt(4, randomNum(5));
			stmt.addBatch();
		}
		
		stmt.executeBatch();
		stmt.close();
	}
	
	private static void insertTechdepts(Connection con) throws SQLException
	{
		String query = "INSERT INTO Techdept (dept, manager, location) values (?, ?, ?)";
		PreparedStatement stmt = con.prepareStatement(query);
		
		for(int i = 0; i < 3; i++)
		{
			stmt.setInt(1, i + 1);
			stmt.setString(2, "Mgr" + randomUppercaseLetter());
			stmt.setString(3, randomName(3));
			stmt.addBatch();
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
	
	private static String randomUppercaseLetter()
	{
		String[] managers = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
													"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
													
		return managers[random.nextInt(managers.length)];
	}
}

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

public class DBConnect {
	public static void main(String[] args) throws IOException {
		String host = "biber.cosy.sbg.ac.at";
		String port = "5432";
		String database = "dbtuning_ws2016";
		String user = "hplatzer";
		String pwd = "Aicae4paed4e";
		String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;

		try (Connection con = DriverManager.getConnection(url, user, pwd);
				Statement stmt = con.createStatement()) {
			long startTime = System.nanoTime();
			
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM publ;");
			if (rs.next()) {
				System.out.println(rs.getInt(1));
			}
			
			System.out.println((System.nanoTime() - startTime) / 1000000 + "ms");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

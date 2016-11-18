import java.io.*;
import java.sql.*;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import java.util.ArrayList;
import java.util.List;


public class DBConnect {
	public static List<String> readFile(String filename) {
		List<String> lines = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty()) {
					lines.add(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public static void main(String[] args) throws IOException {
		String host = "biber.cosy.sbg.ac.at";
		String port = "5432";
		String database = "dbtuning_ws2016";
		String user = "hplatzer";
		String pwd = "Aicae4paed4e";
		String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
		List<String> lines = readFile("pubid_lowest.txt");
		String query = "SELECT * FROM publ WHERE pubid=?";

		try (Connection con = DriverManager.getConnection(url, user, pwd);
				PreparedStatement stmt = con.prepareStatement(query)) {
			long startTime = System.nanoTime();
			
			for (String line : lines) {
				System.out.println("[" + line + "]");
				stmt.setString(1, line);
				ResultSet rs = stmt.executeQuery();
				rs.next();
				System.out.println(rs.getString(2));
			}
			
			System.err.println((System.nanoTime() - startTime) / 1000000 + "ms");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

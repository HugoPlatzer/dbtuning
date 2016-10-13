import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

public class DBConnect {

	public static void printResultSetRow(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			if (i > 1) {
				sb.append(", ");
			}
			sb.append(rsmd.getColumnName(i) + " = " + rs.getString(i));
		}
		System.out.println(sb);
	}

	public static void printResultSet(ResultSet rs) throws SQLException {
		while (rs.next()) {
			printResultSetRow(rs);
		}
	}

	public static void insertSimple(Connection con, TSVParser parser, int n) throws SQLException, IOException {
		String query = "INSERT INTO auth (name, pubid) values (?, ?)";
		PreparedStatement stmt = con.prepareStatement(query);
		String[] data = parser.parseLine();
		int i = 0;
		while (data != null && (n == -1 || i < n)) {
			stmt.setString(1, data[0]);
			stmt.setString(2, data[1]);
			stmt.executeUpdate();
			i++;
			data = parser.parseLine();
		}
		stmt.close();
	}

	public static void insertBatch(Connection con, TSVParser parser, int n, int batchSize)
			throws SQLException, IOException {
		String query = "INSERT INTO auth (name, pubid) values (?, ?)";
		PreparedStatement stmt = con.prepareStatement(query);
		String[] data = parser.parseLine();
		int i = 0, count = 0;
		while (data != null && (n == -1 || i < n)) {
			stmt.setString(1, data[0]);
			stmt.setString(2, data[1]);
			stmt.addBatch();
			if (++count % batchSize == 0) {
				stmt.executeBatch();
				count = 0;
			}
			i++;
			data = parser.parseLine();
		}
		stmt.executeBatch();
		stmt.close();
	}
	
	public static void insertCopyFrom(Connection con, String filename) throws SQLException, IOException {
		CopyManager copyManager = new CopyManager((BaseConnection) con);
		FileReader fileReader = new FileReader(filename);
		
		copyManager.copyIn("COPY auth from STDIN WITH DELIMITER '\t'", fileReader);
	}

	public static void main(String[] args) throws IOException {
		String host = "biber.cosy.sbg.ac.at";
		String port = "5432";
		String database = "dbtuning_ws2016";
		String user = "hplatzer";
		String pwd = "Aicae4paed4e";
		String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
		String filename = "../../data/auth.tsv";
		TSVParser parser = new TSVParser(filename);
		int n = 10000;
		int batchSize = 10000;
		
		try (Connection con = DriverManager.getConnection(url, user, pwd);) {
			long startTime = System.nanoTime();
			
//			insertSimple(con, parser, n);
//			insertBatch(con, parser, -1, batchSize);
			insertCopyFrom(con, filename);
			
			System.out.println((System.nanoTime() - startTime) / 1000000 + "ms");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		parser.close();
	}
}

import java.io.IOException;
import java.sql.*;

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
		for (int i = 0; i < n; i++) {
			String[] data = parser.parseLine();
			stmt.setString(1, data[0]);
			stmt.setString(2, data[1]);
			stmt.executeUpdate();
		}
		stmt.close();
	}

	public static void insertBatch(Connection con, TSVParser parser, int n, int batchSize)
			throws SQLException, IOException {
		String query = "INSERT INTO auth (name, pubid) values (?, ?)";
		PreparedStatement stmt = con.prepareStatement(query);
		int count = 0;
		for (int i = 0; i < n; i++) {
			String[] data = parser.parseLine();
			stmt.setString(1, data[0]);
			stmt.setString(2, data[1]);
			stmt.addBatch();

			if (++count % batchSize == 0) {
				stmt.executeBatch();
				count = 0;
			}
		}
		stmt.executeBatch();
		stmt.close();
	}

	public static void main(String[] args) throws IOException {
		String host = "biber.cosy.sbg.ac.at";
		String port = "5432";
		String database = "dbtuning_ws2016";
		String user = "hplatzer";
		String pwd = "Aicae4paed4e";
		String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;

		try (Connection con = DriverManager.getConnection(url, user, pwd);) {

			TSVParser parser = new TSVParser("../../data/auth.tsv");
			long startTime = System.nanoTime();
			
			insertBatch(con, parser, 100, 10);
			
			System.out.println((System.nanoTime() - startTime) / 1000000 + "ms");
			parser.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

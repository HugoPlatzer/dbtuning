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

    public static void main(String[] args) throws IOException {
        String host = "biber.cosy.sbg.ac.at";
        String port = "5432";
        String database = "dbtuning_ws2016";
        String user = "hplatzer";
        String pwd = "Aicae4paed4e";
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
        String query = "INSERT INTO auth (pubid, name) values (?, ?)";
        
        //Normales Insert
        try (Connection con = DriverManager.getConnection(url, user, pwd);){
                PreparedStatement stmt = con.prepareStatement(query);
        		TSVParser parser = new TSVParser("auth.tsv");
        		long startTime = System.nanoTime();
    		
        		for(int i = 0; i < 10000; i++){
        			String[] data = parser.parseLine();
        		
        			stmt.setInt(1, Integer.parseInt(data[0]));
        			stmt.setString(2, data[1]);
	            	stmt.executeUpdate();
        		}
        	
        		System.out.println((System.nanoTime() - startTime) / 1000000 + "ms");
        		parser.close();
        		stmt.close();
        		con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        //Batch Insert
        try (Connection con = DriverManager.getConnection(url, user, pwd);){
        	PreparedStatement stmt = con.prepareStatement(query);
        	final int batchSize = 1000;
        	int count = 0;
        	TSVParser parser = new TSVParser("auth.tsv");

        	long startTime = System.nanoTime();
        	for(int i = 0; i < 10000; i++){
        		String[] data = parser.parseLine();
        	
	            stmt.setInt(1, Integer.parseInt(data[0]));
	            stmt.setString(2, data[1]);
	            stmt.addBatch();
	            
	            if(++count % batchSize == 0){
	            	stmt.executeBatch();
	            	count = 0;
	            }
        	}

	        parser.close();
	        stmt.executeBatch();
	       	stmt.close();
	       	con.close();
       } catch (SQLException e) {
           e.printStackTrace();
       }
    }
}

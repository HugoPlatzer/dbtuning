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

    public static void main(String[] args) {
        String host = "biber.cosy.sbg.ac.at";
        String port = "5432";
        String database = "dbtuning_ws2016";
        String user = "hplatzer";
        String pwd = "Aicae4paed4e";
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
        String query = "INSERT INTO auth (pubid, name) values (?, ?)";
        
        try (    Connection con = DriverManager.getConnection(url, user, pwd);
                 PreparedStatement stmt = con.prepareStatement(query);) {
        	for(int i = 0; i < 10000; i++){
        		//Daten einlesen
	            stmt.setInt(1, id);
	            stmt.setString(2, name);
	            stmt.executeUpdate();
        	}
        	
        	stmt.close();
        	con.close();
            
            System.out.println("Hi");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        try (    Connection con = DriverManager.getConnection(url, user, pwd);
                PreparedStatement stmt = con.prepareStatement(query);) {
        		final int batchSize = 1000;
        		int count = 0;
        	for(int i = 0; i < 10000; i++){
       		//Daten einlesen
	            stmt.setInt(1, id);
	            stmt.setString(2, name);
	            stmt.addBatch();
	            
	        if(++count % batchSize == 0){
	        	stmt.executeBatch();
	        }
       	}
        stmt.executeBatch();
       	stmt.close();
       	con.close();
           
           System.out.println("Hi");
       } catch (SQLException e) {
           e.printStackTrace();
       }
    }
}

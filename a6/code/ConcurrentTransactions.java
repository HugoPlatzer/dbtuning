/*
 * Example code for Assignment 6 (concurrency tuning) of the course:
 * 
 * Database Tuning
 * Department of Computer Science
 * University of Salzburg, Austria
 * 
 * Lecturer: Nikolaus Augsten
 */
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.*;

/** 
 * Dummy transaction that prints a start message, waits for a random time 
 * (up to 100ms) and finally prints a status message at termination.
 */
class Transaction extends Thread {

	// identifier of the transaction
	int id;
	
	Transaction(int id) {
		this.id = id;
	}
	
	private void runTransaction(Connection con) throws SQLException {
				int e = 0, c = 0;
				System.out.println("transaction " + id + " started");
				con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				con.setAutoCommit(false);
				

				String query = "SELECT balance FROM Accounts WHERE account=?";
				PreparedStatement stmt = con.prepareStatement(query);
				stmt.setInt(1, id);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					e = rs.getInt(1);
				}

				query = "UPDATE Accounts SET balance=? WHERE account=?";
				stmt = con.prepareStatement(query);
				stmt.setInt(1, e+1);
				stmt.setInt(2, id);
				stmt.executeUpdate();

				query = "SELECT balance FROM Accounts WHERE account=?";
				stmt = con.prepareStatement(query);
				stmt.setInt(1, 0);
				rs = stmt.executeQuery();
				while (rs.next()) {
					c = rs.getInt(1);
				}

				query = "UPDATE Accounts SET balance=? WHERE account=?";
				stmt = con.prepareStatement(query);
				stmt.setInt(1, c-1);
				stmt.setInt(2, 0);
				stmt.executeUpdate();

				con.commit();

				System.out.println("transaction " + id + " successful");
	}
	
	@Override
	public void run() {
		String host = "biber.cosy.sbg.ac.at";
		String port = "5432";
		String database = "dbtuning_ws2016";
		String user = "hplatzer";
		String pwd = "Aicae4paed4e";
		String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
		Connection con = null;
		try {
			con = DriverManager.getConnection(url, user, pwd);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		boolean didComplete = false;
		while (!didComplete) {
				didComplete = true;
				try {
					runTransaction(con);
				} catch (SQLException e) {
					System.out.println("transaction " + id + " rolled back");
					didComplete = false;
					//e.printStackTrace();
					try {
						con.rollback();
					} catch (SQLException e2) {
						e2.printStackTrace();
					}
				}
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}	
	
}

/**
 * <p>
 * Run numThreads transactions, where at most maxConcurrent transactions 
 * can run in parallel.
 * 
 * <p>params: numThreads maxConcurrent
 *
 */
public class ConcurrentTransactions {

	public static void main(String[] args) {
		// read command line parameters
		if (args.length != 2) {
			System.out.println("params: numThreads maxConcurrent");
			System.exit(-1);
		}	
		int numThreads = Integer.parseInt(args[0]);
		int maxConcurrent = Integer.parseInt(args[1]);

		// create numThreads transactions
		Transaction[] trans = new Transaction[numThreads];
		for (int i = 0; i < trans.length; i++) {
			trans[i] = new Transaction(i + 1);
		}

		// start all transactions using a thread pool 
		ExecutorService pool = Executors.newFixedThreadPool(maxConcurrent);				
		for (int i = 0; i < trans.length; i++) {
			pool.execute(trans[i]);
		}		
		pool.shutdown(); // end program after all transactions are done
	}
}
 

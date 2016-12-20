/*
 * Example code for Assignment 6 (concurrency tuning) of the course:
 * 
 * Database Tuning
 * Department of Computer Science
 * University of Salzburg, Austria
 * 
 * Lecturer: Nikolaus Augsten
 */
package account;

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
	Connection con;
	
	Transaction(int id, Connection con) {
		this.id = id;
		this.con = con;
	}
	
	@Override
	public void run() {
		System.out.println("transaction " + id + " started");
		
		String query = "declare @e varchar, @c varchar; SELECT balance FROM Accounts WHERE account=?;
		PreparedStatement stmt = con.prepareStatement(query);
		
		System.out.println("transaction " + id + " terminated");
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
		String host = "biber.cosy.sbg.ac.at";
		String port = "5432";
		String database = "dbtuning_ws2016";
		String user = "hplatzer";
		String pwd = "Aicae4paed4e";
		String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;

		// read command line parameters
		if (args.length != 2) {
			System.err.println("params: numThreads maxConcurrent");
			System.exit(-1);
		}	
		int numThreads = Integer.parseInt(args[0]);
		int maxConcurrent = Integer.parseInt(args[1]);

		try (Connection con = DriverManager.getConnection(url, user, pwd);) {
			// create numThreads transactions
			Transaction[] trans = new Transaction[numThreads];
			for (int i = 0; i < trans.length; i++) {
				trans[i] = new Transaction(i + 1, con);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// start all transactions using a thread pool 
		ExecutorService pool = Executors.newFixedThreadPool(maxConcurrent);				
		for (int i = 0; i < trans.length; i++) {
			pool.execute(trans[i]);
		}		
		pool.shutdown(); // end program after all transactions are done	
		
	}
}
 

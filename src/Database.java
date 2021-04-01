package currencyExchange;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;


public class Database {
	
	// PLEASE NOTE! In order to successfully connect to the database, set username and password according to your root user settings
	
	private static String jdbcURL = "jdbc:postgresql://localhost:5432/currencyexchange";
	private static String username = "postgres";	//change to your username
	private static String password = "admin"; 		//change to your password
	
	// This method is used to establish a connection to the PostgreSQL database created by setup.sql file
	// Returns established connection if it was established correctly, null otherwise
	public static Connection getConnection() {
		
		try {
			Connection con = DriverManager.getConnection(jdbcURL, username, password);
			System.out.println("Database connection successful!\n");
			return con;
			
		} catch (SQLException e) {
			System.out.println("Error in connecting to database.");
			e.printStackTrace();
		}
		
		return null;
	}
	
	// Method that executes sql statements, it accepts String sql (sql query) and Connection con (connection setablished to the database) as parameters
	// Returns 1 if query was executed successfully and 0 if it had an error
	public static int executeSql(String sql, Connection con) {
		Statement statement;
		
		try {
			statement = con.createStatement();
			statement.executeUpdate(sql);
			
			System.out.println("Success!");
			return 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	// This method is used to check if the currency code users enter to modify or delete currencies and exchange rates really exists in our database
	public static boolean checkIfExists(Connection con, int check, String sql) {
		Statement statement;
		Set <Integer> ids = new HashSet<Integer>();
		ResultSet rs;
				
		try {
			statement = con.createStatement();
			rs = statement.executeQuery(sql);
			
			while(rs.next()) {
				ids.add(rs.getInt("currency_id"));
				
				if (ids.contains(check)) {
					return true;
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	// Method that deletes everything except the base currency data
	public static void resetDatabase(Connection con) {
		String sql = "DELETE FROM exchange_transactions;\n"
				+ "DELETE FROM exchange_rate WHERE currency_id != 0;\n"
				+ "DELETE FROM currency WHERE currency_id != 0;";
		
		executeSql(sql, con);
	}
}

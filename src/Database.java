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
	
	// Method that prints currencies and their exchange rates
	public static void printList(Connection con) {
		// Creating a view table with all the neccesery data
		String createViewTable = "CREATE OR REPLACE VIEW exchange_table AS\n"
				+ " SELECT t2.currency_id,\n"
				+ "    t1.symbol,\n"
				+ "    t1.currency_name,\n"
				+ "    t1.country,\n"
				+ "    t2.unit,\n"
				+ "    t2.buying_rate,\n"
				+ "    t2.selling_rate\n"
				+ "   FROM currency t1,\n"
				+ "    exchange_rate t2\n"
				+ "  WHERE t1.currency_id = t2.currency_id;";
		
		executeSql(createViewTable, con);
		
		try {
			String viewQuery = "SELECT * FROM exchange_table;";
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(viewQuery);
			
			System.out.println("CODE\t\tNAME\t\tSYMBOL\t\tCOUNTRY\t\tUNIT\t\tBUY RATE\t\tSELL RATE\n");
			
			while(rs.next()) {
				int currencyCode = rs.getInt("currency_id");
				String currencySymbol = rs.getString("symbol");
				String currencyName = rs.getString("currency_name");
				String currencyCountry = rs.getString("country");
				int currencyUnit = rs.getInt("unit");
				double buyRate = rs.getDouble("buying_rate");
				double sellRate = rs.getDouble("selling_rate");
				
				System.out.printf("%-4d\t\t%-14s\t%-15s\t%-15s\t%-3d\t\t%-7f\t\t%-7f\n", currencyCode, currencyName, currencySymbol, currencyCountry, currencyUnit, buyRate, sellRate);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	// This method prints data from transactions table
	public static void printTransactions(Connection con) {
		String sql = "CREATE OR REPLACE VIEW transactions AS\n"
				+	"SELECT currency.currency_id, currency.symbol, exchange_transactions.amount_bought, exchange_transactions.amount_sold, exchange_transactions.transaction_type \n"
				+	"FROM exchange_transactions\n"
				+	"LEFT JOIN currency ON exchange_transactions.currency_id = currency.currency_id;";
		
		executeSql(sql, con);
		
		try {
			String viewQuery = "SELECT * FROM transactions;";
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(viewQuery);
			
			System.out.println("CODE\t\tSYMBOL\t\tAMOUNT BOUGHT\t\tAMOUNT SOLD\t\tTRANSACTION TYPE\n");
			
			while(rs.next()) {
				int currencyCode = rs.getInt("currency_id");
				String currencySymbol = rs.getString("symbol");
				double amountBought = rs.getDouble("amount_bought");
				double amountSold = rs.getDouble("amount_sold");
				String transactionType = rs.getString("transaction_type");
				
				System.out.printf("%-4d\t\t%-3s\t\t%-7f\t\t%-7f\t\t%-6s\n", currencyCode, currencySymbol, amountBought, amountSold, transactionType);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

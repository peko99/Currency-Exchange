package currencyExchange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ExchangeRate extends Currency {

	private int unit;
	private double buyRate;
	private double sellRate;
	
	public ExchangeRate(int currencyCode, String currencySymbol, String currencyName, String currencyCountry, int currencyUnit, double buyRate, double sellRate) {
		super(currencyCode, currencyName, currencyCountry, currencySymbol);
		this.unit = currencyUnit;
		this.buyRate = buyRate;
		this.sellRate = sellRate;
	}
	
	public int getUnit() {
		return this.unit;
	}
	
	public void setUnit(int unit) {
		this.unit = unit;
	}
	
	public double getBuyRate() {
		return this.buyRate;
	}
	
	public void setBuyRate(double buyRate) {
		this.buyRate = buyRate;
	}
	
	public double getSellRate() {
		return this.sellRate;
	}
	
	public void setSellRate(double sellRate) {
		this.sellRate = sellRate;
	}
	
	// This method adds exchange to the 'exchange_rate' table in the database
	// It takes connection to the database and a currency object which needs to be added as parameters
	public static void addNewExchange(Connection con, Currency toBeAdded, Scanner input) {
		
		System.out.print("Enter the currency unit [INT]: ");
		int unit = input.nextInt();		
		System.out.print("Enter buying rate (against EUR) [DOUBLE . ]: ");
		double buyRate = input.nextDouble();		
		System.out.print("Enter selling rate (against EUR) [DOUBLE . ]: ");
		double sellRate = input.nextDouble();
		
		String sql = "INSERT INTO exchange_rate (currency_id, unit, buying_rate, selling_rate)" +
		"VALUES (" + toBeAdded.getCurrencyCode() + ", " + unit + ", " + buyRate + ", " + sellRate + ");";	
					
		Database.executeSql(sql, con);
	}
	
	// This method collects data about a new exchange for an existing currency in the database
	// It is run from the main class
	public static void addExchange(Connection con, Scanner input) {
		
		System.out.print("Enter code of the currency for which you want to add exchange rate [INT]: ");
		int chosenCurrencyCode = input.nextInt();
		
		String sql = "SELECT currency_id FROM currency;";
		if(!Database.checkIfExists(con, chosenCurrencyCode, sql)) {
			System.out.print("Please check if the currency code you entered is correct!\n");
			
			return;
		}
		
		String currencyQuery = "SELECT * FROM currency WHERE currency_id = " + chosenCurrencyCode + ";";
		
		try {
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery(currencyQuery);
		
			if(rs.next()) {
				int currencyCode = rs.getInt("currency_id");
				String currencyName = rs.getString("currency_name");
				String currencyCountry = rs.getString("country");
				String currencySymbol = rs.getString("symbol");			
				
				// After users input the code of the currency for which they want to add exchange data, we collect the rest of the currency data from the database
				// Then we create currency object and send it to addNewExchange method to add exchange data for selected currency
				Currency toBeAdded = new Currency(currencyCode, currencyName, currencyCountry, currencySymbol);
				addNewExchange(con, toBeAdded, input);
			} 			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// This method allows users to modify existing exchange rates 
	public static void modifyExchange(Connection con, Scanner input) {
		
		System.out.print("Enter ID of the currency for which you want to modify the exchange rate [INT]: ");
		int chosenCurrencyCode = input.nextInt();
		
		String sql = "SELECT currency_id FROM exchange_rate;";
		if(!Database.checkIfExists(con, chosenCurrencyCode, sql)) {
			System.out.print("Please check if the currency code you entered is correct!\n");
			
			return;
		}
		
		System.out.print("Enter new unit for chosen currency [INT]: ");
		int newUnit = input.nextInt();		
		System.out.print("Enter new buy rate [DOUBLE .]: ");
		double newBuyRate = input.nextDouble();		
		System.out.print("Enter new sell rate [DOUBLE .]: ");
		double newSellRate = input.nextDouble();
		
		String exchangeQuery = "UPDATE exchange_rate SET unit = " + newUnit + ", buying_rate = " + newBuyRate + ", selling_rate = " + newSellRate + 
				"WHERE currency_id = '" + chosenCurrencyCode + "';";
		
		Database.executeSql(exchangeQuery, con);
	}
	
	// This method allows users to delete exchanges from the database
	public static void deleteExchange(Connection con, Scanner input) {
		
		System.out.print("Currency symbol you want to delete from exchange rate [INT]: ");
		int chosenCurrencyCode = input.nextInt();
		
		String sql = "SELECT currency_id FROM exchange_rate;";
		if(!Database.checkIfExists(con, chosenCurrencyCode, sql)) {
			System.out.print("Please check if the currency code you entered is correct!\n");
			
			return;
		}
		
		String exchangeQuery = "DELETE FROM exchange_rate WHERE currency_id=" + "'" + chosenCurrencyCode + "';";
		
		Database.executeSql(exchangeQuery, con);	
	}
}

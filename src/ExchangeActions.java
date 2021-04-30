package currencyExchange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ExchangeActions {

	// This method is used to ask users which currency they want to buy or sell
	// It takes connection to the database as a parameter, and an integer which tells us if users want to buy or sell the chosen currency
	// We send the integer from the main function, 1 - buy, 2 - sell
	public void transactionCurrency(Connection con, int buyOrSell, Scanner input) {
		
		System.out.print("Enter code of the currency [INT]: ");
		int chosenCurrencyCode = input.nextInt();
		
		String sql = "SELECT currency_id FROM exchange_rate;";
		if(!Database.checkIfExists(con, chosenCurrencyCode, sql)) {
			System.out.print("Please check if the currency code you entered is correct!\n");
			return;
		}
		
		// Here we are creating and updating view which contains all the necessary data for creating an exchangeRate object
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
		
		Database.executeSql(createViewTable, con);
		
		try {
			String viewQuery = "SELECT * FROM exchange_table WHERE currency_id = " + chosenCurrencyCode + ";";
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(viewQuery);
			
			if(rs.next()) {
				int currencyCode = rs.getInt("currency_id");
				String currencySymbol = rs.getString("symbol");
				String currencyName = rs.getString("currency_name");
				String currencyCountry = rs.getString("country");
				int currencyUnit = rs.getInt("unit");
				double buyRate = rs.getDouble("buying_rate");
				double sellRate = rs.getDouble("selling_rate");
				
				// Creating an exchangeRate object to send data in a more convenient way
				ExchangeRate toTransaction = new ExchangeRate(currencyCode, currencySymbol, currencyName, currencyCountry, currencyUnit, buyRate, sellRate);
				
				if(buyOrSell == 1) {		
					executePurchase(con, toTransaction, input);	
				} else if(buyOrSell == 2) {
					executeSelling(con, toTransaction, input);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// This method executes purchases of foreign currencies, it takes connection to the database and an object which users want to purchase as parameters
	public void executePurchase(Connection con, ExchangeRate toBeBought, Scanner input) {
		
		System.out.print("How much of " + toBeBought.getCurrencySymbol() + " do you want to buy [DOUBLE .]: ");
		double amountToBuy = input.nextDouble();
		
		// Formula which is used to calculate how many euros are spent on buying chosen amount of chosen currency
		double eurosSpent = amountToBuy / toBeBought.getUnit() * toBeBought.getBuyRate();
		
		String sql = "INSERT INTO exchange_transactions (currency_id, amount_bought, amount_sold, transaction_type)" + 
					"VALUES (" + toBeBought.getCurrencyCode() + ", " + amountToBuy + ", " + eurosSpent + ", 'BOUGHT');";
		
		Database.executeSql(sql, con);
		
		System.out.print("Successfuly bought " + amountToBuy + " " + toBeBought.getCurrencyName() + " for " + eurosSpent + " Euros!\n");
	}
	
	// This method executes exchanges of foreign currencies for euro in a similar way method above is used to sell euro for foreign currency
	public void executeSelling(Connection con, ExchangeRate toBeSold, Scanner input) {
		
		System.out.print("How much of " + toBeSold.getCurrencySymbol() + " do you want to sell [DOUBLE .]: ");
		double amountToSell = input.nextDouble();
		
		double eurosEarned = amountToSell / toBeSold.getUnit() * toBeSold.getSellRate();
		
		String sql = "INSERT INTO exchange_transactions (currency_id, amount_bought, amount_sold, transaction_type)" + 
				"VALUES (" + toBeSold.getCurrencyCode() + ", " + eurosEarned + ", " + amountToSell + ", 'SOLD');";
	
		Database.executeSql(sql, con);
		
		System.out.print("Successfuly sold " + amountToSell + " " + toBeSold.getCurrencyName() + " for " + eurosEarned + " Euros!\n");
	}
}

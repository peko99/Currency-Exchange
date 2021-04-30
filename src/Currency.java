package currencyExchange;

import java.sql.Connection;
import java.util.Scanner;

public class Currency {
	
	private int currencyCode;
	private String currencyName;
	private String currencyCountry;
	private String currencySymbol;
	
	public Currency(int code, String name, String country, String symbol) {
		this.currencyCode = code;
		this.currencyName = name;
		this.currencyCountry = country;
		this.currencySymbol = symbol;
	}
	
	// Created to allow inheritance
	public Currency() {
		this.currencyCode = 0;
		this.currencyName = null;
		this.currencyCountry = null;
		this.currencySymbol = null;
	}
	
	public int getCurrencyCode() {
		return this.currencyCode;
	}
	
	public void setCurrencyCode(int code) {
		this.currencyCode = code;
	}
	
	public String getCurrencyName() {
		return this.currencyName;
	}
	
	public void setCurrencyName(String name) {
		this.currencyName = name;
	}
	
	public String getCurrencyCountry() {
		return this.currencyCountry;
	}
	
	public void setCurrencyCountry(String country) {
		this.currencyCountry = country;
	}
	
	public String getCurrencySymbol() {
		return this.currencySymbol;
	}
	
	public void setCurrencySymbol(String symbol) {
		this.currencySymbol = symbol;
	}
	
	// This method adds new currencies to database table 'currency'
	// Takes connection to the database as a parameter
	public void addCurrency(Connection con, Scanner input) {
		
		// Asking users to input currency data to be inserted in the database
		System.out.print("Enter the currency code [INT]: ");
		int currencyCode = input.nextInt();		
		
		String sql = "SELECT currency_id FROM exchange_rate;";
		if(Database.checkIfExists(con, currencyCode, sql)) {
			System.out.print("A currency with that code already exists!\n");
			return;
		}
		
		System.out.print("Enter the currency name [STRING]: ");
		String currencyName = input.next();		
		System.out.print("Enter the country of origin of the currency [STRING]: ");
		String currencyCountry = input.next();		
		System.out.print("Enter the currency symbol [STRING MAX 3 SYMBOLS]: ");
		String currencySymbol = input.next();
		
		// Creating an object of currency to transport data to another class in a more convenient way
		Currency toBeAdded = new Currency(currencyCode, currencyName, currencyCountry, currencySymbol);
				
		// Assembling a query to push data in the database
		String currencyQuery = "INSERT INTO currency" +
					" VALUES (" + currencyCode + " , '" + currencyName + "' , '" + currencyCountry + "' , '" + currencySymbol + "' );";
		
		Database.executeSql(currencyQuery, con);
				
		// Immediately after creating a new currency, we go to add exchange rates for it
		ExchangeRate exchangeRate = new ExchangeRate();
		exchangeRate.addNewExchange(con, toBeAdded, input);
	}
	
	// This method allows users to modify currency data (except currency code which always stays the same)
	// It takes connection to the database as a parameter
	public void modifyCurrency(Connection con, Scanner input) {
		
		System.out.print("Code of the currency you want to modify [INT]: ");
		int chosenCurrencyCode = input.nextInt();	
		
		// Checking if the currency code users enter really exists in our database
		String sql = "SELECT currency_id FROM currency;";
		if(!Database.checkIfExists(con, chosenCurrencyCode, sql)) {
			System.out.print("Please check if the currency code you entered is correct!\n");
			return;
		}
		
		System.out.print("Enter new currency name [STRING]: ");
		String currencyName = input.next();		
		System.out.print("Enter new country of origin of the currency [STRING]: ");
		String currencyCountry = input.next();		
		System.out.print("Enter new currency symbol [STRING MAX 3 SYMBOLS]: ");
		String currencySymbol = input.next();
		
		String currencyQuery = "UPDATE currency SET currency_name = '" + currencyName + 
				"', country = '" + currencyCountry + 
				"', symbol = '" + currencySymbol + 
				"' WHERE currency_id = '" + chosenCurrencyCode + "';";

		Database.executeSql(currencyQuery, con);		
	}
	
	// This method allows users to delete currencies from the database, it deletes currencies exxhange rates as well
	public void deleteCurrency(Connection con, Scanner input) {
		
		System.out.print("Code of the currency you want to delete [INT]: ");		
		int chosenCurrencyCode = input.nextInt();
		
		String sql = "SELECT currency_id FROM currency;";
		if(!Database.checkIfExists(con, chosenCurrencyCode, sql)) {
			System.out.print("Please check if the currency code you entered is correct!\n");
			return;
		}
		
		// If a transaction was already made with selected currency, the currency should not be deleted because we lose data of the transaction !
		sql = "SELECT currency_id FROM exchange_transactions;";
		if(Database.checkIfExists(con, chosenCurrencyCode, sql)) {
			System.out.print("Can not delete selected currency, because transactions were already made with it!\n");
			return;
		}

		// Firstly we need to delete data from 'exchange_rate' table because it has a foreign key to 'currency' table
		String exchangeQuery = "DELETE FROM exchange_rate WHERE currency_id=" + "'" + chosenCurrencyCode + "';";		
		Database.executeSql(exchangeQuery, con);
		
		String currencyQuery = "DELETE FROM currency WHERE currency_id=" + "'" + chosenCurrencyCode + "';";		
		Database.executeSql(currencyQuery, con);		
	}
}

package currencyExchange;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
	
	public static void displayMenu() {		
		System.out.println("\nChoose one of the options by pressing a number\n");
		System.out.println("\t -1) Exit program ");
		System.out.println("\t  0) Print the Menu ");
		System.out.println("\t  1) Add a currency ");
		System.out.println("\t  2) Modify a currency ");
		System.out.println("\t  3) Delete a currenc9y ");
		System.out.println("\t  4) Add an exchange (for an existing currency!)");
		System.out.println("\t  5) Modify an exchange");
		System.out.println("\t  6) Delete an excgange");
		System.out.println("\t  7) Buy a currency");
		System.out.println("\t  8) Sell a currency");
		System.out.println("\t  9) Clear database");
		System.out.println("\t 10) Print exchange list");
		System.out.println("\t 11) Print exchange transactions");
		System.out.println("");
	}

	public static void main(String[] args) {
		
		Connection con = null;		
		con = Database.getConnection();
		
		Scanner input = new Scanner(System.in);
		
		// Creating objects to use their methods
		Currency currency = new Currency();
		ExchangeRate exchangeRate = new ExchangeRate();
		ExchangeActions exchangeActions = new ExchangeActions();
		
		System.out.println("Hello! Welcome to Currency Exchange app!");
		System.out.println("All exchange rates shown in euros - EUR (€)");
		int userChoice = -1;
		displayMenu();
		
		do {
			System.out.print("\nYour entry (Press 0 to print Menu) -> ");
			userChoice = input.nextInt();

			switch(userChoice) {
				case -1: 
					System.out.println("\nExiting program...");
					System.exit(0);
					break;
				case 0:
					displayMenu();
					break;
				case 1: 
					currency.addCurrency(con, input);
					break;
				case 2:
					currency.modifyCurrency(con, input);
					break;
				case 3:
					currency.deleteCurrency(con, input);
					break;
				case 4:
					exchangeRate.addExchange(con, input);
					break;
				case 5: 
					exchangeRate.modifyExchange(con, input);
					break;
				case 6:
					exchangeRate.deleteExchange(con, input);
					break;
				case 7:
					// Parameter 1 helps us distinguish between buy and sell option in transactionCurrency method
					exchangeActions.transactionCurrency(con, 1, input);
					break;
				case 8:
					exchangeActions.transactionCurrency(con, 2, input);
					break;
				case 9:
					Database.resetDatabase(con);
					break;
				case 10:
					Database.printList(con);
					break;
				case 11:
					Database.printTransactions(con);
					break;
				default: 
					System.out.println("Sorry, command is not allowed! Try again\n");
			}
		}while(userChoice != -1);
		
		input.close();
	}
}
		

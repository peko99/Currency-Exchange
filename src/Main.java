package currencyExchange;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
	
	public static void displayMenu() {		
		System.out.println("\nChoose one of the options by pressing a number\n");
		System.out.println("\t -1) Exit program ");
		System.out.println("\t 0) Print the Menu ");
		System.out.println("\t 1) Add a currency ");
		System.out.println("\t 2) Modify a currency ");
		System.out.println("\t 3) Delete a currency ");
		System.out.println("\t 4) Add an exchange (for an existing currency!)");
		System.out.println("\t 5) Modify an exchange");
		System.out.println("\t 6) Delete an excgange");
		System.out.println("\t 7) Buy a currency");
		System.out.println("\t 8) Sell a currency");
		System.out.println("\t 9) Clear database");
		System.out.println("");
	}

	public static void main(String[] args) {
		
		Connection con = null;		
		con = Database.getConnection();
		
		Scanner choice = new Scanner(System.in);
		Scanner input = new Scanner(System.in);
		
		System.out.println("Hello! Welcome to Currency Exchange app!");
		System.out.println("All exchange rates shown in euros - EUR (€)");
		int userChoice = -1;
		displayMenu();
		
		do {
			System.out.print("\nYour entry (Press 0 to print Menu) -> ");
			userChoice = choice.nextInt();

			switch(userChoice) {
				case -1: 
					System.out.println("\nExiting program...");
					System.exit(0);
					break;
				case 0:
					displayMenu();
					break;
				case 1: 
					Currency.addCurrency(con, input);
					break;
				case 2:
					Currency.modifyCurrency(con, input);
					break;
				case 3:
					Currency.deleteCurrency(con, input);
					break;
				case 4:
					ExchangeRate.addExchange(con, input);
					break;
				case 5: 
					ExchangeRate.modifyExchange(con, input);
					break;
				case 6:
					ExchangeRate.deleteExchange(con, input);
					break;
				case 7:
					// Parameter 1 helps us distinguish between buy and sell option in transactionCurrency method
					ExchangeActions.transactionCurrency(con, 1, input);
					break;
				case 8:
					ExchangeActions.transactionCurrency(con, 2, input);
					break;
				case 9:
					Database.resetDatabase(con);
					break;
				default: 
					System.out.println("Sorry, command is not allowed! Try again\n");
			}
		}while(userChoice != -1);
		
		input.close();
		choice.close();
	}
}
		
package in.cryptoview.main;

import java.util.List;
import java.util.Scanner;
import in.cryptoview.api.coincap.CoinCapConnector;
import in.cryptoview.db.UserDB;
import in.cryptoview.db.WatchlistDB;
import in.cryptoview.exception.UserExitException;
import in.cryptoview.model.coin.CryptoData;
import in.cryptoview.model.user.User;

/**
 * @author kishor
 */
public class Main {

	private static final CoinCapConnector cryptoApi = new CoinCapConnector();
	private static final Scanner scanner = new Scanner(System.in);
	private static User currentUser;

	static {
		System.out.println(
				"=============================================================================================\n"
			  + "                  CryptoView.in - Cryptocurrency Insights & Tracking App                 \n"
			  + "=============================================================================================");

	}

	public static void main(String[] args) {

		while (true) {
			try {
				if (currentUser == null) {
					showMainMenu();
				} else {
					showLoggedInMenu();
				}
			} catch (UserExitException e) {
				System.out.println("\n >> Exiting the application...");
				System.out.println("\n >> Good Bye");
				System.exit(0);
			} catch (Exception e) {
				System.err.println("Unexpected error occurred: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private static void showMainMenu() {
		System.out.println(
				"----------------------------------\n" 
		        + "1. Check Live Crypto Price\n" 
				+ "2. Sign up (For new user)\n"
				+ "3. Login (For existing user)\n" 
				+ "4. Exit\n" 
				+ "----------------------------------\n");

		System.out.print(" ~ Choose an option: ");
		try {
			int choice = scanner.nextInt();
			scanner.nextLine();

			switch (choice) {
			case 1:
				if (currentUser == null) {
					System.err.println("\n >> Please login to view live crypto prices <<");
				} else {
					List<CryptoData> cryptoDataList = cryptoApi.getAllCryptoData();
					cryptoApi.sortCryptoDataByMarketCap(cryptoDataList);
					printCryptoDetails(cryptoDataList);
				}
				break;
			case 2:	
				signUp();
				break;
			case 3:
				login();
				break;
			case 4:
				throw new UserExitException();  
			default:
				System.err.println("**Invalid choice. Please choose again.");
				break;
			}
		} catch (UserExitException e) {
			throw e;  
		} catch (Exception e) {
			System.err.println("**Invalid input. Please enter a number.");
			scanner.nextLine();  
		}
	}

	private static void showLoggedInMenu() {
		List<CryptoData> cryptoDataList = cryptoApi.getAllCryptoData();
		cryptoApi.sortCryptoDataByMarketCap(cryptoDataList);

		System.out.println(
				"\n----------------------------------\n" 
		           + "1. Check Live Crypto Price\n" 
			       + "2. Add to Watchlist\n"
				   + "3. View Watchlist\n" 
			       + "4. Logout\n" 
				   + "----------------------------------\n");

		System.out.print(" ~ Choose an option: ");
		try {
			int choice = scanner.nextInt();
			scanner.nextLine();  
			
			switch (choice) {
			case 1:
				printCryptoDetails(cryptoDataList);
				break;
			case 2:
				addToWatchlist(cryptoDataList);
				break;
			case 3:
				viewWatchlist();
				break;
			case 4:
				currentUser = null;
				System.err.println("\n >> Logged out successfully.");
				break;
			default:
				System.err.println("**Invalid choice. Please choose again.");
				break;
			}
		} catch (Exception e) {
			System.err.println("**Invalid input. Please enter a number.");
			scanner.nextLine();
		}
	}

	private static void printCryptoDetails(List<CryptoData> cryptoDataList) {
		System.out.println(
				"-----------------------------------------------------------------------------------------------\n"
						+ "                               Live Cryptocurrency Prices                                                \n"
						+ "-----------------------------------------------------------------------------------------------\n"
						+ String.format("#   %-25s %-15s %-30s %-15s", "Name", "Price", "Market Cap", "Volume (24Hr)")
						+ "\n"
						+ "-----------------------------------------------------------------------------------------------");

		int count = 1;
		for (CryptoData coinData : cryptoDataList) {
			System.out.println(String.format("%-3d %s", count++, formatCryptoDetails(coinData)));
		}
	}

	private static void addToWatchlist(List<CryptoData> cryptoDataList) {

		System.out.println("\n----------------------------------");
		System.out.println("Adding to Watchlist...");
		printCryptoDetails(cryptoDataList);
		System.out
				.print("\n ~> Enter the indices of the cryptocurrencies to add to your watchlist (comma-separated): ");
		try {
			String input = scanner.nextLine();
			String[] indices = input.split(",");

			for (String indexStr : indices) {
				int index = Integer.parseInt(indexStr.trim());
				if (index >= 1 && index <= cryptoDataList.size()) {
					if (!currentUser.getWatchlist().getSelectedCryptos().contains(index - 1)) {
						currentUser.getWatchlist().addToWatchlist(index - 1); 
						System.out.println(" > Cryptocurrency at index " + index + " added to watchlist.");
					} else {
						System.out.println(" > Cryptocurrency at index " + index + " is already in the watchlist.");
					}
				} else {
					System.err.println("**Invalid index " + index + ". Please enter a number between 1 and "
							+ cryptoDataList.size() + ".");
				}
			}

			WatchlistDB.updateWatchlist(currentUser.getEmail(), currentUser.getWatchlist());
		} catch (Exception e) {
			System.err.println("**Invalid input. Please enter comma-separated indices.");
		}
	}

	private static void signUp() {
		System.out.println("----------------------------------");
		System.out.println("Sign-Up");

		// Prompt for email until valid email is provided
		String email;
		do {
			System.out.print(" ~> Enter your email: ");
			email = scanner.nextLine();
			if (!isValidEmail(email)) {
				System.err.println("**Invalid email format. Please enter a valid email address.");
			}
		} while (!isValidEmail(email));

		if (UserDB.userExists(email)) {
			System.err.println("**User with this email already exists. Please log in or use a different email.");
			return; // Exit the signUp method and return to the main menu
		}

		// Prompt for password until valid password is provided
		String password;
		do {
			System.out.print(" ~> Enter your password (8 characters or more): ");
			password = scanner.nextLine();
			if (!isValidPassword(password)) {
				System.err.println("**Invalid password. Password must contain 8 characters or more.");
			}
		} while (!isValidPassword(password));

		// Create user if both email and password are valid
		User newUser = new User(email, password);
		UserDB.addUser(newUser);
		System.out.println("\nSign-up successful!");
	}

	private static boolean isValidEmail(String email) {
		if (email == null || email.isEmpty()) {
			return false;
		}

		int atIndex = email.indexOf('@');
		int dotIndex = email.lastIndexOf('.');

		if (atIndex <= 0 || atIndex >= email.length() - 1) {
			return false;
		}

		if (dotIndex <= atIndex || dotIndex >= email.length() - 1) {
			return false;
		}

		if (dotIndex - atIndex <= 1) {
			return false;
		}

		if (email.length() - dotIndex <= 1) {
			return false;
		}

		if (email.contains(" ")) {
			return false;
		}

		return true;
	}

	private static boolean isValidPassword(String password) {
		return password.length() >= 8;  
	}

	private static void login() {
		System.out.println("\nLogin");
		System.out.print(" ~> Enter your email: ");
		String email = scanner.nextLine();
		System.out.print(" ~> Enter your password: ");
		String password = scanner.nextLine();

		if (UserDB.userExists(email)) {
			User user = UserDB.getUser(email);
			if (user.getPassword().equals(password)) {
				currentUser = user;
				System.out.println("\nLogin successful!");
			} else {
				System.err.println("**Incorrect password. Please try again.");
			}
		} else {
			System.err.println("**User with this email does not exist. Please sign up.");
			showMainMenu();
		}
	}

	private static void viewWatchlist() {

		List<Integer> watchlistItems = currentUser.getWatchlist().getSelectedCryptos();
		List<CryptoData> allCoinData = cryptoApi.getAllCryptoData();
		cryptoApi.sortCryptoDataByMarketCap(allCoinData); // Sort allCoinData by market cap

		if (watchlistItems.isEmpty()) {
			System.err.println("**Your watchlist is empty.");
		} else {
			System.out.println(
					"\n-----------------------------------------------------------------------------------------------\n"
							+ "                                   Your  Watchlist                                                       \n"
							+ "-----------------------------------------------------------------------------------------------\n"
							+ String.format("#   %-25s %-15s %-30s %-15s", "Name", "Price", "Market Cap", "Volume (24Hr)")
							+ "\n"
							+ "-----------------------------------------------------------------------------------------------\n");

			int count = 1;
			for (int index : watchlistItems) {

				if (index >= 0 && index < allCoinData.size()) {
					CryptoData coinData = allCoinData.get(index);
					System.out.println(String.format("%-3d %s", count++, formatCryptoDetails(coinData)));
				} else {
					System.err.println("**Invalid index in watchlist.");
				}
			}

			System.out.println(
					"\n----------------------------------\n" 
			         + "1. Check Live Crypto Price\n" 
					 + "2. Add to Watchlist\n"
					 + "3. Remove from Watchlist\n" 
					 + "4. Logout\n" 
					 + "----------------------------------\n");

			System.out.print("Choose an option: ");
			try {
				int choice = scanner.nextInt();

				switch (choice) {
				case 1:
					printCryptoDetails(allCoinData);
					break;
				case 2:
					addToWatchlist(allCoinData);
					break;
				case 3:
					removeFromWatchlist(watchlistItems);
					break;
				case 4:
					currentUser = null;
					System.err.println(" >> Logged out successfully.");
					break;
				default:
					System.err.println("**Invalid choice. Please choose again.");
					break;
				}
			} catch (Exception e) {
				System.err.println("**Invalid input. Please enter a number.");
				scanner.nextLine(); 
			}
		}
	}

	private static void removeFromWatchlist(List<Integer> watchlistItems) {
		System.out.print(" ~> Enter the index of the cryptocurrency to remove from your watchlist: ");
		try {
			int index = scanner.nextInt();
			scanner.nextLine();  
			if (index >= 1 && index <= watchlistItems.size()) {
				currentUser.removeFromWatchlist(index - 1);
				System.err.println("Cryptocurrency removed from watchlist successfully.");
				WatchlistDB.updateWatchlist(currentUser.getEmail(), currentUser.getWatchlist());
			} else {
				System.out.println(
						" > **Invalid index. Please enter a number between 1 and " + watchlistItems.size() + ".");
			}
		} catch (Exception e) {
			System.out.println(" > **Invalid input. Please enter a number.");
			scanner.nextLine();  
		}
	}

	private static String formatCryptoDetails(CryptoData cryptoData) {
		
		String formattedPrice = String.format("%.2f", Double.parseDouble(cryptoData.getPriceUsd()));
		String formattedMarketCap = String.format("%.2f", Double.parseDouble(cryptoData.getMarketCapUsd()));
		String formattedVolume = String.format("%.2f", Double.parseDouble(cryptoData.getVolumeUsd24Hr()));

		return String.format("%-25s %-15s %-30s %-15s", cryptoData.getName(), formattedPrice, formattedMarketCap,
				formattedVolume);
	}
}























 
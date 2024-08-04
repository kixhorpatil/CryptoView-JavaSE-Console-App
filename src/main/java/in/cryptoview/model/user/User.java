package in.cryptoview.model.user;

import java.io.Serializable;

public class User implements Serializable{
	private static final long serialVersionUID =1L;

	private String email;
	private transient String password; // Marked as transient to avoid serialization
	private Watchlist watchlist;

	public User(String email, String password) {
		this.email = email;
		this.password = password;
		this.watchlist = new Watchlist();
	}

	// Getters and setters
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Watchlist getWatchlist() {
		return watchlist;
	}

	public void setWatchlist(Watchlist watchlist) {
		this.watchlist = watchlist;
	}

	
	// Method to remove a selected cryptocurrency from the watchlist
	public void removeFromWatchlist(int index) {
		watchlist.removeSelectedCrypto(index);
	}
}

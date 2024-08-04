package in.cryptoview.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import in.cryptoview.model.user.Watchlist;

public class WatchlistDB {
	private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/cryptoview";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "root";

	public static List<Integer> getWatchlist(String userEmail) {

		List<Integer> watchlist = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String sql = "SELECT crypto_index FROM watchlist WHERE user_email = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, userEmail);
				try (ResultSet rs = pstmt.executeQuery()) {
					while (rs.next()) {
						watchlist.add(rs.getInt("crypto_index"));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return watchlist;
	}

	public static void updateWatchlist(String userEmail, Watchlist watchlist) {
	    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
	        if (!UserDB.userExists(userEmail)) {
	            System.out.println("User with email " + userEmail + " does not exist.");
	            return;
	        }

	        String deleteSql = "DELETE FROM watchlist WHERE user_email = ?";
	        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
	            deleteStmt.setString(1, userEmail);
	            deleteStmt.executeUpdate();
	        }

	        String insertSql = "INSERT INTO watchlist (user_email, crypto_index) VALUES (?, ?)";
	        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
	            for (Integer cryptoIndex : watchlist.getSelectedCryptos()) {
	                insertStmt.setString(1, userEmail);
	                insertStmt.setInt(2, cryptoIndex);
	                insertStmt.addBatch();
	            }
	            insertStmt.executeBatch();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


}

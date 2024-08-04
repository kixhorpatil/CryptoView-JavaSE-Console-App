 package in.cryptoview.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import in.cryptoview.model.user.User;

public class UserDB {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/cryptoview";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "root";


    public static void addUser(User user) {
        String sql = "INSERT INTO users (email, password) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPassword());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	public static User getUser(String email) {
		User user = null;
		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String sql = "SELECT * FROM users WHERE email = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, email);
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						user = new User(rs.getString("email"), rs.getString("password"));
						List<Integer> watchlist = WatchlistDB.getWatchlist(email);
						user.getWatchlist().setSelectedCryptos(watchlist);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

    public static boolean userExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}

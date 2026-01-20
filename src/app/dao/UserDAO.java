package app.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
	public Integer login(String username, String password) {
		String sql = "SELECT id FROM users WHERE username=? AND password=?";
		
		try(Connection conn = DB.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)){
			
			ps.setString(1,  username);
			ps.setString(2,  password);
			
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getInt("id");		//user_id を返す
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;	// ログイン失敗(失敗）
	}
	public String registerWithMessage(String username, String password) {
		String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
		
		try(Connection conn = DB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setString(1, username);
			stmt.setString(2, password);
			
			stmt.executeUpdate();
			return "OK";
		}catch(SQLException e) {
			return e.getMessage(); // エラー内容を返す
		}
	}
}

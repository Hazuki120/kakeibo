package app.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import app.util.PasswordUtil;

/**
 * users テーブルを操作する DAO クラス
 * 
 * ・ログイン認証（ユーザ名 + パスワードの照合）
 * ・新規ユーザ登録
 * 
 * といったユーザ管理に関する DB アクセス処理を担当する
 * パスワードは平文で保持せず、ハッシュ化して扱うことで
 * セキュリティを確保している
 */
public class UserDAO {
	
	/**
	 * ログイン処理を行う
	 * 入力されたユーザ名とパスワードを照合し、一致した場合はユーザID を返す
	 * 
	 * @param username 入力されたユーザ名
	 * @param password 入力されたパスワード（平文）
	 * @return ログイン成功 → ユーザID、失敗 → null
	 */
	public Integer login(String username, String password) {
		String sql = "SELECT id FROM users WHERE username=? AND password=?";
		
		try(Connection conn = DB.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)){
			
			ps.setString(1,  username);
			
			// パスワードは平文では保持しないため、入力値をハッシュ化して照合
			String hashed = PasswordUtil.hash(password);
			ps.setString(2,  hashed);
			
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getInt("id");		// ログイン成功 → user_id を返す
			}
		}catch(Exception e) {
			// 学習用のため標準出力に出力しているが、
			// 実務ではログフレームワーク（SLF4J / Log4j）で記録する
			e.printStackTrace();
		}
		return null;	// ログイン失敗
	}
	
	/**
	 * 新規ユーザを登録する
	 * 
	 * @param username 登録するユーザ名
	 * @param password 登録するパスワード（平文）
	 * @return "OK" → 成功、その他 → SQL エラー内容 
	 */
	public String registerWithMessage(String username, String password) {
		String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
		
		try(Connection conn = DB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setString(1, username);
			
			// パスワードはハッシュ化して保存（セキュリティ対策）
			String hashed = PasswordUtil.hash(password);
			stmt.setString(2, hashed);
			
			stmt.executeUpdate();
			return "OK";
		}catch(SQLException e) {
			// 例外内容をそのまま返すことで、UI 側でエラーメッセージを表示できる
			return e.getMessage();
		}
	}
}

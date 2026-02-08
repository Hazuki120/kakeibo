package app.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import app.model.MukkunTransaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * transactions テーブルを操作する DAO クラス
 * 
 * ・ユーザごとの家計簿データの取得
 * ・新規登録
 * ・削除
 * 
 * など、家計簿データに関する DB アクセス処理を担当する。
 * UI や実業務の処理から SQL を切り離すことで、コードの保守性・再利用性を高めている
 */
public class TransactionDAO {
	/**
	 * 指定したユーザID の家計簿データをすべて取得する
	 * 
	 * @param userId ログイン中のユーザID
	 * @return MukkunTransaction の ObservableList（JavaFX TableView 用）
	 */
	public ObservableList<MukkunTransaction>load(int userId){
		ObservableList<MukkunTransaction>list = FXCollections.observableArrayList();
		String sql = "SELECT * FROM transactions WHERE user_id=?";
		
		// try-with-resources により Connection / PreparedStatement を自動クローズ
		try(Connection conn = DB.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)){
			
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			
			// 1件ずつモデルに詰めてリストへ追加
			while (rs.next()) {
				list.add(new MukkunTransaction(
						rs.getInt("id"),
						rs.getString("date"),
						rs.getString("category"),
						rs.getInt("amount"),
						rs.getString("memo")
						));
			}
		}catch(Exception e) {
			// 実務ではログ出力に切り替えることが多い（現在はローカルで動かすためこのまま）
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 家計簿データを新規登録する
	 * 
	 * @param userId ログイン中のユーザID
	 * @param t 登録する家計簿データ
	 */
	public void add(int userId, MukkunTransaction t) {
		String sql = "INSERT INTO transactions(user_id, date, category, amount, memo)VALUES(?, ?, ?, ?, ?)";
		
		try (Connection conn = DB.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)){
			
			ps.setInt(1, userId);
			ps.setString(2, t.getDate());
			ps.setString(3, t.getCategory());
			ps.setInt(4, t.getAmount());
			ps.setString(5, t.getMemo());
			
			ps.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 指定した ID の家計簿データを削除する
	 * 
	 * 現在の UI では削除機能を提供していないが
	 * 将来的な拡張（管理者画面など）を見据えて
	 * DAOとして必要な CRUD 操作を実装している
	 * 
	 * @param id 削除提唱のレコード ID
	 */
	public void delete(int id) {
		String sql = "DELETE FROM transactions WHERE id=?";
		
		try(Connection conn = DB.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)){
			
			ps.setInt(1, id);
			ps.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}

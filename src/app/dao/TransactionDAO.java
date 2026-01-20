package app.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import app.model.MukkunTransaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TransactionDAO {
	
	/**
	 * 指定したユーザーIDの家計簿データをすべて読み込むメソッド
	 * @param userId ログイン中のユーザーID
	 * @return MukkunTransaction のリスト(ObservableList)
	 */
	public ObservableList<MukkunTransaction>load(int userId){
		ObservableList<MukkunTransaction>list = FXCollections.observableArrayList();
		String sql = "SELECT * FROM transactions WHERE user_id=?";
		
		try(Connection conn = DB.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			
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
			e.printStackTrace();
		}
		return list;
	}
	
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

package app.controller;

import app.dao.TransactionDAO;
import app.model.MukkunTransaction;
import javafx.collections.ObservableList;

/**
 * 家計簿画面のロジックを担当するコントローラークラス
 * 
 * ・収支データの追加
 * ・削除
 * ・合計金額の計算
 * 
 * など、UI（view）と DAO（データアクセス層）の橋渡しを行う
 * 画面の見た目は view に任せ、データ処理は本クラスが担当する
 */
public class BudgetController {
	private TransactionDAO dao = new TransactionDAO();
	
	/** ユーザの全取引データを読み込む */
	public ObservableList<MukkunTransaction> loadTransactions(int userId){
		return dao.load(userId);
	}
	/**
	 * 新しい収支データを DB に保存する
	 * 
	 * @param userId ログイン中のユーザID
	 * @param t 追加する家計簿データ
	 */
	public void addTransaction(int userId, MukkunTransaction t){
		dao.add(userId, t);
	}
	
	/**
	 * 指定したID の収支データを削除する
	 * 
	 * @param id 削除する対象のレコードID
	 */
	public void deleteTransaction(int id) {
		dao.delete(id);
	}
	
	/**
	 * 合計金額を計算する
	 * 
	 * @param list TableView に表示されているデータ
	 * @return 合計金額
	 */
	public int calculateTotal(ObservableList<MukkunTransaction>list) {
		return list.stream().mapToInt(MukkunTransaction::getAmount).sum();
	}
	
	/**
	 * 指定した年月（例：2025-06）の合計金額を計算する
	 * 今後実装予定
	 */
	public int calculateMonthlyTotal(ObservableList<MukkunTransaction> list, String yearMonth) {
		return list.stream()
				.filter(t -> t.getDate().startsWith(yearMonth))
				.mapToInt(MukkunTransaction::getAmount)
				.sum();
	}

}

package app.model;
// １件の収支記録を表すクラス
public class MukkunTransaction {
	private int id;
	private String date;	// 日付（例：2025-06-09）
	private String category;	// カテゴリ（例：おやつ代、むっくん代など）
	private String memo;	// メモ（例：ちょこまんじゅう）
	private int amount;	// 金額（収支はプラス、支出はマイナス）
	
	// コンストラクタ
	public MukkunTransaction(int id, String date, String category, String memo, int amount) {
		this.id = id;
		this.date = date;
		this.category = category;
		this.memo = memo;
		this.amount = amount;
	}
	public MukkunTransaction(String date, String category, String memo, int amount) {
		this(-1, date, category, memo, amount);
	}
	
	// 表示用メソッド（むっくん風🐻）
	public void print() {
		System.out.println("はにゃ～|" + date + "|" + category + "|"  + memo + "|" + amount +"円");
	}
	
	// toStoring() メソッド（デバッグやログ出力に便利）
	@Override
	public String toString() {
		return "MukkunTransaction{" +
				"date=" + date + '\'' +
				", category=" + category + '\'' +
				", memo='" + memo + '\'' +
				", amount=" + amount + '}';
	}
	
	// getter メソッドたち（他のクラスから値を取り出すときに使う）
	public int getId() {
		return id;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getCategory() {
		return category;
	}
	
	public String getMemo() {
		return memo;
	}
	
	public int getAmount() {
		return amount;
	}
	
}

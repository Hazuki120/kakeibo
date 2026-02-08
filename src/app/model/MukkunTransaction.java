package app.model;

/**
 * 1件の家計簿データ（収支記録）を表すモデルクラス
 * 
 * DAO（データアクセス層）とUI（JavaFX）間でデータを受け渡すための
 * シンプルな DTO（Data Transfer Object）として利用する。
 * 
 * ・id			；DB 上の主キー
 * ・date		；日付（例：2025-06-09）
 * ・category	；カテゴリ（例：食費、日用品など）
 * ・amount		；金額（収入はプラス、支出はマイナス）
 * ・memo		；メモ（任意）
 */
public class MukkunTransaction {
	
	private int id;
	private String date;
	private String category;
	private String memo;
	private int amount;
	
	/**
	 * DB から取得したデータを生成する際に使用するコンストラクタ
	 * 
	 * @param id		レコードID（主キー）
	 * @param date		日付
	 * @param category	カテゴリ
	 * @param amount	金額
	 * @param memo		メモ
	 */
	public MukkunTransaction(int id, String date, String category, int amount, String memo) {
		this.id = id;
		this.date = date;
		this.category = category;
		this.amount = amount;
		this.memo = memo;
	}
	
	/**
	 * 新規登録時に使用するコンストラクタ
	 * id はまだ存在しないため -1 を仮値として設定する
	 */
	public MukkunTransaction(String date, String category, int amount, String memo) {
		this(-1, date, category, amount, memo);
	}
	
	// getter メソッドたち（UI や DAO から値を参照するために使用）
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

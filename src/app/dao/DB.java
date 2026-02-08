package app.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * DB クラス
 * 
 * アプリ全体で使用する MySQL への接続処理を一元管理するクラスです
 *  接続情報は .env から読み込み、コードに直が気しないことでセキュリティと保守性を高めています
 */

public class DB {

	/**
	 * dotenv-java を使用して .env の内容を読み込む
	 * 
	 * directory() を指定しないことで、実行ディレクトリから .env を自動探索する
	 * filename(".env")を指定することで、確実に .env を読み込むようにしている
	 */
	private static final Dotenv dotenv = Dotenv.configure()
			.filename(".env") 
			.load();

	/** DB 接続 URL */
	private static final String url = dotenv.get("DB_URL");
	
	/** DB ユーザ名 */
	private static final String user = dotenv.get("DB_USER");
	
	/** DB パスワード */
	private static final String pass = dotenv.get("DB_PASSWORD");

	/**
	 * MySQL への接続を取得するメソッド
	 * 
	 * @return Connection オブジェクト
	 * @throws SQLException JDBC ドライバ未検出 or 接続失敗時
	 */
	public static Connection getConnection() throws SQLException {
		
		// JDBC ドライバのロード（Java では自動ロードされるが、明示的に記述）
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new SQLException("JDBC ドライバが見つかりません", e);
		}

		// MySQL へ接続
		return DriverManager.getConnection(url, user, pass);
	}
}
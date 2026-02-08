package app.util;

import java.security.MessageDigest;

/**
 * パスワードを SHA-256 でハッシュ化するユーティリティクラス
 * 
 * アプリケーション内でパスワードを平文のまま扱わないための
 * セキュリティ対策として使用する
 * 
 * 本プロジェクトでは学習目的のため SHA-256 のみを使用しているが、
 * 実務ではソルト付与やストレッチングを組み合わせてより強固なハッシュ化を行う
 */

public class PasswordUtil {
	
	/**
	 * 入力されたパスワードを SHA-256 でハッシュ化し、
	 * 16進数文字列として返す
	 * 
	 * @param password 平文のパスワード
	 * @return ハッシュ化されたパスワード（16進数文字列）
	 */
	public static String hash(String password) {
		try {
			// SHA-256 アルゴリズムを取得
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			
			// UTF-8 でバイト列に変換し、ハッシュ化を計算
			byte[] bytes = md.digest(password.getBytes("UTF-8"));
			
			// ハッシュ化（バイト列）を16進数文字列に変換
			StringBuilder sb = new StringBuilder();
			for(byte b : bytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		}catch(Exception e) {
			// 学習用のため RuntimeException にラップして投げている
			// 失敗はあり得ないため例外処理を強制しないため
			throw new RuntimeException(e);
		}
	}

}

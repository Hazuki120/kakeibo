-- users テーブル
-- アプリのユーザー情報を管理するテーブル。
-- パスワードは平文ではなくハッシュ値を保存する前提。
CREATE TABLE users(
    id INT AUTO_INCREMENT PRIMARY KEY,			-- ユーザーID（主キー）
    username VARCHAR(50) NOT NULL,				-- ユーザー名（ログイン用）
    password VARCHAR(100) NOT NULL,				-- ハッシュ化されたパスワード
    email VARCHAR(100),							-- メールアドレス（任意）
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP	-- 登録日時
) ENGINE=InnoDB;

-- transactions テーブル
-- 各ユーザーの家計簿データ（収支記録）を管理するテーブル。
CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,			-- レコードID（主キー）
    user_id INT NOT NULL,						-- users.id への外部キー
    date DATE NOT NULL,							-- 日付
    category VARCHAR(50) NOT NULL,				-- カテゴリ（食費など）
    amount INT NOT NULL,						-- 金額（収入はプラス、支出はマイナス）
    memo VARCHAR(255),							-- メモ（任意）
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,	-- 登録日時
    CONSTRAINT fk_transactions_user
        FOREIGN KEY (user_id) REFERENCES users(id)	-- ユーザーと紐付け
) ENGINE=InnoDB;
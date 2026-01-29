Markdown

# 家計簿アプリ(JavaFX + MySQL + Docker)

このプロジェクトは、JavaFX を使用して作成した家計簿アプリです。
データベースは Docker 上の MySQL を使用しており、ローカル環境でも簡単にセットアップできます。
---

## 使用技術

- Java 21
- JavaFX 21
- MySQL 8.0(Docker)
- JDBC (MySQL Connector/J)
- Eclipse / Pleiades

※ MySQL Connector/J は学習目的のため lib 配下に配置しています。  
実務では Maven / Gradle による依存関係管理を想定しています。

---

## 1. リポジトリをクローン
```bash
git clone https://github.com/Hazuki120/kakeibo.git
cd kakeibo
```

## 2. Docker で MySQL を起動
```bash
docker compose up -d
```
MySQL は以下の設定で起動します：  
| 項目           | 値         |
|----------------|------------|
| ホスト         | localhost  |
| ポート         | 3307       |
| ユーザー名     | appuser    |
| パスワード     | apppass    |
| データベース名 | kakeibo    |

※ 開発用のパスワードです。

## 3. データベース構造

本プロジェクトでは docker-compose.yml により、  
sql/init/配下の SQL ファイルが MySQL 初回起動時に自動実行されます。  

通常は手動で SQL を実行する必要はありません。  
テーブル構造を確認したい場合や、MySQL 単体で利用する場合に  
以下の SQL を使用できます。  

テーブル(users / transactions) は MySQL 初回起動時に自動作成されます。  
再作成をしたい場合は、以下を実行してください。

```bash
docker compose down -v
docker compose up -d
```

※ 以下は参考用の SQL 定義です（通常は自動作成されます。）  

users テーブル  

```sql
CREATE TABLE users (  
	id INT AUTO_INCREMENT PRIMARY KEY,  
	username VARCHAR(50) NOT NULL,  
	password VARCHAR(100) NOT NULL,  
	email VARCHAR(100),  
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  
) ENGINE=InnoDB;
```
transactions テーブル

```sql
CREATE TABLE transactions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  date DATE NOT NULL,
  category VARCHAR(50) NOT NULL,
  amount INT NOT NULL,
  memo VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_transactions_user
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;
```
※ transactions テーブルは users テーブルと外部キー制約で関連付けています。  
※ ユーザごとに家計簿データを管理する想定です。

## 4. アプリの起動方法
### ① MySQL Connector/J をビルドパスに追加
lib/mysql-connector-j-9.5.0.jar を  
Eclipse のビルド・パスに追加してください。

設定手順
【プロジェクトのプロパティ】→【Java ビルドパス】→【ライブラリ】→【外部 JAR の追加】

※ この設定を行わない場合、MySQL に接続できません。  
※ 本プロジェクトは Maven / Gradle を使用していないため、  
JDBC ドライバは手動でビルド・パスに追加する必要があります。

### ② JavaFX ライブラリを設定
JavaFX SDK をダウンロードし、Eclipse で以下を設定してください。

設定手順
【プロジェクトのプロパティ】→【Java ビルドパス】→【ライブラリ】
追加する JAR：    
・javafx.base.jar  
・javafx.controls.jar  
・javafx.fxml.jar  
・javafx.graphics.jar  

### ③ VM引数を設定（重要：JavaFX の警告対策)
Eclipse の実行構成に以下を追加します↓

```text
--module-path "javafx-sdk-21/lib" --add-modules javafx.controls,javafx.fxml
```

※ javafx-sdk-21 のパスは環境に合わせて変更してください。  
※ `/C:\...` のようにスラッシュとコロンが混ざるとエラーになります。

### よくあるエラーと対処

| エラー内容 | 原因 | 対処 |
|-----------|------|------|
| Unsupported JavaFX configuration: classes were loaded from 'unnamed module' | JavaFX が classpath で読み込まれている | module-path を正しく設定する |
| InvalidPathException: Illegal char <:> | `/C:\...` のように不正なパス形式 | `C:\...` の Windows パスに修正 |

### ④ アプリを起動
Main.java を実行すると、アプリが起動します。
新規登録・ログイン・家計簿管理機能を利用できます。

## 5. ログイン方法

1. ログイン画面下部の「新規登録」ボタンを押します。  
2. ユーザー名とパスワードを入力し、登録します。  
3. 「戻る」ボタンでログイン画面に戻ります。  
4. 登録したユーザー名とパスワードを入力し、「ログイン」ボタンを押します。  
5. 家計簿管理画面に遷移します。

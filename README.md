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
初回起動後、以下の SQL を実行して users テーブルを作成してください。

```sql
CREATE TABLE users (  
	id INT AUTO_INCREMENT PRIMARY KEY,  
	username VARCHAR(50) NOT NULL,  
	password VARCHAR(100) NOT NULL,  
	email VARCHAR(100),  
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  
);
```
## 4. アプリの起動方法
### ① JavaFX ライブラリを設定
JavaFX SDK をダウンロードし、Eclipse で以下を設定してください。

設定手順
【プロジェクトのプロパティ】→【Java ビルドパス】→【ライブラリ】
追加する JAR：    
・javafx.base.jar  
・javafx.controls.jar  
・javafx.fxml.jar  
・javafx.graphics.jar  

### ② VM引数を設定
Eclipse の実行構成に以下を追加します↓

```text
--module-path "javafx-sdk-21/lib" --add-modules javafx.controls,javafx.fxml
```

※ javafx-sdk-21 のパスは環境に合わせて変更してください。

### ③ アプリを起動
Main.java を実行すると、アプリが起動します。
新規登録・ログイン・家計簿管理機能を利用できます。

## 5. ログイン方法

1. ログイン画面下部の「新規登録」ボタンを押します。  
2. ユーザー名とパスワードを入力し、登録します。  
3. 「戻る」ボタンでログイン画面に戻ります。  
4. 登録したユーザー名とパスワードを入力し、「ログイン」ボタンを押します。  
5. 家計簿管理画面に遷移します。

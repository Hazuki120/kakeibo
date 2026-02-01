Markdown

# 家計簿アプリ(JavaFX + MySQL + Docker)

このプロジェクトは、JavaFX を使用して作成した家計簿アプリです。
データベースは Docker 上の MySQL を使用しており、ローカル環境でも簡単にセットアップできます。
---

## 使用技術

* Java 21
* JavaFX 21
* MySQL 8.0(Docker)
* JDBC (MySQL Connector/J)
* Eclipse / Pleiades
* Docker

※ MySQL Connector/J は学習目的のため lib 配下に配置しています。  
実務では Maven / Gradle による依存関係管理を想定しています。

---

## 前提環境（重要）
* JDK 21 がインストールされていること  
* Docker Desktop が起動していること
* JavaFX SK 21 を別途ダウンロードしていること


⚠ 注意  
Java 11 以降、JavaFX は JDK に同梱されていません。  
本プロジェクトでは JavaFX を module-path 方式で使用します。

## 1. リポジトリをクローン
```bash
git clone https://github.com/Hazuki120/kakeibo.git
cd kakeibo
```

## 2. Docker で MySQL を起動
### 起動
```bash
cd kakeibo
docker compose up -d
```
### 停止
```bash
docker compose down
```

MySQL 接続情報   
| 項目           | 値         |
|----------------|------------|
| ホスト         | localhost  |
| ポート         | 3307       |
| ユーザー名     | appuser    |
| パスワード     | apppass    |
| データベース名 | kakeibo    |

※ 開発用のパスワードです。
※ docker compose で作成したコンテナは `docker start kakeibo-mysql` でも再起動可能です。

## 3. データベース構造

本プロジェクトでは docker-compose.yml により、sql/init/配下の SQL ファイルは、 MySQL のデータ領域（volume）が空の場合のみ自動実行されます。  

通常は手動で SQL を実行する必要はありません。  
テーブル構造を確認したい場合や、MySQL 単体で利用する場合に  
以下の SQL を使用できます。  

テーブル(users / transactions) は MySQL 初回起動時に自動作成されます。  
再作成をしたい場合は、以下を実行してください。

```bash
docker compose down -v
docker compose up -d
```

※ -v オプションを付けると、MySQL のデータ（volume）が全削除されます。  



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
Eclipse のクラスパスに追加してください。

設定手順
【プロジェクトのプロパティ】→【Java ビルドパス】→【ライブラリ】→【外部 JAR の追加】

※ この設定を行わない場合、MySQL に接続できません。  
※ 本プロジェクトは Maven / Gradle を使用していないため、  
JDBC ドライバは手動でビルド・パスに追加する必要があります。

### ② JavaFX を Modulepath に設定
1. JavaFX SDK をダウンロードし、任意の場所に展開  
   例：`C:\javafx\javafx-sdk-21`  
2. 【プロジェクトのプロパティ】→【Java ビルドパス】→【ライブラリ】
3. 【ライブラリーの追加】 → 【ユーザー・ライブラリ】
4. 新規作成（名前例：JavaFX21）  
5. javafx-sdk-21/lib 配下のすべての jar を追加  
6. 追加したライブラリが【Modulepath】になっていることを確認  

### ③ VM引数を設定（重要：JavaFX の警告対策)
Eclipse の実行構成に以下を追加してください。
設定手順
Main.java → 右クリック → 【実行】 → 【実行の構成】→ 【引数】 →【VM引数】

```text
--module-path "javafx-sdk-21/lib" --add-modules javafx.controls,javafx.fxml
```

※ javafx-sdk-21 のパスは環境に合わせて変更してください。  
※ `/C:\...` のようにスラッシュとコロンが混ざるとエラーになります。


### ④ アプリを起動
Main.java を実行すると、アプリが起動します。
新規登録 → ログイン → 家計簿管理機能を利用できます。

## 5. ログイン方法

1. ログイン画面下部の「新規登録」ボタンを押します。  
2. ユーザー名とパスワードを入力し、登録します。  
3. 「戻る」ボタンでログイン画面に戻ります。  
4. 登録したユーザー名とパスワードを入力し、「ログイン」ボタンを押します。  
5. 家計簿管理画面に遷移します。

### よくあるエラーと対処

| エラー内容 | 原因 | 対処 |
|-----------|------|------|
| Module javafx.controls not found | JavaFX が Modulepath に設定されていない | JavaFX を Modulepath に追加する |
| Application を解決できません | JavaFX がコンパイル時に見えていない | Classpath ではなく Modulepath を使用する |
| Unsupported JavaFX configuration: classes were loaded from 'unnamed module' | JavaFX が classpath で読み込まれている | module-path を正しく設定する |
| InvalidPathException: Illegal char <:> | `/C:\...` のように不正なパス形式 | `C:\...` の Windows パスに修正 |  


## 今後の課題  

# 家計簿アプリ（JavaFX + MySQL + Docker）

このプロジェクトは **JavaFX** を使用して作成したデスクトップ家計簿アプリです。
データベースは **Docker 上の MySQL** を使用しており、ローカル環境でも簡単にセットアップできます。

---

## 使用技術

* Java 21
* JavaFX 21
* MySQL 8.0（Docker）
* JDBC（MySQL Connector/J）
* Eclipse / Pleiades

※ MySQL Connector/J は学習目的のため `lib` 配下に配置しています。
※ 実務では Maven / Gradle による依存関係管理を想定しています。

---

## 前提環境（重要）

* JDK 21 がインストールされていること
* Docker Desktop が起動していること
* **JavaFX SDK 21** を別途ダウンロードしていること

> ⚠️ **注意**
> Java 11 以降、JavaFX は JDK に同梱されていません。
> 本プロジェクトでは **JavaFX を module-path 方式で使用**します。

---

## 1. リポジトリをクローン

```bash
git clone https://github.com/Hazuki120/kakeibo.git
cd kakeibo
```

---

## 2. Docker で MySQL を起動

### 起動

```bash
docker compose up -d
```

### 停止

```bash
docker compose down
```

### MySQL 接続情報

| 項目     | 値         |
| ------ | --------- |
| ホスト    | localhost |
| ポート    | 3307      |
| ユーザー名  | appuser   |
| パスワード  | apppass   |
| データベース | kakeibo   |

※ 開発用の設定です。
※ `docker compose` で作成したコンテナは `docker start kakeibo-mysql` でも再起動可能です。

---

## 3. データベース構造

本プロジェクトでは `docker-compose.yml` により、
`sql/init/` 配下の SQL ファイルは **MySQL のデータ領域（volume）が空の場合のみ自動実行**されます。

通常は **手動で SQL を実行する必要はありません**。

### テーブルを再作成したい場合

```bash
docker compose down -v
docker compose up -d
```

※ `-v` オプションを付けると、MySQL のデータ（volume）が全削除されます。

---

### 参考：テーブル定義（通常は自動作成）

**users テーブル**

```sql
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(100) NOT NULL,
  email VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
```

**transactions テーブル**

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

※ `transactions` テーブルは `users` テーブルと外部キー制約で関連付けています。
※ ユーザーごとに家計簿データを管理する想定です。

---

## 4. アプリの起動方法（Eclipse）

### ① MySQL Connector/J をビルドパスに追加（Classpath）

`lib/mysql-connector-j-9.5.0.jar` を Eclipse の **Classpath** に追加してください。

設定手順：
【プロジェクトのプロパティ】→【Java ビルド・パス】→【ライブラリ】→【外部 JAR の追加】

※ この設定を行わない場合、MySQL に接続できません。

---

### ② JavaFX を Modulepath に設定（重要）

> ❗ **JavaFX の JAR を Classpath に追加しないでください**
> 本プロジェクトでは **Modulepath 方式**を使用します。

1. JavaFX SDK 21 をダウンロードし、任意の場所に展開
   例：`C:\javafx\javafx-sdk-21`
2. 【プロジェクトのプロパティ】→【Java ビルド・パス】→【ライブラリ】
3. 【ライブラリーの追加】→【ユーザー・ライブラリー】
4. 新規作成（名前例：`JavaFX21`）
5. `javafx-sdk-21/lib` 配下の **すべての jar** を追加
6. 追加したライブラリが **`[Modulepath]`** になっていることを確認

---

### ③ VM 引数を設定（必須）

Eclipse の実行構成に以下を設定してください。

設定手順：
Main.java → 右クリック →【実行】→【実行の構成】→【引数】→【VM 引数】

```text
--module-path "C:\javafx\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml
```

※ **必ず絶対パス**を指定してください。
※ `/C:\...` のような混在パスはエラーになります。

---

### ④ アプリを起動

`Main.java` を実行すると、アプリが起動します。
新規登録 → ログイン → 家計簿管理機能を利用できます。

---

## 5. ログイン方法

1. ログイン画面下部の「新規登録」ボタンを押します。
2. ユーザー名とパスワードを入力し、登録します。
3. 「戻る」ボタンでログイン画面に戻ります。
4. 登録したユーザー名とパスワードを入力し、「ログイン」ボタンを押します。
5. 家計簿管理画面に遷移します。

---

## よくあるエラーと対処

| エラー                              | 原因                            | 対処                              |
| -------------------------------- | ----------------------------- | ------------------------------- |
| Module javafx.controls not found | JavaFX が Modulepath に設定されていない | JavaFX を Modulepath に追加する       |
| Application を解決できません             | JavaFX がコンパイル時に見えていない         | Classpath ではなく Modulepath を使用する |
| Unsupported JavaFX configuration | classpath と module-path の混在   | JavaFX JAR を Classpath から削除     |
| InvalidPathException             | パス形式が不正                       | Windows の絶対パスに修正                |

---

## 今後の課題

* Maven / Gradle への移行
* 入力バリデーションの強化
* UI/UX の改善
* 単体テストの追加


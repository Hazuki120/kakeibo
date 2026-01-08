import java.util.Optional;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BudgetAppGUI extends Application {

	private Integer loggedInUserId;
	private TransactionDAO dao = new TransactionDAO();
	private ObservableList<MukkunTransaction> data;
	private Label totalLabel;

	private Scene createLoginScene(Stage primaryStage) {
		// --- ログイン画面 ---
		Label userLabel = new Label("ユーザー名");
		TextField userField = new TextField();

		Label passLabel = new Label("パスワード");
		TextField passField = new TextField();

		Button loginButton = new Button("ログイン");
		Button registerButton = new Button("新規登録");

		VBox loginBox = new VBox(10, userLabel, userField, passLabel, passField, loginButton, registerButton);
		Scene loginScene = new Scene(loginBox, 300, 200);

		// ログインボタンの処理
		loginButton.setOnAction(e -> {
			UserDAO userDAO = new UserDAO();
			Integer userId = userDAO.login(userField.getText(), passField.getText());

			if (userId != null) {
				loggedInUserId = userId;
				primaryStage.setScene(createBudgetScene());
			} else {
				new Alert(Alert.AlertType.ERROR, "ユーザー名かパスワードが間違っています。").showAndWait();
			}
		});

		// 新規登録ボタンの処理
		registerButton.setOnAction(e -> {
			primaryStage.setScene(createRegisterScene(primaryStage));
		});
		return loginScene;
	}

	@Override
	public void start(Stage primaryStage) {

		primaryStage.setScene(createLoginScene(primaryStage));
		primaryStage.setTitle("家計簿ログイン");
		primaryStage.show();

	}

	// 家計簿画面
	private Scene createBudgetScene() {

		// TableView の準備
		TableView<MukkunTransaction> table = new TableView<>();
		data = dao.load(loggedInUserId);
		table.setItems(data);

		// 列の定義
		TableColumn<MukkunTransaction, String> dateColumn = new TableColumn<>("日付");
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
		TableColumn<MukkunTransaction, String> categoryColumn = new TableColumn<>("カテゴリ");
		categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
		TableColumn<MukkunTransaction, String> memoColumn = new TableColumn<>("メモ");
		memoColumn.setCellValueFactory(new PropertyValueFactory<>("memo"));
		TableColumn<MukkunTransaction, Integer> amountColumn = new TableColumn<>("金額");
		amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

		// 列を TableView に追加
		table.getColumns().addAll(dateColumn, categoryColumn, memoColumn, amountColumn);

		// データをTableにセット
		table.setItems(data);

		// 合計ラベル
		totalLabel = new Label("合計： 0円");
		updateTotal(); // 初期合計を表示

		// 入力フォーム
		TextField dateField = new TextField();
		dateField.setPromptText("日付（例： 2025-06-09）");
		TextField categoryField = new TextField();
		categoryField.setPromptText("カテゴリ");
		TextField memoField = new TextField();
		memoField.setPromptText("メモ");
		TextField amountField = new TextField();
		amountField.setPromptText("金額");

		// ボタン
		Button addButton = new Button("追加");
		Button deleteButton = new Button("削除");

		// 追加ボタンの処理 
		addButton.setOnAction(e -> {
			try {
				String date = dateField.getText();
				String category = categoryField.getText();
				String memo = memoField.getText();
				int amount = Integer.parseInt(amountField.getText());

				MukkunTransaction newTransaction = new MukkunTransaction(date, category, memo, amount);
				
				// ①DBに保存
				dao.add(loggedInUserId, newTransaction);
				// ②TableView に追加
				data.add(newTransaction);

				// ③合計を更新
				updateTotal();

				// ④入力欄をクリア
				dateField.clear();
				categoryField.clear();
				memoField.clear();
				amountField.clear();
			} catch (NumberFormatException ex) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "金額は数字で入力してください！");
				alert.showAndWait();
			}
		});

		// 削除ボタンの処理 
		deleteButton.setOnAction(e -> {
			MukkunTransaction selected = table.getSelectionModel().getSelectedItem();

			if (selected == null) {
				System.out.println("削除する項目が選択されていません");
				return;
			}

			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("確認");
			alert.setHeaderText("本当に削除しますか？");
			alert.setContentText(selected.toString());

			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				dao.delete(selected.getId());
				data.remove(selected);
				updateTotal();
			}
		});

		// 入力欄
		HBox inputBox = new HBox(10, dateField, categoryField, memoField, amountField, addButton);
		// ボタン
		HBox buttonBox = new HBox(10);
		buttonBox.getChildren().addAll(addButton, deleteButton);

		// 画面レイアウト
		VBox root = new VBox(10, table, inputBox, buttonBox, totalLabel);
		// シーン
		return new Scene(root, 700, 450);
	}

	// 合計を計算してラベルに反映
	private void updateTotal() {
		int total = data.stream().mapToInt(MukkunTransaction::getAmount).sum();
		totalLabel.setText("合計：" + total + "円");
	}

	// 新規登録画面のメソッド
	private Scene createRegisterScene(Stage primaryStage) {
		Label userLabel = new Label("新しいユーザー名");
		TextField userField = new TextField();

		Label passLabel = new Label("パスワード");
		TextField passField = new TextField();

		Button registerButton = new Button("登録");
		Button backButton = new Button("戻る");

		VBox box = new VBox(10, userLabel, userField, passLabel, passField, registerButton, backButton);
		Scene scene = new Scene(box, 300, 250);

		// 登録ボタンの処理
		registerButton.setOnAction(e -> {
			String username = userField.getText();
			String password = passField.getText();

			if (username.isEmpty() || password.isEmpty()) {
				new Alert(Alert.AlertType.ERROR, "ユーザー名とパスワードを入力してください。").showAndWait();
				return;
			}

			UserDAO userDAO = new UserDAO();
			String result = userDAO.registerWithMessage(username, password);

			if (result.equals("OK")) {
				new Alert(Alert.AlertType.INFORMATION, "登録が完了しました！\nログインしてください。").showAndWait();

			} else {
				new Alert(Alert.AlertType.ERROR, "登録に失敗しました。\n理由: " + result).showAndWait();
			}
		});

		// 戻るボタン
		backButton.setOnAction(e -> {
			primaryStage.setScene(createLoginScene(primaryStage));
		});
		return scene;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
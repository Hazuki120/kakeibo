package app.view;

import java.time.LocalDate;
import java.util.Optional;

import app.dao.TransactionDAO;
import app.dao.UserDAO;
import app.model.MukkunTransaction;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
		PasswordField passField = new PasswordField();	// 非表示
		TextField passVisibleField = new TextField();	// 表示
		
		passVisibleField.setVisible(false);
		passVisibleField.setManaged(false);
		
		CheckBox showPasswordCheckBox = new CheckBox("パスワードを表示");
		// パスワードの中身を同期
		passVisibleField.textProperty().bindBidirectional(passField.textProperty());
		
		// 表示切替の処理
		showPasswordCheckBox.selectedProperty().addListener((obs, oldVal, newVal) ->{
			if(newVal) {
				passVisibleField.setVisible(true);
				passVisibleField.setManaged(true);
				passField.setVisible(false);
				passField.setManaged(false);
			}else {
				passField.setVisible(true);
				passField.setManaged(true);
				passVisibleField.setVisible(false);
				passVisibleField.setManaged(false);
			}
		});

		Button loginButton = new Button("ログイン");
		Button registerButton = new Button("新規登録");

		VBox loginBox = new VBox(10, userLabel, userField, passLabel, passField, passVisibleField, showPasswordCheckBox, loginButton, registerButton);
		Scene loginScene = new Scene(loginBox, 300, 220);

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
		dateColumn.setPrefWidth(120);
		TableColumn<MukkunTransaction, String> categoryColumn = new TableColumn<>("カテゴリ");
		categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
		categoryColumn.setPrefWidth(100);
		TableColumn<MukkunTransaction, Integer> amountColumn = new TableColumn<>("金額");
		amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
		amountColumn.setPrefWidth(80);
		// 収支の色付け
		amountColumn.setCellFactory(col -> new TableCell<>() {
			@Override
			protected void updateItem(Integer value, boolean empty) {
				super.updateItem(value, empty);
				if (empty || value == null) {
					setText(null);
					setStyle("");
				}else {
					setText(value + "円");
					if (value < 0) {
						setStyle("-fx-text-fill: red;");
					}else {
						setStyle("-fx-text-fill: blue;");
					}
				}
			}
		});
		
		TableColumn<MukkunTransaction, String> memoColumn = new TableColumn<>("メモ");
		memoColumn.setCellValueFactory(new PropertyValueFactory<>("memo"));
		memoColumn.setPrefWidth(250);
		
		// 列を TableView に追加
		table.getColumns().addAll(dateColumn, categoryColumn, amountColumn, memoColumn);

		// データをTableにセット
		table.setItems(data);

		// 合計ラベル
		totalLabel = new Label("合計： 0円");
		updateTotal(); // 初期合計を表示
		
		String currentMonth = LocalDate.now().toString().substring(0, 7);
		int monthlyTotal = calculateMonthlyTotal(currentMonth);
		Label monthlyLabel = new Label("今月の合計：" + monthlyTotal + "円");


		// 入力フォーム
		// 日付をカレンダーで選択する
		DatePicker datePicker = new DatePicker();
		datePicker.setPromptText("日付選択");
		// カテゴリを選択式にする
		ComboBox<String> categoryBox = new ComboBox<>();
		categoryBox.getItems().addAll(
				"給料", "食費", "日用品", "娯楽", "交通費", "通信費", "光熱費", "家賃", "保険", "ローン", "投資", "せんちゃ", "その他");
		categoryBox.setPromptText("カテゴリ選択");
		TextField amountField = new TextField();
		amountField.setPromptText("金額");
		TextField memoField = new TextField();
		memoField.setPromptText("メモ");
		
		// 収入・支出の選択
		RadioButton expenseRadio = new RadioButton("支出");
		RadioButton incomeRadio = new RadioButton("収入");
		
		ToggleGroup typeGroup = new ToggleGroup();
		expenseRadio.setToggleGroup(typeGroup);
		incomeRadio.setToggleGroup(typeGroup);
		
		// 初期値を支出
		expenseRadio.setSelected(true);

		// ボタン
		Button addButton = new Button("追加");
		Button deleteButton = new Button("削除");

		// 追加ボタンの処理 
		addButton.setOnAction(e -> {
			try {
				LocalDate selectedDate = datePicker.getValue();
				String category = categoryBox.getValue();
				
				if (selectedDate == null || category == null) {
					new Alert(Alert.AlertType.ERROR, "日付とカテゴリを選択してください。").showAndWait();
					return;
				}
				String date = selectedDate.toString();
				
				String memo = memoField.getText();
				int amount = Integer.parseInt(amountField.getText());
				
				// 支出ならマイナス
				if (expenseRadio.isSelected()) {
					amount = -amount;
				}

				MukkunTransaction newTransaction = new MukkunTransaction(date, category, amount, memo);
				
				// ①DBに保存
				dao.add(loggedInUserId, newTransaction);
				// ②TableView に追加
				data.add(newTransaction);

				// ③合計を更新
				updateTotal();

				// ④入力欄をクリア
				datePicker.setValue(null);
				categoryBox.setValue(null);
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
		HBox typeBox = new HBox(10, expenseRadio, incomeRadio);
		HBox inputBox = new HBox(10, datePicker, categoryBox, typeBox, amountField, memoField, addButton);
		HBox.setHgrow(memoField, Priority.ALWAYS);
		memoField.setMaxWidth(Double.MAX_VALUE);
		// ボタン
		HBox buttonBox = new HBox(10);
		buttonBox.getChildren().addAll(addButton, deleteButton);

		// 画面レイアウト
		VBox root = new VBox(10, table, inputBox, buttonBox, totalLabel);
		// 画面の大きさ
		return new Scene(root, 750, 500);
	}

	// 合計を計算してラベルに反映
	private void updateTotal() {
		int total = data.stream().mapToInt(MukkunTransaction::getAmount).sum();
		totalLabel.setText("合計：" + total + "円");
	}
	// 月別合計
	private int calculateMonthlyTotal(String yearMonth) {
		return data.stream()
				.filter(t -> t.getDate().startsWith(yearMonth))
				.mapToInt(MukkunTransaction::getAmount)
				.sum();
	}
	//ComboBox<String> monthBox = new ComboBox<>();
	//monthBox.getItems().addAll(
	//		"2026-01");
	//monthBox.setValue(LocalDate.now().toString().substring(0, 7));
	

	// 新規登録画面のメソッド
	private Scene createRegisterScene(Stage primaryStage) {
		Label userLabel = new Label("新しいユーザー名");
		TextField userField = new TextField();

		Label passLabel = new Label("パスワード");
		TextField passField = new TextField();

		Button registerButton = new Button("登録");
		Button backButton = new Button("戻る");

		VBox box = new VBox(10, userLabel, userField, passLabel, passField, registerButton, backButton);
		Scene scene = new Scene(box, 300, 220);

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
}
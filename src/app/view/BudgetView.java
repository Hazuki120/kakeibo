package app.view;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import app.controller.BudgetController;
import app.model.MukkunTransaction;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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

/**
 * 家計簿画面を構築する View クラス
 * 
 * ・収支一覧の表示（TableView）
 * ・収支データの追加 / 削除
 * ・合計金額の表示
 * 
 * 画面の見た目とイベント処理のみを担当し、
 * データ処理は BudgetController に委譲する
 */
public class BudgetView {
	private final int userId; // ログイン中のユーザID
	private final BudgetController controller = new BudgetController();
	private ObservableList<MukkunTransaction> data;
	private Label totalLabel;

	public BudgetView(int userId) {
		this.userId = userId;
	}

	public Scene create(Stage stage) {
		// TableView の準備
		TableView<MukkunTransaction> table = new TableView<>();
		data = controller.loadTransactions(userId);
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

		// 金額の色付け（収入→青、支出→赤）
		amountColumn.setCellFactory(col -> new TableCell<>() {
			@Override
			protected void updateItem(Integer value, boolean empty) {
				super.updateItem(value, empty);
				if (empty || value == null) {
					setText(null);
					setStyle("");
				} else {
					setText(value + "円");
					if (value < 0) {
						setStyle("-fx-text-fill: red;");
					} else {
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

		// データをTableにセット(いらない？）
		table.setItems(data);

		// 合計ラベル
		totalLabel = new Label("合計： 0円");
		updateTotal();

		// 入力フォーム
		// 日付をカレンダーで選択する
		DatePicker datePicker = new DatePicker();
		datePicker.setPromptText("日付");
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

		HBox typeBox = new HBox(10, expenseRadio, incomeRadio);

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

				MukkunTransaction t = new MukkunTransaction(date, category, amount, memo);

				// ①DBに保存
				controller.addTransaction(userId, t);

				// ②TableView に追加
				data.add(t);

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
			MukkunTransaction selectedItem = table.getSelectionModel().getSelectedItem();
			if (selectedItem == null) {
				new Alert(Alert.AlertType.ERROR, "削除する項目を選択してください。").showAndWait();
				return;
			}

			Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "本当に削除しますか？");
			confirm.showAndWait().ifPresent(result -> {
				if (result == ButtonType.OK) {
					controller.deleteTransaction(selectedItem.getId());
					data.remove(selectedItem);
					updateTotal();
				}
			});
		});
		
		
		// 年月の ComboBox を作る
		ComboBox<Integer> yearCombo = new ComboBox<>();
		for(int y = 2020; y <= 2030; y++) {
			yearCombo.getItems().add(y);
		}
		yearCombo.setPromptText("年");
		
		ComboBox<String> monthCombo = new ComboBox<>();
		monthCombo.getItems().addAll("1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月");
		monthCombo.setPromptText("月");
		

		// グラフ表示ボタン
		Button graphButton = new Button("月別カテゴリグラフ");

		// 月別グラフ表示ボタンの処理
		graphButton.setOnAction(e -> {
			Integer selectedYear = yearCombo.getValue();
			String monthStr = monthCombo.getValue();
			
			if (selectedYear == null || monthStr == null) {
				new Alert(Alert.AlertType.ERROR, "年と月を選択してください。").showAndWait();
				return;
			}
			
			int month = Integer.parseInt(monthStr.replace("月", ""));
			YearMonth ym = YearMonth.of(selectedYear, month);
			String yearMonth = ym.format(DateTimeFormatter.ofPattern("yyyy-MM"));

			Map<String, Integer> totals = controller.calculateMonthlyCategoryTotals(data, yearMonth);
			
			int monthlyTotal = controller.calculateMonthlyTotal(data, yearMonth);

			PieChart pieChart = new PieChart();
			for (Map.Entry<String, Integer> entry : totals.entrySet()) {
				int value = entry.getValue();
				// 支出だけをグラフ化したい場合は amount < 0 のみ
				if (value < 0) {
					pieChart.getData().add(
							new PieChart.Data(entry.getKey(), Math.abs(value)));
				}
			}
			
			Label totalLabel = new Label("月合計:" + monthlyTotal + "円");

			VBox box = new VBox(10, pieChart, totalLabel);
			Stage chartStage = new Stage();
			chartStage.setTitle(yearMonth + "カテゴリ別支出グラフ");
			chartStage.setScene(new Scene(box, 400, 300));
			chartStage.show();
		});

		// レイアウト
		HBox inputBox = new HBox(10, datePicker, categoryBox, typeBox, amountField, memoField, addButton);
		HBox.setHgrow(memoField, Priority.ALWAYS);
		// ↓いらない？
		memoField.setMaxWidth(Double.MAX_VALUE);

		// 画面レイアウト
		HBox ymBox = new HBox(10, yearCombo, monthCombo, graphButton);
		ymBox.setAlignment(Pos.CENTER_RIGHT);
		VBox root = new VBox(10, table, inputBox, ymBox, deleteButton, totalLabel);

		// 画面の大きさ
		return new Scene(root, 800, 500);

	}

	/**
	 * TableView に表示されているデータから合計金額を計算し、
	 * 画面下部のラベルに反映する。
	 */
	private void updateTotal() {
		int total = data.stream().mapToInt(MukkunTransaction::getAmount).sum();
		totalLabel.setText("合計：" + total + "円");
	}

}

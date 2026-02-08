package app.view;

import app.dao.UserDAO;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * ログイン画面を構築する View クラス
 * 
 * ・ユーザ名とパスワードの入力
 * ・ログイン処理（UserDAO を使用）
 * ・新規登録画面への遷移
 * 
 * 画面の見た目とイベント処理のみを担当し
 * ロジックは DAO や Controller に委譲する
 */
public class LoginView {
	public Scene create(Stage stage) {
		// --- ログイン画面 ---
		Label userLabel = new Label("ユーザー名");
		TextField userField = new TextField();

		Label passLabel = new Label("パスワード");
		PasswordField passField = new PasswordField(); // 非表示
		TextField passVisibleField = new TextField(); // 表示

		passVisibleField.setVisible(false);
		passVisibleField.setManaged(false);

		CheckBox showPasswordCheckBox = new CheckBox("パスワードを表示");
		// パスワードの中身を同期
		passVisibleField.textProperty().bindBidirectional(passField.textProperty());

		// 表示切替の処理
		showPasswordCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal) {
				passVisibleField.setVisible(true);
				passVisibleField.setManaged(true);
				passField.setVisible(false);
				passField.setManaged(false);
			} else {
				passField.setVisible(true);
				passField.setManaged(true);
				passVisibleField.setVisible(false);
				passVisibleField.setManaged(false);
			}
		});

		Button loginButton = new Button("ログイン");
		Button registerButton = new Button("新規登録");

		VBox loginBox = new VBox(10, userLabel, userField, passLabel, passField, passVisibleField, showPasswordCheckBox,
				loginButton, registerButton);
		Scene loginScene = new Scene(loginBox, 300, 220);

		// ログインボタンの処理
		loginButton.setOnAction(e -> {
			UserDAO userDAO = new UserDAO();
			Integer userId = userDAO.login(userField.getText(), passField.getText());

			if (userId != null) {
				// ログイン成功 → 家計簿画面へ
				BudgetView budgetView = new BudgetView(userId);
				stage.setScene(budgetView.create(stage));
			} else {
				new Alert(Alert.AlertType.ERROR, "ユーザー名かパスワードが間違っています。").showAndWait();
			}
		});

		registerButton.setOnAction(e -> {
			RegisterView rv = new RegisterView();
			stage.setScene(rv.create(stage));
		});
		return loginScene;
	}

}

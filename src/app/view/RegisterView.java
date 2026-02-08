package app.view;

import app.dao.UserDAO;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * 新規ユーザー登録画面を構築する View クラス
 * 
 * ・ユーザ名とパスワードの入力
 * ・UserDAO を使った登録処理
 * ・登録成功後はログイン画面へ戻る
 */

public class RegisterView {
	
	public Scene create(Stage stage) {
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
				
				// ログイン画面へ戻る
				LoginView lv = new LoginView();
				stage.setScene(lv.create(stage));
			} else {
				new Alert(Alert.AlertType.ERROR, "登録に失敗しました。\n理由: " + result).showAndWait();
			}
		});

		// 戻るボタン
		backButton.setOnAction(e -> {
			LoginView lv = new LoginView();
			stage.setScene(lv.create(stage));
		});
		return scene;
	}
	}



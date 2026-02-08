package app;

import app.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * アプリケーションのエントリーポイント（起動クラス）
 * 
 * JavaFX アプリは通常の Java アプリと異なり、
 * main() から直接画面を表示するのではなく、
 * Application.launch() を通して JavaFX ランタイムを起動する必要がある
 * 
 * launch() に渡したクラス（BudgetAppGUI）が
 * 実際の画面（Stage / Scene）を構築する役割を持つ
 */
public class Main extends Application {
	@Override
	public void start(Stage stage) {
		LoginView loginView = new LoginView();
		Scene scene = loginView.create(stage);
		
		stage.setScene(scene);
		stage.setTitle("家計簿アプリ - ログイン");
		stage.show();
		}
public static void main(String[] args) {
	launch(args);
}

}

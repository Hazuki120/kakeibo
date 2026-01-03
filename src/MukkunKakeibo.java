import java.util.ArrayList;
import java.util.Scanner;

// むっくん風家計簿アプリのメインクラス
public class MukkunKakeibo {
	public static void main(String[] args) {
		Scanner stdIn = new Scanner(System.in);
		ArrayList<MukkunTransaction> transactions = new ArrayList<>();

		// タイトル表示
		System.out.println("=============================");
		System.out.println(" むっくん風おこづかいちょう🐻 ");
		System.out.println("=============================");

		// メインループ（メニュー表示と選択処理）
		while (true) {
			System.out.println("\n【めにゅー】");
			System.out.println("1. きろくする");
			System.out.println("2. りれきみる");
			System.out.println("3. ざんだかみる");
			System.out.println("4. カテゴリべつしゅうけい");
			System.out.println("5. むっくんおこ度をみる");
			System.out.println("6. おわる");
			System.out.print("おえらびください（１～６）→");
			int choice = stdIn.nextInt();
			stdIn.nextLine(); // 改行の吸収

			// 1.収支を記録
			if (choice == 1) {
				System.out.println("あたらしい収支記録");
				System.out.print("日付（例：2025-06-09)→");
				String date = stdIn.nextLine();
				System.out.print("かてごり（おやつ代 / むっくん代 / おでかけ代)→");
				String category = stdIn.nextLine();
				System.out.print("おかね（支出はマイナスで入力してね)→");
				int amount = stdIn.nextInt();
				stdIn.nextLine();
				System.out.print("めも（例：ちょこまんじゅう）→");
				String memo = stdIn.nextLine();

				//トランザクションを作成してリストに追加
				MukkunTransaction t = new MukkunTransaction(date, category, memo, amount);
				transactions.add(t);
				System.out.println("きろくできた！");
			} 
			// 2.収支履歴を表示
			else if (choice == 2) {
				System.out.println("＝＝＝ りれき ＝＝＝");
				for (MukkunTransaction t : transactions) {
					t.print();
				}
				System.out.println("＝＝＝＝＝＝＝＝＝＝");
			} 
			// 3.残高を計算して表示
			else if (choice == 3) {
				int balance = calculateBalance(transactions);
				System.out.println("＝＝＝ ざんだか ＝＝＝");
				System.out.println("現在の残高は..." + balance + "だよ～");
				
				// 残高に応じたコメント
				if(balance < 0) {
					System.out.println("はにゃ～、赤字だ...おこづかいぴんち。。。");
				}else if(balance < 1000) {
					System.out.println("ちょっとつかいすぎ...かも。");
				}else {
					System.out.println("えらい！がんばってる！");
				}
				System.out.println("＝＝＝＝＝＝＝＝＝＝＝");
			}
			// 4.カテゴリ別集計
			else if(choice == 4) {
				System.out.print("どのカテゴリを集計する？（例：おやつ代）→");
				String target= stdIn.nextLine();
				int total = calculateCategoryTotal(transactions, target);
				System.out.println("＝＝＝ しゅうけい ＝＝＝");
				System.out.println("カテゴリ：「" + target + "」のごうけいは" + total + "円だよ～");
				
				// 支出が多いと注意コメント
				if(total < 0) {
					System.out.println("はにゃ～、つかいすぎだよ～");
				}else {
					System.out.println("えらい！じょうずにがんばってる！");
				}
				System.out.println("＝＝＝＝＝＝＝＝＝＝＝");
			}
			// 5.収支によって変わるゲージ表示（むっくんおこ度）
			else if (choice == 5){
				int expenseTotal = calculateTotalExpense(transactions);
				showMukumukuGauge(expenseTotal);
			}
			// 6.終了
			else if(choice == 6) {
				System.out.println("またつかってね。");
				break;
			}
			// 無効な選択肢
			else {
				System.out.println("はにゃ？…その選択はできないよ。");
			}
		}
		stdIn.close();	// スキャナを閉じる
	}

	// 全体の残高を計算（収入 + 支出）
	public static int calculateBalance(ArrayList<MukkunTransaction> transactions) {
		int total = 0;
		for (MukkunTransaction t : transactions) {
			total += t.getAmount();
		}
		return total;
	}
	
	// 指定カテゴリの合計金額を計算
	public static int calculateCategoryTotal(ArrayList<MukkunTransaction>transactions, String targetCategory) {
		int total = 0;
		for(MukkunTransaction t : transactions) {
			if(t.getCategory().equals(targetCategory)) {
				total += t.getAmount();
			}
		}
		return total;
	}
	
	// 支出合計を計算（マイナス金額を反転して加算）
	public static int calculateTotalExpense(ArrayList<MukkunTransaction>transactions) {
		int total = 0;
		for(MukkunTransaction t : transactions) {
			if(t.getAmount() < 0) {
				total += -t.getAmount();	// 支出はマイナスなので反転
			}
		}
		return total;
	}
	
	// むっくんおこ度ゲージを表示（支出額に応じて変化）
	public static void showMukumukuGauge(int expenseTotal) {
		int mukumukuLevel = 0;
		if(expenseTotal < 5000) {
			mukumukuLevel = 2;
		}else if(expenseTotal < 10000) {
			mukumukuLevel = 5;
		}else if(expenseTotal < 15000) {
			mukumukuLevel = 8;
		}else {
			mukumukuLevel = 10;
		}
		
		// ゲージ表示
		System.out.println("＝＝＝ むっくんおこ度ゲージ ＝＝＝");
		System.out.print("おこ度：[");
		
		for (int i = 0; i < mukumukuLevel; i++) {
			System.out.print("🐻");
		}
		for(int i = mukumukuLevel; i < 10; i++) {
			System.out.print("〇");
		}
		System.out.println("]");
		
		// レベルに応じたコメント
		if(mukumukuLevel <= 2) {
			System.out.println("😌 げんきだよ〜");
		}else if( mukumukuLevel <= 5) {
			System.out.println("😐 ちゅういかも～");
		}else if(mukumukuLevel <= 8) {
			System.out.println("😣 そろそろやばいよ～");
		}else {
			System.out.println("😣 げんかいだよ～...つかいすぎ！");
		}
		System.out.println("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
	}
}

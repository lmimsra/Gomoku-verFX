import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;


/**
 * Created by mmr on 2016/06/24.
 */

public class LSub {
	private int Jun = 0;
	private int BWin = 0;
	private int WWin = 0;
	private int Draw = 0;

	private int TimerS = 0;
	private int TimerM = 0;

	private int BTS = 0;
	private int BTM = 0;

	private int WTS = 0;
	private int WTM = 0;

	GFrame Frame;
	GFile file;
	Alert alrt;
	LocalTime time = LocalTime.now();
	LocalDate day = LocalDate.now();
	LocalDateTime DandT = LocalDateTime.now();


	VBox Lbox;      //左カラム　
	VBox Ubox;      //上カラム
	HBox Bbox;      //下カラム
	Label l1 = new Label("今の手番は");
	Label l2 = new Label("　　　黒");
	Label l3 = new Label("                           です");
	Label log = new Label("黒" + BWin + "勝　白" + WWin + "勝　引き分け" + Draw);
	Label notPut = new Label("");
	Label now = new Label("" + time.getHour() + " : " + time.getMinute());

	Label BTL = new Label("黒の使用時間(合計)" + String.format("%02d", BTM) + " : " + String.format("%02d", BTS));
	Label WTL = new Label(String.format("%02d", WTM) + " : " + String.format("%02d", WTS) + "白の使用時間(合計)");
	Label NowTimer = new Label(String.format("%02d", TimerM) + " : " + String.format("%02d", TimerS));

	TextArea txt = new TextArea();
	Button reset = new Button("Reset");
	Button restart = new Button("Restart");
	Button exit = new Button("Exit Game");
	Button Output = new Button("盤面情報を出力");
	Button Input = new Button("ファイルから入力");
	Button Edit = new Button("ファイルの編集");

	String next = System.getProperty("line.separator");//改行文
	Label toDay = new Label("一手につき何度も置き直し、" + next + "考えることができます。" + next + "同じ場所をもう一度クリックすることで" + next + "置き場所を確定できます。");

	LSub() throws IOException {

		//時計処理
		ScheduledService<Void> service = new ScheduledService<Void>() {

			@Override
			protected Task<Void> createTask() {
				Task<Void> task = new Task<Void>() {
					@Override
					protected Void call() throws Exception {

						Thread.sleep(1000);
						time = LocalTime.now();
						TimerS += 1;
						if (Jun == 0) BTS += 1;
						else WTS += 1;

						if (TimerS == 60) {
							TimerM += 1;
							TimerS = 0;
						}

						if (BTS == 60) {
							BTS = 0;
							BTM += 1;
						}

						if (WTS == 60) {
							WTS = 0;
							WTM += 1;
						}
						Platform.runLater(() -> BTL.setText("黒の使用時間(合計)" + String.format("%02d", BTM) + " : " + String.format("%1$02d", BTS)));
						Platform.runLater(() -> WTL.setText(String.format("%02d", WTM) + " : " + String.format("%1$02d", WTS) + "白の使用時間(合計)"));
						Platform.runLater(() -> NowTimer.setText(String.format("%02d", TimerM) + " : " + String.format("%1$02d", TimerS)));

						Platform.runLater(() -> now.setText("" + time.getHour() + " 時 " + time.getMinute() + " 分 " + time.getSecond() + " 秒"));

						return null;
					}
				};

				return task;
			}
		};
		service.setDelay(Duration.seconds(0));
		service.start();

		l1.setFont(new Font("Arial", 20));
		l2.setFont(new Font("Arial", 30));
		l3.setFont(new Font("Arial", 20));
		notPut.setId("NotFound");


		txt.setMaxHeight(300);
		txt.setPrefHeight(300);
		txt.setMaxWidth(250);
		txt.setEditable(false);
		txt.setId("txtarea");
		//for (int i = 0;i<10;i++) txt.insertText(0,"add"+i+""+next); //入力テスト

		//アラートの設定
		alrt = new Alert(Alert.AlertType.NONE, "", ButtonType.YES, ButtonType.NO);
		alrt.setTitle("");
		alrt.setHeaderText("       確認");

		//リセットボタン
		reset.setId("Reset");
		reset.setPrefSize(100, 20);
		reset.setCenterShape(true);
		reset.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				alrt.setContentText("全ての内容をリセットしますよろしいですか？");
				Optional<ButtonType> result = alrt.showAndWait();
				if (result.get() == ButtonType.YES) {
					Reset();

				}
			}
		});

		//リスタートボタン
		restart.setId("Restart");
		restart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				alrt.setContentText("盤面をリセットしてこの回を無効にします");
				Optional<ButtonType> result = alrt.showAndWait();
				if (result.get() == ButtonType.YES) {
					Restart();
				}
			}
		});

		//終了ボタン
		exit.setId("Exit");
		exit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				alrt.setContentText("ゲームを終了してよろしいですか？");
				Optional<ButtonType> result = alrt.showAndWait();
				if (result.get() == ButtonType.YES) {
					Platform.exit();
				}
			}
		});
		exit.setPrefSize(100, 45);
		exit.setPrefSize(250, 20);

		//アウトプットボタン
		Output.setId("Put");
		Output.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				file.setOutputStage();

		/*	try {
					alrt.setContentText("現在の日時をファイル名とした盤面と手番の情報を出力します");
					Optional<ButtonType> result = alrt.showAndWait();
					if(result.get()==ButtonType.YES) file.outputFile();
				}catch (IOException e){

				}
		*/

			}
		});

		//インプットボタン
		Input.setId("Put");
		Input.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					file.setInputStage();
				} catch (IOException e) {

				}


			/*	try {
					alrt.setContentText("ファイルから盤面情報を入力します");
					Optional<ButtonType> reselt =alrt.showAndWait();
					if (reselt.get()==ButtonType.YES) file.inputFile();
				}catch (IOException e){

				}
				*/
			}
		});

		Edit.setId("Put");
		Edit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				file.setEditStage();
			}
		});

		BTL.setPrefWidth(250);
		BTL.setAlignment(Pos.CENTER);
		BTL.setFont(new Font(20));

		WTL.setPrefWidth(250);
		WTL.setAlignment(Pos.CENTER);
		WTL.setFont(new Font(20));

		NowTimer.setPrefWidth(100);
		NowTimer.setAlignment(Pos.CENTER);
		NowTimer.setFont(new Font(20));

		now.setPrefWidth(170);
		now.setAlignment(Pos.CENTER);

		Lbox = new VBox();
		Ubox = new VBox();
		Bbox = new HBox();

		Border Style = new Border(new BorderStroke(
						Color.BLACK, BorderStrokeStyle.SOLID,
						new CornerRadii(0.1),
						new BorderWidths(1.0)
		));
		Lbox.setId("LBOX");
		//	Lbox.setStyle("-fx-background-color: oldlace");  //CSSを使わない書き方がわからん
		Lbox.setMaxHeight(600);
		Lbox.setMaxWidth(300);
		Lbox.setPrefHeight(600);
		Lbox.setPrefWidth(300);
		Lbox.setSpacing(10);
		Lbox.setPadding(new Insets(20));
		Lbox.setBorder(Style);
		Lbox.getChildren().setAll(l1, l2, l3, log, notPut, txt, toDay);


		Ubox.setId("RBOX");
		VBox UVbox = new VBox();
		HBox UHbox = new HBox();
		UHbox.getChildren().addAll(BTL, NowTimer, WTL);
		UHbox.setAlignment(Pos.CENTER);
		UHbox.setSpacing(150);

		//Ubox.setStyle("-fx-background-color: darkgray");
		Ubox.setPrefHeight(60);
		Ubox.setPrefWidth(900);
		Ubox.setSpacing(10);
		Ubox.setPadding(new Insets(10));
		Ubox.setBorder(Style);
		Ubox.setAlignment(Pos.CENTER);
		Ubox.getChildren().addAll(now, UHbox);


		Bbox.setId("BBOX");
		Bbox.setPrefSize(900, 50);
		Bbox.setSpacing(12);
		Bbox.setPadding(new Insets(5));
		Bbox.setBorder(Style);
		Bbox.setAlignment(Pos.CENTER_RIGHT);
		Bbox.getChildren().addAll(Output, Input, Edit, reset, restart, exit);

	}

	//クラスを入手
	public void getFrame(GFrame frame) {
		Frame = frame;
	}

	//クラスを入手
	public void getGFile(GFile f) {
		file = f;
	}

	//リスタート関数
	public void Restart() {
		Frame.Reset();
		setB();
		setNotPut(2);
		BTM = BTS = WTM = WTS = TimerM = TimerS = 0;
		BTL.setText("黒の使用時間(合計)" + String.format("%02d", BTM) + " : " + String.format("%02d", BTS));
		WTL.setText(String.format("%02d", WTM) + " : " + String.format("%1$02d", WTS) + "白の使用時間(合計)");
		NowTimer.setText(String.format("%02d", TimerM) + " : " + String.format("%1$02d", TimerS));
	}

	//リセット関数
	public void Reset() {
		Frame.Reset();
		setNotPut(2);
		BWin = WWin = Draw = 0;
		txt.setText("");
		log.setText("黒" + BWin + "勝　白" + WWin + "勝　引き分け" + Draw);
		setB();
		BTM = BTS = WTM = WTS = TimerM = TimerS = 0;
		BTL.setText("黒の使用時間(合計)" + String.format("%02d", BTM) + " : " + String.format("%02d", BTS));
		WTL.setText(String.format("%02d", WTM) + " : " + String.format("%1$02d", WTS) + "白の使用時間(合計)");
		NowTimer.setText(String.format("%02d", TimerM) + " : " + String.format("%1$02d", TimerS));

	}

	//勝利時用リセット関数
	public void EndAndReset() {
		log.setText("黒" + BWin + "勝　白" + WWin + "勝　引き分け" + Draw);
		Frame.Reset();
		setB();
	}

	//勝利者を表示させる
	public void addWinner(int w) {
		if (w == 0) {
			BWin++;
			txt.insertText(0, "黒の勝ち！" + next);
			Restart();
		} else if (w == 1) {
			WWin++;
			txt.insertText(0, "白の勝ち！" + next);
			Restart();
		} else {
			Draw++;
			txt.insertText(0, "引き分け！" + next);
			Restart();
		}

	}

	//時計情報を送る
	public int[] sendTime() {
		int[] result = new int[6];  // 0:トータル, 1:黒, 2:白
		result[0] = TimerM;
		result[1] = TimerS;
		result[2] = BTM;
		result[3] = BTS;
		result[4] = WTM;
		result[5] = WTS;
		return result;
	}

	public void setTime(int[] t) {
		TimerM = t[0];
		TimerS = t[1];
		BTM = t[2];
		BTS = t[3];
		WTM = t[4];
		WTS = t[5];
		BTL.setText("黒の使用時間(合計)" + String.format("%02d", BTM) + " : " + String.format("%1$02d", BTS));
		WTL.setText(String.format("%02d", WTM) + " : " + String.format("%1$02d", WTS) + "白の使用時間(合計)");
		NowTimer.setText(String.format("%02d", TimerM) + " : " + String.format("%1$02d", TimerS));

	}

	//置けない時メッセージを表示,おけるとき非表示
	public void setNotPut(int i) {
		if (i == 1) notPut.setText("そこは置けませんよぉぉ？");
		else notPut.setText("");
	}

	//手番変更：黒へ
	public void setB() {
		l2.setText("　　　黒");      //黒をセット
		Jun = 0;
		TimerM = TimerS = 0;
		NowTimer.setText(String.format("%02d", TimerM) + " : " + String.format("%1$02d", TimerS));
	}

	//手番変更：白へ
	public void setW() {
		l2.setText("　　　白");      //白をセット
		Jun = 1;
		TimerM = TimerS = 0;
		NowTimer.setText(String.format("%02d", TimerM) + " : " + String.format("%1$02d", TimerS));
	}


}

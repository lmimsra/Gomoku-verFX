
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.time.LocalTime;
import java.util.Optional;

/**
 * Created by mmr on 2016/06/27.
 */


public class GEvent implements EventHandler<MouseEvent>,Runnable {
	GJudge Jud;
	GFrame frame;
	Alert winner;
	Optional<ButtonType> result;
	int counts;
	int pX, pY;  //一手前のx、y
	int nX, nY;  //今のx、y

	//自信判定ステージ
	Stage HowStage;
	Button cancel;
	Button[] g;


	GEvent(GFrame f) {
		frame = f;
		Jud = new GJudge(frame);
		winner = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.NEXT);
		counts = 1;
		pX = pY = 0;
		createStage();

		Service<Void> serviceK = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				Task<Void> task = new Task<Void>() {
					@Override
					protected Void call() throws Exception {

						Thread.sleep(1000);


						System.out.println("起動ちゅう");

					//	Platform.runLater(() -> );

						return null;
					}
				};

				return task;
			}
		};
		serviceK.start();

	}

	@Override
	public void handle(MouseEvent event) {
		int x, y;
		int win = 4;    //勝利判定用変数
		x = Msearchx(event.getX());
		y = Msearchy(event.getY());
		//置けるか判定
		if (frame.Arr[x][y] == frame.getJun() && frame.Jdg[x][y] == 1) {
			nX = x;
			nY = y;
			HowStage.show();
		} else PutS(x, y);

		//System.out.println("" + x + "," + y);

	}

	public void run(){

	}

	//ただ石を置くだけ
	public void PutS(int x, int y) {
		if (frame.CheckF(x, y)) {
			System.out.println(pX + " " + pY);
			frame.notPut(0);
			frame.pushS(x, y);
			frame.drawS(x, y);
			if (frame.Jdg[pX][pY] == counts) {
				frame.setDF(pX, pY);
				frame.clearS(pX, pY);
				frame.Jdg[pX][pY] = 0;
			}
			frame.Jdg[x][y] = counts;
			pX = x;
			pY = y;
		} else frame.notPut(1);
	}

	//勝利判定
	public void Judge(int x, int y) {
		int win = 4;    //勝利判定用変数
		if (frame.Jdg[x][y] == 1) {

			win = Jud.Judge(x, y);    //勝利判定　戻り値　0:黒勝　1:白勝　2:引分 3:続行

			if (win == 0) {
				winner.setHeaderText("黒の勝ち！");
				result = winner.showAndWait();
				if (result.get() == ButtonType.NEXT) frame.Win(win);
			} else if (win == 1) {
				winner.setHeaderText("白の勝ち！");
				result = winner.showAndWait();
				if (result.get() == ButtonType.NEXT) frame.Win(win);
			} else if (win == 2) {
				winner.setHeaderText("引き分け！");
				result = winner.showAndWait();
				if (result.get() == ButtonType.NEXT) frame.Win(win);
			} else {
				frame.ChangeP();

			}
		} else {
			System.out.println("置けません！！");
			frame.notPut(1);

		}
	}

	public void createStage() {
		Border Style = new Border(new BorderStroke(
						Color.BLACK, BorderStrokeStyle.SOLID,
						new CornerRadii(0.1),
						new BorderWidths(1.0)
		));

		Label feeling = new Label("その一手、自信のほどは？");
		feeling.setFont(new Font(35));

		Label grade = new Label("自信無し　＜ーーー　        　　　　　ーーー＞　自信あり");

		g = new Button[5];
		g[0] = new Button("と、とりあえずここで");
		g[1] = new Button("ちょっと心配");
		g[2] = new Button("まぁまぁかな");
		g[3] = new Button("なかなかの一手！");
		g[4] = new Button("最高の一手！");
		cancel = new Button("選択に戻る");
		VBox MainBox = new VBox();
		MainBox.setId("BBOX");
		MainBox.setBorder(Style);
		MainBox.setSpacing(10);
		MainBox.setAlignment(Pos.CENTER);

		HBox Buttons = new HBox();
		Buttons.setId("BBOX");
		Buttons.setSpacing(5);
		Buttons.setAlignment(Pos.CENTER);
		Buttons.setMaxSize(600,100);

		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				HowStage.close();
			}
		});

		g[0].setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				frame.Feel[nX][nY] = 1;
				Judge(nX, nY);
				frame.ResetJ();
				HowStage.close();

			}
		});

		g[1].setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				frame.Feel[nX][nY] = 2;
				Judge(nX, nY);
				frame.ResetJ();
				HowStage.close();

			}
		});

		g[2].setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				frame.Feel[nX][nY] = 3;
				Judge(nX, nY);
				frame.ResetJ();
				HowStage.close();

			}
		});

		g[3].setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				frame.Feel[nX][nY] = 4;
				Judge(nX, nY);
				frame.ResetJ();
				HowStage.close();

			}
		});

		g[4].setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				frame.Feel[nX][nY] = 5;
				Judge(nX, nY);
				frame.ResetJ();
				HowStage.close();

			}
		});


		for (Button i : g
						) {
			Buttons.getChildren().add(i);
		}

		MainBox.getChildren().addAll(feeling, grade, Buttons, cancel);

		Scene feelingS = new Scene(MainBox, 700, 300);
		feelingS.getStylesheets().addAll("gFX.css");
		HowStage = new Stage();
		HowStage.setScene(feelingS);
		HowStage.initStyle(StageStyle.TRANSPARENT);
		HowStage.getScene().getRoot().setStyle("-fx-background-color: transparent");

	}

	public int Msearchx(double x) {
		int t;
		t = (int) x / frame.getLong();
		//System.out.println("" + t);
		return t;
	}

	public int Msearchy(double y) {
		int t;
		t = (int) y / frame.getLong();
		//System.out.println("" + t);
		return t;
	}


}

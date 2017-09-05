import com.sun.javafx.geom.Path2D;
import com.sun.org.glassfish.gmbal.ParameterNames;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.util.Optional;

/**
 * Created by mmr on 2016/06/27.
 */


public class GEvent implements EventHandler<MouseEvent> {
	private int Bcount = 0;  //黒の手数
	private int BTcount = 0;  //黒の総手数
	private int BFeel = 0;    //黒の自信総和
	private double BFAve = 0;
	private double BTAve = 0;

	private int Wcount = 0;  //白の手数
	private int WTcount = 0;  //白の総手数
	private int WFeel = 0;    //白の自信総和
	private double WFAve = 0;
	private double WTAve = 0;


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
	Slider slider;
	Canvas grades;
	GraphicsContext gc;

	//リザルトステージ
	Stage ResultStage;
	Button next;
	GridPane grid = new GridPane();
	Label winnerL = new Label("");
	Label ResultL = new Label("");
	Label b1 = new Label("");
	Label b2 = new Label("");
	Label b3 = new Label("");
	Label w1 = new Label("");
	Label w2 = new Label("");
	Label w3 = new Label("");

	GEvent(GFrame f) {
		frame = f;
		Jud = new GJudge(frame);
		winner = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.NEXT);
		counts = 1;
		pX = pY = 0;
		createStage();
		PopResult();

	}

	@Override
	public void handle(MouseEvent event) {
		if (event.getEventType().toString() == "MOUSE_MOVED") System.out.println("aaaa");
		int x, y;
		x = Msearchx(event.getX());
		y = Msearchy(event.getY());
		if (frame.CheckF(x, y)) {
			frame.notPut(0);
			nX = x;
			nY = y;
//			HowStage.show();
			PutS(nX,nY);
		} else {
			System.out.println("置けません！！");
			frame.notPut(1);
		}

//		System.out.println(event.getEventType().toString());

		//置けるか判定
//		if (frame.Arr[x][y] == frame.getJun() && frame.Jdg[x][y] == 1) {
//			nX = x;
//			nY = y;
//			HowStage.showAndWait();
//			frame.drawS(x,y,frame.Bgc);
//			frame.clearS(pX,pY);
//		} else {
//			//手数トータルを加算
//			if (frame.getJun() == 0) BTcount += 1;
//			else WTcount += 1;
//
//			PutS(x, y);
//		}

		//System.out.println("" + x + "," + y);

	}


	//ただ石を置くだけ
	public void PutS(int x, int y) {
		if (frame.CheckF(x, y)) {
			//System.out.println(pX + " " + pY);
			frame.notPut(0);
			frame.pushS(x, y);
			frame.drawS(x, y, frame.gc);
			Judge(x,y);
			frame.ResetJ();
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


		win = Jud.Judge(x, y);    //勝利判定　戻り値　0:黒勝　1:白勝　2:引分 3:続行

		if (win == 0) {
			winnerL.setText("黒の勝ち！");
			UpdateResult();
			ResultStage.showAndWait();
			frame.Win(win);
		} else if (win == 1) {
			winnerL.setText("白の勝ち！");
			UpdateResult();
			ResultStage.showAndWait();
			frame.Win(win);
		} else if (win == 2) {
			winnerL.setText("引き分け！");
			UpdateResult();
			ResultStage.showAndWait();
			frame.Win(win);
		} else {
			frame.ChangeP();

		}
	}

	//自信選択ウィンドウ作成
	public void createStage() {
		Border Style = new Border(new BorderStroke(
						Color.BLACK, BorderStrokeStyle.SOLID,
						new CornerRadii(0.1),
						new BorderWidths(1.0)
		));


		//自信評価ウィンドウ
		Label feeling = new Label("その一手、自信のほどは？");
		feeling.setFont(new Font(35));

		slider = new Slider(0, 100, 50);
		slider.setOrientation(Orientation.HORIZONTAL);
		slider.setSnapToTicks(false);
		slider.setShowTickMarks(false);
		slider.setMaxSize(500, 20);

		grades = new Canvas(500, 50);
		gc = grades.getGraphicsContext2D();
		Stop[] stops = new Stop[]{new Stop(0, Color.BLUE),
						new Stop(0.5, Color.LIGHTGREEN),
						new Stop(1, Color.RED)
		};

		double[] xp = {0, 500, 500};
		double[] yp = {50, 0, 50};


		LinearGradient gra = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
		gc.setFill(gra);
//		gc.setFill(Color.WHITE);
		gc.fillPolygon(xp, yp, 3);
		//	gc.fillRect(0, 0, 500, 50);

		Label grade = new Label("自信無し　＜ーーー　        　　　　　ーーー＞　自信あり");

		Button next = new Button("  次へ  ");
		next.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (frame.getJun() == 0) {
					BTcount++;
					Bcount++;
				} else {
					WTcount++;
					Wcount++;
				}

				frame.Feel[nX][nY] = (int) slider.getValue();
				if (frame.getJun() == 0) BFeel += frame.Feel[nX][nY];
				else WFeel += frame.Feel[nX][nY];
//				System.out.println(frame.Feel[nX][nY]);
				frame.pushS(nX, nY);
				frame.drawS(nX, nY, frame.gc);
				Judge(nX, nY);
				frame.ResetJ();
				HowStage.close();
			}
		});
		next.setPrefWidth(100);

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
		Buttons.setMaxSize(600, 100);

		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (frame.getJun() == 0) {
					BTcount++;
				} else {
					WTcount++;
				}
				HowStage.close();
			}
		});
		cancel.setPrefWidth(100);

		g[0].setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (frame.getJun() == 0) BFeel += 1;
				else WFeel += 1;

				frame.Feel[nX][nY] = 1;
				Judge(nX, nY);
				frame.ResetJ();
				HowStage.close();

			}
		});

		g[1].setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (frame.getJun() == 0) BFeel += 2;
				else WFeel += 2;
				frame.Feel[nX][nY] = 2;
				Judge(nX, nY);
				frame.ResetJ();
				HowStage.close();

			}
		});

		g[2].setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (frame.getJun() == 0) BFeel += 3;
				else WFeel += 3;
				frame.Feel[nX][nY] = 3;
				Judge(nX, nY);
				frame.ResetJ();
				HowStage.close();

			}
		});

		g[3].setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (frame.getJun() == 0) BFeel += 4;
				else WFeel += 4;
				frame.Feel[nX][nY] = 4;
				Judge(nX, nY);
				frame.ResetJ();
				HowStage.close();

			}
		});

		g[4].setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (frame.getJun() == 0) BFeel += 5;
				else WFeel += 5;
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

		HBox select = new HBox();
		select.setStyle("-fx-background-color: transparent");
		select.getChildren().addAll(cancel, next);
		select.setAlignment(Pos.CENTER);
		select.setSpacing(20);
		select.setPadding(new Insets(0, 30, 0, 30));

		MainBox.getChildren().addAll(feeling, grade, grades, slider, select);

		Scene feelingS = new Scene(MainBox, 700, 300);
		feelingS.getStylesheets().addAll("gFX.css");
		HowStage = new Stage();
		HowStage.setScene(feelingS);
		HowStage.initStyle(StageStyle.TRANSPARENT);
		HowStage.initModality(Modality.APPLICATION_MODAL);
		HowStage.setX(100);
		HowStage.setY(100);
		HowStage.getScene().getRoot().setStyle("-fx-background-color: transparent");


	}

	//リザルトウィンドウ作成
	public void PopResult() {
		Font font = new Font(20);
		Font Large = new Font(30);
		VBox vBox = new VBox();
		vBox.setId("BBOX");
		Label Kuro = new Label("黒");
		Label Shiro = new Label("白");
		Label Exp1 = new Label("時間");
		Label Exp2 = new Label("１手の平均時間");
		Label Exp3 = new Label("自信の平均");

		grid.setAlignment(Pos.CENTER);
		grid.setPadding(new Insets(10));
		grid.setHgap(50);
		grid.setVgap(20);

		Kuro.setFont(Large);
		Shiro.setFont(Large);
		b1.setFont(font);
		b2.setFont(font);
		b3.setFont(font);
		w1.setFont(font);
		w2.setFont(font);
		w3.setFont(font);

		winnerL.setFont(Large);

		next = new Button("次へ");
		next.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ResultStage.close();
			}
		});

		b1.setText("" + BTcount);
		b2.setText(String.format("%.2f", BTAve));
		b3.setText(String.format("%.2f", BFAve));
		w1.setText("" + WTcount);
		w2.setText(String.format("%.2f", WTAve));
		w3.setText(String.format("%.2f", WFAve));


		grid.add(Kuro, 1, 0);
		grid.add(Shiro, 2, 0);
		grid.add(Exp1, 0, 1);
		grid.add(Exp2, 0, 2);
		grid.add(Exp3, 0, 3);
		grid.add(b1, 1, 1);
		grid.add(b2, 1, 2);
		grid.add(b3, 1, 3);
		grid.add(w1, 2, 1);
		grid.add(w2, 2, 2);
		grid.add(w3, 2, 3);

		vBox.setAlignment(Pos.CENTER);
		vBox.getChildren().addAll(winnerL, ResultL, grid, next);

		Scene Res = new Scene(vBox, 400, 300);
		Res.getStylesheets().addAll("gFX.css");
		ResultStage = new Stage();
		ResultStage.setScene(Res);
		//	ResultStage.initStyle(StageStyle.TRANSPARENT);
		ResultStage.getScene().getRoot().setStyle("-fx-background-color: transparent");
		ResultStage.initModality(Modality.APPLICATION_MODAL);

	}

	public void UpdateResult() {
		AveCal();
		int[] result;  // 0:トータル, 1:黒, 2:白
		result = frame.getTime();

		b1.setText(result[2] + "分" + result[3] + "秒");
		b2.setText(String.format("%.2f", BTAve));
		b3.setText(String.format("%.2f", BFAve));
		w1.setText("" + result[4] + "分" + result[5] + "秒");
		w2.setText(String.format("%.2f", WTAve));
		w3.setText(String.format("%.2f", WFAve));


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

	//各数値の平均を計算する
	public void AveCal() {
		int[] result;  // 0:トータル, 1,2:黒, 3,4:白
		result = frame.getTime();

		BFAve = (double) BFeel / (double) Bcount;
		BTAve = (double) (result[2] * 60 + result[3]) / (double) Bcount;

		WFAve = (double) WFeel / (double) Wcount;
		WTAve = (double) (result[4] * 60 + result[5]) / (double) Wcount;
	}

	//privateの数値全てを初期化
	public void ResetAll() {
		Bcount = BTcount = BFeel = Wcount = WTcount = WFeel = 0;
		BFAve = BTAve = WFAve = WTAve = 0;

	}

	//privateの値を渡す
	public int[] getValue() {
		int[] Pvalue = {Bcount, BFeel, Wcount, WFeel};
		return Pvalue;
	}

	public void setValue(int[] v) {
		Bcount = v[0];
		BFeel = v[1];
		Wcount = v[2];
		WFeel = v[3];
	}
}

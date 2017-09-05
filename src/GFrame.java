
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;


/**
 * Created by mmr on 2016/06/24.
 */
public class GFrame {
	Canvas canvas;        //石を置く
	Canvas Back;          //背景
	Canvas moving;        //入力途中の石
	GraphicsContext gc;
	GraphicsContext Bgc;
	GraphicsContext Mgc;
	LSub Sub;
	GEvent Ev;
	GFile file;
	Image image;
	Pane banmen;

	EventHandler<MouseEvent> Enter;
	private int bX, bY;
	private int nx, ny;


	private int Masu = 15;      //マス目の数
	private int Long = 40;    //マスの幅
	private int Jun = 0;  //順番を保存　黒０、白１
	private int BS = 0;   //黒石
	private int WS = 1;   //白石
	private int DF = -1;   //無し
	int[][] Arr;    //盤面情報
	int[][] Jdg;    //複数入力用判定配列
	int[][] Feel;//自信のほどはいかに？ 1~5の5段階評価
	int[] P;
	int margin = 5;
	Circle en;

	GFrame() {
		Arr = new int[Masu][Masu];
		Jdg = new int[Masu][Masu];
		Feel = new int[Masu][Masu];
		P = new int[2];
		Ev = new GEvent(this);
		canvas = new Canvas(600, 600);
		canvas.setOnMouseClicked(Ev);
		gc = canvas.getGraphicsContext2D();

		Back = new Canvas(600, 600);
		Bgc = Back.getGraphicsContext2D();

		moving = new Canvas(600, 600);
		Mgc = moving.getGraphicsContext2D();
		moving.setOnMouseClicked(Ev);
		Mgc.setGlobalAlpha(0.6);
		Mgc.setStroke(Color.RED);
		Mgc.setLineWidth(3);

		image = new Image("tree.jpg");
		banmen = new Pane();

		banmen.getChildren().addAll(moving, canvas, Back);
		canvas.toFront();
		moving.toFront();
		en = new Circle(150, Color.ORANGE);

/*		Enter = (event) -> {
			//	System.out.println(bX);
			bX = event.getX();
			bY = event.getY();
			moveS(event.getX(),event.getY());
			gc.setFill(color.black);
			gc.fillrect(event.getx() * Long + margin, event.getY() * Long + margin, Long - 1.5 * margin, Long - 1.5 * margin);
		};
*/
		ScheduledService<Void> point = new ScheduledService<Void>() {
			@Override
			protected Task<Void> createTask() {
				Task<Void> task = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						Thread.sleep(100);
						//	double x = bX;
						//	double y = bY;
						//	System.out.println(x);
						//Platform.runLater(() -> Mgc.fillOval(x * Long + margin, y * Long + margin, Long - 1.5 * margin, Long - 1.5 * margin));
						//Platform.runLater(() -> drawmoving(x, y));
						return null;
					}
				};
				return task;
			}
		};
		point.setDelay(Duration.seconds(0));
		point.start();


		moving.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				clearS(nx, ny);
				P = getNearestCell(event.getX(), event.getY());
				nx = P[0];
				ny = P[1];
				drawS(nx, ny, Mgc);
				//System.out.println(nx);
			}
		});

		drawF();
		CArray();
		ResetJ();
	}

	public void getSF(LSub sub, GFile f) throws IOException {
		Sub = sub;
		file = f;
	}

	//リセット用
	public void Reset() {
		gc.clearRect(0, 0, 600, 600);
		Mgc.clearRect(0, 0, 600, 600);
		CArray();
		ResetJ();
		Jun = 0;
		Ev.ResetAll();
	}

	//判定配列リセット
	public void ResetJ() {
		for (int i = 0; i < Masu; i++) {
			for (int j = 0; j < Masu; j++) {
				Jdg[i][j] = 0;
			}
		}
	}

	//配列の初期化
	public void CArray() {
		for (int i = 0; i < Masu; i++) {
			for (int j = 0; j < Masu; j++) {
				Arr[i][j] = DF;
				Jdg[i][j] = 0;
			}


		}
	}

	//盤面の描写(背面キャンバス)
	public void drawF() {
		Bgc.setFill(Color.ORANGE);
		Bgc.setStroke(Color.BLACK);
		Bgc.setLineWidth(2.0);
		//gc.fillRect(0, 0, 600, 600);					//背景色を描写する
		Bgc.drawImage(image, 0, 0, 600, 600);    //指定されたイメージを描写する
		Bgc.strokeRect(0, 0, 600, 600);

		//線の描画
		Bgc.setLineWidth(1.0);
		for (int i = 0; i < Masu; i++) {
			Bgc.strokeLine(0, (int) ((0.5 + i) * Long), 600, (int) ((0.5 + i) * Long));
			Bgc.strokeLine((int) ((0.5 + i) * Long), 0, (int) ((0.5 + i) * Long), 600);
		}

		//点の描画
		Bgc.setFill(Color.BLACK);
		Bgc.fillOval(295, 295, 10, 10);    //中央
		Bgc.fillOval(95, 95, 10, 10);    //左上
		Bgc.fillOval(495, 95, 10, 10);   //右上
		Bgc.fillOval(95, 495, 10, 10);   //左下
		Bgc.fillOval(495, 495, 10, 10);  //右下
	}

	//配列に指定の値を入れる
	public void pushS(int x, int y) {
		Arr[x][y] = Jun;

	}

	//配列にDF値を入れる
	public void setDF(int x, int y) {
		Arr[x][y] = DF;
	}

	//ファイル入力用の石
	public void drawSPre(int x, int y) {
		if (Arr[x][y] == BS) { //該当箇所が黒い石

			gc.strokeOval(x * Long + margin, y * Long + margin, Long - 2 * margin, Long - 2 * margin);
			gc.strokeOval(x * Long + margin + 1, y * Long + margin + 1, Long - 2 * margin - 2, Long - 2 * margin - 2);
			gc.fillOval(x * Long + margin, y * Long + margin, Long - 2 * margin, Long - 2 * margin);
		}
		if (Arr[x][y] == WS) { //該当箇所が白い石
			gc.strokeOval(x * Long + margin, y * Long + margin, Long - 2 * margin, Long - 2 * margin);
			gc.strokeOval(x * Long + margin + 1, y * Long + margin + 1, Long - 2 * margin - 2, Long - 2 * margin - 2);
			gc.setFill(Color.WHITE);
			gc.fillOval(x * Long + margin + 1, y * Long + margin + 1, Long - 2 * margin - 2, Long - 2 * margin - 2);
		}


	}

	//座標から配列番号を返す
	public int[] getNearestCell(double x, double y) {
		int[] I = new int[2];
		I[0] = (int) x / Long;
		//System.out.println("" + t);

		I[1] = (int) y / Long;
		//System.out.println("" + t);

		return I;
	}

	//指定した点に石を描く
	public void drawS(int x, int y, GraphicsContext G) {
		if (Jun == 0) { //黒の番なら
			G.setFill(Color.BLACK);
			G.strokeOval(x * Long + margin, y * Long + margin, Long - 2 * margin, Long - 2 * margin);
			G.strokeOval(x * Long + margin + 1, y * Long + margin + 1, Long - 2 * margin - 2, Long - 2 * margin - 2);
			G.fillOval(x * Long + margin, y * Long + margin, Long - 2 * margin, Long - 2 * margin);
		}
		if (Jun == 1) { //白の番なら
			G.setFill(Color.BLACK);
			G.strokeOval(x * Long + margin, y * Long + margin, Long - 2 * margin, Long - 2 * margin);
			G.strokeOval(x * Long + margin + 1, y * Long + margin + 1, Long - 2 * margin - 2, Long - 2 * margin - 2);
			G.setFill(Color.WHITE);
			G.fillOval(x * Long + margin + 1, y * Long + margin + 1, Long - 2 * margin - 2, Long - 2 * margin - 2);
		}


	}

	//指定した点の石を消す
	public void clearS(int x, int y) {
		Mgc.clearRect(x * Long + margin - 2, y * Long + margin - 2, Long - 1 * margin, Long - 1 * margin);

	}

	//置けるか判定
	public boolean CheckF(int x, int y) {
		return Arr[x][y] == DF;
	}

	//手番変更
	public void ChangeP() {
		if (Jun == 0) {
			Sub.setW();
			Jun = 1;
			gc.setFill(Color.WHITE);
			Mgc.setFill(Color.WHITE);
		} else {
			Sub.setB();
			Jun = 0;
			gc.setFill(Color.BLACK);
			Mgc.setFill(Color.BLACK);
		}
	}

	//勝利判定をSubに送る
	public void Win(int w) {
		Sub.addWinner(w);
	}

	//Longを返す
	public int getLong() {
		return Long;
	}

	//Masuを返す
	public int getMasu() {
		return Masu;
	}

	//DFを返す
	public int getDF() {
		return DF;
	}

	//現在の手番を返す
	public int getJun() {
		return Jun;
	}

	//手番をセット
	public void setJun(int i) {
		Jun = i;
		if (Jun == 0) {
			Sub.setB();
		} else {
			Sub.setW();
		}

	}

	//Subからの値を送る
	public int[] getTime() {
		return Sub.sendTime();
	}

	//Fileからの値を送る
	public int[] getValue() {
		return Ev.getValue();
	}

	//時間をセットする
	public void setTime(int[] t) {
		Sub.setTime(t);
	}

	//白黒それぞれの進行度をセットする
	public void setValues(int[] v) {
		Ev.setValue(v);
	}

	//Subにイベントを反映
	public void notPut(int i) {
		Sub.setNotPut(i);
	}


}

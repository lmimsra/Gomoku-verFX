
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;


/**
 * Created by mmr on 2016/06/24.
 */
public class GFrame {
	Canvas canvas;
	Canvas Back;
	GraphicsContext gc;
	GraphicsContext Backgc;
	LSub Sub;
	GEvent Ev;
	GFile file;
	Image image;
	Pane banmen;

	private int Masu = 15;      //マス目の数
	private int Long = 40;    //マスの幅
	private int Jun = 0;  //順番を保存　黒０、白１
	private int BS = 0;   //黒石
	private int WS = 1;   //白石
	private int DF = -1;   //無し
	int[][] Arr;		//盤面情報
	int[][]	Jdg;		//複数入力用判定配列
	int[][] Feel;//自信のほどはいかに？ 1~5の5段階評価
	int margin = 5;

	GFrame() {
		Arr = new int[Masu][Masu];
		Jdg = new int[Masu][Masu];
		Feel = new int[Masu][Masu];
		Ev = new GEvent(this);
		canvas = new Canvas(600, 600);
		canvas.setOnMouseClicked(Ev);
		gc = canvas.getGraphicsContext2D();

		Back = new Canvas(600,600);
		Backgc = Back.getGraphicsContext2D();
		image = new Image("tree.jpg");
		banmen = new Pane();

		banmen.getChildren().addAll(canvas,Back);
		canvas.toFront();

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
		gc.clearRect(0,0,600,600);
		CArray();
		ResetJ();
		Jun = 0;
	}

	//判定配列リセット
	public void ResetJ(){
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

	//盤面の描写(背面)
	public void drawF() {
		Backgc.setFill(Color.ORANGE);
		Backgc.setStroke(Color.BLACK);
		Backgc.setLineWidth(2.0);
		//gc.fillRect(0, 0, 600, 600);					//背景色を描写する
		Backgc.drawImage(image, 0, 0, 600, 600);		//指定されたイメージを描写する
		Backgc.strokeRect(0, 0, 600, 600);
		drawGrid();
		drawPoint();

	}

	//盤面に縦横線を描く(背面キャンバス)
	public void drawGrid() {
		Backgc.setLineWidth(1.0);
		for (int i = 0; i < Masu; i++) {
			Backgc.strokeLine(0, (int) ((0.5 + i) * Long), 600, (int) ((0.5 + i) * Long));
			Backgc.strokeLine((int) ((0.5 + i) * Long), 0, (int) ((0.5 + i) * Long), 600);
		}

	}

	//点をうつ(背面キャンバス)
	public void drawPoint() {
		Backgc.setFill(Color.BLACK);
		Backgc.fillOval(295, 295, 10, 10);
		Backgc.fillOval(95, 95, 10, 10);    //左上
		Backgc.fillOval(495, 95, 10, 10);   //右上
		Backgc.fillOval(95, 495, 10, 10);   //左下
		Backgc.fillOval(495, 495, 10, 10);  //右下

	}

	//配列に指定の値を入れる
	public void pushS(int x, int y) {
		Arr[x][y] = Jun;

	}

	//配列にDF値を入れる
	public void setDF(int x,int y){
		Arr[x][y] = DF;
	}

	//指定した点に丸を描く
	public void drawS(int x, int y) {
		if (Arr[x][y] == BS) { //該当箇所が黒い石
			gc.setFill(Color.BLACK);
			gc.strokeOval(x * Long + margin, y * Long + margin, Long - 2 * margin, Long - 2 * margin);
			gc.strokeOval(x * Long + margin + 1, y * Long + margin + 1, Long - 2 * margin - 2, Long - 2 * margin - 2);
			gc.fillOval(x * Long + margin, y * Long + margin, Long - 2 * margin, Long - 2 * margin);
		}
		if (Arr[x][y] == WS) { //該当箇所が白い石
			gc.setFill(Color.BLACK);
			gc.strokeOval(x * Long + margin, y * Long + margin, Long - 2 * margin, Long - 2 * margin);
			gc.strokeOval(x * Long + margin + 1, y * Long + margin + 1, Long - 2 * margin - 2, Long - 2 * margin - 2);
			gc.setFill(Color.WHITE);
			gc.fillOval(x * Long + margin + 1, y * Long + margin + 1, Long - 2 * margin - 2, Long - 2 * margin - 2);
		}


	}

	//指定した点の石を消す
	public void clearS(int x,int y){
		gc.clearRect(x * Long + margin, y * Long + margin, Long - 1.5 * margin, Long - 1.5 * margin);

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
		} else {
			Sub.setB();
			Jun = 0;
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
	public void setJun(int i){
		Jun =i;
		if (Jun == 0) {
			Sub.setB();
		} else {
			Sub.setW();
		}

	}



	//Subにイベントを反映
	public void notPut(int i) {
		Sub.setNotPut(i);
	}


}

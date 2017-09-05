import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Callback;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by mmr on 2016/06/29.
 * ファイルの入出力処理
 */
public class GFile {

	GFrame frame;
	LSub sub;
	GMain main;

	Stage InputStage;
	Stage OutputStage;
	Stage EditStage;
	String sp;
	String FileName;
	File file;
	PrintWriter Fwrite;
	BufferedReader Fread;
	LocalDate Date;
	LocalTime Time;
	String Dpath;

	GFile(GFrame f, LSub s, GMain m) throws IOException {
		frame = f;
		sub = s;
		main = m;
		sub.getGFile(this);
		sp = File.separator;

		String os = System.getProperty("os.name");
		if (os.startsWith("linux") || os.startsWith("Mac OS X")) {
			Dpath = System.getProperty("user.home") + sp + "FieldFile" + sp;
		} else if (os.startsWith("Windows 10") || os.startsWith("Windows 8") || os.startsWith("Windows 7") || os.startsWith("Windows Vista") || os.startsWith("Windows XP")) {
			Dpath = System.getProperty("user.home") + sp + "AppData" + sp + "Roaming" + sp + "FieldFile" + sp;
		}

	}

	//ファイル入力用
	public void inputFile(String input) throws IOException {
		//	Files = listFile.listFiles();


		File file = new File(input);
		Fread = new BufferedReader(new FileReader(file));
		String str;
		int inputI;
		int[] value = new int[4];
		int[] time = new int[6];
		//	System.out.println("1: "+Fread.readLine());
		//	System.out.println("2: "+Fread.readLine());

		try {
			frame.drawF();
			if (Fread.readLine().equals("GomokuFX")) {
				int Jun = Integer.parseInt(Fread.readLine());
				frame.gc.clearRect(0, 0, 600, 600);
				for (int i = 0; i < frame.getMasu(); i++) {
					for (int j = 0; j < frame.getMasu(); j++) {
						str = Fread.readLine();
						inputI = Integer.parseInt(str);
						if (inputI != -1) {
							frame.setJun(inputI);
							frame.drawS(i, j, frame.gc);
						}

						frame.Arr[i][j] = inputI;


					}

				}
				for (int i = 0; i < 4; i++) {
					str = Fread.readLine();
					System.out.println(str);
					value[i] = Integer.parseInt(str);
				}

				for (int i = 0; i < 6; i++) {
					str = Fread.readLine();
					System.out.println(str);
					time[i] = Integer.parseInt(str);
				}

				frame.setJun(Jun);
				frame.setTime(time);
				frame.setValues(value);
			}
		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.OK);
			alert.setContentText("選択されたファイルは形式が正しくないです");
			Fread.close();
		}


		Fread.close();

	}

	//ファイル出力用
	public void outputFile(String Filename) throws IOException {

//		Date = LocalDate.now();
//		Time = LocalTime.now();
		int[] value = frame.getValue();
		int[] time = frame.getTime();

		//FileName = Date.toString() + "-" + Time.getHour() + "-" + Time.getMinute() + "-" + Time.getSecond();

		file = new File(Dpath + Filename + ".txt");
		file.createNewFile();
		Fwrite = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")));
		Fwrite.println("GomokuFX");
		Fwrite.println(frame.getJun());
		for (int i = 0; i < frame.getMasu(); i++) {
			for (int j = 0; j < frame.getMasu(); j++) {
				Fwrite.println(frame.Arr[i][j]);

			}

		}

		for (int v : value) {
			Fwrite.println(v);
		}

		for (int t : time) {
			Fwrite.println(t);
		}

		Fwrite.close();
	}


	//ファイル読み込みウィンドウ
	public void setInputStage() throws IOException {

		//ウィンドウの外枠スタイル設定
		Border Style = new Border(new BorderStroke(
						Color.BLACK, BorderStrokeStyle.SOLID,
						new CornerRadii(0.1),
						new BorderWidths(1.0)
		));
		Label info = new Label("入力するファイルを選んでください");      //インフォメーションラベル
		info.setId("info");
		Label infof = new Label("");                                //ファイル未選択時ラベル
		infof.setId("NotFound");
		Button input = new Button("ファイル選択");                    //ボタン：ファイル選択
		input.setId("Exit");

		Button exit = new Button("画面を閉じる");                      //ボタン：ウィンドウを閉じる
		exit.setId("Exit");
		exit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				InputStage.close();
			}
		});

		//ファイル読み込み
		String path = "./src/FieldFile/";
		File listFile = new File(Dpath);
		File[] list = listFile.listFiles();
		ListView<String> listView = new ListView();
		ObservableList<String> item = FXCollections.observableArrayList();


		for (int i = 0; i < list.length; i++) {
			File file = list[i];
			item.add(file.getName());
		}


		listView.setItems(item);

		StackPane root = new StackPane();
		root.getChildren().add(listView);


		input.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String str = listView.getFocusModel().getFocusedItem();
				try {
					inputFile(Dpath + "" + str);
					InputStage.close();
				} catch (Exception e) {
					if (e.getMessage() == "null")
						System.out.println("ファイルが選択されてませんが");
					infof.setText("ファイルが選択されていません");


				}

			}
		});

		//ファイル検索用ウィンドウ
		InputStage = new Stage();
		InputStage.initModality(Modality.APPLICATION_MODAL);

		FlowPane fp = new FlowPane();
		VBox vbox = new VBox();
		HBox hBox = new HBox();
		hBox.getChildren().addAll(infof, exit, input);
		hBox.setSpacing(5);
		hBox.setPadding(new Insets(3, 5, 1, 0));
		hBox.setAlignment(Pos.CENTER_RIGHT);
		vbox.getChildren().addAll(info, root, hBox);
		vbox.setId("Output");
		vbox.setBorder(Style);

		Scene InScene = new Scene(vbox, 500, 300);
		InScene.getStylesheets().addAll("gFX.css");
		InputStage.setScene(InScene);
		InputStage.getScene().getRoot().setStyle("-fx-background-color: transparent");
		InputStage.initStyle(StageStyle.TRANSPARENT);

		InputStage.show();
	/*	File im = fc.showOpenDialog(InputStage);
		if(im != null){
			inputFile(im);
			System.out.println(im);
		}*/


	}

	//ファイル出力用ウィンドウ
	public void setOutputStage() {


		Border Style = new Border(new BorderStroke(
						Color.BLACK, BorderStrokeStyle.SOLID,
						new CornerRadii(0.1),
						new BorderWidths(1.0)
		));
		OutputStage = new Stage();
		OutputStage.initModality(Modality.APPLICATION_MODAL);
		Label form = new Label("ファイル入力フォーム");
		Label image = new Label(".txt");
		Label info = new Label("入力された名前のファイルが生成されます");
		Label error = new Label("");
		error.setId("NotFound");

		Button input = new Button("ファイル生成");
		input.setId("Exit");

		Button exit = new Button("画面を閉じる");
		exit.setId("Exit");

		exit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				OutputStage.getScene().getWindow().hide();
			}
		});

		TextField txt = new TextField();
		txt.setPrefSize(100, 20);

		input.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					File file = new File(Dpath + txt.getText() + ".txt");
					System.out.println(Dpath + txt.getText() + ".txt");  //参照ファイルをチェック
					if (file.exists()) {
						error.setText("そのファイルはすでに存在します");              //ファイルがすでに存在している時の表示
					} else {
						outputFile(txt.getText());          //ファイル名にかぶりがなければ生成しウィンドウを閉じる
						OutputStage.close();
					}
				} catch (IOException e) {

				}
			}
		});


		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setHgap(10);
		grid.setVgap(10);
		grid.add(txt, 1, 1);
		grid.add(image, 2, 1);
		grid.setAlignment(Pos.CENTER);

		HBox hbox = new HBox();
		hbox.setSpacing(10);
		hbox.setAlignment(Pos.CENTER);
		hbox.getChildren().addAll(exit, input);

		VBox vbox = new VBox();
		vbox.setId("Output");
		vbox.getChildren().addAll(form, info, grid, error, hbox);
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(20);
		vbox.setBorder(Style);

		Scene OutScene = new Scene(vbox, 300, 300);
		OutScene.getStylesheets().addAll("gFX.css");
		OutputStage.setScene(OutScene);
		//透明化
		OutputStage.initStyle(StageStyle.TRANSPARENT);
		//透明化
		OutputStage.getScene().getRoot().setStyle("-fx-background-color: transparent");
		OutputStage.setResizable(false);
		OutputStage.show();
	}

	//ファイル編集用ウィンドウ(実装途中)
	public void setEditStage() {
		//ウィンドウの外枠スタイル設定
		Border Style = new Border(new BorderStroke(
						Color.BLACK, BorderStrokeStyle.SOLID,
						new CornerRadii(0.1),
						new BorderWidths(1.0)
		));

		//オブジェクト類
		Button del = new Button("ファイル削除");
		del.setId("Put");

		Button exit = new Button("閉じる");
		exit.setId("Exit");
		exit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				EditStage.close();
			}
		});

		Label info = new Label("選択したファイルを削除、もしくは名前を編集できます");

		//ファイル読み込み
		String path = "./src/FieldFile/";
		File listFile = new File(path);
		File[] list = listFile.listFiles();
		ListView<String> listView = new ListView();
		ObservableList<String> item = FXCollections.observableArrayList();

		ListView<File> Flist = new ListView();
		ObservableList<File> Fitem = FXCollections.observableArrayList();


		for (int i = 0; i < list.length; i++) {
			File file = list[i];
			Fitem.add(file);
			item.add(file.getName());
		}


		listView.setItems(item);
		Flist.setItems(Fitem);

		StackPane root = new StackPane();
		//	root.getChildren().add(listView);
		root.getChildren().add(listView);

		//リストへのチェックボックス追加
		listView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
			@Override
			public ObservableValue<Boolean> call(String param) {
				BooleanProperty bool = new SimpleBooleanProperty();

				return bool;
			}
		}));

		EditStage = new Stage();
		EditStage.initModality(Modality.APPLICATION_MODAL);

		VBox vbox = new VBox();
		HBox hBox = new HBox();
		hBox.getChildren().addAll(exit, del);
		hBox.setSpacing(5);
		hBox.setPadding(new Insets(3, 5, 1, 0));
		hBox.setAlignment(Pos.CENTER_RIGHT);
		vbox.getChildren().addAll(info, root, hBox);
		vbox.setId("Output");
		vbox.setBorder(Style);

		Scene EScene = new Scene(vbox, 500, 300);
		EScene.getStylesheets().addAll("gFX.css");
		EditStage.setScene(EScene);
		EditStage.getScene().getRoot().setStyle("-fx-background-color: transparent");
		EditStage.initStyle(StageStyle.TRANSPARENT);

		EditStage.show();

	}

}

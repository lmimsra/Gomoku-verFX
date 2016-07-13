/**
 * Created by mmr on 2016/06/23.
 */

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class GMain extends Application {
	GFrame flame;
	LSub sub;
	GFile file;
	private double xOffset = 0;
	private double yOffset = 0;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException {
		Stage cstage = stage;
		flame = new GFrame();
		sub = new LSub();
		file = new GFile(flame, sub, this);
		flame.getSF(sub, file);
		sub.getFrame(flame);
		stage.setTitle("五目並べ ver.FX");
		BorderPane border = new BorderPane();
		HBox hbox = new HBox(2d);
		hbox.setAlignment(Pos.CENTER);


		//	hbox.getChildren().addAll(sub.Lbox, flame.canvas,sub.Rbox);
		border.setMaxSize(1200, 800);
		border.setCenter(flame.banmen);
		border.setLeft(sub.Lbox);
		border.setTop(sub.Ubox);
		border.setBottom(sub.Bbox);

		border.setMargin(sub.Ubox, new Insets(5));
		border.setMargin(sub.Lbox, new Insets(5));
		border.setMargin(sub.Bbox, new Insets(5));
		border.setMargin(flame.banmen, new Insets(5));
		Scene scene = new Scene(border, 920, 755);
		//透明化
		//	scene.setFill(null);
		scene.setFill(Color.BISQUE);
		scene.getStylesheets().addAll("gFX.css");
		//移動可能ウィンドウ(左と上のフィールドのみ)
		sub.Ubox.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});

		sub.Ubox.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				stage.setX(event.getScreenX() - xOffset);
				stage.setY(event.getScreenY() - yOffset);
			}
		});

		sub.Lbox.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});

		sub.Lbox.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				stage.setX(event.getScreenX() - xOffset);
				stage.setY(event.getScreenY() - yOffset);
			}
		});


		stage.setScene(scene);
		//透明化
		stage.initStyle(StageStyle.TRANSPARENT);
		//透明化
		stage.getScene().getRoot().setStyle("-fx-background-color: transparent");
		stage.setResizable(false);
		stage.show();

	}

}

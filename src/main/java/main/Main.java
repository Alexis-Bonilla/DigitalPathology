package main;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		try {
			stage.setTitle("Patología Digital");
			stage.setResizable(false);
//			stage.setFullScreen(true);
//			stage.setMaximized(true);
			Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
			Scene mainScene = new Scene(root);
			stage.setScene(mainScene);

			stage.show();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
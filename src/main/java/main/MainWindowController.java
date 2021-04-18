package main;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class MainWindowController {

	@FXML
	private Button btnBuscar;

	@FXML
	private Button btnAnalyze;

	@FXML
	private ImageView image;

	@FXML
	void analyzeImage(ActionEvent event) {

	}

//	public void centerImage() {
//		Image img = this.image.getImage();
//		if (img != null) {
//			double w = 0;
//			double h = 0;
//
//			double ratioX = this.image.getFitWidth() / img.getWidth();
//			double ratioY = this.image.getFitHeight() / img.getHeight();
//
//			double reducCoeff = 0;
//			if (ratioX >= ratioY) {
//				reducCoeff = ratioY;
//			} else {
//				reducCoeff = ratioX;
//			}
//
//			w = img.getWidth() * reducCoeff;
//			h = img.getHeight() * reducCoeff;
//
//			image.setX((this.image.getFitWidth() - w) / 2);
//			image.setY((this.image.getFitHeight() - h) / 2);
//
//		}
//	}

	public void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.setTitle("View Pictures");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"),
				new FileChooser.ExtensionFilter("PNG", "*.png"));
	}

	@FXML
	void searchImage(ActionEvent event) {

		FileChooser fc = new FileChooser();
		configureFileChooser(fc);
		File file = fc.showOpenDialog(btnBuscar.getScene().getWindow());

		if (file != null) {
			Image image = new Image(file.toURI().toString());
			this.image.setImage(image);
		}

	}

}
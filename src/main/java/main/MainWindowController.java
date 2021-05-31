package main;

import java.io.File;
import java.util.ArrayList;

import com.amazonaws.buckets.BucketUpload;
import com.amazonaws.buckets.BucketUploadImp;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CustomLabel;

import analyzeImage.AnalizerInterface;
import analyzeImage.AnalizerInterfaceImp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

public class MainWindowController {

	public static final String BUCKET = "imagenesanalizadaspatologiadigital";
	public static final double IMAGE_REDUCTION = 0.4;

	@FXML
	private Button btnBuscar;

	@FXML
	private Button btnAnalyze;

	@FXML
	private Canvas canvas;

	private String path;

	private Image image;

	private AnalizerInterface analizer;

	private BucketUpload uploader;

	@FXML
	void analyzeImage(ActionEvent event) {

		this.uploader = new BucketUploadImp(Regions.US_EAST_1);
		String[] images = { this.path.substring(6) };
		System.out.println("EL QUE BUSCO" + images[0]);

		boolean succesUpload = this.uploader.loadImages(images, this.BUCKET);

		if (succesUpload) {
			this.analizer = new AnalizerInterfaceImp(this.BUCKET);
		}

		double left = 0;
		double top = 0;
		double height = this.image.getHeight();
		double width = this.image.getWidth();

		GraphicsContext g = this.canvas.getGraphicsContext2D();

		ArrayList<CustomLabel> customLabels = analizer.analyzeImage(changeRoute(path));

		for (CustomLabel customLabel : customLabels) {

			if (customLabel.getGeometry() != null) {
				BoundingBox box = customLabel.getGeometry().getBoundingBox();
				left = width * box.getLeft();
				double aux = left * this.IMAGE_REDUCTION;
				left -= aux;
				top = height * box.getTop();
				aux = top * this.IMAGE_REDUCTION;
				top -= aux;
				g.fillText("Bacilo TB", left, top);

				g.strokeRect(Math.round(left), Math.round(top),
						Math.round((width * box.getWidth()))
								- (Math.round((width * box.getWidth())) * this.IMAGE_REDUCTION),
						Math.round((height * box.getHeight()))
								- (Math.round((height * box.getHeight())) * this.IMAGE_REDUCTION));
			}

		}

	}

	private String changeRoute(String path) {
		String changed = "";
		String[] words = path.split("/");
		changed += words[words.length - 1];

		return changed;
	}

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
			this.path = file.toURI().toString();
			this.image = new Image(this.path);
			GraphicsContext g = this.canvas.getGraphicsContext2D();
			double restaAncho = this.IMAGE_REDUCTION * image.getWidth();
			double restaAlto = this.IMAGE_REDUCTION * image.getHeight();
			double anchoFinal = image.getWidth() - restaAncho;
			double altoFinal = image.getHeight() - restaAlto;
			g.drawImage(image, 0, 0, anchoFinal, altoFinal);
			changeRoute(this.path);
		}

	}

}
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

	/**
	 *
	 * @FXML void analyzeImage(ActionEvent event) { DocumentBuilderFactory factory =
	 *       DocumentBuilderFactory.newInstance(); try { DocumentBuilder builder =
	 *       factory.newDocumentBuilder(); Document documento = builder.parse(new
	 *       File(this.getXmlRoute())); documento.getDocumentElement().normalize();
	 *       double porcentaje = 0.4;
	 *
	 *       NodeList listaObjects = documento.getElementsByTagName("object");
	 *
	 *       for (int i = 0; i < listaObjects.getLength(); i++) { Node actualNode =
	 *       listaObjects.item(i); if (actualNode.getNodeType() ==
	 *       Node.ELEMENT_NODE) {
	 *
	 *       Element actualElement = (Element) actualNode;
	 *
	 *       Node actualBox = actualElement.getElementsByTagName("bndbox").item(0);
	 *       NodeList coordinates = actualBox.getChildNodes(); double[]
	 *       rectangleCoor = new double[4]; for (int j = 0; j <
	 *       coordinates.getLength(); j++) {
	 *
	 *       if (coordinates.item(j).getNodeType() == Element.ELEMENT_NODE) {
	 *       Element coorElement = (Element) coordinates.item(j);
	 *       System.out.println("propiedad: " + coorElement.getNodeName() + "valor:
	 *       " + coorElement.getTextContent() + "indice: " + j); if (j == 1) {
	 *       rectangleCoor[0] = Double.parseDouble(coorElement.getTextContent());
	 *       double aux = rectangleCoor[0] * porcentaje; rectangleCoor[0] -= aux; }
	 *       else if (j == 3) {
	 *
	 *       rectangleCoor[1] = Double.parseDouble(coorElement.getTextContent());
	 *       double aux = rectangleCoor[1] * porcentaje; rectangleCoor[1] -= aux; }
	 *       else if (j == 5) {
	 *
	 *       rectangleCoor[2] = Double.parseDouble(coorElement.getTextContent());
	 *       double aux = rectangleCoor[2] * porcentaje; rectangleCoor[2] -= aux; }
	 *       else if (j == 7) { rectangleCoor[3] =
	 *       Double.parseDouble(coorElement.getTextContent()); double aux =
	 *       rectangleCoor[3] * porcentaje; rectangleCoor[3] -= aux;
	 *
	 *       }
	 *
	 *       }
	 *
	 *       } // rectangleCoor[0] = Xmin // rectangleCoor[1] = Ymin //
	 *       rectangleCoor[2] = Xmax // rectangleCoor[3] = Ymax
	 *
	 *       GraphicsContext g = this.canvas.getGraphicsContext2D();
	 *
	 *       double ancho = rectangleCoor[2] - rectangleCoor[0]; double alto =
	 *       rectangleCoor[3] - rectangleCoor[1];
	 *
	 *       g.strokeRect(rectangleCoor[0], ((this.image.getHeight() -
	 *       (this.image.getHeight() * porcentaje)) - ((this.image.getHeight() -
	 *       (this.image.getHeight() * porcentaje)) - rectangleCoor[3])) - alto,
	 *       ancho, alto);
	 *
	 *       // g.strokeRect(100, 50, 100, 100);
	 *
	 *       } }
	 *
	 *       } catch (
	 *
	 *       SAXException e) { // TODO Auto-generated catch block
	 *       e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
	 *       catch block e.printStackTrace(); } catch (ParserConfigurationException
	 *       e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 *
	 *       }
	 */

	@FXML
	void analyzeImage(ActionEvent event) {

		this.uploader = new BucketUploadImp(Regions.US_EAST_1);
		String[] images = { this.path.substring(6) };

		boolean succesUpload = this.uploader.loadImages(images, this.BUCKET);

		if (succesUpload) {
			this.analizer = new AnalizerInterfaceImp(this.BUCKET);
		}

		double porcentaje = 0.4;

		double left = 0;
		double top = 0;
		double height = this.image.getHeight();
		double width = this.image.getWidth();

		System.out.println("Width: " + width);
		System.out.println("Height: " + height);
		GraphicsContext g = this.canvas.getGraphicsContext2D(); // Create a Java2D version of g.

		// Draw the image.
//		g.drawImage(this.image, left, top, width, height);

		ArrayList<CustomLabel> customLabels = analizer.analizeImage(changeRoute(path));
		for (CustomLabel customLabel : customLabels) {

			if (customLabel.getGeometry() != null) {
				BoundingBox box = customLabel.getGeometry().getBoundingBox();
				left = width * box.getLeft();
				double aux = left * porcentaje;
				left -= aux;
				top = height * box.getTop();
				aux = top * porcentaje;
				top -= aux;
				g.fillText("Bacilo TB", left, top);

				g.strokeRect(Math.round(left), Math.round(top),
						Math.round((width * box.getWidth())) - (Math.round((width * box.getWidth())) * porcentaje),
						Math.round((height * box.getHeight())) - (Math.round((height * box.getHeight())) * porcentaje));
			}

		}

	}

	private String changeRoute(String path) {
		String changed = "";

//		changed += this.BUCKET + "/";
		String[] words = path.split("/");
		changed += words[words.length - 1];
		System.out.println("LA NUEVA RUTA : " + changed);
		return changed;
	}

	public void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.setTitle("View Pictures");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"),
				new FileChooser.ExtensionFilter("PNG", "*.png"));
	}

	public String getXmlRoute() {
		String changed;
		String auxPath = this.path.substring(0, path.length() - 4);
		changed = auxPath.substring(6, auxPath.length()) + ".xml";

		return changed.replace('/', '\\').replaceAll("\\\\", "\\\\\\\\");

	}

	@FXML
	void searchImage(ActionEvent event) {

		FileChooser fc = new FileChooser();
		configureFileChooser(fc);
		File file = fc.showOpenDialog(btnBuscar.getScene().getWindow());

		if (file != null) {
			this.path = file.toURI().toString();
			System.out.println("path: " + path);
			this.image = new Image(this.path);

//			this.image = new Image(this.path);
			System.out.println("ANCHO: " + image.getWidth() + " ALTO: " + image.getHeight());
			GraphicsContext g = this.canvas.getGraphicsContext2D();

			System.out.println("Relación de aspecto: " + image.getWidth() / image.getHeight());

			double porcentaje = 0.4;// 20%
			double restaAncho = porcentaje * image.getWidth();
			double restaAlto = porcentaje * image.getHeight();

			double anchoFinal = image.getWidth() - restaAncho;
			double altoFinal = image.getHeight() - restaAlto;

			g.drawImage(image, 0, 0, anchoFinal, altoFinal);
			changeRoute(this.path);
		}

	}

}
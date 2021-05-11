package main;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CustomLabel;
import com.amazonaws.services.rekognition.model.DetectCustomLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectCustomLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

public class MainWindowController {

	@FXML
	private Button btnBuscar;

	@FXML
	private Button btnAnalyze;

	@FXML
	private Canvas canvas;

	private String path;

	private javafx.scene.image.Image image;

	DetectCustomLabelsResult result;

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

		initializeModel();

		double left = 0;
		double top = 0;
		double height = ((RenderedImage) image).getHeight();
		double width = ((RenderedImage) image).getWidth();

		GraphicsContext g = this.canvas.getGraphicsContext2D(); // Create a Java2D version of g.

		// Draw the image.
		g.drawImage(image, 0, 0, width, height);

		List<CustomLabel> customLabels = result.getCustomLabels();
		for (CustomLabel customLabel : customLabels) {

			if (customLabel.getGeometry() != null) {
				BoundingBox box = customLabel.getGeometry().getBoundingBox();
				left = width * box.getLeft();
				top = height * box.getTop();
				g.fillText(customLabel.getName(), left, top);
				g.strokeRect(Math.round(left), Math.round(top), Math.round((width * box.getWidth())),
						Math.round((height * box.getHeight())));
			}

		}

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

	private void initializeModel() {
		String photo = "imagenesdatasetpatologiadigital/tuberculosis-phone-1258_jpg.rf.49df8db840fbb7582f630285017488bc.jpg";
		String bucket = "imagenesdatasetpatologiadigital";
		String projectVersionArn = "arn:aws:rekognition:us-east-1:682086073548:project/DigitalPatology/version/DigitalPatology.2021-04-26T11.24.35/1619454274199";
		float minConfidence = 90;
		int height = 0;
		int width = 0;

		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();

//			System.out.println("estoy aqui");
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. ", e);
		}

		// Get the image from an S3 Bucket
		AmazonS3 s3client = AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider)
				.withRegion(Regions.US_EAST_1).build();

		com.amazonaws.services.s3.model.S3Object s3object = s3client.getObject(bucket, photo);
		S3ObjectInputStream inputStream = s3object.getObjectContent();
		BufferedImage image;
		try {
			image = ImageIO.read(inputStream);

			DetectCustomLabelsRequest request = new DetectCustomLabelsRequest().withProjectVersionArn(projectVersionArn)
					.withImage(new Image().withS3Object(new S3Object().withName(photo).withBucket(bucket)))
					.withMinConfidence(minConfidence);

			width = image.getWidth();
			height = image.getHeight();

			// Call DetectCustomLabels
			AmazonRekognition amazonRekognition = AmazonRekognitionClientBuilder.standard()
					.withRegion(Regions.US_EAST_1).build();
			this.result = amazonRekognition.detectCustomLabels(request);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Show the bounding box info for each custom label.
		List<CustomLabel> customLabels = result.getCustomLabels();
		for (CustomLabel customLabel : customLabels) {

			if (customLabel.getGeometry() != null) {
				BoundingBox box = customLabel.getGeometry().getBoundingBox();

				float left = width * box.getLeft();
				float top = height * box.getTop();
				System.out.println("Custom Label:");

				System.out.println("Left: " + String.valueOf((int) left));
				System.out.println("Top: " + String.valueOf((int) top));
				System.out.println("Label Width: " + String.valueOf((int) (width * box.getWidth())));
				System.out.println("Label Height: " + String.valueOf((int) (height * box.getHeight())));
				System.out.println();
			}
		}

	}

	@FXML
	void searchImage(ActionEvent event) {

		FileChooser fc = new FileChooser();
		configureFileChooser(fc);
		File file = fc.showOpenDialog(btnBuscar.getScene().getWindow());

		if (file != null) {
			this.path = file.toURI().toString();
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

		}

	}

}
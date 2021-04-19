package main;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

public class MainWindowController {

	@FXML
	private Button btnBuscar;

	@FXML
	private Button btnAnalyze;

	@FXML
	private Canvas canvas;

	private String path;

	@FXML
	void analyzeImage(ActionEvent event) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document documento = builder.parse(new File(this.getXmlRoute()));
			documento.getDocumentElement().normalize();

			NodeList listaObjects = documento.getElementsByTagName("object");

			for (int i = 0; i < listaObjects.getLength(); i++) {
				Node actualNode = listaObjects.item(i);
				if (actualNode.getNodeType() == Node.ELEMENT_NODE) {

					Element actualElement = (Element) actualNode;

					Node actualBox = actualElement.getElementsByTagName("bndbox").item(0);
					NodeList coordinates = actualBox.getChildNodes();
					int[] rectangleCoor = new int[4];
					for (int j = 0; j < coordinates.getLength(); j++) {

						if (coordinates.item(j).getNodeType() == Element.ELEMENT_NODE) {
							Element coorElement = (Element) coordinates.item(j);
							System.out.println("propiedad: " + coorElement.getNodeName() + "valor: "
									+ coorElement.getTextContent() + "indice: " + j);
							if (j == 1) {
								rectangleCoor[0] = Integer.parseInt(coorElement.getTextContent());
							} else if (j == 3) {

								rectangleCoor[1] = Integer.parseInt(coorElement.getTextContent());
							} else if (j == 5) {

								rectangleCoor[2] = Integer.parseInt(coorElement.getTextContent());
							} else if (j == 7) {
								rectangleCoor[3] = Integer.parseInt(coorElement.getTextContent());

							}

						}

					}

					GraphicsContext g = this.canvas.getGraphicsContext2D();

					g.strokeRect(rectangleCoor[3], rectangleCoor[0], rectangleCoor[2] - rectangleCoor[0],
							rectangleCoor[3] - rectangleCoor[1]);

//					g.strokeRect(0, 0, 100, 500);

				}
			}

		} catch (

		SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	@FXML
	void searchImage(ActionEvent event) {

		FileChooser fc = new FileChooser();
		configureFileChooser(fc);
		File file = fc.showOpenDialog(btnBuscar.getScene().getWindow());

		if (file != null) {
			this.path = file.toURI().toString();
			Image image = new Image(this.path);
			System.out.println("ANCHO: " + image.getWidth() + "Alto: " + image.getHeight());
			GraphicsContext g = this.canvas.getGraphicsContext2D();
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight());

		}

	}

}
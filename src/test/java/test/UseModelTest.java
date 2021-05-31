package test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.CustomLabel;
import com.amazonaws.services.rekognition.model.ResourceInUseException;
import com.amazonaws.services.rekognition.model.StartProjectVersionRequest;
import com.amazonaws.services.rekognition.model.StartProjectVersionResult;
import com.amazonaws.services.rekognition.model.StopProjectVersionRequest;
import com.amazonaws.services.rekognition.model.StopProjectVersionResult;

import analyzeImage.AnalizerInterface;
import analyzeImage.AnalizerInterfaceImp;
import main.MainWindowController;
import model.Model;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UseModelTest {

	private Model model;
	private String projectVersionArn;
	private int minInferenceUnits;
	private AnalizerInterface analizer;

	private AmazonRekognition rekognitionClient;

	private void setup1() {
		this.projectVersionArn = "arn:aws:rekognition:us-east-1:682086073548:project/DigitalPatology/version/DigitalPatology.2021-04-26T11.24.35/1619454274199";
		this.minInferenceUnits = 1;
		model = new Model();
		rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		analizer = new AnalizerInterfaceImp(MainWindowController.BUCKET);
	}

	private void setup2() {
		this.projectVersionArn = "arn:aws:rekognition:us-east-1:682086073548:project/DigitalPatology/version/DigitalPatology.2021-04-26T11.24.35/1619454274199";
		this.minInferenceUnits = 1;
		model = new Model();
		rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		analizer = new AnalizerInterfaceImp(MainWindowController.BUCKET);

		model.start();
	}

	@Test
	public void test1StartModelTest() {
		setup1();

		model.start();

		try {
			StartProjectVersionRequest request = new StartProjectVersionRequest()
					.withMinInferenceUnits(minInferenceUnits).withProjectVersionArn(projectVersionArn);
			StartProjectVersionResult result = rekognitionClient.startProjectVersion(request);
			assertEquals(result.getStatus(), "STARTING");

		} catch (ResourceInUseException e) {
			System.out.println("Expected exception if the model is running correctly");
		}

	}

	@Test
	public void test2AnalizeImageTest() {
		setup2();

		ArrayList<CustomLabel> customLabels = analizer.analyzeImage("test.jpg");

		assertNotNull(customLabels);
		for (int i = 0; i < customLabels.size(); i++) {
			assertNotNull(customLabels.get(i).getGeometry().getBoundingBox());
		}

	}

	@Test
	public void test3StopModelTest() {
		setup1();

		model.stop();

		try {

			StopProjectVersionRequest stopRequest = new StopProjectVersionRequest()
					.withProjectVersionArn(projectVersionArn);
			StopProjectVersionResult result = rekognitionClient.stopProjectVersion(stopRequest);

			assertEquals(result.getStatus(), "STOPPING");

		} catch (ResourceInUseException e) {
			System.out.println("Expected exception if the model was stopped correctly");
		}

	}

}

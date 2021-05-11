package model;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.StopProjectVersionRequest;
import com.amazonaws.services.rekognition.model.StopProjectVersionResult;

public class StopModel {

	public static void main(String[] args) throws Exception {

		String projectVersionArn = "arn:aws:rekognition:us-east-1:682086073548:project/DigitalPatology/version/DigitalPatology.2021-04-26T11.24.35/1619454274199";

		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(Regions.US_EAST_1)
				.build();

		try {

			StopProjectVersionRequest request = new StopProjectVersionRequest()
					.withProjectVersionArn(projectVersionArn);
			StopProjectVersionResult result = rekognitionClient.stopProjectVersion(request);

			System.out.println(result.getStatus());

		} catch (Exception e) {
			System.out.println(e.toString());
		}
		System.out.println("Done...");
	}
}
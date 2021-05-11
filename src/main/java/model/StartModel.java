package model;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DescribeProjectVersionsRequest;
import com.amazonaws.services.rekognition.model.DescribeProjectVersionsResult;
import com.amazonaws.services.rekognition.model.ProjectVersionDescription;
import com.amazonaws.services.rekognition.model.StartProjectVersionRequest;
import com.amazonaws.services.rekognition.model.StartProjectVersionResult;

import com.amazonaws.services.rekognition.model.StopProjectVersionRequest;
import com.amazonaws.services.rekognition.model.StopProjectVersionResult;
import com.amazonaws.waiters.Waiter;
import com.amazonaws.waiters.WaiterParameters;

public class StartModel {

	public static void main(String[] args) throws Exception {

		String projectVersionArn = "arn:aws:rekognition:us-east-1:682086073548:project/DigitalPatology/version/DigitalPatology.2021-04-26T11.24.35/1619454274199";
		String projectArn = "arn:aws:rekognition:us-east-1:682086073548:project/DigitalPatology/1619307010888";
		int minInferenceUnits = 1;
		String versionName = "DigitalPatology.2021-04-26T11.24.35";

		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

		StartProjectVersionRequest request = new StartProjectVersionRequest().withMinInferenceUnits(minInferenceUnits)
				.withProjectVersionArn(projectVersionArn);

		try {

			StartProjectVersionResult result = rekognitionClient.startProjectVersion(request);

			System.out.println("Status: " + result.getStatus());

			DescribeProjectVersionsRequest describeProjectVersionsRequest = new DescribeProjectVersionsRequest()
					.withVersionNames(versionName).withProjectArn(projectArn);

			Waiter<DescribeProjectVersionsRequest> waiter = rekognitionClient.waiters().projectVersionRunning();
			waiter.run(new WaiterParameters<DescribeProjectVersionsRequest>(describeProjectVersionsRequest));

			DescribeProjectVersionsResult response = rekognitionClient
					.describeProjectVersions(describeProjectVersionsRequest);

			for (ProjectVersionDescription projectVersionDescription : response.getProjectVersionDescriptions()) {
				System.out.println("Status: " + projectVersionDescription.getStatus());
			}
			System.out.println("Done...");

		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		
		System.out.println("El modelo esta prendido");
		
		try {
	          
	         StopProjectVersionRequest requeststop = new StopProjectVersionRequest()
	                  .withProjectVersionArn(projectVersionArn); 
	         StopProjectVersionResult result = rekognitionClient.stopProjectVersion(requeststop);
	  
	         System.out.println(result.getStatus());

	      } catch(Exception e) {
	         System.out.println(e.toString());
	      }
	      System.out.println("Done...");
	   }

	
}

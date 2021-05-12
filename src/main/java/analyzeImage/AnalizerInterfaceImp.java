package analyzeImage;

import java.util.ArrayList;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.CustomLabel;
import com.amazonaws.services.rekognition.model.DetectCustomLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectCustomLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class AnalizerInterfaceImp implements AnalizerInterface {

	public static final String MODEL_ARN = "arn:aws:rekognition:us-east-1:682086073548:project/DigitalPatology/version/DigitalPatology.2021-04-26T11.24.35/1619454274199";

	private String photo;
	private String bucket;
	private String projectVersionArn;
	private float minConfidence;
	private ProfileCredentialsProvider credentialsProvider;
	private AmazonS3 s3client;
	private AmazonRekognition amazonRekognition;
	private DetectCustomLabelsResult result;

	public AnalizerInterfaceImp(String bucket) {
		this.bucket = bucket;
		this.minConfidence = 90;
		this.projectVersionArn = this.MODEL_ARN;

		this.credentialsProvider = new ProfileCredentialsProvider();

		try {
			credentialsProvider.getCredentials();

		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. ", e);
		}

		this.s3client = AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider)
				.withRegion(Regions.US_EAST_1).build();

		this.amazonRekognition = AmazonRekognitionClientBuilder.standard().withRegion(Regions.US_EAST_1).build();

	}

	public ArrayList<CustomLabel> analizeImage(String path) {
		this.photo = path;
		ArrayList<CustomLabel> customLabels = new ArrayList<CustomLabel>();

		System.out.println("PHOTO: " + this.photo + "\n" + "Bucket: " + this.bucket);
		DetectCustomLabelsRequest request = new DetectCustomLabelsRequest()
				.withProjectVersionArn(this.projectVersionArn)
				.withImage(new Image().withS3Object(new S3Object().withName(this.photo).withBucket(bucket)))
				.withMinConfidence(minConfidence);

		this.result = amazonRekognition.detectCustomLabels(request);

		customLabels = (ArrayList<CustomLabel>) this.result.getCustomLabels();

		return customLabels;

	}
}

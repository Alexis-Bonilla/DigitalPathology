package test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.buckets.BucketUpload;
import com.amazonaws.buckets.BucketUploadImp;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class BucketUploadImpTest {

	private static ProfileCredentialsProvider credentialsProvider;
	private BucketUpload bucketUploader;
	private AmazonS3 s3Client;
	private S3Object fullObject;
	private String[] testImagesPaths;
	private GetObjectRequest request;

	private void setup1() {

		credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. ", e);
		}

		try {
			s3Client = AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider)
					.withRegion(Regions.US_EAST_1).build();

		} catch (Exception e) {
			System.out.println("Cannot create s3 object");
			e.printStackTrace();
		}

		bucketUploader = new BucketUploadImp(Regions.US_EAST_1);

		testImagesPaths = new String[1];
		testImagesPaths[0] = "C:/Users/alexi/Pictures/Descargas/test.jpg";

	}

	@Test
	public void uploadWithBucketTest() {

		setup1();
		assertTrue(this.bucketUploader.loadImages(this.testImagesPaths, "imagenesanalizadaspatologiadigital"));
		this.request = new GetObjectRequest("imagenesanalizadaspatologiadigital", "test.jpg");
		this.fullObject = s3Client.getObject(request);
		assertNotNull(fullObject);

	}

	@Test
	public void uploadWithNewBucketTest() {

		setup1();
		assertTrue(
				this.bucketUploader.loadImages(this.testImagesPaths, "imagenesanalizadaspatologiadigitaltestbucket"));
		this.request = new GetObjectRequest("imagenesanalizadaspatologiadigitaltestbucket", "test.jpg");
		this.fullObject = s3Client.getObject(request);
		assertTrue(s3Client.doesBucketExist("imagenesanalizadaspatologiadigitaltestbucket"));
		assertNotNull(fullObject);

	}

}

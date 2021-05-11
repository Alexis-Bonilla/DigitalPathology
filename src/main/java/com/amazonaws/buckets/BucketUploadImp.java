package com.amazonaws.buckets;

import java.io.File;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.StringUtils;

public class BucketUploadImp implements BucketUpload {

	private static ProfileCredentialsProvider credentialsProvider = null;
	private static TransferManager tx;
	private static String bucketName;
	private static AmazonS3 s3;
	private static Regions region;

	private Upload upload;
	
	
	public BucketUploadImp(Regions region) {
		this.region= region;
		
		credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. ", e);
		}
		s3 = AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider)
				.withRegion(region)
				.build();
		System.out.println("pase");
		tx = TransferManagerBuilder.standard().withS3Client(s3).build();
		System.out.println("pase el tx");
	}

	public boolean loadImages(String[] adress, String nbucketName) {
		this.bucketName= nbucketName;

		createAmazonS3Bucket(bucketName);

		for (String imageAdress : adress) {
			uploadImage(imageAdress, bucketName);
		}

		return true;
	}

	public void uploadImage(String adress, String bucket) {

		try {

			File toUpload = new File(adress);
			PutObjectRequest request = new PutObjectRequest(bucket, toUpload.getName(), toUpload);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType("image/jpg");
			metadata.addUserMetadata("title", "someTitle");
			request.setMetadata(metadata);
			
			
			
			
			s3.putObject(request);
		} catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }

	}

	public void createAmazonS3Bucket(String bucket) {
		try {
			if (tx.getAmazonS3Client().doesBucketExist(bucket) == false) {
				tx.getAmazonS3Client().createBucket(bucket);
			}
		} catch (AmazonClientException ace) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. ", ace);
		}
	}

}

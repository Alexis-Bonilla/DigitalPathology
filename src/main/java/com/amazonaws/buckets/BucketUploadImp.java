package com.amazonaws.buckets;

import java.io.File;

import javax.swing.JOptionPane;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.StringUtils;

public class BucketUploadImp implements BucketUpload{
	
	 private static ProfileCredentialsProvider credentialsProvider = null;
	 private static TransferManager tx;
	 private static String bucketName;
	 
	 private Upload upload;
	

	public boolean loadImages(String[] adress, String bucketName) {
		
		credentialsProvider = new ProfileCredentialsProvider();
		  try {
	            credentialsProvider.getCredentials();
	        } catch (Exception e) {
	            throw new AmazonClientException(
	                    "Cannot load the credentials from the credential profiles file. " , e);
	        }
		  AmazonS3 s3 = AmazonS3ClientBuilder.standard()
		            .withCredentials(credentialsProvider)
		            .withRegion("us-east-2")
		            .build();  
		  tx = TransferManagerBuilder.standard()
		            .withS3Client(s3)
		            .build();
		  
		  String accesKeyId= credentialsProvider.getCredentials().getAWSAccessKeyId();
		  String bucket= bucketName + StringUtils.lowerCase(accesKeyId);
		  
		  createAmazonS3Bucket(bucket);
		  
		  
		  for(String imageAdress : adress) {
			  uploadImage(imageAdress,bucket);
		  }
		  
		  
		  
		
		return false;
	}
	
	
	public void uploadImage(String adress,String bucket) {
		
		ProgressListener progressListener = new ProgressListener() {
            public void progressChanged(ProgressEvent progressEvent) {
                if (upload == null) return;

                switch (progressEvent.getEventCode()) {
                case ProgressEvent.COMPLETED_EVENT_CODE:
                    break;
                case ProgressEvent.FAILED_EVENT_CODE:
                    try {
                        AmazonClientException e = upload.waitForException();
//                        JOptionPane.showMessageDialog(frame,
//                                "Unable to upload file to Amazon S3: " + e.getMessage(),
//                                "Error Uploading File", JOptionPane.ERROR_MESSAGE);
                        
                        
                    } catch (InterruptedException e) {}
                    break;
                }
            }
        };
		
		
		try {
			
			File fileToUpload= new File(adress);
			PutObjectRequest request= new PutObjectRequest(bucket,fileToUpload.getName(),fileToUpload);
			upload = tx.upload(request);
					
					
			
		}catch (Exception e) {
		
			
		}
		
		
	}
	
	
	public void createAmazonS3Bucket(String bucket) {
		try {
			if(tx.getAmazonS3Client().doesBucketExistV2(bucket)== false) {
				tx.getAmazonS3Client().createBucket(bucket);
			}
		}catch (AmazonClientException ace){
			throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " , ace);
		}
	}

	

}

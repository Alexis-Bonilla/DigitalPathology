package Test;

import com.amazonaws.buckets.BucketUpload;
import com.amazonaws.buckets.BucketUploadImp;
import com.amazonaws.regions.Regions;

public class Test {
	
	public static Regions REGION= Regions.US_EAST_1;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		
		BucketUpload upload= new BucketUploadImp(REGION);
		
		System.out.println("Pase la inciacion");
		String[] images= {"D:/escritorio/tbcimages/tuberculosis-phonecamera/tuberculosis-phone-0001.jpg","D:\\escritorio\\tbcimages\\tuberculosis-phonecamera\\tuberculosis-phone-0002.jpg","D:\\\\escritorio\\\\tbcimages\\\\tuberculosis-phonecamera\\\\tuberculosis-phone-0003.jpg"};
		boolean funciona= upload.loadImages(images, "imagespatologiadigital");
		System.out.println(funciona);	

	}

}

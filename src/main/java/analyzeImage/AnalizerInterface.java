package analyzeImage;

import java.util.ArrayList;

import com.amazonaws.services.rekognition.model.CustomLabel;

public interface AnalizerInterface {

	public ArrayList<CustomLabel> analizeImage(String path);
}

package ics.cs237.childseeking.dummy;

import android.net.Uri;

/**
 * @author CHazyhabiT
 *
 */
public class ImageEntity {

    private Uri imageUri;
    private int id;
    private boolean isSelected;
      
    public ImageEntity(Uri imageUri, int id) {  
        this.imageUri = imageUri;  
        this.id = id;
        this.isSelected = false;
    }  
      
    public Uri getImageUri() {  
        return imageUri;  
    }  
    
    public int getId() {
    	return id;
    }
 
    public boolean isSelected() {  
        return isSelected;  
    }  

    public void setSelected(boolean value) {
    	isSelected = value;
    }
	
}

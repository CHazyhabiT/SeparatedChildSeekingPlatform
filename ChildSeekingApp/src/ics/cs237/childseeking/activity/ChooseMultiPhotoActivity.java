package ics.cs237.childseeking.activity;

import ics.cs237.childseeking.dummy.ImageEntity;
import ics.cs237.childseeking.helper.HttpConstants;
import ics.cs237.childseeking.helper.JsonHandler;
import ics.cs237.childseekingapp.activity.R;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

/**
 * retrieve multiple photos from server given a list of urls
 * and give feed back
 * 
 * @author CHazyhabiT
 *
 */

public class ChooseMultiPhotoActivity extends Activity {

	// collection of items (data)
	ArrayList<ImageEntity> imageEntities;
	
	// UI component
    GridView mGridView;  
    GridAdapter mAdapter;
    Button mButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.choose_multi_photo_activity);
		
		Intent intent = getIntent();
		String dataString = intent.getStringExtra(android.content.Intent.EXTRA_TEXT);
		imageEntities = JsonHandler.interpretUris(dataString);
		
		
        initView(); 
        
        mButton = (Button) findViewById(R.id.button1);
        mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				submit();
			}
		});
        
	}
	
	private void submit() {
		// remind
		setProgressBarIndeterminateVisibility(true);
		Toast.makeText(ChooseMultiPhotoActivity.this, "retrieving locations from server", Toast.LENGTH_LONG).show();
		
		JSONArray jArray = new JSONArray();
		for(int i=0;i<imageEntities.size();i++) {
			ImageEntity entity = imageEntities.get(i);
			if(entity.isSelected()) {
				jArray.put(entity.getId());
			}
		}
		if(jArray.length()<=0)
			jArray.put(-1);
		
		// encapsulate the message
		RequestParams params = new RequestParams();
		params.put(HttpConstants.FEEDBACK, jArray.toString());

		AsyncHttpClient client = new AsyncHttpClient();
		client.post(HttpConstants.UP_LOAD_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, String feedback) {
				// super.onSuccess(arg0, feedback);
				setProgressBarIndeterminateVisibility(false);
				Toast.makeText(ChooseMultiPhotoActivity.this, "Send Successfully!", Toast.LENGTH_LONG).show();
				
				Intent intent = new Intent(ChooseMultiPhotoActivity.this, DisplayLocationsActivity.class);
				intent.setType("text/plain");
				intent.putExtra(android.content.Intent.EXTRA_TEXT, feedback);
			}
		});
	}
	

	/**
	 * initialize the view
	 */
	private void initView(){
		// remind
		setProgressBarIndeterminateVisibility(true);
		Toast.makeText(ChooseMultiPhotoActivity.this, "retrieving data from server...", Toast.LENGTH_LONG).show();
		
		mGridView = (GridView) findViewById(R.id.gridView1);  
        mAdapter = new GridAdapter();  
        mGridView.setAdapter(mAdapter);  
        setProgressBarIndeterminateVisibility(false);
        
        mGridView.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(imageEntities.get(position).isSelected()){  
                    imageEntities.get(position).setSelected(false);  
                } else {  
                    imageEntities.get(position).setSelected(true);  
                }   
                // notify adapter if data changed
                mAdapter.notifyDataSetChanged();  
            }  
              
        });  
    }  
	
	/**
	 * adapter used to bind data and GridView
	 * @author CHazyhabiT
	 *
	 */
	class GridAdapter extends BaseAdapter{  
		  
        @Override  
        public int getCount() {  
            int count = 0;  
            if(imageEntities!=null){  
                count = imageEntities.size();  
            }  
            return count;  
        }  
  
        @Override  
        public Object getItem(int position) {  
            // TODO Auto-generated method stub  
            return null;  
        }  
  
        @Override  
        public long getItemId(int position) {  
            // TODO Auto-generated method stub  
            return 0;  
        }  
  
        @Override  
        public View getView(int position, View convertView, ViewGroup parent) {
        	/**
        	 * used to interpret the subView layout of GridView,
        	 * and set the image and the select status based on the instance of each ImageEntity
        	 */
        	
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item, null);  
            ImageView image = (ImageView) convertView.findViewById(R.id.image);  
            ImageView selectImage = (ImageView) convertView.findViewById(R.id.isselected);  
            Picasso.with(ChooseMultiPhotoActivity.this).load(imageEntities.get(position).getImageUri().toString()).into(image);
              
            if(imageEntities.get(position).isSelected()){  
                selectImage.setVisibility(View.VISIBLE);  
            }else{  
                selectImage.setVisibility(View.GONE);  
            }  
              
            return convertView;  
        }       
    }
	

}

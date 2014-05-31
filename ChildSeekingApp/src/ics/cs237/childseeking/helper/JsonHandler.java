package ics.cs237.childseeking.helper;

import ics.cs237.childseeking.dummy.ImageEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
/**
 * @author CHazyhabiT
 *
 */

public class JsonHandler {
	
	
	/**
	 * encapsulate the additional info into a JSONObject, and return String
	 */
	public static String additionalInfo(String time, String desc, double longitude, double latitude) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(HttpConstants.TIME_KEY, time);
		map.put(HttpConstants.DESC_KEY, desc);
		map.put(HttpConstants.LATITUDE_KEY, latitude);
		map.put(HttpConstants.LONGITUDE_KEY, longitude);
		JSONObject jsonObject = new JSONObject(map); 
		return jsonObject.toString();

	}
	

	/**
	 * interpret the urls of a set of photos feed back from server
	 * @param urlString
	 * @return
	 */
	public static ArrayList<ImageEntity> interpretUris(String urlString) {
		ArrayList<ImageEntity> imageEntities = new ArrayList<ImageEntity>();
		try {
			JSONArray jArray = new JSONArray(urlString);
			for(int i=0;i<jArray.length();i++) {
				JSONObject jObject = jArray.getJSONObject(i);
				String url = jObject.getString(HttpConstants.URI_KEY);
				int id = jObject.getInt(HttpConstants.ID_KEY);
				Uri uri = Uri.parse(url);
				ImageEntity imageEntity = new ImageEntity(uri, id);
				imageEntities.add(imageEntity);	
			}	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageEntities;
	}
	
	
	/**
	 * interpret a list of locations feed back from server
	 * @param locations
	 * @return
	 */
	public static ArrayList<LatLng> interpretLocations(String locationsString) {
		ArrayList<LatLng> locations = new ArrayList<LatLng>();
		try {
			JSONArray jArray = new JSONArray(locationsString);
			for(int i=0;i<jArray.length();i++) {
				JSONObject jObject = jArray.getJSONObject(i);
				double latitude = jObject.getDouble(HttpConstants.LATITUDE_KEY);
				double longitude = jObject.getDouble(HttpConstants.LONGITUDE_KEY);
				LatLng location = new LatLng(latitude, longitude);
				locations.add(location);
			}	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return locations;
	}


	
	

}

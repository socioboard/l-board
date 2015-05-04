package com.socioboard.lbroadpro;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

public class ProfileActivity extends Activity{
	
	ImageView profileimage ;
	String str_pictureUrl;
	
	// Handler to load Images
	Handler handler=new Handler();

	public static final String TAG_IMACCOUNTS="imAccounts";
	public static final String TAG__TOTAL="_total";
	public static final String TAG_FIRSTNAME="firstName";
	public static final String TAG_LASTNAME="lastName";
	public static final String TAG_EMAILADDRESS="emailAddress";
	public static final String TAG_PICTUREURL="pictureUrl";
	public static final String TAG_POSITIONS="positions";
	public static final String TAG_POSITIONS_OBJ__TOTAL="_total";
	public static final String TAG_VALUES="values";
	public static final String TAG_ISCURRENT="isCurrent";
	public static final String TAG_COMPANY="company";
	public static final String TAG_NAME="name";
	public static final String TAG_ID="id";
	public static final String TAG_VALUES_OBJ_ID="id";
	public static final String TAG_TITLE="title";
	public static final String TAG_JSON_ID="id";
	public static final String TAG_HEADLINE="headline";
	public static final String TAG_PHONENUMBERS="phoneNumbers";
	public static final String TAG_PHONENUMBERS_OBJ__TOTAL="_total";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.profile_layout);
		profileimage = (ImageView) findViewById(R.id.profileImage);
		
		// Call ASync tasks to Fetch User
		new GetProfileDetails().execute();
	}
	
	/**
	 *   ASyncTask for Fetching User Details
	 */
	public class GetProfileDetails extends AsyncTask<Void, Void, String>
	{
		
		String stringResponse;
		 
		@Override
		protected String doInBackground(Void... params) {
			
			 try {
		            HttpClient httpclient = new DefaultHttpClient();  // the http-client, that will send the request
		            
		            String uri = "https://api.linkedin.com/v1/people/~:(id,first-name,headline,last-name,"
		            		+ "picture-url,current-status,summary,main-address,phone-numbers,date-of-birth,email-address)"
		            		+ "?oauth2_access_token="+WelcomeActivity.accesstokenis+"&format=json";
		            
		            //String uri = "https://api.linkedin.com/v1/people/id=YLGud7amoM?oauth2_access_token="+WelcomeActivity.accesstokenis+"&format=json";
		            HttpGet httpGet = new HttpGet(uri);
		           
					httpGet.addHeader("Connection", "Keep-Alive");
					httpGet.addHeader("X-Target-URI", "https://api.linkedin.com");
		            
		            HttpResponse response = httpclient.execute(httpGet); // the client executes the request and gets a response
		            
		            int responseCode = response.getStatusLine().getStatusCode();  // check the response code
		            
		            System.out.println("response code"+String.valueOf(responseCode));
		            
		            switch (responseCode) {
		                case 200: { 
		                    // everything is fine, handle the response
		                    stringResponse = EntityUtils.toString(response.getEntity()); 
		                    
		                    
		                    break;
		                }
		                case 500: {
		                    // server problems ?
		                	System.out.println("server problems");
		                    break;
		                }
		                case 403: {
		                    // you have no authorization to access that resource
		                	System.out.println("server problems");
		                    break;
		                }
		            }
		        } catch (ParseException ex) {
		            // handle exception
		        	Log.wtf("LOG_IN_ERROR", ex);
		        }catch (IOException  ex1)
			 {
		        	Log.wtf("LOG_ERROR", ex1);
			 }
			 
			return stringResponse;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			System.out.println("strung response "+stringResponse);
			
			
			try {
				JSONObject json = new JSONObject(result);

				JSONObject imAccounts_obj = json.getJSONObject(TAG_IMACCOUNTS);

				String str__total = imAccounts_obj.getString(TAG__TOTAL);

				String str_firstName = json.getString(TAG_FIRSTNAME);

				String str_lastName = json.getString(TAG_LASTNAME);

				String str_emailAddress = json.getString(TAG_EMAILADDRESS);

				str_pictureUrl = json.getString(TAG_PICTUREURL);

				
				JSONObject positions_obj = json.getJSONObject(TAG_POSITIONS);

				String str_positions_obj__total = positions_obj.getString(TAG_POSITIONS_OBJ__TOTAL);

				JSONArray values = positions_obj.getJSONArray(TAG_VALUES);
				
				for(int values_i = 0; values_i < values.length(); values_i++)
				{
					JSONObject values_obj=values.getJSONObject(values_i);
					String str_isCurrent = values_obj.getString(TAG_ISCURRENT);
					JSONObject company_obj = values_obj.getJSONObject(TAG_COMPANY);
					String str_name = company_obj.getString(TAG_NAME);
					String str_id = company_obj.getString(TAG_ID);
					String str_values_obj_id = values_obj.getString(TAG_VALUES_OBJ_ID);
					String str_title = values_obj.getString(TAG_TITLE);
				}
				
				String str_json_id = json.getString(TAG_JSON_ID);
				String str_headline = json.getString(TAG_HEADLINE);
				JSONObject phoneNumbers_obj = json.getJSONObject(TAG_PHONENUMBERS);
				String str_phoneNumbers_obj__total = phoneNumbers_obj.getString(TAG_PHONENUMBERS_OBJ__TOTAL);

			} catch (JSONException e){
			}
		
		setimage(profileimage, str_pictureUrl);
		}
		
		
		
	}
	
	// Method to Set User Image via Thread
	public void setimage(final ImageView imageview, final String urll)
	 {
		  new Thread(new Runnable() {
			
			@Override
			public void run() {
				URL url = null;
				try {
					url = new URL(urll);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoInput(true);
					connection.connect();
					InputStream input = connection.getInputStream();
					final Bitmap myBitmap = BitmapFactory.decodeStream(input);
					 
					handler.post(new Runnable() 
					{
						@Override
						public void run() {
							imageview.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 80, 80, true));
						}
					});
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			
			}
			
		}).start();
		 
	 }

}

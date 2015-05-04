package com.socioboard.lbroadpro.fragments;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.lbroadpro.R;
import com.socioboard.lbroadpro.common.CommonUtilss;
import com.socioboard.lbroadpro.database.util.MainSingleTon;

public class Company_Detail extends Fragment{
	
	public static final String TAG_EMPLOYEECOUNTRANGE="employeeCountRange";
	public static final String TAG_CODE="code";
	public static final String TAG_NAME="name";
	public static final String TAG_SPECIALTIES="specialties";
	public static final String TAG__TOTAL="_total";
	public static final String TAG_VALUES="values";
	public static final String TAG_WEBSITEURL="websiteUrl";
	public static final String TAG_NUMFOLLOWERS="numFollowers";
	public static final String TAG_JSON_NAME="name";
	public static final String TAG_DESCRIPTION="description";
	public static final String TAG_ID="id";
	public static final String TAG_FOUNDEDYEAR="foundedYear";
	public static final String TAG_LOGOURL="logoUrl";
	
	String id = Company_Profile.companyid;
	String name,description,websiteurl,numfollowers,foundedyear,employeerange,imageurl;
	TextView cpname,cpdesc,cpweburl,cpfollowers,cpfounded,cpemployee;
	ImageView cpimage;
	CommonUtilss utills=new CommonUtilss();
	private RelativeLayout companydetail_main;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.company_detailfragment, container, false);
		
		companydetail_main = (RelativeLayout) rootView.findViewById(R.id.companydetail_main);
		
		companydetail_main.setVisibility(View.GONE);
		
		cpname = (TextView) rootView.findViewById(R.id.company_name);
		cpdesc = (TextView) rootView.findViewById(R.id.company_description);
		cpemployee = (TextView) rootView.findViewById(R.id.company_employeerate);
		cpfollowers = (TextView) rootView.findViewById(R.id.company_follow);
		cpweburl = (TextView) rootView.findViewById(R.id.company_websiteurl);
		cpfounded = (TextView) rootView.findViewById(R.id.company_foundedin);
		cpimage = (ImageView) rootView.findViewById(R.id.company_pic);

		new GetCompanyProfile().execute();
		return rootView;
	}
	
	
	public class GetCompanyProfile extends AsyncTask<Void, Void, Boolean>
    {
    	String stringResponse;
		String company_str_pictureUrl;
		String company_imageString;
		String company_str_description;

		@Override
		protected Boolean doInBackground(Void... params) {
			
			 try {
				 
				 HttpClient httpclient = new DefaultHttpClient();
				 
				 String uri = "https://api.linkedin.com/v1/companies/"+id+":(id,name,ticker,description,logo-url,"
				 		+ "website-url,"
				 		+ "employee-count-range,specialties,founded-year,num-followers)"
				    		+ "?oauth2_access_token="+MainSingleTon.accesstoken+"&format=json";
				 
				 HttpGet httpGet = new HttpGet(uri);
				   
				 httpGet.addHeader("Connection", "Keep-Alive");
				 httpGet.addHeader("X-Target-URI", "https://api.linkedin.com");
				    
				 HttpResponse response = httpclient.execute(httpGet); // the client executes the request and gets a response
				    
				 int responseCode = response.getStatusLine().getStatusCode();
				    
				 System.out.println("response code"+String.valueOf(responseCode));
				    
				    switch (responseCode) {
				        case 200: { 
				            // everything is fine, handle the response
				            stringResponse = EntityUtils.toString(response.getEntity()); 
				            
				            try {
								
				            	JSONObject json = new JSONObject(stringResponse);
				            	JSONObject employeeCountRange_obj = json.getJSONObject(TAG_EMPLOYEECOUNTRANGE);
				   			 	String str_code = employeeCountRange_obj.getString(TAG_CODE);
				   			 	JSONObject specialties_obj = json.getJSONObject(TAG_SPECIALTIES);
				   			 	String str__total = specialties_obj.getString(TAG__TOTAL);
				   			 	String str_id = json.getString(TAG_ID);
				   			 	JSONArray values = specialties_obj.getJSONArray(TAG_VALUES);
				   			 	for(int values_i = 0; values_i < values.length(); values_i++){
				   			 		String str_values=values.getString(values_i);
				   			 	}
				   		
				   			 	employeerange = employeeCountRange_obj.getString(TAG_NAME);
				   			 	websiteurl= json.getString(TAG_WEBSITEURL);
				   			 	numfollowers = json.getString(TAG_NUMFOLLOWERS);
				   			 	description = json.getString(TAG_DESCRIPTION);
				   			 	foundedyear = json.getString(TAG_FOUNDEDYEAR);
				   			 	String imagebyte = utills.getImageBytearray(json.getString(TAG_LOGOURL));
				   			 	imageurl = imagebyte;
				   			 	name = json.getString(TAG_JSON_NAME);
				   			 	
				   			 	
								return true;
								
							} catch (JSONException e){
							}
				            break;
				        }
				        case 500: {
				            // server problems 
				        	try {
								System.out.println("server problems");
								Toast.makeText(getActivity(), "Server Problem", Toast.LENGTH_SHORT).show();
								return false;
							} catch (Exception e) {
								e.printStackTrace();
							}
				            break;
				        }
				        case 403: {
				            // you have no authorization to access that resource
				        	try {
								System.out.println("server problems");
								Toast.makeText(getActivity(), "Authorization Problem", Toast.LENGTH_SHORT).show();
								return false;
							} catch (Exception e) {
								e.printStackTrace();
							}
				            break;
				        }
				    }
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
			return null;
		}
	
		@Override
		protected void onPostExecute(Boolean result) 
		{
			if(result)
			{
				companydetail_main.setVisibility(View.VISIBLE);
				
				cpdesc.setText(description);
				cpemployee.setText(employeerange);
				cpfollowers.setText(numfollowers);
				cpfounded.setText(foundedyear);
				cpname.setText(name);
				cpweburl.setText(websiteurl);
				
				cpimage.setImageBitmap(utills.getBitmapFromString(imageurl));			
			}
			super.onPostExecute(result);
			
			
		}
    	
    }

}

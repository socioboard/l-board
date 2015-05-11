package com.socioboard.lbroadpro.fragments;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.lbroadpro.ConnectionDetector;
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
	
	RelativeLayout progressellay,insiderellay;
	
	
	CommonUtilss utills=new CommonUtilss();
	private RelativeLayout companydetail_main;
	ConnectionDetector cd;
	Dialog dialog;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.company_detailfragment, container, false);
		cd= new ConnectionDetector(getActivity());
		
		progressellay = (RelativeLayout) rootView.findViewById(R.id.progressellay);
		insiderellay= (RelativeLayout) rootView.findViewById(R.id.insiderellay);
		
		companydetail_main = (RelativeLayout) rootView.findViewById(R.id.companydetail_main);
		
		companydetail_main.setVisibility(View.GONE);
		
		cpname = (TextView) rootView.findViewById(R.id.company_name);
		cpdesc = (TextView) rootView.findViewById(R.id.company_description);
		cpemployee = (TextView) rootView.findViewById(R.id.company_employeerate);
		cpfollowers = (TextView) rootView.findViewById(R.id.company_follow);
		cpweburl = (TextView) rootView.findViewById(R.id.company_websiteurl);
		cpfounded = (TextView) rootView.findViewById(R.id.company_foundedin);
		cpimage = (ImageView) rootView.findViewById(R.id.company_pic);

		if(cd.isConnectingToInternet())
    	{
			progressellay.setVisibility(View.VISIBLE);
			insiderellay.setVisibility(View.GONE);
			new GetCompanyProfile().execute();
    	}else
    	{
    		progressellay.setVisibility(View.GONE);
			insiderellay.setVisibility(View.GONE);   		
    		showCustomDialog();
    	}
		
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
				
				   			 	employeerange = employeeCountRange_obj.getString(TAG_NAME);
				   			 	System.out.println("range "+employeerange);
				   			 	websiteurl= json.getString(TAG_WEBSITEURL);
				   			 System.out.println("weburl  "+websiteurl);
				   			 	numfollowers = json.getString(TAG_NUMFOLLOWERS);
				   			 	description = json.getString(TAG_DESCRIPTION);
				   			 System.out.println("desp "+description);
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
	        
			return false;
		}
	
		@Override
		protected void onPostExecute(Boolean result) 
		{
			if(result)
			{
				progressellay.setVisibility(View.GONE);
				insiderellay.setVisibility(View.VISIBLE);
				
				companydetail_main.setVisibility(View.VISIBLE);
			
				cpdesc.setText(description);
				cpemployee.setText(employeerange);
				cpfollowers.setText(numfollowers);
				cpfounded.setText(foundedyear);
				cpname.setText(name);
				cpweburl.setText(websiteurl);
				
				cpimage.setImageBitmap(utills.getBitmapFromString(imageurl));			
			}else
			{
				try {
					progressellay.setVisibility(View.GONE);
					insiderellay.setVisibility(View.GONE);
					Toast.makeText(getActivity(), "Error in Response !!", Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			super.onPostExecute(result);
		}
    }
	
	protected void showCustomDialog() {

	    dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent);
	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    dialog.setCancelable(false);
	    dialog.setContentView(R.layout.noconnection_dialog);

	    ImageView exitcancel;
	    exitcancel = (ImageView)dialog.findViewById(R.id.internetcancel);
	     
	    exitcancel.setOnClickListener(new OnClickListener() {
	     
	     @Override
	     public void onClick(View v) 
	     {
	      dialog.dismiss();
	      //getActivity().finish();
	     }
	    });
	    dialog.show();
}

}

package com.socioboard.lbroadpro.fragments;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.socioboard.lbroadpro.R;
import com.socioboard.lbroadpro.adapter.Company_Profileadapter;
import com.socioboard.lbroadpro.database.util.MainSingleTon;
import com.socioboard.lbroadpro.models.CompanyDetails;

public class Company_Updates extends Fragment{
	
	public static final String TAG_FOLLOWING="following";
	public static final String TAG_COMPANIES="companies";
	public static final String TAG_COMPANIES_OBJ__TOTAL="_total";
	public static final String TAG_COMPANIES_OBJ_VALUES="values";
	public static final String TAG_COMPANIES_OBJ_VALUES_OBJ_NAME="name";
	public static final String TAG_COMPANIES_OBJ_VALUES_OBJ_ID="id";
	
	ArrayList<CompanyDetails> modelclass = new ArrayList<CompanyDetails>();
	public static String company_id;
	ListView companylistview;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.company_updateid, container, false);
		new GetCompaniesID().execute();
    	companylistview = (ListView) rootView.findViewById(R.id.company_list);
		return rootView;
	}
	
	public class GetCompaniesID extends AsyncTask<Void, Void,Boolean>
    {
    	String stringResponse;
		String company_str_pictureUrl;
		String company_imageString;
		String company_str_description;

		@Override
		protected Boolean doInBackground(Void... params) {
			
			 try {
		            HttpClient httpclient = new DefaultHttpClient();  // the http-client, that will send the request

		            String uri = "https://api.linkedin.com/v1/people/~:(following)"
		            		+ "?oauth2_access_token="+MainSingleTon.accesstoken+"&format=json";
		            
		            System.out.println("uri "+uri);
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
		                    
		                    try {
		                    	
		                    JSONObject json = new JSONObject(stringResponse);
		                
			    			JSONObject following_obj = json.getJSONObject(TAG_FOLLOWING);
		    				JSONObject companies_obj = following_obj.getJSONObject(TAG_COMPANIES);
		    				JSONArray companies_obj_values = companies_obj.getJSONArray(TAG_COMPANIES_OBJ_VALUES);
		    				
		    				for(int values_i = 0; values_i < companies_obj_values.length(); values_i++){
		    							
		    					CompanyDetails model = new CompanyDetails();
		    					JSONObject companies_obj_values_obj=companies_obj_values.getJSONObject(values_i);
		    					
		    					String str_companies_obj_values_obj_name = companies_obj_values_obj.getString(TAG_COMPANIES_OBJ_VALUES_OBJ_NAME);
		    					String str_companies_obj_values_obj_id = companies_obj_values_obj.getString(TAG_COMPANIES_OBJ_VALUES_OBJ_ID);
		    					
		    					model.setCompanyname(companies_obj_values_obj.getString(TAG_COMPANIES_OBJ_VALUES_OBJ_NAME));
		    					model.setCompanyid(companies_obj_values_obj.getString(TAG_COMPANIES_OBJ_VALUES_OBJ_ID));
		    					
		    					modelclass.add(model);
		    				}
		    				
		    				  return true;
		    			} catch (JSONException e){
		        			e.printStackTrace();	    
		    			}
		                  
		                    break;
		                }
		                case 500: {
		                    // server problems 
		                	System.out.println("server problems");
		                	try {
								return false;
							} catch (Exception e) {
								e.printStackTrace();
							}
		                    break;
		                }
		                case 403: {
		                    // you have no authorization to access that resource
		                	System.out.println("server problems");
		                	try {
								return false;
							} catch (Exception e) {
								e.printStackTrace();
							}
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
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			if(result&&modelclass.size()>0)
			{
				Company_Profile.iscompanyprofile=false;
				Company_Profileadapter adapter = new Company_Profileadapter(getActivity(), modelclass);
				companylistview.setAdapter(adapter);
			}else
			{
				
			}
		}
    	
    }

}

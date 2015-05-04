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

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.lbroadpro.R;
import com.socioboard.lbroadpro.adapter.Skills_Adapter;
import com.socioboard.lbroadpro.common.CommonUtilss;
import com.socioboard.lbroadpro.database.util.MainSingleTon;
import com.socioboard.lbroadpro.models.SkillsModel;

public class User_Profile extends Fragment {

	public static final String TAG_SKILLS="skills";
	public static final String TAG__TOTAL="_total";
	public static final String TAG_VALUES="values";
	public static final String TAG_SKILL="skill";
	public static final String TAG_NAME="name";
	public static final String TAG_ID="id";
	public static final String TAG_FIRSTNAME="firstName";
	public static final String TAG_LASTNAME="lastName";
	public static final String TAG_EMAILADDRESS="emailAddress";
	public static final String TAG_FOLLOWING="following";
	public static final String TAG_SPECIALEDITIONS="specialEditions";
	public static final String TAG_SPECIALEDITIONS_OBJ__TOTAL="_total";
	public static final String TAG_COMPANIES="companies";
	public static final String TAG__COUNT="_count";
	public static final String TAG__START="_start";
	public static final String TAG_COMPANIES_OBJ__TOTAL="_total";
	public static final String TAG_COMPANIES_OBJ_VALUES="values";
	public static final String TAG_COMPANIES_OBJ_VALUES_OBJ_NAME="name";
	public static final String TAG_COMPANIES_OBJ_VALUES_OBJ_ID="id";
	public static final String TAG_INDUSTRIES="industries";
	public static final String TAG_INDUSTRIES_OBJ__TOTAL="_total";
	public static final String TAG_INDUSTRIES_OBJ_VALUES="values";
	public static final String TAG_INDUSTRIES_OBJ_VALUES_OBJ_ID="id";
	public static final String TAG_PEOPLE="people";
	public static final String TAG_PEOPLE_OBJ__TOTAL="_total";
	public static final String TAG_PICTUREURL="pictureUrl";
	public static final String TAG_EDUCATIONS="educations";
	public static final String TAG_EDUCATIONS_OBJ__TOTAL="_total";
	public static final String TAG_EDUCATIONS_OBJ_VALUES="values";
	public static final String TAG_EDUCATIONS_OBJ_VALUES_OBJ_ID="id";
	public static final String TAG_SCHOOLNAME="schoolName";
	public static final String TAG_JSON_ID="id";
	public static final String TAG_HEADLINE="headline";
	
	TextView username,userheadline,firstname,lastname,emailid;//,degree,school;
	String nametext,headlinetext,lasttext,emailtext,degreetext,schooltext,userpictureurl;
	CommonUtilss utilss=new CommonUtilss();
	ImageView user_profilepic;

	Bitmap userimage;
	ProgressBar progressBar1;
	Integer firstresponse=null;
	RelativeLayout mainlayout;
	ListView skillslv;
	TextView skillstext;
	ArrayList<SkillsModel> skillslist = new ArrayList<SkillsModel>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.userprofile_fragment, container, false);
		
		//Initialize layout parameters
		username = (TextView) rootView.findViewById(R.id.profile_username);
		userheadline = (TextView) rootView.findViewById(R.id.profile_userheadline);
		firstname= (TextView) rootView.findViewById(R.id.TextViewfirstName);
		lastname = (TextView) rootView.findViewById(R.id.TextViewlastname);
		emailid = (TextView) rootView.findViewById(R.id.TextViewemialaddress);
		/*degree = (TextView) rootView.findViewById(R.id.TextViewdegree);
		school = (TextView) rootView.findViewById(R.id.TextViewuniversity);*/
		user_profilepic = (ImageView) rootView.findViewById(R.id.user_profile_pic);
		progressBar1 = (ProgressBar) rootView.findViewById(R.id.progressBar1);
		mainlayout = (RelativeLayout) rootView.findViewById(R.id.profile_mainrellay);
		skillslv = (ListView) rootView.findViewById(R.id.skillslistview);
		skillstext = (TextView) rootView.findViewById(R.id.skillstext);
		
		progressBar1.setVisibility(View.VISIBLE);
		mainlayout.setVisibility(View.GONE);
		
		//Getting User details in ASyncTask
		new GetProfileDetails().execute();
		
		return rootView;
	}

	/**
	 * 
	 * @author SB
	 *	ASyncTask for Fetching User Details
	 */
	public class GetProfileDetails extends AsyncTask<Void, Void, Boolean>
	{
		String stringResponse;
		String str_pictureUrl;
		String imageString;
		 
		@Override
		protected Boolean doInBackground(Void... params) {
			
			 try {
		            HttpClient httpclient = new DefaultHttpClient();  // the http-client, that will send the request
		            
		            System.out.println("access token "+MainSingleTon.accesstoken);
		            String uri = "https://api.linkedin.com/v1/people/~:(id,first-name,skills,educations,headline,"
		            		+ "last-name,picture-url,current-status,summary,main-address,email-address,following)"
		            		+ "?oauth2_access_token="+MainSingleTon.accesstoken+"&format=json";
		            
		            System.out.println("uri "+uri);
		            HttpGet httpGet = new HttpGet(uri);
		           
					httpGet.addHeader("Connection", "Keep-Alive");
					httpGet.addHeader("X-Target-URI", "https://api.linkedin.com");
		            
		            HttpResponse response = httpclient.execute(httpGet); // the client executes the request and gets a response
		            
		            int responseCode = response.getStatusLine().getStatusCode();  // check the response code
		            System.out.println("response code"+String.valueOf(responseCode));
		            
		            firstresponse = responseCode;
		            
		            switch (responseCode) {
		                case 200: { 
		                    // everything is fine, handle the response
		                    stringResponse = EntityUtils.toString(response.getEntity()); 
		                    
		                    try {
		                    JSONObject json = new JSONObject(stringResponse);
		                
		    				nametext = json.getString(TAG_FIRSTNAME);
		    				lasttext = json.getString(TAG_LASTNAME);
		    				emailtext = json.getString(TAG_EMAILADDRESS);
		    				str_pictureUrl = json.getString(TAG_PICTUREURL);
		    				
		    				System.out.println("str_url "+str_pictureUrl);
		    				
		    				userpictureurl = utilss.getImageBytearray(str_pictureUrl);
		    				
		    				MainSingleTon.userimage=userpictureurl;
		    				
		    				String str_json_id = json.getString(TAG_JSON_ID);
			    			headlinetext = json.getString(TAG_HEADLINE);
		    			
			    			JSONObject educations_obj = json.getJSONObject(TAG_EDUCATIONS);
		    				String str_educations_obj__total = educations_obj.getString(TAG_EDUCATIONS_OBJ__TOTAL);

		    				System.out.println("Value of educations "+str_educations_obj__total);
		    				
		    				if(str_educations_obj__total.contains("1")|str_educations_obj__total.contains("0"))
		    				{
		    					System.out.println("null values");
		    				}else
		    				{
		    					JSONArray educations_obj_values = educations_obj.getJSONArray(TAG_EDUCATIONS_OBJ_VALUES);
			    				for(int values_i = 0; values_i < educations_obj_values.length(); values_i++){
			    						
			    					JSONObject educations_obj_values_obj=educations_obj_values.getJSONObject(values_i);
			    					
			    					String str_educations_obj_values_obj_id = educations_obj_values_obj.getString(TAG_EDUCATIONS_OBJ_VALUES_OBJ_ID);

			    					String str_schoolName = educations_obj_values_obj.getString(TAG_SCHOOLNAME);
			    				}
		    				}
		    				
		    				JSONObject skills_obj = json.getJSONObject(TAG_SKILLS);
		    				String str__total = skills_obj.getString(TAG__TOTAL);

		    				JSONArray values = skills_obj.getJSONArray(TAG_VALUES);
		    				for(int values_i = 0; values_i < values.length(); values_i++)
		    				{
		    					SkillsModel model= new SkillsModel();
		    					JSONObject values_obj=values.getJSONObject(values_i);
		    					JSONObject skill_obj = values_obj.getJSONObject(TAG_SKILL);

		    					String str_name = skill_obj.getString(TAG_NAME);
		    					model.setSkillname(str_name);
		    					String str_id = values_obj.getString(TAG_ID);
		    					
		    					skillslist.add(model);

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
		 
		//Based on the Result, set all Valid Details
		if(result)
		{
			progressBar1.setVisibility(View.GONE);
			mainlayout.setVisibility(View.VISIBLE);
			
			firstname.setText(nametext);
			lastname.setText(lasttext);
			userheadline.setText(headlinetext);
			emailid.setText(emailtext);
			username.setText(nametext);
			user_profilepic.setImageBitmap(utilss.getBitmapFromString(MainSingleTon.userimage));
			
			if(skillslist.size()>0)
			{
				Skills_Adapter adapter = new Skills_Adapter(skillslist, getActivity());
				skillslv.setAdapter(adapter);
				
			}else
			{
				skillstext.setVisibility(View.GONE);
				Toast.makeText(getActivity(), "No Skills present !!", Toast.LENGTH_SHORT).show();
			}
		
		}
		//Error in Response, show toast message
		else
		{
			Toast.makeText(getActivity(), "Error in response", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	}
}

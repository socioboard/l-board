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
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.socioboard.lbroadpro.R;
import com.socioboard.lbroadpro.adapter.Company_Updateadapter;
import com.socioboard.lbroadpro.database.util.MainSingleTon;
import com.socioboard.lbroadpro.models.CompanyUpdatemodel;

public class Company_Update extends Fragment{
	
	public static final String TAG__COUNT="_count";
	public static final String TAG__START="_start";
	public static final String TAG__TOTAL="_total";
	public static final String TAG_VALUES="values";
	public static final String TAG_TIMESTAMP="timestamp";
	public static final String TAG_UPDATECONTENT="updateContent";
	public static final String TAG_COMPANYJOBUPDATE="companyJobUpdate";
	public static final String TAG_JOB="job";
	public static final String TAG_SITEJOBREQUEST="siteJobRequest";
	public static final String TAG_URL="url";
	public static final String TAG_LOCATIONDESCRIPTION="locationDescription";
	public static final String TAG_DESCRIPTION="description";
	public static final String TAG_COMPANY="company";
	public static final String TAG_NAME="name";
	public static final String TAG_POSITION="position";
	public static final String TAG_TITLE="title";

	String company_id;
	static String total ;
	String jobUrl,jobLocation,jobDescription,companyName,jobTitle,updateTime;
	ListView companyupdatelist;
	TextView nodatatext;
	ArrayList<CompanyUpdatemodel> companylist = new ArrayList<CompanyUpdatemodel>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_companyupdate, container, false);
		
		companyupdatelist = (ListView) rootView.findViewById(R.id.companyupdatelist);
		nodatatext = (TextView) rootView.findViewById(R.id.nodatatext);
		
		nodatatext.setVisibility(View.GONE);
		
		company_id = Company_Updates.company_id;
		
		new GetCompanyUpdates().execute();
		
		total = "0";
		
		return rootView;
	}

	public class GetCompanyUpdates extends AsyncTask<Void,Void, Boolean>
	{
		String stringResponse;
		String company_str_pictureUrl;
		
		@Override
		protected Boolean doInBackground(Void... params) {
			
			 try {
		            HttpClient httpclient = new DefaultHttpClient();  // the http-client, that will send the request

		            String uri = "https://api.linkedin.com/v1/companies/"+company_id+"/updates?count=5&"
		            		+ "oauth2_access_token="+MainSingleTon.accesstoken+"&format=json";
		            
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
			   
			        			total = json.getString(TAG__TOTAL);

			        			System.out.println("totla "+total);
			        			if(total.equals("0")||total.contains("0")||total=="0")
			        			{
			        				return true;
			        			}else
			        			{
			        				JSONArray values = json.getJSONArray(TAG_VALUES);
			        				
				        			for(int values_i = 0; values_i < values.length(); values_i++){
				        					
				        				CompanyUpdatemodel listmodel = new CompanyUpdatemodel();
				        				JSONObject values_obj=values.getJSONObject(values_i);
				        				updateTime = values_obj.getString(TAG_TIMESTAMP);
				        				
				        				JSONObject updateContent_obj = values_obj.getJSONObject(TAG_UPDATECONTENT);
			        					
				        				if(updateContent_obj.has(TAG_COMPANYJOBUPDATE))
				        				{
				        					System.out.println(" inside job");
				        					
				        					JSONObject companyJobUpdate_obj = updateContent_obj.getJSONObject(TAG_COMPANYJOBUPDATE);
					        				JSONObject job_obj = companyJobUpdate_obj.getJSONObject(TAG_JOB);
					        				
					        				JSONObject siteJobRequest_obj = job_obj.getJSONObject(TAG_SITEJOBREQUEST);
					        					jobUrl = siteJobRequest_obj.getString(TAG_URL);
					        					listmodel.setJoburl(jobUrl);
					        					
					        				jobLocation = job_obj.getString(TAG_LOCATIONDESCRIPTION);
					        				listmodel.setJoblocation(jobLocation);
					        				
					        				System.out.println("location "+jobLocation);
					        				
					        				jobDescription = job_obj.getString(TAG_DESCRIPTION);
					        				listmodel.setJobdescription(jobDescription);

					        				JSONObject company_obj = job_obj.getJSONObject(TAG_COMPANY);
					        					companyName = company_obj.getString(TAG_NAME);
					        					listmodel.setCompanyname(companyName);
					        					
					        				JSONObject position_obj = job_obj.getJSONObject(TAG_POSITION);
					        					jobTitle = position_obj.getString(TAG_TITLE);
					        					listmodel.setJobtitle(jobTitle);
					        					
					        					companylist.add(listmodel);
				        				}else
				        				{
				        					System.out.println("outside job");
				        					total="0";
				        				}
				        				
				        			}
				        			return true;
			        			}	        			
							} catch (Exception e) {
								// TODO: handle exception
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
			
			if(result)
			{
				System.out.println("result ");
				if(total.contains("0")||total.equals("0"))
				{
					nodatatext.setVisibility(View.VISIBLE);
				}else
				{
					Company_Updateadapter adpater = new Company_Updateadapter(getActivity(), companylist);
					companyupdatelist.setAdapter(adpater);
				}
				
			}else
			{
				Toast.makeText(getActivity(), "Error in response", Toast.LENGTH_SHORT).show();
			}
		}

	}
}

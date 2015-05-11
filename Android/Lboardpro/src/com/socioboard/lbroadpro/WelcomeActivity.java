package com.socioboard.lbroadpro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.socioboard.lbroadpro.common.ApplicationData;
import com.socioboard.lbroadpro.common.Base64;
import com.socioboard.lbroadpro.common.CommonUtilss;
import com.socioboard.lbroadpro.database.util.LinkedInMultipleLocaldata;
import com.socioboard.lbroadpro.database.util.MainSingleTon;
import com.socioboard.lbroadpro.database.util.ModelUserDatas;

public class WelcomeActivity extends Activity {
	
	Button connect;
	CommonUtilss utilss;
	LinkedInMultipleLocaldata db;
	Boolean isalreadyadded = false;

	//This is the public api key of our application
	private static final String API_KEY = ApplicationData.CONSUMER_KEY;
	//This is the private api key of our application
	private static final String SECRET_KEY = ApplicationData.CONSUMER_SECRET;
	//This is any string we want to use. This will be used for avoid CSRF attacks. You can generate one here: http://strongpasswordgenerator.com/
	private static final String STATE = "E3ZHKC1T6H2yP4z";
	//This is the url that LinkedIn Auth process will redirect to. We can put whatever we want that starts with http:// or https:// .
	//We use a made up url that we will intercept when redirecting. Avoid Uppercases. 
	private static final String REDIRECT_URI = ApplicationData.CALLBACK_URL;

	//These are constants used for build the urls
	private static final String AUTHORIZATION_URL = "https://www.linkedin.com/uas/oauth2/authorization";
	private static final String ACCESS_TOKEN_URL = "https://www.linkedin.com/uas/oauth2/accessToken";
	
	private static final String SECRET_KEY_PARAM = "client_secret";
	private static final String RESPONSE_TYPE_PARAM = "response_type";
	private static final String GRANT_TYPE_PARAM = "grant_type";
	private static final String GRANT_TYPE = "authorization_code";
	private static final String RESPONSE_TYPE_VALUE ="code";
	private static final String CLIENT_ID_PARAM = "client_id";
	private static final String STATE_PARAM = "state";
	private static final String REDIRECT_URI_PARAM = "redirect_uri";
	
	private static final String QUESTION_MARK = "?";
	private static final String AMPERSAND = "&";
	private static final String EQUALS = "=";

	public static final String TAG_FIRSTNAME="firstName";
	public static final String TAG_LASTNAME="lastName";
	public static final String TAG_EMAILADDRESS="emailAddress";
	public static final String TAG_PICTUREURL="pictureUrl";
	public static final String TAG_ID="id";
	public static final String TAG_HEADLINE="headline";
	public static final String TAG__TOTAL="_total";
	
	static Boolean haspic=false;
	public static String accesstokenis = "";
	private WebView webView;
	private ProgressDialog pd,pd1;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
		connect = (Button) findViewById(R.id.signin);
		db = new LinkedInMultipleLocaldata(getApplicationContext());
		utilss = new CommonUtilss();
		
		connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Open Dialog to open web-view for sign-In
				showCustomDialog();		
			}
		});
	}
	
	// method name: showCustomDialog (Create and load Dialog, with Web-view)
	protected void showCustomDialog() {
		dialog = new Dialog(WelcomeActivity.this,
				android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(true);
		dialog.setContentView(R.layout.webview_dialog);
		
		ImageView cancel_button = (ImageView) dialog.findViewById(R.id.cancel_button);
		
		 //get the webView from the layout
	    webView = (WebView) dialog.findViewById(R.id.relogin_web_view);

	    //Request focus for the webview
	    webView.requestFocus(View.FOCUS_DOWN);

	    //Show a progress dialog to the user
	    pd = ProgressDialog.show(this, "", this.getString(R.string.loading),true);

	    //Set a custom web view client
	    webView.setWebViewClient(new WebViewClient(){
	          @Override
	          public void onPageFinished(WebView view, String url) {
	                //This method will be executed each time a page finished loading.
	                //The only we do is dismiss the progressDialog, in case we are showing any.
	              if(pd!=null && pd.isShowing()){
	                  pd.dismiss();
	              }
	          }
	        @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl) {
	            //This method will be called when the Auth proccess redirect to our RedirectUri.
	            //We will check the url looking for our RedirectUri.
	            if(authorizationUrl.startsWith(REDIRECT_URI)){
	                Log.i("Authorize", "");
	                Uri uri = Uri.parse(authorizationUrl);
	                //We take from the url the authorizationToken and the state token. We have to check that the state token returned by the Service is the same we sent.
	                //If not, that means the request may be a result of CSRF and must be rejected.
	                String stateToken = uri.getQueryParameter(STATE_PARAM);
	                if(stateToken==null || !stateToken.equals(STATE)){
	                    Log.e("Authorize", "State token doesn't match");
	                    return true;
	                }

	                //If the user doesn't allow authorization to our application, the authorizationToken Will be null.
	                String authorizationToken = uri.getQueryParameter(RESPONSE_TYPE_VALUE);
	                if(authorizationToken==null){
	                    Log.i("Authorize", "The user doesn't allow authorization.");
	                    return true;
	                }
	                Log.i("Authorize", "Auth token received: "+authorizationToken);

	                //Generate URL for requesting Access Token
	                String accessTokenUrl = getAccessTokenUrl(authorizationToken);
	                //We make the request in a AsyncTask
	                new PostRequestAsyncTask().execute(accessTokenUrl);
	                
	                dialog.dismiss();

	            }else{
	                //Default behaviour
	                Log.i("Authorize","Redirecting to: "+authorizationUrl);
	                webView.loadUrl(authorizationUrl);
	            }
	            return true;
	        }
	    });

	    cancel_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

	    //Get the authorization Url
	    String authUrl = getAuthorizationUrl();
	    Log.i("Authorize","Loading Auth Url: "+authUrl);
	    //Load the authorization URL into the webView
	    webView.loadUrl(authUrl);

		db.getAllUsersData();
		
		dialog.show();
		
	}

	/**
	 *  Set User Image While Login
	 *//*
	class Setuserdata extends AsyncTask<String, Void, Void> {
		String imageString;
		@Override
		protected Void doInBackground(String... params) {
			String photourl = params[0];
			imageString = utilss.getImageBytearray(photourl);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}*/
	
	/**
	 * Method that generates the url for get the access token from the Service
	 * @return Url
	 */
	private static String getAccessTokenUrl(String authorizationToken){
	    return ACCESS_TOKEN_URL
	            +QUESTION_MARK
	            +GRANT_TYPE_PARAM+EQUALS+GRANT_TYPE
	            +AMPERSAND
	            +RESPONSE_TYPE_VALUE+EQUALS+authorizationToken
	            +AMPERSAND
	            +REDIRECT_URI_PARAM+EQUALS+REDIRECT_URI
	            +AMPERSAND
	            +CLIENT_ID_PARAM+EQUALS+API_KEY
	            +AMPERSAND
	            +SECRET_KEY_PARAM+EQUALS+SECRET_KEY;
	}

	/**
	 * Method that generates the url for get the authorization token from the Service
	 * @return Url
	 */
	private static String getAuthorizationUrl(){
	    return AUTHORIZATION_URL
	            +QUESTION_MARK+RESPONSE_TYPE_PARAM+EQUALS+RESPONSE_TYPE_VALUE
	            +AMPERSAND+CLIENT_ID_PARAM+EQUALS+API_KEY
	            +AMPERSAND+STATE_PARAM+EQUALS+STATE
	            +AMPERSAND+REDIRECT_URI_PARAM+EQUALS+REDIRECT_URI;
	}
	
	/**
	 * 
	 *   ASyncTask for Fetching AccessToken/Expiry Date
	 *
	 */
	private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean>{

	    @Override
	    protected void onPreExecute(){
	        pd = ProgressDialog.show(WelcomeActivity.this, "", WelcomeActivity.this.getString(R.string.loading),true);
	    }

	    @Override
	    protected Boolean doInBackground(String... urls) {
	        if(urls.length>0){
	            String url = urls[0];
	            
	            //Default HttpClient and HttpPost
	            HttpClient httpClient = new DefaultHttpClient();
	            HttpPost httpost = new HttpPost(url);
	            try{
	                HttpResponse response = httpClient.execute(httpost);
	                if(response!=null){
	                    //If status is OK 200
	                    if(response.getStatusLine().getStatusCode()==200){
	                        String result = EntityUtils.toString(response.getEntity());
	                        
	                        //Convert the string result to a JSON Object
	                        JSONObject resultJson = new JSONObject(result);
	                        //Extract data from JSON Response
	                        int expiresIn = resultJson.has("expires_in") ? resultJson.getInt("expires_in") : 0;

	                        String accessToken = resultJson.has("access_token") ? resultJson.getString("access_token") : null;
	                        Log.e("Tokenm", ""+accessToken);
	                        if(expiresIn>0 && accessToken!=null){
	                            Log.i("Authorize", "This is the access Token: "+accessToken+". It will expires in "+expiresIn+" secs");

	                            accesstokenis = accessToken;
	                            //Calculate date of expiration
	                            Calendar calendar = Calendar.getInstance();
	                            calendar.add(Calendar.SECOND, expiresIn);
	                            long expireDate = calendar.getTimeInMillis();

	                            //Store both expires in and access token in shared preferences
	                            SharedPreferences preferences = WelcomeActivity.this.getSharedPreferences("user_info", 0);
	                            SharedPreferences.Editor editor = preferences.edit();
	                            editor.putLong("expires", expireDate);
	                            editor.putString("accessToken", accessToken);
	                            editor.commit();

	                            return true;
	                        }
	                    }
	                }
	            }catch(IOException e){
	                Log.e("Authorize","Error Http response "+e.getLocalizedMessage());  
	            }
	            catch (ParseException e) {
	                Log.e("Authorize","Error Parsing Http response "+e.getLocalizedMessage());
	            } catch (JSONException e) {
	                Log.e("Authorize","Error Parsing Http response "+e.getLocalizedMessage());
	            }
	        }
	        return false;
	    }

	    @Override
	    protected void onPostExecute(Boolean status){
	    	if(pd!=null && pd.isShowing()){
	            pd.dismiss();
	        }
	        if(status){
	        	//Get the User Profile details in ASync Task
	        	new GetProfileDetails().execute();
	        }
	    }
	};
	
	// ASync Class for Getting Profile Details of User
	public class GetProfileDetails extends AsyncTask<Void, Void, Boolean>
	{
		String stringResponse;
		String str_pictureUrl;
		String imageString;
		String picUrl;
		 
		@Override
	    protected void onPreExecute(){
	        pd1 = ProgressDialog.show(WelcomeActivity.this, "", WelcomeActivity.this.getString(R.string.loading),true);
	    }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			
			 try {
		            HttpClient httpclient = new DefaultHttpClient();  // the http-client, that will send the request
		            
		            String uri = "https://api.linkedin.com/v1/people/~:(id,first-name,headline,last-name,"
		            		+ "picture-url,current-status,email-address)"
		            		+ "?oauth2_access_token="+WelcomeActivity.accesstokenis+"&format=json";
		            
		            HttpGet httpGet = new HttpGet(uri);
		           
					httpGet.addHeader("Connection", "Keep-Alive");
					httpGet.addHeader("X-Target-URI", "https://api.linkedin.com");
		            
		            HttpResponse response = httpclient.execute(httpGet); // the client executes the request and gets a response
		            
		            int responseCode = response.getStatusLine().getStatusCode();  // check the response code
		            
		            switch (responseCode) {
		                case 200: { 
		                    // everything is fine, handle the response
		                    stringResponse = EntityUtils.toString(response.getEntity()); 
		                    
		                    try {
		        				
		                    	JSONObject json = new JSONObject(stringResponse);
		        				
		        				String str_firstName = json.getString(TAG_FIRSTNAME);
		        				String str_lastName = json.getString(TAG_LASTNAME);
		        				String str_emailAddress = json.getString(TAG_EMAILADDRESS);
		        				String str_id = json.getString(TAG_ID);
		        				String str_headline = json.getString(TAG_HEADLINE);
		        				
		        				if(json.has(TAG_PICTUREURL))
		        				{
		        					haspic=true;
		        					picUrl  = json.getString(TAG_PICTUREURL);
		        				}else
		        				{
		        					haspic=false;
		        					Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.user_default);
		        					ByteArrayOutputStream stream = new ByteArrayOutputStream();
		        					bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		        					imageString = Base64.encode(stream.toByteArray());
		        				}
		        				
		        				
		        				
		        				
		        				// Checking if user is already added
		        				if(db.getUserData(str_id)!=null){
		        					isalreadyadded = true;
		        					return false;
		        				}else{
		        					isalreadyadded = false;
		        					
		        					ModelUserDatas datas = new ModelUserDatas();
			        				datas.setUserAcessToken(accesstokenis);
			        				datas.setUserid(str_id);
			        				datas.setUsername(str_firstName);
			        				datas.setLastname(str_lastName);
			        				datas.setUseremailid(str_emailAddress);
			        				datas.setUserheadline(str_headline);
			        				
			        				if(haspic)
			        				{
			        					str_pictureUrl = picUrl;
				        				imageString = utilss.getImageBytearray(str_pictureUrl);
				        				datas.setUserimage(imageString);
			        				}else
			        				{
			        					datas.setUserimage(imageString);
			        				}
			        				
		        					db.addNewUserAccount(datas);
		        					
		        					MainSingleTon.username=str_firstName;
			        				MainSingleTon.userlastname=str_lastName;
			        				MainSingleTon.userimage=imageString;
			        				MainSingleTon.accesstoken=accesstokenis;
			        				MainSingleTon.userid=str_id;
			        				MainSingleTon.userheadline=str_headline;
			        				MainSingleTon.useremailid=str_emailAddress;
			        				
			        				MainSingleTon.userdetails.put(str_id, datas);
			        				MainSingleTon.useridlist.add(str_id);
			        				
			        				SharedPreferences lifesharedpref=getSharedPreferences("LinkedinBoard", Context.MODE_PRIVATE);
			        				SharedPreferences.Editor editor=lifesharedpref.edit();
			        				editor.putString("userid", str_id);
			        				editor.commit();
			        				
			        				return true;
		        				}

		        				
		        			} catch (JSONException e){
		        			}
		                    break;
		                }
		                case 500: {
		                    // server problems 
		                	try {
								return false;
							} catch (Exception e) {
								e.printStackTrace();
							}
		                    break;
		                }
		                case 403: {
		                    // you have no authorization to access that resource
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
			if(pd1!=null && pd1.isShowing()){
	            pd1.dismiss();
	        }
			
			//Based on the result, Switch to Main Class
			if(result)
			{
				Toast.makeText(WelcomeActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
				
				Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
				startActivity(intent);
				
			}
			//If Error occurs, show a toast Message
			if(!result)
			{
				if(isalreadyadded)
				{
					Toast.makeText(WelcomeActivity.this, "Already added !!", Toast.LENGTH_SHORT).show();
					
				}else
				{
					Toast.makeText(WelcomeActivity.this, "Authorization Problem / Server Problem", Toast.LENGTH_SHORT).show();
				}
				
			}
			
		}
	}
}

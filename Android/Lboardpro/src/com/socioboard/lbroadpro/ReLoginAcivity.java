package com.socioboard.lbroadpro;

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
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.socioboard.lbroadpro.common.ApplicationData;
import com.socioboard.lbroadpro.database.util.LinkedInMultipleLocaldata;
import com.socioboard.lbroadpro.database.util.MainSingleTon;

public class ReLoginAcivity extends Activity{
	
	ImageView cancelbutton;
	WebView webView;
	private ProgressDialog pd;
	
	//This is the public API key of our application
	private static final String API_KEY = ApplicationData.CONSUMER_KEY;
		
	//This is the private API key of our application
	private static final String SECRET_KEY = ApplicationData.CONSUMER_SECRET;
		
	//This is any string we want to use. This will be used for avoid CSRF attacks. You can generate one here: http://strongpasswordgenerator.com/
	private static final String STATE = "E3ZYKC1T6H2yP4z";
		
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
	
	public static final String TAG_ID="id";
	
	LinkedInMultipleLocaldata db;
	
	static String accesstokenis;
	
	static Boolean isAnotheruser=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.relogin_activity);
		
		cancelbutton = (ImageView) findViewById(R.id.cancel_button1);
		webView = (WebView) findViewById(R.id.relogin_web_view1);
		
		db = new LinkedInMultipleLocaldata(getApplicationContext());
		
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

	            }else{
	                //Default behaviour
	                Log.i("Authorize","Redirecting to: "+authorizationUrl);
	                webView.loadUrl(authorizationUrl);
	            }
	            return true;
	        }
	    });

	    //Get the authorization Url
	    String authUrl = getAuthorizationUrl();
	    Log.i("Authorize","Loading Auth Url: "+authUrl);
	    //Load the authorization URL into the webView
	    webView.loadUrl(authUrl);

		db.getAllUsersData();
	}
	
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
	 *   ASyncTask for Fetching AccessToken/Expiry Date
	 */
	private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean>{

		String userId;
	    @Override
	    protected void onPreExecute(){
	        pd = ProgressDialog.show(ReLoginAcivity.this, "", ReLoginAcivity.this.getString(R.string.loading),true);
	    }

	    @Override
	    protected Boolean doInBackground(String... urls) {
	        if(urls.length>0){
	            String url = urls[0];            
	            System.out.println("user id = "+userId);
	            
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
	                        Log.e("Token", ""+accessToken);
	                        if(expiresIn>0 && accessToken!=null){
	                            Log.i("Authorize", "This is the access Token: "+accessToken+". "
	                            		+ "It will expires in "+expiresIn+" secs");

                            	accesstokenis = accessToken;	
                           
	                            //Calculate date of expiration
	                            Calendar calendar = Calendar.getInstance();
	                            calendar.add(Calendar.SECOND, expiresIn);
	                            long expireDate = calendar.getTimeInMillis();

	                            return true;
	                        }
	                    }
	                }
	            }catch(IOException e){
	                Log.e("Authorize","IO exception "+e.getLocalizedMessage());  
	            }
	            catch (ParseException e) {
	                Log.e("Authorize","Parse exception "+e.getLocalizedMessage());
	            } catch (JSONException e) {
	                Log.e("Authorize","JSON exception "+e.getLocalizedMessage());
	            }
	        }
	        return false;
	    }

	    @Override
	    protected void onPostExecute(Boolean status){
	       
	        if(status){
	        	// Call ASync task to get user profile details
	        	new GetProfileDetails().execute();
	        }
	    }

	};
	
	/**
	 * 
	 *   ASyncTask for Fetching 
	 *
	 */
	public class GetProfileDetails extends AsyncTask<String, Void, Boolean>
	{
		String stringResponse;
		
		@Override
		protected Boolean doInBackground(String... params) {
			 try {
		            HttpClient httpclient = new DefaultHttpClient();  // the http-client, that will send the request
		            
		            String uri = "https://api.linkedin.com/v1/people/~:(id)"
		            		+ "?oauth2_access_token="+accesstokenis+"&format=json";
		           
		            System.out.println("uri "+uri);
		            
		            HttpGet httpGet = new HttpGet(uri);
		           
					httpGet.addHeader("Connection", "Keep-Alive");
					httpGet.addHeader("X-Target-URI", "https://api.linkedin.com");
		            
		            HttpResponse response = httpclient.execute(httpGet); // the client executes the request and gets a response
		            
		            int responseCode = response.getStatusLine().getStatusCode();  // check the response code
		            
		            System.out.println("response code "+responseCode);
		            
		            switch (responseCode) {
		                case 200: { 
		                    // everything is fine, handle the response
		                    stringResponse = EntityUtils.toString(response.getEntity()); 
		                    
		                    try {
		        				
		                    	JSONObject json = new JSONObject(stringResponse);
		                    	String str_id = json.getString(TAG_ID);
		                    	
		                    	System.out.println("user id is"+MainSingleTon.userdetails.get(str_id));
		                    	
		                    	/**
		                    	 *  Checking for unauthorized access-token response and comparing with userID
		                    	 * 
		                    	 */
		                    	if(MainSingleTon.isUnauthorized&&MainSingleTon.userid==str_id)
		                    	{
		                    		isAnotheruser = false;
		                    		
		                    	}else
		                    	{
		                    		System.out.println("another user ");
		                    		isAnotheruser=true;
		                    	}
		                    	
		                    	return true;
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
		 if(pd!=null && pd.isShowing()){
	            pd.dismiss();
	        }
		 
		 if(result)
		 {
			 if(isAnotheruser)
			 {
				 
				 try {
					Toast.makeText(ReLoginAcivity.this, "You logged in as another User", Toast.LENGTH_SHORT).show();
				} catch (Exception e) { 
					e.printStackTrace();
				}
				
				ReLoginAcivity.this.finish();
			 }else
			 {
				 try {
					Toast.makeText(ReLoginAcivity.this, "Login Success", Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				MainSingleTon.accesstoken=accesstokenis;
         		MainSingleTon.isUnauthorized=false;
         		
				db.updateUserAccessToken(accesstokenis, MainSingleTon.userid);
				
				ReLoginAcivity.this.finish();
			 }
		 }
		 
		 if(!result)
		 {
				try {
					Toast.makeText(ReLoginAcivity.this, "Authorization Problem / Server Problem", Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			 
		}
	  }
	}

}

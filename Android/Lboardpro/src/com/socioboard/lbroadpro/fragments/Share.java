package com.socioboard.lbroadpro.fragments;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.socioboard.lbroadpro.R;
import com.socioboard.lbroadpro.database.util.MainSingleTon;
import com.socioboard.lbroadpro.models.CommentModel;

/**
 * Created by SB
 */
public class Share extends Fragment {

	EditText comment;
	private String commenttext;
	ImageView submit;
	ListView commentlistview;
	ArrayList<CommentModel> commentlist = new ArrayList<CommentModel>();
	
	public static final String TAG_UPDATEKEY="updateKey";
	public static final String TAG_UPDATEURL="updateUrl";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		
		View rootView = inflater.inflate(R.layout.fragment_share, container,false);
		comment = (EditText) rootView.findViewById(R.id.comment_text);
		submit = (ImageView) rootView.findViewById(R.id.submit);
		commentlistview = (ListView) rootView.findViewById(R.id.commentlist);
		
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				boolean check=false;
				check=validateinputs();
				if(check)
				{
					new SharePost().execute();
				}else
				{
					Toast.makeText(getActivity(),"Please Validate",Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		return rootView;
	}

	private boolean validateinputs()
	{
		boolean checked=false;
		boolean validComment=false;
		
		commenttext = comment.getText().toString();
		
		// Comment Validation
		if(commenttext.length()>0)
		{
			commenttext=comment.getText().toString();
			validComment=true;
		}else
		{ 
			Toast.makeText(getActivity(),"Enter something to share",Toast.LENGTH_SHORT).show();
		}
		
		// Validate Comment
		if(validComment)
		{
			checked = true;
		}else
		{
			checked=false;
		}
		
		return checked;
	}
	
	//ASync task to Share message on user's wall
	public class SharePost extends AsyncTask<Void, Void, Boolean>
	{
		String comment = commenttext;
		String stringResponse;
		JSONObject mainobject;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			try {
				//Form JSONObject body for message post
				mainobject = new JSONObject();
					mainobject.put("comment", comment);
				
				JSONObject visibility = new JSONObject();
					visibility.put("code", "anyone");
					mainobject.put("visibility", visibility);
				
				System.out.println("mainobject "+mainobject);
					
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			
			try {
				HttpClient httpclient = new DefaultHttpClient();  // the http-client, that will send the request
				HttpPost httppost = new HttpPost("https://api.linkedin.com/v1/people/~/shares?format=json&"
						+ "oauth2_access_token="+MainSingleTon.accesstoken);
			    StringEntity se = new StringEntity(mainobject.toString()); 
			    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			    httppost.setHeader("Content-Type", "application/json");
			    httppost.setHeader("x-li-format", "json");
			    httppost.setEntity(se);
	            HttpResponse response = httpclient.execute(httppost);
				
	            int responseCode = response.getStatusLine().getStatusCode();  // check the response code
	            System.out.println("response code"+String.valueOf(responseCode));
	            
	            switch (responseCode) {
                case 200: { 
                    // everything is fine, handle the response
                	try {
						return false;
					} catch (Exception e) {
						e.printStackTrace();
					}
                    break;
                }
                
                case 201:{
                	// everything is fine, handle the response
                    stringResponse = EntityUtils.toString(response.getEntity()); 
                   try {

                    	/*JSONObject json = new JSONObject(stringResponse);
                    	CommentModel listmodel = new CommentModel();
            			String str_updateUrl = json.getString(TAG_UPDATEURL);
            			listmodel.setComment(comment);
            			listmodel.setCommenturl(str_updateUrl);
            			commentlist.add(listmodel);
            			*/
            			return true;
            		} catch (Exception e){
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
                	System.out.println("Authorization problems");
                	try {
						return false;
					} catch (Exception e) {
						e.printStackTrace();
					}
                    break;
                }
            }
	            
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			if(result)
			{
				//CommentsAdapter adapter = new CommentsAdapter(getActivity(),commentlist);
				//commentlistview.setAdapter(adapter);
				Toast.makeText(getActivity(), "Comment Posted Successful", Toast.LENGTH_SHORT).show();
			}else
			{
				Toast.makeText(getActivity(), "Error in Response", Toast.LENGTH_SHORT).show();
			}
		}
	}
}

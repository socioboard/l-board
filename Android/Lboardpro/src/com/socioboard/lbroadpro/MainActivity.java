package com.socioboard.lbroadpro;

import java.io.IOException;
import java.util.ArrayList;
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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.socioboard.lbroadpro.adapter.AccountAdapter;
import com.socioboard.lbroadpro.adapter.DrawerAdapter;
import com.socioboard.lbroadpro.common.ApplicationData;
import com.socioboard.lbroadpro.common.CommonUtilss;
import com.socioboard.lbroadpro.database.util.LinkedinManyLocalData;
import com.socioboard.lbroadpro.database.util.MainSingleTon;
import com.socioboard.lbroadpro.database.util.ModelUserDatas;
import com.socioboard.lbroadpro.dialog.Multi_Dialog;
import com.socioboard.lbroadpro.dialog.Radio_Dialog;
import com.socioboard.lbroadpro.dialog.Single_Dialog;
import com.socioboard.lbroadpro.dialog.Standard_Dialog;
import com.socioboard.lbroadpro.fragments.Company_Profile;
import com.socioboard.lbroadpro.fragments.Company_Updates;
import com.socioboard.lbroadpro.fragments.Share;
import com.socioboard.lbroadpro.fragments.User_Profile;
import com.socioboard.lbroadpro.ui.Items;
import com.socioboard.lbroadpro.ui.MultiSwipeRefreshLayout;

public class MainActivity extends ActionBarActivity implements
		MultiSwipeRefreshLayout.CanChildScrollUpCallback {
	
	public static Menu menuitem;
	private String[] mDrawerTitles;

	private ArrayList<String> acountNameArray = new ArrayList<String>();
	private ArrayList<ModelUserDatas> accountList;
	private TypedArray mDrawerIcons;
	private ArrayList<Items> drawerItems;
	private DrawerLayout mDrawerLayout;

	private ListView mDrawerList_Left, mDrawerList_Right;

	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	
	public static FragmentManager fragmentManager;

	private static FragmentManager mManager;
	
	private AccountAdapter accountAdapter;

	// SwipeRefreshLayout allows the user to swipe the screen down to trigger a
	// manual refresh
	private MultiSwipeRefreshLayout mSwipeRefreshLayout;

	TextView current_user_name,current_user_headline;
	ImageView curret_user_profilepic;

	RelativeLayout addacount_view, settings_view, feedback_view;

	CommonUtilss utills;
	LinkedinManyLocalData db;
	
	CommonUtilss utilss;
	
	Dialog dialog;
	private ProgressDialog pd;
	Boolean isalreadyadded = false;
	
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

	public static final String TAG_FIRSTNAME="firstName";
	public static final String TAG_LASTNAME="lastName";
	public static final String TAG_EMAILADDRESS="emailAddress";
	public static final String TAG_PICTUREURL="pictureUrl";
	public static final String TAG_ID="id";
	public static final String TAG_HEADLINE="headline";
	public static final String TAG__TOTAL="_total";
	
	public static String accesstokenis = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null)
			setSupportActionBar(toolbar);

		utilss = new CommonUtilss();
		
		mManager = getSupportFragmentManager();
		fragmentManager= getSupportFragmentManager();
		fragmentManager.addOnBackStackChangedListener(new OnBackStackChangedListener() {
		        @Override
		        public void onBackStackChanged() {
		            if(getFragmentManager().getBackStackEntryCount() == 0) finish();
		        }
		    });
		
		accountList = new ArrayList<ModelUserDatas>();

		// mDrawerTitles = getResources().getStringArray(R.array.drawer_titles);
		mDrawerIcons = getResources().obtainTypedArray(R.array.drawer_icons);
		drawerItems = new ArrayList<Items>();
		mDrawerList_Left = (ListView) findViewById(R.id.left_drawer);
		mDrawerTitles = getResources().getStringArray(R.array.drawer_titles);
		mDrawerList_Right = (ListView) findViewById(R.id.right_drawer);

		utills = new CommonUtilss();
		db = new LinkedinManyLocalData(getApplicationContext());

		acountNameArray.add("User Profile");
		acountNameArray.add("Company Profiles");//+Company followers
		acountNameArray.add("Company Updates");
		acountNameArray.add("Share");

		accountList.clear();
		
			for (int i = 0; i < MainSingleTon.useridlist.size(); i++) {
				ModelUserDatas model = MainSingleTon.userdetails
						.get(MainSingleTon.useridlist.get(i));

				model.setUserid(model.getUserid());
				model.setUsername(model.getUsername());
				model.setUserimage(model.getUserimage());
				model.setUserAcessToken(model.getUserAcessToken());
				
				model.setLastname(model.getLastname());
				model.setUseremailid(model.getUseremailid());
				model.setUserheadline(model.getUserheadline());
				
				accountList.add(model);
			}
	
		for (int i = 0; i < mDrawerTitles.length; i++) 
		  {
		   drawerItems.add(new Items(mDrawerTitles[i], mDrawerIcons.getResourceId(i, -(i + 1))));
		  }
		
		mTitle = mDrawerTitle = getTitle();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		toolbar, /* nav drawer icon to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */
		) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view); 
				getSupportActionBar().setTitle(mTitle);
				menuitem.findItem(R.id.action_settings).setVisible(true);
				// invalidateOptionsMenu();

			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle(mDrawerTitle);
				// invalidateOptionsMenu();

			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		LayoutInflater inflater = getLayoutInflater();

		final ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.footer,mDrawerList_Left, false);

		final ViewGroup headerR = (ViewGroup) inflater.inflate(R.layout.header,mDrawerList_Right, false);

		final ViewGroup footerR = (ViewGroup) inflater.inflate(R.layout.footer,mDrawerList_Right, false);

		current_user_headline = (TextView) headerR.findViewById(R.id.currentname);
		//curret_user_username = (TextView) headerR.findViewById(R.id.currentusername);
		current_user_name = (TextView) headerR.findViewById(R.id.currentheaderline);
		curret_user_profilepic = (ImageView) headerR.findViewById(R.id.current_profile_pic);

		addacount_view = (RelativeLayout) footerR.findViewById(R.id.add_account);
		settings_view = (RelativeLayout) footerR.findViewById(R.id.settings);
		feedback_view = (RelativeLayout) footerR.findViewById(R.id.feedback);

		//curret_user_username.setText(MainSingleTon.useremailid);
		current_user_name.setText(MainSingleTon.username);
		current_user_headline.setText(MainSingleTon.userheadline);
		curret_user_profilepic.setImageBitmap(utills.getBitmapFromString(MainSingleTon.userimage));

		curret_user_profilepic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			
			}
		});

		addacount_view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showCustomDialog();		
			}
		});
		
		feedback_view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
			            "mailto","sumit@socioboard.com", null));
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for inBoardpro");
			startActivity(Intent.createChooser(emailIntent, "Send email..."));
				
			}
		});

		// mDrawerList_Left.addHeaderView(header, null, true); // true =
		// clickable
		//mDrawerList_Left.addFooterView(footer, null, true); // true = clickable

		mDrawerList_Right.addHeaderView(headerR, null, true); // true =
																// clickable
		mDrawerList_Right.addFooterView(footerR, null, true); // true =
																// clickable

		// Set width of drawer
		DrawerLayout.LayoutParams lp = (DrawerLayout.LayoutParams) mDrawerList_Left
				.getLayoutParams();
		lp.width = calculateDrawerWidth();
		mDrawerList_Left.setLayoutParams(lp);

		// Set width of drawer
		DrawerLayout.LayoutParams lpR = (DrawerLayout.LayoutParams) mDrawerList_Right
				.getLayoutParams();
		lpR.width = calculateDrawerWidth();
		mDrawerList_Right.setLayoutParams(lpR);

		// Set the adapter for the list view
		mDrawerList_Left.setAdapter(new DrawerAdapter(getApplicationContext(),
				drawerItems));
		// Set the list's click listener
		mDrawerList_Left.setOnItemClickListener(new DrawerItemClickListener());

		// Set the adapter for the list view
		mDrawerList_Right.setAdapter(new AccountAdapter(
				MainActivity.this, accountList));
		// Set the list's click listener
		mDrawerList_Right.setOnItemClickListener(new RightDrawerItemClickListener());

	}
	
	protected void showCustomDialog() {
		dialog = new Dialog(MainActivity.this,
				android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(true);
		dialog.setContentView(R.layout.webview_dialog);
		
		final WebView webView = (WebView) dialog.findViewById(R.id.relogin_web_view);

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
			
		dialog.show();
		
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

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	 /**
	  * Swaps fragments in the main content view
	  */
	 private void selectItem(int position)
	 {
	  Fragment fragment = null;

	  switch (position)
	  {
	  case 0:
		  fragment = new User_Profile();
		  break;
		  
	  case 1:
	   fragment = new Company_Profile();
	   break;
	   
	  case 2:
	  
	   fragment = new Company_Updates();
	   break;
	  case 3:
	 
	   fragment = new Share();
	   break;
	
	  }

	  if (fragment != null)
	  {
	   // Insert the fragment by replacing any existing fragment
	   FragmentManager fragmentManager = getSupportFragmentManager();
	   fragmentManager.beginTransaction().replace(R.id.main_content, fragment)
	   .commit();
	  }

	  // Highlight the selected item, update the title, and close the drawer
	  if(mDrawerList_Left.isEnabled())
	  {
	   mDrawerList_Left.setItemChecked(position, true);
	   if (position != 0)
	   {
	    setTitle(mDrawerTitles[position - 1]);
	    updateView(position, position, true,mDrawerList_Left);
	   }
	   mDrawerLayout.closeDrawer(mDrawerList_Left);
	  }
	  else
	  {
	   mDrawerList_Right.setItemChecked(position, true);
	   if (position != 0)
	   {
	    setTitle(mDrawerTitles[position - 1]);
	    updateView(position, position, true,mDrawerList_Right);
	   }
	   mDrawerLayout.closeDrawer(mDrawerList_Right);
	  }

	 }
	 
	 /**
	  *  OnBackpress load the previous fragment
	  * 
	  */
	 @Override
	public void onBackPressed() {
		 
			if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
			}
			else
			if(fragmentManager.getBackStackEntryCount()>1)
			{
				fragmentManager.popBackStack();
			}else
			{
				super.onBackPressed();
			}	
		}

	private class RightDrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			selectItemRight(position);
		}
	}

	private void selectItemRight(final int position) {

		if (position>0) {
		
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					
					ModelUserDatas model = MainSingleTon.userdetails.get(accountList.get(position-1).getUserid());
					MainSingleTon.userid=model.getUserid();
					MainSingleTon.username=model.getUsername();
					MainSingleTon.userimage=model.getUserimage();
					MainSingleTon.accesstoken=model.getUserAcessToken();
					MainSingleTon.userlastname=model.getLastname();
					MainSingleTon.useremailid=model.getUseremailid();
					MainSingleTon.userheadline=model.getUserheadline();
					
					MainSingleTon.userdetails.put(MainSingleTon.userid, model);
					MainSingleTon.useridlist.add(MainSingleTon.userid);
					
					SharedPreferences lifesharedpref=getSharedPreferences("LinkedinBoard", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor=lifesharedpref.edit();
					editor.putString("userid", MainSingleTon.userid);
					editor.commit();
					
					current_user_name.setText(MainSingleTon.username);
					current_user_headline.setText(MainSingleTon.userheadline);
					curret_user_profilepic.setImageBitmap(utills
							.getBitmapFromString(MainSingleTon.userimage));
				}
			});
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);

		return true;
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menuitem = menu;
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList_Left);

		boolean drawerOpenR = mDrawerLayout.isDrawerOpen(mDrawerList_Right);

		if (mDrawerLayout.isDrawerOpen(mDrawerList_Right)) {
			mDrawerLayout.closeDrawer(mDrawerList_Right);
			menuitem.findItem(R.id.action_settings).setVisible(true);
		} else {
			menuitem.findItem(R.id.action_settings).setVisible(false);
			mDrawerLayout.openDrawer(mDrawerList_Right);

		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;

		}
		if (mDrawerLayout.isDrawerOpen(mDrawerList_Right)) {
			mDrawerLayout.closeDrawer(mDrawerList_Right);
		} else {
			mDrawerLayout.openDrawer(mDrawerList_Right);
		}
		return super.onOptionsItemSelected(item);
	}

	public int calculateDrawerWidth() {
		// Calculate ActionBar height
		TypedValue tv = new TypedValue();
		int actionBarHeight = 0;
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
					getResources().getDisplayMetrics());
		}

		Display display = getWindowManager().getDefaultDisplay();
		int width;
		int height;
		if (android.os.Build.VERSION.SDK_INT >= 13) {
			Point size = new Point();
			display.getSize(size);
			width = size.x;
			height = size.y;
		} else {
			width = display.getWidth(); // deprecated
			height = display.getHeight(); // deprecated
		}
		return width - actionBarHeight;
	}

	private void updateView(int position, int counter, boolean visible,
			ListView mDrawerList) {

		View v = mDrawerList.getChildAt(position
				- mDrawerList.getFirstVisiblePosition());
		TextView someText = (TextView) v.findViewById(R.id.item_new);
		Resources res = getResources();
		String articlesFound = "";

		switch (position) {
		case 1:
			articlesFound = res.getQuantityString(
					R.plurals.numberOfNewArticles, counter, counter);
			someText.setBackgroundResource(R.drawable.new_apps);
			break;
		case 2:
			articlesFound = res.getQuantityString(
					R.plurals.numberOfNewArticles, counter, counter);
			someText.setBackgroundResource(R.drawable.new_sales);
			break;
		case 3:
			articlesFound = res.getQuantityString(
					R.plurals.numberOfNewArticles, counter, counter);
			someText.setBackgroundResource(R.drawable.new_blog);
			break;
		case 4:
			articlesFound = res.getQuantityString(
					R.plurals.numberOfNewArticles, counter, counter);
			someText.setBackgroundResource(R.drawable.new_bookmark);
			break;
		case 5:
			articlesFound = res.getQuantityString(
					R.plurals.numberOfNewArticles, counter, counter);
			someText.setBackgroundResource(R.drawable.new_community);
			break;
		}

		someText.setText(articlesFound);
		if (visible)
			someText.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean canSwipeRefreshChildScrollUp() {
		return false;
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public void circleIn(View view) {

		// get the center for the clipping circle
		int cx = (view.getLeft() + view.getRight()) / 2;
		int cy = (view.getTop() + view.getBottom()) / 2;

		// get the final radius for the clipping circle
		int finalRadius = Math.max(view.getWidth(), view.getHeight());

		// create the animator for this view (the start radius is zero)
		Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy,
				0, finalRadius);

		// make the view visible and start the animation
		view.setVisibility(View.VISIBLE);
		anim.start();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public void circleOut(final View view) {

		// get the center for the clipping circle
		int cx = (view.getLeft() + view.getRight()) / 2;
		int cy = (view.getTop() + view.getBottom()) / 2;

		// get the initial radius for the clipping circle
		int initialRadius = view.getWidth();

		// create the animation (the final radius is zero)
		Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy,
				initialRadius, 0);

		// make the view invisible when the animation is done
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				view.setVisibility(View.INVISIBLE);
			}
		});

		// start the animation
		anim.start();
	}

	/**
	 * Sets the components of the standard dialog.
	 *
	 * @param title
	 *            Title of the dialog
	 * @param message
	 *            Message of the dialog
	 * @param negativeButton
	 *            Text of negative Button
	 * @param positiveButton
	 *            Text of positive Button
	 */
	public static void showMyDialog(String title, String message,
			String negativeButton, String positiveButton) {
		Standard_Dialog newDialog = Standard_Dialog.newInstance(title, message,
				negativeButton, positiveButton);
		newDialog.show(mManager, "dialog");
	}

	/**
	 * Sets the components of the traditional single-choice dialog.
	 *
	 * @param title
	 *            Title of the dialog
	 * @param dialogItems
	 *            Content of the dialog
	 * @param negativeButton
	 *            Text of negative Button
	 * @param positiveButton
	 *            Text of positive Button
	 */
	public static void showMySingleDialog(String title,
			ArrayList<String> dialogItems, String negativeButton,
			String positiveButton) {
		Single_Dialog newDialog = Single_Dialog.newInstance(title, dialogItems,
				negativeButton, positiveButton);
		newDialog.show(mManager, "dialog");
	}

	/**
	 * Sets the components of the persistent single-choice dialog.
	 *
	 * @param title
	 *            Title of the dialog
	 * @param dialogItems
	 *            Content of the dialog
	 * @param negativeButton
	 *            Text of negative Button
	 * @param positiveButton
	 *            Text of positive Button
	 */
	public static void showMyRadioDialog(String title,
			ArrayList<String> dialogItems, String negativeButton,
			String positiveButton) {
		Radio_Dialog newDialog = Radio_Dialog.newInstance(title, dialogItems,
				negativeButton, positiveButton);
		newDialog.show(mManager, "dialog");
	}

	/**
	 * Sets the components of the persistent multiple-choice dialog.
	 *
	 * @param title
	 *            Title of the dialog
	 * @param dialogItems
	 *            Content of the dialog
	 * @param negativeButton
	 *            Text of negative Button
	 * @param positiveButton
	 *            Text of positive Button
	 */
	public static void showMyMultiDialog(String title,
			ArrayList<String> dialogItems, String negativeButton,
			String positiveButton) {
		Multi_Dialog newDialog = Multi_Dialog.newInstance(title, dialogItems,
				negativeButton, positiveButton);
		newDialog.show(mManager, "dialog");
	}

	// Method to swipe fragment after click in main container
	public static void swipeFragment(Fragment fragment)
	{ 
		fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
	}
	
	/**
	 *   ASyncTask for Fetching AccessToken/Expiry Date
	 */
	private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean>{

	    @Override
	    protected void onPreExecute(){
	        pd = ProgressDialog.show(MainActivity.this, "", MainActivity.this.getString(R.string.loading),true);
	    }

	    @Override
	    protected Boolean doInBackground(String... urls) {
	        if(urls.length>0){
	            String url = urls[0];
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
	                            Log.i("Authorize", "This is the access Token: "+accessToken+". It will expires in "+expiresIn+" secs");

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
	public class GetProfileDetails extends AsyncTask<Void, Void, Boolean>
	{
		String stringResponse;
		String str_pictureUrl;
		String imageString;
		 
		@Override
		protected Boolean doInBackground(Void... params) {
			
			 try {
		            HttpClient httpclient = new DefaultHttpClient();  // the http-client, that will send the request
		            
		            String uri = "https://api.linkedin.com/v1/people/~:(id,first-name,headline,last-name,"
		            		+ "picture-url,current-status,summary,main-address,date-of-birth,email-address)"
		            		+ "?oauth2_access_token="+MainActivity.accesstokenis+"&format=json";
		           
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
		        				String picUrl = json.getString(TAG_PICTUREURL);
		        				String str_headline = json.getString(TAG_HEADLINE);
		        				String str_pictureUrl = json.getString(TAG_PICTUREURL);
		        				String str_id = json.getString(TAG_ID);
		        				
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
		        				datas.setUserimage(imageString);
		        				datas.setLastname(str_lastName);
		        				datas.setUseremailid(str_emailAddress);
		        				datas.setUserheadline(str_headline);
		        				db.addNewUserAccount(datas);
		        				
		        				accountList.add(datas);
		       
		        				MainSingleTon.userdetails.put(str_id, datas);
		        				MainSingleTon.useridlist.add(str_id);
		        				
		        				SharedPreferences lifesharedpref=getSharedPreferences("LinkedinBoard", Context.MODE_PRIVATE);
		        				SharedPreferences.Editor editor=lifesharedpref.edit();
		        				editor.putString("userid", str_id);
		        				editor.commit();
		        				}
		        			} catch (JSONException e){
		        			}
		                    
		                   
		                    break;
		                    
		                }
		                case 500: {
		                    // server problems ?
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
			 Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT)
				.show();
			
			 // Set the adapter for the list view
			accountAdapter=new AccountAdapter(MainActivity.this, accountList);
			mDrawerList_Right.setAdapter(accountAdapter);
			dialog.dismiss();
	
		 }
		 
		 if(!result)
		 {
				if(isalreadyadded)
				{
					Toast.makeText(MainActivity.this, "Already added !!", Toast.LENGTH_SHORT).show();
				}else
				{
					Toast.makeText(MainActivity.this, "Authorization Problem / Server Problem", Toast.LENGTH_SHORT).show();
				}
				
			}
		}
	}
	
	// Notify Adapter so data has changed and use has changed
	public void notifyadapter(){
		
		accountList.clear();

		for (int i = 0; i < MainSingleTon.useridlist.size(); i++) {
			ModelUserDatas model = MainSingleTon.userdetails
					.get(MainSingleTon.useridlist.get(i));

			model.setUserid(model.getUserid());
			model.setUserAcessToken(model.getUserAcessToken());
			model.setUserimage(model.getUserimage());
			model.setUsername(model.getUsername());
			accountList.add(model);
		}
		accountAdapter.notifyDataSetChanged();
	}
	
}

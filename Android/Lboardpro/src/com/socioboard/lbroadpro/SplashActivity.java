package com.socioboard.lbroadpro;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.view.Window;

import com.socioboard.lbroadpro.database.util.LinkedInMultipleLocaldata;
import com.socioboard.lbroadpro.database.util.MainSingleTon;
import com.socioboard.lbroadpro.database.util.ModelUserDatas;

public class SplashActivity extends Activity {

	/*
	 * check user already have stored token in local db or not , 
	 * if there then redirect to feed fragment(Main activity)
	 * or redirect to login screen
	 * 
	 * 
	 */
	
	LinkedInMultipleLocaldata LinkedInmultipleLocalData;
	SharedPreferences preferences;
	String keyhash;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		// Storing KeyHash in local variable
		keyhash = getKeyHash(getApplicationContext(),"com.socioboard.lbroadpro");
			
		// Creating local variable for Database
		LinkedInmultipleLocalData = new LinkedInMultipleLocaldata(getApplicationContext());
		LinkedInmultipleLocalData.CreateTable();
		LinkedInmultipleLocalData.getAllUsersData();
		
		// Thread for initiating Splash Screen
		Thread background = new Thread() {
			public void run() {

				try {
					// Sleep Time for Thread
					sleep(1 * 2500);

					// Adding user for the First Time
					if (MainSingleTon.userdetails.size() == 0) 
					{
						Intent intent = new Intent(SplashActivity.this,WelcomeActivity.class);
						startActivity(intent);
						finish();
					} 
					
					// If user is already added and login
					else
					{
						SharedPreferences lifesharedpref=getSharedPreferences("LinkedinBoard", Context.MODE_PRIVATE);
						MainSingleTon.userid= lifesharedpref.getString("userid", null);
						
						if(MainSingleTon.userid!=null)
						{
							ModelUserDatas model=MainSingleTon.userdetails.get(MainSingleTon.userid);
							MainSingleTon.username=model.getUsername();
							MainSingleTon.userimage=model.getUserimage();
							MainSingleTon.accesstoken=model.getUserAcessToken();
							
							MainSingleTon.userlastname=model.getLastname();
							MainSingleTon.useremailid=model.getUseremailid();
							MainSingleTon.userheadline=model.getUserheadline();
							
							Intent in=new Intent(SplashActivity.this,MainActivity.class);
							startActivity(in);
							SplashActivity.this.finish();
							
						}else
						{
							Map.Entry<String,ModelUserDatas> entry=MainSingleTon.userdetails.entrySet().iterator().next();
							
							MainSingleTon.userid = entry.getKey();
							
							ModelUserDatas value=entry.getValue();
							
							MainSingleTon.username=value.getUsername();
							MainSingleTon.userimage=value.getUserimage();
							MainSingleTon.accesstoken=value.getUserAcessToken();
							
							MainSingleTon.userlastname=value.getLastname();
							MainSingleTon.useremailid=value.getUseremailid();
							MainSingleTon.userheadline=value.getUserheadline();
								
							Intent in=new Intent(SplashActivity.this,MainActivity.class);
							startActivity(in);
							SplashActivity.this.finish();
						}
					}

				} catch (Exception e) {

				}
			}
		};

		background.start();
	}
	
	// method name: getKeyHash (Generate KeyHash from SHA algorithm)
	public static String getKeyHash(Context context, String packageName) {
	    try {
	        PackageInfo info = context.getPackageManager().getPackageInfo(
	                packageName,
	                PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            String keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
	            return keyHash;
	        }
	    } catch (PackageManager.NameNotFoundException e) {
	        return null;
	    } catch (NoSuchAlgorithmException e) {
	        return null;
	    }
	    return null;
	}

}

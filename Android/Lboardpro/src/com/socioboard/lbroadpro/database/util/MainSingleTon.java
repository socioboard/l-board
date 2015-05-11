package com.socioboard.lbroadpro.database.util;

import java.util.ArrayList;
import java.util.HashMap;

public class MainSingleTon {

	public static boolean signedInStatus = false;
	public static String userid;
	public static String username;
	public static String userlastname;
	public static String useremailid;
	public static String userheadline;
	public static ArrayList<String> useridlist=new ArrayList<String>();
	public static String accesstoken;
	public static String expiredaccesstoken;
	public static String userimage;
	
	public static Boolean isUnauthorized=false;
	
	public static HashMap<String, ModelUserDatas> userdetails=new HashMap<String, ModelUserDatas>();
	
}

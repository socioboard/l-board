package com.socioboard.lbroadpro.common;

public class ConstantUrl {

	public static String URL_Follows="https://api.instagram.com/v1/users/self/follows/?access_token=";
	public static String URL_FollowedBy="https://api.instagram.com/v1/users/self/followed-by/?access_token=";
	public static String URL_Media="https://api.instagram.com/v1/users/self/media/recent/?access_token=";
	
	
	public static String Get_UserProfile_URL = "https://api.linkedin.com/v1/people/~:(id,first-name,headline,last-name,"
			+ "industry,site-standard-profile-request,api-standard-profile-request,member-url-resources,picture-url,"
			+ "current-status,summary,positions,main-address,location,distance,specialties,proposal-comments,"
			+ "associations,honors,interests,educations,phone-numbers,im-accounts,twitter-accounts,date-of-birth,"
			+ "email-address)?oauth2_access_token=";

	public static String Get_JobSearchTitle_URL = "https://api.linkedin.com/v1/job-search?job-title=";
	public static String Get_JobSearchKeyword_URL = "https://api.linkedin.com/v1/job-search?keywords=";

}

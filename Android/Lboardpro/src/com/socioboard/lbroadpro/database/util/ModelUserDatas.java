package com.socioboard.lbroadpro.database.util;

public class ModelUserDatas {

	String userid;
	String username;
	String useremailid;
	String lastname;
	String userAccessToken;
	String userimage;
	String userheadline;
	
	public String getUseremailid() {
		return useremailid;
	}

	public void setUseremailid(String useremailid) {
		this.useremailid = useremailid;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getUserAccessToken() {
		return userAccessToken;
	}

	public void setUserAccessToken(String userAccessToken) {
		this.userAccessToken = userAccessToken;
	}

	public String getUserheadline() {
		return userheadline;
	}

	public void setUserheadline(String userheadline) {
		this.userheadline = userheadline;
	}

	public String getUserimage() {
		return userimage;
	}

	public void setUserimage(String userimage) {
		this.userimage = userimage;
	}

	public String getUserAcessToken() {
		return userAccessToken;
	}

	public void setUserAcessToken(String userAcessToken) {
		this.userAccessToken = userAcessToken;
	}

	@Override
	public String toString() {
		return "\nModelUserDatas [userid=" + userid + ", username=" + username
				+ ", userAcessToken=" + userAccessToken + "]";
	}

	public ModelUserDatas() {
	}

	/**
	 * @return the userid
	 */
	public String getUserid() {
		return userid;
	}

	/**
	 * @param userid
	 *            the userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the level
	 */

}

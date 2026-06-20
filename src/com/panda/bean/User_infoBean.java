package com.panda.bean;

import java.util.LinkedHashMap;

public class User_infoBean {

	private String user_id;
	private String license_yyyymmdd;
	private String permissions;
	private String group_id;
	private LinkedHashMap<String, User_infoBean> group_id_user_id;
	private String pw;
	private String license_url;
	private String yaoqing_no;
	private String email;



	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getLicense_yyyymmdd() {
		return license_yyyymmdd;
	}
	public void setLicense_yyyymmdd(String license_yyyymmdd) {
		this.license_yyyymmdd = license_yyyymmdd;
	}
	public String getPermissions() {
		return permissions;
	}
	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public LinkedHashMap<String, User_infoBean> getGroup_id_user_id() {
		return group_id_user_id;
	}
	public void setGroup_id_user_id(LinkedHashMap<String, User_infoBean> group_id_user_id) {
		this.group_id_user_id = group_id_user_id;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public String getLicense_url() {
		return license_url;
	}
	public void setLicense_url(String license_url) {
		this.license_url = license_url;
	}
	public String getYaoqing_no() {
		return yaoqing_no;
	}
	public void setYaoqing_no(String yaoqing_no) {
		this.yaoqing_no = yaoqing_no;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}


}
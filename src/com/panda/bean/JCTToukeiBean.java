package com.panda.bean;

public class JCTToukeiBean {

	private String UPDATE_DATE;
	private String dataFileName;
	private String sakuseiDATE;

	private int add_count = 0;
	private int update_count = 0;
	private int del_count = 0;

	private int riben_gongsi = 0;
	private int waiguo_gongsi_banshichu_you = 0;
	private int waiguo_gongsi_banshichu_wu = 0;

	private int riben_gere = 0;
	private int waiguo_gere_banshichu_you = 0;
	private int waiguo_gere_banshichu_wu = 0;

	private String waiguo_gongsi_banshichu_wu_list;
	private String waiguo_gongsi_banshichu_wu_list2;

	private String bikou;
	private String bikou2;

	private String bikouRedcount;

	public String getUPDATE_DATE() {
		return UPDATE_DATE;
	}

	public void setUPDATE_DATE(String uPDATE_DATE) {
		UPDATE_DATE = uPDATE_DATE;
	}

	public String getDataFileName() {
		return dataFileName;
	}

	public void setDataFileName(String dataFileName) {
		this.dataFileName = dataFileName;
	}

	public String getSakuseiDATE() {
		return sakuseiDATE;
	}

	public void setSakuseiDATE(String sakuseiDATE) {
		this.sakuseiDATE = sakuseiDATE;
	}

	public int getAdd_count() {
		return add_count;
	}

	public void setAdd_count(int add_count) {
		this.add_count = add_count;
	}

	public int getUpdate_count() {
		return update_count;
	}

	public void setUpdate_count(int update_count) {
		this.update_count = update_count;
	}

	public int getDel_count() {
		return del_count;
	}

	public void setDel_count(int del_count) {
		this.del_count = del_count;
	}

	public int getRiben_gongsi() {
		return riben_gongsi;
	}

	public void setRiben_gongsi(int riben_gongsi) {
		this.riben_gongsi = riben_gongsi;
	}

	public int getWaiguo_gongsi_banshichu_you() {
		return waiguo_gongsi_banshichu_you;
	}

	public void setWaiguo_gongsi_banshichu_you(int waiguo_gongsi_banshichu_you) {
		this.waiguo_gongsi_banshichu_you = waiguo_gongsi_banshichu_you;
	}

	public int getWaiguo_gongsi_banshichu_wu() {
		return waiguo_gongsi_banshichu_wu;
	}

	public void setWaiguo_gongsi_banshichu_wu(int waiguo_gongsi_banshichu_wu) {
		this.waiguo_gongsi_banshichu_wu = waiguo_gongsi_banshichu_wu;
	}

	public int getRiben_gere() {
		return riben_gere;
	}

	public void setRiben_gere(int riben_gere) {
		this.riben_gere = riben_gere;
	}

	public int getWaiguo_gere_banshichu_you() {
		return waiguo_gere_banshichu_you;
	}

	public void setWaiguo_gere_banshichu_you(int waiguo_gere_banshichu_you) {
		this.waiguo_gere_banshichu_you = waiguo_gere_banshichu_you;
	}

	public int getWaiguo_gere_banshichu_wu() {
		return waiguo_gere_banshichu_wu;
	}

	public void setWaiguo_gere_banshichu_wu(int waiguo_gere_banshichu_wu) {
		this.waiguo_gere_banshichu_wu = waiguo_gere_banshichu_wu;
	}

	public String getWaiguo_gongsi_banshichu_wu_list() {
		return waiguo_gongsi_banshichu_wu_list;
	}

	public void setWaiguo_gongsi_banshichu_wu_list(String waiguo_gongsi_banshichu_wu_list) {
		this.waiguo_gongsi_banshichu_wu_list = waiguo_gongsi_banshichu_wu_list;
	}

	public String getBikou() {
		return bikou;
	}

	public void setBikou(String bikou) {
		this.bikou = bikou;
	}

	public String getBikou2() {
		return bikou2;
	}

	public void setBikou2(String bikou2) {
		this.bikou2 = bikou2;
	}

	public String getWaiguo_gongsi_banshichu_wu_list2() {
		return waiguo_gongsi_banshichu_wu_list2;
	}

	public void setWaiguo_gongsi_banshichu_wu_list2(String waiguo_gongsi_banshichu_wu_list2) {
		this.waiguo_gongsi_banshichu_wu_list2 = waiguo_gongsi_banshichu_wu_list2;
	}

	public String getBikouRedcount() {
		return bikouRedcount;
	}

	public void setBikouRedcount(String bikouRedcount) {
		this.bikouRedcount = bikouRedcount;
	}
	}
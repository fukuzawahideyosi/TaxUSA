package com.panda.bean;

public class t_freeeBean {
/*
CREATE TABLE t_freee (
  `UPDATE_DATE` timestamp(6) NOT NULL COMMENT '更新时间',
    kouza_mei VARCHAR(255) COMMENT '口座名',
    torihiki_bi DATE COMMENT '取引日',
    torihiki_naiyou VARCHAR(255) COMMENT '取引内容',
    nyuukin_gaku DECIMAL(15,2) COMMENT '入金額',
    shukkin_gaku DECIMAL(15,2) COMMENT '出金額',
    zandaka DECIMAL(15,2) COMMENT '残高',
    joukyou VARCHAR(50) COMMENT '状態',
    koushin_bi DATE COMMENT '更新日',
    shutoku_bi DATE COMMENT '取得日'
);
 */

	private String 		UPDATE_DATE			 = "";
	private String 		kouza_mei			 = "";
	private String 		torihiki_bi			 = "";
	private String 		torihiki_naiyou		 = "";
	private String 		nyuukin_gaku		 = "";
	private String 		shukkin_gaku		 = "";
	private String 		zandaka				 = "";
	private String 		joukyou				 = "";
	private String 		koushin_bi			 = "";
	private String 		shutoku_bi			 = "";



	public String getUPDATE_DATE() {
		return UPDATE_DATE;
	}
	public void setUPDATE_DATE(String uPDATE_DATE) {
		UPDATE_DATE = uPDATE_DATE;
	}
	public String getKouza_mei() {
		return kouza_mei;
	}
	public void setKouza_mei(String kouza_mei) {
		this.kouza_mei = kouza_mei;
	}
	public String getTorihiki_bi() {
		return torihiki_bi;
	}
	public void setTorihiki_bi(String torihiki_bi) {
		this.torihiki_bi = torihiki_bi;
	}
	public String getTorihiki_naiyou() {
		return torihiki_naiyou;
	}
	public void setTorihiki_naiyou(String torihiki_naiyou) {
		this.torihiki_naiyou = torihiki_naiyou;
	}
	public String getNyuukin_gaku() {
		return nyuukin_gaku;
	}
	public void setNyuukin_gaku(String nyuukin_gaku) {
		this.nyuukin_gaku = nyuukin_gaku;
	}
	public String getShukkin_gaku() {
		return shukkin_gaku;
	}
	public void setShukkin_gaku(String shukkin_gaku) {
		this.shukkin_gaku = shukkin_gaku;
	}
	public String getZandaka() {
		return zandaka;
	}
	public void setZandaka(String zandaka) {
		this.zandaka = zandaka;
	}
	public String getJoukyou() {
		return joukyou;
	}
	public void setJoukyou(String joukyou) {
		this.joukyou = joukyou;
	}
	public String getKoushin_bi() {
		return koushin_bi;
	}
	public void setKoushin_bi(String koushin_bi) {
		this.koushin_bi = koushin_bi;
	}
	public String getShutoku_bi() {
		return shutoku_bi;
	}
	public void setShutoku_bi(String shutoku_bi) {
		this.shutoku_bi = shutoku_bi;
	}



}
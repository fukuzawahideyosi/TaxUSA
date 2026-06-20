package com.panda.bean;

public class t_etax_jieguoExExBean extends t_etax_account_infoExBean {



	private String UPDATE_DATE = "";
	private String yyyymmdd_count = "";
	private String yyyy = "";
	private String file_name = "";
	private String html;
	private String html_qr;

	private String event;
	private String taxable_amount;
	private String total_tax_amount;
	private String qr_payment_amount;



	//種目
	private String zhongmu;
	//課税標準額
	private String keshui_jizhune;
	//合計（納付又は還付）税額
	private String heji_shuie;
	private String heji_shuie_qr;

	public String getUPDATE_DATE() {
		return UPDATE_DATE;
	}

	public void setUPDATE_DATE(String uPDATE_DATE) {
		UPDATE_DATE = uPDATE_DATE;
	}

	public String getYyyymmdd_count() {
		return yyyymmdd_count;
	}

	public void setYyyymmdd_count(String yyyymmdd_count) {
		this.yyyymmdd_count = yyyymmdd_count;
	}

	public String getYyyy() {
		return yyyy;
	}

	public void setYyyy(String yyyy) {
		this.yyyy = yyyy;
	}

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getZhongmu() {
		return zhongmu;
	}

	public void setZhongmu(String zhongmu) {
		this.zhongmu = zhongmu;
	}

	public String getKeshui_jizhune() {
		return keshui_jizhune;
	}

	public void setKeshui_jizhune(String keshui_jizhune) {
		this.keshui_jizhune = keshui_jizhune;
	}

	public String getHeji_shuie() {
		return heji_shuie;
	}

	public void setHeji_shuie(String heji_shuie) {
		this.heji_shuie = heji_shuie;
	}

	public String getHeji_shuie_qr() {
		return heji_shuie_qr;
	}

	public void setHeji_shuie_qr(String heji_shuie_qr) {
		this.heji_shuie_qr = heji_shuie_qr;
	}

	public String getHtml_qr() {
		return html_qr;
	}

	public void setHtml_qr(String html_qr) {
		this.html_qr = html_qr;
	}

	public String getTaxable_amount() {
		return taxable_amount;
	}

	public void setTaxable_amount(String taxable_amount) {
		this.taxable_amount = taxable_amount;
	}

	public String getTotal_tax_amount() {
		return total_tax_amount;
	}

	public void setTotal_tax_amount(String total_tax_amount) {
		this.total_tax_amount = total_tax_amount;
	}

	public String getQr_payment_amount() {
		return qr_payment_amount;
	}

	public void setQr_payment_amount(String qr_payment_amount) {
		this.qr_payment_amount = qr_payment_amount;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}


}
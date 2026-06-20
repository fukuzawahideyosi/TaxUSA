package com.panda.bean;

public class t_kuaiji_bcBean {

	private String UPDATE_DATE = "";//  `UPDATE_DATE` timestamp(6) NOT NULL,
	private String yyyymmdd_count = "";//  `yyyymmdd_count` bigint NOT NULL,
	private String yyyy = "";//  `yyyy` varchar(45) NOT NULL,
	private String invoice_date							= "";
	private String issuer									= "";
	private String consumption_tax_number					= "";
	private String product_or_service_description			= "";
	private String total_amount_with_tax					= "";
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
	public String getInvoice_date() {
		return invoice_date;
	}
	public void setInvoice_date(String invoice_date) {
		this.invoice_date = invoice_date;
	}
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public String getConsumption_tax_number() {
		return consumption_tax_number;
	}
	public void setConsumption_tax_number(String consumption_tax_number) {
		this.consumption_tax_number = consumption_tax_number;
	}
	public String getProduct_or_service_description() {
		return product_or_service_description;
	}
	public void setProduct_or_service_description(String product_or_service_description) {
		this.product_or_service_description = product_or_service_description;
	}
	public String getTotal_amount_with_tax() {
		return total_amount_with_tax;
	}
	public void setTotal_amount_with_tax(String total_amount_with_tax) {
		this.total_amount_with_tax = total_amount_with_tax;
	}
}
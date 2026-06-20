package com.panda.bean;

public class t_kuaiji_aBean {
	private String UPDATE_DATE			 = "";//	  `UPDATE_DATE` timestamp(6) NOT NULL,
	private String yyyymmdd_count		 = "";//	  `yyyymmdd_count` bigint NOT NULL,
	private String yyyy					 = "";//	  `yyyy` varchar(45) NOT NULL,
	private String transaction_date		 = "";//	  `transaction_date` date NOT NULL COMMENT '日期',
	private String customer				 = "";//	  `customer` varchar(255) NOT NULL COMMENT '客户',
	private String product				 = "";//	  `product` varchar(255) NOT NULL COMMENT '商品',
	private String total_amount_with_tax			 = "";//	  `tax_amount` decimal(10,2) NOT NULL COMMENT '含消费税（10%）金额',

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
	public String getTransaction_date() {
		return transaction_date;
	}
	public void setTransaction_date(String transaction_date) {
		this.transaction_date = transaction_date;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getTotal_amount_with_tax() {
		return total_amount_with_tax;
	}
	public void setTotal_amount_with_tax(String total_amount_with_tax) {
		this.total_amount_with_tax = total_amount_with_tax;
	}

}
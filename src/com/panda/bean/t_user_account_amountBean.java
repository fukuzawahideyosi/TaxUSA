package com.panda.bean;

public class t_user_account_amountBean {

	private String UPDATE_DATE = "";
	private String yyyymmdd_count = "";
	private String yyyy = "";
	private String CompanyName_Chinese = "";
	private String CompanyName_English = "";
	private String bangou = "";
	private String InvoiceBangou = "";
	private String amount = "";
	private String zhifu_pingzheng = "";
	private String huikuan_pingzheng = "";


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
	public String getCompanyName_Chinese() {
		return CompanyName_Chinese;
	}
	public void setCompanyName_Chinese(String companyName_Chinese) {
		CompanyName_Chinese = companyName_Chinese;
	}
	public String getCompanyName_English() {
		return CompanyName_English;
	}
	public void setCompanyName_English(String companyName_English) {
		CompanyName_English = companyName_English;
	}
	public String getBangou() {
		return bangou;
	}
	public void setBangou(String bangou) {
		this.bangou = bangou;
	}
	public String getInvoiceBangou() {
		return InvoiceBangou;
	}
	public void setInvoiceBangou(String invoiceBangou) {
		InvoiceBangou = invoiceBangou;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getZhifu_pingzheng() {
		return zhifu_pingzheng;
	}
	public void setZhifu_pingzheng(String zhifu_pingzheng) {
		this.zhifu_pingzheng = zhifu_pingzheng;
	}
	public String getHuikuan_pingzheng() {
		return huikuan_pingzheng;
	}
	public void setHuikuan_pingzheng(String huikuan_pingzheng) {
		this.huikuan_pingzheng = huikuan_pingzheng;
	}
	public String getYyyy() {
		return yyyy;
	}
	public void setYyyy(String yyyy) {
		this.yyyy = yyyy;
	}



	/*
	CREATE TABLE `t_user_account_amount` (
	  `UPDATE_DATE` timestamp(6) NOT NULL COMMENT '更新时间',
	  `yyyymmdd_count` bigint NOT NULL COMMENT '日期计数',
	  `yyyy` varchar(45) NOT NULL COMMENT '年份',
	  `CompanyName_Chinese` varchar(256) DEFAULT NULL COMMENT '公司中文名称',
	  `CompanyName_English` varchar(256) DEFAULT NULL COMMENT '公司英文名称',
	  `bangou` varchar(16) DEFAULT NULL COMMENT 'etax 番号',
	  `InvoiceBangou` varchar(45) DEFAULT NULL COMMENT '消费税税号',
	  `amount` decimal(18,2) NOT NULL DEFAULT '0.00' COMMENT '交易金额',
	  `zhifu_pingzheng` varchar(125) DEFAULT NULL COMMENT '支付凭证',
	  `huikuan_pingzheng` varchar(125) DEFAULT NULL COMMENT '汇款凭证'
	) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户 账户资金表'
	 */

}
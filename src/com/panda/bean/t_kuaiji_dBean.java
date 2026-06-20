package com.panda.bean;

public class t_kuaiji_dBean {
	private String UPDATE_DATE = "";//  `UPDATE_DATE` timestamp(6) NOT NULL,
	private String yyyymmdd_count = "";//  `yyyymmdd_count` bigint NOT NULL,
	private String yyyy = "";//  `yyyy` varchar(45) NOT NULL,
	private String declaration_date = "";//  `declaration_date` date NOT NULL COMMENT '申告日期',
	private String declaration_number = "";//  `declaration_number` varchar(255) NOT NULL COMMENT '申告番号',
	private String product_name = "";//  `product_name` varchar(255) NOT NULL COMMENT '品名',
	private String declared_price_cif = "";//  `declared_price_cif` decimal(10,2) NOT NULL COMMENT '申告価格（CIF）',
	private String consumption_tax_national = "";//  `consumption_tax_national` decimal(10,2) NOT NULL COMMENT '消費税（国税部分）',
	private String consumption_tax_local = "";//  `consumption_tax_local` decimal(10,2) NOT NULL COMMENT '消費税（地方税部分）',

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

	public String getDeclaration_date() {
		return declaration_date;
	}

	public void setDeclaration_date(String declaration_date) {
		this.declaration_date = declaration_date;
	}

	public String getDeclaration_number() {
		return declaration_number;
	}

	public void setDeclaration_number(String declaration_number) {
		this.declaration_number = declaration_number;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getDeclared_price_cif() {
		return declared_price_cif;
	}

	public void setDeclared_price_cif(String declared_price_cif) {
		this.declared_price_cif = declared_price_cif;
	}

	public String getConsumption_tax_national() {
		return consumption_tax_national;
	}

	public void setConsumption_tax_national(String consumption_tax_national) {
		this.consumption_tax_national = consumption_tax_national;
	}

	public String getConsumption_tax_local() {
		return consumption_tax_local;
	}

	public void setConsumption_tax_local(String consumption_tax_local) {
		this.consumption_tax_local = consumption_tax_local;
	}

}
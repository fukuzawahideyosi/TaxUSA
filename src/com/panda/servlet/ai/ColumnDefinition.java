package com.panda.servlet.ai;

public class ColumnDefinition {

	public String name;
	public String type;
	public boolean isNullable;
	public boolean isPrimaryKey;
	public String comment; // 新增字段说明
	public String value;

	public ColumnDefinition(String name, String type, boolean isNullable, boolean isPrimaryKey, String comment, String value) {
		this.name = name;
		this.type = type;
		this.isNullable = isNullable;
		this.isPrimaryKey = isPrimaryKey;
		this.comment = comment;
		this.value = value;
	}

}

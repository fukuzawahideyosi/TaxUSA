package com.panda.servlet.ai;

import java.util.LinkedHashMap;
import java.util.List;

public class TableDefinition {
    public String tableName;
    public String tableName_comment;
    public List<ColumnDefinition> columns;
    public LinkedHashMap<String, String> columnsValue = new LinkedHashMap<String, String>();
    public List<String> columns_chk_etax;
}

package com.panda.servlet.ai;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.dao.ConnectionDao;
import com.panda.utils.JdbcUtils;

public class TableServiceDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(TableServiceDao.class.toString());

    public void createTableWithHistory(TableDefinition def) throws Exception {
        String mainTable = "AI_T_" + def.tableName;
        String historyTable = "AI_H_" + def.tableName;

        List<ColumnDefinition> allColumns = new ArrayList<>();

        // 默认字段
        allColumns.add(new ColumnDefinition("UPDATE_DATE", "TIMESTAMP(6)", false, false, "", ""));
        allColumns.add(new ColumnDefinition("yyyymmdd_count", "BIGINT", false, false, "", ""));
        allColumns.add(new ColumnDefinition("user_id", "VARCHAR(45)", false, false, "", ""));
        allColumns.add(new ColumnDefinition("activation_code", "VARCHAR(45)", false, false, "", ""));
        allColumns.add(new ColumnDefinition("status", "VARCHAR(45)", false, false, "", ""));
        allColumns.add(new ColumnDefinition("file_name", "VARCHAR(512)", true, false, "", ""));
        allColumns.addAll(def.columns);

        // 主表和履历表都处理
        syncTableStructure(historyTable, def.tableName_comment, allColumns);
        syncTableStructure(mainTable, def.tableName_comment, allColumns);
    }

    private void syncTableStructure(String tableName, String tableName_comment, List<ColumnDefinition> columns) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet rs = metaData.getTables(null, null, tableName, null)) {
            if (!rs.next()) {
                // 表不存在，直接创建
                StringBuilder createSQL = new StringBuilder("CREATE TABLE " + tableName + " (");
                for (ColumnDefinition col : columns) {
                    createSQL.append(col.name).append(" ").append(col.type)
                        .append(col.isNullable ? "" : " NOT NULL")
                        .append(col.isPrimaryKey ? " PRIMARY KEY" : "")
                        .append(col.comment != null && !col.comment.isEmpty() ? " COMMENT '" + col.comment.replace("'", "''") + "'" : "")
                        .append(",");
                }
                createSQL.deleteCharAt(createSQL.length() - 1).append(")");

                try (Statement stmt = connection.createStatement()) {
        			logger.debug(createSQL.toString());
        			//TODO
                    stmt.executeUpdate(createSQL.toString());


                    // ✅ 表注释语句
                    String tableCommentSQL = null;
                    if (tableName_comment != null && !tableName_comment.trim().isEmpty()) {
                        tableCommentSQL = "ALTER TABLE " + tableName + " COMMENT = '" + tableName_comment.replace("'", "''") + "'";

                        logger.debug(tableCommentSQL);
                        stmt.executeUpdate(tableCommentSQL);  // ✅ 执行表注释语句
                    }

                }
            } else {
                // 表存在，检查字段
                Map<String, ColumnDefinition> existingCols = new HashMap<>();
                try (ResultSet cols = metaData.getColumns(null, null, tableName, null)) {
                    while (cols.next()) {
                        String colName = cols.getString("COLUMN_NAME");
                        String colType = cols.getString("TYPE_NAME");
                        int colSize = cols.getInt("COLUMN_SIZE");
                        boolean nullable = "YES".equals(cols.getString("IS_NULLABLE"));
                        String fullType = (colType.equalsIgnoreCase("VARCHAR") ? "VARCHAR(" + colSize + ")" : colType);
                        String comment = cols.getString("REMARKS");

                        existingCols.put(colName.toUpperCase(), new ColumnDefinition(colName, fullType, nullable, false, comment, ""));
                    }
                }

                try (Statement stmt = connection.createStatement()) {

                    String tableCommentSQL = "ALTER TABLE " + tableName + " COMMENT = '" + tableName_comment.replace("'", "''") + "'";
                    logger.debug(tableCommentSQL);
                	stmt.executeUpdate(tableCommentSQL);


                    for (ColumnDefinition col : columns) {
                    	if ("UPDATE_DATE".equals(col.name) || "yyyymmdd_count".equals(col.name) || "user_id".equals(col.name)
                    			|| "activation_code".equals(col.name) || "status".equals(col.name) || "file_name".equals(col.name)) {
                    		continue;
                    	}
                        ColumnDefinition existing = existingCols.get(col.name.toUpperCase());

                        String commentSQL = (col.comment != null && !col.comment.isEmpty())
                            ? " COMMENT '" + col.comment.replace("'", "''") + "'"
                            : "";

                        if (existing == null) {
                            // 不存在则添加字段
                            String alter = "ALTER TABLE " + tableName + " ADD COLUMN " +
                                col.name + " " + col.type + (col.isNullable ? "" : " NOT NULL") +
                                commentSQL;
                            logger.debug(alter);
                            stmt.executeUpdate(alter);
                        } else {
                            boolean needUpdate = false;

                            if (!existing.type.equalsIgnoreCase(col.type) || existing.isNullable != col.isNullable) {
                                needUpdate = true;
                            }

                            // 注释不同也更新（排除 null 和 "" 的情况）
                            String existingComment = existing.comment == null ? "" : existing.comment.trim();
                            String newComment = col.comment == null ? "" : col.comment.trim();
                            if (!existingComment.equals(newComment)) {
                                needUpdate = true;
                            }

                            if (needUpdate) {
                            	// 再执行 ALTER TABLE
                            	String alter = "ALTER TABLE " + tableName + " MODIFY COLUMN " +
                            			col.name + " " + col.type + (col.isNullable ? "" : " NOT NULL") +
                            			commentSQL;

                                // 如果要修改为 NOT NULL，先把现有数据中为 NULL 的值更新掉
                                if (col.isNullable) {
                                	logger.debug(alter);
                                    stmt.executeUpdate(alter);

                                    String updateSQL = "UPDATE " + tableName +
                                                       " SET " + col.name + " = NULL " +
                                                       " WHERE " + col.name + "  = '-999999999'";
//                                    logger.debug(updateSQL);
//                                    int count = stmt.executeUpdate(updateSQL);
//                                    logger.debug("updateSQL count " + count);

                                } else {
                                    String updateSQL = "UPDATE " + tableName +
                                            " SET " + col.name + " = '-999999999'" +
                                            " WHERE " + col.name + " IS NULL OR " + col.name + " = ''";
//                                    logger.debug(updateSQL);
//                                    int count = stmt.executeUpdate(updateSQL);
//                                    logger.debug("updateSQL count " + count);

                                	logger.debug(alter);
                                    stmt.executeUpdate(alter);
                                }

                            }

                        }
                    }
                }

            }
        }
    }

    public void dropTrigger(TableDefinition def) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // 删除 UPDATE 触发器
            String triggerSQL = "DROP TRIGGER IF EXISTS trig_" + def.tableName + "_update";
        	logger.debug(triggerSQL);
            stmt.executeUpdate(triggerSQL);

            // 删除 DELETE 触发器
            triggerSQL = "DROP TRIGGER IF EXISTS trig_" + def.tableName + "_delete";
        	logger.debug(triggerSQL);
            stmt.executeUpdate(triggerSQL);

        }
    }

	public void createTrigger(TableDefinition def) throws Exception {

		try (Statement stmt = connection.createStatement()) {
			String triggerSQL = "";

			triggerSQL = ""
					+ "DROP TRIGGER IF EXISTS trig_" + def.tableName + "_update;"
					+ "";
			stmt.executeUpdate(triggerSQL);

			triggerSQL = ""
					+ "CREATE TRIGGER trig_" + def.tableName + "_update "
					+ "BEFORE UPDATE ON AI_T_" + def.tableName + " "
					+ "FOR EACH ROW "
					+ "BEGIN "
					+ "  INSERT INTO AI_H_" + def.tableName + ""
					+ "  SELECT * FROM AI_T_" + def.tableName + ""
					+ "  WHERE yyyymmdd_count = OLD.yyyymmdd_count; "
					+ "END;";
			logger.debug(triggerSQL.toString());
			stmt.executeUpdate(triggerSQL);

			triggerSQL = ""
					+ "DROP TRIGGER IF EXISTS trig_" + def.tableName + "_delete;"
					+ "";
			stmt.executeUpdate(triggerSQL);

			triggerSQL = ""
					+ "CREATE TRIGGER trig_" + def.tableName + "_delete "
					+ "BEFORE DELETE ON AI_T_" + def.tableName + " "
					+ "FOR EACH ROW "
					+ "BEGIN "
					+ "  INSERT INTO AI_H_" + def.tableName + ""
					+ "  SELECT * FROM AI_T_" + def.tableName + ""
					+ "  WHERE yyyymmdd_count = OLD.yyyymmdd_count; "
					+ "END;";

			logger.debug(triggerSQL.toString());
			stmt.executeUpdate(triggerSQL);
		}
	}

	public boolean tableExists(String tableName) throws SQLException {
	    DatabaseMetaData dbMeta = connection.getMetaData();
	    try (ResultSet rs = dbMeta.getTables(null, null, tableName.toUpperCase(), new String[] { "TABLE" })) {
	        return rs.next(); // 存在返回 true
	    }
	}

	public LinkedHashMap<String, LinkedHashMap<String, String>> getAiTables() throws SQLException {

	    LinkedHashMap<String, LinkedHashMap<String, String>> result = new LinkedHashMap<>();
	    String sql = "SELECT table_name, table_comment FROM information_schema.tables " +
	                 "WHERE table_schema = 'psma' AND table_name LIKE 'AI_T_%'" +
	                 "ORDER BY table_comment ASC"
	                 ; // 升序
	    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			logger.debug(stmt.toString());
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	        	LinkedHashMap<String, String> tableInfo = new LinkedHashMap<>();
	            tableInfo.put("name", rs.getString("table_name"));
	            tableInfo.put("comment", rs.getString("table_comment"));
	            result.put(rs.getString("table_name"), tableInfo);
	        }
	    }


	    result.keySet().removeIf(key -> !"AI_T_ALL".equals(key));

	    return result;
	}


	public LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> getTableColumns() {
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> tableColumns = new LinkedHashMap<>();

		String sql = "SELECT table_name, COLUMN_NAME, TRIM(BOTH '\\'' FROM QUOTE(COLUMN_COMMENT)) AS comment_quoted, COLUMN_TYPE, IS_NULLABLE " +
				"FROM information_schema.columns " +
				"WHERE table_schema = 'psma' AND table_name LIKE 'AI_T_%' " +
				"ORDER BY table_name, ORDINAL_POSITION";
		PreparedStatement preparedStatement = null;
		try {

			preparedStatement = connection.prepareStatement(sql);
			ResultSet rs = preparedStatement.executeQuery();

			logger.debug(preparedStatement.toString());

			String currentTableName = null;
			LinkedHashMap<String, LinkedHashMap<String, String>> columnMap = null;

			while (rs.next()) {
				String tableName = rs.getString("table_name");

				if (!Objects.equals(currentTableName, tableName)) {
					// 将旧表的字段信息放入主map
					if (currentTableName != null && columnMap != null) {
						tableColumns.put(currentTableName, columnMap);
					}
					// 初始化新表的字段map
					currentTableName = tableName;
					columnMap = new LinkedHashMap<>();
				}

				LinkedHashMap<String, String> colInfo = new LinkedHashMap<>();
				colInfo.put("name", rs.getString("COLUMN_NAME"));
				colInfo.put("comment", rs.getString("comment_quoted"));
				colInfo.put("type", rs.getString("COLUMN_TYPE"));
				colInfo.put("nullable", rs.getString("IS_NULLABLE"));

				columnMap.put(rs.getString("COLUMN_NAME"), colInfo);
			}

			// 添加最后一张表
			if (currentTableName != null && columnMap != null) {
				tableColumns.put(currentTableName, columnMap);
			}

			return tableColumns;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return tableColumns;

	}

	public int insertDynamic0ng(String yyyymmdd_count, String user_id, String activation_code, String status, TableDefinition tableDef) throws SQLException {
	    PreparedStatement ps = null;
	    try {
	        String sql = "INSERT INTO AI_T_" + tableDef.tableName +
	                " (UPDATE_DATE, yyyymmdd_count, user_id, activation_code, status) VALUES (?,?,?,?,?)";

	        ps = connection.prepareStatement(sql);

	        int i = 0;
	        ps.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
	        ps.setString(++i, yyyymmdd_count);
	        ps.setString(++i, user_id);

	        ps.setString(++i, activation_code);
	        ps.setString(++i, status);


			logger.debug(ps.toString());
	        return ps.executeUpdate();
	    } finally {
	        JdbcUtils.close(null, ps, connection);
	    }
	}

	public int insertDynamic0(String yyyymmdd_count, String user_id, String activation_code, String status, TableDefinition tableDef) throws SQLException {
	    PreparedStatement ps = null;
		try {
			// 必须字段
			String baseColumns = "UPDATE_DATE, yyyymmdd_count, user_id, activation_code, status";
			String basePlaceholders = "?,?,?,?,?";

			// 拼接动态字段（只取 name 以 col_name 开头并且 isNullable=true 的列）
			String extraColumns = "";
			String extraPlaceholders = "";

			if (tableDef.columns != null && !tableDef.columns.isEmpty()) {
				List<ColumnDefinition> filteredCols = tableDef.columns.stream()
						.filter(c -> c.name != null && c.name.startsWith("col_name") && c.isNullable == false)
						.collect(Collectors.toList());

				if (filteredCols.size() > 0) {
					extraColumns = filteredCols.stream()
							.map(c -> c.name)
							.collect(Collectors.joining(", ", ", ", "")); // 前面加逗号

					extraPlaceholders = filteredCols.stream()
							.map(c -> "?")
							.collect(Collectors.joining(", ", ", ", ""));
				}

				String sql = "INSERT INTO AI_T_" + tableDef.tableName +
						" (" + baseColumns + extraColumns + ") VALUES (" + basePlaceholders + extraPlaceholders + ")";

				ps = connection.prepareStatement(sql);

				int i = 0;
				ps.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
				ps.setString(++i, yyyymmdd_count);
				ps.setString(++i, user_id);
				ps.setString(++i, activation_code);
				ps.setString(++i, status);

				// 这里 filteredCols 就是要用来赋值的列
				for (ColumnDefinition col : filteredCols) {
					String type = col.type.toLowerCase();
					String value = col.value;

					if (value == null) {
						if (type.startsWith("int") || type.startsWith("bigint") || type.startsWith("decimal") || type.startsWith("numeric")) {
							value = "-999999999"; // 数字默认 0
						} else {
							value = "-999999999"; // 字符串默认空
						}
					}

					if (type.contains("char") || type.contains("text") || type.contains("varchar")) {
						ps.setString(++i, value);
					} else if (type.startsWith("bigint")) {
						ps.setLong(++i, Long.parseLong(value));
					} else if (type.startsWith("int")) {
						ps.setInt(++i, Integer.parseInt(value));
					} else if (type.contains("decimal") || type.contains("numeric")) {
						ps.setBigDecimal(++i, new java.math.BigDecimal(value));
					} else if (type.contains("date") || type.contains("time")) {
						if (value.isEmpty()) {
							ps.setNull(++i, java.sql.Types.TIMESTAMP);
						} else {
							ps.setTimestamp(++i, java.sql.Timestamp.valueOf(value)); // yyyy-MM-dd HH:mm:ss
						}
					} else {
						ps.setObject(++i, value);
					}
				}
			}

			logger.debug(ps.toString());
			return ps.executeUpdate();
		} finally {
	        JdbcUtils.close(null, ps, connection);
	    }
	}


	public int insertDynamic(String yyyymmdd_count, String user_id, String activation_code, String status, String file_name, TableDefinition tableDef) throws SQLException {
	    PreparedStatement ps = null;
	    try {
			// 拼接动态字段（只取 name 以 col_name 开头 的列）
	        // 拼接 SET 部分
	        String columnNames = tableDef.columns.stream()
					.filter(c -> c.name != null && c.name.startsWith("col_name"))
	                .map(c -> c.name)
	                .collect(Collectors.joining(", "));

	        String placeholders = tableDef.columns.stream()
					.filter(c -> c.name != null && c.name.startsWith("col_name"))
	                .map(c -> "?")
	                .collect(Collectors.joining(", "));

	        String sql = "INSERT INTO AI_T_" + tableDef.tableName +
	                " (UPDATE_DATE, yyyymmdd_count, user_id, activation_code, status, file_name," + columnNames + ") VALUES (?,?,?,?,?,?," + placeholders + ")";

	        ps = connection.prepareStatement(sql);


	        int i = 0;
	        ps.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
	        ps.setString(++i, yyyymmdd_count);
	        ps.setString(++i, user_id);

	        ps.setString(++i, activation_code);
	        ps.setString(++i, status);
	        ps.setString(++i, file_name);

	        for (ColumnDefinition col : tableDef.columns) {
	        	if (col.name != null && col.name.startsWith("col_name")) {

	        	} else {
	        		continue;

	        	}
	            String type = col.type.toLowerCase();
	            String value = col.value;

	            if (value == null) {
	            	value = "";
	            }

	            // 根据类型动态绑定
//	            if (type.contains("char") || type.contains("text") || type.contains("varchar")) {
//	                ps.setString(++i, value);
//	            } else if (type.contains("bigint")) {
//	                ps.setLong(++i, Long.parseLong(value));
//	            } else if (type.contains("int")) {
//	                ps.setInt(++i, Integer.parseInt(value));
//	            } else if (type.contains("decimal") || type.contains("numeric")) {
//	                ps.setBigDecimal(++i, new java.math.BigDecimal(value));
//	            } else if (type.contains("date") || type.contains("time")) {
//	                ps.setTimestamp(++i, java.sql.Timestamp.valueOf(value)); // 需要 yyyy-MM-dd HH:mm:ss 格式
//	            } else {
//	                ps.setObject(++i, value); // 默认兜底
//	            }


                ps.setString(++i, value);
	        }

			logger.debug(ps.toString());
	        return ps.executeUpdate();
	    } finally {
	        JdbcUtils.close(null, ps, connection);
	    }
	}

	public int updateDynamic(String yyyymmdd_count, String user_id, String activation_code, String status, String file_name, TableDefinition tableDef) throws SQLException {
	    PreparedStatement ps = null;
	    try {

			// 拼接动态字段（只取 name 以 col_name 开头 的列）
	        // 拼接 SET 部分
	        String columnSet = tableDef.columns.stream()
					.filter(c -> c.name != null && c.name.startsWith("col_name"))
	                .map(c -> c.name + " = ?")
	                .collect(Collectors.joining(", "));

	        String sql = "UPDATE AI_T_" + tableDef.tableName +
	                " SET UPDATE_DATE = ?, yyyymmdd_count = ?, user_id = ?, status = ?, file_name = ?, " +
	                columnSet +
	                " WHERE activation_code = ?";

	        ps = connection.prepareStatement(sql);

	        int i = 0;
	        ps.setTimestamp(++i, new Timestamp(System.currentTimeMillis())); // UPDATE_DATE
	        ps.setString(++i, yyyymmdd_count);
	        ps.setString(++i, user_id);
	        ps.setString(++i, status);
	        ps.setString(++i, file_name);

	        // 设置动态列
	        for (ColumnDefinition col : tableDef.columns) {
	        	if (col.name != null && col.name.startsWith("col_name")) {

	        	} else {
	        		continue;

	        	}

	            String type = col.type.toLowerCase();
	            String value = col.value;

	            if (value == null || value.isEmpty()) {
	                ps.setNull(++i, java.sql.Types.NULL);
	                continue;
	            }

	            if (type.contains("char") || type.contains("text") || type.contains("varchar")) {
	                ps.setString(++i, value);
	            } else if (type.contains("bigint")) {
	                ps.setLong(++i, Long.parseLong(value));
	            } else if (type.contains("int")) {
	                ps.setInt(++i, Integer.parseInt(value));
	            } else if (type.contains("decimal") || type.contains("numeric")) {
	                ps.setBigDecimal(++i, new java.math.BigDecimal(value));
	            } else if (type.contains("date") || type.contains("time")) {
	                ps.setTimestamp(++i, java.sql.Timestamp.valueOf(value)); // 格式需 yyyy-MM-dd HH:mm:ss
	            } else {
	                ps.setObject(++i, value);
	            }
	        }

	        // WHERE 条件 activation_code
	        ps.setString(++i, activation_code);

	        logger.debug(ps.toString());
	        return ps.executeUpdate();
	    } finally {
	        JdbcUtils.close(null, ps, connection);
	    }
	}



	public int updateDynamic(String yyyymmdd_count, String user_id, String activation_code, TableDefinition tableDef) throws SQLException {
	    PreparedStatement ps = null;
	    try {

			// 拼接动态字段（只取 name 以 col_name 开头 的列）
	        // 拼接 SET 部分
	        String columnSet = tableDef.columns.stream()
					.filter(c -> c.name != null && c.name.startsWith("col_name"))
	                .map(c -> c.name + " = ?")
	                .collect(Collectors.joining(", "));

			if(StringUtils.isEmpty(columnSet) == false) {
				columnSet =  ", " + columnSet;
			}

	        String sql = "UPDATE AI_T_" + tableDef.tableName +
	                " SET UPDATE_DATE = ?, yyyymmdd_count = ?, user_id = ? " +
	                columnSet +
	                " WHERE activation_code = ?";

	        ps = connection.prepareStatement(sql);

	        int i = 0;
	        ps.setTimestamp(++i, new Timestamp(System.currentTimeMillis())); // UPDATE_DATE
	        ps.setString(++i, yyyymmdd_count);
	        ps.setString(++i, user_id);

	        // 设置动态列
	        for (ColumnDefinition col : tableDef.columns) {
	        	if (col.name != null && col.name.startsWith("col_name")) {

	        	} else {
	        		continue;

	        	}

	            String type = col.type.toLowerCase();
	            String value = col.value;

	            if (value == null || value.isEmpty()) {
	                ps.setNull(++i, java.sql.Types.NULL);
	                continue;
	            }

	            if (type.contains("char") || type.contains("text") || type.contains("varchar")) {
	                ps.setString(++i, value);
	            } else if (type.contains("bigint")) {
	                ps.setLong(++i, Long.parseLong(value));
	            } else if (type.contains("int")) {
	                ps.setInt(++i, Integer.parseInt(value));
	            } else if (type.contains("decimal") || type.contains("numeric")) {
	                ps.setBigDecimal(++i, new java.math.BigDecimal(value));
	            } else if (type.contains("date") || type.contains("time")) {
	                ps.setTimestamp(++i, java.sql.Timestamp.valueOf(value)); // 格式需 yyyy-MM-dd HH:mm:ss
	            } else {
	                ps.setObject(++i, value);
	            }
	        }

	        // WHERE 条件 activation_code
	        ps.setString(++i, activation_code);

	        logger.debug(ps.toString());
	        return ps.executeUpdate();
	    } finally {
	        JdbcUtils.close(null, ps, connection);
	    }
	}


	public int updateDynamic_status(String yyyymmdd_count, String user_id, String activation_code, String status, TableDefinition tableDef) throws SQLException {
	    PreparedStatement ps = null;
	    try {
	        // 动态拼接要更新的字段


	        // 基础固定字段
	        String sql = "UPDATE AI_T_" + tableDef.tableName +
	                " SET UPDATE_DATE=?, user_id=?, status=?"
	                + " WHERE activation_code=?";

	        ps = connection.prepareStatement(sql);

	        int i = 0;
	        // 固定字段
	        ps.setTimestamp(++i, new Timestamp(System.currentTimeMillis())); // UPDATE_DATE
	        ps.setString(++i, user_id);
	        ps.setString(++i, status);

	        // 动态字段

	        // WHERE 条件
	        ps.setString(++i, activation_code);

	        logger.debug(ps.toString());
	        return ps.executeUpdate();
	    } finally {
	        JdbcUtils.close(null, ps, connection);
	    }
	}


	public TableDefinition selectDynamic(String activation_code, TableDefinition tableDef) throws SQLException {
	    PreparedStatement ps = null;
	    ResultSet rs = null;
        TableDefinition rowDef = new TableDefinition();

	    try {
	        // 拼接 SQL
	        String columnNames = tableDef.columns.stream()
	                .map(c -> c.name)
	                .collect(Collectors.joining(", "));

	        String sql = "SELECT " + columnNames + " FROM AI_T_" + tableDef.tableName + " WHERE activation_code = ?";

	        ps = connection.prepareStatement(sql);
	        ps.setString(1, activation_code);

			logger.debug(ps.toString());
	        rs = ps.executeQuery();

	        while (rs.next()) {
	            rowDef.tableName = tableDef.tableName;
	            rowDef.tableName_comment = tableDef.tableName_comment;
	            rowDef.columns = new ArrayList<>();

	            for (ColumnDefinition col : tableDef.columns) {
	                String type = col.type.toLowerCase();
	                Object val = rs.getObject(col.name);

	                String strVal = null;
	                if (val != null) {
	                    if (type.contains("char") || type.contains("text") || type.contains("varchar")) {
	                        strVal = rs.getString(col.name);
	                    } else if (type.contains("bigint")) {
	                        strVal = String.valueOf(rs.getLong(col.name));
	                    } else if (type.contains("int")) {
	                        strVal = String.valueOf(rs.getInt(col.name));
	                    } else if (type.contains("decimal") || type.contains("numeric")) {
	                        strVal = rs.getBigDecimal(col.name).toPlainString();
	                    } else if (type.contains("date") || type.contains("time")) {
	                        Timestamp ts = rs.getTimestamp(col.name);
	                        strVal = ts != null ? ts.toString() : null;
	                    } else {
	                        strVal = String.valueOf(val);
	                    }
	                }
	                // 兜底：null -> 空串
	                if (strVal == null) {
	                    strVal = "";
	                }

	                // 为当前行生成新的 ColumnDefinition（避免引用同一个对象）
	                ColumnDefinition colCopy = new ColumnDefinition(
	                        col.name, col.type, col.isNullable, col.isPrimaryKey, col.comment, strVal
	                );
	                rowDef.columns.add(colCopy);
	            }
	            return rowDef;
	        }

	    } finally {
	        JdbcUtils.close(rs, ps, connection);
	    }
		return rowDef;
	}

	public List<TableDefinition> selectDynamicListAll(TableDefinition tableDef, String maxNo) {
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    List<TableDefinition> resultList = new ArrayList<>();


	    try {
	        // 拼接 SQL
	        String columnNames = tableDef.columns.stream()
	                .map(c -> c.name)
	                .collect(Collectors.joining(", "));

	        String sql = "SELECT " + columnNames + " FROM AI_T_" + tableDef.tableName + " ";
			sql = sql
					+ " ORDER BY UPDATE_DATE desc"
					+ "";

			if(StringUtils.isEmpty(maxNo) == false) {
				sql = sql
						+ " LIMIT " + maxNo
						+ "";
			}
	        ps = connection.prepareStatement(sql);

			logger.debug(ps.toString());
	        rs = ps.executeQuery();

	        while (rs.next()) {
	            // 每一行数据，复制一个 TableDefinition
	            TableDefinition rowDef = new TableDefinition();
	            rowDef.tableName = tableDef.tableName;
	            rowDef.tableName_comment = tableDef.tableName_comment;
	            rowDef.columns = new ArrayList<>();

	            for (ColumnDefinition col : tableDef.columns) {
	                String type = col.type.toLowerCase();
	                Object val = rs.getObject(col.name);

	                String strVal = null;
	                if (val != null) {
	                    if (type.contains("char") || type.contains("text") || type.contains("varchar")) {
	                        strVal = rs.getString(col.name);
	                    } else if (type.contains("bigint")) {
	                        strVal = String.valueOf(rs.getLong(col.name));
	                    } else if (type.contains("int")) {
	                        strVal = String.valueOf(rs.getInt(col.name));
	                    } else if (type.contains("decimal") || type.contains("numeric")) {
	                        strVal = rs.getBigDecimal(col.name).toPlainString();
	                    } else if (type.contains("date") || type.contains("time")) {
	                        Timestamp ts = rs.getTimestamp(col.name);
	                        strVal = ts != null ? ts.toString() : null;
	                    } else {
	                        strVal = String.valueOf(val);
	                    }
	                }
	                // 兜底：null -> 空串
	                if (strVal == null) {
	                    strVal = "";
	                }

	                // 为当前行生成新的 ColumnDefinition（避免引用同一个对象）
	                ColumnDefinition colCopy = new ColumnDefinition(
	                        col.name, col.type, col.isNullable, col.isPrimaryKey, col.comment, strVal
	                );
	                rowDef.columns.add(colCopy);
	            }

	            resultList.add(rowDef);
	        }

		} catch (Exception e) {
			e.printStackTrace();
	    } finally {
	        JdbcUtils.close(rs, ps, connection);
	    }
		return resultList;
	}


	public Map<String, Map<String, Integer>> getAllTablesYyyymmddCount(LinkedHashMap<String, LinkedHashMap<String, String>> aiTables, LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> tableColumns) throws SQLException {
	    Map<String, Map<String, Integer>> result = new LinkedHashMap<>();


	    // 3. 遍历每个表
	    for (Map.Entry<String, LinkedHashMap<String, String>> tableEntry : aiTables.entrySet()) {
	        String tableName = tableEntry.getKey();
	        String tableComment = tableEntry.getValue().get("comment");

	        TableDefinition tableDef = new TableDefinition();
	        tableDef.tableName = tableName.replaceFirst("^AI_T_", "");
	        tableDef.tableName_comment = tableComment;
	        tableDef.columns = new ArrayList<>();

	        // 构建字段
	        LinkedHashMap<String, LinkedHashMap<String, String>> cols = tableColumns.get(tableName);
	        if (cols != null) {
	            for (Map.Entry<String, LinkedHashMap<String, String>> colEntry : cols.entrySet()) {
	                LinkedHashMap<String, String> colInfo = colEntry.getValue();
	                ColumnDefinition colDef = new ColumnDefinition(
	                        colInfo.get("name"),
	                        colInfo.get("type"),
	                        "YES".equalsIgnoreCase(colInfo.get("nullable")),
	                        false,
	                        colInfo.get("comment"),
	                        null
	                );
	                tableDef.columns.add(colDef);
	            }
	        }

	        // 查询 yyyymmdd_count = "AAA" 的记录数
	        String sql = "SELECT yyyymmdd_count, COUNT(*) AS cnt FROM " + tableName + " GROUP BY yyyymmdd_count";
	        try (PreparedStatement ps = connection.prepareStatement(sql)) {
	            logger.debug(ps.toString());
	            try (ResultSet rs = ps.executeQuery()) {
	                Map<String, Integer> map1 = new LinkedHashMap<>();
	                while (rs.next()) {
	                    String yyyymmdd = rs.getString("yyyymmdd_count");
	                    int count = rs.getInt("cnt");
	                    map1.put(yyyymmdd, count);
	                }
	                result.put(tableName, map1);
	            }
	        }
	    }

	    return result;
	}


	public Map<String, Map<String, String>> getAllTables_activation_code(LinkedHashMap<String, LinkedHashMap<String, String>> aiTables, LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> tableColumns) throws SQLException {
	    Map<String, Map<String, String>> result = new LinkedHashMap<>();


	    // 3. 遍历每个表
	    for (Map.Entry<String, LinkedHashMap<String, String>> tableEntry : aiTables.entrySet()) {
	        String tableName = tableEntry.getKey();
	        String tableComment = tableEntry.getValue().get("comment");

	        TableDefinition tableDef = new TableDefinition();
	        tableDef.tableName = tableName.replaceFirst("^AI_T_", "");
	        tableDef.tableName_comment = tableComment;
	        tableDef.columns = new ArrayList<>();

	        // 构建字段
	        LinkedHashMap<String, LinkedHashMap<String, String>> cols = tableColumns.get(tableName);
	        if (cols != null) {
	            for (Map.Entry<String, LinkedHashMap<String, String>> colEntry : cols.entrySet()) {
	                LinkedHashMap<String, String> colInfo = colEntry.getValue();
	                ColumnDefinition colDef = new ColumnDefinition(
	                        colInfo.get("name"),
	                        colInfo.get("type"),
	                        "YES".equalsIgnoreCase(colInfo.get("nullable")),
	                        false,
	                        colInfo.get("comment"),
	                        null
	                );
	                tableDef.columns.add(colDef);
	            }
	        }

	        // 查询 yyyymmdd_count = "AAA" 的记录数
	        String sql = ""
	        		+ "SELECT yyyymmdd_count, activation_code"
//	        		+ "     , DATE_FORMAT(UPDATE_DATE, '%Y-%m-%d %H:%i:%s') as UPDATE_DATE, status"
	        		+ "     , DATE_FORMAT(UPDATE_DATE, '%Y%m%d') as UPDATE_DATE, status"
	        		+ "  FROM " + tableName + " ORDER BY UPDATE_DATE DESC";
	        try (PreparedStatement ps = connection.prepareStatement(sql)) {
	            logger.debug(ps.toString());
	            try (ResultSet rs = ps.executeQuery()) {
	                while (rs.next()) {
	                    String yyyymmdd_count = rs.getString("yyyymmdd_count");
	                    String activation_code = rs.getString("activation_code");
	                    String UPDATE_DATE = rs.getString("UPDATE_DATE");
	                    String status = rs.getString("status");
	                    if ("jiben_qingbao0".equals(status)) {
	                    	status = "基本情報登录";
	                    } else if ("jiben_qingbao".equals(status)) {
	                    	status = "業務情報登录";
	                    } else if ("yewu_qingbao".equals(status)) {
	                    	status = "后台確認";
	                    } else if ("houtai_queren".equals(status)) {
	                    	status = "確認書登录";
	                    } else if ("queren_shu".equals(status)) {
	                    	status = "后台確認(最終)";
	                    } else if ("houtai_queren_zuizhong".equals(status)) {
	                    	status = "完了";
	                    }

	                    Map<String, String> map1 = result.get(yyyymmdd_count);
	                    if (map1 == null) {
	                    	map1 = new LinkedHashMap<>();
	                    }
	                    map1.put(activation_code, tableName + "," + UPDATE_DATE + "," + status);
	                    result.put(yyyymmdd_count, map1);
	                }
	            }
	        }
	    }

	    return result;
	}

	public int DELETE_where_activation_code(String activation_code, String tableName) {

		PreparedStatement preparedStatement = null;

		try {
			String sql = ""
					+ "DELETE  FROM " + tableName
					+ " where activation_code=?"
					+ "";

//			if (!"admin".equals(user_infoBean.getPermissions()) && !"groupAdmin".equals(user_infoBean.getPermissions())) {
//				sql = sql +""
//						+ " and user_id=?"
//						+ "";
//			}
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, activation_code);
//			if (!"admin".equals(user_infoBean.getPermissions()) && !"groupAdmin".equals(user_infoBean.getPermissions())) {
//				preparedStatement.setString(++i, user_infoBean.getUser_id());
//			}

			logger.debug(preparedStatement.toString());
			int count = preparedStatement.executeUpdate();
			logger.debug("SQL " + count);

			return count;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return 0;
	}


	public void DELETE_where_yyyymmdd_count(LinkedHashMap<String, LinkedHashMap<String, String>> aiTables, String yyyymmdd_count) {
		PreparedStatement preparedStatement = null;

		try {
			for (Map.Entry<String, LinkedHashMap<String, String>> tableEntry : aiTables.entrySet()) {
				String tableName = tableEntry.getKey();

				String sql = "DELETE FROM " + tableName + " WHERE yyyymmdd_count=?";
				preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
				preparedStatement.setString(1, yyyymmdd_count);

				logger.debug(preparedStatement.toString());
				int count = preparedStatement.executeUpdate();
				logger.debug("表 " + tableName + " 删除了 " + count + " 条记录");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}

}

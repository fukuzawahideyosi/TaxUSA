package com.panda.dao;

import java.io.FileReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_amazon_csvBean;
import com.panda.bean.t_etax_amazon_csvExBean;
import com.panda.utils.FuncUtils;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class t_etax_amazon_csvDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(t_etax_amazon_csvDao.class.toString());

	public int select_SIZE_where_yyyymmdd_count_by_yyyy(String yyyymmdd_count, String yyyy) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {

			String sql = ""
					+ "SELECT count(1) SIZE"
					+ "  FROM t_etax_amazon_csv" + yyyy
					+ " WHERE yyyymmdd_count=?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, yyyymmdd_count);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();


			while (resultSet.next()) {
				return resultSet.getInt("SIZE");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return -1;
	}


	public int INSERT(String yyyymmdd_count, String yyyy, String csvFilePath, int fileLineCounter) throws Exception {

		Reader reader = null;
		CSVParser csvParser = null;
		PreparedStatement preparedStatement = null;
		try {

			String sql = generateInsertSQL(yyyy, header);
			preparedStatement = connection.prepareStatement(sql);

			String charset = FuncUtils.detectCharset(csvFilePath);
			logger.debug("CSV 文件编码是: " + charset);



			int count_new = 0;
			int batchSize = 0;

			int[] count_executeBatch;

			reader = new FileReader(csvFilePath, Charset.forName(charset));
			csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());

			for (CSVRecord csvRecord : csvParser) {
//				    for (String column : csvRecord) {
//				        // 处理每一列的值
//				        logger.info(column);
//				    }
				String[] nextLine = new String[28];
//				    String[] nextLine = (String[]) csvRecord.toList().toArray();
				int j = 0;
				for (String value : csvRecord) {
					nextLine[j++] = value;
				}

				++count_new;
				if (count_new >= fileLineCounter - 1) {//去最后一行统计
					break;
				}

				int i = 0;
				preparedStatement.setString(++i, yyyymmdd_count);
//					logger.debug("setParameters S");
				i = setParameters(preparedStatement, nextLine, i);
//					logger.debug("setParameters E");

//					logger.debug(preparedStatement.toString());
//				logger.debug("addBatch S");
				preparedStatement.addBatch();
//				logger.debug("addBatch E");
				batchSize++;

				if (batchSize % 1000 == 0) {
						logger.debug("executeBatch S");
					count_executeBatch = preparedStatement.executeBatch();
						logger.debug("executeBatch E");
					logger.debug("count_all " + (fileLineCounter-2) + " count_new " + count_new + " executeBatch " + count_executeBatch.length);
					batchSize = 0;
				}

			}

			count_executeBatch = preparedStatement.executeBatch(); // Execute the remaining batch
			logger.debug("count_all " + (fileLineCounter-2) + " count_new " + (count_new - 1) + " executeBatch " + count_executeBatch.length);

			return count_new - 1;
		} catch (Exception e) {
			throw e;
		} finally {
			if (csvParser != null) {
				csvParser.close();
			}
			if (reader != null) {
				reader.close();
			}
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}
	public LinkedHashMap<String, t_etax_amazon_csvExBean> selectAll(String yyyy, User_infoBean user_infoBean, String maxNo) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_etax_amazon_csvExBean> LinkedHashMap_t_etax_amazon_csvExBean = new LinkedHashMap<String, t_etax_amazon_csvExBean>();
		try {
			/*
	transaction_datetime 日付/時間
	settlement_number 決済番号
	transaction_type トランザクションの種類
	order_number 注文番号
	sku SKU
	description 説明
	quantity 数量
	amazon_listing_service Amazon 出品サービス
	fulfillment フルフィルメント
	city 市町村
	prefecture 都道府県
	postal_code 郵便番号
	tax_collection_type 税金徴収型
	product_sales 商品売上
	product_sales_tax 商品の売上税
	shipping_fee 配送料
	shipping_tax 配送料の税金
	gift_wrapping_fee ギフト包装手数料
	gift_wrapping_tax ギフト包装クレジットの税金
	amazon_point_fee Amazonポイントの費用
	promotion_discount プロモーション割引額
	promotion_discount_tax プロモーション割引の税金
	marketplace_withholding_tax 源泉徴収税を伴うマーケットプレイス
	commission 手数料
	fba_fee FBA 手数料
	other_transaction_fees トランザクションに関するその他の手数料
	other その他
	total_amount 合計


	);



			 */
			String sql = ""
					+ "SELECT"
					+ "    	 MAX(teas.transaction_datetime) as transaction_datetime"
					+ "    , SUM(product_sales) AS \"商品売上総額\""
					+ "    , SUM(total_amount) AS \"合計総額\""
					+ "    , description AS \"商品説明\""
					+ "    , SUM(quantity) AS \"総数量\""
					+ "    , SUM(product_sales) AS \"商品売上総額\""
					+ "    , SUM(product_sales_tax) AS \"商品の売上税総額\""
					+ "    , SUM(shipping_fee) AS \"配送料総額\""
					+ "    , SUM(shipping_tax) AS \"配送料の税金総額\""
					+ "    , SUM(gift_wrapping_fee) AS \"ギフト包装手数料総額\""
					+ "    , SUM(gift_wrapping_tax) AS \"ギフト包装クレジットの税金総額\""
					+ "    , SUM(amazon_point_fee) AS \"Amazonポイントの費用総額\""
					+ "    , SUM(promotion_discount) AS \"プロモーション割引額総額\""
					+ "    , SUM(promotion_discount_tax) AS \"プロモーション割引の税金総額\""
					+ "    , SUM(marketplace_withholding_tax) AS \"源泉徴収税を伴うマーケットプレイス総額\""
					+ "    , SUM(commission) AS \"手数料総額\""
					+ "    , SUM(fba_fee) AS \"FBA 手数料総額\""
					+ "    , SUM(other_transaction_fees) AS \"トランザクションに関するその他の手数料総額\""
					+ "    , SUM(total_amount) AS \"合計総額\" "

						+ "    , t_etax_account_info.*"

						+ ","
						+ "t_etax_account_res.UPDATE_DATE as tear_UPDATE_DATE,"
						+ "t_etax_account_res.yyyymmdd_count,"
						+ "t_etax_account_res.gHojinmei,"
						//					+ "t_etax_account_res.res_send_mae,"
						//					+ "t_etax_account_res.res_send_go,"
						+ "t_etax_account_res.bangou,"
						+ "t_etax_account_res.word,"
						+ "t_etax_account_res.xml,"
						+ "t_etax_account_res.pdf,"
						+ "t_etax_account_res.horyuu,"
						+ "t_etax_account_res.dataFileName,"
						+ "t_etax_account_res.dataFile,"
						+ "t_etax_account_res.HoujinBangou,"
						+ "t_etax_account_res.InvoiceBangou,"
						+ "t_etax_account_res.output_file"
						+ "  FROM"
						+ "    t_etax_amazon_csv" + yyyy + " as teas"
						+ "    LEFT JOIN t_etax_account_info "
						+ "        ON teas.yyyymmdd_count = t_etax_account_info.yyyymmdd_count "
						+ "    LEFT JOIN t_etax_account_res "
						+ "        ON teas.yyyymmdd_count = t_etax_account_res.yyyymmdd_count "
						+ "GROUP BY"
						+ "    `teas`.`yyyymmdd_count`"
						+ "    , description "
						+ "ORDER BY"
						+ "    SUM(total_amount) DESC"
						+ "";

			if(StringUtils.isEmpty(maxNo) == false) {
				sql = sql
						+ " LIMIT " + maxNo
						+ ";";
			}

			// 创建 DecimalFormat 对象
			DecimalFormat decimalFormat = new DecimalFormat("#,##0");

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			int count = 0;
			while (resultSet.next()) {
				t_etax_amazon_csvExBean t_etax_amazon_csvExBean = new t_etax_amazon_csvExBean();
				t_etax_amazon_csvExBean.setUPDATE_DATE(resultSet.getString("tear_UPDATE_DATE").split("\\.")[0]);
				t_etax_amazon_csvExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));

				t_etax_amazon_csvExBean.setTransaction_datetime(resultSet.getString("transaction_datetime").split("\\.")[0]);
				t_etax_amazon_csvExBean.setProduct_sales(String.valueOf(resultSet.getDouble("商品売上総額")));
				t_etax_amazon_csvExBean.setTotal_amount(String.valueOf(resultSet.getDouble("合計総額")));
				t_etax_amazon_csvExBean.setDescription(resultSet.getString("商品説明"));
				t_etax_amazon_csvExBean.setQuantity(String.valueOf(resultSet.getDouble("総数量")));
				t_etax_amazon_csvExBean.setProduct_sales(String.valueOf(resultSet.getDouble("商品売上総額")));
				t_etax_amazon_csvExBean.setProduct_sales_tax(String.valueOf(resultSet.getDouble("商品の売上税総額")));
				t_etax_amazon_csvExBean.setShipping_fee(String.valueOf(resultSet.getDouble("配送料総額")));
				t_etax_amazon_csvExBean.setShipping_tax(String.valueOf(resultSet.getDouble("配送料の税金総額")));
				t_etax_amazon_csvExBean.setGift_wrapping_fee(String.valueOf(resultSet.getDouble("ギフト包装手数料総額")));
				t_etax_amazon_csvExBean.setGift_wrapping_tax(String.valueOf(resultSet.getDouble("ギフト包装クレジットの税金総額")));
				t_etax_amazon_csvExBean.setAmazon_point_fee(String.valueOf(resultSet.getDouble("Amazonポイントの費用総額")));
				t_etax_amazon_csvExBean.setPromotion_discount(String.valueOf(resultSet.getDouble("プロモーション割引額総額")));
				t_etax_amazon_csvExBean.setPromotion_discount_tax(String.valueOf(resultSet.getDouble("プロモーション割引の税金総額")));
				t_etax_amazon_csvExBean.setMarketplace_withholding_tax(String.valueOf(resultSet.getDouble("源泉徴収税を伴うマーケットプレイス総額")));
				t_etax_amazon_csvExBean.setCommission(String.valueOf(resultSet.getDouble("手数料総額")));
				t_etax_amazon_csvExBean.setFba_fee(String.valueOf(resultSet.getDouble("FBA 手数料総額")));
				t_etax_amazon_csvExBean.setOther_transaction_fees(String.valueOf(resultSet.getDouble("トランザクションに関するその他の手数料総額")));
				t_etax_amazon_csvExBean.setTotal_amount(String.valueOf(resultSet.getDouble("合計総額")));


				t_etax_amazon_csvExBean.setUser_id(resultSet.getString("user_id"));
				t_etax_amazon_csvExBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				t_etax_amazon_csvExBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				t_etax_amazon_csvExBean.setCompany_DD(resultSet.getString("company_DD"));
				t_etax_amazon_csvExBean.setCompany_MM(resultSet.getString("company_MM"));
				t_etax_amazon_csvExBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				t_etax_amazon_csvExBean.setTel_1(resultSet.getString("tel_1"));
				t_etax_amazon_csvExBean.setTel_2(resultSet.getString("tel_2"));
				t_etax_amazon_csvExBean.setTel_3(resultSet.getString("tel_3"));
				t_etax_amazon_csvExBean.setTel_country(resultSet.getString("tel_country"));
				t_etax_amazon_csvExBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				t_etax_amazon_csvExBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_etax_amazon_csvExBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_etax_amazon_csvExBean.setZhice_ziben(resultSet.getString("zhice_ziben"));
				t_etax_amazon_csvExBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				t_etax_amazon_csvExBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_etax_amazon_csvExBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				t_etax_amazon_csvExBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));
				t_etax_amazon_csvExBean.setGeren_dianpu_address(resultSet.getString("geren_dianpu_address"));
				t_etax_amazon_csvExBean.setGeren_dianpu_name(resultSet.getString("geren_dianpu_name"));
				t_etax_amazon_csvExBean.setChangshe_jigou_Select(resultSet.getString("changshe_jigou_Select"));
				t_etax_amazon_csvExBean.setJianyi_keshui_Select(resultSet.getString("jianyi_keshui_Select"));
				t_etax_amazon_csvExBean.setAddress_English(resultSet.getString("address_English"));
				t_etax_amazon_csvExBean.setJianyi_keshui_type(resultSet.getString("jianyi_keshui_type"));
				t_etax_amazon_csvExBean.setTokutei_kikann_siharai_kyuuyo(resultSet.getString("tokutei_kikann_siharai_kyuuyo"));
				t_etax_amazon_csvExBean.setShouri_kaishi_denglu_xiayige(resultSet.getString("shouri_kaishi_denglu_xiayige"));
				t_etax_amazon_csvExBean.setShouri_kaishi_denglu_ben(resultSet.getString("shouri_kaishi_denglu_ben"));
				t_etax_amazon_csvExBean.setEtax_no(resultSet.getString("etax_no"));
				t_etax_amazon_csvExBean.setActivation_code(resultSet.getString("activation_code"));
				t_etax_amazon_csvExBean.setYaoqing_no(resultSet.getString("yaoqing_no"));


				t_etax_amazon_csvExBean.setBangou(resultSet.getString("bangou"));
				t_etax_amazon_csvExBean.setHoryuu(resultSet.getString("horyuu"));
				t_etax_amazon_csvExBean.setHoujinBangou(resultSet.getString("HoujinBangou"));
				t_etax_amazon_csvExBean.setInvoiceBangou(resultSet.getString("InvoiceBangou"));
				t_etax_amazon_csvExBean.setEtax_pw(resultSet.getString("etax_pw"));

				LinkedHashMap_t_etax_amazon_csvExBean.put("" + ++count, t_etax_amazon_csvExBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_etax_amazon_csvExBean;
	}



	public LinkedHashMap<String, t_etax_amazon_csvBean> selectAll_where_yyyymmdd_count_by_yyyy(String yyyymmdd_count, String yyyy) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_etax_amazon_csvBean> LinkedHashMap_t_etax_amazon_csvBean = new LinkedHashMap<String, t_etax_amazon_csvBean>();
		try {
			/*
	transaction_datetime 日付/時間
	settlement_number 決済番号
	transaction_type トランザクションの種類
	order_number 注文番号
	sku SKU
	description 説明
	quantity 数量
	amazon_listing_service Amazon 出品サービス
	fulfillment フルフィルメント
	city 市町村
	prefecture 都道府県
	postal_code 郵便番号
	tax_collection_type 税金徴収型
	product_sales 商品売上
	product_sales_tax 商品の売上税
	shipping_fee 配送料
	shipping_tax 配送料の税金
	gift_wrapping_fee ギフト包装手数料
	gift_wrapping_tax ギフト包装クレジットの税金
	amazon_point_fee Amazonポイントの費用
	promotion_discount プロモーション割引額
	promotion_discount_tax プロモーション割引の税金
	marketplace_withholding_tax 源泉徴収税を伴うマーケットプレイス
	commission 手数料
	fba_fee FBA 手数料
	other_transaction_fees トランザクションに関するその他の手数料
	other その他
	total_amount 合計


	);



			 */



			String sql = ""
					+ "SELECT *"
					+ "  FROM t_etax_amazon_csv" + yyyy
					+ " where yyyymmdd_count=?"
					+ " ORDER BY transaction_datetime ASC"
					+ "";


			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, yyyymmdd_count);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			int count = 0;
			while (resultSet.next()) {
				t_etax_amazon_csvBean t_etax_amazon_csvBean = new t_etax_amazon_csvBean();
				t_etax_amazon_csvBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE").split("\\.")[0]);
				t_etax_amazon_csvBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));

				t_etax_amazon_csvBean.setTransaction_datetime(resultSet.getString("transaction_datetime").split("\\.")[0]);
				t_etax_amazon_csvBean.setSettlement_number(resultSet.getString("settlement_number"));
				t_etax_amazon_csvBean.setTransaction_type(resultSet.getString("transaction_type"));
				t_etax_amazon_csvBean.setOrder_number(resultSet.getString("order_number"));
				t_etax_amazon_csvBean.setSku(resultSet.getString("sku"));
				t_etax_amazon_csvBean.setDescription(resultSet.getString("description"));
				t_etax_amazon_csvBean.setQuantity(resultSet.getString("quantity"));
				t_etax_amazon_csvBean.setAmazon_listing_service(resultSet.getString("amazon_listing_service"));
				t_etax_amazon_csvBean.setFulfillment(resultSet.getString("fulfillment"));
				t_etax_amazon_csvBean.setCity(resultSet.getString("city"));
				t_etax_amazon_csvBean.setPrefecture(resultSet.getString("prefecture"));
				t_etax_amazon_csvBean.setPostal_code(resultSet.getString("postal_code"));
				t_etax_amazon_csvBean.setTax_collection_type(resultSet.getString("tax_collection_type"));
				t_etax_amazon_csvBean.setProduct_sales(String.valueOf(resultSet.getDouble("product_sales")));
				t_etax_amazon_csvBean.setProduct_sales_tax(String.valueOf(resultSet.getDouble("product_sales_tax")));
				t_etax_amazon_csvBean.setShipping_fee(String.valueOf(resultSet.getDouble("shipping_fee")));
				t_etax_amazon_csvBean.setShipping_tax(String.valueOf(resultSet.getDouble("shipping_tax")));
				t_etax_amazon_csvBean.setGift_wrapping_fee(String.valueOf(resultSet.getDouble("gift_wrapping_fee")));
				t_etax_amazon_csvBean.setGift_wrapping_tax(String.valueOf(resultSet.getDouble("gift_wrapping_tax")));
				t_etax_amazon_csvBean.setAmazon_point_fee(String.valueOf(resultSet.getDouble("amazon_point_fee")));
				t_etax_amazon_csvBean.setPromotion_discount(String.valueOf(resultSet.getDouble("promotion_discount")));
				t_etax_amazon_csvBean.setPromotion_discount_tax(String.valueOf(resultSet.getDouble("promotion_discount_tax")));
				t_etax_amazon_csvBean.setMarketplace_withholding_tax(String.valueOf(resultSet.getDouble("marketplace_withholding_tax")));
				t_etax_amazon_csvBean.setCommission(String.valueOf(resultSet.getDouble("commission")));
				t_etax_amazon_csvBean.setFba_fee(String.valueOf(resultSet.getDouble("fba_fee")));
				t_etax_amazon_csvBean.setOther_transaction_fees(String.valueOf(resultSet.getDouble("other_transaction_fees")));
				t_etax_amazon_csvBean.setOther(String.valueOf(resultSet.getDouble("other")));
				t_etax_amazon_csvBean.setTotal_amount(String.valueOf(resultSet.getDouble("total_amount")));

				LinkedHashMap_t_etax_amazon_csvBean.put("" + ++count, t_etax_amazon_csvBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_etax_amazon_csvBean;
	}




	public void delete_yyyy_where_yyyymmdd_count(String yyyy, String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "DELETE FROM t_etax_amazon_csv"+ yyyy
					+ " where yyyymmdd_count = ?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, yyyymmdd_count);
			logger.debug(preparedStatement.toString());
			int count = preparedStatement.executeUpdate();
			logger.debug("SQL " + count);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}

	private String generateInsertSQL(String yyyy, String[] header) {
		// Assuming 'header' contains column names
		String columns = String.join(",", header);
		String placeholders = String.join(",", java.util.Collections.nCopies(header.length - 1, "?"));

		return "INSERT INTO t_etax_amazon_csv" + yyyy + " (" + columns + ") VALUES (now(3), " + placeholders + ")";
	}

	private int setParameters(PreparedStatement preparedStatement, String[] values, int j) throws SQLException {
		for (int i = 0; i < values.length; i++) {
			if (i == 0) {
				// 解析日期时间字符串
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd H:mm:ss z");
				LocalDateTime dateTime = LocalDateTime.parse(values[i], formatter);
				values[i] = dateTime.toString();
			} else {
				try {
					// 格式化器，用于移除逗号
					NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

					// 移除逗号
					Number productSalesNumber = numberFormat.parse(values[i]);
					double productSales = productSalesNumber.doubleValue();
					values[i] = String.format("%.0f", productSales);

				} catch (Exception e) {
					// TODO 自動生成された catch ブロック
//						e.printStackTrace();
				}
			}

			preparedStatement.setString(++j, values[i]);
		}
		return j;
	}

	// 字段名列表
	String[] header = {
			"UPDATE_DATE",
			"yyyymmdd_count",
			"transaction_datetime",
			"settlement_number",
			"transaction_type",
			"order_number",
			"sku",
			"description",
			"quantity",
			"amazon_listing_service",
			"fulfillment",
			"city",
			"prefecture",
			"postal_code",
			"tax_collection_type",
			"product_sales",
			"product_sales_tax",
			"shipping_fee",
			"shipping_tax",
			"gift_wrapping_fee",
			"gift_wrapping_tax",
			"amazon_point_fee",
			"promotion_discount",
			"promotion_discount_tax",
			"marketplace_withholding_tax",
			"commission",
			"fba_fee",
			"other_transaction_fees",
			"other",
			"total_amount"
	};


}

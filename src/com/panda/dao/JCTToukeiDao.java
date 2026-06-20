package com.panda.dao;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.bean.JCTToukeiBean;
import com.panda.bean.t_etax_account_resBean;
import com.panda.utils.FuncUtils;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class JCTToukeiDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(JCTToukeiDao.class.toString());

	public ArrayList<JCTToukeiBean> select(LinkedHashMap<String, t_etax_account_resBean> linkedHashMapEtaxBean, String maxNo) {
		ArrayList<JCTToukeiBean> maList = new ArrayList<JCTToukeiBean>();

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {


			String strOld = ""
					+ "<BR>本日日本税务署官方公示前日（%getSakuseiDATE%）数据"
					+ "<BR>新增JCT税号共%getAdd_count%个  修正%getUpdate_count%个  删除%getDel_count%个"
					+ "<BR>其中新增"
					+ "<BR>日本本地公司%getRiben_gongsi%个"
					+ "<BR>在日本有办事处的外国公司%getWaiguo_gongsi_banshichu_you%个"
					+ "<BR>在日本没有办事处的外国公司%getWaiguo_gongsi_banshichu_wu%个(<font color='red' >%getBikouRedcount%</font>)"
					+ "<BR>日本本地个人%getRiben_gere%个"
					+ "<BR>在日本有办事处的外国个人%getWaiguo_gere_banshichu_you%个"
					+ "<BR>在日本没有办事处的外国个人%getWaiguo_gere_banshichu_wu%个"
					+ "<BR>==========================================="
					+ "<BR>以下是本次公示中在日本没有办事处的外国公司名单"
					+ "<BR>%getWaiguo_gongsi_banshichu_wu_list%"
					+ "==========================================="
					+ "<BR>本数据来自日本唯一官方JCT公示平台每日更新信息"
					+ "<BR>https://www.invoice-kohyo.nta.go.jp/index.html"
					+ "<BR>下号速度慢，是日本官方办事效率低下。"
					+ "<BR>抵制错误信息，拒绝下号焦虑。"
					+ "";



			String sql = ""
					+ "SELECT * FROM t_jct_toukei order by dataFileName desc"
					+ "";

			if(StringUtils.isEmpty(maxNo) == true) {
				maxNo = "8";
			}

			sql = sql
					+ " LIMIT " + maxNo
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				JCTToukeiBean JCTToukeiBean = new JCTToukeiBean();
				JCTToukeiBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				JCTToukeiBean.setDataFileName(resultSet.getString("dataFileName"));

				JCTToukeiBean.setSakuseiDATE(JCTToukeiBean.getDataFileName().replaceAll("diff_", "").replaceAll(".csv", ""));

				JCTToukeiBean.setAdd_count(resultSet.getInt("add_count"));
				JCTToukeiBean.setUpdate_count(resultSet.getInt("update_count"));
				JCTToukeiBean.setDel_count(resultSet.getInt("del_count"));

				JCTToukeiBean.setRiben_gongsi(resultSet.getInt("riben_gongsi"));
				JCTToukeiBean.setWaiguo_gongsi_banshichu_you(resultSet.getInt("waiguo_gongsi_banshichu_you"));
				JCTToukeiBean.setWaiguo_gongsi_banshichu_wu(resultSet.getInt("waiguo_gongsi_banshichu_wu"));

				Blob waiguo_gongsi_banshichu_wu_list = resultSet.getBlob("waiguo_gongsi_banshichu_wu_list");
				byte[] bdata = waiguo_gongsi_banshichu_wu_list.getBytes(1, (int) waiguo_gongsi_banshichu_wu_list.length());
				String s = new String(bdata);

	            StringBuilder sb = new StringBuilder();
	            StringBuilder sb2 = new StringBuilder();
				String[] fruits= s.split("<BR>");
				int bikouRedcount = 0;
		        for (String fruit : fruits) {
		        	String fruitOld = fruit;
		    		if (fruit.split(",").length == 2) {
		    		 	String str = fruit.split(",")[1];
		    		 	str.replaceAll("\"", "");
		    		 	fruit = FuncUtils.getStarString(fruit, 19-2+str.length()/2, fruit.length()-1);
		    		}

		        	//"T17********106","Ｏｈｌｉｎｓ．Ｒａｃｉｎｇ．ＡＢ"
		        	String temp = FuncUtils.getStarString(fruit, 5, 11);
		            if (linkedHashMapEtaxBean.containsKey(fruit.split(",")[0].replaceAll("\"", ""))) {
		            	sb.append("<font color='red' >"+fruitOld+"</font><BR>");
		            	sb2.append("<font color='red' >"+temp+"</font><BR>");
		            	++bikouRedcount;
		            } else {
		            	sb.append(fruitOld+"<BR>");
		            	sb2.append(temp+"<BR>");
		            }
		        }

				JCTToukeiBean.setWaiguo_gongsi_banshichu_wu_list(sb.toString());
				JCTToukeiBean.setWaiguo_gongsi_banshichu_wu_list2(sb2.toString());

				JCTToukeiBean.setRiben_gere(resultSet.getInt("riben_gere"));
				JCTToukeiBean.setWaiguo_gere_banshichu_you(resultSet.getInt("waiguo_gere_banshichu_you"));
				JCTToukeiBean.setWaiguo_gere_banshichu_wu(resultSet.getInt("waiguo_gere_banshichu_wu"));


				// 日期格式化
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Date d1 = sdf.parse(JCTToukeiBean.getSakuseiDATE());
				Date tmp = d1;
				Calendar dd = Calendar.getInstance();
				dd.setTime(d1);
				// 天数加上1
				dd.add(Calendar.DAY_OF_MONTH, 1);
				d1 = dd.getTime();
//				logger.debug(sdf.format(d1));

				String str = strOld;
				str = str.replaceAll("%d1%", "" +sdf.format(d1));
				str = str.replaceAll("%getSakuseiDATE%", "" +JCTToukeiBean.getSakuseiDATE());
				str = str.replaceAll("%getAdd_count%", "" +JCTToukeiBean.getAdd_count());
				str = str.replaceAll("%getUpdate_count%", "" +JCTToukeiBean.getUpdate_count());
				str = str.replaceAll("%getDel_count%", "" +JCTToukeiBean.getDel_count());

				str = str.replaceAll("%getRiben_gongsi%", "" +JCTToukeiBean.getRiben_gongsi());
				str = str.replaceAll("%getWaiguo_gongsi_banshichu_you%", "" +JCTToukeiBean.getWaiguo_gongsi_banshichu_you());
				str = str.replaceAll("%getWaiguo_gongsi_banshichu_wu%", "" +JCTToukeiBean.getWaiguo_gongsi_banshichu_wu());

				JCTToukeiBean.setBikouRedcount("" + bikouRedcount);
				str = str.replaceAll("%getBikouRedcount%", "" +JCTToukeiBean.getBikouRedcount());

				str = str.replaceAll("%getWaiguo_gongsi_banshichu_wu_list%", "" +JCTToukeiBean.getWaiguo_gongsi_banshichu_wu_list());

				str = str.replaceAll("%getRiben_gere%", "" +JCTToukeiBean.getRiben_gere());
				str = str.replaceAll("%getWaiguo_gere_banshichu_you%", "" +JCTToukeiBean.getWaiguo_gere_banshichu_you());
				str = str.replaceAll("%getWaiguo_gere_banshichu_wu%", "" +JCTToukeiBean.getWaiguo_gere_banshichu_wu());

				JCTToukeiBean.setBikou(str);

				str = strOld;
				str = str.replaceAll("%d1%", "" +sdf.format(d1));
				str = str.replaceAll("%getSakuseiDATE%", "" +JCTToukeiBean.getSakuseiDATE());
				str = str.replaceAll("%getAdd_count%", "" +JCTToukeiBean.getAdd_count());
				str = str.replaceAll("%getUpdate_count%", "" +JCTToukeiBean.getUpdate_count());
				str = str.replaceAll("%getDel_count%", "" +JCTToukeiBean.getDel_count());

				str = str.replaceAll("%getRiben_gongsi%", "" +JCTToukeiBean.getRiben_gongsi());
				str = str.replaceAll("%getWaiguo_gongsi_banshichu_you%", "" +JCTToukeiBean.getWaiguo_gongsi_banshichu_you());
				str = str.replaceAll("%getWaiguo_gongsi_banshichu_wu%", "" +JCTToukeiBean.getWaiguo_gongsi_banshichu_wu());

				str = str.replaceAll("\\(<font color='red' >%getBikouRedcount%</font>\\)", "");

				str = str.replaceAll("%getWaiguo_gongsi_banshichu_wu_list%", "" +JCTToukeiBean.getWaiguo_gongsi_banshichu_wu_list2());

				str = str.replaceAll("%getRiben_gere%", "" +JCTToukeiBean.getRiben_gere());
				str = str.replaceAll("%getWaiguo_gere_banshichu_you%", "" +JCTToukeiBean.getWaiguo_gere_banshichu_you());
				str = str.replaceAll("%getWaiguo_gere_banshichu_wu%", "" +JCTToukeiBean.getWaiguo_gere_banshichu_wu());

				str = str.replaceAll("<font color='red' >", "");
				str = str.replaceAll("</font><BR>", "<BR>");
				str = str.replaceAll("<BR>下号速度慢，是日本官方办事效率低下。", "<BR>");
				str = str.replaceAll("<BR>抵制错误信息，拒绝下号焦虑。", "<BR><BR>");
				JCTToukeiBean.setBikou2(str);

				maList.add(JCTToukeiBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return maList;
	}

	public void add(JCTToukeiBean JCTToukeiBean) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {



			int i = 0;
			String sql = ""
					+ "insert into t_jct_toukei"
					+ "(UPDATE_DATE,dataFileName"
					+ ",add_count,update_count,del_count"
					+ ",riben_gongsi,waiguo_gongsi_banshichu_you,waiguo_gongsi_banshichu_wu,waiguo_gongsi_banshichu_wu_list"
					+ ",riben_gere,waiguo_gere_banshichu_you,waiguo_gere_banshichu_wu)"
					+ "values(now(3),?,?,?,?,?,?,?,?,?,?,?);";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(JCTToukeiBean.getDataFileName());
			preparedStatement.setString(++i, JCTToukeiBean.getDataFileName());

			logger.debug(JCTToukeiBean.getAdd_count());
			preparedStatement.setInt(++i, JCTToukeiBean.getAdd_count());
			logger.debug(JCTToukeiBean.getUpdate_count());
			preparedStatement.setInt(++i, JCTToukeiBean.getUpdate_count());
			logger.debug(JCTToukeiBean.getDel_count());
			preparedStatement.setInt(++i, JCTToukeiBean.getDel_count());

			logger.debug(JCTToukeiBean.getRiben_gongsi());
			preparedStatement.setInt(++i, JCTToukeiBean.getRiben_gongsi());
			logger.debug(JCTToukeiBean.getWaiguo_gongsi_banshichu_you());
			preparedStatement.setInt(++i, JCTToukeiBean.getWaiguo_gongsi_banshichu_you());
			logger.debug(JCTToukeiBean.getWaiguo_gongsi_banshichu_wu());
			preparedStatement.setInt(++i, JCTToukeiBean.getWaiguo_gongsi_banshichu_wu());

			Blob b1 = connection.createBlob();
			b1.setBytes(1, JCTToukeiBean.getWaiguo_gongsi_banshichu_wu_list().getBytes());
			preparedStatement.setBlob(++i, b1);

			logger.debug(JCTToukeiBean.getRiben_gere());
			preparedStatement.setInt(++i, JCTToukeiBean.getRiben_gere());
			logger.debug(JCTToukeiBean.getWaiguo_gere_banshichu_you());
			preparedStatement.setInt(++i, JCTToukeiBean.getWaiguo_gere_banshichu_you());
			logger.debug(JCTToukeiBean.getWaiguo_gere_banshichu_wu());
			preparedStatement.setInt(++i, JCTToukeiBean.getWaiguo_gere_banshichu_wu());


			logger.debug(preparedStatement.toString());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}

	}

	public int selectSize() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""

					+ "SELECT"
					+ "        count(1) as SIZE"
					+ " FROM t_jct_toukei"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

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
		return 0;
	}

}

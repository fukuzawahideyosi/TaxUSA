package com.panda.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.demo.bean.User;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.panda.bean.MABean;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class UserDao  extends ConnectionDao {

	private static Logger logger = Logger.getLogger(UserDao.class.toString());

	//数据库连接对象
	//此方法用于在数据库中查询信息并与Login.jsp表格中所填信息比较，若数据库中存在
	//与表格所填数据一一对应相等，则登陆成功，否则登录失败
	public User login(String username, String password) {
		User u = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			String sql = "select * from user where name=? and password=?";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				u = new User();
				u.setName(resultSet.getString("name"));
				u.setPassword(resultSet.getString("password"));
				logger.debug("登录成功");
			} else {
				logger.debug("用户名或者密码错误");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return u;
	}

	//此方法实现注册功能，向数据库中写入新用户的信息
	public void addUser(User user) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			String sql = "insert into user(id,name,password,role)values(?,?,?,?);";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setInt(1, user.getId());
			preparedStatement.setString(2, user.getName());
			preparedStatement.setString(3, user.getPassword());
			preparedStatement.setInt(4, user.getRole());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}

	}

	public ArrayList<MABean> select(HttpServletRequest req, int pageNo, int pageSize, HashMap<String, String> hashMap) {
		ArrayList<MABean> maList = new ArrayList<MABean>();

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ "select *"
					+ "  from companylist"
					+ "";

			String sqlWhere = getSqlWHERE(req);
			sql = sql + sqlWhere;
			sql = "select P.* from (SELECT @rowno:=@rowno+1 as rowno, O.* from (" + sql + ") O,(select @rowno:=0) t) P"
					+ " where rowno between " + ((pageNo - 1) * pageSize + 1) + " and " + (pageNo * pageSize)
					+ "";


			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				MABean u = new MABean();
				u.setTITLE(resultSet.getString("TITLE").replaceAll("\\?", "・"));
				u.setANNKENN_NO(resultSet.getInt("ANNKENN_NO"));
				u.setKAIRANN_NO(resultSet.getString("KAIRANN_NO").replaceAll("\\?", "・"));
				u.setKOUSYOU_NO(resultSet.getString("KOUSYOU_NO").replaceAll("\\?", "・"));
				u.setOPEN_YYYYMMDD(resultSet.getInt("OPEN_YYYYMMDD"));
				u.setJYOUTO_INFO(resultSet.getString("JYOUTO_INFO").replaceAll("\\?", "・"));
				u.setWATCH_NO(resultSet.getString("WATCH_NO").replaceAll("\\?", "・"));
				u.setCATEGORY(hashMap.get(resultSet.getString("CATEGORY").replaceAll("\\?", "・")));
				u.setKOUSYOU_TAISYOU(resultSet.getString("KOUSYOU_TAISYOU").replaceAll("\\?", "・"));
				u.setSERVICE(resultSet.getString("SERVICE").replaceAll("\\?", "・"));
				u.setURIAGE(resultSet.getInt("URIAGE"));
				u.setJYOUTO_KAGAKU(resultSet.getInt("JYOUTO_KAGAKU"));
				u.setTIIKI(resultSet.getString("TIIKI").replaceAll("\\?", "・"));
				u.setSOUGYOU_YYYY(resultSet.getInt("SOUGYOU_YYYY"));
				u.setNAIYOU(resultSet.getString("NAIYOU").replaceAll("\\?", "・"));

				maList.add(u);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return maList;
	}

	/**
	 * @param req
	 * @return
	 */
	private String getSqlWHERE(HttpServletRequest req) {
		String sql = "";
		String sqlWhere = "";

		String KEY_VALUE = req.getParameter("KEY_VALUE");
		String CATEGORY = req.getParameter("CATEGORY");
		String URIAGE_MIN = req.getParameter("URIAGE_MIN");
		String URIAGE_MAX = req.getParameter("URIAGE_MAX");
		String JYOUTO_KAGAKU_MIN = req.getParameter("JYOUTO_KAGAKU_MIN");
		String JYOUTO_KAGAKU_MAX = req.getParameter("JYOUTO_KAGAKU_MAX");
		String TIIKI = req.getParameter("TIIKI");
		String SOUGYOU_YYYY = req.getParameter("SOUGYOU_YYYY");
		String ANNKENN_NO = req.getParameter("ANNKENN_NO");


		if (StringUtils.isEmpty(KEY_VALUE) == true && StringUtils.isEmpty(CATEGORY) == true
				&& StringUtils.isEmpty(URIAGE_MIN) == true && StringUtils.isEmpty(URIAGE_MAX) == true
				&& StringUtils.isEmpty(JYOUTO_KAGAKU_MIN) == true && StringUtils.isEmpty(JYOUTO_KAGAKU_MAX) == true
				&& StringUtils.isEmpty(TIIKI) == true
				&& StringUtils.isEmpty(ANNKENN_NO) == true) {

		} else {

			// 把繁体转换成简体
			String simple = "";
			// 把简体转换成繁体
			String traditional = "";

			sql = sql + " where ";

			if (StringUtils.isEmpty(KEY_VALUE) == false) {
				// 把繁体转换成简体
				simple = ZhConverterUtil.convertToSimple(KEY_VALUE);
				// 把简体转换成繁体
				traditional = ZhConverterUtil.convertToTraditional(KEY_VALUE);
				sqlWhere = sqlWhere + " (   TITLE like '%" + simple + "%' OR TITLE like '%" + traditional + "%'";
				sqlWhere = sqlWhere + "  OR NAIYOU like '%" + simple + "%' OR NAIYOU like '%" + traditional + "%')";

			}

			if (StringUtils.isEmpty(CATEGORY) == false) {
				if (sqlWhere != "") {
					sqlWhere = sqlWhere + " AND ";
				}
				sqlWhere = sqlWhere + " CATEGORY= '" + CATEGORY.replaceAll("・", "\\?") + "'";

			}

			if (StringUtils.isEmpty(URIAGE_MIN) == false) {
				if (sqlWhere != "") {
					sqlWhere = sqlWhere + " AND ";
				}
				sqlWhere = sqlWhere + " (URIAGE>= " + URIAGE_MIN + " OR URIAGE=-1)";

			}

			if (StringUtils.isEmpty(URIAGE_MAX) == false) {
				if (sqlWhere != "") {
					sqlWhere = sqlWhere + " AND ";
				}
				sqlWhere = sqlWhere + " (URIAGE<= " + URIAGE_MAX + " OR URIAGE=-1)";

			}
			if (StringUtils.isEmpty(JYOUTO_KAGAKU_MIN) == false) {
				if (sqlWhere != "") {
					sqlWhere = sqlWhere + " AND ";
				}
				sqlWhere = sqlWhere + " (JYOUTO_KAGAKU>= " + JYOUTO_KAGAKU_MIN + " OR JYOUTO_KAGAKU=-1)";

			}
			if (StringUtils.isEmpty(JYOUTO_KAGAKU_MAX) == false) {
				if (sqlWhere != "") {
					sqlWhere = sqlWhere + " AND ";
				}
				sqlWhere = sqlWhere + " (JYOUTO_KAGAKU<=  " + JYOUTO_KAGAKU_MAX + " OR JYOUTO_KAGAKU=-1)";

			}
			if (StringUtils.isEmpty(TIIKI) == false) {
				if (sqlWhere != "") {
					sqlWhere = sqlWhere + " AND ";
				}

				// 把繁体转换成简体
				simple = ZhConverterUtil.convertToSimple(TIIKI);
				// 把简体转换成繁体
				traditional = ZhConverterUtil.convertToTraditional(TIIKI);
				sqlWhere = sqlWhere + " (TIIKI like '%" + simple + "%' OR TIIKI like '%" + traditional + "%')";

			}
			if (StringUtils.isEmpty(SOUGYOU_YYYY) == false) {
				if (sqlWhere != "") {
					sqlWhere = sqlWhere + " AND ";
				}
				sqlWhere = sqlWhere + " (SOUGYOU_YYYY>=  " + SOUGYOU_YYYY + " OR SOUGYOU_YYYY=-1)";

			}
			if (StringUtils.isEmpty(ANNKENN_NO) == false) {
				if (sqlWhere != "") {
					sqlWhere = sqlWhere + " AND ";
				}
				sqlWhere = sqlWhere + " (ANNKENN_NO=  " + ANNKENN_NO + ")";

			}

		}

		return sql + sqlWhere + " order by UPDATE_DATE desc";
	}

	public int selectSize(HttpServletRequest req) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ "select count(1) as SIZE"
					+ "  from companylist"
					+ "";

			String sqlWhere = getSqlWHERE(req);

			sql = sql + sqlWhere;
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

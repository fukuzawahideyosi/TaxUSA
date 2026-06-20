package com.panda.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.panda.bean.JCTBean;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class JCTDao  extends ConnectionDao {

	private static Logger logger = Logger.getLogger(JCTDao.class.toString());

	public ArrayList<JCTBean> select(HttpServletRequest req, int pageNo, int pageSize, HashMap<String, String> hashMap) {
		ArrayList<JCTBean> maList = new ArrayList<JCTBean>();

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;


		HashMap<String, String> CorrectHashMap =new HashMap<String, String>();
		CorrectHashMap.put("1", "日本");
		CorrectHashMap.put("0", "其他国家或地区");
		CorrectHashMap.put("2", "无");
		CorrectHashMap.put("3", "有");


		try {

			String sql = ""
					+ "SELECT"
					+ "        t_jct.*"
					+ " FROM t_jct"
//					+ "    , (SELECT registratedNumber"
//					+ "           , CONCAT(sequenceNumber, registratedNumber, process, correct, kind, country, latest, registrationDate"
//					+ "           , updateDate, disposalDate, expireDate, address, addressPrefectureCode, addressCityCode, addressRequest"
//					+ "           , addressRequestPrefectureCode, addressRequestCityCode, kana, name, addressInside, addressInsidePrefectureCode"
//					+ "           , addressInsideCityCode, tradeName, popularName_previousName) AS val"
//					+ "        FROM t_jct"
//					+ "     ) temp"
					+ "";

			String sqlWhere = getSqlWHERE(req);
			sql = sql + sqlWhere;
//			sql = "select P.* from (SELECT @rowno:=@rowno+1 as rowno, O.* from (" + sql + ") O,(select @rowno:=0) t) P"
//					+ " where rowno between " + ((pageNo - 1) * pageSize + 1) + " and " + (pageNo * pageSize)
//					+ "";

			sql =  sql + "  limit  " + ((pageNo - 1) * pageSize) + " , " + (pageSize)
					+ "";


			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				JCTBean u = new JCTBean();
				u.setSequenceNumber(resultSet.getString("SequenceNumber"));
				u.setRegistratedNumber(resultSet.getString("RegistratedNumber"));
				u.setProcess(resultSet.getString("Process"));
				u.setCorrect(resultSet.getString("Correct"));
				u.setKind(resultSet.getString("Kind"));
				u.setCountry(resultSet.getString("Country"));

				if("1".equals(u.getCountry()) == true) {
					u.setCountrySub("");
				} else {
					u.setCountrySub(CorrectHashMap.get(u.getCountry()));
					u.setCountry("0");

				}
				u.setCountry(CorrectHashMap.get(u.getCountry()));

				u.setLatest(resultSet.getString("Latest"));
				u.setRegistrationDate(resultSet.getString("RegistrationDate"));
				u.setUpdateDate(resultSet.getString("UpdateDate"));
				u.setDisposalDate(resultSet.getString("DisposalDate"));
				u.setExpireDate(resultSet.getString("ExpireDate"));
				u.setAddress(resultSet.getString("Address"));
				u.setAddressPrefectureCode(resultSet.getString("AddressPrefectureCode"));
				u.setAddressCityCode(resultSet.getString("AddressCityCode"));
				u.setAddressRequest(resultSet.getString("AddressRequest"));
				u.setAddressRequestPrefectureCode(resultSet.getString("AddressRequestPrefectureCode"));
				u.setAddressRequestCityCode(resultSet.getString("AddressRequestCityCode"));
				u.setKana(resultSet.getString("Kana"));
				u.setName(resultSet.getString("Name"));
				u.setAddressInside(resultSet.getString("AddressInside"));
				u.setAddressInsidePrefectureCode(resultSet.getString("AddressInsidePrefectureCode"));
				u.setAddressInsideCityCode(resultSet.getString("AddressInsideCityCode"));
				u.setTradeName(resultSet.getString("TradeName"));
				u.setPopularName_previousName(resultSet.getString("PopularName_previousName"));

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

		String sequenceNumber = req.getParameter("sequenceNumber");
		String registratedNumber = req.getParameter("registratedNumber");
		String process = req.getParameter("process");
		String correct = req.getParameter("correct");
		String kind = req.getParameter("kind");
		String country = req.getParameter("country");
		String countrySub = req.getParameter("countrySub");
		String latest = req.getParameter("latest");
		String registrationDate = req.getParameter("registrationDate");
		String updateDate_MIN = req.getParameter("updateDate_MIN");
		String updateDate_MAX = req.getParameter("updateDate_MAX");
		String disposalDate = req.getParameter("disposalDate");
		String expireDate = req.getParameter("expireDate");
		String address = req.getParameter("address");
		String addressPrefectureCode = req.getParameter("addressPrefectureCode");
		String addressCityCode = req.getParameter("addressCityCode");
		String addressRequest = req.getParameter("addressRequest");
		String addressRequestPrefectureCode = req.getParameter("addressRequestPrefectureCode");
		String addressRequestCityCode = req.getParameter("addressRequestCityCode");
		String kana = req.getParameter("kana");
		String name = req.getParameter("name");
		String addressInside = req.getParameter("addressInside");
		String addressInsidePrefectureCode = req.getParameter("addressInsidePrefectureCode");
		String addressInsideCityCode = req.getParameter("addressInsideCityCode");
		String tradeName = req.getParameter("tradeName");
		String popularName_previousName = req.getParameter("popularName_previousName");


		if (StringUtils.isEmpty(sequenceNumber) == true
				|| StringUtils.isEmpty(sequenceNumber) == true
				|| StringUtils.isEmpty(registratedNumber) == true
				|| StringUtils.isEmpty(process) == true
				|| StringUtils.isEmpty(correct) == true
				|| StringUtils.isEmpty(kind) == true
				|| StringUtils.isEmpty(country) == true
				|| StringUtils.isEmpty(latest) == true
				|| StringUtils.isEmpty(registrationDate) == true
				|| StringUtils.isEmpty(updateDate_MIN) == true
				|| StringUtils.isEmpty(updateDate_MAX) == true
				|| StringUtils.isEmpty(disposalDate) == true
				|| StringUtils.isEmpty(expireDate) == true
				|| StringUtils.isEmpty(address) == true
				|| StringUtils.isEmpty(addressPrefectureCode) == true
				|| StringUtils.isEmpty(addressCityCode) == true
				|| StringUtils.isEmpty(addressRequest) == true
				|| StringUtils.isEmpty(addressRequestPrefectureCode) == true
				|| StringUtils.isEmpty(addressRequestCityCode) == true
				|| StringUtils.isEmpty(kana) == true
				|| StringUtils.isEmpty(name) == true
				|| StringUtils.isEmpty(addressInside) == true
				|| StringUtils.isEmpty(addressInsidePrefectureCode) == true
				|| StringUtils.isEmpty(addressInsideCityCode) == true
				|| StringUtils.isEmpty(tradeName) == true
				|| StringUtils.isEmpty(popularName_previousName) == true ) {

			sql = sql + " where sequenceNumber>=0";

			// 把繁体转换成简体
			String simple = "";
			// 把简体转换成繁体
			String traditional = "";

//			一連番号	sequenceNumber
//			登録番号	registratedNumber
			if (StringUtils.isEmpty(registratedNumber) == false) {
				if (registratedNumber.toUpperCase().indexOf("T") != 0) {
					registratedNumber = "T" + registratedNumber;
				}
				sqlWhere = sqlWhere + " and (   registratedNumber ='" + registratedNumber + "'  )";

			}
//			事業者処理区分	process
//			訂正区分	correct
//			人格区分	kind
//			国内外区分	country
			if (StringUtils.isEmpty(country) == false) {
				if("1".equals(country) == false) {
					country = countrySub;
				}
				sqlWhere = sqlWhere + " and (   country ='" + country + "'  )";

			}
//			最新履歴	latest
//			登録年月日	registrationDate
//			更新年月日	updateDate
			if (StringUtils.isEmpty(updateDate_MIN) == false) {
				sqlWhere = sqlWhere + " and (   '" + updateDate_MIN + "'<= updateDate )";

			}
			if (StringUtils.isEmpty(updateDate_MAX) == false) {
				sqlWhere = sqlWhere + " and (   updateDate <= '" + updateDate_MAX + "' )";

			}
//			取消年月日	disposalDate
//			失効年月日	expireDate
//			本店又は主たる事務所の所在地（法人）	address
			if (StringUtils.isEmpty(address) == false) {
				// 把繁体转换成简体
				simple = ZhConverterUtil.convertToSimple(address);
				// 把简体转换成繁体
				traditional = ZhConverterUtil.convertToTraditional(simple);
				sqlWhere = sqlWhere + " and ( address collate utf8mb4_unicode_ci like '%" + address + "%' OR address collate utf8mb4_unicode_ci like '%" + simple + "%' OR address collate utf8mb4_unicode_ci like '%" + traditional + "%')";

			}
//			本店又は主たる事務所の所在地都道府県コード（法人）	addressPrefectureCode
//			本店又は主たる事務所の所在地市区町村コード（法人）	addressCityCode
//			本店又は主たる事務所の所在地（公表申出）	addressRequest
//			本店又は主たる事務所の所在地都道府県コード（公表申出）	addressRequestPrefectureCode
//			本店又は主たる事務所の所在地市区町村コード（公表申出）	addressRequestCityCode
//			日本語（カナ）	kana
//			氏名又は名称	name
			if (StringUtils.isEmpty(name) == false) {
				// 把繁体转换成简体
				simple = ZhConverterUtil.convertToSimple(name);
				// 把简体转换成繁体
				traditional = ZhConverterUtil.convertToTraditional(simple);
				sqlWhere = sqlWhere + " and ( name collate utf8mb4_unicode_ci like '%" + name + "%' OR  name collate utf8mb4_unicode_ci like '%" + simple + "%' OR name collate utf8mb4_unicode_ci like '%" + traditional + "%')";

			}
//			国内において行う資産の譲渡等に係る事務所、事業所その他これらに準ずるものの所在地	addressInside
//			国内において行う資産の譲渡等に係る事務所、事業所その他これらに準ずるものの所在地都道府県コード	addressInsidePrefectureCode
//			国内において行う資産の譲渡等に係る事務所、事業所その他これらに準ずるものの所在地市区町村コード	addressInsideCityCode
//			主たる屋号	tradeName
//			通称・旧姓	popularName_previousName



		}

		return sql + sqlWhere + " order by updateDate desc";//desc asc
	}

	public int selectSize(HttpServletRequest req) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""

					+ "SELECT"
					+ "        count(1) as SIZE"
					+ " FROM t_jct"
//					+ "    , (SELECT registratedNumber"
//					+ "           , CONCAT(sequenceNumber, registratedNumber, process, correct, kind, country, latest, registrationDate"
//					+ "           , updateDate, disposalDate, expireDate, address, addressPrefectureCode, addressCityCode, addressRequest"
//					+ "           , addressRequestPrefectureCode, addressRequestCityCode, kana, name, addressInside, addressInsidePrefectureCode"
//					+ "           , addressInsideCityCode, tradeName, popularName_previousName) AS val"
//					+ "        FROM t_jct"
//					+ "     ) temp"
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

	public String selectUpdateDateMax() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ "SELECT MAX(updateDate) as updateDateMax FROM t_jct  "
					+ "";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				return resultSet.getString("updateDateMax");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return "*";
	}

	public String selectName(String data) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ "SELECT registratedNumber FROM t_jct where name like '"+ data +"%' "
					+ "";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				return resultSet.getString("registratedNumber");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return "";
	}

	public void deleteUpdateDateMax(String updateDateMax) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "DELETE FROM t_jct WHERE updateDate = '" + updateDateMax + "'"
					+ "";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			preparedStatement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}

	public void delete_t_jct_overseas() {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "truncate table t_jct_overseas"
					+ "";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			preparedStatement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}

	public void INSERT_t_jct_overseas() {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "INSERT INTO t_jct_overseas SELECT * FROM t_jct  WHERE country ='2'"
					+ "";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			preparedStatement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}
}

package com.panda.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.panda.bean.t_xiaofeishui_shengaoBean;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class t_xiaofeishui_shengaoDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(t_xiaofeishui_shengaoDao.class.toString());

	public static String CREATE_TABL = ""
			+ "CREATE TABLE `t_xiaofeishui_shengao` (\r\n"
			+ "  `UPDATE_DATE` timestamp(6) NOT NULL COMMENT 'UPDATE_DATE',\r\n"
			+ "  `PDSK` varchar(45) NOT NULL COMMENT 'PDSK编号',\r\n"
			+ "  `yyyymmdd_count` bigint NOT NULL COMMENT 'yyyymmdd_count',\r\n"
			+ "  `yyyy` varchar(45) NOT NULL COMMENT 'yyyy',\r\n"
			+ "  `shengao_qijian_from` varchar(8) NOT NULL COMMENT '会计年度自',\r\n"
			+ "  `shengao_qijian_to` varchar(8) NOT NULL COMMENT '会计年度至',\r\n"
			+ "  `jizhun_qijian` decimal(15,0) DEFAULT NULL COMMENT '本申告主体在基准期间的日本课税销售额',\r\n"
			+ "  `teding_qijian` decimal(15,0) DEFAULT NULL COMMENT '本申告主体在特定期间的日本课税销售额',\r\n"
			+ "  `shangyi_niandu` decimal(15,0) DEFAULT NULL COMMENT '本申告主体在上一会计年度的日本课税销售额',\r\n"
			+ "  `qunian_xiaofeishui_shengao` varchar(45) DEFAULT NULL COMMENT '去年是否申告过消费税',\r\n"
			+ "  `keshui_type` varchar(45) DEFAULT NULL COMMENT '本申告主体在该会计年度计算消费税时采用',\r\n"
			+ "  `qunian_xiaofeishui_guoshui` decimal(15,0) DEFAULT NULL COMMENT '去年消费税申告的消费税国税额',\r\n"
			+ "  `hanshui_zongxiaoshoue` decimal(15,0) DEFAULT NULL COMMENT '含税总销售额',\r\n"
			+ "  `shige_qingqiushu_zongzhichue` decimal(15,0) DEFAULT NULL COMMENT '适格请求书总支出额',\r\n"
			+ "  `fei_shige_qingqiushu_zongzhichue` decimal(15,0) DEFAULT NULL COMMENT '非适格请求书总支出额',\r\n"
			+ "  `jinkou_xiaofeishui_guoshui_zonge` decimal(15,0) DEFAULT NULL COMMENT '进口消费税国税部分总额',\r\n"
			+ "  `activation_code` varchar(45) DEFAULT NULL COMMENT 'activation_code',\r\n"
			+ "  `email` varchar(45) DEFAULT NULL COMMENT 'email',\r\n"
			+ "  `fading_zhongjian_shengao_cishu` decimal(15,0) DEFAULT NULL COMMENT '法定中间申告次数',\r\n"
			+ "  `fading_zhongjian_shengao_danci_duiying_yueshu` decimal(15,0) DEFAULT NULL COMMENT '法定中间申告单次对应月数',\r\n"
			+ "  `fading_zhongjian_shengao_danci_guoshui_e` decimal(15,0) DEFAULT NULL COMMENT '法定中间申告单次国税额',\r\n"
			+ "  `fading_zhongjian_shengao_danci_difangshui_e` decimal(15,0) DEFAULT NULL COMMENT '法定中间申告单次地方税额',\r\n"
			+ "  `buhan_shui_xiaoshou_e` decimal(15,0) DEFAULT NULL COMMENT '不含税销售额',\r\n"
			+ "  `keshuibiao_zhun_e` decimal(15,0) DEFAULT NULL COMMENT '课税标准额',\r\n"
			+ "  `xiaofeishui_e_guoshui_bufen` decimal(15,0) DEFAULT NULL COMMENT '消费税额国税部分',\r\n"
			+ "  `kongchu_shui_e_guoshui_bufen_you_hegui_fapiao` decimal(15,0) DEFAULT NULL COMMENT '控除税额国税部分（有合规发票部分）',\r\n"
			+ "  `kongchu_shui_e_guoshui_bufen_wu_hegui_fapiao` decimal(15,0) DEFAULT NULL COMMENT '控除税额国税部分（无合规发票部分）',\r\n"
			+ "  `kongchu_shui_e_guoshui_bufen_jinkou` decimal(15,0) DEFAULT NULL COMMENT '控除税额国税部分（进口部分）',\r\n"
			+ "  `kongchu_shui_e_guoshui_bufen_heji` decimal(15,0) DEFAULT NULL COMMENT '控除税额国税部分（合计）',\r\n"
			+ "  `quannian_yingjiao_xiaofeishui_guoshui_bufen` decimal(15,0) DEFAULT NULL COMMENT '全年应缴消费税国税部分',\r\n"
			+ "  `zhongjian_shengao_yingjiao_xiaofeishui_guoshui_bufen` decimal(15,0) DEFAULT NULL COMMENT '中间申告应缴消费税国税部分',\r\n"
			+ "  `queren_shengao_yingjiao_xiaofeishui_guoshui_bufen` decimal(15,0) DEFAULT NULL COMMENT '确定申告应缴消费税国税部分',\r\n"
			+ "  `quannian_yingjiao_xiaofeishui_difangshui_bufen` decimal(15,0) DEFAULT NULL COMMENT '全年应缴消费税地方税部分',\r\n"
			+ "  `zhongjian_shengao_yingjiao_xiaofeishui_difangshui_bufen` decimal(15,0) DEFAULT NULL COMMENT '中间申告应缴消费税地方税部分',\r\n"
			+ "  `queren_shengao_yingjiao_xiaofeishui_difangshui_bufen` decimal(15,0) DEFAULT NULL COMMENT '确定申告应缴消费税地方税部分',\r\n"
			+ "  `quannian_yingjiao_xiaofeishui_heji` decimal(15,0) DEFAULT NULL COMMENT '全年应缴消费税合计额',\r\n"
			+ "  `zhongjian_shengao_yingjiao_xiaofeishui_heji` decimal(15,0) DEFAULT NULL COMMENT '中间申告应缴消费税合计额',\r\n"
			+ "  `queren_shengao_yingjiao_xiaofeishui_heji` decimal(15,0) DEFAULT NULL COMMENT '确定申告应缴消费税合计额',\r\n"
			+ "  PRIMARY KEY (`yyyymmdd_count`,`yyyy`,`shengao_qijian_from`,`shengao_qijian_to`),\r\n"
			+ "  UNIQUE KEY `PDSK_UNIQUE` (`PDSK`)\r\n"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消费税申告'"
			+ "";

	public int INSERT(t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean) throws SQLException {
		PreparedStatement preparedStatement = null;
		try {
			String sql = ""
					+ "INSERT INTO t_xiaofeishui_shengao"
					+ " ( UPDATE_DATE,PDSK,yyyymmdd_count,yyyy,shengao_qijian_from,shengao_qijian_to,jizhun_qijian,teding_qijian,shangyi_niandu"
					+ "  ,qunian_xiaofeishui_shengao,keshui_type,qunian_xiaofeishui_guoshui,hanshui_zongxiaoshoue,shige_qingqiushu_zongzhichue"
					+ "  ,fei_shige_qingqiushu_zongzhichue,jinkou_xiaofeishui_guoshui_zonge,activation_code"

					+ ",email"

					+ ",fading_zhongjian_shengao_cishu"
					+ ",fading_zhongjian_shengao_danci_duiying_yueshu"
					+ ",fading_zhongjian_shengao_danci_guoshui_e"
					+ ",fading_zhongjian_shengao_danci_difangshui_e"
					+ ",buhan_shui_xiaoshou_e"
					+ ",keshuibiao_zhun_e"
					+ ",xiaofeishui_e_guoshui_bufen"
					+ ",kongchu_shui_e_guoshui_bufen_you_hegui_fapiao"
					+ ",kongchu_shui_e_guoshui_bufen_wu_hegui_fapiao"
					+ ",kongchu_shui_e_guoshui_bufen_jinkou"
					+ ",kongchu_shui_e_guoshui_bufen_heji"
					+ ",quannian_yingjiao_xiaofeishui_guoshui_bufen"
					+ ",zhongjian_shengao_yingjiao_xiaofeishui_guoshui_bufen"
					+ ",queren_shengao_yingjiao_xiaofeishui_guoshui_bufen"
					+ ",quannian_yingjiao_xiaofeishui_difangshui_bufen"
					+ ",zhongjian_shengao_yingjiao_xiaofeishui_difangshui_bufen"
					+ ",queren_shengao_yingjiao_xiaofeishui_difangshui_bufen"
					+ ",quannian_yingjiao_xiaofeishui_heji"
					+ ",zhongjian_shengao_yingjiao_xiaofeishui_heji"
					+ ",queren_shengao_yingjiao_xiaofeishui_heji"
					+ ") "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"

					+ ",?"

					+ ",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"
					+ ")";

			int i = 0;
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getPDSK());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getYyyymmdd_count());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getYyyy());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getShengao_qijian_from());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getShengao_qijian_to());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getJizhun_qijian());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getTeding_qijian());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getShangyi_niandu());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getQunian_xiaofeishui_shengao());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getKeshui_type());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getQunian_xiaofeishui_guoshui());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getHanshui_zongxiaoshoue());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getShige_qingqiushu_zongzhichue());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getFei_shige_qingqiushu_zongzhichue());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getJinkou_xiaofeishui_guoshui_zonge());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getActivation_code());


			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getForm_mailarea());

			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getFading_zhongjian_shengao_cishu());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getFading_zhongjian_shengao_danci_duiying_yueshu());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getFading_zhongjian_shengao_danci_guoshui_e());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getFading_zhongjian_shengao_danci_difangshui_e());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getBuhan_shui_xiaoshou_e());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getKeshuibiao_zhun_e());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getXiaofeishui_e_guoshui_bufen());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getKongchu_shui_e_guoshui_bufen_you_hegui_fapiao());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getKongchu_shui_e_guoshui_bufen_wu_hegui_fapiao());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getKongchu_shui_e_guoshui_bufen_jinkou());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getKongchu_shui_e_guoshui_bufen_heji());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getQuannian_yingjiao_xiaofeishui_guoshui_bufen());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getZhongjian_shengao_yingjiao_xiaofeishui_guoshui_bufen());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getQueren_shengao_yingjiao_xiaofeishui_guoshui_bufen());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getQuannian_yingjiao_xiaofeishui_difangshui_bufen());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getZhongjian_shengao_yingjiao_xiaofeishui_difangshui_bufen());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getQueren_shengao_yingjiao_xiaofeishui_difangshui_bufen());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getQuannian_yingjiao_xiaofeishui_heji());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getZhongjian_shengao_yingjiao_xiaofeishui_heji());
			preparedStatement.setString(++i, t_xiaofeishui_shengaoBean.getQueren_shengao_yingjiao_xiaofeishui_heji());

			logger.debug(preparedStatement.toString());
			return preparedStatement.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}



	public t_xiaofeishui_shengaoBean SelectKeyValue(String key, String value) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean = new t_xiaofeishui_shengaoBean();

		try {

			String sql = ""
					+ "SELECT"
					+ "    *"
					+ " from"
					+ "    t_xiaofeishui_shengao"
					+ " where"
					+ "    " + key + "=?"
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(1, value);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
//				if (StringUtils.isEmpty(t_xiaofeishui_shengaoBean.getYyyymmdd_count())) {
//					t_xiaofeishui_shengaoBean = new t_xiaofeishui_shengaoBean();
//					break;
//				}

				t_xiaofeishui_shengaoBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_xiaofeishui_shengaoBean.setPDSK(resultSet.getString("PDSK"));
				t_xiaofeishui_shengaoBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_xiaofeishui_shengaoBean.setYyyy(resultSet.getString("yyyy"));
				t_xiaofeishui_shengaoBean.setShengao_qijian_from(resultSet.getString("shengao_qijian_from"));
				t_xiaofeishui_shengaoBean.setShengao_qijian_to(resultSet.getString("shengao_qijian_to"));
				t_xiaofeishui_shengaoBean.setJizhun_qijian(resultSet.getString("jizhun_qijian"));
				t_xiaofeishui_shengaoBean.setTeding_qijian(resultSet.getString("teding_qijian"));
				t_xiaofeishui_shengaoBean.setShangyi_niandu(resultSet.getString("shangyi_niandu"));
				t_xiaofeishui_shengaoBean.setQunian_xiaofeishui_shengao(resultSet.getString("qunian_xiaofeishui_shengao"));
				t_xiaofeishui_shengaoBean.setKeshui_type(resultSet.getString("keshui_type"));
				t_xiaofeishui_shengaoBean.setQunian_xiaofeishui_guoshui(resultSet.getString("qunian_xiaofeishui_guoshui"));
				t_xiaofeishui_shengaoBean.setHanshui_zongxiaoshoue(resultSet.getString("hanshui_zongxiaoshoue"));
				t_xiaofeishui_shengaoBean.setShige_qingqiushu_zongzhichue(resultSet.getString("shige_qingqiushu_zongzhichue"));
				t_xiaofeishui_shengaoBean.setFei_shige_qingqiushu_zongzhichue(resultSet.getString("fei_shige_qingqiushu_zongzhichue"));
				t_xiaofeishui_shengaoBean.setJinkou_xiaofeishui_guoshui_zonge(resultSet.getString("jinkou_xiaofeishui_guoshui_zonge"));
				t_xiaofeishui_shengaoBean.setActivation_code(resultSet.getString("activation_code"));

				t_xiaofeishui_shengaoBean.setForm_mailarea(resultSet.getString("email"));

				t_xiaofeishui_shengaoBean.setFading_zhongjian_shengao_cishu (resultSet.getString("fading_zhongjian_shengao_cishu"));
				t_xiaofeishui_shengaoBean.setFading_zhongjian_shengao_danci_duiying_yueshu (resultSet.getString("fading_zhongjian_shengao_danci_duiying_yueshu"));
				t_xiaofeishui_shengaoBean.setFading_zhongjian_shengao_danci_guoshui_e (resultSet.getString("fading_zhongjian_shengao_danci_guoshui_e"));
				t_xiaofeishui_shengaoBean.setFading_zhongjian_shengao_danci_difangshui_e (resultSet.getString("fading_zhongjian_shengao_danci_difangshui_e"));
				t_xiaofeishui_shengaoBean.setBuhan_shui_xiaoshou_e (resultSet.getString("buhan_shui_xiaoshou_e"));
				t_xiaofeishui_shengaoBean.setKeshuibiao_zhun_e (resultSet.getString("keshuibiao_zhun_e"));
				t_xiaofeishui_shengaoBean.setXiaofeishui_e_guoshui_bufen (resultSet.getString("xiaofeishui_e_guoshui_bufen"));
				t_xiaofeishui_shengaoBean.setKongchu_shui_e_guoshui_bufen_you_hegui_fapiao (resultSet.getString("kongchu_shui_e_guoshui_bufen_you_hegui_fapiao"));
				t_xiaofeishui_shengaoBean.setKongchu_shui_e_guoshui_bufen_wu_hegui_fapiao (resultSet.getString("kongchu_shui_e_guoshui_bufen_wu_hegui_fapiao"));
				t_xiaofeishui_shengaoBean.setKongchu_shui_e_guoshui_bufen_jinkou (resultSet.getString("kongchu_shui_e_guoshui_bufen_jinkou"));
				t_xiaofeishui_shengaoBean.setKongchu_shui_e_guoshui_bufen_heji (resultSet.getString("kongchu_shui_e_guoshui_bufen_heji"));
				t_xiaofeishui_shengaoBean.setQuannian_yingjiao_xiaofeishui_guoshui_bufen (resultSet.getString("quannian_yingjiao_xiaofeishui_guoshui_bufen"));
				t_xiaofeishui_shengaoBean.setZhongjian_shengao_yingjiao_xiaofeishui_guoshui_bufen (resultSet.getString("zhongjian_shengao_yingjiao_xiaofeishui_guoshui_bufen"));
				t_xiaofeishui_shengaoBean.setQueren_shengao_yingjiao_xiaofeishui_guoshui_bufen (resultSet.getString("queren_shengao_yingjiao_xiaofeishui_guoshui_bufen"));
				t_xiaofeishui_shengaoBean.setQuannian_yingjiao_xiaofeishui_difangshui_bufen (resultSet.getString("quannian_yingjiao_xiaofeishui_difangshui_bufen"));
				t_xiaofeishui_shengaoBean.setZhongjian_shengao_yingjiao_xiaofeishui_difangshui_bufen (resultSet.getString("zhongjian_shengao_yingjiao_xiaofeishui_difangshui_bufen"));
				t_xiaofeishui_shengaoBean.setQueren_shengao_yingjiao_xiaofeishui_difangshui_bufen (resultSet.getString("queren_shengao_yingjiao_xiaofeishui_difangshui_bufen"));
				t_xiaofeishui_shengaoBean.setQuannian_yingjiao_xiaofeishui_heji (resultSet.getString("quannian_yingjiao_xiaofeishui_heji"));
				t_xiaofeishui_shengaoBean.setZhongjian_shengao_yingjiao_xiaofeishui_heji (resultSet.getString("zhongjian_shengao_yingjiao_xiaofeishui_heji"));
				t_xiaofeishui_shengaoBean.setQueren_shengao_yingjiao_xiaofeishui_heji(resultSet.getString("queren_shengao_yingjiao_xiaofeishui_heji"));







			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return t_xiaofeishui_shengaoBean;
	}


	public t_xiaofeishui_shengaoBean Select_Where_yyyymmdd_count_and_yyyy(String yyyymmdd_count, String yyyy) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean = new t_xiaofeishui_shengaoBean();

		try {

			String sql = ""
					+ "SELECT"
					+ "    *"
					+ " from"
					+ "    t_xiaofeishui_shengao"
					+ " where"
					+ "    yyyymmdd_count=? and yyyy=?"
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, yyyymmdd_count);
			preparedStatement.setString(++i, yyyy);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_xiaofeishui_shengaoBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_xiaofeishui_shengaoBean.setPDSK(resultSet.getString("PDSK"));
				t_xiaofeishui_shengaoBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_xiaofeishui_shengaoBean.setYyyy(resultSet.getString("yyyy"));
				t_xiaofeishui_shengaoBean.setShengao_qijian_from(resultSet.getString("shengao_qijian_from"));
				t_xiaofeishui_shengaoBean.setShengao_qijian_to(resultSet.getString("shengao_qijian_to"));
				t_xiaofeishui_shengaoBean.setJizhun_qijian(resultSet.getString("jizhun_qijian"));
				t_xiaofeishui_shengaoBean.setTeding_qijian(resultSet.getString("teding_qijian"));
				t_xiaofeishui_shengaoBean.setShangyi_niandu(resultSet.getString("shangyi_niandu"));
				t_xiaofeishui_shengaoBean.setQunian_xiaofeishui_shengao(resultSet.getString("qunian_xiaofeishui_shengao"));
				t_xiaofeishui_shengaoBean.setKeshui_type(resultSet.getString("keshui_type"));
				t_xiaofeishui_shengaoBean.setQunian_xiaofeishui_guoshui(resultSet.getString("qunian_xiaofeishui_guoshui"));
				t_xiaofeishui_shengaoBean.setHanshui_zongxiaoshoue(resultSet.getString("hanshui_zongxiaoshoue"));
				t_xiaofeishui_shengaoBean.setShige_qingqiushu_zongzhichue(resultSet.getString("shige_qingqiushu_zongzhichue"));
				t_xiaofeishui_shengaoBean.setFei_shige_qingqiushu_zongzhichue(resultSet.getString("fei_shige_qingqiushu_zongzhichue"));
				t_xiaofeishui_shengaoBean.setJinkou_xiaofeishui_guoshui_zonge(resultSet.getString("jinkou_xiaofeishui_guoshui_zonge"));
				t_xiaofeishui_shengaoBean.setActivation_code(resultSet.getString("activation_code"));

				t_xiaofeishui_shengaoBean.setForm_mailarea(resultSet.getString("email"));

				t_xiaofeishui_shengaoBean.setFading_zhongjian_shengao_cishu (resultSet.getString("fading_zhongjian_shengao_cishu"));
				t_xiaofeishui_shengaoBean.setFading_zhongjian_shengao_danci_duiying_yueshu (resultSet.getString("fading_zhongjian_shengao_danci_duiying_yueshu"));
				t_xiaofeishui_shengaoBean.setFading_zhongjian_shengao_danci_guoshui_e (resultSet.getString("fading_zhongjian_shengao_danci_guoshui_e"));
				t_xiaofeishui_shengaoBean.setFading_zhongjian_shengao_danci_difangshui_e (resultSet.getString("fading_zhongjian_shengao_danci_difangshui_e"));
				t_xiaofeishui_shengaoBean.setBuhan_shui_xiaoshou_e (resultSet.getString("buhan_shui_xiaoshou_e"));
				t_xiaofeishui_shengaoBean.setKeshuibiao_zhun_e (resultSet.getString("keshuibiao_zhun_e"));
				t_xiaofeishui_shengaoBean.setXiaofeishui_e_guoshui_bufen (resultSet.getString("xiaofeishui_e_guoshui_bufen"));
				t_xiaofeishui_shengaoBean.setKongchu_shui_e_guoshui_bufen_you_hegui_fapiao (resultSet.getString("kongchu_shui_e_guoshui_bufen_you_hegui_fapiao"));
				t_xiaofeishui_shengaoBean.setKongchu_shui_e_guoshui_bufen_wu_hegui_fapiao (resultSet.getString("kongchu_shui_e_guoshui_bufen_wu_hegui_fapiao"));
				t_xiaofeishui_shengaoBean.setKongchu_shui_e_guoshui_bufen_jinkou (resultSet.getString("kongchu_shui_e_guoshui_bufen_jinkou"));
				t_xiaofeishui_shengaoBean.setKongchu_shui_e_guoshui_bufen_heji (resultSet.getString("kongchu_shui_e_guoshui_bufen_heji"));
				t_xiaofeishui_shengaoBean.setQuannian_yingjiao_xiaofeishui_guoshui_bufen (resultSet.getString("quannian_yingjiao_xiaofeishui_guoshui_bufen"));
				t_xiaofeishui_shengaoBean.setZhongjian_shengao_yingjiao_xiaofeishui_guoshui_bufen (resultSet.getString("zhongjian_shengao_yingjiao_xiaofeishui_guoshui_bufen"));
				t_xiaofeishui_shengaoBean.setQueren_shengao_yingjiao_xiaofeishui_guoshui_bufen (resultSet.getString("queren_shengao_yingjiao_xiaofeishui_guoshui_bufen"));
				t_xiaofeishui_shengaoBean.setQuannian_yingjiao_xiaofeishui_difangshui_bufen (resultSet.getString("quannian_yingjiao_xiaofeishui_difangshui_bufen"));
				t_xiaofeishui_shengaoBean.setZhongjian_shengao_yingjiao_xiaofeishui_difangshui_bufen (resultSet.getString("zhongjian_shengao_yingjiao_xiaofeishui_difangshui_bufen"));
				t_xiaofeishui_shengaoBean.setQueren_shengao_yingjiao_xiaofeishui_difangshui_bufen (resultSet.getString("queren_shengao_yingjiao_xiaofeishui_difangshui_bufen"));
				t_xiaofeishui_shengaoBean.setQuannian_yingjiao_xiaofeishui_heji (resultSet.getString("quannian_yingjiao_xiaofeishui_heji"));
				t_xiaofeishui_shengaoBean.setZhongjian_shengao_yingjiao_xiaofeishui_heji (resultSet.getString("zhongjian_shengao_yingjiao_xiaofeishui_heji"));
				t_xiaofeishui_shengaoBean.setQueren_shengao_yingjiao_xiaofeishui_heji(resultSet.getString("queren_shengao_yingjiao_xiaofeishui_heji"));


			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return t_xiaofeishui_shengaoBean;
	}


	public int UpdateKeyValue(String yyyymmdd_count, String key, String value) {
		if (value == null) {
			value = "";
		}

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_xiaofeishui_shengao SET " + key + "=? where yyyymmdd_count=?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(1, value);
			preparedStatement.setString(2, yyyymmdd_count);


			logger.debug(preparedStatement.toString());
			int i = preparedStatement.executeUpdate();
			logger.debug("SQL " + i);

			return i;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return 0;
	}

	public void delete_where_yyyymmdd_count(String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "DELETE FROM t_xiaofeishui_shengao"
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

	public void delete_where_yyyymmdd_count_and_yyyy(String yyyymmdd_count, String yyyy) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "DELETE FROM t_xiaofeishui_shengao"
					+ " where yyyymmdd_count = ? and yyyy=?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, yyyymmdd_count);
			preparedStatement.setString(++i, yyyy);
			logger.debug(preparedStatement.toString());
			int count = preparedStatement.executeUpdate();
			logger.debug("SQL " + count);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}

}

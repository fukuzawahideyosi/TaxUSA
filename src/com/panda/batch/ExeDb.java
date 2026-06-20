package com.panda.batch;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.bean.t_etax_account_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.bean.t_etax_account_resBean;
import com.panda.dao.EtaxDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_etax_account_resDao;
import com.panda.utils.FuncUtils;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class ExeDb {

	private static Logger logger = Logger.getLogger(ExeDb.class.toString());

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ



		try {


	        int digits = 8; // 需要生成的位数
	        for (int i = 1; i <= 10; i++) {
		        String etax_pw = "ps" + FuncUtils.generateRandomNumber(digits);
		        logger.info(etax_pw);
	        }

//	        set_etax_pw();

			String ByLike_yyyymmdd_count = "";
			 ByLike_yyyymmdd_count = "2024042098";
			 ByLike_yyyymmdd_count = "2024042298";
			 ByLike_yyyymmdd_count = "2024050898";
			 ByLike_yyyymmdd_count = "2024051398";
			 ByLike_yyyymmdd_count = "2024052098";
//			 ByLike_yyyymmdd_count = "2024052099";
			 ByLike_yyyymmdd_count = "2024052799";
			 ByLike_yyyymmdd_count = "2024060399";
			 ByLike_yyyymmdd_count = "2024060499";
			 ByLike_yyyymmdd_count = "20240509000798";
			 ByLike_yyyymmdd_count = "20250106000756";
//			 ByLike_yyyymmdd_count = "";


//			exe_activation(ByLike_yyyymmdd_count);

//			set_pianjiaming(ByLike_yyyymmdd_count);
//			set_DaibiaoName_English(ByLike_yyyymmdd_count);
//			setInfoForAPI_ALL(ByLike_yyyymmdd_count);
//			exe_activation(ByLike_yyyymmdd_count);



			t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
			LinkedHashMap<String, t_etax_account_resBean> t_etax_account_resLinkedHashMap= t_etax_account_resDao.selectInvoiceBangouNotNull();
			t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();

			for (String yyyymmdd_count : t_etax_account_resLinkedHashMap.keySet()) {
				logger.info("yyyymmdd_count: " + yyyymmdd_count);
				setInfoForAPI(yyyymmdd_count, t_etax_account_infoDao);
				Thread.sleep(500); // 暂停1秒

			}



//			  String[] data = {
//					   "20240612000866"
//					  ,"20240612000865"
//					  ,"20240612000864"
//					  ,"20240520990004"
//
//			        };
//
//			        for (String item : data) {
////			        	setInfoForAPI(item);
//			        }

		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return;
		}





	}


	public static void set_etax_pw() {
        t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		LinkedHashMap<String, t_etax_account_infoBean> LinkedHashMap_t_etax_account_infoBean = t_etax_account_infoDao.selectWhere_etax_pw_bpstax2302();

		int digits = 8; // 需要生成的位数
		for (String yyyymmdd_count : LinkedHashMap_t_etax_account_infoBean.keySet()) {
			t_etax_account_infoBean t_etax_account_infoBean = LinkedHashMap_t_etax_account_infoBean.get(yyyymmdd_count);
			if (t_etax_account_infoBean.getEtax_pw().indexOf("ps") > -1) {
				logger.debug("skip " + yyyymmdd_count);
				continue;
			}
	        String etax_pw = "ps" + FuncUtils.generateRandomNumber(digits);
			t_etax_account_infoBean.setEtax_pw(etax_pw);
			t_etax_account_infoDao.Update_etax_pw(t_etax_account_infoBean.getYyyymmdd_count(), t_etax_account_infoBean);

        }


	}


	public static void set_DaibiaoName_English(String ByLike_yyyymmdd_count) {
		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		LinkedHashMap<String, t_etax_account_infoBean> LinkedHashMap_t_etax_account_infoBean = t_etax_account_infoDao.selectAll_ByLike_yyyymmdd_count(ByLike_yyyymmdd_count);

		for (Entry<String, t_etax_account_infoBean> entry : LinkedHashMap_t_etax_account_infoBean.entrySet()) {
		    String yyyymmdd_count = entry.getKey();
		    t_etax_account_infoBean t_etax_account_infoBean = entry.getValue();

			try {


				String DaibiaoName_English = t_etax_account_infoBean.getDaibiaoName_English();
				if (StringUtils.isEmpty(DaibiaoName_English)) {

				} else {
					continue;

				}


				String DaibiaoName_Chinese = t_etax_account_infoBean.getDaibiaoName_Chinese();
				if (DaibiaoName_Chinese.length() > 0) {
				    // 文字列が空でない場合、最初の文字の後にスペースを追加します
					DaibiaoName_English = FuncUtils.changeChinesePinyin(DaibiaoName_Chinese.charAt(0)+"").get("fullPinyin") + " " + FuncUtils.changeChinesePinyin(DaibiaoName_Chinese.substring(1)).get("fullPinyin");
				}

				t_etax_account_infoDao.Update_key_value(yyyymmdd_count, "DaibiaoName_English", DaibiaoName_English);
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

		}


	}


	public static void exe_activation(String ByLike_yyyymmdd_count) {
		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		LinkedHashMap<String, t_etax_account_infoBean> LinkedHashMap_t_etax_account_infoBean = t_etax_account_infoDao.selectAll_ByLike_yyyymmdd_count(ByLike_yyyymmdd_count);

		t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
		EtaxDao EtaxDao = new EtaxDao();


		for (Entry<String, t_etax_account_infoBean> entry : LinkedHashMap_t_etax_account_infoBean.entrySet()) {
		    String yyyymmdd_count = entry.getKey();
		    t_etax_account_infoBean t_etax_account_infoBean = entry.getValue();

			try {
				FuncUtils.exe_activation(yyyymmdd_count, t_etax_account_infoDao,
						t_etax_account_resDao, EtaxDao, t_etax_account_infoBean.getCompanyName_pianjiaming());
				t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活完了");

			} catch (SQLException e) {
				t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活失败");

			}
		}


	}


	public static void setInfoForAPI_ALL(String ByLike_yyyymmdd_count) throws Exception {
		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		LinkedHashMap<String, t_etax_account_infoBean> LinkedHashMap_t_etax_account_infoBean = t_etax_account_infoDao.selectAll_ByLike_yyyymmdd_count(ByLike_yyyymmdd_count);

		for (String yyyymmdd_count : LinkedHashMap_t_etax_account_infoBean.keySet()) {
			t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
			Thread.sleep(1000); // 暂停1秒
			t_etax_account_infoExBean t_etax_account_infoExBeanAPI = FuncUtils.sendGetInvoiceBangou(t_etax_account_infoExBean.getInvoiceBangou());
			if (StringUtils.isEmpty(t_etax_account_infoExBeanAPI.getCompanyName_English())) {
				t_etax_account_infoExBeanAPI = FuncUtils.sendGetHoujinBangou(t_etax_account_infoExBean.getHoujinBangou());
			}


			if (StringUtils.isEmpty(t_etax_account_infoExBeanAPI.getCompanyName_English())) {
				//何もしない
				logger.error("error yyyymmdd_count: " + yyyymmdd_count);
			} else {
				t_etax_account_infoExBeanAPI.setYyyymmdd_count(yyyymmdd_count);
				t_etax_account_infoDao.Update_For_API(t_etax_account_infoExBeanAPI);

			}

		}
	}

	public static void setInfoForAPI(String yyyymmdd_count, t_etax_account_infoDao t_etax_account_infoDao) throws Exception {

		t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
		t_etax_account_infoExBean t_etax_account_infoExBeanAPI = FuncUtils.sendGetInvoiceBangou(t_etax_account_infoExBean.getInvoiceBangou());
		if (StringUtils.isEmpty(t_etax_account_infoExBeanAPI.getCompanyName_English())) {
			t_etax_account_infoExBeanAPI = FuncUtils.sendGetHoujinBangou(t_etax_account_infoExBean.getHoujinBangou());
		}


		if (StringUtils.isEmpty(t_etax_account_infoExBeanAPI.getCompanyName_English())) {
			//何もしない
			logger.error("error yyyymmdd_count: " + yyyymmdd_count);
		} else {
			t_etax_account_infoExBeanAPI.setYyyymmdd_count(yyyymmdd_count);
			t_etax_account_infoDao.Update_For_API(t_etax_account_infoExBeanAPI);

		}


	}

	public static void set_pianjiaming(String byLike_yyyymmdd_count) {
		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();

        FuncUtils FuncUtils = new FuncUtils();
		LinkedHashMap<String, t_etax_account_infoBean> LinkedHashMap_t_etax_account_infoBean = t_etax_account_infoDao.selectWhere_pianjiaming_null(byLike_yyyymmdd_count);

		for (String yyyymmdd_count : LinkedHashMap_t_etax_account_infoBean.keySet()) {
			t_etax_account_infoBean t_etax_account_infoBean = LinkedHashMap_t_etax_account_infoBean.get(yyyymmdd_count);

			if (StringUtils.isEmpty(t_etax_account_infoBean.getCompanyName_pianjiaming())
					&& !StringUtils.isEmpty(t_etax_account_infoBean.getCompanyName_Chinese())) {
				String CompanyName_pianjiaming = FuncUtils.fn_hanzi(t_etax_account_infoBean.getCompanyName_Chinese());
	    		t_etax_account_infoBean.setCompanyName_pianjiaming(CompanyName_pianjiaming);

			}
			if (StringUtils.isEmpty(t_etax_account_infoBean.getDaibiaoName_pianjiaming())
					&& !StringUtils.isEmpty(t_etax_account_infoBean.getDaibiaoName_Chinese())) {
				String DaibiaoName_pianjiaming = FuncUtils.fn_hanzi(t_etax_account_infoBean.getDaibiaoName_Chinese());
	    		t_etax_account_infoBean.setDaibiaoName_pianjiaming(DaibiaoName_pianjiaming);

			}
			if (StringUtils.isEmpty(t_etax_account_infoBean.getAddress_pianjiaming())
					&& !StringUtils.isEmpty(t_etax_account_infoBean.getAddress_Chinese())) {
				String address_pianjiaming = FuncUtils.fn_hanzi(t_etax_account_infoBean.getAddress_Chinese());
	    		t_etax_account_infoBean.setAddress_pianjiaming(address_pianjiaming);
			}

			if (StringUtils.isEmpty(t_etax_account_infoBean.getDaibiaoName_address_pianjiaming())
					&& !StringUtils.isEmpty(t_etax_account_infoBean.getDaibiaoName_address_Chinese())) {
				String DaibiaoName_address_pianjiaming = FuncUtils.fn_hanzi(t_etax_account_infoBean.getDaibiaoName_address_Chinese());
	    		t_etax_account_infoBean.setDaibiaoName_address_pianjiaming(DaibiaoName_address_pianjiaming);
			}

			logger.debug("CompanyName_pianjiaming : " + t_etax_account_infoBean.getCompanyName_pianjiaming());
			logger.debug("DaibiaoName_pianjiaming : " + t_etax_account_infoBean.getDaibiaoName_pianjiaming());
			logger.debug("address_pianjiaming : " + t_etax_account_infoBean.getAddress_pianjiaming());
			logger.debug("DaibiaoName_address_pianjiaming : " + t_etax_account_infoBean.getDaibiaoName_address_pianjiaming());

			if (StringUtils.isEmpty(t_etax_account_infoBean.getCompanyName_pianjiaming())
					&& StringUtils.isEmpty(t_etax_account_infoBean.getDaibiaoName_pianjiaming())
					&& StringUtils.isEmpty(t_etax_account_infoBean.getAddress_pianjiaming())
					&& StringUtils.isEmpty(t_etax_account_infoBean.getDaibiaoName_address_pianjiaming())) {

			} else {
				t_etax_account_infoDao.Update_pianjiaming(t_etax_account_infoBean.getYyyymmdd_count(), t_etax_account_infoBean);

			}


        }




	}


/*
SELECT
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM `t_etax_account_info`.`yyyymmdd_count`)) as yyyymmdd_count,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM `t_etax_account_info`.`CompanyName_English`)) as CompanyName_English,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM `t_etax_account_info`.`address_English`)) as address_English,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM `t_etax_account_info`.`DaibiaoName_English`)) as DaibiaoName_English,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM `t_etax_account_res`.`Bangou`)) as Bangou,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM `t_etax_account_res`.`HoujinBangou`)) as HoujinBangou,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM `t_etax_account_res`.`InvoiceBangou`)) as InvoiceBangou,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM SUBSTRING(`t_etax_account_info`.`CompanyName_English`, 1, 25))) as CompanyName_English25,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM SUBSTRING(`t_etax_account_info`.`address_English`, 1, 25))) as address_English25,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM SUBSTRING(`t_etax_account_info`.`DaibiaoName_English`, 1, 25))) as DaibiaoName_English25,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM `t_etax_account_info`.`CompanyName_pianjiaming`)) as CompanyName_pianjiaming,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM `t_etax_account_info`.`address_pianjiaming`)) as address_pianjiaming,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM `t_etax_account_info`.`DaibiaoName_pianjiaming`)) as DaibiaoName_pianjiaming,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM SUBSTRING(`t_etax_account_info`.`CompanyName_pianjiaming`, 1, 25))) as CompanyName_pianjiaming25,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM SUBSTRING(`t_etax_account_info`.`address_pianjiaming`, 1, 25))) as address_pianjiaming25,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM SUBSTRING(`t_etax_account_info`.`DaibiaoName_pianjiaming`, 1, 25))) as DaibiaoName_pianjiaming25,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM `t_etax_account_info`.`CompanyName_Chinese`)) as CompanyName_Chinese,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM `t_etax_account_info`.`address_Chinese`)) as address_Chinese,
    TRIM(BOTH '　' FROM TRIM(BOTH ' ' FROM `t_etax_account_info`.`DaibiaoName_Chinese`)) as DaibiaoName_Chinese
FROM `psma`.`t_etax_account_info`
JOIN `psma`.`t_etax_account_res`
ON `t_etax_account_info`.`yyyymmdd_count` = `t_etax_account_res`.`yyyymmdd_count`
order by yyyymmdd_count DESC;








where t_etax_account_info.yyyymmdd_count =20240117000226;










SELECT
    a.yyyymmdd_count
    , a.html_id
    , a.html_value
    , i.DaibiaoName_Chinese
    , i.CompanyName_English ,
    DATE_FORMAT(i.UPDATE_DATE,'%Y/%m/%d %H:%i:%s')  AS 页面更新时间,
    DATE_FORMAT(tear.UPDATE_DATE,'%Y/%m/%d %H:%i:%s') AS 拿号时间,

    CASE 
        WHEN i.UPDATE_DATE > tear.UPDATE_DATE 
        THEN '拿号后更新页面'
        ELSE ''
    END AS 页面更新状态
    
FROM
    t_etax_account a 
    LEFT JOIN t_etax_account_info i 
        ON a.yyyymmdd_count = i.yyyymmdd_count 
        
-- 排除 info 表的删除/测试数据
AND (
        i.CompanyName_Chinese  NOT LIKE '%（删除20%'
    AND i.CompanyName_English NOT LIKE '%（删除20%'
    AND i.DaibiaoName_Chinese NOT LIKE '%（删除20%'
    AND i.DaibiaoName_English NOT LIKE '%（删除20%'
)

AND (
        i.CompanyName_Chinese  NOT LIKE '%ＴＥＳＴ%'
    AND i.CompanyName_English NOT LIKE '%ＴＥＳＴ%'
       AND i.CompanyName_Chinese  NOT LIKE '%TEST%'
    AND i.CompanyName_English NOT LIKE '%TEST%'
    AND i.CompanyName_Chinese NOT LIKE '%Ｆｏｒｅｖｅｒ%'
    AND i.CompanyName_English NOT LIKE '%Ｆｏｒｅｖｅｒ%'
)
    LEFT JOIN t_etax_account_res tear 
        ON a.yyyymmdd_count = tear.yyyymmdd_count 
    -- 排除 html_value
AND a.html_value NOT LIKE '%ＴＥＳＴ%'
AND a.html_value NOT LIKE '%Ｆｏｒｅｖｅｒ%'

WHERE
    ( 
        a.html_id IN ('gDmei', 'gDSei') 
        AND i.DaibiaoName_Chinese IS NOT NULL 
        AND TRIM(i.DaibiaoName_Chinese) <> '' 
        AND i.DaibiaoName_Chinese NOT LIKE CONCAT('%', a.html_value, '%')
    ) 
    OR ( 
        a.html_id = 'gHojinmei' 
        AND i.CompanyName_English IS NOT NULL 
        AND TRIM(i.CompanyName_English) <> '' 
        AND i.CompanyName_English NOT LIKE CONCAT('%', a.html_value, '%')
    )
    
    

ORDER BY i.UPDATE_DATE  DESC;



 *
 *
 *
 *
 */


}

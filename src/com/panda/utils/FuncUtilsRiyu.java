package com.panda.utils;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

public class FuncUtilsRiyu {

	private static Logger logger = Logger.getLogger(FuncUtilsRiyu.class.toString());

	public static String changeRiyuToPianjiaming(String riyu) {
		if (StringUtils.isEmpty(riyu)) {
			return "";
		}
		riyu = FuncUtils.toFullWidth(riyu);
        Tokenizer tokenizer = new Tokenizer() ;
        List<Token> tokens = tokenizer.tokenize(riyu);
        riyu = "";
        for (Token token : tokens) {
        	logger.info(token.getSurface() + "\t" + token.getAllFeatures());
        	riyu = riyu + token.getAllFeaturesArray()[7];// + " "
        }
        logger.info(riyu);
		return riyu;
	}

    public static void main(String[] args) {
    	//OK
//    	changeRiyuToPianjiaming("東京都港区赤坂１丁目１１−３６");
	changeRiyuToPianjiaming("東京都文京区千石４丁目１４番９号１階");
    	//NG
//    	changeRiyuToPianjiaming("深圳市青天鹭电子商务有限公司");
//    	changeRiyuToPianjiaming("深圳市罗湖区东门街道湖贝社区文锦中路1013号朝花大厦北座1206");
//    	changeRiyuToPianjiaming("王鹏焜");

    	//NG
//    	changeRiyuToPianjiaming("Longyan city laugh Yang clothing co., LTD");
//    	changeRiyuToPianjiaming("Pengkun Wang");
//    	changeRiyuToPianjiaming("shenzhenshilonghuaquminzhijiedaobeizhanshequdongquanxincun158dong702 shenzhenshi longhuaqu guangdongsheng 518000 CN");


    }

}
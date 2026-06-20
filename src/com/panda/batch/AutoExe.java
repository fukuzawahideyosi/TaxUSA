package com.panda.batch;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.dao.t_etax_account_resDao;

public class AutoExe {

	private static Logger logger = Logger.getLogger(SearchKono.class.toString());

    // 定时任务间隔（毫秒）
    private static final long CHECK_INTERVAL = 60000; // 每分钟检查一次

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new CheckTask(), 0, CHECK_INTERVAL);
    }

    static class CheckTask extends TimerTask {
        @Override
        public void run() {
            // 在这里检查数据库表t_etax_account_res是否有数据
			t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
			String Max_yyyymmdd_count = t_etax_account_resDao.selectMax_yyyymmdd_count_active();
            if (StringUtils.isEmpty(Max_yyyymmdd_count)) {
                // 如果有数据，调用方法abc
                abc();
            }
        }


        private void abc() {
            // 在这里编写调用方法abc的逻辑
            logger.info("调用方法abc");
        }
    }
}

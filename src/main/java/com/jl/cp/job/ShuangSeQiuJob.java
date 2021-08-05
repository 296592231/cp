/**
 * alifepay.com Inc.
 * Copyright (c) 2016-2020 All Rights Reserved.
 */
package com.jl.cp.job;

import com.jl.cp.job.service.ShuangSeQiuJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jl
 * @date 2021/2/26 11:56
 */
@Slf4j
@Component
public class ShuangSeQiuJob {


    @Autowired
    private ShuangSeQiuJobService shuangSeQiuJobService;




    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    /**
     * 初始化彩票数据
     * @param
     * @return
     * Created by jl on 2021/2/27 11:53.
     */
    //每隔2秒执行一次
//    @Scheduled(cron = "0 43 16 * * ?")
    public void initSsqData() {
        shuangSeQiuJobService.initSsqData();
        System.out.println("定时任务执行时间：" + dateFormat.format(new Date()));
    }


    /**
     * 增量更新双色球没期数据
     * Created by jl on 2021/2/27 12:25.
     */
    @Scheduled(cron = "0 30 23 * * 2,4,7")
    public void incrementalUpdate () {
        log.info("每周2,4,7晚上23点30分获取最新双色球数据===========>> 开始");
        shuangSeQiuJobService.incrementalUpdate();
        log.info("每周2,4,7晚上23点30分获取最新双色球数据===========>> 结束");
    }

//    /**
//     * 每次开奖前发送预测号的数据
//     * Created by jl on 2021/2/27 12:25.
//     */
//    @Scheduled(cron = "18 18 18 * * 2,4,7")
//    public void forecastAndSendMail () {
//        log.info("每周2,4,7晚上23点30分获取最新双色球数据===========>> 开始");
//        shuangSeQiuJobService.forecastAndSendMail();
//        log.info("每周2,4,7晚上23点30分获取最新双色球数据===========>> 结束");
//    }
//
//    /**
//     * 每隔一段时间扫描是否有人工筛选数据
//     * Created by jl on 2021/2/27 12:25.
//     */
//    @Scheduled(cron = "0 0 2/ * * ?")
//    public void IntervalScanPredictionData () {
//        log.info("每隔一段时间扫描是否有人工筛选数据===========>> 开始");
//        shuangSeQiuJobService.IntervalScanPredictionData();
//        log.info("每隔一段时间扫描是否有人工筛选数据===========>> 结束");
//    }


    /**
     * 批量新增详情数据
     * Created by jl on 2021/2/27 12:25.
     */
//    @Scheduled(cron = "0 50 17 * * ?")
    public void  batchInsertDetail() {
        shuangSeQiuJobService.batchInsertDetail();
    }





}

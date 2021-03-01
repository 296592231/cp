/**
 * alifepay.com Inc.
 * Copyright (c) 2016-2020 All Rights Reserved.
 */
package com.jl.cp.job;

import com.jl.cp.job.service.ShuangSeQiuJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jl
 * @date 2021/2/26 11:56
 */
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
    @Scheduled(cron = "0 43 16 * * ?")
    public void initSsqData() {
        shuangSeQiuJobService.initSsqData();
        System.out.println("定时任务执行时间：" + dateFormat.format(new Date()));
    }


    /**
     * 增量更新双色球没期数据
     * Created by jl on 2021/2/27 12:25.
     */
    @Scheduled(cron = "0 13 14 * * 2,4,7")
    public void incrementalUpdate () {
        shuangSeQiuJobService.incrementalUpdate();
    }


    /**
     * 批量新增详情数据
     * Created by jl on 2021/2/27 12:25.
     */
    @Scheduled(cron = "0 50 17 * * ?")
    public void  batchInsertDetail() {
        shuangSeQiuJobService.batchInsertDetail();
    }





}

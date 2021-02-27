/**
 * alifepay.com Inc.
 * Copyright (c) 2016-2020 All Rights Reserved.
 */
package com.jl.cp.job;

import com.jl.cp.entity.SsqBaseInfoDO;
import com.jl.cp.mapper.SsqBaseInfoMapper;
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
    private SsqBaseInfoMapper ssqBaseInfoMapper;


    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    //每隔2秒执行一次
    @Scheduled(fixedRate = 2000)
    public void testTasks() {
        ssqBaseInfoMapper.selectAll();
        System.out.println("定时任务执行时间：" + dateFormat.format(new Date()));
    }
}

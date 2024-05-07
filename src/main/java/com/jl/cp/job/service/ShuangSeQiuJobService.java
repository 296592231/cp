/**
 * alifepay.com Inc.
 * Copyright (c) 2016-2020 All Rights Reserved.
 */
package com.jl.cp.job.service;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.jl.cp.constants.Constants;
import com.jl.cp.coverter.SsqBaseInfoDOCoverter;
import com.jl.cp.entity.SsqBaseInfoDO;
import com.jl.cp.entity.SsqDetailInfoDO;
import com.jl.cp.mapper.SsqBaseInfoMapper;
import com.jl.cp.mapper.SsqDetailInfoMapper;
import com.jl.cp.utils.HttpUtil;
import com.jl.cp.vo.HttpConverterResponseVO.SsqBaseInfoResponseVO;
import com.jl.cp.vo.HttpConverterResponseVO.SsqJsBaseResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author jl
 * @date 2021/2/27 12:22
 */
@Service
public class ShuangSeQiuJobService {

    @Autowired
    private SsqBaseInfoMapper ssqBaseInfoMapper;

    @Autowired
    private SsqDetailInfoMapper ssqDetailInfoMapper;

    public void initSsqData() {
        //获取1000期彩票数据
        List<SsqBaseInfoResponseVO> saveList = new ArrayList<>();
        int tempIssueno = 0;
        while (saveList.size() < 1700) {
            SsqJsBaseResponseVO result = getData(20, tempIssueno, 11);

            if (result == null || !result.getStatus().equals("0")) {
                return;
            }

            Collections.sort(result.getResult().getList(), new Comparator<SsqBaseInfoResponseVO>() {
                @Override
                public int compare(SsqBaseInfoResponseVO o1, SsqBaseInfoResponseVO o2) {
                    return (o1.getIssueno() + "").compareTo((o2.getIssueno() + ""));
                }
            });
            saveList.addAll(result.getResult().getList());
            tempIssueno = Integer.valueOf(result.getResult().getList().get(0).getIssueno());
        }
        List<SsqBaseInfoDO> ssqBaseInfoDOS = SsqBaseInfoDOCoverter.createSsqBaseInfoDOS(saveList);

        if (CollectionUtil.isNotEmpty(ssqBaseInfoDOS)) {
            ssqBaseInfoDOS.forEach(ssqBaseInfoDO -> {
                ssqBaseInfoMapper.insert(ssqBaseInfoDO);
            });

        }
    }

    /**
     * 增量更新双色球没期数据
     * Created by jl on 2021/2/27 12:25.
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void incrementalUpdate() {
        SsqJsBaseResponseVO result = getData(1, 0, 11);
        SsqBaseInfoResponseVO ssqBaseInfoResponseVO  = result.getResult().getList().get(0);

        //查询数据是否存在
        SsqBaseInfoDO ssqBaseInfoDO = ssqBaseInfoMapper.selectOne(new LambdaQueryWrapper<SsqBaseInfoDO>().eq(SsqBaseInfoDO::getIssueno,ssqBaseInfoResponseVO.getIssueno()));

        if (ssqBaseInfoDO == null) {
            SsqBaseInfoDO insertSsqBaseInfoDO = SsqBaseInfoDOCoverter.createSsqBaseInfoDO(ssqBaseInfoResponseVO);
            ssqBaseInfoMapper.insert(insertSsqBaseInfoDO);
            SsqDetailInfoDO ssqDetailInfoDO = getSsqDetailInfoDO(insertSsqBaseInfoDO.getNumber(),insertSsqBaseInfoDO.getIssueno());
            ssqDetailInfoMapper.insert(ssqDetailInfoDO);
        }

    }

    public void batchInsertDetail() {
        List<SsqBaseInfoDO> baseInfoDOS = ssqBaseInfoMapper.selectList(new QueryWrapper<>());
        baseInfoDOS.forEach(ssqBaseInfoDO -> {
            SsqDetailInfoDO ssqDetailInfoDO = getSsqDetailInfoDO(ssqBaseInfoDO.getNumber(),ssqBaseInfoDO.getIssueno());
            ssqDetailInfoMapper.insert(ssqDetailInfoDO);
        });
    }


    /**
     * 根据红球获取详细信息
     * @param qiu 红球以空格隔开，例如：12 16 17 24 28 29
     * @param issuen 期数
     * @return
     * Created by jl on 2021/2/27 14:40.
     */
    public SsqDetailInfoDO getSsqDetailInfoDO (String qiu,String issuen) {
        List<String> hongQiuList = Arrays.asList(qiu.split(" "));
        //排序
        Collections.sort(hongQiuList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        SsqDetailInfoDO ssqDetailInfoDO = SsqBaseInfoDOCoverter.createSsqDetailInfoDO(hongQiuList,issuen);

        return ssqDetailInfoDO;
    }

    /**
     * @Author LeJiang
     * @CreateOn 2019/6/19 ^ 下午2:41
     * @Parameter
     * @Remark
     */
    public static SsqJsBaseResponseVO getData( int num, int issueno, int caipiaoid) {
        StringBuffer url = new StringBuffer();
        Map<String,String> map = new HashMap<>();
        if (num > 0) {
            map.put("num",String.valueOf(num));
        }
        if (issueno  > 0 ) {
            map.put("issueno",String.valueOf(issueno));
        }
        map.put("caipiaoid",String.valueOf(caipiaoid));
        String json = HttpUtil.postForm(map,true, Constants.URL_HISTORY,"UTF-8");
        return new Gson().fromJson(json, SsqJsBaseResponseVO.class);
    }

}

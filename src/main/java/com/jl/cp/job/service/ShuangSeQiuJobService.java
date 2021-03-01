/**
 * alifepay.com Inc.
 * Copyright (c) 2016-2020 All Rights Reserved.
 */
package com.jl.cp.job.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.google.gson.Gson;
import com.jl.cp.constants.Constants;
import com.jl.cp.coverter.SsqBaseInfoDOCoverter;
import com.jl.cp.dto.YuCeDataDTO;
import com.jl.cp.entity.SsqBaseInfoDO;
import com.jl.cp.entity.SsqDetailInfoDO;
import com.jl.cp.mapper.SsqBaseInfoMapper;
import com.jl.cp.mapper.SsqDetailInfoMapper;
import com.jl.cp.utils.HttpUtil;
import com.jl.cp.vo.HttpConverterResponseVO.SsqBaseInfoResponseVO;
import com.jl.cp.vo.HttpConverterResponseVO.SsqJsBaseResponseVO;
import org.apache.commons.lang3.StringUtils;
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
        while (saveList.size() < 1100) {
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
        SsqBaseInfoDO querySsqBaseInfoDO = new SsqBaseInfoDO();
        querySsqBaseInfoDO.setIssueno(ssqBaseInfoResponseVO.getIssueno());
        SsqBaseInfoDO ssqBaseInfoDO = ssqBaseInfoMapper.selectOne(querySsqBaseInfoDO);

        if (ssqBaseInfoDO == null) {
            SsqBaseInfoDO insertSsqBaseInfoDO = SsqBaseInfoDOCoverter.createSsqBaseInfoDO(ssqBaseInfoResponseVO);
            ssqBaseInfoMapper.insert(insertSsqBaseInfoDO);
            SsqDetailInfoDO ssqDetailInfoDO = getSsqDetailInfoDO(insertSsqBaseInfoDO.getNumber(),insertSsqBaseInfoDO.getIssueno());
            ssqDetailInfoMapper.insert(ssqDetailInfoDO);
            getYuCeData(null);

        }

    }

    /**
      * @Author LeJiang
      * @Remark 手动获取需要查看的数据集合
      */
    public void getYuCeData (Long issueno) {
        //查询数据发送邮件
        List<SsqDetailInfoDO> ssqDetailInfoDOS = ssqDetailInfoMapper.selectListByLimit(issueno);
        String content = getContent(getYuCeDataDTO(ssqDetailInfoDOS));


        MailAccount account = new MailAccount();
        account.setHost("smtp.qq.com");
        account.setPort(25);
        account.setAuth(true);
        account.setFrom("296592231@qq.com");
        account.setUser("296592231@qq.com");
        account.setPass("wyqcrfzgduprbhec"); //密码
        MailUtil.send(account, CollUtil.newArrayList("296592231@qq.com"), "彩票6行6列预测", content, true);
    }

    public void batchInsertDetail() {
        List<SsqBaseInfoDO> baseInfoDOS = ssqBaseInfoMapper.selectAll();
        baseInfoDOS.forEach(ssqBaseInfoDO -> {
            SsqDetailInfoDO ssqDetailInfoDO = getSsqDetailInfoDO(ssqBaseInfoDO.getNumber(),ssqBaseInfoDO.getIssueno());
            ssqDetailInfoMapper.insert(ssqDetailInfoDO);
        });
    }

    private YuCeDataDTO getYuCeDataDTO(List<SsqDetailInfoDO> ssqDetailInfoDOS) {
        Collections.sort(ssqDetailInfoDOS, new Comparator<SsqDetailInfoDO>() {
            @Override
            public int compare(SsqDetailInfoDO o1, SsqDetailInfoDO o2) {
                return (o2.getIssueno() + "").compareTo((o1.getIssueno() + ""));
            }
        });

        YuCeDataDTO yuCeDataDTO = new YuCeDataDTO();
        int count = 0;
        int flagNum = 4;
        for (int i = 0 ; i < ssqDetailInfoDOS.size() ;i++) {
            if (count == flagNum) {
                flagNum = 5;
                SsqDetailInfoDO ssqDetailInfoDO = ssqDetailInfoDOS.get(i);

                yuCeDataDTO.setHangLieRed1(getStr(yuCeDataDTO.getHangLieRed1(),ssqDetailInfoDO.getAQiu()));
                yuCeDataDTO.setHangLieLuShu1(getStr(yuCeDataDTO.getHangLieLuShu1(),ssqDetailInfoDO.getAYushu()));
                yuCeDataDTO.setHangLieWuXing1(getStr(yuCeDataDTO.getHangLieWuXing1(),ssqDetailInfoDO.getAWuxing()));
                yuCeDataDTO.setHangLieDanShuang1(getStr(yuCeDataDTO.getHangLieDanShuang1(),ssqDetailInfoDO.getADanshuang()));
                yuCeDataDTO.setHangLieHeZhi1(getStr(yuCeDataDTO.getHangLieHeZhi1(),ssqDetailInfoDO.getSumValue()));
                yuCeDataDTO.setHangLieWeiHe1(getStr(yuCeDataDTO.getHangLieWeiHe1(),ssqDetailInfoDO.getTailSumValue()));
                yuCeDataDTO.setHangLieDaXiaoBi1(getStr(yuCeDataDTO.getHangLieDaXiaoBi1(),ssqDetailInfoDO.getDaxiaoRatio()));
                yuCeDataDTO.setHangLieQiOuBi1(getStr(yuCeDataDTO.getHangLieQiOuBi1(),ssqDetailInfoDO.getDanshuangRatio()));

                yuCeDataDTO.setHangLieRed2(getStr(yuCeDataDTO.getHangLieRed2(),ssqDetailInfoDO.getBQiu()));
                yuCeDataDTO.setHangLieLuShu2(getStr(yuCeDataDTO.getHangLieLuShu2(),ssqDetailInfoDO.getBYushu()));
                yuCeDataDTO.setHangLieWuXing2(getStr(yuCeDataDTO.getHangLieWuXing2(),ssqDetailInfoDO.getBWuxing()));
                yuCeDataDTO.setHangLieDanShuang2(getStr(yuCeDataDTO.getHangLieDanShuang2(),ssqDetailInfoDO.getBDanshuang()));
                yuCeDataDTO.setHangLieHeZhi2(getStr(yuCeDataDTO.getHangLieHeZhi2(),ssqDetailInfoDO.getSumValue()));
                yuCeDataDTO.setHangLieWeiHe2(getStr(yuCeDataDTO.getHangLieWeiHe2(),ssqDetailInfoDO.getTailSumValue()));
                yuCeDataDTO.setHangLieDaXiaoBi2(getStr(yuCeDataDTO.getHangLieDaXiaoBi2(),ssqDetailInfoDO.getDaxiaoRatio()));
                yuCeDataDTO.setHangLieQiOuBi2(getStr(yuCeDataDTO.getHangLieQiOuBi2(),ssqDetailInfoDO.getDanshuangRatio()));

                yuCeDataDTO.setHangLieRed3(getStr(yuCeDataDTO.getHangLieRed3(),ssqDetailInfoDO.getCQiu()));
                yuCeDataDTO.setHangLieLuShu3(getStr(yuCeDataDTO.getHangLieLuShu3(),ssqDetailInfoDO.getCYushu()));
                yuCeDataDTO.setHangLieWuXing3(getStr(yuCeDataDTO.getHangLieWuXing3(),ssqDetailInfoDO.getCWuxing()));
                yuCeDataDTO.setHangLieDanShuang3(getStr(yuCeDataDTO.getHangLieDanShuang3(),ssqDetailInfoDO.getCDanshuang()));
                yuCeDataDTO.setHangLieHeZhi3(getStr(yuCeDataDTO.getHangLieHeZhi3(),ssqDetailInfoDO.getSumValue()));
                yuCeDataDTO.setHangLieWeiHe3(getStr(yuCeDataDTO.getHangLieWeiHe3(),ssqDetailInfoDO.getTailSumValue()));
                yuCeDataDTO.setHangLieDaXiaoBi3(getStr(yuCeDataDTO.getHangLieDaXiaoBi3(),ssqDetailInfoDO.getDaxiaoRatio()));
                yuCeDataDTO.setHangLieQiOuBi3(getStr(yuCeDataDTO.getHangLieQiOuBi3(),ssqDetailInfoDO.getDanshuangRatio()));

                yuCeDataDTO.setHangLieRed4(getStr(yuCeDataDTO.getHangLieRed4(),ssqDetailInfoDO.getDQiu()));
                yuCeDataDTO.setHangLieLuShu4(getStr(yuCeDataDTO.getHangLieLuShu4(),ssqDetailInfoDO.getDYushu()));
                yuCeDataDTO.setHangLieWuXing4(getStr(yuCeDataDTO.getHangLieWuXing4(),ssqDetailInfoDO.getDWuxing()));
                yuCeDataDTO.setHangLieDanShuang4(getStr(yuCeDataDTO.getHangLieDanShuang4(),ssqDetailInfoDO.getDDanshuang()));
                yuCeDataDTO.setHangLieHeZhi4(getStr(yuCeDataDTO.getHangLieHeZhi4(),ssqDetailInfoDO.getSumValue()));
                yuCeDataDTO.setHangLieWeiHe4(getStr(yuCeDataDTO.getHangLieWeiHe4(),ssqDetailInfoDO.getTailSumValue()));
                yuCeDataDTO.setHangLieDaXiaoBi4(getStr(yuCeDataDTO.getHangLieDaXiaoBi4(),ssqDetailInfoDO.getDaxiaoRatio()));
                yuCeDataDTO.setHangLieQiOuBi4(getStr(yuCeDataDTO.getHangLieQiOuBi4(),ssqDetailInfoDO.getDanshuangRatio()));

                yuCeDataDTO.setHangLieRed5(getStr(yuCeDataDTO.getHangLieRed5(),ssqDetailInfoDO.getEQiu()));
                yuCeDataDTO.setHangLieLuShu5(getStr(yuCeDataDTO.getHangLieLuShu5(),ssqDetailInfoDO.getEYushu()));
                yuCeDataDTO.setHangLieWuXing5(getStr(yuCeDataDTO.getHangLieWuXing5(),ssqDetailInfoDO.getEWuxing()));
                yuCeDataDTO.setHangLieDanShuang5(getStr(yuCeDataDTO.getHangLieDanShuang5(),ssqDetailInfoDO.getEDanshuang()));
                yuCeDataDTO.setHangLieHeZhi5(getStr(yuCeDataDTO.getHangLieHeZhi5(),ssqDetailInfoDO.getSumValue()));
                yuCeDataDTO.setHangLieWeiHe5(getStr(yuCeDataDTO.getHangLieWeiHe5(),ssqDetailInfoDO.getTailSumValue()));
                yuCeDataDTO.setHangLieDaXiaoBi5(getStr(yuCeDataDTO.getHangLieDaXiaoBi5(),ssqDetailInfoDO.getDaxiaoRatio()));
                yuCeDataDTO.setHangLieQiOuBi5(getStr(yuCeDataDTO.getHangLieQiOuBi5(),ssqDetailInfoDO.getDanshuangRatio()));

                yuCeDataDTO.setHangLieRed6(getStr(yuCeDataDTO.getHangLieRed6(),ssqDetailInfoDO.getFQiu()));
                yuCeDataDTO.setHangLieLuShu6(getStr(yuCeDataDTO.getHangLieLuShu6(),ssqDetailInfoDO.getFYushu()));
                yuCeDataDTO.setHangLieWuXing6(getStr(yuCeDataDTO.getHangLieWuXing6(),ssqDetailInfoDO.getFWuxing()));
                yuCeDataDTO.setHangLieDanShuang6(getStr(yuCeDataDTO.getHangLieDanShuang6(),ssqDetailInfoDO.getFDanshuang()));
                yuCeDataDTO.setHangLieHeZhi6(getStr(yuCeDataDTO.getHangLieHeZhi6(),ssqDetailInfoDO.getSumValue()));
                yuCeDataDTO.setHangLieWeiHe6(getStr(yuCeDataDTO.getHangLieWeiHe6(),ssqDetailInfoDO.getTailSumValue()));
                yuCeDataDTO.setHangLieDaXiaoBi6(getStr(yuCeDataDTO.getHangLieDaXiaoBi6(),ssqDetailInfoDO.getDaxiaoRatio()));
                yuCeDataDTO.setHangLieQiOuBi6(getStr(yuCeDataDTO.getHangLieQiOuBi6(),ssqDetailInfoDO.getDanshuangRatio()));
                count = 0;
            }
            count++;
        }
        return yuCeDataDTO;
    }

    /**
     *
     * @param str1 已设置的红球
     * @param str2 需要拼接的红球
     * @return
     * Created by jl on 2021/2/27 17:34.
     */
    public String getStr (String str1,String str2) {
        if (StringUtils.isBlank(str1)) {
            return str2;
        }
        return str1 + "," + str2;
    }

    public String getContent (YuCeDataDTO yuCeDataDTO) {
        StringBuilder content = new StringBuilder();
        content.append("<html><head><style type=\"text/css\">");
        content.append("table{border-collapse: collapse;text-align: center;table-layout: fixed;width: 1080px;}");
        content.append("table td, table th{border: 1px solid #cad9ea;color: #666;height: 30px;}");
        content.append("table thead th{background-color: #CCE8EB;width: 120px;}");
        content.append("table tr:nth-child(odd){background: #fff;}");
        content.append("table tr:nth-child(even){background: #F5FAFA;}");
        content.append("</style></head><body>");
        content.append("<h3>拉取结果汇总</h3><table style=\"text-align: left;width: 500px;\">");
        content.append("<tr><td>6行6列每五期数据，红球1</td><td>"+yuCeDataDTO.getHangLieRed1()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球1，012路</td><td>"+yuCeDataDTO.getHangLieLuShu1()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球1，五行</td><td>"+yuCeDataDTO.getHangLieWuXing1()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球1，单双</td><td>"+yuCeDataDTO.getHangLieDanShuang1()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球1，和值</td><td>"+yuCeDataDTO.getHangLieHeZhi1()+"</td></tr>");

        content.append("<tr><td>6行6列每五期数据，红球2</td><td>"+yuCeDataDTO.getHangLieRed2()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球2，012路</td><td>"+yuCeDataDTO.getHangLieLuShu2()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球2，五行</td><td>"+yuCeDataDTO.getHangLieWuXing2()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球2，单双</td><td>"+yuCeDataDTO.getHangLieDanShuang2()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球2，和值</td><td>"+yuCeDataDTO.getHangLieHeZhi2()+"</td></tr>");

        content.append("<tr><td>6行6列每五期数据，红球3</td><td>"+yuCeDataDTO.getHangLieRed3()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球3，012路</td><td>"+yuCeDataDTO.getHangLieLuShu3()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球3，五行</td><td>"+yuCeDataDTO.getHangLieWuXing3()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球3，单双</td><td>"+yuCeDataDTO.getHangLieDanShuang3()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球3，和值</td><td>"+yuCeDataDTO.getHangLieHeZhi3()+"</td></tr>");

        content.append("<tr><td>6行6列每五期数据，红球4</td><td>"+yuCeDataDTO.getHangLieRed4()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球4，012路</td><td>"+yuCeDataDTO.getHangLieLuShu4()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球4，五行</td><td>"+yuCeDataDTO.getHangLieWuXing4()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球4，单双</td><td>"+yuCeDataDTO.getHangLieDanShuang4()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球4，和值</td><td>"+yuCeDataDTO.getHangLieHeZhi4()+"</td></tr>");

        content.append("<tr><td>6行6列每五期数据，红球5</td><td>"+yuCeDataDTO.getHangLieRed5()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球5，012路</td><td>"+yuCeDataDTO.getHangLieLuShu5()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球5，五行</td><td>"+yuCeDataDTO.getHangLieWuXing5()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球5，单双</td><td>"+yuCeDataDTO.getHangLieDanShuang5()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球5，和值</td><td>"+yuCeDataDTO.getHangLieHeZhi5()+"</td></tr>");

        content.append("<tr><td>6行6列每五期数据，红球6</td><td>"+yuCeDataDTO.getHangLieRed6()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球6，012路</td><td>"+yuCeDataDTO.getHangLieLuShu6()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球6，五行</td><td>"+yuCeDataDTO.getHangLieWuXing6()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球6，单双</td><td>"+yuCeDataDTO.getHangLieDanShuang6()+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球6，和值</td><td>"+yuCeDataDTO.getHangLieHeZhi6()+"</td></tr>");

        content.append("</table></body></html>");
        return content.toString();
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


    public static void main(String[] args) {
        MailAccount account = new MailAccount();
        account.setHost("smtp.qq.com");
        account.setPort(25);
        account.setAuth(true);
        account.setFrom("296592231@qq.com");
        account.setUser("296592231@qq.com");
        account.setPass("wyqcrfzgduprbhec"); //密码
        MailUtil.send(account, CollUtil.newArrayList("296592231@qq.com"), "彩票6行6列预测", "测试", true);
    }

}

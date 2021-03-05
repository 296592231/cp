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
            getYuCeData(Long.valueOf(ssqDetailInfoDO.getIssueno()),ssqDetailInfoDO.getSanSection());
        }

    }

    /**
      * @Author LeJiang
      * @Remark 手动获取需要查看的数据集合
      */
    public void getYuCeData (Long issueno,String sanSection) {
        //查询数据发送邮件
        List<SsqDetailInfoDO> ssqDetailInfoDOS = ssqDetailInfoMapper.selectListByLimit(issueno);

        //查看以往三区间分布
        Map<String,Object> map = new HashMap<>();
        map.put("issueno",issueno);
        map.put("sanSection",sanSection);
        List<SsqDetailInfoDO> sectionStaDos = ssqDetailInfoMapper.findListByIdAddOne(map);


        String content = getContent(getYuCeDataDTO(ssqDetailInfoDOS),getHistorySanSection(sectionStaDos));

        MailAccount account = new MailAccount();
        account.setHost("smtp.qq.com");
        account.setPort(25);
        account.setAuth(true);
        account.setFrom("296592231@qq.com");
        account.setUser("296592231@qq.com");
        //密码
        account.setPass("wyqcrfzgduprbhec");
        MailUtil.send(account, CollUtil.newArrayList("296592231@qq.com"), "彩票6行6列预测", content, true);
    }

    /**
     * 获取历史三区间分布
     * @param
     * @return
     * Created by jl on 2021/3/5 13:53.
     */
    private YuCeDataDTO getHistorySanSection(List<SsqDetailInfoDO> sectionStaDos) {
        if (CollectionUtil.isEmpty(sectionStaDos)) {
            return null;
        }
        Collections.sort(sectionStaDos, new Comparator<SsqDetailInfoDO>() {
            @Override
            public int compare(SsqDetailInfoDO o1, SsqDetailInfoDO o2) {
                return (o2.getIssueno() + "").compareTo((o1.getIssueno() + ""));
            }
        });
        YuCeDataDTO yuCeDataDTO = new YuCeDataDTO();
        for (SsqDetailInfoDO ssqDetailInfoDO : sectionStaDos) {
            yuCeDataDTO.setDaXiaoBi(getStr(yuCeDataDTO.getDaXiaoBi(),ssqDetailInfoDO.getDaxiaoRatio()));
            yuCeDataDTO.setHeZhi(getStr(yuCeDataDTO.getHeZhi(),ssqDetailInfoDO.getSumValue()));
            yuCeDataDTO.setQiOuBi(getStr(yuCeDataDTO.getQiOuBi(),ssqDetailInfoDO.getDanshuangRatio()));
            yuCeDataDTO.setWeiHe(getStr(yuCeDataDTO.getWeiHe(),ssqDetailInfoDO.getTailSumValue()));
            yuCeDataDTO.setSanSection(getStr(yuCeDataDTO.getSanSection(),ssqDetailInfoDO.getSanSection()));
        }
        return yuCeDataDTO;
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

                yuCeDataDTO.setHangLieRed2(getStr(yuCeDataDTO.getHangLieRed2(),ssqDetailInfoDO.getBQiu()));
                yuCeDataDTO.setHangLieLuShu2(getStr(yuCeDataDTO.getHangLieLuShu2(),ssqDetailInfoDO.getBYushu()));
                yuCeDataDTO.setHangLieWuXing2(getStr(yuCeDataDTO.getHangLieWuXing2(),ssqDetailInfoDO.getBWuxing()));
                yuCeDataDTO.setHangLieDanShuang2(getStr(yuCeDataDTO.getHangLieDanShuang2(),ssqDetailInfoDO.getBDanshuang()));

                yuCeDataDTO.setHangLieRed3(getStr(yuCeDataDTO.getHangLieRed3(),ssqDetailInfoDO.getCQiu()));
                yuCeDataDTO.setHangLieLuShu3(getStr(yuCeDataDTO.getHangLieLuShu3(),ssqDetailInfoDO.getCYushu()));
                yuCeDataDTO.setHangLieWuXing3(getStr(yuCeDataDTO.getHangLieWuXing3(),ssqDetailInfoDO.getCWuxing()));
                yuCeDataDTO.setHangLieDanShuang3(getStr(yuCeDataDTO.getHangLieDanShuang3(),ssqDetailInfoDO.getCDanshuang()));

                yuCeDataDTO.setHangLieRed4(getStr(yuCeDataDTO.getHangLieRed4(),ssqDetailInfoDO.getDQiu()));
                yuCeDataDTO.setHangLieLuShu4(getStr(yuCeDataDTO.getHangLieLuShu4(),ssqDetailInfoDO.getDYushu()));
                yuCeDataDTO.setHangLieWuXing4(getStr(yuCeDataDTO.getHangLieWuXing4(),ssqDetailInfoDO.getDWuxing()));
                yuCeDataDTO.setHangLieDanShuang4(getStr(yuCeDataDTO.getHangLieDanShuang4(),ssqDetailInfoDO.getDDanshuang()));

                yuCeDataDTO.setHangLieRed5(getStr(yuCeDataDTO.getHangLieRed5(),ssqDetailInfoDO.getEQiu()));
                yuCeDataDTO.setHangLieLuShu5(getStr(yuCeDataDTO.getHangLieLuShu5(),ssqDetailInfoDO.getEYushu()));
                yuCeDataDTO.setHangLieWuXing5(getStr(yuCeDataDTO.getHangLieWuXing5(),ssqDetailInfoDO.getEWuxing()));
                yuCeDataDTO.setHangLieDanShuang5(getStr(yuCeDataDTO.getHangLieDanShuang5(),ssqDetailInfoDO.getEDanshuang()));

                yuCeDataDTO.setHangLieRed6(getStr(yuCeDataDTO.getHangLieRed6(),ssqDetailInfoDO.getFQiu()));
                yuCeDataDTO.setHangLieLuShu6(getStr(yuCeDataDTO.getHangLieLuShu6(),ssqDetailInfoDO.getFYushu()));
                yuCeDataDTO.setHangLieWuXing6(getStr(yuCeDataDTO.getHangLieWuXing6(),ssqDetailInfoDO.getFWuxing()));
                yuCeDataDTO.setHangLieDanShuang6(getStr(yuCeDataDTO.getHangLieDanShuang6(),ssqDetailInfoDO.getFDanshuang()));
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

    public String getContent (YuCeDataDTO yuCeDataDTO,YuCeDataDTO historyYuCeDataDTO) {
        StringBuilder content = new StringBuilder();
        content.append("<html><head><style type=\"text/css\">");
        content.append("table{border-collapse: collapse;text-align: center;table-layout: fixed;width: 1080px;}");
        content.append("table td, table th{border: 1px solid #cad9ea;color: #666;height: 30px;}");
        content.append("table thead th{background-color: #CCE8EB;width: 120px;}");
        content.append("table tr:nth-child(odd){background: #fff;}");
        content.append("table tr:nth-child(even){background: #F5FAFA;}");
        content.append("</style></head><body>");
        content.append("<h3>拉取结果汇总</h3><table style=\"text-align: left;width: 500px;\">");
        content.append("<tr><td>6行6列每五期数据，红球1</td><td>"+printConvert(yuCeDataDTO.getHangLieRed1()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球1，012路</td><td>"+printConvert(yuCeDataDTO.getHangLieLuShu1()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球1，五行</td><td>"+printConvert(yuCeDataDTO.getHangLieWuXing1()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球1，单双</td><td>"+printConvert(yuCeDataDTO.getHangLieDanShuang1()).replaceAll(",","，")+"</td></tr>");

        content.append("<tr><td>6行6列每五期数据，红球2</td><td>"+printConvert(yuCeDataDTO.getHangLieRed2()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球2，012路</td><td>"+printConvert(yuCeDataDTO.getHangLieLuShu2()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球2，五行</td><td>"+printConvert(yuCeDataDTO.getHangLieWuXing2()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球2，单双</td><td>"+printConvert(yuCeDataDTO.getHangLieDanShuang2()).replaceAll(",","，")+"</td></tr>");

        content.append("<tr><td>6行6列每五期数据，红球3</td><td>"+printConvert(yuCeDataDTO.getHangLieRed3()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球3，012路</td><td>"+printConvert(yuCeDataDTO.getHangLieLuShu3()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球3，五行</td><td>"+printConvert(yuCeDataDTO.getHangLieWuXing3()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球3，单双</td><td>"+printConvert(yuCeDataDTO.getHangLieDanShuang3()).replaceAll(",","，")+"</td></tr>");

        content.append("<tr><td>6行6列每五期数据，红球4</td><td>"+printConvert(yuCeDataDTO.getHangLieRed4()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球4，012路</td><td>"+printConvert(yuCeDataDTO.getHangLieLuShu4()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球4，五行</td><td>"+printConvert(yuCeDataDTO.getHangLieWuXing4()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球4，单双</td><td>"+printConvert(yuCeDataDTO.getHangLieDanShuang4()).replaceAll(",","，")+"</td></tr>");

        content.append("<tr><td>6行6列每五期数据，红球5</td><td>"+printConvert(yuCeDataDTO.getHangLieRed5()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球5，012路</td><td>"+printConvert(yuCeDataDTO.getHangLieLuShu5()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球5，五行</td><td>"+printConvert(yuCeDataDTO.getHangLieWuXing5()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球5，单双</td><td>"+printConvert(yuCeDataDTO.getHangLieDanShuang5()).replaceAll(",","，")+"</td></tr>");

        content.append("<tr><td>6行6列每五期数据，红球6</td><td>"+printConvert(yuCeDataDTO.getHangLieRed6()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球6，012路</td><td>"+printConvert(yuCeDataDTO.getHangLieLuShu6()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球6，五行</td><td>"+printConvert(yuCeDataDTO.getHangLieWuXing6()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>行6列每五期数据，红球6，单双</td><td>"+printConvert(yuCeDataDTO.getHangLieDanShuang6()).replaceAll(",","，")+"</td></tr>");


        content.append("<tr><td>历史数据分布，和值</td><td>"+printConvert(historyYuCeDataDTO.getHeZhi()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>历史数据分布，尾和</td><td>"+printConvert(historyYuCeDataDTO.getWeiHe()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>历史数据分布，大小比</td><td>"+printConvert(historyYuCeDataDTO.getDaXiaoBi()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>历史数据分布，奇偶比</td><td>"+printConvert(historyYuCeDataDTO.getQiOuBi()).replaceAll(",","，")+"</td></tr>");
        content.append("<tr><td>历史数据分布，三区间比</td><td>"+printConvert(historyYuCeDataDTO.getSanSection()).replaceAll(",","，")+"</td></tr>");

        content.append("</table></body></html>");
        return content.toString();
    }

    /**
     * 将从右至左看改成从左至右看
     * @param
     * @return
     * Created by jl on 2021/3/5 14:20.
     */
    public String printConvert (String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }

        String[] strs = str.split(",");

        StringBuffer sb = new StringBuffer();
        for (int i = strs.length-1 ; i >= 0 ;i--) {
            if (StringUtils.isBlank(sb.toString())) {
                sb.append(strs[i]);
            } else {
                sb.append(","+strs[i]);
            }
        }

        return sb.toString();

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
//        MailAccount account = new MailAccount();
//        account.setHost("smtp.qq.com");
//        account.setPort(25);
//        account.setAuth(true);
//        account.setFrom("296592231@qq.com");
//        account.setUser("296592231@qq.com");
//        account.setPass("wyqcrfzgduprbhec"); //密码
//        MailUtil.send(account, CollUtil.newArrayList("296592231@qq.com"), "彩票6行6列预测", "测试", true);

        //System.out.println(printConvert("01,02,03,04,05,06"));
    }


}

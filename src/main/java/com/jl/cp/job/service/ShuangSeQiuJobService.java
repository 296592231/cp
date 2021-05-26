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
import com.jl.cp.dto.StatYuShuDTO;
import com.jl.cp.dto.SumValueDTO;
import com.jl.cp.dto.YuCeDataDTO;
import com.jl.cp.entity.SsqBaseInfoDO;
import com.jl.cp.entity.SsqDetailInfoDO;
import com.jl.cp.entity.SsqYuCeDO;
import com.jl.cp.entity.biz.SsqBaseInfoBizDO;
import com.jl.cp.mapper.SsqBaseInfoMapper;
import com.jl.cp.mapper.SsqDetailInfoMapper;
import com.jl.cp.mapper.SsqYuCeMapper;
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

    @Autowired
    private SsqYuCeMapper ssqYuCeMapper;

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
            getYuCeData(ssqDetailInfoDO);
        }

    }

    /**
      * @Author LeJiang
      * @Remark 手动获取需要查看的数据集合
      */
    public void getYuCeData (SsqDetailInfoDO ssqDetailInfoDO) {

        String content = getContent(forecas(ssqDetailInfoDO));

        MailAccount account = new MailAccount();
        account.setHost("smtp.qq.com");
        account.setPort(25);
        account.setAuth(true);
        account.setFrom("296592231@qq.com");
        account.setUser("296592231@qq.com");
        //密码
        account.setPass("wyqcrfzgduprbhec");
        MailUtil.send(account, CollUtil.newArrayList("296592231@qq.com"), "彩票012路加三区间加和值加尾和预测", content, true);
    }

    /**
     * 篮球预测
     * @param issueno 当前期数
     * @return
     * Created by jl on 2021/5/8 15:23.
     */
    public String yuCeBlue (String issueno) {
        //查询数据是否存在
        SsqBaseInfoDO querySsqBaseInfoDO = new SsqBaseInfoDO();
        querySsqBaseInfoDO.setIssueno(issueno);
        SsqBaseInfoDO ssqBaseInfoDO = ssqBaseInfoMapper.selectOne(querySsqBaseInfoDO);
        if (ssqBaseInfoDO != null) {
            Long id = ssqBaseInfoDO.getId() -100L;
            SsqBaseInfoDO querySsqBaseInfoDO1 = new SsqBaseInfoDO();
            querySsqBaseInfoDO1.setId(id);
            SsqBaseInfoDO restSsqBaseInfoDO = ssqBaseInfoMapper.selectOne(querySsqBaseInfoDO1);
            if (restSsqBaseInfoDO!= null) {
                Map<String,Object> map = new HashMap<>();
                map.put("statIssueno",restSsqBaseInfoDO.getIssueno());
                map.put("endIssueno",issueno);
                map.put("refernumber",ssqBaseInfoDO.getRefernumber());
                List<SsqBaseInfoBizDO> ssqBaseInfoDOS = ssqBaseInfoMapper.findListByMap(map);
                if (CollectionUtil.isNotEmpty(ssqBaseInfoDOS)) {
                    StringBuffer blueRule = new StringBuffer();
                    StringBuffer blueNumberRule = new StringBuffer();
                    ssqBaseInfoDOS.forEach(ssqBaseInfoBizDO -> {
                        String s = ssqBaseInfoBizDO.getRefernumber();
                        String s1 = ssqBaseInfoBizDO.getNumber();
                        if (StringUtils.isBlank(blueRule.toString())) {
                            blueRule.append(s);
                        } else {
                            blueRule.append("," + s);
                        }
                        if (StringUtils.isBlank(blueNumberRule.toString())) {
                            blueNumberRule.append(s1);
                        } else {
                            blueNumberRule.append("," + s1);
                        }
                    });
                    return issueno + "：" + blueRule.toString() + "--" + blueNumberRule.toString();
                }
            }
        }
        return null;
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

    public String getContent (String yuce) {
        StringBuilder content = new StringBuilder();
        content.append("<html><head><style type=\"text/css\">");
        content.append("table{border-collapse: collapse;text-align: center;table-layout: fixed;width: 1080px;}");
        content.append("table td, table th{border: 1px solid #cad9ea;color: #666;height: 30px;}");
        content.append("table thead th{background-color: #CCE8EB;width: 120px;}");
        content.append("table tr:nth-child(odd){background: #fff;}");
        content.append("table tr:nth-child(even){background: #F5FAFA;}");
        content.append("</style></head><body>");
        content.append("<h3>预测结果</h3><table style=\"text-align: left;width: 500px;\">");
        content.append("<tr><td>预测结果</td><td>"+yuce+"</td></tr>");
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


    /**
     * 双色球预测
     * @param ssqDetailInfoDO 最新详情数据
     * @return
     * Created by jl on 2021/5/26 17:29.
     */

    public String forecas (SsqDetailInfoDO ssqDetailInfoDO) {
        //==============================================查当前===============================================================
        Map<String,Object> paremMap = new HashMap<>();
        //查A余数 得到一个集合排序最大的在第一条
        paremMap.clear();
        paremMap.put("issueno",ssqDetailInfoDO.getIssueno());
        paremMap.put("columnName","a_yushu");
        List<StatYuShuDTO> aStatYuShuDTOS = ssqDetailInfoMapper.currentStatYuShuInfo(paremMap);

        //查B余数
        paremMap.clear();
        paremMap.put("issueno",ssqDetailInfoDO.getIssueno());
        paremMap.put("columnName","b_yushu");
        List<StatYuShuDTO> bStatYuShuDTOS = ssqDetailInfoMapper.currentStatYuShuInfo(paremMap);

        //查C余数
        paremMap.clear();
        paremMap.put("issueno",ssqDetailInfoDO.getIssueno());
        paremMap.put("columnName","c_yushu");
        List<StatYuShuDTO> cStatYuShuDTOS = ssqDetailInfoMapper.currentStatYuShuInfo(paremMap);

        //查D余数
        paremMap.clear();
        paremMap.put("issueno",ssqDetailInfoDO.getIssueno());
        paremMap.put("columnName","d_yushu");
        List<StatYuShuDTO> dStatYuShuDTOS = ssqDetailInfoMapper.currentStatYuShuInfo(paremMap);

        //查E余数
        paremMap.clear();
        paremMap.put("issueno",ssqDetailInfoDO.getIssueno());
        paremMap.put("columnName","e_yushu");
        List<StatYuShuDTO> eStatYuShuDTOS = ssqDetailInfoMapper.currentStatYuShuInfo(paremMap);

        //查F余数
        paremMap.clear();
        paremMap.put("issueno",ssqDetailInfoDO.getIssueno());
        paremMap.put("columnName","f_yushu");
        List<StatYuShuDTO> fStatYuShuDTOS = ssqDetailInfoMapper.currentStatYuShuInfo(paremMap);




        //==============================================查历史===============================================================

        //查历史余数统计
        paremMap.clear();
        paremMap.put("issueno",ssqDetailInfoDO.getIssueno());
        paremMap.put("columnName","a_qiu");
        paremMap.put("columnValue",ssqDetailInfoDO.getAQiu());
        paremMap.put("columnNameYuShu","a_yushu");
        List<StatYuShuDTO> aHistoryStatYuShuDTOS = ssqDetailInfoMapper.statYuShuInfo(paremMap);
        String aYuCe = getYuCeData(aStatYuShuDTOS,aHistoryStatYuShuDTOS,ssqDetailInfoDO.getIssueno());

        paremMap.clear();
        paremMap.put("issueno",ssqDetailInfoDO.getIssueno());
        paremMap.put("columnName","b_qiu");
        paremMap.put("columnValue",ssqDetailInfoDO.getBQiu());
        paremMap.put("columnNameYuShu","b_yushu");
        List<StatYuShuDTO> bHistoryStatYuShuDTOS = ssqDetailInfoMapper.statYuShuInfo(paremMap);
        String bYuCe = getYuCeData(bStatYuShuDTOS,bHistoryStatYuShuDTOS,ssqDetailInfoDO.getIssueno());

        paremMap.clear();
        paremMap.put("issueno",ssqDetailInfoDO.getIssueno());
        paremMap.put("columnName","c_qiu");
        paremMap.put("columnValue",ssqDetailInfoDO.getCQiu());
        paremMap.put("columnNameYuShu","c_yushu");
        List<StatYuShuDTO> cHistoryStatYuShuDTOS = ssqDetailInfoMapper.statYuShuInfo(paremMap);
        String cYuCe = getYuCeData(cStatYuShuDTOS,cHistoryStatYuShuDTOS,ssqDetailInfoDO.getIssueno());

        paremMap.clear();
        paremMap.put("issueno",ssqDetailInfoDO.getIssueno());
        paremMap.put("columnName","d_qiu");
        paremMap.put("columnValue",ssqDetailInfoDO.getDQiu());
        paremMap.put("columnNameYuShu","d_yushu");
        List<StatYuShuDTO> dHistoryStatYuShuDTOS = ssqDetailInfoMapper.statYuShuInfo(paremMap);
        String dYuCe = getYuCeData(dStatYuShuDTOS,dHistoryStatYuShuDTOS,ssqDetailInfoDO.getIssueno());

        paremMap.clear();
        paremMap.put("issueno",ssqDetailInfoDO.getIssueno());
        paremMap.put("columnName","e_qiu");
        paremMap.put("columnValue",ssqDetailInfoDO.getEQiu());
        paremMap.put("columnNameYuShu","e_yushu");
        List<StatYuShuDTO> eHistoryStatYuShuDTOS = ssqDetailInfoMapper.statYuShuInfo(paremMap);
        String eYuCe = getYuCeData(eStatYuShuDTOS,eHistoryStatYuShuDTOS,ssqDetailInfoDO.getIssueno());

        paremMap.clear();
        paremMap.put("issueno",ssqDetailInfoDO.getIssueno());
        paremMap.put("columnName","f_qiu");
        paremMap.put("columnValue",ssqDetailInfoDO.getFQiu());
        paremMap.put("columnNameYuShu","f_yushu");
        List<StatYuShuDTO> fHistoryStatYuShuDTOS = ssqDetailInfoMapper.statYuShuInfo(paremMap);
        String fYuCe = getYuCeData(fStatYuShuDTOS,fHistoryStatYuShuDTOS,ssqDetailInfoDO.getIssueno());


        //查尾和
        paremMap.clear();
        paremMap.put("issueno",ssqDetailInfoDO.getIssueno());
        paremMap.put("maxValue",ssqDetailInfoDO.getTailSumValue());
        SumValueDTO tailSumValueDTO = ssqDetailInfoMapper.sumTailSumValue(paremMap);
        int tailSanMax = tailSumValueDTO.getSumAvgValue() + 15;
        int tailSanMin = tailSumValueDTO.getSumAvgValue() - 5;


        //查总和
        paremMap.clear();
        paremMap.put("issueno",ssqDetailInfoDO.getIssueno());
        paremMap.put("minValue",Integer.parseInt(ssqDetailInfoDO.getSumValue()) - 10);
        paremMap.put("maxValue",Integer.parseInt(ssqDetailInfoDO.getSumValue()) + 10);
        SumValueDTO sumValueDTO = ssqDetailInfoMapper.sumSumValue(paremMap);
        int sanMax = sumValueDTO.getSumAvgValue() + 25;
        int sanMin = sumValueDTO.getSumAvgValue() - 25;

        //查三区间
        paremMap.clear();
        paremMap.put("issueno",ssqDetailInfoDO.getIssueno());
        paremMap.put("sanSection",ssqDetailInfoDO.getSanSection());
        List<String> sanSectionList = ssqDetailInfoMapper.getSanSectionList(paremMap);

        StringBuffer sb = new StringBuffer();
        sb.append("a:【"+aYuCe+"】");
        sb.append("，b:【"+bYuCe+"】");
        sb.append("，c:【"+cYuCe+"】");
        sb.append("，d:【"+dYuCe+"】");
        sb.append("，e:【"+eYuCe+"】");
        sb.append("，f:【"+fYuCe+"】");
        sb.append("=======总和:【"+ sanMin +"~"+ sanMax +"】");
        sb.append("，尾和:【"+tailSanMin +"~"+ tailSanMax +"】");
        sb.append("，三区间:【"+new Gson().toJson(sanSectionList)+"】");

        Map<String,List<StatYuShuDTO>> stringListMap = new HashMap<>();
        stringListMap.put("aStatYuShuDTOS",aStatYuShuDTOS);
        stringListMap.put("aHistoryStatYuShuDTOS",aHistoryStatYuShuDTOS);
        stringListMap.put("bStatYuShuDTOS",bStatYuShuDTOS);
        stringListMap.put("bHistoryStatYuShuDTOS",bHistoryStatYuShuDTOS);
        stringListMap.put("cStatYuShuDTOS",cStatYuShuDTOS);
        stringListMap.put("cHistoryStatYuShuDTOS",cHistoryStatYuShuDTOS);
        stringListMap.put("dStatYuShuDTOS",dStatYuShuDTOS);
        stringListMap.put("dHistoryStatYuShuDTOS",dHistoryStatYuShuDTOS);
        stringListMap.put("eStatYuShuDTOS",eStatYuShuDTOS);
        stringListMap.put("eHistoryStatYuShuDTOS",eHistoryStatYuShuDTOS);
        stringListMap.put("fStatYuShuDTOS",fStatYuShuDTOS);
        stringListMap.put("fHistoryStatYuShuDTOS",fHistoryStatYuShuDTOS);

        SsqYuCeDO ssqYuCeDO = new SsqYuCeDO();

        SsqDetailInfoDO ssqDetailInfoDO1 = new SsqDetailInfoDO();
        if (ssqDetailInfoDO.getId() != null) {
            ssqDetailInfoDO1.setId(ssqDetailInfoDO.getId() + 1);
            SsqDetailInfoDO querySsqDetailInfoDO = ssqDetailInfoMapper.selectOne(ssqDetailInfoDO1);
            if (querySsqDetailInfoDO != null) {
                StringBuffer ssb = new StringBuffer();
                ssb.append(querySsqDetailInfoDO.getAYushu() + "," + querySsqDetailInfoDO.getBYushu() + "," + querySsqDetailInfoDO.getCYushu() + "," + querySsqDetailInfoDO.getDYushu() + "," + querySsqDetailInfoDO.getEYushu() + "," + querySsqDetailInfoDO.getFYushu());
                ssb.append("--尾和:" + querySsqDetailInfoDO.getTailSumValue());
                ssb.append("--总和:" + querySsqDetailInfoDO.getSumValue());
                ssb.append("--三区间:" + querySsqDetailInfoDO.getSanSection());

                ssqYuCeDO.setIsInBody(ssb.toString());
                int isIn = 0;
                if (aYuCe.contains(querySsqDetailInfoDO.getAYushu())) {
                    isIn += 1;
                }
                if (bYuCe.contains(querySsqDetailInfoDO.getBYushu())) {
                    isIn += 1;
                }
                if (cYuCe.contains(querySsqDetailInfoDO.getCYushu())) {
                    isIn += 1;
                }
                if (dYuCe.contains(querySsqDetailInfoDO.getDYushu())) {
                    isIn += 1;
                }
                if (eYuCe.contains(querySsqDetailInfoDO.getEYushu())) {
                    isIn += 1;
                }
                if (fYuCe.contains(querySsqDetailInfoDO.getFYushu())) {
                    isIn += 1;
                }

                StringBuffer isin = new StringBuffer();
                isin.append(isIn);
                int tailSumValue = Integer.parseInt(querySsqDetailInfoDO.getTailSumValue());
                if (tailSumValue >= tailSanMin && tailSumValue <= tailSanMax) {
                    isin.append("--在尾和区间");
                }

                int sumValue = Integer.parseInt(querySsqDetailInfoDO.getSumValue());
                if (sumValue >= sanMin && sumValue <= sanMax) {
                    isin.append("--在总和区间");
                }

                if (CollectionUtil.isNotEmpty(sanSectionList)) {
                    sanSectionList.forEach(s -> {
                        if (s.equals(querySsqDetailInfoDO.getSanSection())) {
                            isin.append("--在三区间");
                        }
                    });
                }

                ssqYuCeDO.setIsin(isin.toString());

            }
        }


        ssqYuCeDO.setYuCeData(sb.toString());
        ssqYuCeDO.setBody(new Gson().toJson(stringListMap));
        ssqYuCeDO.setOdds(ssqDetailInfoDO.getIssueno());
        ssqYuCeMapper.insert(ssqYuCeDO);

        return sb.toString();

    }


    /**
     * 测试双色球预测
     * @param
     * @return
     * Created by jl on 2021/5/19 14:44.
     */
    public void testForecast () {

        Map<String,Object> paremMap = new HashMap<>();
        List<SsqDetailInfoDO> allSsqDetailInfoDOS = ssqDetailInfoMapper.selectListByLimit(paremMap);

        if (CollectionUtil.isNotEmpty(allSsqDetailInfoDOS)) {

            for (SsqDetailInfoDO ssqDetailInfoDO : allSsqDetailInfoDOS) {
                forecas(ssqDetailInfoDO);
            }

        }
    }
    
    /**
     * 
     * @param statYuShuDTOS 当前统计数据
     * @param historyStatYuShuDTOS 当前统计数据
     * @return 返回 预测的0,1,2路
     * Created by jl on 2021/5/26 11:28.
     */
    public String getYuCeData (List<StatYuShuDTO> statYuShuDTOS,List<StatYuShuDTO> historyStatYuShuDTOS,String issueno) {
        try {
            Integer currentMaxYuShu = statYuShuDTOS.get(0).getYuShu();
            Integer currentMaxYuShuNum = statYuShuDTOS.get(0).getCountYuShu();
            Integer currentMinYuShu = statYuShuDTOS.get(2).getYuShu();
            Integer currentMinYuShuNum = statYuShuDTOS.get(2).getCountYuShu();
            Integer currentAvgYuShu = statYuShuDTOS.get(1).getYuShu();
            Integer currentAvgYuShuNum = statYuShuDTOS.get(1).getCountYuShu();
            Integer historyMaxYuShu = historyStatYuShuDTOS.get(0).getYuShu();
            Integer historyMaxYuShuNum = historyStatYuShuDTOS.get(0).getCountYuShu();
            Integer historyMinYuShu = historyStatYuShuDTOS.get(2).getYuShu();
            Integer historyMinYuShuNum = historyStatYuShuDTOS.get(2).getCountYuShu();
            Integer historyAvgYuShu = historyStatYuShuDTOS.get(1).getYuShu();
            Integer historyAvgYuShuNum = historyStatYuShuDTOS.get(1).getCountYuShu();
            return getYuCeData(currentMaxYuShu,currentMaxYuShuNum,currentMinYuShu,currentMinYuShuNum,currentAvgYuShu,currentAvgYuShuNum,historyMaxYuShu,historyMaxYuShuNum,historyMinYuShu,historyMinYuShuNum,historyAvgYuShu,historyAvgYuShuNum);
        } catch (Exception e) {
            System.out.println(issueno + "参数" + new Gson().toJson(statYuShuDTOS) + "," + new Gson().toJson(historyStatYuShuDTOS));
        }

        return "";
    }

    /**
     * 获取预测值
     * @param currentMaxYuShu 最大余数 路数
     * @param currentMaxYuShuNum 最大余数数值
     * @param currentMinYuShu 最小余数数值
     * @param currentMinYuShuNum 最小余数数值
     * @param currentAvgYuShu 最平均余数数值
     * @param currentAvgYuShuNum 最平均余数数值
     * @param historyMaxYuShu 历史最大余数 路数
     * @param historyMaxYuShuNum 历史最大余数数值
     * @param historyMinYuShu 历史最小余数数值
     * @param historyMinYuShuNum 历史最小余数数值
     * @param historyAvgYuShu 历史最平均余数数值
     * @param historyAvgYuShuNum 历史最平均余数数值
     * @return 返回 预测的0,1,2路
     * Created by jl on 2021/5/26 10:28.
     */
    public String getYuCeData (Integer currentMaxYuShu,
                               Integer currentMaxYuShuNum,
                               Integer currentMinYuShu,
                               Integer currentMinYuShuNum,
                               Integer currentAvgYuShu,
                               Integer currentAvgYuShuNum,
                               Integer historyMaxYuShu,
                               Integer historyMaxYuShuNum,
                               Integer historyMinYuShu,
                               Integer historyMinYuShuNum,
                               Integer historyAvgYuShu,
                               Integer historyAvgYuShuNum) {

        StringBuffer sb = new StringBuffer();

        //拿0跟三个当前的比较看谁是0
        if (currentMaxYuShu.equals(historyMaxYuShu)) {
            sb.append(currentMaxYuShu);
            if (currentAvgYuShuNum.equals(historyAvgYuShuNum)) {
                sb.append("," + currentAvgYuShu);
            } else {
                if (currentAvgYuShuNum > historyAvgYuShuNum) {
                    sb.append("," + currentAvgYuShu);
                } else if (currentAvgYuShuNum.equals(historyAvgYuShuNum)) {
                    sb.append("," + currentAvgYuShu);
                } else {
                    sb.append("," + historyAvgYuShu);
                }
            }
        } else {
            sb.append(currentMaxYuShu + "," + historyMaxYuShu);
        }
        return sb.toString();

    }
}

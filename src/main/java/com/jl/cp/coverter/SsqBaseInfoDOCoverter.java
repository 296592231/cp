/**
 * alifepay.com Inc.
 * Copyright (c) 2016-2020 All Rights Reserved.
 */
package com.jl.cp.coverter;

import com.google.gson.Gson;
import com.jl.cp.constants.Constants;
import com.jl.cp.entity.SsqBaseInfoDO;
import com.jl.cp.entity.SsqDetailInfoDO;
import com.jl.cp.enums.DaXiaoStatusEnum;
import com.jl.cp.enums.DanShuangStatusEnum;
import com.jl.cp.vo.HttpConverterResponseVO.SsqBaseInfoResponseVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jl
 * @date 2021/2/27 10:30
 */
public class SsqBaseInfoDOCoverter {
    public static List<SsqBaseInfoDO> createSsqBaseInfoDOS(List<SsqBaseInfoResponseVO> saveList) {
        List<SsqBaseInfoDO> ssqBaseInfoDOS = new ArrayList<>();
        saveList.forEach(ssqBaseInfoResponseVO -> {
            SsqBaseInfoDO ssqBaseInfoDO = createSsqBaseInfoDO(ssqBaseInfoResponseVO);
            ssqBaseInfoDOS.add(ssqBaseInfoDO);
        });

        return ssqBaseInfoDOS;
    }

    public static SsqBaseInfoDO createSsqBaseInfoDO(SsqBaseInfoResponseVO responseVO) {
        SsqBaseInfoDO ssqBaseInfoDO = new SsqBaseInfoDO();
        ssqBaseInfoDO.setOpendate(responseVO.getOpendate());
        ssqBaseInfoDO.setIssueno(responseVO.getIssueno());
        ssqBaseInfoDO.setNumber(responseVO.getNumber());
        ssqBaseInfoDO.setRefernumber(responseVO.getRefernumber());
        ssqBaseInfoDO.setSaleamount(responseVO.getSaleamount());
        ssqBaseInfoDO.setTotalmoney(responseVO.getTotalmoney());
        ssqBaseInfoDO.setPrize(new Gson().toJson(responseVO.getPrize()));
        ssqBaseInfoDO.initCreated();
        return ssqBaseInfoDO;
    }

    public static SsqDetailInfoDO createSsqDetailInfoDO(List<String> hongQiuList, String issuen) {
        SsqDetailInfoDO ssqDetailInfoDO = new SsqDetailInfoDO();
        ssqDetailInfoDO.setIssueno(issuen);
        ssqDetailInfoDO.setAQiu(String.valueOf(hongQiuList.get(0)));
        ssqDetailInfoDO.setADaxiao(setSize(hongQiuList.get(0)));
        ssqDetailInfoDO.setADanshuang(setDanShuang(hongQiuList.get(0)));
        ssqDetailInfoDO.setAYushu(Integer.parseInt(hongQiuList.get(0)) % 3 + "");
        ssqDetailInfoDO.setAWuxing(compare(hongQiuList.get(0)));
        ssqDetailInfoDO.setBQiu(String.valueOf(hongQiuList.get(1)));
        ssqDetailInfoDO.setBDaxiao(setSize(hongQiuList.get(1)));
        ssqDetailInfoDO.setBDanshuang(setDanShuang(hongQiuList.get(1)));
        ssqDetailInfoDO.setBYushu(Integer.parseInt(hongQiuList.get(1)) % 3 + "");
        ssqDetailInfoDO.setBWuxing(compare(hongQiuList.get(1)));
        ssqDetailInfoDO.setCQiu(String.valueOf(hongQiuList.get(2)));
        ssqDetailInfoDO.setCDaxiao(setSize(hongQiuList.get(2)));
        ssqDetailInfoDO.setCDanshuang(setDanShuang(hongQiuList.get(2)));
        ssqDetailInfoDO.setCYushu(Integer.parseInt(hongQiuList.get(2)) % 3 + "");
        ssqDetailInfoDO.setCWuxing(compare(hongQiuList.get(2)));
        ssqDetailInfoDO.setDQiu(String.valueOf(hongQiuList.get(3)));
        ssqDetailInfoDO.setDDaxiao(setSize(hongQiuList.get(3)));
        ssqDetailInfoDO.setDDanshuang(setDanShuang(hongQiuList.get(3)));
        ssqDetailInfoDO.setDYushu(Integer.parseInt(hongQiuList.get(3)) % 3 + "");
        ssqDetailInfoDO.setDWuxing(compare(hongQiuList.get(3)));
        ssqDetailInfoDO.setEQiu(String.valueOf(hongQiuList.get(4)));
        ssqDetailInfoDO.setEDaxiao(setSize(hongQiuList.get(4)));
        ssqDetailInfoDO.setEDanshuang(setDanShuang(hongQiuList.get(4)));
        ssqDetailInfoDO.setEYushu(Integer.parseInt(hongQiuList.get(4)) % 3 + "");
        ssqDetailInfoDO.setEWuxing(compare(hongQiuList.get(4)));
        ssqDetailInfoDO.setFQiu(String.valueOf(hongQiuList.get(5)));
        ssqDetailInfoDO.setFDaxiao(setSize(hongQiuList.get(5)));
        ssqDetailInfoDO.setFDanshuang(setDanShuang(hongQiuList.get(5)));
        ssqDetailInfoDO.setFYushu(Integer.parseInt(hongQiuList.get(5)) % 3 + "");
        ssqDetailInfoDO.setFWuxing(compare(hongQiuList.get(5)));
        ssqDetailInfoDO.setSumValue(sum(hongQiuList));
        ssqDetailInfoDO.setTailSumValue(tailSum(hongQiuList));
        ssqDetailInfoDO.setSanSection(sanSection(hongQiuList));
        ssqDetailInfoDO.setSiSection(siSection(hongQiuList));
        ssqDetailInfoDO.setQiSection(qiSection(hongQiuList));
        ssqDetailInfoDO.setDaxiaoRatio(daxiaoRatio(hongQiuList));
        ssqDetailInfoDO.setDanshuangRatio(danshuangRatio(hongQiuList));
        ssqDetailInfoDO.initCreated();
        return ssqDetailInfoDO;
    }

    /**
     * 求和
     * @param hongQiuList
     * @return 和值
     * Created by jl on 2021/2/27 15:14.
     */
    public static String sum (List<String> hongQiuList) {
        int sum = hongQiuList.stream().mapToInt(Integer::parseInt).sum();
        return String.valueOf(sum);
    }

    /**
     * 求尾和
     * @param hongQiuList
     * @return 和值
     * Created by jl on 2021/2/27 15:14.
     */
    public static String tailSum (List<String> hongQiuList) {
        AtomicInteger sum = new AtomicInteger();
        hongQiuList.forEach(s -> {
            int temp = Integer.parseInt(s);
            if (temp > 10) {
                sum.addAndGet(Integer.parseInt(s.substring(1)));
            } else {
                sum.addAndGet(temp);
            }
        });
        return String.valueOf(sum.get());
    }

    /**
     * 单双比例
     * @param hongQiuList
     * @return 和值
     * Created by jl on 2021/2/27 15:14.
     */
    public static String danshuangRatio (List<String> hongQiuList) {
        int dan = 0;
        int shuang = 0;
        for (String s : hongQiuList) {
            int temp = Integer.parseInt(s);
            if (temp % 2 == 0) {
                shuang++;
            } else {
                dan++;
            }
        }
        return dan + ":" + shuang;
    }

    /**
     * 大小比例
     * @param hongQiuList
     * @return 和值
     * Created by jl on 2021/2/27 15:14.
     */
    public static String daxiaoRatio(List<String> hongQiuList) {
        int da = 0;
        int xiao = 0;
        for (String s : hongQiuList) {
            int temp = Integer.parseInt(s);
            if (temp >= 17) {
                da++;
            } else {
                xiao++;
            }
        }
        return da + ":" + xiao;
    }

    /**
     * 三区间比例
     * @param hongQiuList
     * @return 和值
     * Created by jl on 2021/2/27 15:14.
     */
    public static String sanSection(List<String> hongQiuList) {
        int one = 0;
        int two = 0;
        int three = 0;
        for (String s : hongQiuList) {
            int temp = Integer.parseInt(s);
            if (temp < 12) {
                one++;
            } else if (temp > 11 && temp < 23){
                two++;
            } else {
                three++;
            }
        }
        return one + ":" + two + ":" + three;
    }
    /**
     * 四区间比例
     * @param hongQiuList
     * @return 和值
     * Created by jl on 2021/2/27 15:14.
     */
    public static String siSection(List<String> hongQiuList) {
        int one = 0;
        int two = 0;
        int three = 0;
        int four = 0;
        for (String s : hongQiuList) {
            int temp = Integer.parseInt(s);
            if (temp <= 8) {
                one++;
            } else if (temp >= 9 && temp <= 16){
                two++;
            } else if (temp >= 18 && temp <= 25) {
                three++;
            } else if (temp >= 26 && temp <= 33){
                four++;
            }
        }
        return one + ":" + two + ":" + three + ":" + four;
    }

    /**
     * 四区间比例
     * @param hongQiuList
     * @return 和值
     * Created by jl on 2021/2/27 15:14.
     */
    public static String qiSection(List<String> hongQiuList) {
        int one = 0;
        int two = 0;
        int three = 0;
        int four = 0;
        int five = 0;
        int six = 0;
        int seven = 0;
        for (String s : hongQiuList) {
            int temp = Integer.parseInt(s);
            if (temp <= 5) {
                one++;
            } else if (temp >= 6 && temp <= 10){
                two++;
            } else if (temp >= 11 && temp <= 15) {
                three++;
            } else if (temp >= 16 && temp <= 20){
                four++;
            } else if (temp >= 21 && temp <= 25){
                five++;
            } else if (temp >= 26 && temp <= 30){
                six++;
            } else if (temp >= 31 && temp <= 33){
                seven++;
            }
        }
        return one + ":" + two + ":" + three + ":" + four + ":" + five + ":" + six + ":" + seven;
    }

    public static void main(String[] args) {
        List<String> hongQiuLis = new ArrayList<>();
        hongQiuLis.add("09");
        hongQiuLis.add("11");
        hongQiuLis.add("13");
        hongQiuLis.add("18");
        hongQiuLis.add("19");
        hongQiuLis.add("28");
        System.out.println(tailSum(hongQiuLis));
    }

    /**
     * @Author LeJiang
     * @CreateOn 2019/6/19 ^ 下午8:22
     * @Parameter red 当前红球
     * @Remark 当前红球所属五行中的那一个，返回五行编码
     */
    public static String compare(String red) {
        Map<String, String> map = Constants.WU_XING;
        for (String str : map.keySet()) {
            if (map.get(str).contains(red)) {
                return str;
            }
        }
        return "";
    }

    /**
     * @Author LeJiang
     * @CreateOn 2019/6/20 ^ 下午8:04
     * @Parameter str:当前红球
     * @Remark 设置大小
     */
    public static String setSize(String str) {
        if (Integer.parseInt(str) > 16) {
            return DaXiaoStatusEnum.DA.getCode();
        } else {
            return DaXiaoStatusEnum.XIAO.getCode();
        }
    }

    /**
     * 单双
     * @param str:当前红球
     * @return
     * Created by jl on 2021/2/27 15:06.
     */
    public static String setDanShuang(String str) {
        return Integer.parseInt(str) % 2 == 0 ? DanShuangStatusEnum.SHUANG.getCode() : DanShuangStatusEnum.DAN.getCode();
    }

}

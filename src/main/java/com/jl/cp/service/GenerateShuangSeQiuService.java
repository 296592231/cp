package com.jl.cp.service;

import cn.hutool.core.collection.CollectionUtil;
import com.google.gson.Gson;
import com.jl.cp.dto.requestVO.GenerateShuangSeQiuRequestVO;
import com.jl.cp.dto.requestVO.QueryHistoryRequestVO;
import com.jl.cp.dto.responseVO.GenerateShuangSeQiuResponseVO;
import com.jl.cp.entity.SsqDetailInfoDO;
import com.jl.cp.entity.SsqYuCeLogDO;
import com.jl.cp.mapper.SsqDetailInfoMapper;
import com.jl.cp.mapper.SsqYuCeLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @author jl
 * @date 2021/7/22 14:14
 */
@Slf4j
@Service
public class GenerateShuangSeQiuService {

    @Autowired
    private SsqDetailInfoMapper ssqDetailInfoMapper;

    @Autowired
    private SsqYuCeLogMapper ssqYuCeLogMapper;


    public String listUI() {
        //查询最大期数
        return ssqDetailInfoMapper.maxIssueno();
    }

    public Object queryHistory(QueryHistoryRequestVO requestVO) throws Exception {
        String weiZhi = "";
        String yuShuName = "";
        if (requestVO.getWeiZhi().equals("1")) {
            weiZhi = "a_qiu";
            yuShuName = "a_yushu";
        } else if (requestVO.getWeiZhi().equals("2")) {
            weiZhi = "b_qiu";
            yuShuName = "b_yushu";
        } else if (requestVO.getWeiZhi().equals("3")) {
            weiZhi = "c_qiu";
            yuShuName = "c_yushu";
        } else if (requestVO.getWeiZhi().equals("4")) {
            weiZhi = "d_qiu";
            yuShuName = "d_yushu";
        } else if (requestVO.getWeiZhi().equals("5")) {
            weiZhi = "e_qiu";
            yuShuName = "e_yushu";
        } else if (requestVO.getWeiZhi().equals("6")) {
            weiZhi = "f_qiu";
            yuShuName = "f_yushu";
        } else {
            return null;
        }

        Map<String,Object> paremMap = new HashMap<>();
        paremMap.put("columnName",weiZhi);
        paremMap.put("yuShu",yuShuName);
        paremMap.put("columnValue",requestVO.getHongQiu());
        List<SsqDetailInfoDO> ssqDetailInfoDOS = ssqDetailInfoMapper.getHistoryShuangSeQiuInfo(paremMap);
        if (CollectionUtil.isEmpty(ssqDetailInfoDOS)) {
            return null;
        }
        Map<String,String> map = new HashMap<>();
        StringBuffer hongQiu = new StringBuffer();
        StringBuffer yuShu = new StringBuffer();

        ssqDetailInfoDOS.forEach(ssqDetailInfoDO -> {
            if (StringUtils.isBlank(hongQiu.toString())) {
                hongQiu.append(ssqDetailInfoDO.getAQiu());
                yuShu.append(ssqDetailInfoDO.getAYushu());
            } else {
                hongQiu.append("," + ssqDetailInfoDO.getAQiu());
                yuShu.append("," + ssqDetailInfoDO.getAYushu());
            }
        });
        map.put("hongQiu",hongQiu.toString());
        map.put("yuShu",yuShu.toString());
        return map;
    }

    /**
     * 生成双色球
     * @param requestVO 请求参数
     * @return  返回结果
     * Created by jl on 2021/7/22 14:24
     */
    public GenerateShuangSeQiuResponseVO generateShuangSeQiu(GenerateShuangSeQiuRequestVO requestVO) {


        LinkedList<int[]> resultList = getGroupData();

        LinkedList<int[]> mantissaSumList = mantissaSum(resultList,requestVO.getMinWh(),requestVO.getMaxWh());

        //在和值区间的双色求号码组
        LinkedList<int[]> totalList = getSumList(mantissaSumList,requestVO.getTotalMinWh(),requestVO.getTotalMaxWh());

        //奇偶比例
        List<int[]> dsbList = new ArrayList<>();
        try {
            if (StringUtils.isNotBlank(requestVO.getDsb())) {
                analysis(requestVO.getDsb(),dsbList);
            }
        } catch (Exception e) {
            log.info("解析单双比失败：解析参数{}",requestVO.getDsb());
        }
        LinkedList<int[]> singleAndDoubleList = getSingleAndDouble(totalList,dsbList);

        //大小比例
        List<int[]> dxbList = new ArrayList<>();
        try {
            if (StringUtils.isNotBlank(requestVO.getDxb())) {
                analysis(requestVO.getDxb(),dxbList);
            }
        } catch (Exception e) {
            log.info("解析大小比失败：解析参数{}",requestVO.getDsb());
        }
        LinkedList<int[]> sizeRatio = getSizeRatio(singleAndDoubleList,dxbList);

        //路数
        int[][] reqLushu = new int[][]{analysis1(requestVO.getLuShu1(),false),
                analysis1(requestVO.getLuShu2(),false),
                analysis1(requestVO.getLuShu3(),false),
                analysis1(requestVO.getLuShu4(),false),
                analysis1(requestVO.getLuShu5(),false),
                analysis1(requestVO.getLuShu6(),false)};
        LinkedList<int[]> luShu = luShu(sizeRatio,reqLushu);

        //单双
        int[][] dsLists = new int[][]
                {analysis1(requestVO.getDs1(),true),
                        analysis1(requestVO.getDs2(),true),
                        analysis1(requestVO.getDs3(),true),
                        analysis1(requestVO.getDs4(),true),
                        analysis1(requestVO.getDs5(),true),
                        analysis1(requestVO.getDs6(),true)};
        LinkedList<int[]> dsList = eachNumberRange(luShu,dsLists);



        //精选
        int[][] selected = new int[][]
                {analysis1(requestVO.getJx1(),false),
                        analysis1(requestVO.getJx2(),false),
                        analysis1(requestVO.getJx3(),false),
                        analysis1(requestVO.getJx4(),false),
                        analysis1(requestVO.getJx5(),false),
                        analysis1(requestVO.getJx6(),false)};
        LinkedList<int[]> selectedList = selected(dsList,selected);

        GenerateShuangSeQiuResponseVO responseVO = new GenerateShuangSeQiuResponseVO();
        responseVO.setWhNum(mantissaSumList.size() + "");
        responseVO.setTotalNum(totalList.size() + "");
        responseVO.setDxbNum(sizeRatio.size() + "");
        responseVO.setDsbNum(singleAndDoubleList.size() + "");
        responseVO.setLuShuNum(luShu.size() + "");
        responseVO.setDsNum(dsList.size() + "");
        responseVO.setJxNum(selectedList.size() + "");


        //校验是否包含中奖号码
        boolean iszj = checkIsPrize(selectedList,requestVO.getZjhm());

        //生成想要的数据
        LinkedList<int[]>  printlnList = getRandom(selectedList,requestVO.getNum());

        StringBuffer sb = new StringBuffer();
        int count  = 1;
        for (int i = 1; i <= printlnList.size() ; i++) {
            if (i % 5==0) {
                sb.append("," + new Gson().toJson(printlnList.get(i-1)) + "-------"+count+"-------");
                count++;
            } else {
                if (StringUtils.isBlank(sb.toString())) {
                    sb.append(new Gson().toJson(printlnList.get(i-1)));
                } else {
                    sb.append("," + new Gson().toJson(printlnList.get(i-1)));
                }
            }
        }


        responseVO.setQiuJson(sb.toString());
        responseVO.setIsSfbhzj(iszj ? "中奖号码在最终筛选区间" : "中奖号码不在筛选号码中");
        responseVO.setZjxx(checkIsPrizeNum(printlnList,requestVO.getZjhm()));

        SsqYuCeLogDO queryDO = new SsqYuCeLogDO();
        queryDO.setIssueno(requestVO.getIssueno());
        SsqYuCeLogDO ssqYuCeLogDO = ssqYuCeLogMapper.selectOne(queryDO);
        ssqYuCeLogDO.setPALuShu(requestVO.getLuShu1());
        ssqYuCeLogDO.setPBLuShu(requestVO.getLuShu2());
        ssqYuCeLogDO.setPCLuShu(requestVO.getLuShu3());
        ssqYuCeLogDO.setPDLuShu(requestVO.getLuShu4());
        ssqYuCeLogDO.setPELuShu(requestVO.getLuShu5());
        ssqYuCeLogDO.setPFLuShu(requestVO.getLuShu6());
        ssqYuCeLogDO.setPMinWeiHe(requestVO.getMinWh() == null? null : Long.parseLong(requestVO.getMinWh()+""));
        ssqYuCeLogDO.setPMaxWeiHe(requestVO.getMaxWh() == null? null : Long.parseLong(requestVO.getMaxWh()+""));
        ssqYuCeLogDO.setPMinZongHe(requestVO.getTotalMinWh() == null? null : Long.parseLong(requestVO.getTotalMinWh()+""));
        ssqYuCeLogDO.setPMaxZongHe(requestVO.getTotalMaxWh() == null? null : Long.parseLong(requestVO.getTotalMaxWh()+""));
        ssqYuCeLogDO.setPDaXiaoBi(requestVO.getDsb());
        ssqYuCeLogDO.setPDanShuangBi(requestVO.getDsb());
        ssqYuCeLogDO.setPADanShuang(requestVO.getDs1());
        ssqYuCeLogDO.setPBDanShuang(requestVO.getDs2());
        ssqYuCeLogDO.setPCDanShuang(requestVO.getDs3());
        ssqYuCeLogDO.setPDDanShuang(requestVO.getDs4());
        ssqYuCeLogDO.setPEDanShuang(requestVO.getDs5());
        ssqYuCeLogDO.setPFDanShuang(requestVO.getDs6());
        ssqYuCeLogDO.setPAJingXuan(requestVO.getJx1());
        ssqYuCeLogDO.setPBJingXuan(requestVO.getJx2());
        ssqYuCeLogDO.setPCJingXuan(requestVO.getJx3());
        ssqYuCeLogDO.setPDJingXuan(requestVO.getJx4());
        ssqYuCeLogDO.setPEJingXuan(requestVO.getJx5());
        ssqYuCeLogDO.setPFJingXuan(requestVO.getJx6());
        ssqYuCeLogDO.setIssueno(requestVO.getIssueno());
        ssqYuCeLogDO.setShengChengHaoMa(sb.toString());
        Example example = new Example(SsqYuCeLogDO.class);
        example.createCriteria().andEqualTo("id",ssqYuCeLogDO.getId());
        ssqYuCeLogMapper.updateByExample(ssqYuCeLogDO,example);
        return responseVO;
    }

    /**
     * 解析“2,1或单,双类”类似的参数
     * @param str 请求参数
     * @return  有或空数组
     * Created by jl on 2021/7/22 15:18
     */
    public int[] analysis1 (String str,boolean isConvert) {

        try {
            if (StringUtils.isBlank(str)) {
                return  new int[]{};
            }
            String[] strs = str.split(",");
            int[] ints = new int[strs.length];
            for (int i = 0;i<strs.length ;i++) {
                if (isConvert) {
                    int temp = strs[i].equals("单") ? 1 : 0;
                    ints[i] = temp;
                } else {
                    ints[i] = Integer.parseInt(strs[i]);
                }
            }
            return ints;
        } catch (Exception e) {
            log.info("解析“2,1或单,双类”类似的参数失败，请求参数{}",str);
            return  new int[]{};
        }
    }


    /**
     * 解析大小比 或者单双比  异常抛出
     * @param str 请求参数
     * @param dxbList 返回参数
     * Created by jl on 2021/7/22 14:50
     */
    public void analysis (String str,List<int[]> dxbList) {
        List<String> dsbListStr = Arrays.asList(str.split(","));
        dsbListStr.forEach(s -> {
            String[] strs = s.split(":");
            int[] ints = new int[]{Integer.parseInt(strs[0]),Integer.parseInt(strs[1])};
            dxbList.add(ints);
        });
    }


    /**
     * 递归算法核心
     *
     * @param pce
     * @param w
     * @param m
     */
    private static void calpce(int[] pce, int w, int m) {
        if (pce[w] + 1 > (m - pce.length + w + 1)) {
            if (w > 0) {
                calpce(pce, w - 1, m);
                pce[w] = pce[w - 1] + 1;
            } else {
                pce[w] = pce[w] + 1;
            }
        } else {
            pce[w] = pce[w] + 1;
        }
    }

    private static int sumCount(int m, int n) {
        int a = 1, c = 1;
        for (int _m = m; _m > (m - n); _m--) {
            a = a * _m;
        }
        for (int _n = n; _n > 0; _n--) {
            c = c * _n;
        }
        return a / c;
    }

    /**
     * @Author LeJiang
     * @CreateOn 2021/1/9 ^ 下午2:35
     * @Remark 获取双色球所有组合号码，以英文都好分割,例如："01,02,03,04,05,06"
     */
    public static LinkedList<int[]> getGroupData() {
        LinkedList<int[]> resultList = new LinkedList<>();
        int sumc = sumCount(33, 6);

        System.out.println("排列组合后共有" + sumc + "个组合。");

        int[] pce = new int[]{1, 2, 3, 4, 5, 6};

        int count = 0;

        long t1 = System.currentTimeMillis();
        while (count <= sumc) {
            count++;
            int[] groupData = new int[]{pce[0], pce[1], pce[2], pce[3], pce[4], pce[5]};
            resultList.add(groupData);
            calpce(pce, 6 - 1, 33);
        }
        resultList.remove(resultList.size()-1);
        long t2 = System.currentTimeMillis();

        System.out.println("耗时:" + (t2 - t1) + "ms,计数总数:" + resultList.size());
        return resultList;
    }

    /**
     * @Author LeJiang
     * @CreateOn 2021/1/9 ^ 下午7:28
     * @Parameter
     * @Remark 和值区间数据获取
     */
    public LinkedList<int[]> getSumList (LinkedList<int[]> paramList,Integer startSum,Integer endSum) {
        if (paramList == null || paramList.size() < 1) {
            return null;
        }
        LinkedList<int[]> resultList = new LinkedList<>();
        for (int[] ints :paramList) {
            int sum = 0;
            for (int i : ints) {
                sum = sum + i;
            }
            if (startSum != null && endSum != null) {
                if (sum >= startSum && sum <= endSum) {
                    resultList.add(ints);
                }
            } else if (startSum != null) {
                if (sum >= startSum) {
                    resultList.add(ints);
                }
            } else if (endSum != null){
                if (sum <= endSum) {
                    resultList.add(ints);
                }
            } else {
                resultList.add(ints);
            }
        }
        System.out.println("和值区间数据总共："+resultList.size() + "条。");
        return resultList;
    }


    /**
     * @Author LeJiang
     * @CreateOn 2021/1/9 ^ 下午8:09
     * @Parameter 33个号码组合集合
     * @Remark 奇偶比
     */
    public LinkedList<int[]> getSingleAndDouble(LinkedList<int[]> paramList,List<int[]> dsbList) {
        if (paramList == null || paramList.size() < 1) {
            return null;
        }
        LinkedList<int[]> resultList = new LinkedList<>();
        for (int[] ints :paramList) {
            int single = 0;
            int doubles = 0;
            for (int i : ints) {
                if (i % 2 == 0) {
                    //双
                    doubles++;
                } else {
                    single++;
                }
            }

            if (CollectionUtil.isEmpty(dsbList)) {
                resultList.add(ints);
            } else {
                for (int[] singleAndDouble : dsbList) {
                    if (single == singleAndDouble[0] && doubles == singleAndDouble[1]) {
                        resultList.add(ints);
                    }
                }
            }
        }
        System.out.println("单双比例数据总共："+resultList.size() + "条。");
        return resultList;
    }

    /**
     * @Author LeJiang
     * @CreateOn 2021/1/9 ^ 下午8:09
     * @Parameter 33个号码组合集合
     * @Remark 大小比
     */
    public LinkedList<int[]> getSizeRatio(LinkedList<int[]> paramList,List<int[]> sizeRatios) {
        if (paramList == null || paramList.size() < 1) {
            return null;
        }
        LinkedList<int[]> resultList = new LinkedList<>();
        for (int[] ints :paramList) {
            int large = 0;
            int small = 0;
            for (int i : ints) {
                if (i >= 17) {
                    large++;
                } else {
                    small++;
                }
            }

            if (CollectionUtil.isEmpty(sizeRatios)) {
                resultList.add(ints);
            } else {
                for (int[] sizeRatio : sizeRatios) {
                    if (large == sizeRatio[0] && small == sizeRatio[1]) {
                        resultList.add(ints);
                    }
                }
            }
        }
        System.out.println("大小比例数据总共："+resultList.size() + "条。");
        return resultList;
    }

    /**
     * @Author LeJiang
     * @CreateOn 2021/1/9 ^ 下午8:51
     * @Parameter
     * @Remark 单双
     */
    public LinkedList<int[]> eachNumberRange(LinkedList<int[]> paramList,int[][] dss) {
        if (paramList == null || paramList.size() < 1) {
            return null;
        }
        LinkedList<int[]> resultList = new LinkedList<>();
        int pubIndex = 0;
        for (int[] ints :paramList) {

            boolean index0Flag = false;
            boolean index1Flag = false;
            boolean index2Flag = false;
            boolean index3Flag = false;
            boolean index4Flag = false;
            boolean index5Flag = false;
            int[] index0 = dss[0];
            if (index0.length < 1) {
                index0Flag = true;
            } else {
                int temp = ints[0] % 2 == 0 ? 0 : 1;
                for (int i : index0) {
                    if (temp == i) {
                        index0Flag = true;
                    }
                }
            }

            int[] index1 = dss[1];
            if (index1.length < 1) {
                index1Flag = true;
            } else {
                int temp = ints[1] % 2 == 0 ? 0 : 1;
                for (int i : index1) {
                    if (temp == i) {
                        index1Flag = true;
                    }
                }
            }

            int[] index2 = dss[2];
            if (index2.length < 1) {
                index2Flag = true;
            } else {
                int temp = ints[2] % 2 == 0 ? 0 : 1;
                for (int i : index2) {
                    if (temp == i) {
                        index2Flag = true;
                    }
                }
            }

            int[] index3 = dss[3];
            if (index3.length < 1) {
                index3Flag = true;
            } else {
                int temp = ints[3] % 2 == 0 ? 0 : 1;
                for (int i : index3) {
                    if (temp == i) {
                        index3Flag = true;
                    }
                }
            }

            int[] index4 = dss[4];
            if (index4.length < 1) {
                index4Flag = true;
            } else {
                int temp = ints[4] % 2 == 0 ? 0 : 1;
                for (int i : index4) {
                    if (temp == i) {
                        index4Flag = true;
                    }
                }
            }

            int[] index5 = dss[5];
            if (index5.length < 1) {
                index5Flag = true;
            } else {
                int temp = ints[5] % 2 == 0 ? 0 : 1;
                for (int i : index5) {
                    if (temp == i) {
                        index5Flag = true;
                    }
                }
            }

            if (index0Flag && index1Flag && index2Flag && index3Flag && index4Flag && index5Flag) {
                resultList.add(ints);
            }
        }
        System.out.println("每个球区间存在的数据总共："+resultList.size() + "条。");
        return resultList;
    }


    /**
     * @Author LeJiang
     * @CreateOn 2021/1/9 ^ 下午8:51
     * @Parameter
     * @Remark 精选号码过滤
     */
    public LinkedList<int[]> selected(LinkedList<int[]> paramList,int[][] selected ) {
        if (paramList == null || paramList.size() < 1) {
            return null;
        }
        LinkedList<int[]> resultList = new LinkedList<>();
        int pubIndex = 0;
        for (int[] ints :paramList) {

            boolean index0Flag = false;
            boolean index1Flag = false;
            boolean index2Flag = false;
            boolean index3Flag = false;
            boolean index4Flag = false;
            boolean index5Flag = false;
            int[] index0 = selected[0];
            if (index0.length < 1) {
                index0Flag = true;
            } else {
                for (int i : index0) {
                    if (ints[0] == i) {
                        index0Flag = true;
                    }
                }
            }

            int[] index1 = selected[1];
            if (index1.length < 1) {
                index1Flag = true;
            } else {
                for (int i : index1) {
                    if (ints[1] == i) {
                        index1Flag = true;
                    }
                }
            }

            int[] index2 = selected[2];
            if (index2.length < 1) {
                index2Flag = true;
            } else {
                for (int i : index2) {
                    if (ints[2] == i) {
                        index2Flag = true;
                    }
                }
            }

            int[] index3 = selected[3];
            if (index3.length < 1) {
                index3Flag = true;
            } else {
                for (int i : index3) {
                    if (ints[3] == i) {
                        index3Flag = true;
                    }
                }
            }

            int[] index4 = selected[4];
            if (index4.length < 1) {
                index4Flag = true;
            } else {
                for (int i : index4) {
                    if (ints[4] == i) {
                        index4Flag = true;
                    }
                }
            }

            int[] index5 = selected[5];
            if (index5.length < 1) {
                index5Flag = true;
            } else {
                for (int i : index5) {
                    if (ints[5] == i) {
                        index5Flag = true;
                    }
                }
            }

            if (index0Flag && index1Flag && index2Flag && index3Flag && index4Flag && index5Flag) {
                resultList.add(ints);
            }
        }
        System.out.println("精选号码存在的数据总共："+resultList.size() + "条。");
        return resultList;
    }


    /**
     * @Author LeJiang
     * @CreateOn 2021/1/9 ^ 下午8:51
     * @Parameter
     * @Remark 012路排除法
     */
    public LinkedList<int[]> luShu(LinkedList<int[]> paramList,int[][] reqLuShu) {
        if (paramList == null || paramList.size() < 1) {
            return null;
        }
        LinkedList<int[]> resultList = new LinkedList<>();
        for (int[] ints :paramList) {

            boolean index0Flag = false;
            boolean index1Flag = false;
            boolean index2Flag = false;
            boolean index3Flag = false;
            boolean index4Flag = false;
            boolean index5Flag = false;
            int zeroCount = 0;
            int oneCount = 0;
            int twoCount = 0;
            int[] index0 = reqLuShu[0];
            if (index0.length < 1) {
                index0Flag = true;
            } else {
                for (int i : index0) {
                    if (ints[0] % 3 == i) {
                        if (ints[0] % 3 == 0) {
                            zeroCount++;
                        }
                        if (ints[0] % 3 == 1) {
                            oneCount++;
                        }
                        if (ints[0] % 3 == 2) {
                            twoCount++;
                        }
                        index0Flag = true;
                    }
                }
            }

            int[] index1 = reqLuShu[1];
            if (index1.length < 1) {
                index1Flag = true;
            } else {
                for (int i : index1) {
                    if (ints[1] % 3 == i) {
                        if (ints[1] % 3 == 0) {
                            zeroCount++;
                        }
                        if (ints[1] % 3 == 1) {
                            oneCount++;
                        }
                        if (ints[1] % 3 == 2) {
                            twoCount++;
                        }
                        index1Flag = true;
                    }
                }
            }

            int[] index2 = reqLuShu[2];
            if (index2.length < 1) {
                index2Flag = true;
            } else {
                for (int i : index2) {
                    if (ints[2] % 3 == i) {
                        if (ints[2] % 3 == 0) {
                            zeroCount++;
                        }
                        if (ints[2] % 3 == 1) {
                            oneCount++;
                        }
                        if (ints[2] % 3 == 2) {
                            twoCount++;
                        }
                        index2Flag = true;
                    }
                }
            }

            int[] index3 = reqLuShu[3];
            if (index3.length < 1) {
                index3Flag = true;
            } else {
                for (int i : index3) {
                    if (ints[3] % 3 == i) {
                        if (ints[3] % 3 == 0) {
                            zeroCount++;
                        }
                        if (ints[3] % 3 == 1) {
                            oneCount++;
                        }
                        if (ints[3] % 3 == 2) {
                            twoCount++;
                        }
                        index3Flag = true;
                    }
                }
            }

            int[] index4 = reqLuShu[4];
            if (index4.length < 1) {
                index4Flag = true;
            } else {
                for (int i : index4) {
                    if (ints[4] % 3 == i) {
                        if (ints[4] % 3 == 0) {
                            zeroCount++;
                        }
                        if (ints[4] % 3 == 1) {
                            oneCount++;
                        }
                        if (ints[4] % 3 == 2) {
                            twoCount++;
                        }
                        index4Flag = true;
                    }
                }
            }

            int[] index5 = reqLuShu[5];
            if (index5.length < 1) {
                index5Flag = true;
            } else {
                for (int i : index5) {
                    if (ints[5] % 3 == i) {
                        if (ints[5] % 3 == 0) {
                            zeroCount++;
                        }
                        if (ints[5] % 3 == 1) {
                            oneCount++;
                        }
                        if (ints[5] % 3 == 2) {
                            twoCount++;
                        }
                        index5Flag = true;
                    }
                }
            }

            if (index0Flag && index1Flag && index2Flag && index3Flag && index4Flag && index5Flag) {
                resultList.add(ints);
            }
        }
        System.out.println("每个012路存在的数据总共："+resultList.size() + "条。");
        return resultList;
    }


    /**
     * @Author LeJiang
     * @CreateOn 2021/1/9 ^ 下午7:28
     * @Parameter
     * @Remark 和值区间数据获取
     */
    public LinkedList<int[]> mantissaSum (LinkedList<int[]> paramList,Integer startMantissaSum,Integer endMantissaSum) {
        if (paramList == null || paramList.size() < 1) {
            return null;
        }
        LinkedList<int[]> resultList = new LinkedList<>();
        for (int[] ints :paramList) {
            int sum = 0;
            for (int i : ints) {
                String temp = i + "";
                if (temp.length() == 1) {
                    sum = sum + i;
                } else {
                    sum = sum + Integer.parseInt(temp.substring(1));
                }
            }

            if (startMantissaSum != null && endMantissaSum != null) {
                if (sum >= startMantissaSum && sum <= endMantissaSum) {
                    resultList.add(ints);
                }
            } else if (startMantissaSum != null) {
                if (sum >= startMantissaSum) {
                    resultList.add(ints);
                }
            } else if (endMantissaSum != null){
                if (sum <= endMantissaSum) {
                    resultList.add(ints);
                }
            } else {
                resultList.add(ints);
            }
        }
        return resultList;
    }


    /**
     * @Author LeJiang
     * @Parameter
     * @Remark 随机从结果集中选出12注做为投注
     */
    public LinkedList<int[]> getRandom(LinkedList<int[]> paramList,int forSize) {
        Random r = new Random();
        LinkedList<int[]> resultList = new LinkedList<>();

        for (int i = 0 ; i < forSize ;i++) {
            int size = paramList.size();
            int index = r.nextInt(size);
            int[] currentObj = paramList.get(index);
            Iterator<int[]> iterator = paramList.iterator();
            while (iterator.hasNext()) {
                if (currentObj == iterator.next()) {
                    iterator.remove();
                }
            }
            resultList.add(currentObj);
        }
        return resultList;
    }

    /**
     * @Author LeJiang
     * @CreateOn 2021/1/9 ^ 下午8:30
     * @Parameter
     * @Remark 判断中奖号码是否在这个集合中
     */
    public boolean checkIsPrize (LinkedList<int[]> paramList,String str) {

        if (StringUtils.isBlank(str)) {
            return false;
        }
        try {
            String[] prizeStrs = str.split(",");
            int[] prizes = new int[] {Integer.parseInt(prizeStrs[0]),Integer.parseInt(prizeStrs[1]),Integer.parseInt(prizeStrs[2]),Integer.parseInt(prizeStrs[3]),Integer.parseInt(prizeStrs[4]),Integer.parseInt(prizeStrs[5])};

            boolean isZj = false;
            for (int[] ints : paramList) {
                int prizeCount = 0;
                if (ints[0] == prizes[0]) {
                    prizeCount++;
                }
                if (ints[1] == prizes[1]) {
                    prizeCount++;
                }
                if (ints[2] == prizes[2]) {
                    prizeCount++;
                }
                if (ints[3] == prizes[3]) {
                    prizeCount++;
                }
                if (ints[4] == prizes[4]) {
                    prizeCount++;
                }
                if (ints[5] == prizes[5]) {
                    prizeCount++;
                }

                if (prizeCount == ints.length) {
                    isZj = true;
                    System.out.println("恭喜您红球全中："+ints[0] + "," + ints[1] + "," + ints[2] + "," + ints[3] + "," + ints[4]
                            + "," + ints[5]);
                }
            }
            return isZj;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @Author LeJiang
     * @CreateOn 2021/1/9 ^ 下午8:30
     * @Parameter
     * @Remark 判断中奖号码是否在这个集合中
     */
    public String checkIsPrizeNum (LinkedList<int[]> paramList,String str) {

        if (StringUtils.isBlank(str)) {
            return "";
        }
        try {
            String[] prizeStrs = str.split(",");
            int[] prizes = new int[] {Integer.parseInt(prizeStrs[0]),Integer.parseInt(prizeStrs[1]),Integer.parseInt(prizeStrs[2]),Integer.parseInt(prizeStrs[3]),Integer.parseInt(prizeStrs[4]),Integer.parseInt(prizeStrs[5])};

            StringBuffer sb = new StringBuffer();
            int fourCount = 0;
            int fiveCount = 0;
            int sixCount = 0;

            for (int i= 0 ; i< paramList.size() ; i++) {
                int[] ints = paramList.get(i);
                int prizeCount = 0;
                if (ints[0] == prizes[0]) {
                    prizeCount++;
                }
                if (ints[1] == prizes[1]) {
                    prizeCount++;
                }
                if (ints[2] == prizes[2]) {
                    prizeCount++;
                }
                if (ints[3] == prizes[3]) {
                    prizeCount++;
                }
                if (ints[4] == prizes[4]) {
                    prizeCount++;
                }
                if (ints[5] == prizes[5]) {
                    prizeCount++;
                }

                if (prizeCount == 4) {
                    fourCount++;
                }
                if (prizeCount == 5) {
                    fiveCount++;
                }
                if (prizeCount == 6) {
                    sixCount++;
                }
            }
            if (fourCount > 0) {
                sb.append(String.format("中4个一个有【%s】",fourCount+""));
            }
            if (fiveCount > 0) {
                sb.append(String.format("中5个一个有【%s】",fiveCount+""));
            }
            if (sixCount > 0) {
                sb.append(String.format("中6个一个有【%s】",sixCount+""));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}

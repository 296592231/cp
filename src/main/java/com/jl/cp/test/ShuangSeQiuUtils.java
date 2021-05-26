package com.jl.cp.test;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @Author LeJiang
 * @CreateOn 2021/1/9 ^ 下午2:09
 * @Path /
 * @ContentType Content-Type/application-json
 * @Remark
 */
public class ShuangSeQiuUtils {


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

    //开奖号码02 04 07 24 25 32
    public final static int[] prizes = {2,4,7,24,25,32};

    //尾数和区间
    public final static int START_MANTISSA_SUM = 15;
    public final static int END_MANTISSA_SUM = 28;

    //和值区间
    public final static int START_SUM = 74;
    public final static int END_SUM = 124;

    //三区间比（支持多个区间）
    public final static List<int[]> THREE_SECTION = new ArrayList<>();

    //单双比例（支持多个比例）
    public final static List<int[]> SINGLE_AND_DOUBLE = new ArrayList<>();

    //大小比
    public final static List<int[]> SIZE_RATIO = new ArrayList<>();

    //每个球的区间
    public final static int[][] LU_SHU = new int[][]
            {{2,1},{1,0},{1,2},{2,0},{2,1},{0,2}};

    //每个球的区间
    public final static int[][] EACH_NUMBER_RANGE = new int[][]
            {{2,3,6,8,9},{3,4,6,7,9,10,12,13,15,16},{7,10,11,13,14,16,17,19},{15,17,18,20,21,23,24},{19,20,22,23,25,26,28,29},{24,26,27,29,30,32,33}};

    //精选号码
    public final static int[][] SELECTED = new int[][]
            {{2,6,8},{3,4,9,12,13},{14,13,16,17,11},{15,17,21,23,24,26},{22,23,26,28},{26,27,29,30,32,33}};

    static {
        //三区间比设置（支持多个区间）
        THREE_SECTION.add(new int[]{3,0,3});

        //单双比例（支持多个区间） 第一个是单  第二个是双
        SINGLE_AND_DOUBLE.add(new int[]{3,3});
        SINGLE_AND_DOUBLE.add(new int[]{2,4});

        //大小比例  第一个是大 第二个是小
        SIZE_RATIO.add(new int[]{2,4});
        SIZE_RATIO.add(new int[]{3,3});
    }

    /**
      * @Author LeJiang
      * @CreateOn 2021/1/9 ^ 下午7:28
      * @Parameter
      * @Remark 和值区间数据获取
      */
    public static LinkedList<int[]> getSumList (LinkedList<int[]> paramList) {
        if (paramList == null || paramList.size() < 1) {
            return null;
        }
        LinkedList<int[]> resultList = new LinkedList<>();
        for (int[] ints :paramList) {
            int sum = 0;
            for (int i : ints) {
                sum = sum + i;
            }

            if (sum >= START_SUM && sum <= END_SUM) {
                resultList.add(ints);
            }
        }
        System.out.println("和值区间数据总共："+resultList.size() + "条。");
        return resultList;
    }


    /**
      * @Author LeJiang
      * @CreateOn 2021/1/9 ^ 下午7:48
      * @Parameter 33个号码组合集合
      * @Remark 获取三区间比例
      */
    public static LinkedList<int[]> getThreeSection (LinkedList<int[]> paramList) {
        if (paramList == null || paramList.size() < 1) {
            return null;
        }
        LinkedList<int[]> resultList = new LinkedList<>();
        for (int[] ints :paramList) {
            int oneSection = 0;
            int twoSection = 0;
            int threeSection = 0;
            for (int i : ints) {
                if (i < 12 && i >= 1) {
                    //在区间1
                    oneSection++;
                } else if (i < 23 && i >= 12) {
                    //在区间2
                    twoSection++;
                } else {
                    //在区间3
                    threeSection++;
                }
            }

            for (int[] sections :THREE_SECTION) {
                if (oneSection == sections[0] && twoSection == sections[1] && threeSection == sections[2]) {
                    resultList.add(ints);
                }
            }
        }
        System.out.println("在三区间数据总共："+resultList.size() + "条。");
        return resultList;
    }

    /**
      * @Author LeJiang
      * @CreateOn 2021/1/9 ^ 下午8:09
      * @Parameter 33个号码组合集合
      * @Remark 奇偶比
      */
    public static LinkedList<int[]> getSingleAndDouble(LinkedList<int[]> paramList) {
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

            for (int[] singleAndDouble :SINGLE_AND_DOUBLE) {
                if (single == singleAndDouble[0] && doubles == singleAndDouble[1]) {
                    resultList.add(ints);
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
    public static LinkedList<int[]> getSizeRatio(LinkedList<int[]> paramList) {
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

            for (int[] sizeRatio :SIZE_RATIO) {
                if (large == sizeRatio[0] && small == sizeRatio[1]) {
                    resultList.add(ints);
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
      * @Remark 五行校验
      */
    public static LinkedList<int[]> eachNumberRange(LinkedList<int[]> paramList) {
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
            int[] index0 = EACH_NUMBER_RANGE[0];
            for (int i : index0) {
                if (ints[0] == i) {
                    index0Flag = true;
                }
            }

            int[] index1 = EACH_NUMBER_RANGE[1];
            for (int i : index1) {
                if (ints[1] == i) {
                    index1Flag = true;
                }
            }

            int[] index2 = EACH_NUMBER_RANGE[2];
            for (int i : index2) {
                if (ints[2] == i) {
                    index2Flag = true;
                }
            }

            int[] index3 = EACH_NUMBER_RANGE[3];
            for (int i : index3) {
                if (ints[3] == i) {
                    index3Flag = true;
                }
            }

            int[] index4 = EACH_NUMBER_RANGE[4];
            for (int i : index4) {
                if (ints[4] == i) {
                    index4Flag = true;
                }
            }

            int[] index5 = EACH_NUMBER_RANGE[5];
            for (int i : index5) {
                if (ints[5] == i) {
                    index5Flag = true;
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
    public static LinkedList<int[]> selected(LinkedList<int[]> paramList) {
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
            int[] index0 = SELECTED[0];
            for (int i : index0) {
                if (ints[0] == i) {
                    index0Flag = true;
                }
            }

            int[] index1 = SELECTED[1];
            for (int i : index1) {
                if (ints[1] == i) {
                    index1Flag = true;
                }
            }

            int[] index2 = SELECTED[2];
            for (int i : index2) {
                if (ints[2] == i) {
                    index2Flag = true;
                }
            }

            int[] index3 = SELECTED[3];
            for (int i : index3) {
                if (ints[3] == i) {
                    index3Flag = true;
                }
            }

            int[] index4 = SELECTED[4];
            for (int i : index4) {
                if (ints[4] == i) {
                    index4Flag = true;
                }
            }

            int[] index5 = SELECTED[5];
            for (int i : index5) {
                if (ints[5] == i) {
                    index5Flag = true;
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
    public static LinkedList<int[]> luShu(LinkedList<int[]> paramList) {
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
            int[] index0 = LU_SHU[0];
            for (int i : index0) {
                if (ints[0] % 3 == i) {
                    index0Flag = true;
                }
            }

            int[] index1 = LU_SHU[1];
            for (int i : index1) {
                if (ints[1]  % 3 == i) {
                    index1Flag = true;
                }
            }

            int[] index2 = LU_SHU[2];
            for (int i : index2) {
                if (ints[2]  % 3 == i) {
                    index2Flag = true;
                }
            }

            int[] index3 = LU_SHU[3];
            for (int i : index3) {
                if (ints[3] % 3 == i) {
                    index3Flag = true;
                }
            }

            int[] index4 = LU_SHU[4];
            for (int i : index4) {
                if (ints[4] % 3 == i) {
                    index4Flag = true;
                }
            }

            int[] index5 = LU_SHU[5];
            for (int i : index5) {
                if (ints[5] % 3 == i) {
                    index5Flag = true;
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
    public static LinkedList<int[]> mantissaSum (LinkedList<int[]> paramList) {
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

            if (sum >= START_MANTISSA_SUM && sum <= END_MANTISSA_SUM) {
                resultList.add(ints);
            }
        }
        System.out.println("尾和值区间数据总共："+resultList.size() + "条。");
        return resultList;
    }



    public static void main(String[] args) throws FileNotFoundException {

        //获取33个号码的组合
        LinkedList<int[]> resultList = getGroupData();
        LinkedList<int[]>  printlnList = new LinkedList<>();

        //尾数和
        LinkedList<int[]> mantissaSumList = mantissaSum(resultList);
        //printlnList.addAll(getRandom(mantissaSumList,1));

        //在和值区间的双色求号码组
        LinkedList<int[]> sumList = getSumList(mantissaSumList);
        //System.out.println(new Gson().toJson(getRandom(sumList,1)));

//        //三区间比
        LinkedList<int[]> threeSectionList = getThreeSection(sumList);
        //System.out.println(new Gson().toJson(getRandom(threeSectionList,2)));

//        //奇偶比例
        LinkedList<int[]> singleAndDoubleList = getSingleAndDouble(threeSectionList);
       // System.out.println(new Gson().toJson(getRandom(singleAndDoubleList,2)));

        //大小比例
        LinkedList<int[]> sizeRatio = getSizeRatio(singleAndDoubleList);
        //System.out.println(new Gson().toJson(getRandom(sizeRatio,2)));

        //大小比例
        LinkedList<int[]> luShu = luShu(sizeRatio);
        //printlnList.addAll(getRandom(luShu,10));

        //每个区间
        LinkedList<int[]> eachNumberRangeList = eachNumberRange(luShu);
        printlnList.addAll(getRandom(eachNumberRangeList,100));

        //每个区间
        LinkedList<int[]> selectedList = selected(luShu);
        //printlnList.addAll(getRandom(selectedList,5));

        System.out.println(new Gson().toJson(printlnList));

        checkIsPrize(printlnList);
    }

    /**
      * @Author LeJiang
      * @Parameter
      * @Remark 随机从结果集中选出12注做为投注
      */
    public static LinkedList<int[]> getRandom(LinkedList<int[]> paramList,int forSize) {

        Random r = new Random();
        LinkedList<int[]> resultList = new LinkedList<>();
        int size = paramList.size();
        for (int i = 0 ; i < forSize ;i++) {
            int index = r.nextInt(size);
            resultList.add(paramList.get(index));
        }
        return resultList;
    }

    /**
      * @Author LeJiang
      * @CreateOn 2021/1/9 ^ 下午8:30
      * @Parameter
      * @Remark 判断中奖号码是否在这个集合中
      */
    public static void checkIsPrize (LinkedList<int[]> paramList) {

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
                System.out.println("恭喜您红球全中："+ints[0] + "," + ints[1] + "," + ints[2] + "," + ints[3] + "," + ints[4]
                        + "," + ints[5]);
            }
        }

    }

}

/**
 * alifepay.com Inc.
 * Copyright (c) 2016-2020 All Rights Reserved.
 */
package com.jl.cp.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jl
 * @date 2021/2/27 16:45
 */
@Getter
@Setter
public class YuCeDataDTO {

    /**6行6列每五期数据，红球1**/
    private String hangLieRed1;

    /**行6列每五期数据，红球1，012路**/
    private String hangLieLuShu1;

    /**行6列每五期数据，红球1，五行**/
    private String hangLieWuXing1;

    /**行6列每五期数据，红球1，单双**/
    private String hangLieDanShuang1;

    /**6行6列每五期数据，红球1**/
    private String hangLieRed2;

    /**行6列每五期数据，红球2，012路**/
    private String hangLieLuShu2;

    /**行6列每五期数据，红球2，五行**/
    private String hangLieWuXing2;

    /**行6列每五期数据，红球2，单双**/
    private String hangLieDanShuang2;

    /**6行6列每五期数据，红球3**/
    private String hangLieRed3;

    /**行6列每五期数据，红球3，012路**/
    private String hangLieLuShu3;

    /**行6列每五期数据，红球3，五行**/
    private String hangLieWuXing3;

    /**行6列每五期数据，红球3，单双**/
    private String hangLieDanShuang3;

    /**6行6列每五期数据，红球4**/
    private String hangLieRed4;

    /**行6列每五期数据，红球4，012路**/
    private String hangLieLuShu4;

    /**行6列每五期数据，红球4，五行**/
    private String hangLieWuXing4;

    /**行6列每五期数据，红球4，单双**/
    private String hangLieDanShuang4;

    /**6行6列每五期数据，红球5**/
    private String hangLieRed5;

    /**行6列每五期数据，红球5，012路**/
    private String hangLieLuShu5;

    /**行6列每五期数据，红球5，五行**/
    private String hangLieWuXing5;

    /**行6列每五期数据，红球5，单双**/
    private String hangLieDanShuang5;

    /**6行6列每五期数据，红球6**/
    private String hangLieRed6;

    /**行6列每五期数据，红球6，012路**/
    private String hangLieLuShu6;

    /**行6列每五期数据，红球6，五行**/
    private String hangLieWuXing6;

    /**行6列每五期数据，单双**/
    private String hangLieDanShuang6;

    /**历史数据分布，和值**/
    private String heZhi;

    /**历史数据分布，尾和**/
    private String weiHe;

    /**历史数据分布，大小比**/
    private String daXiaoBi;

    /**历史数据分布，奇偶比**/
    private String qiOuBi;

    /**历史数据分布，三区间比**/
    private String sanSection;

}

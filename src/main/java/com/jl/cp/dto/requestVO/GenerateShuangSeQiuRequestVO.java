package com.jl.cp.dto.requestVO;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jl
 * @date 2021/7/22 11:59
 */
@Getter
@Setter
public class GenerateShuangSeQiuRequestVO {

    private String issueno;

    /**中奖号码**/
    private String zjhm;

    /**生成条数**/
    private Integer num;

    /**尾和最小值 **/
    private Integer minWh;

    /**尾和最大值 **/
    private Integer maxWh;

    /** 总和最小值**/
    private Integer totalMinWh;

    /**总和最大值 **/
    private Integer totalMaxWh;

    /**大小比 **/
    private String dxb;

    /**单双比 **/
    private String dsb;

    /**012第一位 **/
    private String luShu1;

    /**012第二位 **/
    private String luShu2;

    /** 012第三位**/
    private String luShu3;

    /**012第四位 **/
    private String luShu4;

    /**012第五位 **/
    private String luShu5;

    /**012第六位 **/
    private String luShu6;

    /**单双第一位 **/
    private String ds1;

    /**单双第二位 **/
    private String ds2;

    /**单双第三位 **/
    private String ds3;

    /**单双第四位 **/
    private String ds4;

    /**单双第五位 **/
    private String ds5;

    /**单双第六位 **/
    private String ds6;

    /** 精选号码第一位**/
    private String jx1;

    /** 精选号码第二位**/
    private String jx2;

    /**精选号码第三位 **/
    private String jx3;

    /** 精选号码第四位**/
    private String jx4;

    /**精选号码第五位 **/
    private String jx5;

    /**精选号码第六位 **/
    private String jx6;
}

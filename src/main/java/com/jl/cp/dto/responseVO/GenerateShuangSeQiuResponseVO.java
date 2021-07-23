package com.jl.cp.dto.responseVO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author jl
 * @date 2021/7/22 11:59
 */
@Getter
@Setter
public class GenerateShuangSeQiuResponseVO {

    /**尾和**/
    private String whNum;

    /**总和**/
    private String totalNum;

    /**大小比总和**/
    private String dxbNum;

    /**单双比总和**/
    private String dsbNum;

    /**012路总和**/
    private String luShuNum;

    /**单双总和**/
    private String dsNum;

    /**精选总和**/
    private String jxNum;

    /**生成的球**/
    private String qiuJson;

    /**是否包含中奖号码**/
    private String isSfbhzj;

    /**中奖信息**/
    private String zjxx;
}

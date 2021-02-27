/**
 * alifepay.com Inc.
 * Copyright (c) 2016-2020 All Rights Reserved.
 */
package com.jl.cp.vo.HttpConverterResponseVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 急速数据接口获取双色球数据解析
 * @author jl
 * @date 2021/2/27 10:17
 */
@Getter
@Setter
@ToString
public class SsqJsBaseResponseVO {

    /**状态**/
    private String status;

    /**返回结果**/
    private SsqJsResultResponseVO result;
}

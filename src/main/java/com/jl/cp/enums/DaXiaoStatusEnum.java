/**
 * alifepay.com Inc.
 * Copyright (c) 2016-2020 All Rights Reserved.
 */
package com.jl.cp.enums;

import lombok.Getter;

/**
 * @author jl
 * @date 2021/2/27 15:04
 */
@Getter
public enum DaXiaoStatusEnum {

    //枚举值
    DA("大", "0"),
    XIAO("小", "1");

    private String code;
    private String mesg;

    DaXiaoStatusEnum(String code, String mesg) {
        this.code = code;
        this.mesg = mesg;
    }
}
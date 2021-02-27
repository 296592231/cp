/**
 * alifepay.com Inc.
 * Copyright (c) 2016-2020 All Rights Reserved.
 */
package com.jl.cp.enums;

import lombok.Getter;

/**
 * @author jl
 * @date 2021/2/27 15:07
 */
@Getter
public enum DanShuangStatusEnum {

    //枚举值
    DAN("单", "0"),
    SHUANG("双", "1");
    ;

    private String code;
    private String mesg;

    DanShuangStatusEnum(String code, String mesg) {
        this.code = code;
        this.mesg = mesg;
    }
}
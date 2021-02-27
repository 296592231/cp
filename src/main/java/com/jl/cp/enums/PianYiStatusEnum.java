/**
 * alifepay.com Inc.
 * Copyright (c) 2016-2020 All Rights Reserved.
 */
package com.jl.cp.enums;

import lombok.Getter;

/**
 * @author jl
 * @date 2021/2/27 14:59
 */
@Getter
public enum PianYiStatusEnum {

    //枚举值
    ZUO("向左", "1"),
    DENGYU("连续", "2"),
    YOU("向又", "3");

    private String code;
    private String mesg;

    PianYiStatusEnum(String code, String mesg) {
        this.code = code;
        this.mesg = mesg;
    }
}
/**
 * alifepay.com Inc.
 * Copyright (c) 2016-2020 All Rights Reserved.
 */
package com.jl.cp.dto;

import lombok.Getter;
import lombok.Setter;
/**
 * @author jl
 * @date 2021/5/19 16:53
 */
@Getter
@Setter
public class BILieDto {

    /**多少期类出现的次数**/
    private Integer num;

    /**0,1,2**/
    private String yuShu;

}

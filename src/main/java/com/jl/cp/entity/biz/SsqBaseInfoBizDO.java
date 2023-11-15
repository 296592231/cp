package com.jl.cp.entity.biz;

import com.jl.cp.entity.BaseDO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class SsqBaseInfoBizDO extends BaseDO {

    private String number;

    private String refernumber;
}
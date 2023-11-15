package com.jl.cp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ssq_base_info")
public class SsqBaseInfoDO extends BaseDO {

    private Date opendate;

    private String issueno;

    private String number;

    private String refernumber;

    private String saleamount;

    private String totalmoney;

    private String prize;
}
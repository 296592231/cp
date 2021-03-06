package com.jl.cp.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@ToString(callSuper = true)
@Table(name = "ssq_base_info")
public class SsqBaseInfoDO extends BaseDO {

    private Date opendate;

    private String issueno;

    private String number;

    private String refernumber;

    private String saleamount;

    private String totalmoney;

    private String prize;
}
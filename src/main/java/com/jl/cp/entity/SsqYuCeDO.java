package com.jl.cp.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Table;

@Getter
@Setter
@ToString
@Table(name = "ssq_yu_ce")
public class SsqYuCeDO {
    private Integer id;

    private String yuCeData;

    private String body;

    private String odds;

    private String isin;

    private String isInBody;

}
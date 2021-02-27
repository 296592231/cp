package com.jl.cp.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Table;


@Getter
@Setter
@ToString
@Table(name = "ssq_detail_info")
public class SsqDetailInfoDO extends BaseDO{

    private String issueno;

    private String aQiu;

    private String aPianyi;

    private String aDaxiao;

    private String aDanshuang;

    private String aYushu;

    private String aWuxing;

    private String bQiu;

    private String bPianyi;

    private String bDaxiao;

    private String bDanshuang;

    private String bYushu;

    private String bWuxing;

    private String cQiu;

    private String cPianyi;

    private String cDaxiao;

    private String cDanshuang;

    private String cYushu;

    private String cWuxing;

    private String dQiu;

    private String dPianyi;

    private String dDaxiao;

    private String dDanshuang;

    private String dYushu;

    private String dWuxing;

    private String eQiu;

    private String ePianyi;

    private String eDaxiao;

    private String eDanshuang;

    private String eYushu;

    private String eWuxing;

    private String fQiu;

    private String fPianyi;

    private String fDaxiao;

    private String fDanshuang;

    private String fYushu;

    private String fWuxing;

    private String sumValue;

    private String tailSumValue;

    private String sanSection;

    private String siSection;

    private String qiSection;

    private String daxiaoRatio;

    private String danshuangRatio;
}
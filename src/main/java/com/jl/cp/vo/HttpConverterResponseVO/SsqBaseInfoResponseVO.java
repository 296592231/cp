package com.jl.cp.vo.HttpConverterResponseVO;

import com.jl.cp.entity.BaseDO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Table;
import java.util.Date;


/**
 * 急速数据接口获取双色球数据解析
 * Created by jl on 2021/2/27 10:21.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class SsqBaseInfoResponseVO{

    private String refernumber;

    private Date opendate;

    private String issueno;

    private String number;

    private String saleamount;

    private String totalmoney;

    private Object prize;
}
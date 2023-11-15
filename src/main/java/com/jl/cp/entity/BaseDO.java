package com.jl.cp.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础DO
 * Created by zhaojunjian on 2018/06/12.
 */
@Getter
@Setter
@ToString
public class BaseDO implements Serializable {

    /** 序列化ID */
    private static final long serialVersionUID = 2400929148472356001L;

    /** 主键ID */
    private Long              id;

    /** 创建日期 */
    private Date              createTime;

    /** 创建人 */
    private String            createUser;

    /**
    * 初始化 新增时间、新增人与版本号
    * Created by kz on 2019/2/15 16:19.
    */
    public void initCreated() {
        Date date = new Date();
        this.setCreateTime(date);
        this.setCreateUser("SYSTEM");
    }

    /**
     * 初始化 新增时间、新增人与版本号
     * Created by kz on 2019/2/15 16:19.
     */
    public void initCreatedBy() {
        Date date = new Date();
        this.setCreateTime(date);
        this.setCreateUser("SYSTEM");
    }
    
    /**
    * 初始化新增时间与版本号
    * Created by kz on 2018/11/19 15:00.
    */
    public void initInputDate() {
        this.setCreateTime(new Date());
    }

    public void initUpdateBy() {
        this.setCreateUser("SYSTEM");
    }
}

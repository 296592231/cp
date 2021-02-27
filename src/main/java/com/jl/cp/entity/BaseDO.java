package com.jl.cp.entity;

import com.jksj.tftpos.common.constants.BaseConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long              id;

    /** 创建日期 */
    private Date              createTime;

    /** 创建人 */
    private String            createUser;

    /** 修改日期 */
    private Date              updateTime;

    /** 更新人 */
    private String            updateUser;

    /** 版本号 */
    private Long              version;

    /**
    * 初始化 新增时间、新增人与版本号
    * Created by kz on 2019/2/15 16:19.
    */
    public void initCreated() {
        Date date = new Date();
        this.setCreateTime(date);
        this.setCreateUser(BaseConstant.DEFAULT_INPUT_BY);
        this.setVersion(BaseConstant.DEFAULT_VERSION);
    }

    /**
     * 初始化 新增时间、新增人与版本号
     * Created by kz on 2019/2/15 16:19.
     */
    public void initCreatedBy() {
        Date date = new Date();
        this.setCreateTime(date);
        this.setCreateUser(BaseConstant.DEFAULT_INPUT_BY);
        this.setUpdateTime(date);
        this.setUpdateUser(BaseConstant.DEFAULT_INPUT_BY);
        this.setVersion(BaseConstant.DEFAULT_VERSION);
    }
    
    /**
    * 初始化新增时间与版本号
    * Created by kz on 2018/11/19 15:00.
    */
    public void initInputDate() {
        this.setCreateTime(new Date());
        this.setVersion(BaseConstant.DEFAULT_VERSION);
    }

    public void initUpdateBy() {
        this.setCreateUser(BaseConstant.DEFAULT_UPDATE_BY);
        this.setUpdateTime(new Date());
    }
}

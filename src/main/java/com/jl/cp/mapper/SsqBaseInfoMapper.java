package com.jl.cp.mapper;


import com.jl.cp.entity.SsqBaseInfoDO;
import com.jl.cp.entity.biz.SsqBaseInfoBizDO;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;


public interface SsqBaseInfoMapper extends Mapper<SsqBaseInfoDO>{

    List<SsqBaseInfoBizDO> findListByMap(Map<String,Object> map);
}
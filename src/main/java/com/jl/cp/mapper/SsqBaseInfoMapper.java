package com.jl.cp.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jl.cp.entity.SsqBaseInfoDO;
import com.jl.cp.entity.biz.SsqBaseInfoBizDO;

import java.util.List;
import java.util.Map;


public interface SsqBaseInfoMapper extends BaseMapper<SsqBaseInfoDO> {

    List<SsqBaseInfoBizDO> findListByMap(Map<String,Object> map);
}
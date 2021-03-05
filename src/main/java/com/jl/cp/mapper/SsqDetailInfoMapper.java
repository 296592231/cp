package com.jl.cp.mapper;

import com.jl.cp.entity.SsqDetailInfoDO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SsqDetailInfoMapper extends Mapper<SsqDetailInfoDO> {

    List<SsqDetailInfoDO> selectListByLimit(@Param("issueno") Long issueno);

    List<SsqDetailInfoDO> findListByIdAddOne(Map<String,Object> map);
}
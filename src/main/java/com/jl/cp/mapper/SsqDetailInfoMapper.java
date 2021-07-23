package com.jl.cp.mapper;

import com.jl.cp.dto.StatYuShuDTO;
import com.jl.cp.dto.SumValueDTO;
import com.jl.cp.entity.SsqDetailInfoDO;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SsqDetailInfoMapper extends Mapper<SsqDetailInfoDO> {

    List<SsqDetailInfoDO> selectListByLimit(Map<String,Object> map);

    List<SsqDetailInfoDO> findListByIdAddOne(Map<String,Object> map);

    /**
     * 根据每个球统计一定条数内 当前期数的数据
     * @param paremMap 主要是 当前期数 以及获取多少条
     * @return 返回结果集
     * Created by jl on 2021/5/24 17:58.
     */
    List<StatYuShuDTO> currentStatYuShuInfo(Map<String, Object> paremMap);

    /**
     * 根据每个球统计一定条数内 当前期数的数据
     * @param paremMap 主要是 当前期数 以及获取多少条
     * @return 返回结果集
     * Created by jl on 2021/5/24 17:58.
     */
    List<StatYuShuDTO> statYuShuInfo(Map<String, Object> paremMap);

    /**
     * 查尾和 最小值 平均值 最大值
     * @param paremMap 请求参数
     * @return 返回结果
     * Created by jl on 2021/5/26 14:42.
     */
    SumValueDTO sumTailSumValue(Map<String, Object> paremMap);

    /**
     * 查总和 最小值 平均值 最大值
     * @param paremMap 请求参数
     * @return 返回结果
     * Created by jl on 2021/5/26 14:43.
     */
    SumValueDTO sumSumValue(Map<String, Object> paremMap);

    /**
     * 获取三区间列表
     * @param paremMap 请求参数
     * @return 返回结果集
     * Created by jl on 2021/5/26 15:06.
     */
    List<String> getSanSectionList(Map<String, Object> paremMap);

    /**
     * 获取历史列表
     * @param paremMap 请求参数
     * @return 返回结果集
     * Created by jl on 2021/5/26 15:06.
     */
    List<SsqDetailInfoDO> getHistoryShuangSeQiuInfo(Map<String, Object> paremMap);
}
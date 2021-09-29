package com.jl.cp.controller;
import com.google.gson.Gson;
import com.jl.cp.dto.requestVO.GenerateShuangSeQiuRequestVO;
import com.jl.cp.dto.requestVO.QueryHistoryRequestVO;
import com.jl.cp.dto.responseVO.GenerateShuangSeQiuResponseVO;
import com.jl.cp.dto.responseVO.PubResponseVO;
import com.jl.cp.service.GenerateShuangSeQiuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Slf4j
@Controller
@RequestMapping(value = "/admin/generate/")
public class GenerateShuangSeQiuController {

    @Autowired
    private GenerateShuangSeQiuService generateShuangSeQiuService;

    /**
     * 跳转商品类别
     * @return 分页页面
     * Created by kz on 2021-03-26 15:30
     */
    @RequestMapping("viewShuangSeQiu.html")
    public String listUI(Model model) {
        model.addAttribute("issueno",generateShuangSeQiuService.listUI());
        return "generateTickets";
    }

    /**
     * 生成号码
     * @return 新增结果
     * Created by kz on 2021-03-26 15:30
     */
    @RequestMapping(value = "generate.html", method = RequestMethod.POST)
    @ResponseBody
    public String generateShuangSeQiu(GenerateShuangSeQiuRequestVO requestVO) {
        String label = "生成号码";
        log.info(label + " 请求 requestVO：{}", requestVO);
        PubResponseVO pubResponseVO = new PubResponseVO();
        try {
            GenerateShuangSeQiuResponseVO responseVO = generateShuangSeQiuService.generateShuangSeQiu(requestVO);
            pubResponseVO.setRespCode("0000");
            pubResponseVO.setRespMsg("成功");
            pubResponseVO.setData(responseVO);
            return new Gson().toJson(pubResponseVO);
        } catch (Exception e) {
            pubResponseVO.setRespCode("0001");
            pubResponseVO.setRespMsg("生成失败");
            return new Gson().toJson(pubResponseVO);
        }
    }

    /**
     * 查询历史
     * @return 新增结果
     * Created by kz on 2021-03-26 15:30
     */
    @RequestMapping(value = "queryHistory.html", method = RequestMethod.POST)
    @ResponseBody
    public String queryHistory(QueryHistoryRequestVO requestVO) {
        String label = "查询历史";
        log.info(label + " 请求 requestVO：{}", requestVO);
        PubResponseVO pubResponseVO = new PubResponseVO();
        try {
            Object o = generateShuangSeQiuService.queryHistory(requestVO);
            pubResponseVO.setRespCode("0000");
            pubResponseVO.setRespMsg("成功");
            pubResponseVO.setData(o);
            return new Gson().toJson(pubResponseVO);
        } catch (Exception e) {
            pubResponseVO.setRespCode("0001");
            pubResponseVO.setRespMsg("查询历史失败");
            return new Gson().toJson(pubResponseVO);
        }
    }
}
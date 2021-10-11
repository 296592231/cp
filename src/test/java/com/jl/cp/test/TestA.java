package com.jl.cp.test;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.jl.cp.job.service.ShuangSeQiuJobService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @Author LeJiang
 * @CreateOn 2021/2/28 ^ 上午12:21
 * @Path /
 * @ContentType Content-Type/application-json
 * @Remark
 */
@RunWith(SpringRunner.class)
@SpringBootTest
//由于是Web项目，Junit需要模拟ServletContext，因此我们需要给我们的测试类加上@WebAppConfiguration。
@WebAppConfiguration
public class TestA {

    @Autowired
    private ShuangSeQiuJobService shuangSeQiuJobService;

    @org.junit.Test
    public void test() {
        shuangSeQiuJobService.updateWhetherToWinTheLottery();
    }
}

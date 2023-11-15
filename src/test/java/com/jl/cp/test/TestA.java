package com.jl.cp.test;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.jl.cp.job.service.ShuangSeQiuJobService;
import org.junit.jupiter.api.Test;
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
@SpringBootTest
public class TestA {

    @Autowired
    private ShuangSeQiuJobService shuangSeQiuJobService;

    @Test
    public void test() {
        shuangSeQiuJobService.batchInsertDetail();
    }
}

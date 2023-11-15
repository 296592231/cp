package com.jl.cp;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@MapperScan("com.jl.cp.mapper")
public class CpApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(CpApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  cp模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}

package com.jl.cp.cofig;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.MapperHelper;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Mybatis的sqlSession配置
 * Created by chenjunyi on 2017/11/17.
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = { "com.jl.cp.mapper" })
public class MybatisConfiguration {

    /**
     * mybatis持久层数据源配置
     * @return 数据源对象
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource dataSource() {
        //使用druid数据源
        return DruidDataSourceBuilder.create().build();

        //build自动按照固定的类名进行数据源查找，目前使用的是tomcat数据源
        //return DataSourceBuilder.create().build();
    }

    /**
     * Mybatis-SqlSession配置
     * @return SqlSessionFactory实例
     * @throws Exception 异常
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();

        //设置数据源
        factoryBean.setDataSource(dataSource);

        //设置xml文件路径
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factoryBean.setMapperLocations(resolver.getResources("classpath*:mapper/*Mapper.xml"));

        //设置分页插件
        PageInterceptor pageHelper = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("reasonable", "true");
        pageHelper.setProperties(properties);
        factoryBean.setPlugins(new Interceptor[] { pageHelper });

        //获取SqlSessionFactory
        SqlSessionFactory factory = factoryBean.getObject();

        //设置通用mapper
        MapperHelper mapperHelper = new MapperHelper();
        Config config = new Config();
        config.setIDENTITY("MYSQL");
        mapperHelper.setConfig(config);
        mapperHelper.registerMapper(Mapper.class);
        mapperHelper.processConfiguration(factory.openSession().getConfiguration());

        //返回sqlSession对象
        return factory;
    }

}

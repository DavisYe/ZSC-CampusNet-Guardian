package com.zsxyww.backend.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;

@Configuration
@MapperScan("com.zsxyww.backend.mapper")
@RequiredArgsConstructor
public class MybatisPlusConfig {

    private final Environment env;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 分页配置，根据环境选择数据库类型
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 在测试环境中使用H2数据库
        String[] activeProfiles = env.getActiveProfiles();
        boolean isTestProfile = false;
        for (String profile : activeProfiles) {
            if ("test".equals(profile)) {
                isTestProfile = true;
                break;
            }
        }
        paginationInterceptor.setDbType(isTestProfile ? DbType.H2 : DbType.MYSQL);
        paginationInterceptor.setMaxLimit(500L);
        interceptor.addInnerInterceptor(paginationInterceptor);
        
        // 防止全表更新与删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        
        return interceptor;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        
        // 设置mapper.xml文件位置
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/mapper/**/*.xml"));
                
        // 设置实体类包路径
        sqlSessionFactory.setTypeAliasesPackage("com.zsxyww.backend.model.entity");
        
        // 设置分页插件
        sqlSessionFactory.setPlugins(mybatisPlusInterceptor());
                
        return sqlSessionFactory.getObject();
    }
}
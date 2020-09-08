package com.sirius.xmybatis;

import com.sirius.xmybatis.configuration.XmyBatisConfiguration;
import com.sirius.xmybatis.domain.DataSourceInfo;
import com.sirius.xmybatis.exception.SqlSessionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sirius
 * SqlSessionFactory管理器
 */
@Slf4j
@Component
class SqlSessionManager {

    /**
     * 自定义数据源配置
     */
    @Autowired
    private XmyBatisConfiguration xMybatisConfiguration;

    /**
     * 自定义数据源工厂
     */
    @Autowired
    private CustomDataSourceFactory customDataSourceFactory;

    /**
     * sqlSessionFactory集合
     */
    private Map<String, SqlSession> customSqlSessionMap = new ConcurrentHashMap<>();

    /*@PostConstruct
    private void init() {
        for (DataSourceInfo dataSourceInfo : xMybatisConfiguration.getDataSourceInfoList()) {
            // 1.创建DataSource
            DataSource dataSource = customDataSourceFactory.createXADataSource(dataSourceInfo);
            if (dataSource == null) {
                log.error("数据源 {} 创建DataSource失败", dataSourceInfo);
                continue;
            }
            // 2.创建TransactionFactory
            TransactionFactory transactionFactory = new JdbcTransactionFactory();
            // 3.创建Environment
            Environment environment = new Environment(dataSourceInfo.getDbName(), transactionFactory, dataSource);
            // 4.创建Configuration
            Configuration configuration = new Configuration(environment);
            // 启动缓存功能
            configuration.setCacheEnabled(true);
            // 启动关联表延迟查询
            configuration.setLazyLoadingEnabled(true);
            // 5.加载XML文件
            try {
                Resource[] xmlResources = new PathMatchingResourcePatternResolver().getResources(xMybatisConfiguration.getXmlMapperPath());
                for (Resource xmlResource : xmlResources) {
                    XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(xmlResource.getInputStream(), configuration, xmlResource.toString(), configuration.getSqlFragments());
                    xmlMapperBuilder.parse();
                }
            } catch (Exception e) {
                log.error("数据源 {} 创建DataSource失败", e.getMessage());
                continue;
            }
            // 6.创建SqlSessionFactory
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
            CustomSqlSession customSqlSession = new CustomSqlSession(sqlSessionFactory);
            customSqlSessionMap.put(dataSourceInfo.getDbName(), customSqlSession);
        }
    }*/

    @PostConstruct
    private void init() {
        for (DataSourceInfo dataSourceInfo : xMybatisConfiguration.getDataSourceInfoList()) {
            // 1.获取DataSource
            DataSource dataSource = customDataSourceFactory.createXADataSource(dataSourceInfo);
            if (dataSource == null) {
                log.error("数据源 {} 创建DataSource失败", dataSourceInfo);
                continue;
            }
            // 2.创建SqlSessionFactory
            SqlSessionFactory sqlSessionFactory;
            try {
                SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
                // 添加数据源
                bean.setDataSource(dataSource);
                // 添加配置
                Configuration configuration = new Configuration();
                configuration.setDatabaseId(dataSourceInfo.getDbName());
                // 启动缓存功能
                configuration.setCacheEnabled(true);
                // 启动关联表延迟查询
                configuration.setLazyLoadingEnabled(true);
                // 启用驼峰命名转换
                configuration.setMapUnderscoreToCamelCase(true);
                bean.setConfiguration(configuration);
                // 配置XML文件路径
                bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(xMybatisConfiguration.getXmlMapperPath()));
                // 设置实体类包路径
                bean.setTypeAliasesPackage(xMybatisConfiguration.getTypeAliasesPackage());
                // 添加插件
                // 设置SpringBootVFS文件扫描
                bean.setVfs(SpringBootVFS.class);
                // 创建SqlSessionFactory
                sqlSessionFactory = bean.getObject();
            } catch (Exception e) {
                log.error("数据源 {} 创建DataSource失败", e.getMessage());
                continue;
            }
            // 3.创建自定义SqlSession
            SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
            customSqlSessionMap.put(dataSourceInfo.getDbName(), sqlSessionTemplate);
        }
    }

    /**
     * 获取SqlSession
     */
    public SqlSession getSqlSession(String dbName) {
        SqlSession sqlSession = customSqlSessionMap.get(dbName);
        if (sqlSession == null) {
            throw new SqlSessionNotFoundException();
        }
        return sqlSession;
    }
}

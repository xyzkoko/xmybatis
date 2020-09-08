package com.sirius.xmybatis.configuration;

import com.sirius.xmybatis.domain.DataSourceInfo;
import com.sirius.xmybatis.domain.DataSourceTypeEnum;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;

/**
 * @author Sirius
 * 读取配置
 */
@Data
@Configuration
public class XmyBatisConfiguration {

    /**
     * 驱动jar包
     */
    private Map<DataSourceTypeEnum, List<String>> driverPathMap = new HashMap<>();

    /**
     * 数据源列表
     */
    private List<DataSourceInfo> dataSourceInfoList = new ArrayList<>();

    /**
     * XML文件路径
     */
    private String xmlMapperPath = "classpath:mapper/*.xml";

    private String typeAliasesPackage = "com.example.demo.domain";

    @PostConstruct
    public void test() {
        // 添加驱动
        List<String> mysqlDriverInfoList = new ArrayList<>();
        mysqlDriverInfoList.add("file:lib" + File.separator + "driver" + File.separator + "mysql" + File.separator + "mysql-connector-java-5.1.48.jar");
        mysqlDriverInfoList.add("file:lib" + File.separator + "driver" + File.separator + "mysql" + File.separator + "mysql-connector-java-8.0.11.jar");
        driverPathMap.put(DataSourceTypeEnum.MYSQL, mysqlDriverInfoList);

        // 添加数据源1
        DataSourceInfo dataSourceInfo = new DataSourceInfo();
        dataSourceInfo.setDbName("mysql_001");
        dataSourceInfo.setDataSourceType(DataSourceTypeEnum.MYSQL);
        Properties prop = new Properties();
        prop.put("druid.url", "jdbc:mysql://127.0.0.1:3306/demo01?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true");
        prop.put("druid.username", "root");
        prop.put("druid.password", "123456");
        prop.put("druid.initialSize", 1);
        prop.put("druid.minIdle", 1);
        prop.put("druid.maxActive", 20);
        prop.put("druid.maxWait", 60 * 1000);
        prop.put("druid.validationQuery", "select 1");
        prop.put("druid.testWhileIdle", true);
        prop.put("druid.testOnBorrow", false);
        prop.put("druid.testOnReturn", false);
        prop.put("druid.keepAlive", true);
        prop.put("druid.timeBetweenEvictionRunsMillis", 60 * 1000);
        prop.put("druid.minEvictableIdleTimeMillis", 5 * 60 * 1000);
        dataSourceInfo.setProperties(prop);
        dataSourceInfoList.add(dataSourceInfo);

        // 添加数据源2
        dataSourceInfo = new DataSourceInfo();
        dataSourceInfo.setDbName("mysql_002");
        dataSourceInfo.setDataSourceType(DataSourceTypeEnum.MYSQL);
        prop = new Properties();
        prop.put("druid.url", "jdbc:mysql://127.0.0.1:3306/demo02?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true");
        prop.put("druid.username", "root");
        prop.put("druid.password", "123456");
        prop.put("druid.initialSize", 1);
        prop.put("druid.minIdle", 1);
        prop.put("druid.maxActive", 20);
        prop.put("druid.maxWait", 60 * 1000);
        prop.put("druid.validationQuery", "select 1");
        prop.put("druid.testWhileIdle", true);
        prop.put("druid.testOnBorrow", false);
        prop.put("druid.testOnReturn", false);
        prop.put("druid.keepAlive", true);
        prop.put("druid.timeBetweenEvictionRunsMillis", 60 * 1000);
        prop.put("druid.minEvictableIdleTimeMillis", 5 * 60 * 1000);
        dataSourceInfo.setProperties(prop);
        dataSourceInfoList.add(dataSourceInfo);


        // 添加数据源3
/*        dataSourceInfo = new DataSourceInfo();
        dataSourceInfo.setDbName("mysql_003");
        dataSourceInfo.setDataSourceType(DataSourceTypeEnum.MYSQL);
        prop = new Properties();
        prop.put("druid.url", "jdbc:mysql://192.168.247.129:3306/demo01?characterEncoding=utf8&useSSL=false&rewriteBatchedStatements=true");
        prop.put("druid.username", "root");
        prop.put("druid.password", "123456");
        prop.put("druid.initialSize", 1);
        prop.put("druid.minIdle", 1);
        prop.put("druid.maxActive", 20);
        prop.put("druid.maxWait", 60 * 1000);
        prop.put("druid.validationQuery", "select 1");
        prop.put("druid.testWhileIdle", true);
        prop.put("druid.testOnBorrow", false);
        prop.put("druid.testOnReturn", false);
        prop.put("druid.keepAlive", true);
        prop.put("druid.timeBetweenEvictionRunsMillis", 60 * 1000);
        prop.put("druid.minEvictableIdleTimeMillis", 5 * 60 * 1000);
        dataSourceInfo.setProperties(prop);
        dataSourceInfoList.add(dataSourceInfo);*/
    }


}

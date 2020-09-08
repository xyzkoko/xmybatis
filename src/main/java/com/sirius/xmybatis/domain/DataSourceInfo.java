package com.sirius.xmybatis.domain;

import lombok.Data;

import java.util.Properties;

/**
 * @author Sirius
 * 数据源基本信息
 */
@Data
public class DataSourceInfo {

    /**
     * 数据源名称
     */
    private String dbName;

    /**
     * 数据库类型
     */
    private DataSourceTypeEnum dataSourceType;


    /**
     * 连接池配置信息
     */
    private Properties properties;
}

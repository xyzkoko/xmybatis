package com.sirius.xmybatis;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.sirius.xmybatis.domain.DataSourceInfo;
import com.sirius.xmybatis.datasource.druid.CustomDruidXADataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 * @author Sirius
 * 自定义数据源工厂
 */
@Slf4j
@Component
class CustomDataSourceFactory {

    @Autowired
    private DriverClassLoadManager driverClassLoadManager;

    /**
     * 创建自定义XA数据源
     */
    DataSource createXADataSource(DataSourceInfo dataSourceInfo) {
        // 创建XADataSource
        boolean customDriver = false;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            customDriver = true;
        }
        // TODO 优化
        if (!customDriver) {
            DruidXADataSource xaDataSource = new CustomDruidXADataSource();
            xaDataSource.configFromPropety(dataSourceInfo.getProperties());
            AtomikosDataSourceBean atomikosDataSource = new AtomikosDataSourceBean();
            atomikosDataSource.setUniqueResourceName(dataSourceInfo.getDbName());
            atomikosDataSource.setXaDataSource(xaDataSource);
            return atomikosDataSource;
        } else {
            List<DriverClassLoadManager.DriverClassLoader> driverClassLoaderList = driverClassLoadManager.getDriverClassLoaderList(dataSourceInfo.getDataSourceType());
            for (DriverClassLoadManager.DriverClassLoader driverClassLoader : driverClassLoaderList) {
                CustomDruidXADataSource xaDataSource = new CustomDruidXADataSource();
                xaDataSource.setDriverClassLoader(driverClassLoader);
                xaDataSource.configFromPropety(dataSourceInfo.getProperties());
                // 测试能否连接数据库
                try (Connection connection = xaDataSource.getConnection()) {
                    String databaseProductVersion = connection.getMetaData().getDatabaseProductVersion();
                    log.info("数据源 {} 连接成功版本为 {}", dataSourceInfo, databaseProductVersion);
                } catch (Exception e) {
                    log.debug("驱动 {} 连接失败 {}", driverClassLoader.getURLs()[0].toString(), e.getMessage());
                    continue;
                }
                AtomikosDataSourceBean atomikosDataSource = new AtomikosDataSourceBean();
                atomikosDataSource.setUniqueResourceName(dataSourceInfo.getDbName());
                atomikosDataSource.setXaDataSource(xaDataSource);
                return atomikosDataSource;
            }
        }
        return null;
    }

}

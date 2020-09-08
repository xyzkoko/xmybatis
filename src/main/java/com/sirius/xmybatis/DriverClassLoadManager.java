package com.sirius.xmybatis;

import com.sirius.xmybatis.configuration.XmyBatisConfiguration;
import com.sirius.xmybatis.domain.DataSourceTypeEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sirius
 * 驱动管理器
 */
@Slf4j
@Component
class DriverClassLoadManager {

    @Autowired
    private XmyBatisConfiguration xMybatisConfiguration;

    @Getter
    private Map<DataSourceTypeEnum, List<DriverClassLoader>> driverClassLoaderMap = new HashMap<>();

    @PostConstruct
    private void init() {
        for (DataSourceTypeEnum dataSourceType : DataSourceTypeEnum.values()) {
            List<DriverClassLoader> driverClassLoaderList = new ArrayList<>();
            List<String> driverInfoList = xMybatisConfiguration.getDriverPathMap().get(dataSourceType);
            if (driverInfoList == null) {
                continue;
            }
            for (String driverPath : driverInfoList) {
                DriverClassLoader driverClassLoader = null;
                try {
                    driverClassLoader = new DriverClassLoader(new URL[]{new URL(driverPath)});
                } catch (MalformedURLException e) {
                    log.error("加载驱动{}失败:{}", driverPath, ExceptionUtils.getStackTrace(e));
                }
                driverClassLoaderList.add(driverClassLoader);
            }
            driverClassLoaderMap.put(dataSourceType, driverClassLoaderList);
        }
    }

    /**
     * 获取DataSourceType下的所有DriverClassLoader列表
     */
    List<DriverClassLoader> getDriverClassLoaderList(DataSourceTypeEnum dataSourceType) {
        return driverClassLoaderMap.get(dataSourceType) == null ? new ArrayList<>() : driverClassLoaderMap.get(dataSourceType);
    }

    /**
     * 自定义classloader加载驱动
     */
    static class DriverClassLoader extends URLClassLoader {
        private Map<String, Class<?>> classMap = new HashMap<>();

        private DriverClassLoader(URL[] urls) {
            super(urls);
        }

        @Override
        public Class<?> loadClass(String className) throws ClassNotFoundException {
            if (this.classMap.containsKey(className)) {
                return this.classMap.get(className);
            } else {
                try {
                    Class<?> findClass = this.findClass(className);
                    if (findClass != null) {
                        this.classMap.put(className, findClass);
                        return findClass;
                    }
                } catch (ClassNotFoundException ignored) {
                }

                return super.loadClass(className);
            }
        }

        @Override
        protected Package getPackage(String packageName) {
            return null;
        }
    }
}

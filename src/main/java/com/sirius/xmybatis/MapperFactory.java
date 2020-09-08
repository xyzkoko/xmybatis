package com.sirius.xmybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Sirius
 * Mapper管理器
 */
@Slf4j
@Component
public class MapperFactory {

    @Autowired
    private SqlSessionManager sqlSessionManager;

    public <T> T getMapper(String dbName, Class<T> clazz) {
        SqlSession sqlSession = sqlSessionManager.getSqlSession(dbName);
        return sqlSession.getMapper(clazz);
    }
}

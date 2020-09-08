package com.sirius.xmybatis.datasource.druid;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.xa.DruidPooledXAConnection;
import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.alibaba.druid.pool.xa.JtdsXAConnection;
import com.alibaba.druid.util.*;
import lombok.extern.slf4j.Slf4j;

import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Sirius
 */
@Slf4j
public class CustomDruidXADataSource extends DruidXADataSource {

    private Object h2Factory = null;

    @Override
    public XAConnection getXAConnection() throws SQLException {
        DruidPooledConnection conn = this.getConnection();

        Connection physicalConn = conn.unwrap(Connection.class);

        XAConnection rawXAConnection = createPhysicalXAConnection(physicalConn);

        return new DruidPooledXAConnection(conn, rawXAConnection);
    }

    private XAConnection createPhysicalXAConnection(Connection physicalConn) throws SQLException {
        if (JdbcUtils.ORACLE.equals(dbType)) {
            try {
                return OracleUtils.OracleXAConnection(physicalConn);
            } catch (XAException xae) {
                log.error("create xaConnection error", xae);
                return null;
            }
        }

        if (JdbcUtils.MYSQL.equals(dbType) || JdbcUtils.MARIADB.equals(dbType)) {
            if (driverClassLoader == null) {
                return CustomMySqlUtils.createXAConnection(driver, physicalConn);
            } else {
                return CustomMySqlUtils.createXAConnection(driver, physicalConn, driverClassLoader);
            }
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)) {
            return PGUtils.createXAConnection(physicalConn);
        }

        if (JdbcUtils.H2.equals(dbType)) {
            return H2Utils.createXAConnection(h2Factory, physicalConn);
        }

        if (JdbcUtils.JTDS.equals(dbType)) {
            return new JtdsXAConnection(physicalConn);
        }

        throw new SQLException("xa not support dbType : " + this.dbType);
    }
}

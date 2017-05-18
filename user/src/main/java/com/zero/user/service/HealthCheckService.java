package com.zero.user.service;

import com.zaxxer.hikari.HikariDataSource;
import com.zero.vo.HealthCheckVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/5/18
 */
@Service
public class HealthCheckService {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckService.class);
    @Resource
    private HikariDataSource dataSource;

    public HealthCheckVo checkDBConnection() {
        String url = dataSource.getJdbcUrl();
        String driver = dataSource.getDriverClassName();
        String user = dataSource.getUsername();
        String password = dataSource.getPassword();
        Connection conn = null;
        Statement statement = null;
        HealthCheckVo model = new HealthCheckVo();
        model.setServiceName(url);

        try {
            long e = System.currentTimeMillis();
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            statement = conn.createStatement();
            statement.executeQuery("select 1");
            model.setCostTime(String.valueOf(System.currentTimeMillis() - e));
            model.setNormal(true);
        } catch (Exception var17) {
            LOG.error("[checkDB]发生异常", var17);
            model.setNormal(false);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException var16) {
                LOG.error("[checkDB]关闭资源发生异常", var16);
            }

        }
        return model;
    }
}
